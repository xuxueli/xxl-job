<!DOCTYPE html>
<html>
<head>
  	<title>调度日志</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  
	<!-- DataTables CSS -->
	<link rel="stylesheet" type="text/css" href="http://cdn.datatables.net/1.10.7/css/jquery.dataTables.css">
	<!-- jQuery -->
	<script type="text/javascript" charset="utf8" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
	<!-- DataTables -->
	<script type="text/javascript" charset="utf8" src="http://cdn.datatables.net/1.10.7/js/jquery.dataTables.js"></script>
  
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
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header">
			            	<h3 class="box-title">调度列表</h3>
			            </div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display">
				                <thead>
					            	<tr>
					                	<th>id</th>
					                  	<th>jobName</th>
					                  	<th>jobCron</th>
					                  	<th>jobClass</th>
					                  	<th>handleTime</th>
					                  	<th>handleStatus</th>
					                </tr>
				                </thead>
				                <tbody></tbody>
				                <tfoot>
					            	<tr>
					                  	<th>id</th>
					                  	<th>jobName</th>
					                  	<th>jobCron</th>
					                  	<th>jobClass</th>
					                  	<th>handleTime</th>
					                  	<th>handleStatus</th>
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
<@netCommon.comAlert />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script>var base_url = '${request.contextPath}';</script>
<script src="${request.contextPath}/static/js/joblog.index.1.js"></script>
</body>
</html>
