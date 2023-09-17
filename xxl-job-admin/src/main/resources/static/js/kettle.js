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
                {field: 'name', title: '名称'},
                {field: 'version', width: 60, title: '版本'},
                {
                    field: 'type', title: '类型', width: 100,
                    templet: function (row) {
                        var type = row.type;
                        if (_.eq('KTR', type)) {
                            return "转换模型";
                        }else {
                            return "作业模型";
                        }
                    }
                },
                {field: 'fileName', title: '文件名'},
                {
                    field: 'logLevel', title: '日志级别', width: 100,
                    templet: function (row) {
                        var logLevel = row.logLevel;
                        if (_.eq('NOTHING', logLevel)) {
                            return '没有日志';
                        } else if (_.eq('ERROR', logLevel)) {
                            return '错误日志';
                        } else if (_.eq('MINIMAL', logLevel)) {
                            return '最小日志';
                        } else if (_.eq('BASIC', logLevel)) {
                            return '基本日志';
                        } else if (_.eq('DETAILED', logLevel)) {
                            return '详细日志';
                        } else if (_.eq('DEBUG', logLevel)) {
                            return '调试';
                        } else if (_.eq('ROWLEVEL', logLevel)) {
                            return '行级日志';
                        }
                        return '基本日志';
                    }
                },
                {
                    field: 'status', title: '状态', width: 90, templet: function (row) {
                        let kettleId = row.id;
                        if (row.status == 0) {
                            return "<input type='checkbox'  kettleId = '" + kettleId + "' lay-filter='kettle-status-filter' lay-skin='switch' lay-text='ON|OFF'>"
                        } else {
                            return "<input type='checkbox'  kettleId = '" + kettleId + "'  lay-filter='kettle-status-filter' lay-skin='switch' lay-text='ON|OFF' checked>"
                        }
                    }
                },
                {field: 'guideKjb', title: 'kjb引导文件'},
                // {field: 'createdUser', width: 100 ,title: '添加人'},
                // {
                //     field: 'createdTime', title: '添加时间', sort: true,
                //     templet: function (row) {
                //         return ts2Time(row.createdTime)
                //     }
                // },
                {
                    fixed: 'right', width: 240, title: '操作', toolbar: '<div class="td-manage">\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-blue" lay-event="update" >版本升级\n' +
                        '              </a>\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-btn-primary" lay-event="download" >下载文件\n' +
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

        from.on('switch(kettle-status-filter)', function (row) {
            updateStatus(row.elem.attributes['kettleId'].nodeValue, row.elem.checked);
        });

        table.on('tool(table-data)', function (obj) {
            let data = obj.data;
            let layEvent = obj.event;
            if ("update" === layEvent) {
                update(data);
            } else if ("delete" === layEvent) {
                deleteData(data);
            }else if ("download" === layEvent) {
                download(data);
            }
        });
    });
}

/**
 * 下载
 * @param data 数据
 */
function download(data) {
    window.open("kettle-info/download/" + data.id, "_blank")
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
        http.delPath("kettle-info/" + obj.id);
        layer.close(index);
        search();
    });
}

/**
 * 修改状态
 * @param id 主键
 * @param status 状态
 */
function updateStatus(id, status) {
    if (status) {
        http.patchPath('kettle-info/' + id + "/" + 1);
    } else {
        http.patchPath('kettle-info/' + id + "/" + 0);
    }
    pageSearch(1, 50);
}

/**
 * 分页查询
 * @param currentPage 当前页
 * @param pageSize 页大小
 * @returns {*} 分页数据
 */
function pageSearch(currentPage, pageSize) {

    let type = $("#select-type").find("option:selected").val();
    let status = $("#select-status").find("option:selected").val();
    let logLevel = $("#select-log-level").find("option:selected").val();
    let name = $("#name").val();

    let start = date2Timestamp($("#start").val());
    let end = date2Timestamp($("#end").val());
    if (!_.isNull(end)) {
        end = end + 999;
    }
    let pageDTO = {
        'currentPage': currentPage,
        'pageSize': pageSize,
        'startTime': start,
        'endTime': end,
        'name': name,
        'type': type,
        'status': status,
        'logLevel': logLevel,
    };

    return http.get("kettle-info", pageDTO);
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
            http.delBody("kettle-info/batch", ids);
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
        title: '新增模型',
        content: $('#add-kettle'),
        success: function (index) {
            typeSelect("#guide-kjb", "KTR");
            form.render();
        },
        cancel: function (index, layero, that) {
            $("#add-kettle-form")[0].reset();
            form.render();
            return true;
        },
    });

    changeTypeFilter(form, "add-type-filter", "#guide-kjb");

    validate(form);
    form.on('submit(add)', function (data) {
        let field = data.field;
        let formData = new FormData();
        for(let key in field) {
            formData.append(key, field[key]);
        }
        formData.set("file", document.getElementById('model-upload').files[0]);
        if (_.eq("KJB", field.type)) {
            var kjbFile = document.getElementById('guide-kjb-upload').files[0]
            if (_.isNil(kjbFile) || _.isNull(kjbFile)) {
                message.warning("KJB引导文件不能为空");
                return false;
            }else {
                field.guideKjb = kjbFile.name;
            }
        }
        let res = http.postFormData("kettle-info", formData);
        if (!isSuccess(res.code)) {
            message.error(res.message);
            return false;
        }
        return true;
    });
}

