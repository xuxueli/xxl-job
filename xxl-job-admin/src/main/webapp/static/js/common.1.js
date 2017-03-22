$(function(){

	// logout
	$("#logoutBtn").click(function(){
		ComConfirm.show("确认注销登录?", function(){
			$.post(base_url + "/logout", function(data, status) {
				if (data.code == "200") {
					ComAlert.show(1, "注销成功", function(){
						window.location.href = base_url + "/";
					});
				} else {
					ComAlert.show(1, data.msg);
				}
			});
		});
	});

	// slideToTop
	var slideToTop = $("<div />");
	slideToTop.html('<i class="fa fa-chevron-up"></i>');
	slideToTop.css({
		position: 'fixed',
		bottom: '20px',
		right: '25px',
		width: '40px',
		height: '40px',
		color: '#eee',
		'font-size': '',
		'line-height': '40px',
		'text-align': 'center',
		'background-color': '#222d32',
		cursor: 'pointer',
		'border-radius': '5px',
		'z-index': '99999',
		opacity: '.7',
		'display': 'none'
	});
	slideToTop.on('mouseenter', function () {
		$(this).css('opacity', '1');
	});
	slideToTop.on('mouseout', function () {
		$(this).css('opacity', '.7');
	});
	$('.wrapper').append(slideToTop);
	$(window).scroll(function () {
		if ($(window).scrollTop() >= 150) {
			if (!$(slideToTop).is(':visible')) {
				$(slideToTop).fadeIn(500);
			}
		} else {
			$(slideToTop).fadeOut(500);
		}
	});
	$(slideToTop).click(function () {
		$("body").animate({
			scrollTop: 0
		}, 100);
	});

	// 左侧菜单状态，js + 后端 + cookie方式（新）
	$('.sidebar-toggle').click(function(){
		var adminlte_settings = $.cookie('adminlte_settings');	// 左侧菜单展开状态[adminlte_settings]：on=展开，off=折叠
		if ('off' == adminlte_settings) {
			adminlte_settings = 'on';
		} else {
			adminlte_settings = 'off';
		}
		$.cookie('adminlte_settings', adminlte_settings, { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
	});
	// 左侧菜单状态，js + cookie方式（遗弃）
	/*
	 var adminlte_settings = $.cookie('adminlte_settings');
	 if (adminlte_settings == 'off') {
	 $('body').addClass('sidebar-collapse');
	 }
	 */
	
});
