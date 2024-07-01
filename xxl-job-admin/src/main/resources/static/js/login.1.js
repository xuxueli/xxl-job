$(function(){

	// input iCheck
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });

	// login Form Valid
	var loginFormValid = $("#loginForm").validate({
		errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
        	userName : {
        		required : true ,
                minlength: 4,
                maxlength: 18
            },
            password : {
            	required : true ,
                minlength: 4,
                maxlength: 18
            }
        },
        messages : {
        	userName : {
                required  : I18n.login_username_empty,
                minlength : I18n.login_username_lt_4
            },
            password : {
            	required  : I18n.login_password_empty  ,
                minlength : I18n.login_password_lt_4
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
            let spkParam=new URLSearchParams()
            let cpk=Sm2.generateKeyPairHex()
            spkParam.set('pk',cpk.publicKey)
            spkParam.set('sign',Sm3.sm3(spkParam.get('pk')))
            $.post(base_url + "/spk",spkParam.toString() , function(data, status) {
                if (data.code == "200") {
                    let publicKey=Sm2.doDecrypt(data.content,cpk.privateKey,1)
                    let sign=Sm3.sm3(publicKey)

                    let param=$("#loginForm").serialize()
                    let searchParams=new URLSearchParams(param)
                    let password=searchParams.get("password")
                    password=Sm2.doEncrypt(password,publicKey,1)
                    searchParams.set('password',password)
                    searchParams.set('sign',sign)
                    param=searchParams.toString()
                    $.post(base_url + "/login",param , function(data, status) {
                        if (data.code == "200") {
                            layer.msg( I18n.login_success );
                            setTimeout(function(){
                                window.location.href = base_url + "/";
                            }, 500);
                        } else {
                            layer.open({
                                title: I18n.system_tips,
                                btn: [ I18n.system_ok ],
                                content: (data.msg || I18n.login_fail ),
                                icon: '2'
                            });
                        }
                    });
                }else{
                    layer.open({
                        title: I18n.system_tips,
                        btn: [ I18n.system_ok ],
                        content: (data.msg || I18n.login_fail ),
                        icon: '2'
                    });
                }
            })
		}
	});
});
