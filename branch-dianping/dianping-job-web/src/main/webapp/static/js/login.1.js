$(function(){
	// 复选框
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });
	 
	// 登陆.规则校验
	var loginFormValid = $("#loginForm").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	userName : {  
        		required : true ,
                minlength: 6,
                maxlength: 18
            },  
            password : {  
            	required : true ,
                minlength: 6,
                maxlength: 18
            } 
        }, 
        messages : {  
        	userName : {  
                required :"请输入账号."  ,
                minlength:"账号不应低于6位",
                maxlength:"账号不应超过18位"
            },  
            password : {
            	required :"请输入密码."  ,
                minlength:"密码不应低于6位",
                maxlength:"密码不应超过18位"
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
			$.post(base_url + "/login", $("#loginForm").serialize(), function(data, status) {
				if (data.code == "S") {
					ComAlert.show(1, "登陆成功", function(){
						window.location.reload();
					});
				} else {
					ComAlert.show(2, data.msg);
				}
			});
		}
	});
});