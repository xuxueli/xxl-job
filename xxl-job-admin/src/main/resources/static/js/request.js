var http = (function () {
    $.ajaxSetup({
        async: false,
    });

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

    function postFormData(url, formData) {
        let data;
        $.ajax({
            method: "POST",
            url: url,
            async: false,
            processData: false,  // 不处理数据
            contentType: false,   // 不设置内容类型
            data: formData,
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

    function getPath(url) {
        let data;
        $.ajax({
            method: "GET",
            url: url,
            async: false,
            contentType: 'application/json',
            success: function(res) {
                data = callback(res);
            }
        });
        return data;
    }

    function delPath(url) {
        let data;
        $.ajax({
            method: "DELETE",
            url: url,
            async: false,
            contentType: 'application/json',
            success: function(res) {
                data = callback(res);
            }
        });
        return data;
    }

    function patchPath(url) {
        let data;
        $.ajax({
            method: "PATCH",
            url: url,
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
            message.error(res.message);
            return false;
        }
        return res.data;
    }

    return {
        get: get,
        post: post,
        put: put,
        getPath: getPath,
        delPath: delPath,
        patchPath: patchPath,
        patchBody: patchBody,
        delBody: delBody,
        postFormData: postFormData,
    }
})();



















































