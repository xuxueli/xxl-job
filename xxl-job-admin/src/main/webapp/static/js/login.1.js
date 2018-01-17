$(function(){
	// 复选框
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });
    
	// 登录.规则校验
	var loginFormValid = $("#loginForm").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {  
        	userName : {  
        		required : true ,
                minlength: 5,
                maxlength: 18
            },  
            password : {  
            	required : true ,
                minlength: 5,
                maxlength: 18
            } 
        }, 
        messages : {  
        	userName : {  
                required  : login_username_empty,
                minlength : login_username_lt_5
            },
            password : {
            	required  : login_password_empty  ,
                minlength : login_password_lt_5
                /*,maxlength:"登录密码不应超过18位"*/
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
				if (data.code == "200") {
                    layer.msg(login_success);
                    setTimeout(function(){
                        window.location.href = base_url;
                    }, 500);
                    /*layer.open({
                        title: '系统提示',
                        content: '登录成功',
                        icon: '1',
                        end: function(layero, index){
                            window.location.href = base_url;
                        }
                    });*/
				} else {
                    layer.open({
                        title: system_tips,
                        btn: [system_ok],
                        content: (data.msg || login_fail),
                        icon: '2'
                    });
				}
			});
		}
	});
});