/**
 * 修改
 */
function update(data) {
    var form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.7) + 'px', ($(window).height() - 200) + 'px'],
        fix: false, //不固定
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: '模型版本升级',
        content: $('#update-kettle'),
        success: function (index) {
            form.val("layui-update-kettle-form", {
                "name": data.name,
                "type": data.type,
                "file": data.fileName,
                "guideKjb": data.guideKjb,
                "logLevel": data.logLevel,
                "status": data.status,
            });
            $('#update-name').attr('readonly', true);
            $('#update-type').attr("disabled", "disabled");
            typeSelect("#update-guide-kjb", data.type);
            form.render();
        },
        cancel: function (index, layero, that) {
            $("#update-kettle-form")[0].reset();
            form.render();
            return true;
        },
    });

    changeTypeFilter(form, "update-type-filter", "#update-guide-kjb");

    validate(form);
    form.on('submit(update)', function (data) {
        let field = data.field;
        let formData = new FormData();
        for(let key in field) {
            formData.append(key, field[key]);
        }

        formData.delete("file");
        var file = document.getElementById('update-model-upload').files[0];
        if (!_.isNil(file) && !_.isNull(file)) {
            formData.append("file", file);
        }

        if (_.eq("KJB", field.type)) {
            var kjbFile = document.getElementById('update-guide-kjb-upload').files[0]
            if (_.isNil(kjbFile) || _.isNull(kjbFile)) {
                message.warning("KJB引导文件不能为空");
                return false;
            }else if (!_.isNil(kjbFile)) {
                field.guideKjb = kjbFile.name;
            }
        }

        let res = http.postFormData("kettle-info", formData);
        if (!isSuccess(res.code)) {
            message.error(res.message);
            return false;
        }
        return true;
    });
}

function finishSelect(upload, viewFile) {
    console.log(upload, viewFile);
    var content = '', files = [];
    if (document.getElementById(upload).files === undefined) {
        files[0] = {
            'name': document.getElementById(upload) && document.getElementById(upload).value
        };
    } else {
        files = document.getElementById(upload).files;
    }

    for (var i = 0; i < files.length; i++) {
        content += files[i].name.split("\\").pop() + ', ';
    }

    if (content !== '') {
        document.getElementById(viewFile).value = content.replace(/\, $/g, '');
    } else {
        document.getElementById(viewFile).value = '';
    }
}

/**
 * 监听类型
 * @param form 表单对象
 * @param divId 标签ID
 */
function changeTypeFilter(form, divId, guide) {
    form.on('select(' + divId + ')', function (data) {
        var elem = data.elem; // 获得 select 原始 DOM 对象
        var value = data.value; // 获得被选中的值
        var othis = data.othis; // 获得 select 元素被替换后的 jQuery 对象
        typeSelect(guide, value);
    });
}

/**
 * 类型选择器
 * @param type 类型
 */
function typeSelect(guide, type) {
    if (_.eq('KTR', type)) {
        $(guide).hide();
    } else if (_.eq('KJB', type)) {
        $(guide).show();
    }
}

/**
 * 校验字段
 * @param form 表单对象
 */
function validate(form) {
    form.verify({
        name: function (value, item) {
            if(_.isEmpty(value)) return "名称不能为空";
        },
        type: function (value, item) {
            if(_.isEmpty(value)) return "类型不能为空";
        },
        status: function (value, item) {
            if(_.isEmpty(value)) return "状态不能为空";
        },
        logLevel: function (value, item) {
            if(_.isEmpty(value)) return "日志级别不能为空";
        },
        file: function (value, item) {
            if(_.isEmpty(value)) return "模型文件不能为空";
        },
    });
}

/**
 * 清空条件
 */
function clean() {
    $("#start").val('');
    $("#end").val('');
    $("#name").val('');
    $("#select-type option[value='']").prop("selected", true);
    $("#select-status option[value='']").prop("selected", true);
    $("#select-log-level option[value='']").prop("selected", true);

    layui.use('form', function () {
        var form = layui.form;
        form.render();
    });
    search();
}


