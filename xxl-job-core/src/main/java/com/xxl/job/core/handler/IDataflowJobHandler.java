package com.xxl.job.core.handler;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import com.xxl.job.core.util.ShardingUtil.ShardingVO;

/**
 * 数据流处理扩展
 * @author created by liyong on 2019-1-24 15:30:56
 */
public abstract class IDataflowJobHandler<T> extends IJobHandler {

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
		XxlJobLogger.log("分片参数：当前分片序号：{}, 总分片数：{}", shardingVO.getIndex(), shardingVO.getTotal());
		Collection<T> datas = fetchData(param);
		int count = 0;
		do {
			if (!CollectionUtils.isEmpty(datas)) {
				int startCount = count;
				count += datas.size();
				XxlJobLogger.log("分片参数：当前分片序号：{}, 总分片数：{}, 处理数据：{} - {}", shardingVO.getIndex(), shardingVO.getTotal(), startCount, count);
				ReturnT<String> result = processData(param, datas);
				if (result != null && ReturnT.SUCCESS_CODE == result.getCode()) {
					datas = fetchData(param);
				} else {
					return result;
				}
				XxlJobLogger.log("分片参数：当前分片序号：{}, 总分片数：{}, 处理数据（SUCCESS）：{} - {}", shardingVO.getIndex(), shardingVO.getTotal(), startCount, count);
			}
		} while (!CollectionUtils.isEmpty(datas));
		return SUCCESS;
	}
	
	private Collection<T> fetchData(String param) {
		ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
		Collection<T> datas = null;
		try {
			datas = fetchData(shardingVO, param);
		} catch (Exception e) {
			XxlJobLogger.log("获取分片数据异常，分片任务已终止，建议人工接入，或开启重试机制，当前分片序号：{}, 总分片数：{}", shardingVO.getIndex(), shardingVO.getTotal());
			XxlJobLogger.log(e);
			throw e;
		}
		return datas;
	}
	
	private ReturnT<String> processData(String param, Collection<T> data) {
		ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
		try {
			return processData(shardingVO, param, data);
		} catch (Exception e) {
			XxlJobLogger.log("分片数据处理失败，分片任务已终止，建议人工接入，或开启重试机制，当前分片序号：{}, 总分片数：{}", shardingVO.getIndex(), shardingVO.getTotal());
			XxlJobLogger.log(e);
			throw e;
		}
	}
	
    /**
     * 获取待处理数据列表，在具体实现中通过分片信息获取待处理的数据集合，如果返回集合为空，则表示数据已经处理完毕
     * @param shardingVO 数据分片信息
     * @param param 任务调度参数
     * @return 待处理的数据
     */
    protected abstract Collection<T> fetchData(ShardingVO shardingVO, String param);
    
    /**
     * 处理数据
     * @param shardingVO 数据分片信息
     * @param param 任务调度参数
     * @param data 待处理的数据
     */
    protected abstract ReturnT<String> processData(ShardingVO shardingVO, String param, Collection<T> data);
	
}
