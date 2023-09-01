$(function () {
    initDate('#start');
    initDate('#end');
    search();
})

/**
 * 创建表格
 * @param records 记录
 */
function createTable(records) {
    layui.use('table', function () {
        var table = layui.table;
        var from = layui.form;
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
                {field: 'appName', title: 'AppName'},
                {field: 'title', title: '名称'},
                {
                    field: 'addressType', title: '注册方式', width: 100, templet: function (d) {
                        if (!_.isNil(d.addressType)) {
                            if (d.addressType === '1') {
                                return '手动录入';
                            } else {
                                return '自动注册';
                            }
                        }
                        return '';
                    }
                },
                {field: 'addresses', title: 'OnLine 机器地址'},
                {field: 'createdUser', width: 100 ,title: '添加人'},
                {
                    field: 'createdTime', title: '添加时间', sort: true,
                    templet: function (row) {
                        return ts2Time(row.createdTime)
                    }
                },
                {
                    fixed: 'right', width: 140, title: '操作', toolbar: '<div class="td-manage">\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-blue" lay-event="update" >编辑\n' +
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
            }
        });
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
 * 删除
 * @param obj 数据
 */
function deleteData(obj) {
    layer.confirm('确认要删除吗？', function (index) {
        delPath("/group", obj.id);
        layer.close(index);
        search();
    });
}

/**
 * 分页查询
 * @param currentPage 当前页
 * @param pageSize 页大小
 * @returns {*} 分页数据
 */
function pageSearch(currentPage, pageSize) {
    let start = date2Timestamp($("#start").val());
    let end = date2Timestamp($("#end").val());
    let title = $("#title").val();
    let appName = $("#appName").val();
    let pageDTO = {
        'currentPage': currentPage,
        'pageSize': pageSize,
        'startTime': start,
        'endTime': end,
        'title': title,
        'appName': appName
    };
    return get("/group", pageDTO);
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
            delBody("/group/batch", ids);
            layer.close(index);
            search();
        });
    }
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
        title: '新增执行器',
        content: $('#add-form'),
        success: function (index) {
            setAddressReadonly(form, '#key-addressList');
            form.render();
        }
    });

    validate(form);
    form.on('submit(add)', function (data) {
        let field = data.field;
        let res = post("/group", field);
        if (!isSuccess(res.code)) {
            message.error(res.message);
            return false;
        }
        return true;
    });
}

function setAddressReadonly(from, divId) {
    from.on('radio(addressType-radio-filter)', function(data){
        var elem = data.elem;
        var value = elem.value;
        if (value == 0) {
            $(divId).css("background-color", "#eee");
            $(divId).attr("readonly","readonly");
            $(divId).val("");
        } else {
            $(divId).css("background-color", "white");
            $(divId).removeAttr("readonly");
        }
    });
}

/**
 * 修改
 */
function update(data) {
    let form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.7) + 'px', ($(window).height() - 200) + 'px'],
        fix: false,
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: '修改执行器',
        content: $('#update-form'),
        success: function () {
            setAddressReadonly(form, '#update-addressList');
            form.val("layui-update-form", {
                "appName": data.appName,
                "title": data.title,
                "addressType": data.addressType,
                "addresses": data.addresses,
                "description": data.description,
            });
            form.render();
        }
    });

    validate(form);
    form.on('submit(update)', function (new_obj) {
        let field = new_obj.field;
        field.id = data.id;
        field.addresses = str2List(field.addresses);
        let res = put("/group", field);
        if (!isSuccess(res.code)) {
            message.error(res.message);
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
        appName: function (value, item) {
            if (/(^_)|(__)|(_+$)/.test(value)) return 'AppName首尾不能出现 _ 下划线';
            if (/^\d+$/.test(value)) return 'AppName不能全为数字';
        },
        title: function (value, item) {
            if (/(^_)|(__)|(_+$)/.test(value)) return '名称首尾不能出现 _ 下划线';
            if (/^\d+$/.test(value)) return '名称不能全为数字';
        },
    });
}

/**
 * 清空条件
 */
function clean() {
    $("#start").val('');
    $("#end").val('');
    $("#title").val('');
    $("#appName").val('');
    search();
}


