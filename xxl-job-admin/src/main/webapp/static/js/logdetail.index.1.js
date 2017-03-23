$(function() {

	// valid
	if (!running) {
		return;
	}

	// 加载日志
	var fromLineNum = 0;
	var pullFailCount = 0;
	function pullLog() {

		// pullFailCount, max=20
		if (pullFailCount >= 20) {
			console.log("pullLog fail-count limit");
			running = false;
		}

		// valid
		if (!running) {
			$('.logConsoleRunning').hide();
			logRun = window.clearInterval(logRun)
			return;
		}

		// load
		console.log("pullLog, fromLineNum:" + fromLineNum);
		$.ajax({
			type : 'POST',
			async: false,   // async, avoid js invoke pagelist before jobId data init
			url : base_url + '/joblog/logDetailCat',
			data : {
				"executorAddress":executorAddress,
				"triggerTime":triggerTime,
				"logId":logId,
				"fromLineNum":fromLineNum
			},
			dataType : "json",
			success : function(data){
				pullFailCount++;
				if (data.code == 200) {
					if (!data.content) {
						console.log('pullLog fail');
						return;
					}
					if (fromLineNum != data.content.fromLineNum) {
						console.log('pullLog fromLineNum not match');
						return;
					}
					if (fromLineNum == (data.content.toLineNum + 1) ) {
						console.log('pullLog already line-end');
						return;
					}

					// append
					fromLineNum = data.content.toLineNum + 1;
					$('#logConsole').append(data.content.logContent);
					pullFailCount = 0;

					// valid end
					if (data.content.end) {
						running = false;
						console.log("pullLog already file-end");
					}
				} else {
					ComAlertTec.show(data.msg);
				}
			}
		});
	}

	// 周期运行
	pullLog();
	var logRun = setInterval(function () {
		pullLog()
	}, 3000);

});
