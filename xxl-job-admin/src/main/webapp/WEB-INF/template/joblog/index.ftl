<!DOCTYPE html>
<html>
<head>
  	<title>调度日志</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker-bs3.css">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>调度日志<small>调度中心</small></h1>
			<ol class="breadcrumb">
				<li><a><i class="fa fa-dashboard"></i>调度日志</a></li>
				<li class="active">调度管理</li>
			</ol>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	            <div class="col-xs-5">
              		<div class="input-group">
                		<span class="input-group-addon">
	                  		调度时间
	                	</span>
	                	<input type="text" class="form-control" id="filterTime" readonly 
	                		value="<#if triggerTimeStart?exists && triggerTimeEnd?exists >${triggerTimeStart?if_exists?string('yyyy-MM-dd HH:mm:ss')} - ${triggerTimeEnd?if_exists?string('yyyy-MM-dd HH:mm:ss')}</#if>"  >
	              	</div>
	            </div>
	            <div class="col-xs-5">
	              	<div class="input-group">
	                	<span class="input-group-addon">
	                  		jobName
	                	</span>
	                	<input type="text" class="form-control" id="jobName" value="${jobName}" autocomplete="on" >
	              	</div>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-info" id="searchBtn">搜索</button>
	            </div>
          	</div>
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header"><h3 class="box-title">调度日志</h3></div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
					                	<th>id</th>
					                  	<th>jobName</th>
					                  	<th>jobCron</th>
					                  	<th>jobClass</th>
					                  	<th>jobData</th>
					                  	<th>triggerTime</th>
					                  	<th>triggerStatus</th>
					                  	<th>triggerMsg</th>
					                  	<th>handleTime</th>
					                  	<th>handleStatus</th>
					                  	<th>handleMsg</th>
					                </tr>
				                </thead>
				                <tbody></tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
	<!-- control -->
	<@netCommon.commonControl />
</div>

<@netCommon.commonScript />
<@netCommon.comAlert />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script>var base_url = '${request.contextPath}';</script>
<script src="${request.contextPath}/static/js/joblog.index.1.js"></script>
</body>
</html>
