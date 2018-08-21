<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
    <title>${I18n.admin_name}</title>
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
			<h1>${I18n.joblog_name}</h1>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	    		<div class="col-xs-2">
 					<div class="input-group">
	                	<span class="input-group-addon">${I18n.jobinfo_field_jobgroup}</span>
                		<select class="form-control" id="jobGroup"  paramVal="<#if jobInfo?exists>${jobInfo.jobGroup}</#if>" >
                            <option value="0" >${I18n.system_all}</option>
                			<#list JobGroupList as group>
                				<option value="${group.id}" >${group.title}</option>
                			</#list>
	                  	</select>
	              	</div>
	            </div>
	            <div class="col-xs-2">
	              	<div class="input-group">
	                	<span class="input-group-addon">${I18n.jobinfo_job}</span>
                        <select class="form-control" id="jobId" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>" >
                            <option value="0" >${I18n.system_all}</option>
						</select>
	              	</div>
	            </div>

                <div class="col-xs-1">
                    <div class="input-group">

                        <input type="checkbox" id="showChild">
                        <span class="input-group-addon">显示子任务</span>
                    </div>
                </div>

                <div class="col-xs-2">
                    <div class="input-group">
                        <span class="input-group-addon">${I18n.joblog_status}</span>
                        <select class="form-control" id="logStatus" >
                            <option value="-1" >${I18n.joblog_status_all}</option>
                            <option value="1" >${I18n.joblog_status_suc}</option>
                            <option value="2" >${I18n.joblog_status_fail}</option>
                            <option value="3" >${I18n.joblog_status_running}</option>
                            <option value="5" >部分成功</option>
                        </select>
                    </div>
                </div>

	            <div class="col-xs-4">
              		<div class="input-group">
                		<span class="input-group-addon">
	                  		${I18n.joblog_field_triggerTime}
	                	</span>
	                	<input type="text" class="form-control" id="filterTime" readonly >
	              	</div>
	            </div>

                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">${I18n.system_search}</button>
                </div>

	            <div class="col-xs-1">
                    <button class="btn btn-block btn-nomal" id="clearLog">${I18n.joblog_clean}</button>
	            </div>
                <div class="col-xs-1">
                    <button class="btn btn-block btn-nomal" id="stopBatch">终止执行</button>
                </div>
          	</div>
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <#--<div class="box-header hide"><h3 class="box-title">调度日志</h3></div>-->
			            <div class="box-body">
			              	<form id="listForm">
                                <table id="joblog_list" class="table table-bordered table-striped display" width="100%" >
                                    <thead>
                                    <tr>
                                        <th name="jobId" ><input type="checkbox" id="allSelect">  &nbsp;${I18n.jobinfo_field_id}</th>
                                        <th>子任务信息</th>
                                        <th name="jobGroup" >jobGroup</th>
                                    <#--<th name="executorAddress" >执行器地址</th>
                                    <th name="glueType" >运行模式</th>
                                      <th name="executorParam" >任务参数</th>-->
                                        <th name="triggerTime" >${I18n.joblog_field_triggerTime}</th>
                                        <th name="triggerCode" >${I18n.joblog_field_triggerCode}</th>
                                        <th name="triggerMsg" >${I18n.joblog_field_triggerMsg}</th>
                                        <th name="handleTime" >${I18n.joblog_field_handleTime}</th>
                                        <th name="handleCode" >${I18n.joblog_field_handleCode}</th>
                                        <th name="handleMsg" >${I18n.joblog_field_handleMsg}</th>
                                        <th name="handleMsg" >${I18n.system_opt}</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </form>
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
                <h4 class="modal-title" >${I18n.joblog_clean_log}</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal form" role="form" >
                    <div class="form-group">
                        <label class="col-sm-3 control-label"">${I18n.jobinfo_field_jobgroup}：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobGroupText" readonly >
							<input type="hidden" name="jobGroup" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label"">${I18n.jobinfo_job}：</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control jobIdText" readonly >
                            <input type="hidden" name="jobId" >
						</div>
                    </div>

                    <div class="form-group">
                        <label class="col-sm-3 control-label"">${I18n.joblog_clean_type}：</label>
                        <div class="col-sm-9">
                            <select class="form-control" name="type" >
                                <option value="1" >${I18n.joblog_clean_type_1}</option>
                                <option value="2" >${I18n.joblog_clean_type_2}</option>
                                <option value="3" >${I18n.joblog_clean_type_3}</option>
                                <option value="4" >${I18n.joblog_clean_type_4}</option>
                                <option value="5" >${I18n.joblog_clean_type_5}</option>
                                <option value="6" >${I18n.joblog_clean_type_6}</option>
                                <option value="7" >${I18n.joblog_clean_type_7}</option>
                                <option value="8" >${I18n.joblog_clean_type_8}</option>
                                <option value="9" >${I18n.joblog_clean_type_9}</option>
                            </select>
                        </div>
                    </div>

                    <hr>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-6">
                            <button type="button" class="btn btn-primary ok" >${I18n.system_ok}</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">${I18n.system_cancel}</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<input type="hidden" id="parentIdParam" value="0" />
