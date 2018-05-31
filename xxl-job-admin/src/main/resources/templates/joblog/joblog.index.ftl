<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
    <title><@spring.message code="admin_name" /></title>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "joblog" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1><@spring.message code="joblog_name" /></h1>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	    		<div class="col-xs-2">
 					<div class="input-group">
	                	<span class="input-group-addon"><@spring.message code="jobinfo_field_jobgroup" /></span>
                		<select class="form-control" id="jobGroup"  paramVal="<#if jobInfo?exists>${jobInfo.jobGroup}</#if>" >
                            <option value="0" ><@spring.message code="system_all" /></option>
                			<#list JobGroupList as group>
                				<option value="${group.id}" >${group.title}</option>
                			</#list>
	                  	</select>
	              	</div>
	            </div>
	            <div class="col-xs-2">
	              	<div class="input-group">
	                	<span class="input-group-addon"><@spring.message code="jobinfo_job" /></span>
                        <select class="form-control" id="jobId" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >
                            <option value="0" ><@spring.message code="system_all" /></option>
						</select>
	              	</div>
	            </div>

                <div class="col-xs-2">
                    <div class="input-group">
                        <span class="input-group-addon"><@spring.message code="joblog_status" /></span>
                        <select class="form-control" id="logStatus" >
                            <option value="-1" ><@spring.message code="joblog_status_all" /></option>
                            <option value="1" ><@spring.message code="joblog_status_suc" /></option>
                            <option value="2" ><@spring.message code="joblog_status_fail" /></option>
                            <option value="3" ><@spring.message code="joblog_status_running" /></option>
                        </select>
                    </div>
                </div>

	            <div class="col-xs-4">
              		<div class="input-group">
                		<span class="input-group-addon">
	                  		<@spring.message code="joblog_field_triggerTime" />
	                	</span>
	                	<input type="text" class="form-control" id="filterTime" readonly >
	              	</div>
	            </div>

                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn"><@spring.message code="system_search" /></button>
                </div>

	            <div class="col-xs-1">
                    <button class="btn btn-block btn-nomal" id="clearLog"><@spring.message code="joblog_clean" /></button>
	            </div>
          	</div>
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <#--<div class="box-header hide"><h3 class="box-title">调度日志</h3></div>-->
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
                                        <th name="jobId" ><@spring.message code="jobinfo_field_id" /></th>
                                        <th name="jobGroup" >jobGroup</th>
										<#--<th name="executorAddress" >执行器地址</th>
										<th name="glueType" >运行模式</th>
                                      	<th name="executorParam" >任务参数</th>-->
                                        <th name="triggerTime" ><@spring.message code="joblog_field_triggerTime" /></th>
                                        <th name="triggerCode" ><@spring.message code="joblog_field_triggerCode" /></th>
                                        <th name="triggerMsg" ><@spring.message code="joblog_field_triggerMsg" /></th>
					                  	<th name="handleTime" ><@spring.message code="joblog_field_handleTime" /></th>
					                  	<th name="handleCode" ><@spring.message code="joblog_field_handleCode" /></th>
					                  	<th name="handleMsg" ><@spring.message code="joblog_field_handleMsg" /></th>
					                  	<th name="handleMsg" ><@spring.message code="system_opt" /></th>
					                </tr>
				                </thead>
				                <tbody></tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<!-- 日志清理.模态框 -->
<div class="modal fade" id="clearLogModal" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" ><@spring.message code="joblog_clean_log" /></h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div class="form-group">
                        <label class="col-sm-3 control-label""><@spring.message code="jobinfo_field_jobgroup" />：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobGroupText" readonly >
							<input type="hidden" name="jobGroup" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label""><@spring.message code="jobinfo_job" />：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobIdText" readonly >
                            <input type="hidden" name="jobId" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label""><@spring.message code="joblog_clean_type" />：</label>
                        <div class="col-sm-9">
                            <select class="form-control" name="type" >
                                <option value="1" ><@spring.message code="joblog_clean_type_1" /></option>
                                <option value="2" ><@spring.message code="joblog_clean_type_2" /></option>
                                <option value="3" ><@spring.message code="joblog_clean_type_3" /></option>
                                <option value="4" ><@spring.message code="joblog_clean_type_4" /></option>
                                <option value="5" ><@spring.message code="joblog_clean_type_5" /></option>
                                <option value="6" ><@spring.message code="joblog_clean_type_6" /></option>
                                <option value="7" ><@spring.message code="joblog_clean_type_7" /></option>
                                <option value="8" ><@spring.message code="joblog_clean_type_8" /></option>
                                <option value="9" ><@spring.message code="joblog_clean_type_9" /></option>
                            </select>
                        </div>
                    </div>

                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-6">
                            <button type="button" class="btn btn-primary ok" ><@spring.message code="system_ok" /></button>
                            <button type="button" class="btn btn-default" data-dismiss="modal"><@spring.message code="system_cancel" /></button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<@netCommon.commonScript />
<script>
    var GlueTypeEnum = {};
    <#list GlueTypeEnum as item>
    GlueTypeEnum['${item}'] = '${item.desc}';
    </#list>
</script>
<#include "/common/common.i18n.init.ftl" />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/joblog.index.1.js"></script>
</body>
</html>
