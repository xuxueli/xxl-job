$(function() {
	// init date tables
	var jobTable = $("#job_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/jobinfo/pageList",
	        data : function ( d ) {
	        	d.jobGroup = $('#jobGroup').val();
                d.jobName = $('#jobName').val();
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,	// X轴滚动条，取消自适应
	    "columns": [
	                { "data": 'id', "bSortable": false, "visible" : false},
	                { 
	                	"data": 'jobGroup', 
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
	                { "data": 'jobCron', "visible" : true},
	                { "data": 'jobDesc', "visible" : false},
	                { "data": 'jobClass', "visible" : true},
	                { 
	                	"data": 'jobData',
	                	"visible" : true,
	                	"render": function ( data, type, row ) {
	                		return data?'<a class="logTips" href="javascript:;" >查看<span style="display:none;">'+ data +'</span></a>':"无";
	                	}
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
	                { "data": 'author', "visible" : true},
	                { "data": 'alarmEmail', "visible" : false},
	                { "data": 'alarmThreshold', "visible" : false},
	                { 
	                	"data": 'jobStatus', 
	                	"visible" : true,
	                	"render": function ( data, type, row ) {
	                		if ('NORMAL' == data) {
	                			return '<small class="label label-success" ><i class="fa fa-clock-o"></i>'+ data +'</small>'; 
							} else if ('PAUSED' == data){
								return '<small class="label label-default"><i class="fa fa-clock-o"></i>'+ data +'</small>'; 
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
	                				pause_resume = '<button class="btn btn-info btn-xs job_operate" type="job_pause" type="button">暂停</button>  ';
								} else if ('PAUSED' == row.jobStatus){
									pause_resume = '<button class="btn btn-info btn-xs job_operate" type="job_resume" type="button">恢复</button>  ';
								}
	                			// log url
	                			var logUrl = base_url +'/joblog?jobGroup='+ row.jobGroup +'&jobName='+ row.jobName;
	                			
	                			// job data
	                			var jobDataMap = eval('(' + row.jobData + ')');
	                			
	                			var html = '<p id="'+ row.id +'" '+
	                							' jobGroup="'+ row.jobGroup +'" '+
	                							' jobName="'+ row.jobName +'" '+
	                							' jobCron="'+ row.jobCron +'" '+
	                							' jobDesc="'+ row.jobDesc +'" '+
	                							' jobClass="'+ row.jobClass +'" '+
	                							' jobData="'+ row.jobData +'" '+
	                							' author="'+ row.author +'" '+
	                							' alarmEmail="'+ row.alarmEmail +'" '+
	                							' alarmThreshold="'+ row.alarmThreshold +'" '+
	                							' handler_params="'+jobDataMap.handler_params +'" '+
	                							' handler_address="'+ jobDataMap.handler_address +'" '+
	                							' handler_name="'+ jobDataMap.handler_name +'" '+
	                							'>'+
	                					pause_resume +
										'<button class="btn btn-info btn-xs job_operate" type="job_trigger" type="button">执行</button>  '+
										'<button class="btn btn-warning btn-xs update" type="button">编辑</button><br> '+
									  	'<button class="btn btn-warning btn-xs" type="job_del" type="button" '+
									  		'onclick="javascript:window.open(\'' + logUrl + '\')" >查看日志</button> '+
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
	
	// 日志弹框提示
	$('#job_list').on('click', '.logTips', function(){
		var msg = $(this).find('span').html();
		ComAlertTec.show(msg);
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
            handler_address : {
            	required : true ,
                maxlength: 200
            },
            handler_name : {
            	required : true ,
                maxlength: 200
            },
            author : {
            	required : true ,
                maxlength: 200
            },
            alarm_email : {
            	required : true ,
                maxlength: 200
            },
            alarm_threshold : {
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
            	required :"请输入“Corn”."  ,
                maxlength:"“Corn”长度不应超过100位"
            },  
            jobDesc : {
            	required :"请输入“任务描述”."  ,
                maxlength:"“任务描述”长度不应超过200位"
            },  
            handler_address : {
            	required :"请输入“远程-机器地址”."  ,
                maxlength:"“远程-机器地址”长度不应超过200位"
            },
            handler_name : {
            	required : "请输入“远程-执行器”."  ,
                maxlength: "“远程-执行器”长度不应超过200位"
            },
            author : {
            	required : "请输入“负责人”."  ,
                maxlength: "“负责人”长度不应超过50位"
            },
            alarm_email : {
            	required : "请输入“报警邮件”."  ,
                maxlength: "“报警邮件”长度不应超过200位"
            },
            alarm_threshold : {
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
    				ComAlert.show(1, "新增调度任务成功", function(){
    					ComAlert.show(1, "新增任务成功");
    					jobTable.fnDraw();
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
		//$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
	});
	
	// 远程任务/本地任务，切换
	$("#addModal select[name='jobClass']").change(function() {
		//console.log($(this).val());
		console.log( $(this).val().indexOf('HttpJobBean') );
		if($(this).val().indexOf('HttpJobBean') > -1){
			$(".remote_panel").show();	// remote
		} else if($(this).val().indexOf('HttpJobBean') == -1){
			$(".remote_panel").hide();	// local
		}
    });
	
	// 更新
	$("#job_list").on('click', '.update',function() {
		$("#updateModal .form input[name='triggerKeyName']").val($(this).parent('p').attr("jobName"));
		$("#updateModal .form input[name='cronExpression']").val($(this).parent('p').attr("cronExpression"));
		$("#updateModal .form input[name='job_desc']").val($(this).parent('p').attr("job_desc"));
		$("#updateModal .form input[name='job_url']").val($(this).parent('p').attr("job_url"));
		$("#updateModal .form input[name='handleName']").val($(this).parent('p').attr("handleName"));
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
                minlength:"“任务Key”不应低于4位",
                maxlength:"“任务Key”不应超过100位"
            },  
            cronExpression : {
            	required :"请输入“任务Corn”."  ,
                maxlength:"“任务Corn”不应超过100位"
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
