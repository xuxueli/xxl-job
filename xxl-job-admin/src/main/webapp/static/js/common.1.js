$(function(){
	
	// 导航栏,选中样式处理，js遍历匹配url（遗弃）
	$(".nav-click").removeClass("active");
	$(".nav-click").each(function(){
		if( window.location.href.indexOf( $(this).find("a").attr("href") ) > -1){
			$(this).addClass("active");
			$(this).parents(".nav-click").addClass("active");
		}
	});
	
	// scrollup
	$.scrollUp({
		animation: 'fade',	// fade/slide/none
		scrollImg: true
	});
	
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
