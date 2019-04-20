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
                obj.jobDesc = $('#jobDesc').val();
	        	obj.executorHandler = $('#executorHandler').val();
	        	obj.start = d.start;
	        	obj.length = d.length;
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
						"width":'10%'
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

                            // status
	                		if (data && data != 'NONE') {
                                if ('NORMAL' == data) {
                                    return '<small class="label label-success" ><i class="fa fa-clock-o"></i>RUNNING</small>';
                                } else {
                                    return '<small class="label label-warning" >ERROR('+ data +')</small>';
                                }
							} else {
                                return '<small class="label label-default" ><i class="fa fa-clock-o"></i>STOP</small>';
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
	                			var start_stop = "";
                                if (row.jobStatus && row.jobStatus != 'NONE') {
                                    if ('NORMAL' == row.jobStatus) {
                                        start_stop = '<button class="btn btn-primary btn-xs job_operate" _type="job_pause" type="button">'+ I18n.jobinfo_opt_stop +'</button>  ';
                                    } else {
                                        start_stop = '<button class="btn btn-primary btn-xs job_operate" _type="job_pause" type="button">'+ I18n.jobinfo_opt_stop +'</button>  ';
                                    }
                                } else {
                                    start_stop = '<button class="btn btn-primary btn-xs job_operate" _type="job_resume" type="button">'+ I18n.jobinfo_opt_start +'</button>  ';
                                }

	                			// log url
	                			var logUrl = base_url +'/joblog?jobId='+ row.id;
	                			
	                			// log url
	                			var codeBtn = "";
                                if ('BEAN' != row.glueType) {
									var codeUrl = base_url +'/jobcode?jobId='+ row.id;
									codeBtn = '<a href="'+ codeUrl +'" target="_blank" > <button class="btn btn-warning btn-xs" type="button" >GLUE</button> </a> '
								}

								// html
                                tableData['key'+row.id] = row;
								var html = '<p id="'+ row.id +'" >'+
									'<button class="btn btn-primary btn-xs job_trigger" type="button">'+ I18n.jobinfo_opt_run +'</button>  '+
                                    start_stop +
									'<a href="'+ logUrl +'"> <button class="btn btn-primary btn-xs" type="job_del" type="button" >'+ I18n.jobinfo_opt_log +'</button> </a> <br>  '+
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
			typeName = I18n.jobinfo_opt_stop ;
			url = base_url + "/jobinfo/stop";
			needFresh = true;
		} else if ("job_resume" == type) {
			typeName = I18n.jobinfo_opt_start ;
			url = base_url + "/jobinfo/start";
			needFresh = true;
		} else if ("job_del" == type) {
			typeName = I18n.system_opt_del ;
			url = base_url + "/jobinfo/remove";
			needFresh = true;
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
                        layer.msg( typeName + I18n.system_success );
                        if (needFresh) {
                            //window.location.reload();
                            jobTable.fnDraw(false);
                        }
					} else {
                        layer.msg( data.msg || typeName + I18n.system_fail );
					}
				}
			});
		});
	});

    // job trigger
    $("#job_list").on('click', '.job_trigger',function() {
        var id = $(this).parent('p').attr("id");
        var row = tableData['key'+id];

        $("#jobTriggerModal .form input[name='id']").val( row.id );
        $("#jobTriggerModal .form textarea[name='executorParam']").val( row.executorParam );

        $('#jobTriggerModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    $("#jobTriggerModal .ok").on('click',function() {
        $.ajax({
            type : 'POST',
            url : base_url + "/jobinfo/trigger",
            data : {
                "id" : $("#jobTriggerModal .form input[name='id']").val(),
                "executorParam" : $("#jobTriggerModal .textarea[name='executorParam']").val()
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    $('#jobTriggerModal').modal('hide');

                    layer.msg( I18n.jobinfo_opt_run + I18n.system_success );
                } else {
                    layer.msg( data.msg || I18n.jobinfo_opt_run + I18n.system_fail );
                }
            }
        });
    });
    $("#jobTriggerModal").on('hide.bs.modal', function () {
        $("#jobTriggerModal .form")[0].reset();
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
			},
            executorTimeout : {
                digits:true
            },
            executorFailRetryCount : {
                digits:true
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
            },
            executorTimeout : {
                digits: I18n.system_please_input + I18n.system_digits
            },
            executorFailRetryCount : {
                digits: I18n.system_please_input + I18n.system_digits
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

			// process
            var executorTimeout = $("#addModal .form input[name='executorTimeout']").val();
            if(!/^\d+$/.test(executorTimeout)) {
                executorTimeout = 0;
			}
            $("#addModal .form input[name='executorTimeout']").val(executorTimeout);
            var executorFailRetryCount = $("#addModal .form input[name='executorFailRetryCount']").val();
            if(!/^\d+$/.test(executorFailRetryCount)) {
                executorFailRetryCount = 0;
            }
            $("#addModal .form input[name='executorFailRetryCount']").val(executorFailRetryCount);


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
		} else if ('GLUE_PHP'==glueType){
            $("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_php").val() );
        } else if ('GLUE_NODEJS'==glueType){
			$("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_nodejs").val() );			
		} else if ('GLUE_POWERSHELL'==glueType){
            $("#addModal .form textarea[name='glueSource']").val( $("#addModal .form .glueSource_powershell").val() );
        } else {
            $("#addModal .form textarea[name='glueSource']").val("");
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
		$("#updateModal .form input[name='executorTimeout']").val( row.executorTimeout );
        $("#updateModal .form input[name='executorFailRetryCount']").val( row.executorFailRetryCount );
		$('#updateModal .form select[name=executorRouteStrategy] option[value='+ row.executorRouteStrategy +']').prop('selected', true);
		$("#updateModal .form input[name='executorHandler']").val( row.executorHandler );
		$("#updateModal .form textarea[name='executorParam']").val( row.executorParam );
        $("#updateModal .form input[name='childJobId']").val( row.childJobId );
		$('#updateModal .form select[name=executorBlockStrategy] option[value='+ row.executorBlockStrategy +']').prop('selected', true);
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
			},
            executorTimeout : {
                digits:true
            },
            executorFailRetryCount : {
                digits:true
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
			},
            executorTimeout : {
                digits: I18n.system_please_input + I18n.system_digits
            },
            executorFailRetryCount : {
                digits: I18n.system_please_input + I18n.system_digits
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

            // process
            var executorTimeout = $("#updateModal .form input[name='executorTimeout']").val();
            if(!/^\d+$/.test(executorTimeout)) {
                executorTimeout = 0;
            }
            $("#updateModal .form input[name='executorTimeout']").val(executorTimeout);
            var executorFailRetryCount = $("#updateModal .form input[name='executorFailRetryCount']").val();
            if(!/^\d+$/.test(executorFailRetryCount)) {
                executorFailRetryCount = 0;
            }
            $("#updateModal .form input[name='executorFailRetryCount']").val(executorFailRetryCount);

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
