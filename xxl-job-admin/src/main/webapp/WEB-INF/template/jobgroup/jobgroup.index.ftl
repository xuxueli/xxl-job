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
			<h1>分组管理<small>任务调度中心</small></h1>
		</section>

		<!-- Main content -->
	    <section class="content">
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header">
							<h3 class="box-title">分组管理</h3>
                            <button class="btn btn-success btn-xs pull-left2 add" >+新增分组</button>
						</div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
                                        <th name="groupDesc" >名称</th>
                                        <th name="groupName" >AppName</th>
					                  	<th name="order" >排序</th>
                                        <th name="operate" >操作</th>
					                </tr>
				                </thead>
                                <tbody>
								<#if list?exists && list?size gt 0>
								<#list list as group>
									<tr>
                                        <td>${group.groupDesc}</td>
                                        <td>${group.groupName}</td>
                                        <td>${group.order}</td>
										<td>
                                            <button class="btn btn-warning btn-xs update" groupName="${group.groupName}" groupDesc="${group.groupDesc}" order="${group.order}" >编辑</button>
                                            <button class="btn btn-danger btn-xs remove" groupName="${group.groupName}" >删除</button>
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
                    <h4 class="modal-title" >新增分组</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">分组<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="groupName" placeholder="请输入“分组”" maxlength="200" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">描述<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="groupDesc" placeholder="请输入“描述”" maxlength="200" ></div>
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
                    <h4 class="modal-title" >编辑分组</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">分组<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="groupName" placeholder="请输入“分组”" maxlength="200" readonly ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">描述<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="groupDesc" placeholder="请输入“描述”" maxlength="200" ></div>
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
