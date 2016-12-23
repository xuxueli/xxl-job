package com.xxl.job.executor.model;

import java.util.Date;

import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "xxl_job_qrtz_trigger_log")
@SuppressWarnings("serial")
public class Log  {
	private Integer id ;
	//任务组
	private Integer jobGroup ;
	//执行器地址，本次执行的地址
	private String executorAddress ;
	//执行器任务handler
	private String executorHandler ;
	//executor_param
	private String executorParam ;
	//执行-日志
	private String handleMsg ;
	//执行-状态
	private String handleStatus ;
	//任务名
	private String jobName ;
	//调度-日志
	private String triggerMsg ;
	//调度-结果
	private String triggerStatus ;
	//执行-时间
	private Date handleTime ;
	//调度-时间
	private Date triggerTime ;
	

	@AutoID
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public Integer getJobGroup(){
		return  jobGroup;
	}
	public void setJobGroup(Integer jobGroup ){
		this.jobGroup = jobGroup;
	}
	
	public String getExecutorAddress(){
		return  executorAddress;
	}
	public void setExecutorAddress(String executorAddress ){
		this.executorAddress = executorAddress;
	}
	
	public String getExecutorHandler(){
		return  executorHandler;
	}
	public void setExecutorHandler(String executorHandler ){
		this.executorHandler = executorHandler;
	}
	
	public String getExecutorParam(){
		return  executorParam;
	}
	public void setExecutorParam(String executorParam ){
		this.executorParam = executorParam;
	}
	
	public String getHandleMsg(){
		return  handleMsg;
	}
	public void setHandleMsg(String handleMsg ){
		this.handleMsg = handleMsg;
	}
	
	public String getHandleStatus(){
		return  handleStatus;
	}
	public void setHandleStatus(String handleStatus ){
		this.handleStatus = handleStatus;
	}
	
	public String getJobName(){
		return  jobName;
	}
	public void setJobName(String jobName ){
		this.jobName = jobName;
	}
	
	public String getTriggerMsg(){
		return  triggerMsg;
	}
	public void setTriggerMsg(String triggerMsg ){
		this.triggerMsg = triggerMsg;
	}
	
	public String getTriggerStatus(){
		return  triggerStatus;
	}
	public void setTriggerStatus(String triggerStatus ){
		this.triggerStatus = triggerStatus;
	}
	
	public Date getHandleTime(){
		return  handleTime;
	}
	public void setHandleTime(Date handleTime ){
		this.handleTime = handleTime;
	}
	
	public Date getTriggerTime(){
		return  triggerTime;
	}
	public void setTriggerTime(Date triggerTime ){
		this.triggerTime = triggerTime;
	}
	

}
