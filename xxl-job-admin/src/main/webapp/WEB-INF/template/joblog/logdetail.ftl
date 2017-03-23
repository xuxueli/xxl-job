<!DOCTYPE html>
<html>
<head>
    <title>任务调度中心</title>
<#import "/common/common.macro.ftl" as netCommon>
<@netCommon.commonStyle />
    <style type="text/css">
        .logConsolePre {
            font-size:12px;
            width: 100%;
            height: 100%;
            /*bottom: 0;
            top: 0px;*/
            position: absolute;
            /*color:white;background-color:black*/
        }
        .logConsoleRunning {
            font-size: 20px;
            margin-top: 7px;
        }
    </style>
</head>
<body class="skin-blue fixed layout-top-nav">

<div class="wrapper">

    <header class="main-header">
        <nav class="navbar navbar-static-top">
            <div class="container">
            <#-- icon -->
                <div class="navbar-header">
                    <a href="../../index2.html" class="navbar-brand"><b>日志</b>Console</a>
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse">
                        <i class="fa fa-bars"></i>
                    </button>
                </div>

                <#-- left nav -->
                <div class="collapse navbar-collapse pull-left" id="navbar-collapse">
                    <ul class="nav navbar-nav">
                        <#--<li class="active" ><a href="javascript:;">任务：<span class="sr-only">(current)</span></a></li>-->
                    </ul>
                </div>

                <#-- right nav -->
                <div class="navbar-custom-menu">
                    <ul class="nav navbar-nav">
                        <li>
                            <a href="javascript:window.location.reload();" >
                                <i class="fa fa-fw fa-refresh" ></i>
                                刷新
                            </a>
                        </li>
                    </ul>
                </div>

            </div>
        </nav>
    </header>

    <div class="content-wrapper" >
        <pre class="logConsolePre"><div id="logConsole"></div><li class="fa fa-refresh fa-spin logConsoleRunning" ></li></pre>
    </div>

</div>

<@netCommon.commonScript />
<script>

    // 参数
    var running = true;     // 允许运行
    var executorAddress;
    var triggerTime;
    var logId;

    // init
    <#if logStatue.code == 200>
        running = true;
        $('.logConsoleRunning').show();

        executorAddress = '${executorAddress}';
        triggerTime = '${triggerTime}';
        logId = '${logId}';
    <#else>
        running = false;
        $('.logConsoleRunning').hide();

        $('.logConsole').append('${logStatue.msg}');
    </#if>

</script>
<script src="${request.contextPath}/static/js/logdetail.index.1.js"></script>

</body>
</html>