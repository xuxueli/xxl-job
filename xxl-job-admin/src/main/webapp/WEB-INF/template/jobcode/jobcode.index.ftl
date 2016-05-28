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

<#if !jobInfo?exists>
	<div class="wrapper">
		<div class="content-wrapper">
			<section class="content-header">
				<h1>抱歉，任务不存在.</small></h1>
			</section>
		</div>
	</div>
<#else>
	<div class="wrapper">
		
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>任务调度中心<small>任务GLUE管理</small></h1>
			</section>
			<!-- Main content -->
		    <section class="content">
		    	<div class="row">
		    		<div class="col-xs-4">
						<div class="input-group margin">
	                    	<div class="input-group-btn">
	                      		<button type="button" class="btn btn-info">版本回溯</button>
	                    	</div>
	                    	<select class="form-control" id="glue_version" >
	            				<option value="glue_now" >${jobInfo.glueRemark}【线上】</option>
	            				<#if jobLogGlues?exists && jobLogGlues?size gt 0 >
			                  	<#list jobLogGlues as glue>
			                  		<option value="glue_log_${glue.id}" >${glue.glueRemark}</option>
			                  	</#list>
			                  	</#if>
		                  	</select>
		                  	
		                  	<textarea id="glue_now" style="display:none;" >${jobInfo.glueSource}</textarea>
		                  	<#if jobLogGlues?exists && jobLogGlues?size gt 0 >
		                  	<#list jobLogGlues as glue>
		                  		<textarea id="glue_log_${glue.id}" style="display:none;" >${glue.glueSource}</textarea>
		                  	</#list>
		                  	</#if>
		                  	
						</div>
		            </div>
		            <div class="col-xs-4">
		            	<div class="input-group margin">
	                    	<div class="input-group-btn">
	                      		<button type="button" class="btn btn-info">备注</button>
	                    	</div>
	                    	<input type="text" class="form-control" id="glueRemark" value="${jobName}" autocomplete="on" >
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
							<textarea id="glueSource" ></textarea>
						</div>
					</div>
				</div>
		    </section>
		</div>
		<!-- footer -->
		<@netCommon.commonFooter />
	</div>
	
<@netCommon.comAlert />
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/codemirror/lib/codemirror.js"></script>
<script src="${request.contextPath}/static/plugins/codemirror/mode/clike/clike.js"></script>
<script src="${request.contextPath}/static/plugins/codemirror/addon/hint/show-hint.js"></script>
<script src="${request.contextPath}/static/plugins/codemirror/addon/hint/anyword-hint.js"></script>
<script>
var jobGroup = '${jobInfo.jobGroup}';
var jobName = '${jobInfo.jobName}';
</script>
<script src="${request.contextPath}/static/js/jobcode.index.1.js"></script>

</#if>
</body>
</html>
