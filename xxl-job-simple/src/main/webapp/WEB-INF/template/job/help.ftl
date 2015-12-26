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
				<h4>简介：XXL_JOB</h4>
				<p>基于quartz封装实现的的集群任务调度管理平台.</p>
				<p></p>
            </div>
            <div class="callout callout-default">
				<h4>特点：</h4>
				<p>1、简单：支持通过Web页面对任务进行CRUD操作，操作简单，一分钟上手.</p>
				<p>2、动态：支持动态修改任务状态，动态暂停/恢复任务，即时生效.</p>
				<p>3、集群：任务信息持久化到mysql中，支持Job服务器集群(高可用)，一个任务只会在其中一台服务器上执行.</p>
            </div>
            
            <div class="callout callout-default">
				<h4>分层模型：</h4>
				<p>1、基础：基于quartz封装底层调度层，通过CORN自定义任务执行周期，最终执行自定义JobBean的execute方法，如需多个任务，需要开发多个JobBean实现.</p>
				<p>2、分层：上述基础调度模型存在一定局限，调度层和任务层耦合，当新任务上线势必影响任务的正常调度，因此规划将调度系统分层为：调度层 + 任务层 + 通讯层.</p>
				<p>
				 	<div class="row">
				      	<div class="col-xs-offset-1 col-xs-11">
				      		<p>》调度模块：维护任务的调度信息，负责定时/周期性的发出调度请求.</p>
							<p>》任务模块：具体的任务逻辑，负责接收调度模块的调度请求，执行任务逻辑.</p>
							<p>》通讯模块：负责调度模块和任务模块之间的通讯.</p>
							<p>(总而言之，一条完整任务由 “调度信息” 和 “任务信息” 组成.)</p>
				      	</div>      
			   		</div>
				</p>
            </div>
            
            <div class="callout callout-default">
				<h4>调度属性解析 : 发出HTTP调度请求</h4>
				<p>1、调度Key【必填】：调度信息的全局唯一标识.</p>
				<p>2、调度Corn【必填】：调度执行的时间表达式.</p>
				<p>3、调度描述【必填】：调度的简述.</p>
				<p>4、调度URL【必填】：调度执行时发出HTTP请求的目标URL地址.</p>
				<p>5、+args【选填】：调度执行时发出HTTP请求的附带的POST参数.</p>
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
