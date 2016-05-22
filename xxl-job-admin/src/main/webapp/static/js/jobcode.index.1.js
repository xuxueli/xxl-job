$(function() {

	// init code editor
	var codeEditor = CodeMirror.fromTextArea(document.getElementById("glueSource"), {
		mode : "text/x-java",
		lineNumbers : true,
		matchBrackets : true
	});
	codeEditor.setValue( $("#glue_now").val() );
	
	// code change
	$("#glue_version").change(function(){
		var temp = $( "#" + $(this).val() ).val();
		codeEditor.setValue( temp );
	});
	
	// editor height
	var height = Math.max(document.documentElement.clientHeight, document.body.offsetHeight);
	$(".CodeMirror").attr('style', 'height:'+ height +'px');
	
	// code source save
	$("#save").click(function() {
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
					'jobGroup' : jobGroup,
					'jobName' : jobName,
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
