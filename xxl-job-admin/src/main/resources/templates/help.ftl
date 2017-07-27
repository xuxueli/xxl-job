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

                                        <a >技术交流群5：138274130</a>&nbsp;&nbsp;&nbsp;&nbsp;
                                        <a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=a3f3aea7e5943e7a24e9726495747ddc19bccd3592d7a70ecb5a97b616062241">
                    						<img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="《xxl-javaer》（五群）" title="《xxl-javaer》（五群）">
                </a>
                    <br><br>

                                        <a >技术交流群4：464762661</a>&nbsp;&nbsp;&nbsp;&nbsp;
                                        （群即将满，请加群5）
                                        <br><br>

                    <a >技术交流群3：242151780</a>&nbsp;&nbsp;&nbsp;&nbsp;
                                        （群即将满，请加群5）
                    <br><br>

                    <a >技术交流群2：438249535</a>&nbsp;&nbsp;&nbsp;&nbsp;
                                        （群即将满，请加群5）
                    <br><br>

                    <a >技术交流群1：367260654</a>&nbsp;&nbsp;&nbsp;&nbsp;
                                        （群即将满，请加群5）

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
