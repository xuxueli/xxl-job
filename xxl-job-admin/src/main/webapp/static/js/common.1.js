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
	
});
