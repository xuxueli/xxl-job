<!DOCTYPE html>
<html>
<head>
    <#import "./common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <title>${I18n.admin_name}</title>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["xxljob_adminlte_settings"]?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "emailconfig" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>${I18n.emailconfig_name}</h1>
        </section>

        <!-- Main content -->
        <section class="content">
            <form class="form-horizontal form" role="form" id="emailConfigForm">
                <div class="form-group">
                    <label for="firstname" class="col-sm-2 control-label">${I18n.emailconfig_name}<font
                                color="black">*</font></label>
                    <div class="col-sm-10">
                        <textarea class="textarea form-control" name="content"
                                  placeholder="${I18n.system_please_input}${I18n.emailconfig_name}" maxlength="2000"
                                  style="height: 240px; line-height: 1.2;">
                        </textarea>
                    </div>
                </div>

                <hr>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-6">
                        <button type="submit" class="btn btn-primary">${I18n.system_save}</button>
                    </div>
                </div>
            </form>
        </section>
    </div>
    <!-- /.content-wrapper -->

    <!-- footer -->
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
</body>
<script>

</script>
<script src="${request.contextPath}/static/plugins/com/antherd/sm-crypto-0.3.2/sm3.js"></script>
<script src="${request.contextPath}/static/plugins/com/antherd/sm-crypto-0.3.2/sm2.js"></script>
<script src="${request.contextPath}/static/plugins/com/antherd/sm-crypto-0.3.2/sm4.js"></script>
<script src="${request.contextPath}/static/js/emailconfig.index.1.js"></script>
</html>