<@netCommon.commonScript />
<script>
    var GlueTypeEnum = {};
    <#list GlueTypeEnum as item>
    GlueTypeEnum['${item}'] = '${item.desc}';
    </#list>
</script>
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<#--<script src="${request.contextPath}/static/js/joblog.index.1.js"></script>-->
<script type="text/javascript">
    function research(id){
        $('#parentIdParam').val(id)
        logTable.fnDraw();
    }
    $(function() {

        // jobGroup change, job list init and select
        $("#jobGroup").on("change", function () {
            var jobGroup = $(this).children('option:selected').val();
            $.ajax({
                type : 'POST',
                async: false,   // async, avoid js invoke pagelist before jobId data init
                url : base_url + '/joblog/getJobsByGroup',
                data : {"jobGroup":jobGroup},
                dataType : "json",
                success : function(data){
                    if (data.code == 200) {
                        $("#jobId").html( '<option value="0" >'+ I18n.system_all +'</option>' );
                        $.each(data.content, function (n, value) {
                            $("#jobId").append('<option value="' + value.id + '" >' + value.jobDesc + '</option>');
                        });
                        if ($("#jobId").attr("paramVal")){
                            $("#jobId").find("option[value='" + $("#jobId").attr("paramVal") + "']").attr("selected",true);
                        }
                    } else {
                        layer.open({
                            title: I18n.system_tips ,
                            btn: [ I18n.system_ok ],
                            content: (data.msg || I18n.system_api_error ),
                            icon: '2'
                        });
                    }
                },
            });
        });
        if ($("#jobGroup").attr("paramVal")){
            $("#jobGroup").find("option[value='" + $("#jobGroup").attr("paramVal") + "']").attr("selected",true);
            $("#jobGroup").change();
        }

        // filter Time
        var rangesConf = {};
        rangesConf[I18n.daterangepicker_ranges_recent_hour] = [moment().subtract(1, 'hours'), moment()];
        rangesConf[I18n.daterangepicker_ranges_today] = [moment().startOf('day'), moment().endOf('day')];
        rangesConf[I18n.daterangepicker_ranges_yesterday] = [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')];
        rangesConf[I18n.daterangepicker_ranges_this_month] = [moment().startOf('month'), moment().endOf('month')];
        rangesConf[I18n.daterangepicker_ranges_last_month] = [moment().subtract(1, 'months').startOf('month'), moment().subtract(1, 'months').endOf('month')];
        rangesConf[I18n.daterangepicker_ranges_recent_week] = [moment().subtract(1, 'weeks').startOf('day'), moment().endOf('day')];
        rangesConf[I18n.daterangepicker_ranges_recent_month] = [moment().subtract(1, 'months').startOf('day'), moment().endOf('day')];

        $('#filterTime').daterangepicker({
            autoApply:false,
            singleDatePicker:false,
            showDropdowns:false,        // 是否显示年月选择条件
            timePicker: true, 			// 是否显示小时和分钟选择条件
            timePickerIncrement: 10, 	// 时间的增量，单位为分钟
            timePicker24Hour : true,
            opens : 'left', //日期选择框的弹出位置
            ranges: rangesConf,
            locale : {
                format: 'YYYY-MM-DD HH:mm:ss',
                separator : ' - ',
                customRangeLabel : I18n.daterangepicker_custom_name ,
                applyLabel : I18n.system_ok ,
                cancelLabel : I18n.system_cancel ,
                fromLabel : I18n.daterangepicker_custom_starttime ,
                toLabel : I18n.daterangepicker_custom_endtime ,
                daysOfWeek : I18n.daterangepicker_custom_daysofweek.split(',') ,        // '日', '一', '二', '三', '四', '五', '六'
                monthNames : I18n.daterangepicker_custom_monthnames.split(',') ,        // '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'
                firstDay : 1
            },
            startDate: rangesConf[I18n.daterangepicker_ranges_today][0],
            endDate: rangesConf[I18n.daterangepicker_ranges_today][1]
        });

        // init date tables
        logTable = $("#joblog_list").dataTable({
            "deferRender": true,
            "processing" : true,
            "serverSide": true,
            "ajax": {
                url: base_url + "/joblog/pageList" ,
                type:"post",
                data : function ( d ) {
                    var obj = {};
                    obj.jobGroup = $('#jobGroup').val();
                    obj.parentId=$('#parentIdParam').val()
                    if(obj.parentId!='0'){
                        $('#jobId').val("0");
                    }
                    obj.jobId = $('#jobId').val();
                    obj.logStatus = $('#logStatus').val();
                    obj.filterTime = $('#filterTime').val();
                    obj.start = d.start;
                    obj.showChild=$('#showChild')[0].checked;
                    console.log($('#showChild')[0].checked);
                    obj.length = d.length;
                    return obj;
                }
            },
            "searching": false,
            "ordering": false,
            //"scrollX": false,
            "columns": [
                {
                    "data": 'jobId',
                    "visible" : true,
                    "width":'7%',
                    "render": function ( data, type, row ) {

                        var glueTypeTitle = GlueTypeEnum[row.glueType];
                        if (row.executorHandler) {
                            glueTypeTitle = glueTypeTitle +"：" + row.executorHandler;
                        }

                        var temp = '';
                        temp += I18n.joblog_field_executorAddress + '：' + (row.executorAddress?row.executorAddress:'');
                        temp += '<br>'+ I18n.jobinfo_field_gluetype +'：' + glueTypeTitle;
                        temp += '<br>'+ I18n.jobinfo_field_executorparam +'：' + row.executorParam;
                        childInfo=''
                        return '<input type="checkbox" name="ids" value="'+row.id+'" >&nbsp;<a class="logTips" href="javascript:;" >'+ row.jobId +'<span style="display:none;">'+ temp +'</span></a>'+childInfo;
                    }
                },
                {
                    "data": 'childSummary',
                    "visible" : true,
                    "width":'10%',
                    "render": function ( data, type, row ) {
                        childInfo=(row.childSummary.length>0?"&nbsp;<a href='javascript:research("+row.id+")'>"+row.childSummary+"</a>":"")
                        return childInfo;
                    }
                },
                { "data": 'jobGroup', "visible" : false},
                {
                    "data": 'triggerTime',
                    "width":'16%',
                    "render": function ( data, type, row ) {
                        return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                    }
                },
                {
                    "data": 'triggerCode',
                    "width":'12%',
                    "render": function ( data, type, row ) {
                        var html = data;
                        if (data == 200) {
                            html = '<span style="color: green">'+ I18n.system_success +'</span>';
                        } else if (data == 500) {
                            html = '<span style="color: red">'+ I18n.system_fail +'</span>';
                        } else if (data == 0) {
                            html = '';
                        }
                        return html;
                    }
                },
                {
                    "data": 'triggerMsg',
                    "width":'12%',
                    "render": function ( data, type, row ) {
                        return data?'<a class="logTips" href="javascript:;" >'+ I18n.system_show +'<span style="display:none;">'+ data +'</span></a>':I18n.system_empty;
                    }
                },
                {
                    "data": 'handleTime',
                    "width":'16%',
                    "render": function ( data, type, row ) {
                        return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
                    }
                },
                {
                    "data": 'handleCode',
                    "width":'12%',
                    "render": function ( data, type, row ) {
                        var html = data;
                        if (data == 200) {
                            html = '<span style="color: green">'+ I18n.joblog_handleCode_200 +'</span>';
                        } else if (data == 500) {
                            html = '<span style="color: red">'+ I18n.joblog_handleCode_500 +'</span>';
                        } else if (data == 501) {
                            html = '<span style="color: red">'+ I18n.joblog_handleCode_501 +'</span>';
                        } else if (data == 433) {
                            html = '<span style="color: red">'+ "部分成功" +'</span>';
                        } if (data == 0) {
                            if(row.triggerCode==200){
                                html = '运行中';
                            }else{
                                html='';
                            }
                        }
                        return html;
                    }
                },
                {
                    "data": 'handleMsg',
                    "width":'12%',
                    "render": function ( data, type, row ) {
                        return data?(data.length<100?data:'<a class="logTips" href="javascript:;" >'+ I18n.system_show +'<span style="display:none;">'+ data +'</span></a>')
                                :I18n.system_empty;
                    }
                },
                {
                    "data": 'handleMsg' ,
                    "bSortable": false,
                    "width":'10%',
                    "render": function ( data, type, row ) {
                        // better support expression or string, not function
                        return function () {
                            if (row.triggerCode == 200){
                                var temp = '<a href="javascript:;" class="logDetail" _id="'+ row.id +'">'+ I18n.joblog_rolling_log +'</a>';
                                if(row.handleCode == 0){
                                    temp += '<br><a href="javascript:;" class="logKill" _id="'+ row.id +'" style="color: red;" >'+ I18n.joblog_kill_log +'</a>';
                                }
                                return temp;
                            }
                            return null;
                        }
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

        // logTips alert
        $('#joblog_list').on('click', '.logTips', function(){
            var msg = $(this).find('span').html();
            ComAlertTec.show(msg);
        });

        // search Btn
        $('#searchBtn').on('click', function(){
            logTable.fnDraw();
        });

        // logDetail look
        $('#joblog_list').on('click', '.logDetail', function(){
            var _id = $(this).attr('_id');

            window.open(base_url + '/joblog/logDetailPage?id=' + _id);
            return;
        });

        $('#allSelect').change(function () {
            var checked=$('#allSelect').is(':checked')
            console.log(checked)
            if(checked){
                $('input[name="ids"]').attr("checked",true);
            }else{
                $('input[name="ids"]').removeAttr("checked");
            }
        });

        /**
         * log Kill
         */
        $('#joblog_list').on('click', '.logKill', function(){
            var _id = $(this).attr('_id');

            layer.confirm( (I18n.system_ok + I18n.joblog_kill_log + '?'), {
                icon: 3,
                title: I18n.system_tips ,
                btn: [ I18n.system_ok, I18n.system_cancel ]
            }, function(index){
                layer.close(index);

                $.ajax({
                    type : 'POST',
                    url : base_url + '/joblog/logKill',
                    data : {"id":_id},
                    dataType : "json",
                    success : function(data){
                        if (data.code == 200) {
                            layer.open({
                                title: I18n.system_tips,
                                btn: [ I18n.system_ok ],
                                content: I18n.system_opt_suc ,
                                icon: '1',
                                end: function(layero, index){
                                    logTable.fnDraw();
                                }
                            });
                        } else {
                            layer.open({
                                title: I18n.system_tips,
                                btn: [ I18n.system_ok ],
                                content: (data.msg || I18n.system_opt_fail ),
                                icon: '2'
                            });
                        }
                    },
                });
            });

        });

        /**
         * clear Log
         */
        $('#clearLog').on('click', function(){

            var jobGroup = $('#jobGroup').val();
            var jobId = $('#jobId').val();

            var jobGroupText = $("#jobGroup").find("option:selected").text();
            var jobIdText = $("#jobId").find("option:selected").text();

            $('#clearLogModal input[name=jobGroup]').val(jobGroup);
            $('#clearLogModal input[name=jobId]').val(jobId);

            $('#clearLogModal .jobGroupText').val(jobGroupText);
            $('#clearLogModal .jobIdText').val(jobIdText);

            $('#clearLogModal').modal('show');

        });

        $('#stopBatch').on('click', function(){

            $.post(base_url + "/joblog/logKillBatch", $("#listForm").serialize(), function(data, status) {
                if (data.code == "200") {
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (I18n.system_success) ,
                        icon: '1',
                        end: function(layero, index){
                            logTable.fnDraw();
                        }
                    });
                } else {
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (data.msg || ( I18n.system_fail) ),
                        icon: '2'
                    });
                }
            });
        });


        $("#clearLogModal .ok").on('click', function(){
            $.post(base_url + "/joblog/clearLog",  $("#clearLogModal .form").serialize(), function(data, status) {
                if (data.code == "200") {
                    $('#clearLogModal').modal('hide');
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (I18n.joblog_clean_log + I18n.system_success) ,
                        icon: '1',
                        end: function(layero, index){
                            logTable.fnDraw();
                        }
                    });
                } else {
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (data.msg || (I18n.joblog_clean_log + I18n.system_fail) ),
                        icon: '2'
                    });
                }
            });
        });
        $("#clearLogModal").on('hide.bs.modal', function () {
            $("#clearLogModal .form")[0].reset();
        });

    });


    // Com Alert by Tec theme
    var ComAlertTec = {
        html:function(){
            var html =
                    '<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
                    '<div class="modal-dialog">' +
                    '<div class="modal-content-tec">' +
                    '<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' +
                    '<div class="modal-footer">' +
                    '<div class="text-center" >' +
                    '<button type="button" class="btn btn-info ok" data-dismiss="modal" >'+ I18n.system_ok +'</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>';
            return html;
        },
        show:function(msg, callback){
            // dom init
            if ($('#ComAlertTec').length == 0){
                $('body').append(ComAlertTec.html());
            }

            // init com alert
            $('#ComAlertTec .alert').html(msg);
            $('#ComAlertTec').modal('show');

            $('#ComAlertTec .ok').click(function(){
                $('#ComAlertTec').modal('hide');
                if(typeof callback == 'function') {
                    callback();
                }
            });
        }
    };

</script>
</body>
</html>
