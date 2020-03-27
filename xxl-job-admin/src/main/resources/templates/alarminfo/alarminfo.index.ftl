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
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "alarminfo" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>${I18n.alarm_name}</h1>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    
	    	<div class="row">
	    		<div class="col-xs-2">
                    <div class="input-group">
                    	<span class="input-group-addon">${I18n.alarminfo_type}</span>
                        <select class="form-control" id="jobAlarmerEnum" >
                        	<option value="" >${I18n.system_all}</option>
                            <#list JobAlarmerEnum as item>
								<option value="${item}" >${item.title}</option>
							</#list>
                        </select>
                    </div>
                </div>
	    		<div class="col-xs-4">
	              	<div class="input-group">
	                	<input type="text" class="form-control" id="alarmName" autocomplete="on" placeholder="${I18n.system_please_input}${I18n.alarminfo_name}" >
	              	</div>
	            </div>
                <div class="col-xs-4">
                    <div class="input-group">
                        <input type="text" class="form-control" id="alarmDesc" autocomplete="on" placeholder="${I18n.system_please_input}${I18n.alarminfo_alarmdesc}" >
                    </div>
                </div>
	            <div class="col-xs-1">
	            	<button class="btn btn-block btn-info" id="searchBtn">${I18n.system_search}</button>
	            </div>
	            <div class="col-xs-1">
	            	<button class="btn btn-block btn-success add" type="button">${I18n.jobinfo_field_add}</button>
	            </div>
          	</div>
	    	
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-body" >
			              	<table id="alarm_list" class="table table-bordered table-striped" width="100%" >
				                <thead>
					            	<tr>
					            		<th name="id" >${I18n.alarminfo_alarm}ID</th>
					                	<th name="alarmName" >${I18n.alarminfo_name}</th>
					                  	<th name="alarmType" >${I18n.alarminfo_type}</th>
                                        <th name="alarmParam" >${I18n.alarminfo_param}</th>
					                  	<th name="alarmDesc" >${I18n.alarminfo_alarmdesc}</th>
					                  	<th name="createTime" >${I18n.system_create_time}</th>
					                  	<th name="updateTime" >${I18n.system_update_time}</th>
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
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<!-- alarminfo模态框 -->
<div class="modal fade" id="alarminfoModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" ></h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<input name="id" value="" type="hidden"/>
					<input id="editType" value="add" type="hidden"/>
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">${I18n.alarminfo_type}<font color="red">*</font></label>
						<div class="col-sm-4">
							<select class="form-control" name="alarmType" >
								<#list JobAlarmerEnum as item>
									<option value="${item}" >${item.title}</option>
								</#list>
		                  	</select>
						</div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.alarminfo_name}<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="alarmName" placeholder="${I18n.system_please_input}${I18n.alarminfo_name}" maxlength="50" ></div>
					</div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.alarminfo_param}<font color="black">*</font></label>
                        <div class="col-sm-10">
                            <textarea class="textarea form-control" name="alarmParam" placeholder="${I18n.system_please_input}${I18n.alarminfo_param}" maxlength="512" style="height: 63px; line-height: 1.2;"></textarea>
						</div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.alarminfo_alarmdesc}<font color="black">*</font></label>
                        <div class="col-sm-10">
                            <textarea class="textarea form-control" name="alarmDesc" placeholder="${I18n.system_please_input}${I18n.alarminfo_alarmdesc}" maxlength="512" style="height: 63px; line-height: 1.2;"></textarea>
						</div>
                    </div>
                    <hr>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
							<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">${I18n.system_cancel}</button>
						</div>
					</div>
				</form>
         	</div>
		</div>
	</div>
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/js/alarminfo.index.1.js"></script>
</body>
</html>
