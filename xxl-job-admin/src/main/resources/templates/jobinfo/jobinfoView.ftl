<!DOCTYPE html>
<html>
<head>
	<#import "../common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
	<title>${I18n.admin_name}</title>

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["xxljob_adminlte_settings"]?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
	<!-- header -->
	<!-- left -->

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper" style="margin-left: 0px;">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>查看任务关系</h1>
		</section>

		<!-- Main content -->
		<section class="content">
			<input type="hidden" id="id" name="id"  value="${id!}">
			<div class="col-xs-12">
				<div id="relationTree" style="width: 100%; height: 50%;" class="info-box bg-white">
				</div>
			</div>
		</section>
	</div>

</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<#-- cronGen -->
<script src="${request.contextPath}/static/plugins/cronGen/cronGen<#if I18n.admin_i18n?default('')?length gt 0 >_${I18n.admin_i18n}</#if>.js"></script>

<link type="text/javascript" href="${request.contextPath}/static/plugins/fancytree/fancytree-2.34/skin-bootstrap/ui.fancytree.css" rel="stylesheet" class="skinswitcher">
<script type="text/javascript" src="${request.contextPath}/static/plugins/fancytree/fancytree-2.34/jquery.fancytree-all-deps.min.js"></script>
<script src="${request.contextPath}/static/plugins/icsoft/ics-fancytree.js"></script>
<script src="${request.contextPath}/static/plugins/echarts/echarts.js"></script>
<script src="${request.contextPath}/static/js/jobrelation.index.1.js"></script>
<script>

	$(function() {
    		var id = $("#id").val();
    		relationTree(id);
    	});


</script>
</body>
</html>
