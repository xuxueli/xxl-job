<!DOCTYPE html>
<html>
<head>
  	<title>任务调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>任务调度中心<small>调度管理</small></h1>
			<!--
			<ol class="breadcrumb">
				<li><a><i class="fa fa-dashboard"></i>调度管理</a></li>
				<li class="active">调度中心</li>
			</ol>
			-->
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    
	    	<div class="row">
	    		<div class="col-xs-4">
	              	<div class="input-group">
	                	<span class="input-group-addon">任务组</span>
                		<select class="form-control" id="jobGroup" >
                			<#list JobGroupList as group>
                				<option value="${group}" >${group.desc}</option>
                			</#list>
	                  	</select>
	              	</div>
	            </div>
	            <div class="col-xs-4">
	              	<div class="input-group">
	                	<span class="input-group-addon">任务名</span>
	                	<input type="text" class="form-control" id="jobName" value="${jobName}" autocomplete="on" >
	              	</div>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-info" id="searchBtn">搜索</button>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-success add" type="button">+新增任务</button>
	            </div>
          	</div>
	    	
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header">
			            	<h3 class="box-title">调度列表</h3>
			            </div>
			            <div class="box-body">
			              	<table id="job_list" class="table table-bordered table-striped">
				                <thead>
					            	<tr>
					            		<th name="id" >id</th>
					                	<th name="jobGroup" >任务组</th>
					                  	<th name="jobName" >任务名</th>
					                  	<th name="jobDesc" >描述</th>
					                  	<th name="jobCron" >Cron</th>
					                  	<th name="jobClass" >JobBean</th>
					                  	<th name="executorAddress" >执行器地址</th>
					                  	<th name="executorHandler" >JobHandler</th>
					                  	<th name="executorParam" >任务参数</th>
					                  	<th name="addTime" >新增时间</th>
					                  	<th name="updateTime" >更新时间</th>
					                  	<th name="author" >负责人</th>
					                  	<th name="alarmEmail" >报警邮件</th>
					                  	<th name="alarmThreshold" >报警阀值</th>
					                  	<th name="glueSwitch" >GLUE模式</th>
					                  	<th name="jobStatus" >状态</th>
					                  	<th>操作</th>
					                </tr>
				                </thead>
				                <tbody></tbody>
				                <tfoot></tfoot>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<!-- job新增.模态框 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >新增任务调度信息</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">任务组<font color="red">*</font></label>
						<div class="col-sm-4">
							<select class="form-control" name="jobGroup" >
		            			<#list JobGroupList as group>
		            				<option value="${group}" >${group.desc}</option>
		            			</#list>
		                  	</select>
						</div>
						<label for="firstname" class="col-sm-2 control-label">任务名<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="jobName" placeholder="请输入“任务名”" minlength="4" maxlength="100" ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">Cron<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="jobCron" placeholder="请输入“Cron”" maxlength="100" ></div>
						<label for="lastname" class="col-sm-2 control-label">描述<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="jobDesc" placeholder="请输入“描述”" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">执行器地址<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="executorAddress" placeholder="请输入“执行器地址”，多个地址逗号分隔" maxlength="200" ></div>
						<label for="lastname" class="col-sm-2 control-label">jobHandler<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="executorHandler" placeholder="请输入“jobHandler”" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">执行参数<font color="black">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="executorParam" placeholder="请输入“执行参数”" maxlength="100" ></div>
						<label for="lastname" class="col-sm-2 control-label">负责人<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="author" placeholder="请输入“负责人”" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">报警邮件<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="alarmEmail" placeholder="请输入“报警邮件”，多个邮件地址逗号分隔" maxlength="200" ></div>
						<label for="lastname" class="col-sm-2 control-label">报警阈值<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="alarmThreshold" placeholder="请输入“报警阈值”" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
							<button type="submit" class="btn btn-primary"  >保存</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						</div>
						<div class="col-sm-3">
							<div class="checkbox">
		                        <label><input type="checkbox" class="ifGLUE" >开启GLUE模式<font color="black">*</font></label>
		                        <input type="hidden" name="glueSwitch" value="0" >
	                    	</div>
						</div>
					</div>

<input type="hidden" name="glueRemark" value="GLUE代码初始化" >
<textarea name="glueSource" style="display:none;" >
package com.xxl.job.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.IJobHandler.JobHandleStatus;

public class DemoJobHandler extends IJobHandler {
	private static transient Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	
	@Override
	public JobHandleStatus execute(String... params) throws Exception {
		logger.info("XXL-JOB, Hello World.");
		return JobHandleStatus.SUCCESS;
	}
	
}
</textarea>
					
				</form>
         	</div>
		</div>
	</div>
</div>

<!-- 更新.模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >更新任务调度信息</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">任务组<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="jobGroup" placeholder="请输入“任务组”" minlength="4" maxlength="100" readonly ></div>
						<label for="firstname" class="col-sm-2 control-label">任务名<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="jobName" placeholder="请输入“任务名”" minlength="4" maxlength="100" readonly ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">Cron<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="jobCron" placeholder="请输入“Cron”" maxlength="100" ></div>
						<label for="lastname" class="col-sm-2 control-label">描述<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="jobDesc" placeholder="请输入“描述”" maxlength="200" ></div>
					</div>
					
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">执行器地址<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="executorAddress" placeholder="请输入“执行器地址”，多个地址逗号分隔" maxlength="200" ></div>
						<label for="lastname" class="col-sm-2 control-label">jobHandler<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="executorHandler" placeholder="请输入“jobHandler”" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">执行参数<font color="black">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="executorParam" placeholder="请输入“执行参数”" maxlength="100" ></div>
						<label for="lastname" class="col-sm-2 control-label">负责人<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="author" placeholder="请输入“负责人”" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<label for="lastname" class="col-sm-2 control-label">报警邮件<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="alarmEmail" placeholder="请输入“报警邮件”，多个邮件地址逗号分隔" maxlength="200" ></div>
						<label for="lastname" class="col-sm-2 control-label">报警阈值<font color="red">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="alarmThreshold" placeholder="请输入“报警阈值”" maxlength="200" ></div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
							<button type="submit" class="btn btn-primary"  >保存</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						</div>
						<div class="col-sm-3">
							<div class="checkbox">
		                        <label><input type="checkbox" class="ifGLUE" >开启GLUE模式<font color="black">*</font></label>
		                        <input type="hidden" name="glueSwitch" value="0" >
	                    	</div>
						</div>
					</div>
				</form>
         	</div>
		</div>
	</div>
</div>

<@netCommon.commonScript />
<@netCommon.comAlert />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/jobinfo.index.1.js"></script>
</body>
</html>
