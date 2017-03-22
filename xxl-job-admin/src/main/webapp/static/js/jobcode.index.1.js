$(function() {

	// init code editor
	/*var codeEditor = CodeMirror.fromTextArea(document.getElementById("glueSource"), {
		mode : "text/x-java",
		lineNumbers : true,
		matchBrackets : true
	});*/
	var codeEditor = CodeMirror(document.getElementById("ideWindow"), {
		mode : "text/x-java",
		lineNumbers : true,
		matchBrackets : true,
		value: $("#version_now").val()
	});

	// code change
	$(".source_version").click(function(){
		var sourceId = $(this).attr('version');
		var temp = $( "#" + sourceId ).val();
		codeEditor.setValue( temp );
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
		if (glueRemark.length < 6|| glueRemark.length > 100) {
			ComAlert.show(2, "备注长度应该在6至100之间");
			return;
		}
		
		ComConfirm.show("是否执行保存操作?", function(){
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
		
	});
	
	// before upload
	/*$(window).bind('beforeunload',function(){
		return 'Glue尚未保存，确定离开Glue编辑器？';
	});*/
	
});
