<!DOCTYPE html>
<html>
<head>
  	<title>AdminLTE 2 | Dashboard</title>
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
				<h4>简介：xxl-job</h4>
				<p>调度管理平台：基于quartz封装实现的的集群任务调度管理平台.</p>
				<p></p>
            </div>
            
            <div class="callout callout-info">
				<h4>特点：</h4>
				<p>1、简单：支持通过Web页面对任务进行CRUD操作，操作简单，一分钟上手.</p>
				<p>2、动态：支持动态修改任务状态，动态暂停/恢复任务，即时生效.</p>
				<p>3、集群：任务信息持久化到mysql中，支持Job服务器集群(高可用)，一个任务只会在其中一台服务器上执行.</p>
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
