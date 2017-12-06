package com.xxl.job.executor.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.xxl.job.executor.service.JobService;

@Controller
@EnableAutoConfiguration
public class IndexController {
	private static Logger logger = LoggerFactory.getLogger(IndexController.class);
	@Autowired
	private JobService jobService;
	@Autowired
	private MetricRegistry regist;
    
	public static String ADMIN_ADRRESS = "";
	private static CloseableHttpClient httpClient = HttpClients.custom().disableAutomaticRetries().build();
	private static CloseableHttpAsyncClient httpAsynClient = HttpAsyncClients.createDefault();
    @RequestMapping("/")
    @ResponseBody
    public String index(@RequestParam(defaultValue="0") int type) {
    	JobService.DATABASE_TYPE = type;
        return "[DBType:"+(type==1?"MongoDB":"MySQL|MariaDB")+"]";
    }
    @RequestMapping("/job")
    @ResponseBody
    public String job(@RequestParam(defaultValue="0") int type,@RequestParam(defaultValue="1") int thread,@RequestParam(defaultValue="1") int count,@RequestParam(defaultValue="false") boolean flag,@RequestParam(defaultValue="false") boolean syn,@RequestParam(defaultValue="1") int jobType,@RequestParam(defaultValue="") String childKey) {
    	String table = "insert_job";
    	boolean rebuilt = false;
    	if(type==1){
    		table = "select_job";
    	}else if(type==2){
    		table = "update_job";
    	}else if(type==3){
    		table = "delete_job";
    	}else{
    		rebuilt = true;
    	}
    	jobService.createTable(table, rebuilt);
    	final int _type = type;
    	final int _thread = thread;
    	final String _table = table;
    	final int _count = count;
    	final boolean _flag = flag;
    	final boolean _syn = syn;
    	final int _jobType = jobType;
    	final String _childKey = childKey;
    	Thread main = new Thread(new Runnable() {
			@Override
			public void run() {
				if(_flag){
					jobthread(_type, _thread, _table, _count, _syn, _jobType, _childKey);
				}else{
					jobdetail(_type, _thread, _table, _count, _syn, _jobType, _childKey);
				}
			}
		});
    	main.start();
        return "["+table+"]Success!";
    }
    private void jobdetail(final int type,final int thread,final String table,final int count,final boolean syn,final int jobType,final String childKey){
    	final Meter meter = regist.meter("Controller-Metric-TPS");
    	final Histogram histogram = regist.histogram("Controller-Metric-Histogram");
    	final Counter jobcounter = regist.counter("Controller-Metric-Counter");
    	final Timer jobtimer = regist.timer("Controller-Metric-ExecuteTime");
    	ExecutorService service = Executors.newSingleThreadExecutor();
    	if(thread>1){
    		service = Executors.newFixedThreadPool(thread);
    	}else{
    		if(thread<0){
    			service = Executors.newCachedThreadPool();
    		}
    	}
    	for(int i=0;thread>0?i<thread:true;i++){
    		final int thread_id = i;
    		service.submit(new Runnable() {
    			@Override
    			public void run() {
    				JSONObject obj = new JSONObject();
    				obj.put("id", 0);
    				obj.put("glueRemark", "GLUE初始化");
    				obj.put("updateTime", null);
    				obj.put("jobCron", "0 0/1 * * * ?");
    				obj.put("addTime", null);
    				obj.put("childJobKey", childKey);
    				obj.put("executorRouteStrategy", "FIRST");
    				obj.put("executorParam", table);
    				if(type==1){
    					obj.put("executorHandler", "selectJobHandler");
    		    	}else if(type==2){
    		    		obj.put("executorHandler", "insertJobHandler");
    		    	}else if(type==3){
    		    		obj.put("executorHandler", "insertJobHandler");
    		    		obj.put("executorParam", "select_job");
    		    	}else{
    		    		obj.put("executorHandler", "insertJobHandler");
    		    	}
    				obj.put("alarmEmail", "");
    				obj.put("glueType", "BEAN");
    				obj.put("jobStatus", null);
    				obj.put("glueSource", "");
    				obj.put("jobGroup", 1);
    				obj.put("executorBlockStrategy", "SERIAL_EXECUTION");
    				obj.put("glueUpdatetime", null);
    				obj.put("jobType", jobType);// 任务类型(0.周期[默认],1.单次)
    				obj.put("executorFailStrategy", "FAIL_RETRY");//FAIL_RETRY | FAIL_ALARM
    				obj.put("author", "ZhangYi");
    				for(long i=0;count>0?i<count:true;i++){
    					String today = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
    					if(jobType==0){
    						obj.put("jobDesc", "cycle_"+table+"_"+thread_id+"_"+i+"_"+today);
    					}else{
    						obj.put("jobDesc", "single_"+table+"_"+thread_id+"_"+i+"_"+today);
    					}
    					meter.mark();
    					jobcounter.inc();
    					histogram.update(i);
    			        final Timer.Context context = jobtimer.time();
    					try {
    						String url = ADMIN_ADRRESS+"/jobinfo/add?token=auth";
    						if(syn){
    							httpRequest(url, obj);
    						}else{
    							httpAsynRequest(url, obj);
    						}
    					} catch (Exception e) {
    						e.printStackTrace();
    					}finally {
    			            context.stop();
    			        }
    				}
    			}
    		});
    	}
    }
    private void jobthread(final int type,final int thread,final String table,final int count,final boolean syn,final int jobType,final String childKey){
    	final Meter meter = regist.meter("Controller-Metric-TPS");
    	final Histogram histogram = regist.histogram("Controller-Metric-Histogram");
    	final Counter jobcounter = regist.counter("Controller-Metric-Counter");
    	final Timer jobtimer = regist.timer("Controller-Metric-ExecuteTime");
    	for(int i=0;thread>0?i<thread:true;i++){
    		final int thread_id = i;
    		String thread_name = "["+table+"_Thread_"+i+"]";
    		Thread service = new Thread(new Runnable() {
    			@Override
    			public void run() {
    				JSONObject obj = new JSONObject();
    				obj.put("id", 0);
    				obj.put("glueRemark", "GLUE初始化");
    				obj.put("updateTime", null);
    				obj.put("jobCron", "0 0/1 * * * ?");
    				obj.put("addTime", null);
    				obj.put("childJobKey", childKey);
    				obj.put("executorRouteStrategy", "FIRST");
    				obj.put("executorParam", table);
    				if(type==1){
    					obj.put("executorHandler", "selectJobHandler");
    				}else if(type==2){
    					obj.put("executorHandler", "insertJobHandler");
    				}else if(type==3){
    					obj.put("executorHandler", "insertJobHandler");
    					obj.put("executorParam", "select_job");
    				}else{
    					obj.put("executorHandler", "insertJobHandler");
    				}
    				obj.put("alarmEmail", "");
    				obj.put("glueType", "BEAN");
    				obj.put("jobStatus", null);
    				obj.put("glueSource", "");
    				obj.put("jobGroup", 1);
    				obj.put("executorBlockStrategy", "SERIAL_EXECUTION");
    				obj.put("glueUpdatetime", null);
    				obj.put("jobType", jobType);// 任务类型(0.周期[默认],1.单次)
    				obj.put("executorFailStrategy", "FAIL_RETRY");//FAIL_RETRY | FAIL_ALARM
    				obj.put("author", "ZhangYi");
    				for(long i=0;count>0?i<count:true;i++){
    					String today = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
    					if(jobType==0){
    						obj.put("jobDesc", "cycle_"+table+"_"+thread_id+"_"+i+"_"+today);
    					}else{
    						obj.put("jobDesc", "single_"+table+"_"+thread_id+"_"+i+"_"+today);
    					}
    					meter.mark();
    					jobcounter.inc();
    					histogram.update(i);
    					final Timer.Context context = jobtimer.time();
    					try {
    						String url = ADMIN_ADRRESS+"/jobinfo/add?token=auth";
    						if(syn){
    							httpRequest(url, obj);
    						}else{
    							httpAsynRequest(url, obj);
    						}
    					} catch (Exception e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}finally {
    						context.stop();
    					}
    				}
    			}
    		});
    		service.setName(thread_name);
    		service.start();
    	}
    }
    
