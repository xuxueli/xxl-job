<#-- import: style -->
<#macro commonStyle>

<#-- i18n -->
<#global I18n = I18nUtil.getMultString()?eval />
<#-- title、favicon、meta -->
<title>${I18n.admin_name_full}</title>
<link rel="icon" href="${request.contextPath}/static/favicon.ico" />
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<#-- css -->
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/Ionicons/css/ionicons.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE.min.css">
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
<link rel="stylesheet" href="${request.contextPath}/static/plugins/nprogress/nprogress.css">

</#macro>

<#-- import: script -->
<#macro commonScript>

<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap/js/bootstrap.min.js"></script>
<script src="${request.contextPath}/static/adminlte/dist/js/adminlte.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/jquery-slimscroll/jquery.slimscroll.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/fastclick/fastclick.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/plugins/layer/layer.js"></script>
<script src="${request.contextPath}/static/plugins/nprogress/nprogress.js"></script>
<script src="${request.contextPath}/static/plugins/fullscreen/jquery.fullscreen.js"></script>
<script>
	// init page param
	var base_url = '${request.contextPath}';
	var I18n = ${I18nUtil.getMultString()};
</script>


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
			color: white !important;
		}
		.sidebar-menu>li>a {
			color: white !important;
		}
		.sidebar-menu>li:hover>a,
		.sidebar-menu>li.active>a,
		.sidebar-menu>li.menu-open>a {
			background: #1e282caa !important;
			color: #ccc !important;
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
	<script src="${request.contextPath}/static/platform/security/sm-crypto-0.3.2/sm2.js"></script>
	<script src="${request.contextPath}/static/platform/security/sm-crypto-0.3.2/sm3.js"></script>
	<script src="${request.contextPath}/static/platform/security/sm-crypto-0.3.2/sm4.js"></script>
</#macro>

