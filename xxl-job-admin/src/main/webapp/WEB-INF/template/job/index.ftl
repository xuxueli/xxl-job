<!DOCTYPE html>
<html>
<head>
  	<title>调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  
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
			<h1>使用教程<small>调度管理平台</small></h1>
			<ol class="breadcrumb">
				<li><a><i class="fa fa-dashboard"></i>调度中心</a></li>
				<li class="active">使用教程</li>
			</ol>
		</section>
		
		<!-- Main content -->
	    <section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header"><h3 class="box-title">任务列表</h3></div>
			            <div class="box-body">
			              	<table id="job_list" class="table table-bordered table-striped">
				                <thead>
					            	<tr>
					                	<th>任务ID</th>
					                  	<th>cron</th>
					                  	<th>Job类路径</th>
					                  	<th>简介</th>
					                  	<th>操作</th>
					                </tr>
				                </thead>
				                <tbody>
			                		<#if jobList?exists && jobList?size gt 0>
									<#list jobList as item>
									<tr>
					            		<td>${item['TriggerKey'].name}</td>
					                  	<td>${item['Trigger'].cronExpression}</td>
					                  	<td>${item['JobDetail'].jobClass}</td>
					                  	<td>-</td>
					                  	<td>X</td>
					                </tr>
									</#list>
									</#if>
				                </tbody>
				                <tfoot>
					            	<tr>
					                  	<th>任务ID</th>
					                  	<th>cron</th>
					                  	<th>Job类路径</th>
					                  	<th>简介</th>
					                  	<th>操作</th>
					                </tr>
				                </tfoot>
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
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/js/job.index.1.js"></script>
</body>
</html>
