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
			<h1>任务信息</h1>
		</section>

		<!-- Main content -->
		<section class="content">

			<div class="row">
				<div class="col-xs-4">
					<div class="input-group">
						<input type="text" class="form-control" id="jobDesc" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_jobdesc}" >
					</div>
				</div>
				<div class="col-xs-4">
					<div class="input-group">
						<input type="text" class="form-control" id="executorHandler" placeholder="${I18n.system_please_input}JobHandler" >
					</div>
				</div>
				<div class="col-xs-2">
					<button class="btn btn-block btn-info" id="searchBtn">${I18n.system_search}</button>
				</div>
				<input type="hidden" id="childJobId" name="childJobId"  value="${childJobId!}">
			</div>

			<div class="row">
				<div class="col-xs-12">
					<div class="box">
						<#--<div class="box-header hide">
                            <h3 class="box-title">调度列表</h3>
                        </div>-->
						<div class="box-body" >
							<table id="job_list" class="table table-bordered table-striped" width="100%" >
								<thead>
								<tr>
									<th id="checkAll" lay-skin="primary">选择框</th>
									<th name="id" >${I18n.jobinfo_field_id}</th>
									<th name="jobGroup" >${I18n.jobinfo_field_jobgroup}</th>
									<th name="jobDesc" >${I18n.jobinfo_field_jobdesc}</th>
									<th name="scheduleType" >${I18n.schedule_type}</th>
									<th name="glueType" >${I18n.jobinfo_field_gluetype}</th>
									<th name="executorParam" >${I18n.jobinfo_field_executorparam}</th>
									<th name="addTime" >addTime</th>
									<th name="updateTime" >updateTime</th>
									<th name="author" >${I18n.jobinfo_field_author}</th>
									<th name="alarmEmail" >${I18n.jobinfo_field_alarmemail}</th>
									<th name="triggerStatus" >${I18n.system_status}</th>
									<th>${I18n.system_opt}</th>
								</tr>
								</thead>
								<tbody></tbody>
								<tfoot></tfoot>
							</table>
						</div>
					</div>
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
<script src="${request.contextPath}/static/js/jobinfoTop.js"></script>
<script>

	//返回选择的结果，用于弹出选择页面
	function getChoosedtData() {
		var ids = [];
		$("input[name='checkBox1']:checked").each(function(i){
			ids.push($(this).val())
		});
		return ids;
	}
</script>
</body>
</html>
