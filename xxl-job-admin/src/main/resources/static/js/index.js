$(function (){
    let account = sessionStorage.getItem('currentUser');
    $("#current").text(account);
})

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



















