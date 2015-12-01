<!DOCTYPE html>
<html>
<head>
  	<title>调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
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
            <div class="callout callout-info">
				<h4>在线任务：</h4>
				<#if jobList?exists && jobList?size gt 0>
				<#list jobList as item>
				<p>${item['TriggerKey']}</p>
				<p>${item['Trigger']}</p>
				<p>${item['Trigger'].cronExpression}</p>
				<p>${item['JobDetail']}</p>
				</#list>
				</#if>
            </div>
            
		</section>
		<!-- /.content -->
	</div>
	<!-- /.content-wrapper -->
	
	<!-- footer -->
	<@netCommon.commonFooter />
	<!-- control -->
	<@netCommon.commonControl />
</div>
<@netCommon.commonScript />
</body>
</html>
