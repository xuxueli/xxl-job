$(function () {
    init();

    window.onload = function(){
        let name = getUrlParam('name')
        // let sex =  decodeURIComponent(getUrlParam('sex')) //解码
        console.log(name)
    }
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg); // 匹配目标参数
        if (r != null)
            return r[2];
        return null; // 返回参数值
    }
})

/**
 * 初始化
 */
function init() {
    searchJobGroup("#select-jobGroup")
        .then(res => {
            multiSelector.init("#select-jobInfo", "jobInfoId", []);
            cascadeJobInfo('jobGroup-selected', "#key-jobIds");
        })

    initDate('#start', moment().startOf('days').format('YYYY-MM-DD HH:mm:ss'))
        .then(res => {
            initDate('#end', moment().endOf('days').format('YYYY-MM-DD HH:mm:ss'))
                .then(res => search());
        })
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
 * 分页查询
 * @param currentPage 当前页
 * @param pageSize 页大小
 * @returns {*} 分页数据
 */
function pageSearch(currentPage, pageSize) {
    let groupId = $("#select-jobGroup").find("option:selected").val();
    let status = $("#select-status").find("option:selected").val();
    let jobInfoIds = multiSelector.getValue();
    let start = $("#start").val();
    let end = $("#end").val();

    let pageDTO = {
        'currentPage': currentPage,
        'pageSize': pageSize,
        'groupId': groupId,
        'jobIds': jobInfoIds,
        'startTime': start,
        'endTime': end,
        'status': status,
    };
    return http.get("log", pageDTO);
}

/**
 * 创建表格
 * @param records 记录
 */
function createTable(records) {
    layui.use('table', function () {
        var table = layui.table;
        var dropdown = layui.dropdown;
        var form = layui.form;
        // 已知数据渲染
        table.render({
            elem: '#table-data',
            //标题栏
            cols: [[
                {
                    title: '所属执行器', templet: function (row) {
                        if (!_.isEmpty(row) && !_.isEmpty(row.group)) {
                            return row.group.title;
                        }
                        return '';
                    }
                },
                {
                    title: '所属任务', templet: function (row) {
                        if (!_.isEmpty(row) && !_.isEmpty(row.job)) {
                            return row.job.name;
                        }
                        return '';
                    }
                },
                {
                    field: "triggerTime",
                    title: '调度时间',
                    width: 180,
                },
                {
                    title: '调度结果',
                    width: 100,
                    templet: function (row) {
                        var html;
                        var triggerCode = row.triggerCode;
                        if (triggerCode == 200 || triggerCode == 0) {
                            html = '<span style="color: green">成功</span>';
                        } else if (triggerCode == 500) {
                            html = '<span style="color: red">失败</span>';
                        } else if (triggerCode == -1) {
                            html = '';
                        }
                        return html;
                    }
                },
                {
                    title: '调度备注',
                    width: 100,
                    templet: function (row) {
                        return row.triggerMessage ? '<button class="layui-btn layui-btn-primary layui-btn-radius layui-btn-sm layui-border-blue" lay-event="trigger-message-event">查看</button>' : '无';
                    }
                },
                {
                    field: "handleTime",
                    title: '执行时间',
                    width: 180,
                },
                {
                    title: '执行结果',
                    width: 100,
                    templet: function (row) {
                        var html;
                        var handleCode = row.handleCode;
                        if (handleCode == 200 || handleCode == 0) {
                            html = '<span style="color: green">成功</span>';
                        } else if (handleCode == 500) {
                            html = '<span style="color: red">失败</span>';
                        } else if (handleCode == 502) {
                            html = '<span style="color: red">失败(超时)</span>';
                        } else if (handleCode == -1) {
                            html = '';
                        }
                        return html;
                    }
                },
                {
                    title: '执行备注',
                    width: 100,
                    templet: function (row) {
                        var handleMessage = row.handleMessage;
                        return handleMessage ? '<button class="layui-btn layui-btn-primary layui-btn-radius layui-btn-sm layui-border-blue" lay-event="handle-message-event">查看</button>' : '无';
                    }
                },
                {
                    fixed: 'right', width: 100, title: '操作', toolbar: '<div class="layui-clear-space">\n' +
                        '              <a class="layui-btn layui-btn-primary layui-btn-radius layui-btn-sm layui-border-green" lay-event="exeLog-event" >执行日志\n' +
                        '              </a>\n' +
                        '            </div>'
                }
            ]],
            data: records,
            // skin: 'line', // 表格风格
            even: true,// 是否开启隔行背景
            page: false, // 是否显示分页
        });

        form.on('switch(trigger-status-filter)', function (row) {
            updateStatus(row.elem.attributes['jobId'].nodeValue, row.elem.checked);
        });

        table.on('tool(table-data)', function (obj) {
            let data = obj.data;
            let layEvent = obj.event;
            if ("update" === layEvent) {
                change("修改任务", data);
            } else if ("delete" === layEvent) {
                deleteData(data);
            } else if ("moreOperate" === layEvent) {

            }else if (_.eq('trigger-message-event', layEvent)) {
                showMessage('调度结果', data.triggerMessage);
            }else if (_.eq('handle-message-event', layEvent)) {
                showMessage('执行结果', data.handleMessage)
            }else if (_.eq('exeLog-event', layEvent)) {
                findExeLog(data);
            }

        });
    })
    ;
}

/**
 * 查询执行日志
 */
function findExeLog(data) {
    var res = http.getPath('log/cat/' + data.id + "/" + 1);
    var str = '<span style="color: green;">[Load Log Finish]</span>';
    if (!_.isEmpty(res)) {
        str = res.logContent + "<br>" + str;
    }
    showMessage('执行日志', str);
}

/**
 * 手动清理日志
 */
function manualClearLog() {
    let form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.7) + 'px', ($(window).height() - 200) + 'px'],
        fix: false, //不固定
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: '日志清理',
        content: $('#clean-log-form'),
        success: function (index) {
            searchJobGroup("#clean-group")
                .then(res => cascadeJobInfo('clean-group-selected', '#clean-jobIds'))
            multiSelector.init("#clean-jobIds", "jobIds", []);
            form.render();
        },
        cancel: function (index, layero, that) {
            $("#key-group option[value='']").prop("selected", true);
            $("#clear-mode option[value='1']").prop("selected", true);
            multiSelector.update([]);
            $("#clean-log-form-form")[0].reset();
            form.render();
            return true;
        },
    });

    form.on('submit(clearLog)', function (data) {
        let field = data.field;
        field.jobIds = multiSelector.getValue();
        let res = http.delBody('log/clean', field);
        if (!isSuccess(res.code)) {
            message.error(res.message);
            return false;
        }
        return true;
    });
}

