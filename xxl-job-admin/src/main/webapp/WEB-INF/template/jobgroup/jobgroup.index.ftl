<!DOCTYPE html>
<html>
<head>
  	<title>任务调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker-bs3.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>执行器管理<small>任务调度中心</small></h1>
		</section>

		<!-- Main content -->
	    <section class="content">
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header">
							<h3 class="box-title">执行器列表</h3>&nbsp;&nbsp;
                            <button class="btn btn-info btn-xs pull-left2 add" >+新增执行器</button>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            调度中心OnLine:<#if adminAddressList?exists><#list adminAddressList as item><span class="badge bg-green">${item}</span></#list></#if>
						</div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
                                        <th name="id" >ID</th>
                                        <th name="appName" >AppName</th>
                                        <th name="title" >名称</th>
					                  	<th name="order" >排序</th>
                                        <th name="registryList" >OnLine 机器</th>
                                        <th name="operate" >操作</th>
					                </tr>
				                </thead>
                                <tbody>
								<#if list?exists && list?size gt 0>
								<#list list as group>
									<tr>
                                        <td>${group.id}</td>
                                        <td>${group.appName}</td>
                                        <td>${group.title}</td>
                                        <td>${group.order}</td>
                                        <td><#if group.registryList?exists><#list group.registryList as item><span class="badge bg-green">${item}</span><br></#list></#if></td>
										<td>
                                            <button class="btn btn-warning btn-xs update" id="${group.id}" appName="${group.appName}" title="${group.title}" order="${group.order}" >编辑</button>
                                            <button class="btn btn-danger btn-xs remove" id="${group.id}" >删除</button>
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
                    <h4 class="modal-title" >新增执行器</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="appName" placeholder="请输入“AppName”" maxlength="64" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">名称<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="请输入“名称”" maxlength="12" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">排序<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="order" placeholder="请输入“排序”" maxlength="50" ></div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
                                <button type="submit" class="btn btn-primary"  >保存</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
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
                    <h4 class="modal-title" >编辑执行器</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">AppName<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="appName" placeholder="请输入“AppName”" maxlength="64" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">名称<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="title" placeholder="请输入“名称”" maxlength="12" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">排序<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="order" placeholder="请输入“排序”" maxlength="50" ></div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
                                <button type="submit" class="btn btn-primary"  >保存</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
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
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/js/jobgroup.index.1.js"></script>
</body>
</html>
