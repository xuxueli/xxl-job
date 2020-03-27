$(function() {

	// init date tables
	var jobTable = $("#alarm_list").dataTable({
		"deferRender": true,
		"processing" : true,
	    "serverSide": true,
		"ajax": {
			url: base_url + "/alarminfo/pageList",
			type:"post",
	        data : function (d) {
	        	var obj = {};
                obj.alarmEnum = $('#jobAlarmerEnum').val();
	        	obj.alarmDesc = $('#alarmDesc').val();
                obj.alarmName = $('#alarmName').val();
	        	obj.start = d.start;
	        	obj.length = d.length;
                return obj;
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,	// scroll x，close self-adaption
	    "columns": [
	                {"data": 'id', "bSortable": false, "visible" : true, "width":'4%'},
	                {"data": 'alarmName', "visible" : true, "width":'15%'},
	                {"data": 'alarmType', "visible" : true, "width":'7%',"render":function(data, type, row){
	                	return findAlarmTypeTitle(data);
	                }},
	                {"data": 'alarmParam', "visible" : true, "render":function(data, type, row){
	                	if(data && data.length > 100){
	                		return data.substr(0, 100) + "...";
	                	}
	                	return data;
	                }},
					{"data": 'alarmDesc', "visible" : true, "width":'20%'},
	                {"data": 'createTime', "visible" : true, "render": function(data, type, row) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}},
	                {"data": 'updateTime', "visible" : true, "render": function (data, type, row) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}},
	                {"data": I18n.system_opt, "width":'10%', "render": function (data, type, row) {
	                		return function(){
	                			// data
                                tableData['key'+row.id] = row;
                                // opt
                                var html = '<div class="btn-group" _id="'+ row.id +'">\n' +
                                    '     <button type="button" class="btn btn-primary btn-sm update">'+ I18n.system_opt_edit +'</button>\n' +
                                    '     <button type="button" class="btn btn-primary btn-sm alarm_del">'+ I18n.system_opt_del +'</button>\n' +
                                    '   </div>';
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

	// add
	$(".add").click(function(){
		$("#alarminfoModal .form input[id='editType']").val("add");
		$("#alarminfoModal .modal-title").text(I18n.alarminfo_field_add);
		$("#alarminfoModal .form input[name='id']").attr("disabled",true);
		$('#alarminfoModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	
	// update
	$("#alarm_list").on('click', '.update',function() {
		$("#alarminfoModal .form input[id='editType']").val("update");
		$("#alarminfoModal .modal-title").html(I18n.alarminfo_field_update);
		$("#alarminfoModal .form input[name='id']").removeAttr("disabled");
        var id = $(this).parents('div').attr("_id");
        var row = tableData['key'+id];

		// base data
		$("#alarminfoModal .form input[name='id']").val(row.id);
		$('#alarminfoModal .form select[name=alarmType] option[value='+ row.alarmType +']').prop('selected', true);
		$("#alarminfoModal .form input[name='alarmName']").val(row.alarmName);
		$("#alarminfoModal .form textarea[name='alarmParam']").val(row.alarmParam);
        $("#alarminfoModal .form textarea[name='alarmDesc']").val(row.alarmDesc);

		// show
		$('#alarminfoModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	
	// delete
	$("#alarm_list").on('click', '.alarm_del',function() {
		var id = $(this).parents('div').attr("_id");

		layer.confirm(I18n.system_ok + I18n.system_opt_del + '?', {
			icon: 3,
			title: I18n.system_tips ,
            btn: [ I18n.system_ok, I18n.system_cancel ]
		}, function(index){
			layer.close(index);
			$.ajax({
				type : 'POST',
				url : base_url + "/alarminfo/remove",
				data : {
					"id" : id
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
                        layer.msg(I18n.system_opt_del + I18n.system_success);
                        jobTable.fnDraw(false);
					} else {
                        layer.msg(data.msg || typeName + I18n.system_fail);
					}
				}
			});
		});
	});
	
	var modalValidate = $("#alarminfoModal .form").validate({
		errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	alarmName : {
				required : true,
				maxlength: 50
			}
        },
        messages : {
        	alarmName : {
            	required : I18n.system_please_input + I18n.jobinfo_field_jobdesc
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
        	var editType = $("#alarminfoModal .form input[id='editType']").val();
        	var suc;
        	var fail;
        	if(editType == 'add'){
        		suc = I18n.system_add_suc;
        		fail = I18n.system_add_fail;
        	} else if(editType == 'update'){
        		suc = I18n.system_update_suc;
        		fail = I18n.system_update_fail;
        	}
        	$.post(base_url + "/alarminfo/addOrUpdate",  $("#alarminfoModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
					$('#alarminfoModal').modal('hide');
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: suc,
						icon: '1',
						end: function(layero, index){
							jobTable.fnDraw();
						}
					});
    			} else {
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: (data.msg || fail),
						icon: '2'
					});
    			}
    		});
		}
	});
	
	$("#alarminfoModal").on('hide.bs.modal', function () {
        modalValidate.resetForm();
		$("#alarminfoModal .form")[0].reset();
		$("#alarminfoModal .form .form-group").removeClass("has-error");
	});

	/**
	 * find title by name, GlueType
     */
	function findAlarmTypeTitle(alarmType) {
		var alarmTypeTitle = '-';
        $("#jobAlarmerEnum option").each(function () {
            var name = $(this).val();
            var title = $(this).text();
            if (alarmType == name) {
            	alarmTypeTitle = title;
                return false
            }
        });
        return alarmTypeTitle;
    }
});
