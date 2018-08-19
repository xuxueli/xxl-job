<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <title>${I18n.admin_name}</title>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft "jobinfo" />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>${I18n.jobinfo_name}</h1>
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
                <div class="col-xs-3">
                    <div class="input-group">
                        <span class="input-group-addon">${I18n.jobinfo_field_jobdesc}</span>
                        <input type="text" class="form-control" id="jobDesc" autocomplete="on" >
                    </div>
                </div>
                <div class="col-xs-3">
                    <div class="input-group">
                        <span class="input-group-addon">JobHandler</span>
                        <input type="text" class="form-control" id="executorHandler" autocomplete="on" >
                    </div>
                </div>
	            <div class="col-xs-1">
	            	<button class="btn btn-block btn-info" id="searchBtn">${I18n.system_search}</button>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-success add" type="button">${I18n.jobinfo_field_add}</button>
	            </div>
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
					            		<th name="id" >${I18n.jobinfo_field_id}</th>
					                	<th name="jobGroup" >${I18n.jobinfo_field_jobgroup}</th>
					                  	<th name="jobDesc" >${I18n.jobinfo_field_jobdesc}</th>
                                        <th name="glueType" >${I18n.jobinfo_field_gluetype}</th>
					                  	<th name="executorParam" >${I18n.jobinfo_field_executorparam}</th>
                                        <th name="jobCron" >Cron</th>
					                  	<th name="addTime" >addTime</th>
					                  	<th name="updateTime" >updateTime</th>
					                  	<th name="author" >${I18n.jobinfo_field_author}</th>
					                  	<th name="alarmEmail" >${I18n.jobinfo_field_alarmemail}</th>
					                  	<th name="jobStatus" >${I18n.system_status}</th>
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

<!-- job新增.模态框 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >${I18n.jobinfo_field_add}</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
						<label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_jobgroup}<font color="red">*</font></label>
						<div class="col-sm-4">
							<select class="form-control" name="jobGroup" >
		            			<#list JobGroupList as group>
		            				<option value="${group.id}" <#if jobGroup==group.id>selected</#if> >${group.title}</option>
		            			</#list>
		                  	</select>
						</div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_jobdesc}<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="jobDesc" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_jobdesc}" maxlength="50" ></div>
					</div>

                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">父id</label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="parentId" maxlength="512" ></div>
					</div>

                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorRouteStrategy}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="executorRouteStrategy" >
							<#list ExecutorRouteStrategyEnum as item>
                                <option value="${item}" >${item.title}</option>
							</#list>
                            </select>
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">Cron<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="jobCron" placeholder="${I18n.system_please_input}Cron" maxlength="128" ></div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_gluetype}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control glueType" name="glueType" >
								<#list GlueTypeEnum as item>
									<option value="${item}" >${item.desc}</option>
								</#list>
                            </select>
                        </div>
                        <label for="firstname" class="col-sm-2 control-label">JobHandler<font color="black">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="executorHandler" placeholder="${I18n.system_please_input}JobHandler" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorparam}<font color="black">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="executorParam" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_executorparam}" maxlength="512" ></div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_childJobId}<font color="black">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="childJobId" placeholder="${I18n.jobinfo_field_childJobId_placeholder}" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorBlockStrategy}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="executorBlockStrategy" >
								<#list ExecutorBlockStrategyEnum as item>
									<option value="${item}" >${item.title}</option>
								</#list>
                            </select>
						</div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorFailStrategy}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="executorFailStrategy" >
								<#list ExecutorFailStrategyEnum as item>
									<option value="${item}" >${item.title}</option>
								</#list>
                            </select>
						</div>
                    </div>
					<div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_author}<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="author" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_author}" maxlength="50" ></div>
						<label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_alarmemail}<font color="black">*</font></label>
						<div class="col-sm-4"><input type="text" class="form-control" name="alarmEmail" placeholder="${I18n.jobinfo_field_alarmemail_placeholder}" maxlength="100" ></div>
					</div>

                    <hr>
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
							<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">${I18n.system_cancel}</button>
						</div>
					</div>

