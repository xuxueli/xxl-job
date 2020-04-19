<!DOCTYPE html>
<html>
<head>
  	<#import "./common/common.macro.ftl" as netCommon>
  	<#import "./cron/cron.ftl" as  cronCommon>
	<@netCommon.commonStyle />
	<@cronCommon.cronHeadStyle />
	<title>${I18n.admin_name}</title>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["xxljob_adminlte_settings"]?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "help" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper content-wrapper-left">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>${I18n.cron_name}</h1>
		</section>
		<!-- Main content -->
		<section class="content">
			<@cronCommon.cronContent />

		</section>
	</div>
	<@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
<@cronCommon.cronSript />
</body>
</html>
