package com.xxl.job.admin.thread;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.xxl.job.admin.common.config.XxlJobAdminProperties;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.common.enums.TriggerTypeEnum;
import com.xxl.job.admin.common.pojo.dto.JobLogDTO;
import com.xxl.job.admin.common.pojo.dto.TriggerLogDTO;
import com.xxl.job.admin.common.pojo.vo.JobGroupVO;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.service.ExecutorClient;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.admin.strategy.ExecutorRouterStrategy;
import com.xxl.job.admin.strategy.RouterParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.core.thread.AbstractThreadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务触发线程池
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class TriggerThreadPool extends AbstractThreadListener implements Ordered {

    @Autowired
    private XxlJobAdminProperties xxlJobAdminProperties;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private ExecutorRouterStrategy executorRouterStrategy;

    @Autowired
    private ExecutorClient executorClient;

    /**
     * fast/slow thread pool
     */
    private ThreadPoolExecutor fastTriggerPool = null;
    private ThreadPoolExecutor slowTriggerPool = null;

    /**
     * job timeout count
     * // ms > min
     */
    private volatile long minTime = System.currentTimeMillis() / 60000;
    private volatile ConcurrentMap<Long, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void start() {

        fastTriggerPool = new ThreadPoolExecutor(
                10,
                xxlJobAdminProperties.getTriggerPoolFastMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> new Thread(r, "xxl-job, admin JobTriggerPoolHelper-fastTriggerPool-" + r.hashCode()));

        slowTriggerPool = new ThreadPoolExecutor(
                10,
                xxlJobAdminProperties.getTriggerPoolSlowMax(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                r -> new Thread(r, "xxl-job, admin JobTriggerPoolHelper-slowTriggerPool-" + r.hashCode()));
    }

    @Override
    public void stop() {
        //triggerPool.shutdown();
        fastTriggerPool.shutdownNow();
        slowTriggerPool.shutdownNow();
        log.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }

    /**
     * add trigger
     */
    public void addTrigger(final Long jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorShardingParam,
                           final String executorParam,
                           final String addresses) {

        // choose thread pool
        ThreadPoolExecutor triggerPool = fastTriggerPool;
        AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);

        // job-timeout 10 times in 1 min
        if (ObjectUtil.isNotEmpty(jobTimeoutCount) && jobTimeoutCount.get() > 10) {
            triggerPool = slowTriggerPool;
        }

        // trigger
        triggerPool.execute(() -> {

            long start = System.currentTimeMillis();

            try {
                // do trigger
                trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addresses);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {

                // check timeout-count-map
                long minTimeNow = System.currentTimeMillis() / 60000;
                if (minTime != minTimeNow) {
                    minTime = minTimeNow;
                    jobTimeoutCountMap.clear();
                }

                // incr timeout-count-map
                long cost = System.currentTimeMillis() - start;

                // ob-timeout threshold 500ms
                if (cost > 500) {
                    AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                    if (timeoutCount != null) {
                        timeoutCount.incrementAndGet();
                    }
                }
            }
        });
    }

    /**
     * 触发
     *
     * @param jobId                 任务ID
     * @param triggerType           触发类型
     * @param failRetryCount        失败重试计数
     * @param executorShardingParam 执行器切分参数
     * @param executorParam         执行器参数
     * @param addresses             地址
     */
    private void trigger(Long jobId, TriggerTypeEnum triggerType, int failRetryCount,
                         String executorShardingParam, String executorParam, String addresses) {

        // load data
        JobInfoVO jobInfo = jobInfoService.queryById(jobId);
        if (ObjectUtil.isEmpty(jobInfo)) {
            log.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (ObjectUtil.isNotEmpty(executorParam)) {
            jobInfo.setExecutorParam(executorParam);
        }

        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        JobGroupVO group = jobInfo.getJobGroup();

        // cover addressList
        if (StrUtil.isNotBlank(addresses)) {
            group.setAddressType(NumberConstant.ONE);
            group.setAddresses(addresses);
        }

        // sharding param
        int[] shardingParam = null;
        if (StrUtil.isNotBlank(executorShardingParam)) {
            String[] shardingArr = StrUtil.splitToArray(executorShardingParam, StrUtil.C_SLASH);
            if (shardingArr.length == NumberConstant.TWO && NumberUtil.isNumber(shardingArr[0]) && NumberUtil.isNumber(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.parseInt(shardingArr[0]);
                shardingParam[1] = Integer.parseInt(shardingArr[1]);
            }
        }

        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST.equals(ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy()))
                && StrUtil.isNotBlank(group.getAddresses()) && ArrayUtil.isEmpty(shardingParam)) {
            List<String> addressList = StrUtil.split(group.getAddresses(), StrUtil.COMMA);
            for (int i = 0; i < addressList.size(); i++) {
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, addressList.size());
            }
        } else {
            if (ArrayUtil.isEmpty(shardingParam)) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }
    }

    /**
     * 执行触发
     *
     * @param group               任务组信息，注册集合可能为空
     * @param index               sharding index
     * @param total               sharding index
     * @param jobInfo             任务信息
     * @param finalFailRetryCount 最后失败重试计数
     * @param triggerType         触发类型
     */
    private void processTrigger(JobGroupVO group, JobInfoVO jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {

        // param
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy());
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy());
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST.equals(executorRouteStrategyEnum))
                ? String.valueOf(index).concat(StrUtil.SLASH).concat(String.valueOf(total)) : null;

        // 1、save log-id
        JobLogDTO jobLogDTO = new JobLogDTO();
        jobLogDTO.setGroupId(jobInfo.getJobGroup().getId());
        jobLogDTO.setJobId(jobInfo.getId());
        jobLogDTO.setTriggerTime(DateUtil.current());
        Long jobLogId = jobLogService.syncJobLog(jobLogDTO);

        log.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobInfo.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        BeanUtil.copyProperties(jobInfo, triggerParam);
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setLogId(jobLogId);
        triggerParam.setLogDateTime(jobLogDTO.getTriggerTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、init address
        String address = null;
        ResponseEnum routeAddressResult = null;
        if (StrUtil.isNotBlank(group.getAddresses())) {
            List<String> addresses = StrUtil.split(group.getAddresses(), StrUtil.COMMA);
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST.equals(executorRouteStrategyEnum)) {
                address = index < addresses.size() ? addresses.get(index) :  addresses.get(NumberConstant.ZERO);
            } else {
                RouterParam routerParam = new RouterParam();
                routerParam.setExecutorRouteStrategy(executorRouteStrategyEnum);
                routerParam.setJobId(triggerParam.getJobId());
                routerParam.setRegistries(addresses);
                address = executorRouterStrategy.route(routerParam);
            }
        } else {
            routeAddressResult = ResponseEnum.SCHEDULING_FAILED_THE_ACTUATOR_ADDRESS_IS_EMPTY;
        }

        // 4、trigger remote executor
        ResponseVO triggerResult = StrUtil.isNotBlank(address) ? runExecutor(address, triggerParam) : ResponseVO.error();

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append("任务触发类型").append("：").append(triggerType.getValue());
        triggerMsgSb.append("<br>").append("调度机器").append("：").append(NetUtil.getLocalhostStr());
        triggerMsgSb.append("<br>").append("执行器-注册方式").append("：")
                .append((ObjectUtil.equals(NumberConstant.ZERO, group.getAddressType())) ? "自动注册" : "手动录入");
        triggerMsgSb.append("<br>").append("执行器-地址列表").append("：").append(JSON.toJSONString(StrUtil.split(group.getAddresses(), StrUtil.COMMA)));
        triggerMsgSb.append("<br>").append("路由策略").append("：").append(executorRouteStrategyEnum.getValue());
        if (StrUtil.isNotBlank(shardingParam)) {
            triggerMsgSb.append("(" + shardingParam + ")");
        }
        triggerMsgSb.append("<br>").append("阻塞处理策略").append("：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append("任务超时时间").append("：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append("失败重试次数").append("：").append(finalFailRetryCount);

        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>")
                .append((ObjectUtil.isNotEmpty(routeAddressResult) && StrUtil.isNotBlank(routeAddressResult.getMessage())) ? routeAddressResult.getMessage() + "<br><br>" : "")
                .append(StrUtil.isNotBlank(triggerResult.getMessage()) ? triggerResult.getMessage() : "未知问题,联系工作人员排除");

        // 6、save log trigger-info
        TriggerLogDTO triggerLogDTO = new TriggerLogDTO();
        triggerLogDTO.setId(jobLogId);
        triggerLogDTO.setExecutorAddress(address);
        triggerLogDTO.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerLogDTO.setExecutorParam(jobInfo.getExecutorParam());
        triggerLogDTO.setExecutorShardingParam(shardingParam);
        triggerLogDTO.setExecutorFailRetryCount(finalFailRetryCount);
        triggerLogDTO.setTriggerTime(jobLogDTO.getTriggerTime());
        triggerLogDTO.setTriggerCode(triggerResult.getCode());
        triggerLogDTO.setTriggerMessage(triggerMsgSb.toString());
        jobLogService.updateTriggerInfo(triggerLogDTO);

        log.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobInfo.getId());
    }

    /**
     * run executor
     *
     * @param address      地址
     * @param triggerParam 触发参数
     * @return {@link ResponseVO}
     */
    private ResponseVO runExecutor(String address, TriggerParam triggerParam){
        ResponseVO responseVO = ResponseVO.error();
        try {
            responseVO = executorClient.run(address, triggerParam);
            if (ObjectUtil.isNull(responseVO)) {
                responseVO = ResponseVO.error();
            }
            if (ObjectUtil.isAllEmpty(responseVO.getCode())) {
                responseVO.setCode(500);
                responseVO.setMessage((StrUtil.isBlank(responseVO.getMessage()) ? "" : responseVO.getMessage() + ",") + "客户端机器未启用执行器......");
            }
        } catch (Exception e) {
            log.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running. [{}]", address, e.getMessage());
            responseVO = ResponseVO.error(ExceptionUtil.getMessage(e));
        }

        StringBuilder stringBuilder = new StringBuilder("触发调度：");
        stringBuilder.append("<br>address：").append(address);
        stringBuilder.append("<br>code：").append(responseVO.getCode());
        stringBuilder.append("<br>msg：").append(responseVO.getMessage());

        responseVO.setMessage(stringBuilder.toString());
        return responseVO;
    }


}
