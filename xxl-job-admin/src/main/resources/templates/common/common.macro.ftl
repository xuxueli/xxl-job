<#macro commonStyle>

	<#-- favicon -->
	<link rel="icon" href="${request.contextPath}/static/favicon.ico" />

	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/Ionicons/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

	<!-- pace -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/PACE/themes/blue/pace-theme-flash.css">

	<#-- i18n -->
	<#global I18n = I18nUtil.getMultString()?eval />

	<style>
		html{
			background: linear-gradient(125deg, #2ceee0, #27ae60, #2980b9, #e75c3c, #8e44ad);
			background-size: 400%;
			animation: aniHtml 20s infinite;
		}
		body{
			background: transparent !important;
		}
		.login-box-body{
			border-radius: 8px !important;
			background: linear-gradient(0deg, rgba(255,255,255,0.8), rgba(255,255,255,0.3)) !important;
		}
		.login-logo a{
			color: white !important;
		}
		@keyframes aniHtml {
			0% {
				background-position: 0% 50%;
			}
			50% {
				background-position: 100% 50%;
			}
			100% {
				background-position: 0% 50%;
			}
		}
		.wrapper{
			background-color: #222d3244 !important;
		}
		.main-footer{
			background: linear-gradient(28deg, #fffb, #fff3) !important;
		}
		.main-sidebar, .left-side{
			background: linear-gradient(90deg,#222d3266,#222d3211) !important;
		}
		.main-header .logo{
			background: linear-gradient(90deg,#367fa966,#367fa944) !important;
		}
		.main-header .navbar{
			background: linear-gradient(92deg,#367fa944,#367fa911) !important;
		}
		.sidebar-menu>li.header {
			background: #1a2226aa !important;
		}
		.sidebar-menu>li:hover>a,
		.sidebar-menu>li.active>a,
		.sidebar-menu>li.menu-open>a {
			background: #1e282caa !important;
		}
		.content-wrapper{
			background: linear-gradient(130deg,#ecf0f5aa,#ecf0f544) !important;
		}
		.box,
		.card{
			background: linear-gradient(179deg, #fffd, #fff1) !important;
		}
		.table-striped>tbody>tr:nth-of-type(odd) {
			background-color: #fff8 !important;
		}
		.pagination > li >a {
			background: #fafafa88 !important;
		}
		.pagination > .disable >a {
			background: #fafafa88 !important;
		}
		.pagination > .active >a {
			background: #337ab788 !important;
		}
		.modal-content {
			background: linear-gradient(179deg, #ffff, #fff8) !important;
			border-radius: 8px !important;
		}
		.callout-info {
			background: linear-gradient(180deg, #00c0efee, #00c0ef11) !important;
		}
		.layui-layer {
			background: linear-gradient(176deg, #fffe, #fffa) !important;
		}
		.layui-layer-shade {
			opacity: 0.35 !important;
		}

		.layui-layer-hui .layui-layer-content{
			color: #666;
		}
	</style>
</#macro>

<#macro commonScript>
	<!-- jQuery -->
	<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
	<!-- Bootstrap -->
	<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="${request.contextPath}/static/adminlte/bower_components/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="${request.contextPath}/static/adminlte/dist/js/adminlte.min.js"></script>
	<!-- jquery.slimscroll -->
	<script src="${request.contextPath}/static/adminlte/bower_components/jquery-slimscroll/jquery.slimscroll.min.js"></script>

    <!-- pace -->
    <script src="${request.contextPath}/static/adminlte/bower_components/PACE/pace.min.js"></script>
    <#-- jquery cookie -->
	<script src="${request.contextPath}/static/plugins/jquery/jquery.cookie.js"></script>
	<#-- jquery.validate -->
	<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>

	<#-- layer -->
	<script src="${request.contextPath}/static/plugins/layer/layer.js"></script>

	<#-- common -->
    <script src="${request.contextPath}/static/js/common.1.js"></script>
    <script>
		var base_url = '${request.contextPath}';
        var I18n = ${I18nUtil.getMultString()};
	</script>

	<script>
		setTimeout(function(){
			let url=new URL(window.location.href)
			let enableAnimation=url.searchParams.get('ani')
			if(enableAnimation===null|| enableAnimation===undefined){
				enableAnimation=localStorage.getItem('ani')
			}else{
				localStorage.setItem('ani',enableAnimation)
			}
			if('false'===enableAnimation||'0'===enableAnimation){
				let dom=document.getElementsByTagName('html')
				if(dom && dom.length>0){
					dom[0].style.animation='unset'
					dom[0].style.backgroundPosition=Math.floor(Math.random()*80+10)+'% '+Math.floor(Math.random()*80+10)+'%'
				}

				dom=document.getElementsByClassName('login-footer')
				if(dom && dom.length>0){
					dom[0].style.animation='unset'
					dom[0].style.backgroundPosition=Math.floor(Math.random()*80+10)+'% '+Math.floor(Math.random()*80+10)+'%'
				}
			}
		},300);

	</script>

	<script src="${request.contextPath}/static/plugins/com/antherd/sm-crypto-0.3.2/sm2.js"></script>
	<script src="${request.contextPath}/static/plugins/com/antherd/sm-crypto-0.3.2/sm3.js"></script>
</#macro>

<#macro commonHeader>
	<header class="main-header">
		<a href="${request.contextPath}/" class="logo">
			<span class="logo-mini"><b>XXL</b></span>
			<span class="logo-lg"><b>${I18n.admin_name}</b></span>
		</a>
		<nav class="navbar navbar-static-top" role="navigation">

			<a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

          	<div class="navbar-custom-menu">
				<ul class="nav navbar-nav">
					<#-- login user -->
                    <li class="dropdown">
                        <a href="javascript:" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                            ${I18n.system_welcome} ${loginUser.username}
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" role="menu">
                            <li id="updatePwd" ><a href="javascript:">${I18n.change_pwd}</a></li>
                            <li id="logoutBtn" ><a href="javascript:">${I18n.logout_btn}</a></li>
                        </ul>
                    </li>
				</ul>
			</div>

		</nav>
	</header>

	<!-- 修改密码.模态框 -->
	<div class="modal fade" id="updatePwdModal" tabindex="-1" role="dialog"  aria-hidden="true">
		<div class="modal-dialog ">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" >${I18n.change_pwd}</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal form" role="form" >
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">${I18n.change_pwd_field_oldpwd}<font color="red">*</font></label>
							<div class="col-sm-10"><input type="password" class="form-control" name="oldPassword" placeholder="${I18n.system_please_input} ${I18n.change_pwd_field_oldpwd}" maxlength="50" ></div>
						</div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">${I18n.change_pwd_field_newpwd}<font color="red">*</font></label>
							<div class="col-sm-10"><input type="password" class="form-control" name="password" placeholder="${I18n.system_please_input} ${I18n.change_pwd_field_newpwd}" maxlength="50" ></div>
						</div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">${I18n.change_pwd_field_repeat_pwd}<font color="red">*</font></label>
							<div class="col-sm-10"><input type="password" class="form-control" name="repeatPassword" placeholder="${I18n.system_please_input} ${I18n.change_pwd_field_repeat_pwd}" maxlength="50" ></div>
						</div>
						<hr>
						<div class="form-group">
							<div class="col-sm-offset-3 col-sm-6">
								<button type="submit" class="btn btn-primary"  >${I18n.system_save}</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">${I18n.system_cancel}</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

</#macro>

<#macro commonLeft pageName >
	<!-- Left side column. contains the logo and sidebar -->
	<aside class="main-sidebar">
		<!-- sidebar: style can be found in sidebar.less -->
		<section class="sidebar">
			<!-- sidebar menu: : style can be found in sidebar.less -->
			<ul class="sidebar-menu">
                <li class="header">${I18n.system_nav}</li>
                <li class="nav-click <#if pageName == "index">active</#if>" ><a href="${request.contextPath}/"><i class="fa fa-circle-o text-aqua"></i><span>${I18n.job_dashboard_name}</span></a></li>
				<li class="nav-click <#if pageName == "jobinfo">active</#if>" ><a href="${request.contextPath}/jobinfo"><i class="fa fa-circle-o text-yellow"></i><span>${I18n.jobinfo_name}</span></a></li>
				<li class="nav-click <#if pageName == "joblog">active</#if>" ><a href="${request.contextPath}/joblog"><i class="fa fa-circle-o text-green"></i><span>${I18n.joblog_name}</span></a></li>
				<#if loginUser.role == 1>
                    <li class="nav-click <#if pageName == "jobgroup">active</#if>" ><a href="${request.contextPath}/jobgroup"><i class="fa fa-circle-o text-red"></i><span>${I18n.jobgroup_name}</span></a></li>
                    <li class="nav-click <#if pageName == "user">active</#if>" ><a href="${request.contextPath}/user"><i class="fa fa-circle-o text-purple"></i><span>${I18n.user_manage}</span></a></li>
					<li class="nav-click <#if pageName == "emailconfig">active</#if>" ><a href="${request.contextPath}/emailconfig"><i class="fa fa-circle-o text-orange"></i><span>${I18n.emailconfig_name}</span></a></li>
				</#if>
				<li class="nav-click <#if pageName == "help">active</#if>" ><a href="${request.contextPath}/help"><i class="fa fa-circle-o text-gray"></i><span>${I18n.job_help}</span></a></li>
			</ul>
		</section>
		<!-- /.sidebar -->
	</aside>
</#macro>

<#macro commonControl >
	<!-- Control Sidebar -->
	<aside class="control-sidebar control-sidebar-dark">
		<!-- Create the tabs -->
		<ul class="nav nav-tabs nav-justified control-sidebar-tabs">
			<li class="active"><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
			<li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
		</ul>
		<!-- Tab panes -->
		<div class="tab-content">
			<!-- Home tab content -->
			<div class="tab-pane active" id="control-sidebar-home-tab">
				<h3 class="control-sidebar-heading">近期活动</h3>
				<ul class="control-sidebar-menu">
					<li>
						<a href="javascript::;">
							<i class="menu-icon fa fa-birthday-cake bg-red"></i>
							<div class="menu-info">
								<h4 class="control-sidebar-subheading">张三今天过生日</h4>
								<p>2015-09-10</p>
							</div>
						</a>
					</li>
					<li>
						<a href="javascript::;">
							<i class="menu-icon fa fa-user bg-yellow"></i>
							<div class="menu-info">
								<h4 class="control-sidebar-subheading">Frodo 更新了资料</h4>
								<p>更新手机号码 +1(800)555-1234</p>
							</div>
						</a>
					</li>
					<li>
						<a href="javascript::;">
							<i class="menu-icon fa fa-envelope-o bg-light-blue"></i>
							<div class="menu-info">
								<h4 class="control-sidebar-subheading">Nora 加入邮件列表</h4>
								<p>nora@example.com</p>
							</div>
						</a>
					</li>
					<li>
						<a href="javascript::;">
						<i class="menu-icon fa fa-file-code-o bg-green"></i>
						<div class="menu-info">
							<h4 class="control-sidebar-subheading">001号定时作业调度</h4>
							<p>5秒前执行</p>
						</div>
						</a>
					</li>
				</ul>
				<!-- /.control-sidebar-menu -->
			</div>
			<!-- /.tab-pane -->

			<!-- Settings tab content -->
			<div class="tab-pane" id="control-sidebar-settings-tab">
				<form method="post">
					<h3 class="control-sidebar-heading">个人设置</h3>
					<div class="form-group">
						<label class="control-sidebar-subheading"> 左侧菜单自适应
							<input type="checkbox" class="pull-right" checked>
						</label>
						<p>左侧菜单栏样式自适应</p>
					</div>
					<!-- /.form-group -->

				</form>
			</div>
			<!-- /.tab-pane -->
		</div>
	</aside>
	<!-- /.control-sidebar -->
	<!-- Add the sidebar's background. This div must be placed immediately after the control sidebar -->
	<div class="control-sidebar-bg"></div>
</#macro>

<#macro commonFooter >
	<footer class="main-footer">
        Powered by <b>XXL-JOB</b> ${I18n.admin_version}
		<div class="pull-right hidden-xs">
            <strong>Copyright &copy; 2015-${.now?string('yyyy')} &nbsp;
                <a href="https://www.xuxueli.com/" target="_blank" >xuxueli</a>
				&nbsp;
                <a href="https://github.com/xuxueli/xxl-job" target="_blank" >github</a>
            </strong><!-- All rights reserved. -->
		</div>
	</footer>
</#macro>
