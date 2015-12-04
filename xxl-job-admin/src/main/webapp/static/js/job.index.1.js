$(function() {
	// init date tables
	$("#job_list").DataTable({
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
	
	// job operate
	$(".job_operate").click(function() {
		var typeName;
		var url;
		var type = $(this).attr("type");
		if ("job_pause" == type) {
			typeName = "暂停";
			url = base_url + "/job/pause";
		} else if ("job_resume" == type) {
			typeName = "恢复";
			url = base_url + "/job/resume";
		} else if ("job_del" == type) {
			typeName = "删除";
			url = base_url + "/job/remove";
		} else {
			return;
		}
		
		var name = $(this).parent('p').attr("name");
		var group = $(this).parent('p').attr("group");
		
		ComConfirm.show("确认" + typeName + "?", function(){
			$.ajax({
				type : 'POST',
				url : url,
				data : {
					"triggerKeyName" :	name,
					"group"			 :	group
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, typeName + "成功", function(){
							window.location.reload();
						});
					} else {
						ComAlert.show(1, typeName + "失败");
					}
				},
			});
		});
	});
	
	// 新增
	$(".add").click(function(){
		$('#addModal').modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	triggerKeyName : {  
        		required : true ,
                minlength: 4,
                maxlength: 100
            },  
            cronExpression : {  
            	required : true ,
                maxlength: 100
            },  
            jobClassName : {  
            	required : true ,
                maxlength: 100
            },  
            jobDesc : {  
            	required : true ,
                maxlength: 200
            }
        }, 
        messages : {  
        	triggerKeyName : {  
        		required :"请输入“任务Key”."  ,
                minlength:"“任务Key”不应低于4位",
                maxlength:"“任务Key”不应超过100位"
            },  
            cronExpression : {
            	required :"请输入“任务Corn”."  ,
                maxlength:"“任务Corn”不应超过100位"
            },  
            jobClassName : {
            	required :"请输入“任务Impl”."  ,
                maxlength:"“任务Impl”不应超过100位"
            },  
            jobDesc : {
            	required :"请输入“任务描述”."  ,
                maxlength:"“任务描述”不应超过200位"
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
    		$.post(base_url + "/job/add", $("#addModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				ComAlert.show(1, "新增调度任务成功", function(){
    					window.location.reload();
    				});
    			} else {
    				if (data.msg) {
    					ComAlert.show(2, data.msg);
					} else {
						ComAlert.show(2, "新增失败");
					}
    			}
    		});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset()
	});
	
	// 更新
	$(".update").click(function(){
		$("#updateModal .form input[name='triggerKeyName']").val($(this).parent('p').attr("name"));
		$("#updateModal .form input[name='cronExpression']").val($(this).parent('p').attr("cronExpression"));
		$("#updateModal .form input[name='jobClassName']").val($(this).parent('p').attr("jobClassName"));
		$("#updateModal .form input[name='jobDesc']").val($(this).parent('p').attr("jobDesc"));
		$('#updateModal').modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	triggerKeyName : {  
        		required : true ,
                minlength: 4,
                maxlength: 100
            },  
            cronExpression : {  
            	required : true ,
                maxlength: 100
            },  
            jobClassName : {  
            	required : true ,
                maxlength: 100
            },  
            jobDesc : {  
            	required : true ,
                maxlength: 200
            }
        }, 
        messages : {  
        	triggerKeyName : {  
        		required :"请输入“任务Key”."  ,
                minlength:"“任务Key”不应低于4位",
                maxlength:"“任务Key”不应超过100位"
            },  
            cronExpression : {
            	required :"请输入“任务Corn”."  ,
                maxlength:"“任务Corn”不应超过100位"
            },  
            jobClassName : {
            	required :"请输入“任务Impl”."  ,
                maxlength:"“任务Impl”不应超过100位"
            },  
            jobDesc : {
            	required :"请输入“任务描述”."  ,
                maxlength:"“任务描述”不应超过200位"
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
    		$.post(base_url + "/job/reschedule", $("#updateModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				ComAlert.show(1, "更新成功", function(){
    					window.location.reload();
    				});
    			} else {
    				if (data.msg) {
    					ComAlert.show(2, data.msg);
					} else {
						ComAlert.show(2, "更新失败");
					}
    			}
    		});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset()
	});
	
});
