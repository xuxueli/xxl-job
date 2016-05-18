$(function() {

	var javaEditor = CodeMirror.fromTextArea(document.getElementById("editor"), {
		mode : "text/x-java",
		lineNumbers : true,
		matchBrackets : true,
		extraKeys: {
			"F11": function(cm) {
				cm.setOption("fullScreen", !cm.getOption("fullScreen"));
			},
	        "Esc": function(cm) {
	        	if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
	        }
		}
	});
});
