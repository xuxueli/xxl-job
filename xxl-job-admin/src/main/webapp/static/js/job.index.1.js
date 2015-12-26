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
		} else if ("job_trigger" == type) {
			typeName = "执行一次";
			url = base_url + "/job/trigger";
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
	
	// jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
	jQuery.validator.addMethod("myValid01", function(value, element) {
		var length = value.length;
		var valid = /^[a-zA-Z][a-zA-Z0-9_]*$/;
		return this.optional(element) || valid.test(value);
	}, "只支持英文字母开头，只含有英文字母、数字和下划线");
	
	// 新增
	$(".add").click(function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	triggerKeyName : {  
        		required : true ,
                minlength: 4,
                maxlength: 100,
                myValid01:true
            },  
            cronExpression : {  
            	required : true ,
                maxlength: 100
            },  
            job_desc : {  
            	required : true ,
                maxlength: 200
            },
            job_url : {
            	required : true ,
                maxlength: 200
            },
            handleName : {
            	required : true ,
                maxlength: 200
            }
        }, 
        messages : {  
        	triggerKeyName : {  
        		required :"请输入“任务Key”."  ,
                minlength:"“任务Key”长度不应低于4位",
                maxlength:"“任务Key”长度不应超过100位"
            },  
            cronExpression : {
            	required :"请输入“任务Corn”."  ,
                maxlength:"“任务Corn”长度不应超过100位"
            },  
            job_desc : {
            	required :"请输入“任务描述”."  ,
                maxlength:"“任务描述”长度不应超过200位"
            },  
            job_url : {
            	required :"请输入“任务URL”."  ,
                maxlength:"“任务URL”长度不应超过200位"
            },
            handleName : {
            	required : "请输入“任务handler”."  ,
                maxlength: "“任务handler”长度不应超过200位"
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
        	
        	var triggerKeyName = $('#addModal input[name="triggerKeyName"]').val();
        	var cronExpression = $('#addModal input[name="cronExpression"]').val();
        	var job_desc = $('#addModal input[name="job_desc"]').val();
        	var job_url = $('#addModal input[name="job_url"]').val();
        	var handleName = $('#addModal input[name="handleName"]').val();
        	
        	var paramStr = 'triggerKeyName=' + triggerKeyName + 
        		'&cronExpression=' + cronExpression + 
        		'&job_desc=' + job_desc +
        		'&job_url=' + job_url +
        		'&handleName=' + handleName;
        	
        	var ifFin = true;
        	$('#addModal .newParam').each(function(){
        		ifFin = false;
        		var key = $(this).find('input[name="key"]').val();
        		var value = $(this).find('input[name="value"]').val();
        		if (!key) {
        			ComAlert.show(2, "新增参数key不可为空");
        			return;
				} else {
					if(!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(key)){
						ComAlert.show(2, "新增参数key不合法, 只支持英文字母开头，只含有英文字母、数字和下划线");
	        			return;
					}
				}
        		paramStr += "&" + key + "=" + value;
        		ifFin = true;
        	});
        	
        	if(ifFin){
        		$.post(base_url + "/job/add", paramStr, function(data, status) {
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
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset()
	});
	
	// 新增-添加参数
	$("#addModal .addParam").on('click', function () {
		var html = '<div class="form-group newParam">'+
				'<label for="lastname" class="col-sm-2 control-label">参数&nbsp;<button class="btn btn-danger btn-xs removeParam" type="button">移除</button></label>'+
				'<div class="col-sm-4"><input type="text" class="form-control" name="key" placeholder="请输入参数key[将会强转为String]" maxlength="200" /></div>'+
				'<div class="col-sm-6"><input type="text" class="form-control" name="value" placeholder="请输入参数value[将会强转为String]" maxlength="200" /></div>'+
			'</div>';
		$(this).parents('.form-group').parent().append(html);
		
		$("#addModal .removeParam").on('click', function () {
			$(this).parents('.form-group').remove();
		});
	});
	
	// 更新
	$(".update").click(function(){
		$("#updateModal .form input[name='triggerKeyName']").val($(this).parent('p').attr("name"));
		$("#updateModal .form input[name='cronExpression']").val($(this).parent('p').attr("cronExpression"));
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
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
