$(function() {

	// init code editor
	/*var codeEditor = CodeMirror.fromTextArea(document.getElementById("glueSource"), {
		mode : "text/x-java",
		lineNumbers : true,
		matchBrackets : true
	});*/

	var codeEditor;
	function initIde(glueSource) {
		if (codeEditor == null) {
            codeEditor = CodeMirror(document.getElementById("ideWindow"), {
                mode : ideMode,
                lineNumbers : true,
                matchBrackets : true,
                value: glueSource
            });
		} else {
            codeEditor.setValue(glueSource);
		}
	}

	initIde($("#version_now").val());

	// code change
	$(".source_version").click(function(){
		var sourceId = $(this).attr('version');
		var temp = $( "#" + sourceId ).val();

		//codeEditor.setValue('');
		initIde(temp);
	});

	// code source save
	$("#save").click(function() {
		$('#saveModal').modal({backdrop: false, keyboard: false}).modal('show');
	});

	$("#saveModal .ok").click(function() {

		var glueSource = codeEditor.getValue();
		var glueRemark = $("#glueRemark").val();
		
		if (!glueRemark) {
			layer.open({
				title: '系统提示',
				content: '请输入备注',
				icon: '2'
			});
			return;
		}
		if (glueRemark.length <4 || glueRemark.length > 100) {
			layer.open({
				title: '系统提示',
				content: '备注长度应该在4至100之间',
				icon: '2'
			});
			return;
		}

		$.ajax({
			type : 'POST',
			url : base_url + '/jobcode/save',
			data : {
				'id' : id,
				'glueSource' : glueSource,
				'glueRemark' : glueRemark
			},
			dataType : "json",
			success : function(data){
				if (data.code == 200) {
					layer.open({
						title: '系统提示',
						content: '保存成功',
						icon: '1',
						end: function(layero, index){
							//$(window).unbind('beforeunload');
							window.location.reload();
						}
					});
				} else {
					layer.open({
						title: '系统提示',
						content: (data.msg || "保存失败"),
						icon: '2'
					});
				}
			}
		});

	});
	
	// before upload
	/*$(window).bind('beforeunload',function(){
		return 'Glue尚未保存，确定离开Glue编辑器？';
	});*/
	
});
