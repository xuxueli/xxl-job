package com.xxl.job.executor.handle;

import java.util.Date;
import java.util.Random;

import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.executor.service.JobService;
import com.xxl.job.executor.service.pojo.RealTimeJob;


/**
 * 任务Handler的一个Demo（Bean模式）
 *
 * 开发步骤：
 * 1、继承 “IJobHandler” ；
 * 2、装配到Spring，例如加 “@Service” 注解；
 * 3、加 “@JobHander” 注解，注解value值为新增任务生成的JobKey的值;多个JobKey用逗号分割;
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHander(value="insertJobHandler")
@Service
public class InsertJobHandler extends IJobHandler {
	@Autowired
	private JobService jobService;
	@Autowired
	private MetricRegistry regist;
	
	@Override
	public ReturnT<String> execute(String... params) throws Exception {
		final Meter meter = regist.meter("Insert-Handle-Metric-TPS");
		final Histogram histogram = regist.histogram("Insert-Handle-Metric-Histogram");
    	final Counter jobcounter = regist.counter("Insert-Handle-Metric-Counter");
    	final Timer jobtimer = regist.timer("Insert-Handle-Metric-ExecuteTime");
//		XxlJobLogger.log("XXL-JOB, Insert Job.");
		String table = params[0];
		Date today = new Date();
		String datetime = DateUtils.formatDate(today, "yyyy-MM-dd HH:mm:ss");
		int random = new Random().nextInt(10000);
		RealTimeJob obj = new RealTimeJob();
		obj.setCode("code");
		obj.setContent("百度翻译支持全球28种热门语言互译");
		obj.setCreatetime(today);
		obj.setFlag(random%2==0?true:false);
		obj.setMax_retry_times(random);
		obj.setMsg(random%2==0?"Success":"Error");
		obj.setName(table);
		obj.setPrice(3.1413926+random);
		obj.setPriority(0);
		obj.setRetry_times(1);
		obj.setStatus(random%2);
		obj.setTotal(today.getTime());
		obj.setUpdatetime(today);
		meter.mark();
		jobcounter.inc();
		histogram.update(new Random().nextInt(10));
        final Timer.Context context = jobtimer.time();
		try {
			jobService.insert(table, obj);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            context.stop();
        }
		return new ReturnT<String>("["+datetime+"]insert success!");
	}

}
