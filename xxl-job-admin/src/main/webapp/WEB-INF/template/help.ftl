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
	<@netCommon.commonLeft "help" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>使用教程<small>任务调度中心</small></h1>
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
				<h4>简介：XXL-JOB</h4>
				<br>
				<p>
					<a target="_blank" href="https://github.com/xuxueli/xxl-job">github地址</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<iframe src="https://ghbtns.com/github-btn.html?user=xuxueli&repo=xxl-job&type=star&count=true" frameborder="0" scrolling="0" width="170px" height="20px" style="margin-bottom:-5px;"></iframe> 
					<br><br>
					<a target="_blank" href="http://my.oschina.net/xuxueli/blog/690978">oschina地址</a>
                    <br><br>

                    <a >技术交流群3：242151780</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=bab676dc27c7e855da54da433fc39cef8474db6c7095711e4bd21097f89bf320">
                        <img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="《xxl-javaer》（三群）" title="《xxl-javaer》（三群）">
                    </a>
                    <br><br>

                    <a >技术交流群2：438249535</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=555701907c3d37a6a48c165c57f417438f0b7a44e3c648ff5aac94dfdf0522b7">
						<img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="《xxl-javaer》（二群）" title="《xxl-javaer》（二群）">
					</a>
                    （群即将满，请加群3）
                    <br><br>

                    <a >技术交流群1：367260654</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=4686e3fe01118445c75673a66b4cc6b2c7ce0641528205b6f403c179062b0a52">
                        <img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="《xxl-javaer》" title="《xxl-javaer》">
                    </a>
                    （群即将满，请加群3）

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
</body>
</html>
