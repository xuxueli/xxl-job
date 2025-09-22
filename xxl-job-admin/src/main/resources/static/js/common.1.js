$(function(){


    // ---------------------- logout ----------------------

    $.ajaxSetup({
        complete: function(xhr,textStatus) {
            if(!xhr.responseText){
                return
            }
            if(xhr.responseText.indexOf('loginForm')>=0 && xhr.responseText.indexOf('login.1.js')>=0){
                // 处理302重定向到登录页
                window.location.reload()
            }
        }
    });

	// logout
	$("#logoutBtn").click(function(){
		layer.confirm( I18n.logout_confirm , {
			icon: 3,
			title: I18n.system_tips ,
            btn: [ I18n.system_ok, I18n.system_cancel ]
		}, function(index){
			layer.close(index);

			$.post(base_url + "/auth/logout", function(data, status) {
				if (data.code == "200") {
                    layer.msg( I18n.logout_success );
                    setTimeout(function(){
                        window.location.href = base_url + "/";
                    }, 500);
				} else {
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: (data.msg || I18n.logout_fail),
						icon: '2'
					});
				}
			});
		});

	});


    // ---------------------- update pwd ----------------------

    // update pwd
    $('#updatePwd').on('click', function(){
        $('#updatePwdModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var updatePwdModalValidate = $("#updatePwdModal .form").validate({
        errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
            oldPassword : {
                required : true ,
                rangelength:[4,50]
            },
            password : {
                required : true ,
                rangelength:[4,50]
            },
            repeatPassword : {
                required : true ,
                rangelength:[4,50]
            }
        },
        messages : {
            oldPassword : {
                required : I18n.system_please_input +I18n.change_pwd_field_oldpwd,
                rangelength : "密码长度限制为4~50"
            },
            password : {
                required : I18n.system_please_input +I18n.change_pwd_field_newpwd,
                rangelength : "密码长度限制为4~50"
            },
            repeatPassword : {
                required : I18n.system_please_input +I18n.change_pwd_field_newpwd,
                rangelength : "密码长度限制为4~50"
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

                    let param=$("#updatePwdModal .form").serialize()
                    let searchParams=new URLSearchParams(param)
                    let oldPassword=searchParams.get("oldPassword")
                    let password=searchParams.get("password")
                    let repeatPassword=searchParams.get("repeatPassword")

                    oldPassword=Sm2.doEncrypt(oldPassword,publicKey,1)
                    searchParams.set('oldPassword',oldPassword)

                    password=Sm2.doEncrypt(password,publicKey,1)
                    searchParams.set('password',password)

                    repeatPassword=Sm2.doEncrypt(repeatPassword,publicKey,1)
                    searchParams.set('repeatPassword',repeatPassword)

                    searchParams.set('sign',sign)
                    param=searchParams.toString()
                    $.post(base_url + "/user/updatePwd",param , function (data, status) {
                        if (data.code == 200) {
                            $('#updatePwdModal').modal('hide');

                            layer.msg(I18n.change_pwd_suc_to_logout);
                            setTimeout(function () {
                        $.post(base_url + "/auth/logout", function(data, status) {
                                    if (data.code == 200) {
                                        window.location.href = base_url + "/";
                                    } else {
                                        layer.open({
                                            icon: '2',
                                            content: (data.msg || I18n.logout_fail)
                                        });
                                    }
                                });
                            }, 500);
                        } else {
                            layer.open({
                                icon: '2',
                                content: (data.msg || I18n.change_pwd + I18n.system_fail)
                            });
                        }
                    });
                } else {
                    layer.open({
                        icon: '2',
                        content: (data.msg || I18n.change_pwd + I18n.system_fail)
                    });
                }
            })
        }
    });
    $("#updatePwdModal").on('hide.bs.modal', function () {
        $("#updatePwdModal .form")[0].reset();
        updatePwdModalValidate.resetForm();
        $("#updatePwdModal .form .form-group").removeClass("has-error");
    });


    // ---------------------- slideToTop ----------------------

	// slideToTop
	var slideToTop = $("<div />");
	slideToTop.html('<i class="fa fa-chevron-up"></i>');
	slideToTop.css({
		position: 'fixed',
		bottom: '20px',
		right: '25px',
		width: '40px',
		height: '40px',
		color: '#eee',
		'font-size': '',
		'line-height': '40px',
		'text-align': 'center',
		'background-color': '#222d32',
		cursor: 'pointer',
		'border-radius': '5px',
		'z-index': '99999',
		opacity: '.7',
		'display': 'none'
	});
	slideToTop.on('mouseenter', function () {
		$(this).css('opacity', '1');
	});
	slideToTop.on('mouseout', function () {
		$(this).css('opacity', '.7');
	});
	$('.wrapper').append(slideToTop);
	$(window).scroll(function () {
		if ($(window).scrollTop() >= 150) {
			if (!$(slideToTop).is(':visible')) {
				$(slideToTop).fadeIn(500);
			}
		} else {
			$(slideToTop).fadeOut(500);
		}
	});
	$(slideToTop).click(function () {
		$("html,body").animate({		// firefox ie not support body, chrome support body. but found that new version chrome not support body too.
			scrollTop: 0
		}, 100);
	});


    // ---------------------- body fixed ----------------------

    // init body fixed
    $('body').addClass('fixed');


    // ---------------------- menu, sidebar-toggle ----------------------

    // init menu speed
    $('.sidebar-menu').attr('data-animation-speed', 1);		// default 300ms

    // init menu status
    if ( 'close' === $.cookie('sidebar_status') ) {
        $('body').addClass('sidebar-collapse');
    } else {
        $('body').removeClass('sidebar-collapse');
    }

    // change menu status
    $('.sidebar-toggle').click(function(){
        if ( 'close' === $.cookie('sidebar_status') ) {
            $.cookie('sidebar_status', 'open', { expires: 7 });
        } else {
            $.cookie('sidebar_status', 'close', { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
        }
    });

});
