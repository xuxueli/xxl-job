$(function(){
	
	// 导航栏,选中样式处理
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
		$.post(base_url + "/logout", function(data, status) {
			if (data.code == "S") {
				ComAlert.show(1, "注销成功", function(){
					//window.location.reload();
					window.location.href = base_url;
				});
			} else {
				ComAlert.show(1, data.msg);
			}
		});
	});
	
	// adminlte_settings
	$('.sidebar-toggle').click(function(){
		var adminlte_settings = $.cookie('adminlte_settings');
		if ('off' == adminlte_settings) {
			adminlte_settings = 'on';
		} else {
			adminlte_settings = 'off';
		}
		$.cookie('adminlte_settings', adminlte_settings, { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
	});
	var adminlte_settings = $.cookie('adminlte_settings');
	if (adminlte_settings == 'off') {
		$('body').addClass('sidebar-collapse');
	}
});
