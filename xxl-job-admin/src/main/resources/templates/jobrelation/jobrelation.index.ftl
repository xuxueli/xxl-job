<!DOCTYPE html>
<html>
<head>
  	<#import "../common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <title>${I18n.admin_name}</title>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["xxljob_adminlte_settings"]?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">

<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "jobrelation" />

	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>${I18n.jobrelation_name}</h1>
		</section>

		<!-- Main content -->
	    <section class="content">

	    	<div class="row">
	    		<div class="col-xs-3">
                    <div class="input-group">
                        <span class="input-group-addon">${I18n.jobinfo_field_jobgroup}</span>
                        <select class="form-control" id="jobGroup" >
                            <#list JobGroupList as group>
                                <option value="${group.id}" <#if jobGroup==group.id>selected</#if> >${group.title}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="col-xs-1">
                    <div class="input-group">
                        <select class="form-control" id="triggerStatus" >
                            <option value="-1" >${I18n.system_all}</option>
                            <option value="0" >${I18n.jobinfo_opt_stop}</option>
                            <option value="1" >${I18n.jobinfo_opt_start}</option>
                        </select>
                    </div>
                </div>
                <div class="col-xs-2">
                    <div class="input-group">
                        <input type="text" class="form-control" id="jobDesc" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_jobdesc}" >
                    </div>
                </div>
	            <div class="col-xs-1">
	            	<button class="btn btn-block btn-info" id="searchBtn">${I18n.system_search}</button>
	            </div>

	            <--!  新增按钮取消并挪移未知
                    <div class="col-xs-1">
                        <button class="btn btn-block btn-success add" type="button">${I18n.jobinfo_field_add}</button>
                    </div>
	            -->
          	</div>

			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <#--<div class="box-header hide">
			            	<h3 class="box-title">调度列表</h3>
			            </div>-->
			            <div class="box-body" >
			                <button></button>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>

	<!-- footer -->
	<@netCommon.commonFooter />
</div>


<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/jobrelation.index.1.js"></script>
</body>
</html>