$(function() {

	// init code editor
	/*var codeEditor = CodeMirror.fromTextArea(document.getElementById("glueSource"), {
		mode : "text/x-java",
		lineNumbers : true,
		matchBrackets : true
	});*/

	var codeEditor;
	function initIde(glueType, glueSource) {
		var ideMode = "text/x-java";
		if ('GLUE_GROOVY'==glueType){
			ideMode = "text/x-java";
		} else if ('GLUE_SHELL'==glueType){
			ideMode = "text/x-sh";
		} else if ('GLUE_PYTHON'==glueType){
			ideMode = "text/x-python";
		}

		codeEditor = CodeMirror(document.getElementById("ideWindow"), {
			mode : ideMode,
			lineNumbers : true,
			matchBrackets : true,
			value: glueSource
		});
	}

	initIde(glueType, $("#version_now").val());

	// code change
	$(".source_version").click(function(){
		var glueType = $(this).attr('glueType');
		var sourceId = $(this).attr('version');
		var temp = $( "#" + sourceId ).val();

		codeEditor.setValue('');
		initIde(glueType, temp);
	});

	// code source save
	$("#save").click(function() {
		$('#saveModal').modal({backdrop: false, keyboard: false}).modal('show');
	});

	$("#saveModal .ok").click(function() {

		var glueSource = codeEditor.getValue();
		var glueRemark = $("#glueRemark").val();
		
		if (!glueRemark) {
			ComAlert.show(2, "请输入备注");
			return;
		}
		if (glueRemark.length <4 || glueRemark.length > 100) {
			ComAlert.show(2, "备注长度应该在4至100之间");
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
					ComAlert.show(1, '保存成功', function(){
						//$(window).unbind('beforeunload');
						window.location.reload();
					});
				} else {
					ComAlert.show(2, data.msg);
				}
			}
		});

	});
	
	// before upload
	/*$(window).bind('beforeunload',function(){
		return 'Glue尚未保存，确定离开Glue编辑器？';
	});*/
	
});
