<!DOCTYPE html>
<html>
<head>
    <#-- import macro -->
  	<#import "./common/common.macro.ftl" as netCommon>
    <#-- commonStyle -->
	<@netCommon.commonStyle />

    <#-- biz start（1/5 style） -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <#-- biz end（1/5 end） -->

</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

	<!-- header -->
	<@netCommon.commonHeader />

	<!-- left -->
	<@netCommon.commonLeft "index" />

    <!-- right start -->
	<div class="content-wrapper">

        <!-- content-header -->
		<section class="content-header">
            <#-- biz start（3/5 name） -->
			<h1>${I18n.job_dashboard_name}</h1>
            <#-- biz end（3/5 name） -->
		</section>

        <!-- content-main -->
		<section class="content">

            <#-- biz start（4/5 content） -->

            <!-- 任务信息 -->
            <div class="row">

                <#-- 任务信息 -->
                <div class="col-md-4 col-sm-6 col-xs-12">
                    <div class="info-box bg-aqua">
                        <span class="info-box-icon"><i class="fa fa-flag-o"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">${I18n.job_dashboard_job_num}</span>
                            <span class="info-box-number">${jobInfoCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">${I18n.job_dashboard_job_num_tip}</span>
                        </div>
                    </div>
                </div>

                <#-- 调度信息 -->
                <div class="col-md-4 col-sm-6 col-xs-12" >
                    <div class="info-box bg-yellow">
                        <span class="info-box-icon"><i class="fa fa-calendar"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">${I18n.job_dashboard_trigger_num}</span>
                            <span class="info-box-number">${jobLogCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%" ></div>
                            </div>
                            <span class="progress-description">
                                ${I18n.job_dashboard_trigger_num_tip}
                                <#--<#if jobLogCount gt 0>
                                    调度成功率：${(jobLogSuccessCount*100/jobLogCount)?string("0.00")}<small>%</small>
                                </#if>-->
                            </span>
                        </div>
                    </div>
                </div>

                <#-- 执行器 -->
                <div class="col-md-4 col-sm-6 col-xs-12">
                    <div class="info-box bg-green">
                        <span class="info-box-icon"><i class="fa ion-ios-settings-strong"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">${I18n.job_dashboard_jobgroup_num}</span>
                            <span class="info-box-number">${executorCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">${I18n.job_dashboard_jobgroup_num_tip}</span>
                        </div>
                    </div>
                </div>

            </div>

            <#-- 调度报表：时间区间筛选，左侧折线图 + 右侧饼图 -->
            <div class="row">
                <div class="col-md-12">
                    <div class="box">
                        <div class="box-header with-border">
                            <h3 class="box-title">${I18n.job_dashboard_report}</h3>
                            <#--<input type="text" class="form-control" id="filterTime" readonly >-->

                            <!-- tools box -->
                            <div class="pull-right box-tools">
                                <button type="button" class="btn btn-primary btn-sm daterange pull-right" data-toggle="tooltip" id="filterTime" >
                                    <i class="fa fa-calendar"></i>
                                </button>
                                <#--<button type="button" class="btn btn-primary btn-sm pull-right" data-widget="collapse" data-toggle="tooltip" title="" style="margin-right: 5px;" data-original-title="Collapse">
                                    <i class="fa fa-minus"></i>
                                </button>-->
                            </div>
                            <!-- /. tools -->

                        </div>
                        <div class="box-body">
                            <div class="row">
                                <#-- 左侧折线图 -->
                                <div class="col-md-8">
                                    <div id="lineChart" style="height: 350px;"></div>
                                </div>
                                <#-- 右侧饼图 -->
                                <div class="col-md-4">
                                    <div id="pieChart" style="height: 350px;"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <#-- biz end（4/5 content） -->

		</section>
	</div>
    <!-- right end -->

    <!-- footer -->
	<@netCommon.commonFooter />
</div>
<@netCommon.commonScript />

<#-- biz start（5/5 script） -->

<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<#-- echarts -->
<script src="${request.contextPath}/static/plugins/echarts/echarts.common.min.js"></script>
<script src="${request.contextPath}/static/js/index.js"></script>

<#-- biz end（5/5 script） -->
</body>
</html>
