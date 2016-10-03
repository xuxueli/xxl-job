$(function() {

	// remove
	$('.remove').on('click', function(){
		var id = $(this).attr('id');

		ComConfirm.show("确认删除分组?", function(){
			$.ajax({
				type : 'POST',
				url : base_url + '/jobgroup/remove',
				data : {"id":id},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, '删除成功');
						window.location.reload();
					} else {
						if (data.msg) {
							ComAlert.show(2, data.msg);
						} else {
							ComAlert.show(2, '删除失败');
						}
					}
				},
			});
		});
	});

	// jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
	jQuery.validator.addMethod("myValid01", function(value, element) {
		var length = value.length;
		var valid = /^[a-z][a-zA-Z0-9-]*$/;
		return this.optional(element) || valid.test(value);
	}, "限制以小写字母开头，由小写字母、数字和下划线组成");

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
				required :"请输入“AppName”",
				rangelength:"AppName长度限制为4~64",
				myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
			},
			title : {
				required :"请输入“执行器名称”",
				rangelength:"长度限制为4~12"
			},
			order : {
				required :"请输入“排序”",
				digits: "请输入整数",
				range: "取值范围为1~1000"
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
					setTimeout(function () {
						ComAlert.show(1, "新增成功", function(){
							window.location.reload();
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
	});

	$('.update').on('click', function(){
		$("#updateModal .form input[name='id']").val($(this).attr("id"));
		$("#updateModal .form input[name='appName']").val($(this).attr("appName"));
		$("#updateModal .form input[name='title']").val($(this).attr("title"));
		$("#updateModal .form input[name='order']").val($(this).attr("order"));

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
				required :"请输入“AppName”",
				rangelength:"AppName长度限制为4~64",
				myValid01: "限制以小写字母开头，由小写字母、数字和中划线组成"
			},
			title : {
				required :"请输入“执行器名称”",
				rangelength:"长度限制为4~12"
			},
			order : {
				required :"请输入“排序”",
				digits: "请输入整数",
				range: "取值范围为1~1000"
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
					setTimeout(function () {
						ComAlert.show(1, "更新成功", function(){
							window.location.reload();
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
		$("#updateModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#updateModal .form .form-group").removeClass("has-error");
	});

	
});
