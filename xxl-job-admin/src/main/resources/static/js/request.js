$(function () {
    $.ajaxSetup({
        async: false,
        // headers: {
        //     'Authorization': 'Bearer ' + token
        // }
    });
})

function get(url, params) {
    let data;
    $.ajax({
        method: "GET",
        url: url,
        async: false,
        data: params,
        success: function(res) {
            data = callback(res);
        }
    });
    return data;
}

function post(url, params) {
    let data;
    $.ajax({
        method: "POST",
        url: url,
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(params),
        success: function(res) {
            data = res;
        }
    });
    return data;
}

function put(url, params) {
    let data;
    $.ajax({
        method: "PUT",
        url: url,
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(params),
        success: function(res) {
            data = res;
        }
    });
    return data;
}

function getPath(url, param) {
    let data;
    $.ajax({
        method: "GET",
        url: url + "/" + param,
        async: false,
        contentType: 'application/json',
        success: function(res) {
            data = res;
        }
    });
    return data;
}

function delPath(url, param) {
    let data;
    $.ajax({
        method: "DELETE",
        url: url + "/" + param,
        async: false,
        contentType: 'application/json',
        success: function(res) {
            data = callback(res);
        }
    });
    return data;
}

function patchPath(url, param) {
    let data;
    $.ajax({
        method: "PATCH",
        url: url + "/" + param,
        async: false,
        contentType: 'application/json',
        success: function(res) {
            data = callback(res);
        }
    });
    return data;
}

function patchBody(url, body) {
    let data;
    $.ajax({
        method: "PATCH",
        url: url,
        async: false,
        contentType: 'application/json',
        data: JSON.stringify(body),
        success: function(res) {
            data = res;
        }
    });
    return data;
}

function delBody(url, body) {
    let data;
    $.ajax({
        method: "DELETE",
        url: url,
        async: false,
        data: JSON.stringify(body),
        contentType: 'application/json',
        success: function(res) {
            data = callback(res);
        }
    });
    return data;
}

function callback(res) {
    if (res.code !== 0) {
        error(res.message);
        return false;
    }
    return res.data;
}





















