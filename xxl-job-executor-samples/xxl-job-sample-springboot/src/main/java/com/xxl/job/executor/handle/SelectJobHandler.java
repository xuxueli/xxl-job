package com.xxl.job.executor.handle;

import java.util.Date;
import java.util.List;
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
import com.xxl.job.core.log.XxlJobLogger;
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
@JobHander(value="selectJobHandler")
@Service
public class SelectJobHandler extends IJobHandler {
	@Autowired
	private JobService jobService;
	@Autowired
	private MetricRegistry regist;
	
	@Override
	public ReturnT<String> execute(String... params) throws Exception {
		Meter meter = regist.meter("Select-Handle-Metric-TPS");
		Histogram histogram = regist.histogram("Select-Handle-Metric-Histogram");
    	Counter jobcounter = regist.counter("Select-Handle-Metric-Counter");
    	Timer jobtimer = regist.timer("Select-Handle-Metric-ExecuteTime");
		XxlJobLogger.log("XXL-JOB, Select Job.");
		String table = params[0];
		Date today = new Date();
		String datetime = DateUtils.formatDate(today, "yyyy-MM-dd HH:mm:ss");
		int count = 0;
		meter.mark();
		jobcounter.inc();
		histogram.update(new Random().nextInt(10));
        final Timer.Context context = jobtimer.time();
		try {
			List<RealTimeJob> list = jobService.select(table);
			count = list!=null?list.size():0;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            context.stop();
        }
		return new ReturnT<String>("["+datetime+"]select data size:"+count);
	}

}
