<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <title><@spring.message code="admin_name" /></title>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "jobgroup" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1><@spring.message code="jobgroup_name" /></h1>
		</section>

		<!-- Main content -->
	    <section class="content">
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header">
							<h3 class="box-title"><@spring.message code="jobgroup_list" /></h3>&nbsp;&nbsp;
                            <button class="btn btn-info btn-xs pull-left2 add" ><@spring.message code="jobgroup_add" /></button>
						</div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
                                        <#--<th name="id" >ID</th>-->
                                        <th name="order" ><@spring.message code="jobgroup_field_order" /></th>
                                        <th name="appName" >AppName</th>
                                        <th name="title" ><@spring.message code="jobgroup_field_title" /></th>
                                        <th name="addressType" ><@spring.message code="jobgroup_field_addressType" /></th>
                                        <th name="registryList" >OnLine <@spring.message code="jobgroup_field_registryList" /></th>
                                        <th name="operate" ><@spring.message code="system_opt" /></th>
					                </tr>
				                </thead>
                                <tbody>
								<#if list?exists && list?size gt 0>
								<#list list as group>
									<tr>
                                        <#--<td>${group.id}</td>-->
                                        <td>${group.order}</td>
                                        <td>${group.appName}</td>
                                        <td>${group.title}</td>
                                        <td><#if group.addressType==0><@spring.message code="jobgroup_field_addressType_0" /><#else><@spring.message code="jobgroup_field_addressType_1" /></#if></td>
                                        <td>
                                            <#if group.registryList?exists>
                                                <#list group.registryList as item>
                                                    <span class="badge bg-green" title="${item}" >
                                                        <#if item?length gt 35>
                                                            ${item?substring(0, 35)}...
                                                        <#else>
                                                            ${item}
                                                        </#if>
                                                    </span>
                                                    <br>
                                                </#list>
                                            </#if>
                                        </td>
										<td>
                                            <button class="btn btn-warning btn-xs update"
                                                    id="${group.id}"
                                                    appName="${group.appName}"
                                                    title="${group.title}"
                                                    order="${group.order}"
                                                    addressType="${group.addressType}"
                                                    addressList="<#if group.addressList?exists>${group.addressList} ></#if>"><@spring.message code="system_opt_edit" /></button>
                                            <button class="btn btn-danger btn-xs remove" id="${group.id}" ><@spring.message code="system_opt_del" /></button>
										</td>
									</tr>
								</#list>
								</#if>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>

    <!-- 新增.模态框 -->
    <div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
        <div class="modal-dialog ">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" ><@spring.message code="jobgroup_add" /></h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="appName" placeholder="<@spring.message code="system_please_input" />AppName" maxlength="64" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_title" /><font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="<@spring.message code="system_please_input" /><@spring.message code="jobgroup_field_title" />" maxlength="12" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_order" /><font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="order" placeholder="<@spring.message code="system_please_input" /><@spring.message code="jobgroup_field_order" />" maxlength="50" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_addressType" /><font color="red">*</font></label>
                            <div class="col-sm-10">
                                <input type="radio" name="addressType" value="0" checked /><@spring.message code="jobgroup_field_addressType_0" />
                                &nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="radio" name="addressType" value="1" /><@spring.message code="jobgroup_field_addressType_1" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_registryList" /><font color="red">*</font></label>
                            <div class="col-sm-10">
                                <textarea class="textarea" name="addressList" maxlength="512" placeholder="<@spring.message code="jobgroup_field_registryList_placeholder" />" readonly="readonly" style="background-color:#eee; width: 100%; height: 100px; font-size: 14px; line-height: 10px; border: 1px solid #dddddd; padding: 10px;"></textarea>
                            </div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
                                <button type="submit" class="btn btn-primary"  ><@spring.message code="system_save" /></button>
                                <button type="button" class="btn btn-default" data-dismiss="modal"><@spring.message code="system_cancel" /></button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- 更新.模态框 -->
    <div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
        <div class="modal-dialog ">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" ><@spring.message code="jobgroup_edit" /></h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="appName" placeholder="<@spring.message code="system_please_input" />AppName" maxlength="64" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_title" /><font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="<@spring.message code="system_please_input" /><@spring.message code="jobgroup_field_title" />" maxlength="12" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_order" /><font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="order" placeholder="<@spring.message code="system_please_input" /><@spring.message code="jobgroup_field_order" />" maxlength="50" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_addressType" /><font color="red">*</font></label>
                            <div class="col-sm-10">
                                <input type="radio" name="addressType" value="0" /><@spring.message code="jobgroup_field_addressType_0" />
                                &nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="radio" name="addressType" value="1" /><@spring.message code="jobgroup_field_addressType_1" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label"><@spring.message code="jobgroup_field_registryList" /><font color="red">*</font></label>
                            <div class="col-sm-10">
                                <textarea class="textarea" name="addressList" maxlength="512" placeholder="<@spring.message code="jobgroup_field_registryList_placeholder" />" readonly="readonly" style="background-color:#eee; width: 100%; height: 100px; font-size: 14px; line-height: 10px; border: 1px solid #dddddd; padding: 10px;"></textarea>
                            </div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
                                <button type="submit" class="btn btn-primary"  ><@spring.message code="system_save" /></button>
                                <button type="button" class="btn btn-default" data-dismiss="modal"><@spring.message code="system_cancel" /></button>
                                <input type="hidden" name="id" >
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<#-- jquery.validate -->
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/js/jobgroup.index.1.js"></script>
</body>
</html>
