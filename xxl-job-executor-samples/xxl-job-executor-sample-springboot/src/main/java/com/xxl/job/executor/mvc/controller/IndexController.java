package com.xxl.job.executor.mvc.controller;

import com.xxl.job.executor.core.http.HttpSender;
import com.xxl.job.executor.core.http.JobReq;
import com.xxl.registry.client.util.json.BasicJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class IndexController {

	@Autowired
	private JobReq jobReq;
	@Value("${xxl.job.executor.appname}")
	private String appname;

	@ResponseBody
	@RequestMapping("/")
	String index() {
		return "xxl job executor running.";
	}

	/**
	 * 执行器向job服务动态添加一次性任务
	 * @return
	 * @Description: 应用场景：用户可通过接口动态启、禁用某业务。
	 * 例如：运营人员在后台，设置某广告在双十一前启用，到第二天凌晨结束。
	 * 通过此接口动态向job服务添加任务，并在到达启用时间后，由job服务触发本服务（或其它服务）的执行器，改变数据库某条数据状态、清理缓存等等一系列事务操作。
	 * 此方式是执行器反向调用job-admin服务。
	 */
	@ResponseBody
	@RequestMapping("/addJob")
	String addJob() {
		SimpleDateFormat sdf = new SimpleDateFormat("s m H d M ? yyyy");
		Map<String, Object> pms = new HashMap<>(16);
		pms.put("appName", this.appname);
		pms.put("author", "author");
		pms.put("glueType", "BEAN");							//运行模式
		pms.put("triggerStatus", 1);							//调度状态：0-停止，1-运行
		pms.put("jobDesc", "动态添加任务");						//任务描述
		pms.put("jobCron", sdf.format(new Date()));
		pms.put("leastOnce", true);								//必须执行一次
		pms.put("executorTimeout", 6);							//执行超时时间，单位秒
		pms.put("executorFailRetryCount", 1);					//失败重试次数
		pms.put("executorHandler", "demoJobDynamicAdd");		// @JobHandler 执行器名称
		pms.put("executorRouteStrategy", "FAILOVER");			//执行器路由策略
		pms.put("executorBlockStrategy", "SERIAL_EXECUTION");	//阻塞处理策略
		pms.put("executorParam", "{\\\"pk\\\":\\\"唯一标识\\\",\\\"id\\\":\\\"1\\\"}");	//参数
		String res = jobReq.send(HttpSender.Arg.New("/jobinfo/add4appName", BasicJson.toJson(pms)));
		return res;
	}
}