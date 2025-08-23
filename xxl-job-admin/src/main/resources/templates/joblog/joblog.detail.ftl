<!DOCTYPE html>
<html>
<head>
    <#-- import macro -->
    <#import "../common/common.macro.ftl" as netCommon>

    <#-- commonStyle -->
    <@netCommon.commonStyle />

    <#-- biz start（1/5 style） -->
    <#-- biz end（1/5 end） -->

</head>
<body class="hold-transition skin-blue layout-top-nav">
<div class="wrapper">

    <!-- header -->
    <#-- biz start（2/5 style） -->
    <header class="main-header">
        <nav class="navbar navbar-static-top">
            <div class="container">
                <#-- icon -->
                <div class="navbar-header">
                    <a class="navbar-brand"><b>${I18n.joblog_rolling_log}</b> Console</a>
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
                                ${I18n.joblog_rolling_log_refresh}
                            </a>
                        </li>
                    </ul>
                </div>

            </div>
        </nav>
    </header>
    <#-- biz end（2/5 end） -->

    <!-- right start -->
    <div class="content-wrapper" >

        <#-- biz start（3/5 name） -->
        <#-- biz end（3/5 name） -->

        <!-- content-main -->
        <section class="content">

            <#-- biz start（4/5 content） -->
            <pre style="font-size:12px;position:relative;" >
                <div id="logConsole"></div>
                <li class="fa fa-refresh fa-spin" style="font-size: 20px;float: left;" id="logConsoleRunning" ></li>
            </pre>
            <#-- biz end（4/5 content） -->

        </section>
    </div>
    <!-- right end -->

    <!-- footer -->
    <@netCommon.commonFooter />

</div>

<@netCommon.commonScript />

<#-- biz start（5/5 script） -->
<script>
    // 参数
    var triggerCode = '${triggerCode}';
    var handleCode = '${handleCode}';
    var logId = '${logId}';
</script>
<script src="${request.contextPath}/static/js/joblog.detail.1.js"></script>
<#-- biz end（5/5 script） -->

</body>
</html>