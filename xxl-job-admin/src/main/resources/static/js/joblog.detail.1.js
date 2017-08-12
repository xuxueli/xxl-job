$(function() {

    // trigger fail, end
    if (triggerCode != 200) {
        $('#logConsoleRunning').hide();
        $('#logConsole').append('<span style="color: red;">任务发起调度失败，无法查看执行日志</span>');
        return;
    }

    // pull log
    var fromLineNum = 1;    // [from, to], start as 1
    var pullFailCount = 0;
    function pullLog() {
        // pullFailCount, max=20
        if (pullFailCount++ > 20) {
            logRunStop('<span style="color: red;">终止请求Rolling日志,请求失败次数超上限,可刷新页面重新加载日志</span>');
            return;
        }

        // load
        console.log("pullLog, fromLineNum:" + fromLineNum);

        $.ajax({
            type : 'POST',
            async: false,   // sync, make log ordered
            url : base_url + '/joblog/logDetailCat',
            data : {
                "executorAddress":executorAddress,
                "triggerTime":triggerTime,
                "logId":logId,
                "fromLineNum":fromLineNum
            },
            dataType : "json",
            success : function(data){

                if (data.code == 200) {
                    if (!data.content) {
                        console.log('pullLog fail');
                        return;
                    }
                    if (fromLineNum != data.content.fromLineNum) {
                        console.log('pullLog fromLineNum not match');
                        return;
                    }
                    if (fromLineNum > data.content.toLineNum ) {
                        console.log('pullLog already line-end');

                        // valid end
                        if (data.content.end) {
                            logRunStop('<br><span style="color: green;">[Rolling Log Finish]</span>');
                            return;
                        }

                        return;
                    }

                    // append content
                    fromLineNum = data.content.toLineNum + 1;
                    $('#logConsole').append(data.content.logContent);
                    pullFailCount = 0;

                    // scroll to bottom
                    scrollTo(0, document.body.scrollHeight);        // $('#logConsolePre').scrollTop( document.body.scrollHeight + 300 );

                } else {
                    console.log('pullLog fail:'+data.msg);
                }
            }
        });
    }

    // pull first page
    pullLog();

    // handler already callback, end
    if (handleCode > 0) {
        logRunStop('<br><span style="color: green;">[Load Log Finish]</span>');
        return;
    }

    // round until end
    var logRun = setInterval(function () {
        pullLog()
    }, 3000);
    function logRunStop(content){
        $('#logConsoleRunning').hide();
        logRun = window.clearInterval(logRun);
        $('#logConsole').append(content);
    }

});
