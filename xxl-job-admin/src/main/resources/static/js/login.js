$(function () {
    $("#create").click(function () {
        checkRegister();
        return false;
    })
    $("#login").click(function () {
        checkLogin();
        return false;
    })
    $('.message a').click(function () {
        $('form').animate({
            height: 'toggle',
            opacity: 'toggle'
        }, 'slow');
    });
})

function checkLogin() {
    let account = $("#account").val();
    let password = $("#password").val();

    if (_.isNil(account) || _.isEmpty(account)) {
        message.error("账号不能为空");
        return false;
    }

    if (_.isNil(password) || _.isEmpty(password)) {
        message.error("密码不能为空");
        return false;
    }

    let login = {
        "account": account,
        "password": password,
    }

    // let res = http.post("auth/login", login);

    $.ajax({
        url: "auth/login",
        method: "POST",
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(login),
        success: function(res, status, xhr) {
            if (isSuccess(res.code)) {
                message.info('恭喜你, 登录成功');
                $("#account").val("");
                $("#password").val("");
                sessionStorage.setItem('currentUser', account);
                $.cookie("AUTHORIZATION", xhr.getResponseHeader('Authorization'));
                $(location).attr('href', 'index');
            }else {
                message.error(res.message);
                $("#login_form").removeClass('shake_effect');
                setTimeout(function () {
                    $("#login_form").addClass('shake_effect')
                }, 1);
            }
        },
    });
}

function checkRegister() {
    var account = $("#r_account").val();
    var name = $("#r_name").val();
    var password = $("#r_password").val();
    var email = $("#r_email").val();

    if (_.isNil(account) || _.isEmpty(account)) {
        message.error("账号不能为空");
        return false;
    }

    if (_.isNil(name) || _.isEmpty(name)) {
        message.error("姓名不能为空");
        return false;
    }

    if (_.isNil(password) || _.isEmpty(password)) {
        message.error("密码不能为空");
        return false;
    }

    if (!new RegExp("(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*()_.]+)$)^[\\w~!@#$%^&*()_.]{6,16}$").test(password)) {
        message.error('密码应为字母，数字，特殊符号(~!@#$%^&*()_.)，两种及以上组合，6-16位');
        return false;
    }

    let login = {
        "account": account,
        "password": password,
        "mail": email,
        "name": name,
    }

    let res = http.post("auth/register", login);
    if (isSuccess(res.code)) {
        message.info('恭喜你, 注册成功');
        $("#r_account").val('');
        $("#r_name").val('');
        $("#r_password").val('');
        $("#r_email").val('');
    }else {
        message.error(res.message);
        $("#login_form").removeClass('shake_effect');
        setTimeout(function () {
            $("#login_form").addClass('shake_effect')
        }, 1);
    }
}

