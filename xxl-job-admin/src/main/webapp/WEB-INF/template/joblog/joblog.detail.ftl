<!DOCTYPE html>
<html>
<head>
    <title>任务调度中心</title>
<#import "/common/common.macro.ftl" as netCommon>
<@netCommon.commonStyle />
    <style>
        html{  background-color: whitesmoke;  }
    </style>
</head>
<body class="hold-transition skin-blue layout-top-nav">

<div class2="wrapper">

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

    <div>
        <pre>
            <div id="logConsole"></div>
            <li class="fa fa-refresh fa-spin" style="font-size: 20px;float: left;" id="logConsoleRunning" ></li>
            <div style="margin-top: 50px;" ></div>
        </pre>
    </div>

</div>

<@netCommon.commonScript />
<script>
    // 参数
    var triggerCode = '${triggerCode}';
    var handleCode = '${handleCode}';
    var executorAddress = '${executorAddress}';
    var triggerTime = '${triggerTime}';
    var logId = '${logId}';
</script>
<script src="${request.contextPath}/static/js/joblog.detail.1.js"></script>

</body>
</html>