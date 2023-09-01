var message = (function () {
    var notice;
    layui.config({
        base: 'plugins/notice/'
    })
    layui.use(['notice'], function () {
        notice = layui.notice;
        // 初始化配置，同一样式只需要配置一次，非必须初始化，有默认配置
        notice.options = {
            closeButton: false,//显示关闭按钮
            debug: false,//启用debug
            positionClass: "toast-top-right",//弹出的位置,
            showDuration: "300",//显示的时间
            hideDuration: "1000",//消失的时间
            timeOut: "2000",//停留的时间
            extendedTimeOut: "1000",//控制时间
            showEasing: "swing",//显示时的动画缓冲方式
            hideEasing: "linear",//消失时的动画缓冲方式
            iconClass: 'toast-info', // 自定义图标，有内置，如不需要则传空 支持layui内置图标/自定义iconfont类名
            onclick: null, // 点击关闭回调
        };
    });
    function warning (msg) {
        notice.warning(msg);
    }
    function info (msg) {
        notice.info(msg);
    }
    function error (msg) {
        notice.error(msg);
    }
     function success (msg) {
        notice.success(msg);
    }
     return {
         warning: warning,
         info: info,
         error: error,
         success: success,
     }
})();



