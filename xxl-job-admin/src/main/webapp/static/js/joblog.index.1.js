$(function() {

	// 过滤时间
	$('#filterTime').daterangepicker({
		timePicker: true, 			//是否显示小时和分钟
		timePickerIncrement: 10, 	//时间的增量，单位为分钟
		timePicker12Hour : false,	//是否使用12小时制来显示时间
		format: 'YYYY-MM-DD HH:mm:ss',
		separator : ' - ',
		ranges : {
            '最近1小时': [moment().subtract('hours',1), moment()],
            '今日': [moment().startOf('day'), moment()],
            '昨日': [moment().subtract('days', 1).startOf('day'), moment().subtract('days', 1).endOf('day')],
            '最近7日': [moment().subtract('days', 6), moment()],
            '最近30日': [moment().subtract('days', 29), moment()]
        },
        opens : 'left', //日期选择框的弹出位置
        locale : {
        	customRangeLabel : '自定义',
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            toLabel : '结束时间',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1
        }
	});
	$('#filterTime').val( moment(new Date()).format("YYYY-MM-DD 00:00:00") + ' - ' + moment(new Date()).add(1, 'days').format("YYYY-MM-DD 00:00:00") );	// YYYY-MM-DD HH:mm:ss
	
	// init date tables
	var logTable = $("#joblog_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
	        url: base_url + "/joblog/pageList" ,
	        data : function ( d ) {
	        	var obj = {};
	        	obj.jobGroup = $('#jobGroup').val();
	        	obj.jobName = $('#jobName').val();
	        	obj.start = d.start;
	        	obj.length = d.length;
                return obj;
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": false,
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { 
	                	"data": 'jobGroup', 
	                	"visible" : false, 
	                	"bSortable": false, 
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
	                { "data": 'jobName', "visible" : false},
	                { "data": 'jobCron', "visible" : false},
	                { "data": 'jobDesc', "visible" : false},
	                { "data": 'jobClass', "visible" : false},
	                
	                { "data": 'executorAddress', "visible" : true},
	                { "data": 'executorHandler', "visible" : true},
	                { "data": 'executorParam', "visible" : true},
	                { 
	                	"data": 'triggerTime', 
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'triggerStatus'},
	                { 
	                	"data": 'triggerMsg',
	                	"render": function ( data, type, row ) {
	                		return data?'<a class="logTips" href="javascript:;" >查看<span style="display:none;">'+ data +'</span></a>':"无";
	                	}
	                },
	                { 
	                	"data": 'handleTime',
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'handleStatus',"bSortable": false},
	                { 
	                	"data": 'handleMsg',
	                	"render": function ( data, type, row ) {
	                		return data?'<a class="logTips" href="javascript:;" >查看<span style="display:none;">'+ data +'</span></a>':"无";
	                	}
	                },
	                { "data": 'handleMsg' , "bSortable": false,
	                	"render": function ( data, type, row ) {
	                		// better support expression or string, not function
	                		return function () {
		                		if (row.triggerStatus == 'SUCCESS'){
		                			var temp = '<a href="javascript:;" class="logDetail" _id="'+ row.id +'">执行日志</a>';
		                			if(!row.handleStatus){
		                				temp += '<br><a href="javascript:;" class="logKill" _id="'+ row.id +'">终止任务</a>';
		                			}
		                			return temp;
		                		}
		                		return null;	
	                		}
	                	}
	                }
	            ],
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	
	// 任务数据
	$('#joblog_list').on('click', '.logMsg', function(){
		var msg = $(this).find('span').html();
		ComAlert.show(2, msg);
	});
	
	// 日志弹框提示
	$('#joblog_list').on('click', '.logTips', function(){
		var msg = $(this).find('span').html();
		ComAlertTec.show(msg);
	});
	
	// 搜索按钮
	$('#searchBtn').on('click', function(){
		logTable.fnDraw();
	});
	
	// 查看执行器详细执行日志
	$('#joblog_list').on('click', '.logDetail', function(){
		var _id = $(this).attr('_id');
		
		window.open(base_url + '/joblog/logDetailPage?id=' + _id);
		return;
		
		/*
		$.ajax({
			type : 'POST',
			url : base_url + '/joblog/logDetail',
			data : {"id":_id},
			dataType : "json",
			success : function(data){
				if (data.code == 200) {
					ComAlertTec.show('<pre style="color: white;background-color: black;width2:'+ $(window).width()*2/3 +'px;" >'+ data.content +'</pre>');
				} else {
					ComAlertTec.show(data.msg);
				}
			},
		});
		*/
	});
	
	$('#joblog_list').on('click', '.logKill', function(){
		var _id = $(this).attr('_id');
		ComConfirm.show("确认主动终止任务?", function(){
			$.ajax({
				type : 'POST',
				url : base_url + '/joblog/logKill',
				data : {"id":_id},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, '操作成功');
						logTable.fnDraw();
					} else {
						ComAlert.show(2, data.msg);
					}
				},
			});
		});
	});
	
});
