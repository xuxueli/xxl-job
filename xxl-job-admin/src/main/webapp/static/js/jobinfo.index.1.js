$(function() {
	// init date tables
	var jobTable = $("#job_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/jobinfo/pageList",
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
	    //"scrollX": true,	// X轴滚动条，取消自适应
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { 
	                	"data": 'jobGroup', 
	                	"visible" : false,
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
	                { "data": 'jobName'},
	                { "data": 'jobDesc', "visible" : true},
	                { "data": 'jobCron', "visible" : true},
	                { "data": 'jobClass', "visible" : false},
	                { "data": 'executorAddress', "visible" : false},
	                { "data": 'executorHandler', "visible" : false},
	                { "data": 'executorParam', "visible" : false},
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
	                { "data": 'author', "visible" : true},
	                { "data": 'alarmEmail', "visible" : false},
	                { "data": 'alarmThreshold', "visible" : false},
	                { "data": 'glueSwitch', "visible" : false},
	                { 
	                	"data": 'jobStatus', 
	                	"visible" : true,
	                	"render": function ( data, type, row ) {
	                		if ('NORMAL' == data) {
	                			return '<small class="label label-success" ><i class="fa fa-clock-o"></i>'+ data +'</small>'; 
							} else if ('PAUSED' == data){
								return '<small class="label label-default" title="暂停" ><i class="fa fa-clock-o"></i>'+ data +'</small>'; 
							} else if ('BLOCKED' == data){
								return '<small class="label label-default" title="阻塞[串行]" ><i class="fa fa-clock-o"></i>'+ data +'</small>'; 
							}
	                		return data;
	                	}
	                },
	                { "data": '操作' ,
	                	"render": function ( data, type, row ) {
	                		return function(){
	                			// status
	                			var pause_resume = "";
	                			if ('NORMAL' == row.jobStatus) {
	                				pause_resume = '<button class="btn btn-primary btn-xs job_operate" type="job_pause" type="button">暂停</button>  ';
								} else if ('PAUSED' == row.jobStatus){
									pause_resume = '<button class="btn btn-primary btn-xs job_operate" type="job_resume" type="button">恢复</button>  ';
								}
	                			// log url
	                			var logUrl = base_url +'/joblog?jobGroup='+ row.jobGroup +'&jobName='+ row.jobName;
	                			
	                			// log url
	                			var codeBtn = "";
	                			if(row.glueSwitch > 0){
	                				var codeUrl = base_url +'/jobcode?jobGroup='+ row.jobGroup +'&jobName='+ row.jobName;
	                				codeBtn = '<button class="btn btn-warning btn-xs" type="button" onclick="javascript:window.open(\'' + codeUrl + '\')" >GLUE</button>  '
	                			}
	                			
	                			var html = '<p id="'+ row.id +'" '+
	                							' jobGroup="'+ row.jobGroup +'" '+
	                							' jobName="'+ row.jobName +'" '+
	                							' jobCron="'+ row.jobCron +'" '+
	                							' jobDesc="'+ row.jobDesc +'" '+
	                							' jobClass="'+ row.jobClass +'" '+
	                							' jobData="'+ row.jobData +'" '+
	                							' executorAddress="'+row.executorAddress +'" '+
	                							' executorHandler="'+ row.executorHandler +'" '+
	                							' executorParam="'+ row.executorParam +'" '+
	                							' author="'+ row.author +'" '+
	                							' alarmEmail="'+ row.alarmEmail +'" '+
	                							' alarmThreshold="'+ row.alarmThreshold +'" '+
	                							' glueSwitch="'+ row.glueSwitch +'" '+
	                							'>'+
										'<button class="btn btn-primary btn-xs job_operate" type="job_trigger" type="button">执行</button>  '+
										pause_resume +
										'<button class="btn btn-primary btn-xs" type="job_del" type="button" onclick="javascript:window.open(\'' + logUrl + '\')" >日志</button><br>  '+
										'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
										codeBtn +
								  		'<button class="btn btn-danger btn-xs job_operate" type="job_del" type="button">删除</button>  '+
									'</p>';
									
	                			
	                			return html;
	                		};
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
	
	// 搜索按钮
	$('#searchBtn').on('click', function(){
		jobTable.fnDraw();
	});
	
	// job operate
	$("#job_list").on('click', '.job_operate',function() {
		var typeName;
		var url;
		var type = $(this).attr("type");
		if ("job_pause" == type) {
			typeName = "暂停";
			url = base_url + "/jobinfo/pause";
		} else if ("job_resume" == type) {
			typeName = "恢复";
			url = base_url + "/jobinfo/resume";
		} else if ("job_del" == type) {
			typeName = "删除";
			url = base_url + "/jobinfo/remove";
		} else if ("job_trigger" == type) {
			typeName = "执行";
			url = base_url + "/jobinfo/trigger";
		} else {
			return;
		}
		
		var jobGroup = $(this).parent('p').attr("jobGroup");
		var jobName = $(this).parent('p').attr("jobName");
		
		ComConfirm.show("确认" + typeName + "?", function(){
			$.ajax({
				type : 'POST',
				url : url,
				data : {
					"jobGroup" : jobGroup,
					"jobName"  : jobName
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, typeName + "成功", function(){
							//window.location.reload();
							jobTable.fnDraw();
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
        	jobName : {  
        		required : true ,
                minlength: 4,
                maxlength: 100,
                myValid01:true
            },  
            jobCron : {  
            	required : true ,
                maxlength: 100
            },  
            jobDesc : {  
            	required : true ,
                maxlength: 200
            },
            executorAddress : {
            	required : true ,
                maxlength: 200
            },
            executorHandler : {
            	required : true ,
                maxlength: 200
            },
            author : {
            	required : true ,
                maxlength: 200
            },
            alarmEmail : {
            	required : true ,
                maxlength: 200
            },
            alarmThreshold : {
            	required : true ,
            	digits:true
            }
        }, 
        messages : {  
        	jobName : {  
        		required :"请输入“任务名”"  ,
                minlength:"“任务名”长度不应低于4位",
                maxlength:"“任务名”长度不应超过100位"
            },  
            jobCron : {
            	required :"请输入“Cron”."  ,
                maxlength:"“Cron”长度不应超过100位"
            },  
            jobDesc : {
            	required :"请输入“任务描述”."  ,
                maxlength:"“任务描述”长度不应超过200位"
            },  
            executorAddress : {
            	required :"请输入“执行器地址”."  ,
                maxlength:"“执行器地址”长度不应超过200位"
            },
            executorHandler : {
            	required : "请输入“jobHandler”."  ,
                maxlength: "“jobHandler”长度不应超过200位"
            },
            author : {
            	required : "请输入“负责人”."  ,
                maxlength: "“负责人”长度不应超过50位"
            },
            alarmEmail : {
            	required : "请输入“报警邮件”."  ,
                maxlength: "“报警邮件”长度不应超过200位"
            },
            alarmThreshold : {
            	required : "请输入“报警阈值”."  ,
            	digits:"阀值应该为整数."
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
    				ComAlert.show(1, "新增任务成功", function(){
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
		$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote
	});
	
	// GLUE模式开启
	$("#addModal .form .ifGLUE").click(function(){
		var ifGLUE = $(this).is(':checked');
		var $executorHandler = $("#addModal .form input[name='executorHandler']");
		var $glueSwitch = $("#addModal .form input[name='glueSwitch']");
		if (ifGLUE) {
			$executorHandler.val("");
			$executorHandler.attr("readonly","readonly");
			$glueSwitch.val(1);
		} else {
			$executorHandler.removeAttr("readonly");
			$glueSwitch.val(0);
		}
	});
	$("#updateModal .form .ifGLUE").click(function(){
		var ifGLUE = $(this).is(':checked');
		var $executorHandler = $("#updateModal .form input[name='executorHandler']");
		var $glueSwitch = $("#updateModal .form input[name='glueSwitch']");
		if (ifGLUE) {
			$executorHandler.val("");
			$executorHandler.attr("readonly","readonly");
			$glueSwitch.val(1);
		} else {
			$executorHandler.removeAttr("readonly");
			$glueSwitch.val(0);
		}
	});
	
	// 更新
	$("#job_list").on('click', '.update',function() {
		$("#updateModal .form input[name='jobGroup']").val($(this).parent('p').attr("jobGroup"));
		$("#updateModal .form input[name='jobName']").val($(this).parent('p').attr("jobName"));
		$("#updateModal .form input[name='jobCron']").val($(this).parent('p').attr("jobCron"));
		$("#updateModal .form input[name='jobDesc']").val($(this).parent('p').attr("jobDesc"));
		$("#updateModal .form input[name='executorAddress']").val($(this).parent('p').attr("executorAddress"));
		$("#updateModal .form input[name='executorHandler']").val($(this).parent('p').attr("executorHandler"));
		$("#updateModal .form input[name='executorParam']").val($(this).parent('p').attr("executorParam"));
		$("#updateModal .form input[name='author']").val($(this).parent('p').attr("author"));
		$("#updateModal .form input[name='alarmEmail']").val($(this).parent('p').attr("alarmEmail"));
		$("#updateModal .form input[name='alarmThreshold']").val($(this).parent('p').attr("alarmThreshold"));
		$("#updateModal .form input[name='glueSwitch']").val($(this).parent('p').attr("glueSwitch"));
		
		// GLUE check
		var $glueSwitch = $("#updateModal .form input[name='glueSwitch']");
		var $executorHandler = $("#updateModal .form input[name='executorHandler']");
		if ($glueSwitch.val() != 0) {
			$executorHandler.attr("readonly","readonly");
			$("#updateModal .form .ifGLUE").attr("checked", true);
		} else {
			$executorHandler.removeAttr("readonly");
			$("#updateModal .form .ifGLUE").attr("checked", false);
		}
		
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
            jobCron : {  
            	required : true ,
                maxlength: 100
            },  
            jobDesc : {  
            	required : true ,
                maxlength: 200
            },
            executorAddress : {
            	required : true ,
                maxlength: 200
            },
            executorHandler : {
            	required : true ,
                maxlength: 200
            },
            author : {
            	required : true ,
                maxlength: 200
            },
            alarmEmail : {
            	required : true ,
                maxlength: 200
            },
            alarmThreshold : {
            	required : true ,
            	digits:true
            }
        }, 
        messages : {  
            jobCron : {
            	required :"请输入“Cron”."  ,
                maxlength:"“Cron”长度不应超过100位"
            },  
            jobDesc : {
            	required :"请输入“任务描述”."  ,
                maxlength:"“任务描述”长度不应超过200位"
            },  
            executorAddress : {
            	required :"请输入“执行器地址”."  ,
                maxlength:"“执行器地址”长度不应超过200位"
            },
            executorHandler : {
            	required : "请输入“jobHandler”."  ,
                maxlength: "“jobHandler”长度不应超过200位"
            },
            author : {
            	required : "请输入“负责人”."  ,
                maxlength: "“负责人”长度不应超过50位"
            },
            alarmEmail : {
            	required : "请输入“报警邮件”."  ,
                maxlength: "“报警邮件”长度不应超过200位"
            },
            alarmThreshold : {
            	required : "请输入“报警阈值”."  ,
            	digits:"阀值应该为整数."
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
    		$.post(base_url + "/jobinfo/reschedule", $("#updateModal .form").serialize(), function(data, status) {
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
	
	
	/*
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
	*/
});
