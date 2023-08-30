let notice;

$(function (){
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
})

function ts2Time(timestamp) {
    if (_.isNil(timestamp) || _.isEqual(timestamp, '')) {
        return '';
    }
    if (typeof timestamp === 'string') {
        timestamp = Number(timestamp);
    }
    if (typeof timestamp !== 'number') {
        alert("输入参数无法识别为时间戳");
    }
    let date = new Date(timestamp);
    let Y = date.getFullYear() + '-';
    let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    let D = (String(date.getDate()).length < 2 ? '0' + String(date.getDate()) : date.getDate()) + ' ';
    let h = (String(date.getHours()).length < 2 ? '0' + String(date.getHours()) : date.getHours()) + ':';
    let m = (String(date.getMinutes()).length < 2 ? '0' + String(date.getMinutes()) : date.getMinutes()) + ':';
    let s = String(date.getSeconds()).length < 2 ? '0' + String(date.getSeconds()) : date.getSeconds();
    return Y + M + D + h + m + s;
}

function date2Timestamp(date) {
    if (!_.isNil(date) && !_.isEmpty(date)) {
        return new Date(date).getTime();
    }
    return null;
}

function warning(msg) {
    notice.warning(msg);
}

function info(msg) {
    notice.info(msg);
}

function error(msg) {
    notice.error(msg);
}

function success(msg) {
    notice.success(msg);
}

function isSuccess(code) {
    return code === 0;
}

function nowDate() {
    var date = new Date();
    var sign2 = ":";
    var year = date.getFullYear() // 年
    var month = date.getMonth() + 1; // 月
    var day = date.getDate(); // 日
    var hour = date.getHours(); // 时
    var minutes = date.getMinutes(); // 分
    var seconds = date.getSeconds() //秒
    var weekArr = ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期天'];
    var week = weekArr[date.getDay()];
    // 给一位数的数据前面加 “0”
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (day >= 0 && day <= 9) {
        day = "0" + day;
    }
    if (hour >= 0 && hour <= 9) {
        hour = "0" + hour;
    }
    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (seconds >= 0 && seconds <= 9) {
        seconds = "0" + seconds;
    }
    return year + "-" + month + "-" + day + " " + hour + sign2 + minutes + sign2 + seconds;
}

function initDate(divId) {
    layui.use('laydate', function () {
        let layDate = layui.laydate;
        layDate.render({
            type: 'datetime',
            elem: divId,
        });
    });
}

























