<!DOCTYPE html>
<html>
<head>
  	<title>${I18nUtil.getString("admin_name")}</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/blue.css">
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<a><b>XXL</b>JOB</a>
		</div>
		<form id="loginForm" method="post" >
			<div class="login-box-body">
				<p class="login-box-msg">${I18nUtil.getString("admin_name")}</p>
				<div class="form-group has-feedback">
	            	<input type="text" name="userName" class="form-control" placeholder="${I18nUtil.getString("login_username_placeholder")}" value="admin" maxlength="18" >
	            	<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
				</div>
	          	<div class="form-group has-feedback">
	            	<input type="password" name="password" class="form-control" placeholder="${I18nUtil.getString("login_password_placeholder")}" value="123456" maxlength="18" >
	            	<span class="glyphicon glyphicon-lock form-control-feedback"></span>
	          	</div>
				<div class="row">
					<div class="col-xs-8">
		              	<div class="checkbox icheck">
		                	<label>
		                  		<input type="checkbox" name="ifRemember" > ${I18nUtil.getString("login_remember_me")}
		                	</label>
						</div>
		            </div><!-- /.col -->
		            <div class="col-xs-4">
						<button type="submit" class="btn btn-primary btn-block btn-flat">${I18nUtil.getString("login_btn")}</button>
					</div>
				</div>
			</div>
		</form>
	</div>
<@netCommon.commonScript />
<script>
var system_tips = '${I18nUtil.getString("system_tips")}';
var system_ok = '${I18nUtil.getString("system_ok")}';

var login_username_empty = '${I18nUtil.getString("login_username_empty")}';
var login_username_lt_5 = '${I18nUtil.getString("login_username_lt_5")}';

var login_password_empty = '${I18nUtil.getString("login_password_empty")}';
var login_password_lt_5 = '${I18nUtil.getString("login_password_lt_5")}';

var login_success = '${I18nUtil.getString("login_success")}';
var login_fail = '${I18nUtil.getString("login_fail")}';
</script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/js/login.1.js"></script>

</body>
</html>
