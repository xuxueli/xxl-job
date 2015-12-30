$(function() {

	// init date tables
	var logTable = $("#joblog_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
	        url: base_url + "/joblog/pageList" ,
	        data : function ( d ) {
                d.filterTime = $('#filterTime').val();
                d.jobName = $('#jobName').val()
            }
	    },
	    "scrollX": true,
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { "data": 'jobName', "bSortable": false},
	                { "data": 'jobCron', "bSortable": false, "visible" : false},
	                { "data": 'jobClass', "bSortable": false, "visible" : false},
	                { "data": 'jobData', "bSortable": false, "visible" : false},
	                { 
	                	"data": 'triggerTime', 
	                	"bSortable": false, 
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'triggerStatus', "bSortable": false},
	                { "data": 'triggerMsg',"bSortable": false},
	                { 
	                	"data": 'handleTime',
	                	"bSortable": false,
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'handleStatus',"bSortable": false},
	                { "data": 'handleMsg' , "bSortable": false}
	            ],
	    "searching": false,
	    "ordering": true,
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
        opens : 'right', //日期选择框的弹出位置
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
	
	// 搜索按钮
	$('#searchBtn').on('click', function(){
		logTable.fnDraw();
	});
	
});
