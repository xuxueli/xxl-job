<!DOCTYPE html>
<html>
<head>
	<#-- import macro -->
  	<#import "./common/common.macro.ftl" as netCommon>
	<#-- commonStyle -->
	<@netCommon.commonStyle />

	<#-- biz start（1/5 style） -->
	<#-- biz end（1/5 end） -->

</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

	<!-- header -->
	<@netCommon.commonHeader />

	<!-- left -->
	<#-- biz start（2/5 left） -->
	<@netCommon.commonLeft "help" />
	<#-- biz end（2/5 left） -->

	<!-- right start -->
	<div class="content-wrapper">

		<!-- content-header -->
		<section class="content-header">
			<#-- biz start（3/5 name） -->
			<h1>${I18n.job_help}</h1>
			<#-- biz end（3/5 name） -->
		</section>

		<!-- content-main -->
		<section class="content">

			<#-- biz start（4/5 content） -->

			<div class="callout callout-info">
				<h4>${I18n.admin_name_full}</h4>
				<br>
				<p>
					<a target="_blank" href="https://github.com/xuxueli/xxl-job">Github</a>&nbsp;&nbsp;&nbsp;&nbsp;
					<iframe src="https://ghbtns.com/github-btn.html?user=xuxueli&repo=xxl-job&type=star&count=true" frameborder="0" scrolling="0" width="170px" height="20px" style="margin-bottom:-5px;"></iframe> 
					<br><br>
                    <a target="_blank" href="https://www.xuxueli.com/xxl-job/">${I18n.job_help_document}</a>
                    <br><br>

				</p>
				<p></p>
            </div>

			<#-- biz end（4/5 content） -->

		</section>
	</div>
	<!-- right end -->

	<!-- footer -->
	<@netCommon.commonFooter />
</div>
<@netCommon.commonScript />

<#-- biz start（5/5 script） -->
<#-- biz end（5/5 script） -->

</body>
</html>
