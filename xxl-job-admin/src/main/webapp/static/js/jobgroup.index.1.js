$(function() {

	// remove
	$('.remove').on('click', function(){
		var id = $(this).attr('id');

		layer.confirm( (I18n.system_ok + I18n.jobgroup_del + '？') , {
			icon: 3,
			title: I18n.system_tips ,
            btn: [ I18n.system_ok, I18n.system_cancel ]
		}, function(index){
			layer.close(index);

			$.ajax({
				type : 'POST',
				url : base_url + '/jobgroup/remove',
				data : {"id":id},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						layer.open({
							title: I18n.system_tips ,
                            btn: [ I18n.system_ok ],
							content: (I18n.jobgroup_del + I18n.system_success),
							icon: '1',
							end: function(layero, index){
								window.location.reload();
							}
						});
					} else {
						layer.open({
							title: I18n.system_tips,
                            btn: [ I18n.system_ok ],
							content: (data.msg || (I18n.jobgroup_del + I18n.system_fail)),
							icon: '2'
						});
					}
				},
			});
		});

	});

	// jquery.validate “low letters start, limit contants、 letters、numbers and line-through.”
	jQuery.validator.addMethod("myValid01", function(value, element) {
		var length = value.length;
		var valid = /^[a-z][a-zA-Z0-9-]*$/;
		return this.optional(element) || valid.test(value);
	}, I18n.jobgroup_field_appName_limit );

	$('.add').on('click', function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			appName : {
				required : true,
				rangelength:[4,64],
				myValid01 : true
			},
			title : {
				required : true,
				rangelength:[4, 12]
			},
			order : {
				required : true,
				digits:true,
				range:[1,1000]
			}
		},
		messages : {
			appName : {
				required : I18n.system_please_input+"AppName",
				rangelength: I18n.jobgroup_field_appName_length ,
				myValid01: I18n.jobgroup_field_appName_limit
			},
			title : {
				required : I18n.system_please_input + I18n.jobgroup_field_title ,
				rangelength: I18n.jobgroup_field_title_length
			},
			order : {
				required : I18n.system_please_input + I18n.jobgroup_field_order ,
				digits: I18n.jobgroup_field_order_digits ,
				range: I18n.jobgroup_field_orderrange
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
			$.post(base_url + "/jobgroup/save",  $("#addModal .form").serialize(), function(data, status) {
				if (data.code == "200") {
					$('#addModal').modal('hide');
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: I18n.system_add_suc ,
						icon: '1',
						end: function(layero, index){
							window.location.reload();
						}
					});
				} else {
					layer.open({
						title: I18n.system_tips,
                        btn: [ I18n.system_ok ],
						content: (data.msg || I18n.system_add_fail  ),
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
	});

	// addressType change
	$("#addModal input[name=addressType], #updateModal input[name=addressType]").click(function(){
		var addressType = $(this).val();
		var $addressList = $(this).parents("form").find("textarea[name=addressList]");
		if (addressType == 0) {
            $addressList.css("background-color", "#eee");	// 自动注册
            $addressList.attr("readonly","readonly");
			$addressList.val("");
		} else {
            $addressList.css("background-color", "white");
			$addressList.removeAttr("readonly");
		}
	});

	// update
	$('.update').on('click', function(){
		$("#updateModal .form input[name='id']").val($(this).attr("id"));
		$("#updateModal .form input[name='appName']").val($(this).attr("appName"));
		$("#updateModal .form input[name='title']").val($(this).attr("title"));
		$("#updateModal .form input[name='order']").val($(this).attr("order"));

		// 注册方式
		var addressType = $(this).attr("addressType");
		$("#updateModal .form input[name='addressType']").removeAttr('checked');
		//$("#updateModal .form input[name='addressType'][value='"+ addressType +"']").attr('checked', 'true');
		$("#updateModal .form input[name='addressType'][value='"+ addressType +"']").click();
		// 机器地址
		$("#updateModal .form textarea[name='addressList']").val($(this).attr("addressList"));

		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			appName : {
				required : true,
				rangelength:[4,64],
				myValid01 : true
			},
			title : {
				required : true,
				rangelength:[4, 12]
			},
			order : {
				required : true,
				digits:true,
				range:[1,1000]
			}
		},
		messages : {
            appName : {
                required : I18n.system_please_input+"AppName",
                rangelength: I18n.jobgroup_field_appName_length ,
                myValid01: I18n.jobgroup_field_appName_limit
            },
            title : {
                required : I18n.system_please_input + I18n.jobgroup_field_title ,
                rangelength: I18n.jobgroup_field_title_length
            },
            order : {
                required : I18n.system_please_input + I18n.jobgroup_field_order ,
                digits: I18n.jobgroup_field_order_digits ,
                range: I18n.jobgroup_field_orderrange
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
			$.post(base_url + "/jobgroup/update",  $("#updateModal .form").serialize(), function(data, status) {
				if (data.code == "200") {
					$('#addModal').modal('hide');

					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: I18n.system_update_suc ,
						icon: '1',
						end: function(layero, index){
							window.location.reload();
						}
					});
				} else {
					layer.open({
						title: I18n.system_tips,
                        btn: [ I18n.system_ok ],
						content: (data.msg || I18n.system_update_fail  ),
						icon: '2'
					});
				}
			});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#updateModal .form .form-group").removeClass("has-error");
	});

	
});
