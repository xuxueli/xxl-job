$(function (){
    let account = sessionStorage.getItem('currentUser');
    $("#current").text(account);
})

function getTime(){
    var myDate = new Date();
    var myYear = myDate.getFullYear(); //获取完整的年份(4位,1970-????)
    var myMonth = myDate.getMonth()+1; //获取当前月份(0-11,0代表1月)
    var myToday = myDate.getDate(); //获取当前日(1-31)
    var myDay = myDate.getDay(); //获取当前星期X(0-6,0代表星期天)
    var myHour = myDate.getHours(); //获取当前小时数(0-23)
    var myMinute = myDate.getMinutes(); //获取当前分钟数(0-59)
    var mySecond = myDate.getSeconds(); //获取当前秒数(0-59)
    var week = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];
    var nowTime;

    nowTime = myYear+'-'+fillZero(myMonth)+'-'+fillZero(myToday)+'&nbsp;&nbsp;'+week[myDay]+'&nbsp;&nbsp;'+fillZero(myHour)+':'+fillZero(myMinute)+':'+fillZero(mySecond);
    //console.log(nowTime);
    $('#time').html(nowTime);
}
function fillZero(str){
    var realNum;
    if(str<10){
        realNum	= '0'+str;
    }else{
        realNum	= str;
    }
    return realNum;
}
setInterval(getTime,1000);

/**
 * 修改密码
 */
function updatePwd() {
    let currentUser = sessionStorage.getItem('currentUser');
    let form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.6) + 'px', ($(window).height() - 400) + 'px'],
        fix: false, //不固定
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: '修改密码',
        content: $('#update-pwd-form'),
        success: function (index) {
            form.val("layui-update-pwd-form", {
                "account": currentUser,
            });
            form.render("select");
        }
    });
    form.on('submit(updatePwd)', function (new_obj) {
        let field = new_obj.field;
        if (_.isEqual(field.oldPwd, field.newPwd)) {
            error("新旧密码不能一致");
            return false;
        }

        if (!_.isEqual(field.newPwd, field.confirmPwd)) {
            error("两次输入不一致");
            return false;
        }

        let param = {
            "account": currentUser,
            "oldPwd": field.oldPwd,
            "newPwd": field.newPwd,
        }

        let res = patchBody("/userInfo/pwd", param);
        if (!isSuccess(res.code)) {
            error(res.message);
            return false;
        }
        return true;
    });
}

/**
 * 校验字段
 * @param form 表单对象
 */
function validate(form) {
    form.verify({
        account: function (value, item) {
            if (!new RegExp("^[a-zA-Z0-9_\\s·]+$").test(value)) {
                return '账号只能由字母,数字,_ 组成';
            }
            if (/(^_)|(__)|(_+$)/.test(value)) return '账号首尾不能出现 _ 下划线';
        },
        name: function (value, item) {
            if (!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)) {
                return '姓名不能有特殊字符';
            }
            if (/(^_)|(__)|(_+$)/.test(value)) return '姓名首尾不能出现 _ 下划线';
        },
        password: function (value, item) {
            if (!new RegExp("(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*()_.]+)$)^[\\w~!@#$%^&*()_.]{6,16}$").test(value)) {
                return '密码应为字母，数字，特殊符号(~!@#$%^&*()_.)，两种及以上组合，6-16位';
            }
        }
    });
}

/**
 * 登出
 */
function logout() {
    sessionStorage.removeItem('currentUser');
    get("/auth/logout");
    $(location).attr('href', 'login');
}



















