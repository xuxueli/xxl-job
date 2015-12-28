$(function() {
	// init date tables
	$("#joblog_list").dataTable({
		"serverSide": true,
		"ajax": {
	        url: base_url + "/joblog/pageList"
	    },
	    "processing" : true, 
	    "deferRender": true,
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : true},
	                { "data": 'jobName', "bSortable": false},
	                { "data": 'jobCron', "bSortable": false},
	                { "data": 'jobClass', "bSortable": false},
	                { "data": 'handleTime',"bSortable": false},
	                { "data": 'handleStatus' , "bSortable": false}
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
	
});