<input type="hidden" name="glueRemark" value="GLUE代码初始化" >
<textarea name="glueSource" style="display:none;" ></textarea>
<textarea class="glueSource_java" style="display:none;" >
package com.xxl.job.service.handler;

import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

public class DemoGlueJobHandler extends IJobHandler {

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("XXL-JOB, Hello World.");
		return ReturnT.SUCCESS;
	}

}
</textarea>
<textarea class="glueSource_shell" style="display:none;" >
#!/bin/bash
echo "xxl-job: hello shell"

echo "${I18n.jobinfo_script_location}：$0"
echo "${I18n.jobinfo_field_executorparam}：$1"
echo "${I18n.jobinfo_shard_index} = $2"
echo "${I18n.jobinfo_shard_total} = $3"
<#--echo "参数数量：$#"
for param in $*
do
    echo "参数 : $param"
    sleep 1s
done-->

echo "Good bye!"
exit 0
</textarea>
<textarea class="glueSource_python" style="display:none;" >
#!/usr/bin/python
# -*- coding: UTF-8 -*-
import time
import sys

print "xxl-job: hello python"

print "${I18n.jobinfo_script_location}：", sys.argv[0]
print "${I18n.jobinfo_field_executorparam}：", sys.argv[1]
print "${I18n.jobinfo_shard_index}：", sys.argv[2]
print "${I18n.jobinfo_shard_total}：", sys.argv[3]
<#--for i in range(1, len(sys.argv)):
	time.sleep(1)
	print "参数", i, sys.argv[i]-->

print "Good bye!"
exit(0)
<#--
import logging
logging.basicConfig(level=logging.DEBUG)
logging.info("脚本文件：" + sys.argv[0])
-->
</textarea>
<textarea class="glueSource_nodejs" style="display:none;" >
#!/usr/bin/env node
console.log("xxl-job: hello nodejs")

var arguments = process.argv

console.log("${I18n.jobinfo_script_location}: " + arguments[1])
console.log("${I18n.jobinfo_field_executorparam}: " + arguments[2])
console.log("${I18n.jobinfo_shard_index}: " + arguments[3])
console.log("${I18n.jobinfo_shard_total}: " + arguments[4])
<#--for (var i = 2; i < arguments.length; i++){
	console.log("参数 %s = %s", (i-1), arguments[i]);
}-->

console.log("Good bye!")
process.exit(0)
</textarea>		
				</form>
         	</div>
		</div>
	</div>
</div>

