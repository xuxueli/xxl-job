<!DOCTYPE html>
<html>
<head>
  	<#import "./common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/blue.css">
	<title>${I18n.admin_name}</title>
	<style>
		.login-footer{
			position: fixed;
			width: 100%;
			bottom: 0;
			left: 0;
			right: 0;
			margin-left: 0;
			background: linear-gradient(90deg, rgba(255,255,255,0.7), transparent,rgba(255,255,255,0.7));
			border-radius: 18px 18px 0 0;
			border: none;
			background-size: 300%;
			animation: aniHtml 13s infinite;
		}
	</style>
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<a><b>XXL</b>JOB</a>
		</div>
		<form id="loginForm" method="post" >
			<div class="login-box-body">
				<p class="login-box-msg">${I18n.admin_name}</p>
				<div class="form-group has-feedback">
	            	<input type="text" name="userName" class="form-control" placeholder="${I18n.login_username_placeholder}"  maxlength="18" >
	            	<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
				</div>
	          	<div class="form-group has-feedback">
	            	<input type="password" name="password" class="form-control" placeholder="${I18n.login_password_placeholder}"  maxlength="18" >
	            	<span class="glyphicon glyphicon-lock form-control-feedback"></span>
	          	</div>
				<div class="row">
					<div class="col-xs-8">
		              	<div class="checkbox icheck">
		                	<label>
		                  		<input type="checkbox" name="ifRemember" > &nbsp; ${I18n.login_remember_me}
		                	</label>
						</div>
		            </div><!-- /.col -->
		            <div class="col-xs-4">
						<button type="submit" class="btn btn-primary btn-block btn-flat">${I18n.login_btn}</button>
					</div>
				</div>
			</div>
		</form>
	</div>

	<footer class="main-footer login-footer">
		Powered by <b>XXL-JOB</b> ${I18n.admin_version}
		<div class="pull-right hidden-xs">
			<strong>Copyright &copy; 2015-${.now?string('yyyy')} &nbsp;
				<a href="https://www.xuxueli.com/" target="_blank" >xuxueli</a>
				&nbsp;
				<a href="https://github.com/xuxueli/xxl-job" target="_blank" >github</a>
			</strong><!-- All rights reserved. -->
		</div>
	</footer>
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/com/antherd/sm-crypto-0.3.2/sm3.js"></script>
<script src="${request.contextPath}/static/plugins/com/antherd/sm-crypto-0.3.2/sm2.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/js/login.1.js"></script>

</body>
</html>
