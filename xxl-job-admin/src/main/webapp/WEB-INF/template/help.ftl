<!DOCTYPE html>
<html>
<head>
  	<title>任务调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>任务调度中心<small>使用教程</small></h1>
			<!--
			<ol class="breadcrumb">
				<li><a><i class="fa fa-dashboard"></i>调度中心</a></li>
				<li class="active">使用教程</li>
			</ol>
			-->
		</section>

		<!-- Main content -->
		<section class="content">
			<div class="callout callout-info">
				<h4>简介：XXL_JOB</h4>
				<br>
				<p>
					<a target="_blank" href="https://github.com/xuxueli/xxl-job">github地址</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<iframe src="https://ghbtns.com/github-btn.html?user=xuxueli&repo=xxl-job&type=star&count=true" frameborder="0" scrolling="0" width="170px" height="20px" style="margin-bottom:-5px;"></iframe> 
					<br><br>
					<a target="_blank" href="http://git.oschina.net/xuxueli0323/xxl-job">git.osc地址</a><br><br>
					<a target="_blank" href="http://www.cnblogs.com/xuxueli/p/5021979.html">博客地址(内附使用教程)</a>
					<br><br>
					<a >技术交流群(仅作技术交流)：367260654</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=4686e3fe01118445c75673a66b4cc6b2c7ce0641528205b6f403c179062b0a52">
						<img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="《xxl-javaer》" title="《xxl-javaer》">
					</a>
				</p>
				<p></p>
            </div>
		</section>
		<!-- /.content -->
	</div>
	<!-- /.content-wrapper -->
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
<@netCommon.comAlert />
</body>
</html>
