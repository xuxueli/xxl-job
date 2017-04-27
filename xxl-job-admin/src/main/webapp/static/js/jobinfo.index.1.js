$(function() {
	// init date tables
	var jobTable = $("#job_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/jobinfo/pageList",
			type:"post",
	        data : function ( d ) {
	        	var obj = {};
	        	obj.jobGroup = $('#jobGroup').val();
	        	obj.executorHandler = $('#executorHandler').val();
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
					{
						"data": 'childJobKey',
						"width":'10%',
						"visible" : true,
						"render": function ( data, type, row ) {
							var jobKey = row.jobGroup + "_" + row.id;
							return jobKey;
						}
					},
	                { "data": 'jobDesc', "visible" : true,"width":'20%'},
					{
						"data": 'glueType',
						"width":'20%',
						"visible" : true,
						"render": function ( data, type, row ) {
							if ('GLUE_GROOVY'==row.glueType) {
								return "GLUE模式(Java)";
							} else if ('GLUE_SHELL'==row.glueType) {
								return "GLUE模式(Shell)";
							} else if ('GLUE_PYTHON'==row.glueType) {
								return "GLUE模式(Python)";
							} else if ('BEAN'==row.glueType) {
								return "BEAN模式：" + row.executorHandler;
							}
							return row.executorHandler;
						}
					},
	                { "data": 'executorParam', "visible" : false},
					{ "data": 'jobCron', "visible" : true,"width":'10%'},
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
	                { "data": 'glueType', "visible" : false},
	                { 
	                	"data": 'jobStatus',
						"width":'10%',
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
	                {
						"data": '操作' ,
						"width":'15%',
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
	                			var logUrl = base_url +'/joblog?jobId='+ row.id;
	                			
	                			// log url
	                			var codeBtn = "";
                                if ('BEAN' != row.glueType) {
									var codeUrl = base_url +'/jobcode?jobId='+ row.id;
									codeBtn = '<button class="btn btn-warning btn-xs" type="button" onclick="javascript:window.open(\'' + codeUrl + '\')" >GLUE</button>  '
								}

								// html
								var html = '<p id="'+ row.id +'" '+
									' jobGroup="'+ row.jobGroup +'" '+
									' jobCron="'+ row.jobCron +'" '+
									' jobDesc="'+ row.jobDesc +'" '+
									' author="'+ row.author +'" '+
									' alarmEmail="'+ row.alarmEmail +'" '+
									' executorRouteStrategy="'+row.executorRouteStrategy +'" '+
									' executorHandler="'+row.executorHandler +'" '+
									' executorParam="'+ row.executorParam +'" '+
									' glueType="'+ row.glueType +'" '+
                                    ' childJobKey="'+ row.childJobKey +'" '+
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
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
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
		var needFresh = false;

		var type = $(this).attr("type");
		if ("job_pause" == type) {
			typeName = "暂停";
			url = base_url + "/jobinfo/pause";
			needFresh = true;
		} else if ("job_resume" == type) {
			typeName = "恢复";
			url = base_url + "/jobinfo/resume";
			needFresh = true;
		} else if ("job_del" == type) {
			typeName = "删除";
			url = base_url + "/jobinfo/remove";
			needFresh = true;
		} else if ("job_trigger" == type) {
			typeName = "执行";
			url = base_url + "/jobinfo/trigger";
		} else {
			return;
		}
		
		var id = $(this).parent('p').attr("id");
		
		ComConfirm.show("确认" + typeName + "?", function(){
			$.ajax({
				type : 'POST',
				url : url,
				data : {
					"id" : id
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, typeName + "成功", function(){
							if (needFresh) {
								//window.location.reload();
								jobTable.fnDraw();
							}
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
            	required :"请输入“描述”."
            },
            jobCron : {
            	required :"请输入“Cron”."
            },
            author : {
            	required : "请输入“负责人”."
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
					setTimeout(function () {
						ComAlert.show(1, "新增任务成功", function(){
							jobTable.fnDraw();
							//window.location.reload();
						});
					}, 315);
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

		$("#addModal .form input[name='executorHandler']").removeAttr("readonly");
	});


    // 运行模式
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
		}
	});

	// 更新
	$("#job_list").on('click', '.update',function() {

		// base data
		$("#updateModal .form input[name='id']").val($(this).parent('p').attr("id"));
		$('#updateModal .form select[name=jobGroup] option[value='+ $(this).parent('p').attr("jobGroup") +']').prop('selected', true);
		$("#updateModal .form input[name='jobDesc']").val($(this).parent('p').attr("jobDesc"));
		$("#updateModal .form input[name='jobCron']").val($(this).parent('p').attr("jobCron"));
		$("#updateModal .form input[name='author']").val($(this).parent('p').attr("author"));
		$("#updateModal .form input[name='alarmEmail']").val($(this).parent('p').attr("alarmEmail"));
		$('#updateModal .form select[name=executorRouteStrategy] option[value='+ $(this).parent('p').attr("executorRouteStrategy") +']').prop('selected', true);
		$("#updateModal .form input[name='executorHandler']").val($(this).parent('p').attr("executorHandler"));
		$("#updateModal .form input[name='executorParam']").val($(this).parent('p').attr("executorParam"));
        $("#updateModal .form input[name='childJobKey']").val($(this).parent('p').attr("childJobKey"));
		$('#updateModal .form select[name=glueType] option[value='+ $(this).parent('p').attr("glueType") +']').prop('selected', true);


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
				required :"请输入“描述”."
			},
			jobCron : {
				required :"请输入“Cron”."
			},
			author : {
				required : "请输入“负责人”."
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
    		$.post(base_url + "/jobinfo/reschedule", $("#updateModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
					$('#updateModal').modal('hide');
					setTimeout(function () {
						ComAlert.show(1, "更新成功", function(){
							//window.location.reload();
							jobTable.fnDraw();
						});
					}, 315);
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
