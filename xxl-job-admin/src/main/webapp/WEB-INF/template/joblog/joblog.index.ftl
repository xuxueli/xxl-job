<!DOCTYPE html>
<html>
<head>
  	<title>任务调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "joblog" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>调度日志<small>任务调度中心</small></h1>
			<!--
			<ol class="breadcrumb">
				<li><a><i class="fa fa-dashboard"></i>调度日志</a></li>
				<li class="active">调度管理</li>
			</ol>
			-->
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	    		<div class="col-xs-3">
 					<div class="input-group">
	                	<span class="input-group-addon">执行器</span>
                		<select class="form-control" id="jobGroup"  paramVal="<#if jobInfo?exists>${jobInfo.jobGroup}</#if>" >
                            <option value="0" >全部</option>
                			<#list JobGroupList as group>
                				<option value="${group.id}" >${group.title}</option>
                			</#list>
	                  	</select>
	              	</div>
	            </div>
	            <div class="col-xs-3">
	              	<div class="input-group">
	                	<span class="input-group-addon">任务</span>
                        <select class="form-control" id="jobId" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >
                            <option value="0" >全部</option>
						</select>
	              	</div>
	            </div>
	            <div class="col-xs-4">
              		<div class="input-group">
                		<span class="input-group-addon">
	                  		调度时间
	                	</span>
	                	<input type="text" class="form-control" id="filterTime" readonly >
	              	</div>
	            </div>

                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                </div>

	            <div class="col-xs-1">
                    <button class="btn btn-block btn-nomal" id="clearLog">清理</button>
	            </div>
          	</div>
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header hide"><h3 class="box-title">调度日志</h3></div>
			            <div class="box-body">
			              	<table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
				                <thead>
					            	<tr>
					                	<th name="id" >id</th>
                                        <th name="jobGroup" >执行器ID</th>
					                	<th name="jobId" >任务ID</th>
                                        <th name="triggerTime" >调度时间</th>
                                        <th name="triggerCode" >调度结果</th>
                                        <th name="triggerMsg" >调度备注</th>
					                  	<th name="executorAddress" >执行器地址</th>
                                        <th name="glueType" >运行模式</th>
					                  	<th name="executorParam" >任务参数</th>
					                  	<th name="handleTime" >执行时间</th>
					                  	<th name="handleCode" >执行结果</th>
					                  	<th name="handleMsg" >执行备注</th>
					                  	<th name="handleMsg" >操作</th>
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
                <h4 class="modal-title" >日志清理</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div class="form-group">
                        <label class="col-sm-3 control-label"">执行器：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobGroupText" readonly >
							<input type="hidden" name="jobGroup" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label"">任务：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobIdText" readonly >
                            <input type="hidden" name="jobId" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label"">清理类型：</label>
                        <div class="col-sm-9">
                            <select class="form-control" name="type" >
                                <option value="1" >清理一个月之前日志数据</option>
                                <option value="2" >清理三个月之前日志数据</option>
                                <option value="3" >清理六个月之前日志数据</option>
                                <option value="4" >清理一年之前日志数据</option>
                                <option value="5" >清理一千条以前日志数据</option>
                                <option value="6" >清理一万条以前日志数据</option>
                                <option value="7" >清理三万条以前日志数据</option>
                                <option value="8" >清理十万条以前日志数据</option>
                                <option value="9" >清理所用日志数据</option>
                            </select>
                        </div>
                    </div>

                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-6">
                            <button type="button" class="btn btn-primary ok" >确定</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/joblog.index.1.js"></script>
</body>
</html>
