$(function() {

	// init code editor
	var codeEditor = CodeMirror.fromTextArea(document.getElementById("codeSource"), {
		mode : "text/x-java",
		lineNumbers : true,
		matchBrackets : true
	});
	codeEditor.setValue( $("#demoCode").val() );
	
	
	$("#save").click(function() {
		var codeSource = codeEditor.getValue();
		var codeRemark = $("#codeRemark").val();
		
		if (!codeRemark) {
			ComAlert.show(1, "请输入备注");
			return;
		}
		if (codeRemark.length < 6|| codeRemark.length > 100) {
			ComAlert.show(1, "备注长度应该在6至100之间");
			return;
		}
		
		ComConfirm.show("是否执行保存操作?", function(){
			$.ajax({
				type : 'POST',
				url : base_url + '/jobcode/save',
				data : {
					'jobInfo.id' : id,
					'jobInfo.codeSource' : codeSource,
					'jobInfo.codeRemark' : codeRemark
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, '提交成功', function(){
							//$(window).unbind('beforeunload');
							window.location.reload();
						});
					} else {
						ComAlert.alert(data.msg);
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