/**
 * 显示信息
 * @param 标题
 * @param message 消息
 */
function showMessage(title, message) {
    if (!_.isEmpty(message)) {
        let html = '<div>';
            html += '<span class="badge bg-green">' + message + '</span><br>';
            html += '</div>';
        layer.alert(html, {
            title: title,
            skin: 'layui-layer-molv',
            area: ['800px', '800px'],
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
 * 查询执行器
 */
function searchJobGroup(divId) {
    return new Promise(resolve => {
        let page = http.get("group", {'currentPage': -1,});
        layui.use(['form'], function () {
            let form = layui.form;
            $.each(page.records, function (index, item) {
                let option = new Option(item.title, item.id);
                $(divId).append(option);
            });
            form.render("select");
            resolve(true)
        });
    })
}

/**
 * 查询任务
 */
function searchJobInfo(divId, params) {
    return new Promise(resolve => {
        let jobInfos = http.get("/job", params);
        let val = [];
        for (let jobInfo of jobInfos.records) {
            val.push({name: jobInfo.name, value: jobInfo.id, selected: false, disabled: false});
        }
        multiSelector.update(val);
        resolve(true);
    })
}

/**
 * 级联任务信息
 */
function cascadeJobInfo(select, divId) {
    layui.use('form', function () {
        var form = layui.form;
        form.on('select('+ select +')', function (data) {
            var groupId = data.value;
            multiSelector.update([]);
            if (!_.isEmpty(groupId)) {
                searchJobInfo(divId, {'currentPage': -1, "groupId": groupId})
                    .then(res => {
                        form.render;
                    });
            }
        });
    });
}

/**
 * 清空条件
 */
function clean() {
    $("#select-jobGroup option[value='']").prop("selected", true);
    $("#select-status option[value='']").prop("selected", true);
    multiSelector.update([]);
    $("#start").val('');
    $("#end").val('');
    initDate('#start', moment().startOf('days').format('YYYY-MM-DD HH:mm:ss'));
    initDate('#end', moment().endOf('days').format('YYYY-MM-DD HH:mm:ss'));

    layui.use('form', function () {
        var form = layui.form;
        form.render();
    });
    search();
}