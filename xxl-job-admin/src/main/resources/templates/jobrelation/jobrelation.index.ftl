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
                <div class="col-xs-3" id="selectList">
                    <div class="input-info">
                        <span class="input-group-addon">${I18n.jobinfo_field_jobdesc}</span>
                    </div>
                    <select class="form-control" id="jobInfoId" >
                        <#list JobInfoList as info>
                            <option value="${info.id}" <#if jobInfoId==info.id>selected</#if> >${info.jobDesc}</option>
                        </#list>
                    </select>
                </div>
                <div class="col-xs-1">
                    <button class="btn  btn-success" id="newRelationBtn">${I18n.system_newRelation}</button>
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-default" id="truncateSelectBtn">${I18n.system_truncateSelect}</button>
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-info" id="saveRelationBtn">${I18n.system_save}</button>
                </div>

                <!-- 右侧任务描述 + 该任务现有关系查询-->
                <div class="col-xs-5">
				    <div class="row">
				        <div class="col-xs-8" id="jobList" >
                            <div class="input-info">
                                <span class="input-group-addon">${I18n.jobinfo_field_jobdesc}</span>
                            </div>
                            <select class="form-control" id="jobInfoId" >
                                <#list JobInfoList as info>
                                    <option value="${info.id}" <#if jobInfoId==info.id>selected</#if> >${info.jobDesc}</option>
                                </#list>
                            </select>
                        </div>
                        <div class="col-xs-2">
                            <button class="btn  btn-info" id="currentRelationSearchBtn">${I18n.system_search}</button>
                        </div>
                        <div class="col-xs-1">
                            <button class="btn  btn-default" id="truncateRelationBtn">${I18n.system_truncateRelation}</button>
                        </div>
				    </div>

				    <div class="row">
                        <div class="input-info">
                            <span class="input-group-addon">${I18n.jobrelation_current_relation}</span>
                        </div>
                        <div class="box">
                            <#--<div class="box-header hide">
                                <h3 class="box-title">调度列表</h3>
                            </div>-->
                            <div class="box-body" >
                                <table id="relation_list" class="table table-bordered table-striped" width="100%" >
                                    <thead>
                                        <tr>
                                            <th name="id" >${I18n.jobinfo_field_id}</th>
                                            <th name="jobDesc" >${I18n.jobinfo_field_jobdesc}</th>
                                            <!--<th name="jobGroup" >${I18n.jobinfo_field_jobgroup}</th>-->
                                        </tr>
                                    </thead>
                                    <tbody></tbody>
                                    <tfoot></tfoot>
                                </table>
                            </div>
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
<script src="${request.contextPath}/static/plugins/jstree/jstree.js"></script>
<script src="${request.contextPath}/static/plugins/jstree/jstree.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/jobrelation.index.1.js"></script>
</body>
</html>