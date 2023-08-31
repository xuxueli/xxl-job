$(function () {
    search();
    okLoading.close(layui.jquery);
})

/**
 * 新增
 */
function add() {
    var form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.7) + 'px', ($(window).height() - 200) + 'px'],
        fix: false, //不固定
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: '新增用户',
        content: $('#add-form'),
        success: function (index) {
            form.render();
            validate(form);
            form.on('submit(add)', function (data) {
                let field = data.field;
                let res = post("/userInfo", field);
                if (!isSuccess(res.code)) {
                    error(res.message);
                    return false;
                }
                return true;
            });
        }
    });
}

/**
 * 查询数据
 */
function search() {
    let page = pageSearch(1, 50);
    createTable(page.records);
    loadPage(page.total);
}

/**
 * 创建表格
 * @param records 记录
 */
function createTable(records) {
    layui.use('table', function () {
        var table = layui.table;

        // 已知数据渲染
        table.render({
            elem: '#table-data',
            //标题栏
            cols: [[
                {
                    fixed: 'left',
                    type: 'checkbox',
                    toolbar: '<div class="layui-unselect layui-form-checkbox" lay-skin="primary"><i class="layui-icon" onclick="delAll()">&#xe605;</i></div> '
                },
                {field: 'account', title: '账号', sort: true},
                {field: 'name', title: '姓名'},
                {field: 'mail', title: '邮箱'},
                {
                    field: 'sex', title: '性别', width: 60, templet: function (d) {
                        if (!_.isNil(d.sex)) {
                            if (d.sex === '1') {
                                return '<span style="color: blue">♂</span>';
                            } else {
                                return '<span style="color: pink">♀</span>';
                            }
                        }
                        return '';
                    }
                },
                {field: 'telephone', title: '电话', sort: true},
                {
                    field: 'status', title: '状态',
                    templet: function (row) {
                        let status = '启用';
                        if (row.status === 0) {
                            status = "已过期";
                        } else if (row.status === 1) {
                            status = "启用";
                        } else if (row.status === -1) {
                            status = "禁用";
                        }
                        return status;
                    }
                },
                {field: 'createdUser', title: '添加人'},
                {
                    field: 'createdTime', title: '添加时间', sort: true,
                    templet: function (row) {
                        return ts2Time(row.createdTime);
                    }
                },
                {
                    fixed: 'right', width: 300, title: '操作', toolbar: '<div class="td-manage">\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-blue" lay-event="update" >编辑\n' +
                        '              </a>\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-orange" lay-event="updatePwd" >修改密码\n' +
                        '              </a>\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-purple" lay-event="resetPwd" >重置密码\n' +
                        '              </a>\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-red" lay-event="delete">删除\n' +
                        '              </a>\n' +
                        '            </div>'
                }
            ]],
            data: records,
            // skin: 'line', // 表格风格
            even: true,// 是否开启隔行背景
            page: false, // 是否显示分页
        });

        table.on('tool(table-data)', function (obj) {
            let data = obj.data;
            let layEvent = obj.event;
            if ("update" === layEvent) {
                update(data);
            } else if ("delete" === layEvent) {
                deleteData(data);
            } else if ("updatePwd" === layEvent) {
                updatePwd(data);
            } else if ("resetPwd" === layEvent) {
                resetPwd(data);
            }
        });
    });
}

function resetPwd(data) {
    layer.confirm('<div><span>"确认要重置吗？"</span><span class="x-red">, 默认密码: ABCabc123456</span></div>',
        function (index) {
            patchPath("/userInfo", data.account);
            layer.close(index);
            search();
        });
}

/**
 * 修改密码
 * @param data
 */
function updatePwd(data) {
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
                "account": data.account,
            });
            form.render("select");
        }
    });
    validate(form);
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
            "account": data.account,
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
 * 加载分页数据
 * @param total 总数
 */
function loadPage(total) {
    layui.use(function () {
        var laypage = layui.laypage;
        laypage.render({
            elem: 'page', // 元素 id
            limit: 50, // 每页显示的条数
            count: total, // 数据总数
            limits: [50, 100, 150],//每页条数的选择项
            layout: ['count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip'], // 功能布局
            jump: function (obj, first) {
                if (!first) {
                    let page = pageSearch(obj.curr, obj.limit);
                    createTable(page.records);
                }
            }
        });
    });
}

/**
 * 分页查询
 * @param currentPage 当前页
 * @param pageSize 页大小
 * @returns {*} 分页数据
 */
function pageSearch(currentPage, pageSize) {
    let account = $("#account").val();
    let name = $("#name").val();
    let mail = $("#mail").val();
    let sex = $("#select-sex").find("option:selected").val();
    let telephone = $("#telephone").val();

    let pageDTO = {
        'currentPage': currentPage,
        'pageSize': pageSize,
        'account': account,
        'name': name,
        'mail': mail,
        'sex': sex,
        'telephone': telephone,
    };
    return get("/userInfo", pageDTO);
}

/**
 * 修改
 */
function update(data) {
    let form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.7) + 'px', ($(window).height() - 200) + 'px'],
        fix: false, //不固定
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: '修改用户',
        content: $('#update-form'),
        success: function (index) {
            form.val("layui-update-form", {
                "account": data.account,
                "password": data.password,
                "name": data.name,
                "sex": data.sex,
                "mail": data.mail,
                "telephone": data.telephone,
                "description": data.description,
            });
            form.render("select");
        }
    });
    validate(form);
    form.on('submit(update)', function (new_obj) {
        let field = new_obj.field;
        field.id = data.id;
        let res = put("/userInfo", field);
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
 * 删除所有
 */
function delAll() {
    var datas = layui.table.checkStatus('table-data').data;
    if (!_.isNil(datas) && !_.isEmpty(datas)) {
        let ids = [];
        datas.forEach(a => {
            ids.push(a.id);
        })
        layer.confirm('确认要删除吗？', function (index) {
            delBody("/userInfo/batch", ids);
            layer.close(index);
            search();
        });
    }
}

/**
 * 删除
 * @param obj 数据
 */
function deleteData(obj) {
    layer.confirm('确认要删除吗？', function (index) {
        delPath("/userInfo", obj.id);
        layer.close(index);
        search();
    });
}

/**
 * 清空条件
 */
function clean() {
    $("#account").val('');
    $("#mail").val('');
    $("#name").val('');
    $("#telephone").val('');
    $('#select-sex').val('').trigger('chosen:updated');
    search();
}
















