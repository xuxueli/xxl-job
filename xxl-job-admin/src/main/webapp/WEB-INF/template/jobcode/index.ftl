<!DOCTYPE html>
<html>
<head>
  	<title>任务调度中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/codemirror/lib/codemirror.css">
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/codemirror/addon/hint/show-hint.css">
	<style type="text/css">
		.CodeMirror {
      		border: 0px solid black;
      		font-size:16px;
      		height: 100%;
		}
    </style>
</head>
<body class=" layout-top-nav">

<div class="wrapper">
	
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>任务调度中心<small>任务CODE管理</small></h1>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    	<div class="row">
	    		<div class="col-xs-4">
					<div class="input-group margin">
                    	<div class="input-group-btn">
                      		<button type="button" class="btn btn-info">版本回溯</button>
                    	</div>
                    	<select class="form-control" id="jobGroup" >
            				<option value="999" >逻辑调整版本C</option>
            				<option value="999" >逻辑调整版本B</option>
            				<option value="999" >逻辑调整版本A</option>
            				<option value="999" >代码初始化</option>
	                  	</select>
					</div>
	            </div>
	            <div class="col-xs-4">
	            	<div class="input-group margin">
                    	<div class="input-group-btn">
                      		<button type="button" class="btn btn-info">备注</button>
                    	</div>
                    	<input type="text" class="form-control" id="codeRemark" value="${jobName}" autocomplete="on" >
                  	</div>
	            </div>
	            <div class="col-xs-2">
	            	<div class="input-group margin">
                    	<div class="input-group-btn">
                      		<button type="button" class="btn btn-primary" id="save" >保存</button>
                    	</div>
                  	</div>
	            </div>
          	</div>
	    	
			<div class="row">
				<div class="col-xs-12">
					<div class="box callout callout-info">
						<textarea id="codeSource" ></textarea>
					</div>
				</div>
			</div>
	    </section>
	</div>
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<textarea id="demoCode" style="display:none;" >
package com.xxl.job.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.handler.IJobHandler;

public class DemoJobHandler extends IJobHandler {
	private static transient Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	
	@Override
	public JobHandleStatus handle(String... params) throws Exception {
		logger.info(" ... params:" + params);
		for (int i = 0; i < 5; i++) {
			TimeUnit.SECONDS.sleep(1);
			logger.info("handler run:{}", i);
		}
		return JobHandleStatus.SUCCESS;
	}
}
</textarea>
	
<@netCommon.comAlert />
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/codemirror/lib/codemirror.js"></script>
<script src="${request.contextPath}/static/plugins/codemirror/mode/clike/clike.js"></script>
<script src="${request.contextPath}/static/plugins/codemirror/addon/hint/show-hint.js"></script>
<script src="${request.contextPath}/static/plugins/codemirror/addon/hint/anyword-hint.js"></script>
<script>
var id = ${id!-1};
</script>
<script src="${request.contextPath}/static/js/jobcode.index.1.js"></script>
</body>
</html>
