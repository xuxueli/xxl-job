$(function() {
	function getConfig(){
		let spkParam=new URLSearchParams()
		let cpk=Sm2.generateKeyPairHex()
		spkParam.set('pk',cpk.publicKey)
		spkParam.set('sign',Sm3.sm3(spkParam.get('pk')))
		$.post(base_url + "/spk",spkParam.toString() , function(data, status) {
			if (data.code == "200") {
				let publicKey=Sm2.doDecrypt(data.content,cpk.privateKey,1)
				let sign=Sm3.sm3(publicKey)

				let secretKey=Sm4.generateKeyHex()
				let param={
					payload: secretKey
				}
				let searchParams=new URLSearchParams(param)
				let payload=searchParams.get("payload")
				payload=Sm2.doEncrypt(payload,publicKey,1)
				searchParams.set('payload',payload)
				searchParams.set('sign',sign)
				param=searchParams.toString()
				$.post(base_url + "/emailconfig/get",param , function(data, status) {
					if (data.code == "200") {
						layer.msg( I18n.system_success );
						let content=Sm4.decrypt(data.content,secretKey)
						$("#emailConfigForm textarea[name='content']").val(content)
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

	getConfig()

	function doUpdate(){

		let spkParam=new URLSearchParams()
		let cpk=Sm2.generateKeyPairHex()
		spkParam.set('pk',cpk.publicKey)
		spkParam.set('sign',Sm3.sm3(spkParam.get('pk')))
		$.post(base_url + "/spk",spkParam.toString() , function(data, status) {
			if (data.code == "200") {
				let publicKey=Sm2.doDecrypt(data.content,cpk.privateKey,1)
				let sign=Sm3.sm3(publicKey)

				let secretKey=Sm4.generateKeyHex()
				let param=$("#emailConfigForm").serialize()
				let searchParams=new URLSearchParams(param)
				searchParams.set('content',Sm4.encrypt(searchParams.get('content'),secretKey))
				searchParams.set('payload',secretKey)
				let payload=searchParams.get("payload")
				payload=Sm2.doEncrypt(payload,publicKey,1)
				searchParams.set('payload',payload)
				searchParams.set('sign',sign)
				param=searchParams.toString()
				$.post(base_url + "/emailconfig/update",param , function(data, status) {
					if (data.code == "200") {
						layer.msg( I18n.system_success );
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


	var updateModalValidate = $("#emailConfigForm").validate({
		errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
			content : {
				required : true,
				maxlength: 2000
			}
        },
        messages : {
			content : {
            	required : I18n.system_please_input + I18n.emailconfig_name
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
			layer.confirm( I18n.emailconfig_submit_confirm , {
				icon: 3,
				title: I18n.system_tips ,
				btn: [ I18n.system_ok, I18n.system_cancel ]
			}, function(index) {
				layer.close(index);

				doUpdate()
			})

		}
	});


});