<!-- 更新.模态框 -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
            	<h4 class="modal-title" >${I18n.jobinfo_field_update}</h4>
         	</div>
         	<div class="modal-body">
				<form class="form-horizontal form" role="form" >
					<div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_jobgroup}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="jobGroup" disabled >
							<#list JobGroupList as group>
                                <option value="${group.id}" >${group.title}</option>
							</#list>
                            </select>
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_jobdesc}<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="jobDesc" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_jobdesc}" maxlength="50" ></div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorRouteStrategy}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="executorRouteStrategy" >
							<#list ExecutorRouteStrategyEnum as item>
                                <option value="${item}" >${item.title}</option>
							</#list>
                            </select>
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">Cron<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="jobCron" placeholder="${I18n.system_please_input}Cron" maxlength="128" ></div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_gluetype}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control glueType" name="glueType" disabled >
							<#list GlueTypeEnum as item>
                                <option value="${item}" >${item.desc}</option>
							</#list>
                            </select>
                        </div>
                        <label for="firstname" class="col-sm-2 control-label">JobHandler<font color="black">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="executorHandler" placeholder="${I18n.system_please_input}JobHandler" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorparam}<font color="black">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="executorParam" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_executorparam}" maxlength="512" ></div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_childJobId}<font color="black">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="childJobId" placeholder="${I18n.jobinfo_field_childJobId_placeholder}" maxlength="100" ></div>
                    </div>
                    <div class="form-group">
                        <label for="firstname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorBlockStrategy}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="executorBlockStrategy" >
							<#list ExecutorBlockStrategyEnum as item>
                                <option value="${item}" >${item.title}</option>
							</#list>
                            </select>
                        </div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_executorFailStrategy}<font color="red">*</font></label>
                        <div class="col-sm-4">
                            <select class="form-control" name="executorFailStrategy" >
							<#list ExecutorFailStrategyEnum as item>
                                <option value="${item}" >${item.title}</option>
							</#list>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_author}<font color="red">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="author" placeholder="${I18n.system_please_input}${I18n.jobinfo_field_author}" maxlength="50" ></div>
                        <label for="lastname" class="col-sm-2 control-label">${I18n.jobinfo_field_alarmemail}<font color="black">*</font></label>
                        <div class="col-sm-4"><input type="text" class="form-control" name="alarmEmail" placeholder="${I18n.jobinfo_field_alarmemail_placeholder}" maxlength="100" ></div>
                    </div>

					<hr>
					<div class="form-group">
                        <div class="col-sm-offset-3 col-sm-6">
							<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
							<button type="button" class="btn btn-default" data-dismiss="modal">${I18n.system_cancel}</button>
                            <input type="hidden" name="id" >
						</div>
					</div>

				</form>
				<input type="hidden" id="parentIdParam" value="0" />
         	</div>
		</div>
	</div>
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script>

    function research(id){
        $('#parentIdParam').val(id)
        jobTable.fnDraw();
    }

    $(function() {

        console.log('test');

        // init date tables
		jobTable = $("#job_list").dataTable({
            "deferRender": true,
            "processing" : true,
            "serverSide": true,
            "ajax": {
                url: base_url + "/jobinfo/pageList",
                type:"post",
                data : function ( d ) {
                    var obj = {};
                    obj.jobGroup = $('#jobGroup').val();
                    obj.jobDesc = $('#jobDesc').val();
                    obj.executorHandler = $('#executorHandler').val();
                    obj.start = d.start;
                    obj.length = d.length;
                    obj.parentId = $('#parentIdParam').val()
                    return obj;
                }
            },
            "searching": false,
            "ordering": false,
            //"scrollX": true,	// scroll x，close self-adaption
            "columns": [
                {
                    "data": 'id',
                    "bSortable": false,
                    "visible" : true,
                    "width":'10%',
                    "render": function ( data, type, row ) {
                        return data+(row.childJobId && row.childJobId.length>0?"&nbsp;<a href='javascript:research("+data+")'>子任务</a>":"")
                    }
                },
                {
                    "data": 'jobGroup',
                    "visible" : false,
                    "width":'20%',
                    "render": function ( data, type, row ) {
                        var groupMenu = $("#jobGroup").find("option");
                        for ( var index in $("#jobGroup").find("option")) {
                            if ($(groupMenu[index]).attr('value') == data) {
                                return $(groupMenu[index]).html();
                            }
                        }
                        return data;
                    }
                },
                {
                    "data": 'jobDesc',
                    "visible" : true,
                    "width":'20%'
                },
                {
                    "data": 'glueType',
                    "width":'20%',
                    "visible" : true,
                    "render": function ( data, type, row ) {
                        var glueTypeTitle = findGlueTypeTitle(row.glueType);
                        if (row.executorHandler) {
                            return glueTypeTitle +"：" + row.executorHandler;
                        } else {
                            return glueTypeTitle;
                        }
                    }
                },
                { "data": 'executorParam', "visible" : false},
                {
                    "data": 'jobCron',
                    "visible" : true,
                    "width":'10%'
                },
                {
                    "data": 'addTime',
                    "visible" : false,
                    "render": function ( data, type, row ) {
                        return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                    }
                },
                {
                    "data": 'updateTime',
                    "visible" : false,
                    "render": function ( data, type, row ) {
                        return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                    }
                },
                { "data": 'author', "visible" : true, "width":'10%'},
                { "data": 'alarmEmail', "visible" : false},
                {
                    "data": 'jobStatus',
                    "width":'10%',
                    "visible" : true,
                    "render": function ( data, type, row ) {
                        if ('NORMAL' == data) {
                            return '<small class="label label-success" ><i class="fa fa-clock-o"></i>'+ data +'</small>';
                        } else if ('PAUSED' == data){
                            return '<small class="label label-default" ><i class="fa fa-clock-o"></i>'+ data +'</small>';
                        } else if ('BLOCKED' == data){
                            return '<small class="label label-default" ><i class="fa fa-clock-o"></i>'+ data +'</small>';
                        }
                        return data;
                    }
                },
                {
                    "data": I18n.system_opt ,
                    "width":'15%',
                    "render": function ( data, type, row ) {
                        return function(){
                            // status
                            var pause_resume = "";
                            if ('NORMAL' == row.jobStatus) {
                                pause_resume = '<button class="btn btn-primary btn-xs job_operate" _type="job_pause" type="button">'+ I18n.jobinfo_opt_pause +'</button>  ';
                            } else if ('PAUSED' == row.jobStatus){
                                pause_resume = '<button class="btn btn-primary btn-xs job_operate" _type="job_resume" type="button">'+ I18n.jobinfo_opt_resume +'</button>  ';
                            }
                            // log url
                            var logUrl = base_url +'/joblog?jobId='+ row.id;

                            // log url
                            var codeBtn = "";
                            if ('BEAN' != row.glueType) {
                                var codeUrl = base_url +'/jobcode?jobId='+ row.id;
                                codeBtn = '<button class="btn btn-warning btn-xs" type="button" onclick="javascript:window.open(\'' + codeUrl + '\')" >GLUE</button>  '
                            }

                            // html
                            tableData['key'+row.id] = row;
                            var html = '<p id="'+ row.id +'" >'+
                                    '<button class="btn btn-primary btn-xs job_operate" _type="job_trigger" type="button">'+ I18n.jobinfo_opt_run +'</button>  '+
                                    pause_resume +
                                    '<button class="btn btn-primary btn-xs" type="job_del" type="button" onclick="javascript:window.open(\'' + logUrl + '\')" >'+ I18n.jobinfo_opt_log +'</button><br>  '+
                                    '<button class="btn btn-warning btn-xs update" type="button">'+ I18n.system_opt_edit +'</button>  '+
                                    codeBtn +
                                    '<button class="btn btn-danger btn-xs job_operate" _type="job_del" type="button">'+ I18n.system_opt_del +'</button>  '+
                                    '</p>';

                            return html;
                        };
                    }
                }
            ],
            "language" : {
                "sProcessing" : I18n.dataTable_sProcessing ,
                "sLengthMenu" : I18n.dataTable_sLengthMenu ,
                "sZeroRecords" : I18n.dataTable_sZeroRecords ,
                "sInfo" : I18n.dataTable_sInfo ,
                "sInfoEmpty" : I18n.dataTable_sInfoEmpty ,
                "sInfoFiltered" : I18n.dataTable_sInfoFiltered ,
                "sInfoPostFix" : "",
                "sSearch" : I18n.dataTable_sSearch ,
                "sUrl" : "",
                "sEmptyTable" : I18n.dataTable_sEmptyTable ,
                "sLoadingRecords" : I18n.dataTable_sLoadingRecords ,
                "sInfoThousands" : ",",
                "oPaginate" : {
                    "sFirst" : I18n.dataTable_sFirst ,
                    "sPrevious" : I18n.dataTable_sPrevious ,
                    "sNext" : I18n.dataTable_sNext ,
                    "sLast" : I18n.dataTable_sLast
                },
                "oAria" : {
                    "sSortAscending" : I18n.dataTable_sSortAscending ,
                    "sSortDescending" : I18n.dataTable_sSortDescending
                }
            }
        });


        // table data
        var tableData = {};

        // search btn
        $('#searchBtn').on('click', function(){
            jobTable.fnDraw();
        });

        // jobGroup change
        $('#jobGroup').on('change', function(){
            //reload
            var jobGroup = $('#jobGroup').val();
            window.location.href = base_url + "/jobinfo?jobGroup=" + jobGroup;
        });

        // job operate
        $("#job_list").on('click', '.job_operate',function() {
            var typeName;
            var url;
            var needFresh = false;

            var type = $(this).attr("_type");
            if ("job_pause" == type) {
                typeName = I18n.jobinfo_opt_pause ;
                url = base_url + "/jobinfo/pause";
                needFresh = true;
            } else if ("job_resume" == type) {
                typeName = I18n.jobinfo_opt_resume ;
                url = base_url + "/jobinfo/resume";
                needFresh = true;
            } else if ("job_del" == type) {
                typeName = I18n.system_opt_del ;
                url = base_url + "/jobinfo/remove";
                needFresh = true;
            } else if ("job_trigger" == type) {
                typeName = I18n.jobinfo_opt_run ;
                url = base_url + "/jobinfo/trigger";
            } else {
                return;
            }

            var id = $(this).parent('p').attr("id");

            layer.confirm( I18n.system_ok + typeName + '?', {
                icon: 3,
                title: I18n.system_tips ,
                btn: [ I18n.system_ok, I18n.system_cancel ]
            }, function(index){
                layer.close(index);

                $.ajax({
                    type : 'POST',
                    url : url,
                    data : {
                        "id" : id
                    },
                    dataType : "json",
                    success : function(data){
                        if (data.code == 200) {

                            layer.open({
                                title: I18n.system_tips,
                                btn: [ I18n.system_ok ],
                                content: typeName + I18n.system_success ,
                                icon: '1',
                                end: function(layero, index){
                                    if (needFresh) {
                                        //window.location.reload();
                                        jobTable.fnDraw();
                                    }
                                }
                            });
                        } else {
                            layer.open({
                                title: I18n.system_tips,
                                btn: [ I18n.system_ok ],
                                content: (data.msg || typeName + I18n.system_fail ),
                                icon: '2'
                            });
                        }
                    },
                });
            });
        });

        // add
        $(".add").click(function(){
            $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
        });
        var addModalValidate = $("#addModal .form").validate({
            errorElement : 'span',
            errorClass : 'help-block',
            focusInvalid : true,
            rules : {
                jobDesc : {
                    required : true,
                    maxlength: 50
                },
                jobCron : {
                    required : true
                },
                author : {
                    required : true
                }
            },
            messages : {
                jobDesc : {
                    required : I18n.system_please_input + I18n.jobinfo_field_jobdesc
                },
                jobCron : {
                    required : I18n.system_please_input + "Cron"
                },
                author : {
                    required : I18n.system_please_input + I18n.jobinfo_field_author
                }
            },
            highlight : function(element) {
                $(element).closest('.form-group').addClass('has-error');
            },
            success : function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },
            errorPlacement : function(error, element) {
                element.parent('div').append(error);
            },
            submitHandler : function(form) {
                $.post(base_url + "/jobinfo/add",  $("#addModal .form").serialize(), function(data, status) {
                    if (data.code == "200") {
                        $('#addModal').modal('hide');
                        layer.open({
                            title: I18n.system_tips ,
                            btn: [ I18n.system_ok ],
                            content: I18n.system_add_suc ,
                            icon: '1',
                            end: function(layero, index){
                                jobTable.fnDraw();
                                //window.location.reload();
                            }
                        });
                    } else {
                        layer.open({
                            title: I18n.system_tips ,
                            btn: [ I18n.system_ok ],
                            content: (data.msg || I18n.system_add_fail),
                            icon: '2'
                        });
                    }
                });
            }
        });
        $("#addModal").on('hide.bs.modal', function () {
            $("#addModal .form")[0].reset();
            addModalValidate.resetForm();
            $("#addModal .form .form-group").removeClass("has-error");
            $(".remote_panel").show();	// remote

            $("#addModal .form input[name='executorHandler']").removeAttr("readonly");
        });


        // glueType change
        $(".glueType").change(function(){
            // executorHandler
            var $executorHandler = $(this).parents("form").find("input[name='executorHandler']");
            var glueType = $(this).val();
            if ('BEAN' != glueType) {
                $executorHandler.val("");
                $executorHandler.attr("readonly","readonly");
            } else {
                $executorHandler.removeAttr("readonly");
            }
        });

        $("#addModal .glueType").change(function(){
            // glueSource
            var glueType = $(this).val();
            if ('GLUE_GROOVY'==glueType){
                $("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_java").val() );
            } else if ('GLUE_SHELL'==glueType){
                $("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_shell").val() );
            } else if ('GLUE_PYTHON'==glueType){
                $("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_python").val() );
            } else if ('GLUE_NODEJS'==glueType){
                $("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_nodejs").val() );
            }
        });

        // update
        $("#job_list").on('click', '.update',function() {

            var id = $(this).parent('p').attr("id");
            var row = tableData['key'+id];

            // base data
            $("#updateModal .form input[name='id']").val( row.id );
            $('#updateModal .form select[name=jobGroup] option[value='+ row.jobGroup +']').prop('selected', true);
            $("#updateModal .form input[name='jobDesc']").val( row.jobDesc );
            $("#updateModal .form input[name='jobCron']").val( row.jobCron );
            $("#updateModal .form input[name='author']").val( row.author );
            $("#updateModal .form input[name='alarmEmail']").val( row.alarmEmail );
            $('#updateModal .form select[name=executorRouteStrategy] option[value='+ row.executorRouteStrategy +']').prop('selected', true);
            $("#updateModal .form input[name='executorHandler']").val( row.executorHandler );
            $("#updateModal .form input[name='executorParam']").val( row.executorParam );
            $("#updateModal .form input[name='childJobId']").val( row.childJobId );
            $('#updateModal .form select[name=executorBlockStrategy] option[value='+ row.executorBlockStrategy +']').prop('selected', true);
            $('#updateModal .form select[name=executorFailStrategy] option[value='+ row.executorFailStrategy +']').prop('selected', true);
            $('#updateModal .form select[name=glueType] option[value='+ row.glueType +']').prop('selected', true);

            $("#updateModal .form select[name=glueType]").change();

            // show
            $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
        });
        var updateModalValidate = $("#updateModal .form").validate({
            errorElement : 'span',
            errorClass : 'help-block',
            focusInvalid : true,

            rules : {
                jobDesc : {
                    required : true,
                    maxlength: 50
                },
                jobCron : {
                    required : true
                },
                author : {
                    required : true
                }
            },
            messages : {
                jobDesc : {
                    required : I18n.system_please_input + I18n.jobinfo_field_jobdesc
                },
                jobCron : {
                    required : I18n.system_please_input + "Cron"
                },
                author : {
                    required : I18n.system_please_input + I18n.jobinfo_field_author
                }
            },
            highlight : function(element) {
                $(element).closest('.form-group').addClass('has-error');
            },
            success : function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },
            errorPlacement : function(error, element) {
                element.parent('div').append(error);
            },
            submitHandler : function(form) {
                // post
                $.post(base_url + "/jobinfo/update", $("#updateModal .form").serialize(), function(data, status) {
                    if (data.code == "200") {
                        $('#updateModal').modal('hide');
                        layer.open({
                            title: I18n.system_tips ,
                            btn: [ I18n.system_ok ],
                            content: I18n.system_update_suc ,
                            icon: '1',
                            end: function(layero, index){
                                //window.location.reload();
                                jobTable.fnDraw();
                            }
                        });
                    } else {
                        layer.open({
                            title: I18n.system_tips ,
                            btn: [ I18n.system_ok ],
                            content: (data.msg || I18n.system_update_fail ),
                            icon: '2'
                        });
                    }
                });
            }
        });
        $("#updateModal").on('hide.bs.modal', function () {
            $("#updateModal .form")[0].reset()
        });

        /**
         * find title by name, GlueType
         */
        function findGlueTypeTitle(glueType) {
            var glueTypeTitle;
            $("#addModal .form select[name=glueType] option").each(function () {
                var name = $(this).val();
                var title = $(this).text();
                if (glueType == name) {
                    glueTypeTitle = title;
                    return false
                }
            });
            return glueTypeTitle;
        }

    });

</script>
</body>
</html>