    private void httpRequest(String url,JSONObject param){
    	try {
			HttpPost http = new HttpPost(url);
			if(param!=null){
				String body = null;
				for(String key:param.keySet()){
					Object value = param.get(key);
					if(value==null)continue;
					if(value instanceof String)value = URLEncoder.encode(value.toString(), Consts.UTF_8.name());
					if(body == null){
						body = key+"="+value;
					}else{
						body +="&"+ key+"="+value;
					}
				}
				StringEntity entity = new StringEntity(body,ContentType.APPLICATION_FORM_URLENCODED);
				http.setEntity(entity);
			}
			CloseableHttpResponse httpResponse = httpClient.execute(http);
			HttpEntity entity = httpResponse.getEntity();
			String data = EntityUtils.toString(entity,Consts.UTF_8);
			logger.debug("------>"+data);
		}catch (Exception e) {
			logger.error("--Http request error!",e);
		}
    }
    private void httpAsynRequest(String url,JSONObject param){
    	try {
    		final HttpPost http = new HttpPost(url);
    		if(param!=null){
    			String body = null;
    			for(String key:param.keySet()){
    				Object value = param.get(key);
    				if(value==null)continue;
    				if(value instanceof String)value = URLEncoder.encode(value.toString(), Consts.UTF_8.name());
    				if(body == null){
    					body = key+"="+value;
    				}else{
    					body +="&"+ key+"="+value;
    				}
    			}
    			StringEntity entity = new StringEntity(body,ContentType.APPLICATION_FORM_URLENCODED);
    			http.setEntity(entity);
    		}
    		httpAsynClient.start();
    		httpAsynClient.execute(http, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        String data = EntityUtils.toString(response.getEntity(), "UTF-8");
                        logger.debug("------>"+data);
                    } catch (IOException e) {
                        logger.error("--Http asyn complete error!",e);
                    }
                }
                public void failed(final Exception ex) {
                	logger.error("--Http asyn failed!",ex);
                }
                public void cancelled() {
                    logger.error("--Http asyn ["+http.getRequestLine()+"] cancelled callback,thread id is : " + Thread.currentThread().getId());
                }
            });
//    		httpAsynClient.close();
    	}catch (Exception e) {
    		logger.error("--Http asyn request error!",e);
    	}
    }
}