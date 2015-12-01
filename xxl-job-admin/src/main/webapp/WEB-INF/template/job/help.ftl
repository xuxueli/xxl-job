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
				<p>分布式配置管理平台：一套完整的基于zookeeper的分布式配置统一解决方案.</p>
				<p></p>
            </div>
            
            <div class="callout callout-info">
				<h4>主要目标：</h4>
				<p>1、简化部署：同一个上线包，无须改动配置，即可在 多个环境中(研发RD/测试QA/线上PRODUCTION) 上线.</p>
				<p>2、动态部署：更改配置，无需重新打包或重启，即可 实时生效.</p>
				<p>3、统一管理：提供web平台，统一管理 多个环境(RD/QA/PRODUCTION)、多个产品 的所有配置.</p>
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
