$(function () {
    searchJobGroup('#select-jobGroup');
    search();
})

/**
 * 查询执行器
 */
function searchJobGroup(divId) {
    return new Promise(resolve => {
        let page = http.get("/group", {'currentPage': -1,});
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
        var dropdown = layui.dropdown;
        var form = layui.form;
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
                {title: '所属执行器', templet: '<div>{{d.jobGroup.title}}</div>'},
                {field: 'name', title: '任务名'},
                {
                    title: '调度类型', templet: function (row) {
                        return row.scheduleType + ": " + row.scheduleConf;
                    }
                },
                {
                    title: '运行模式', templet: function (row) {
                        let runModel = findGlueTypeTitle(row.glueType);
                        if (!_.isEmpty(row.executorHandler)) {
                            runModel = runModel + ": " + row.executorHandler;
                        }
                        return runModel;
                    }
                },
                {
                    field: 'triggerStatus', title: '状态', width: 100, templet: function (row) {
                        let jobId = row.id;
                        if (row.triggerStatus == 0) {
                            return "<input type='checkbox'  jobId = '" + jobId + "' lay-filter='trigger-status-filter' lay-skin='switch' lay-text='ON|OFF'>"
                        } else {
                            return "<input type='checkbox'  jobId = '" + jobId + "'  lay-filter='trigger-status-filter' lay-skin='switch' lay-text='ON|OFF' checked>"
                        }
                    }
                },
                {field: 'author', title: '负责人'},
                {
                    fixed: 'right', width: 220, title: '操作', toolbar: '<div class="layui-clear-space">\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-blue" lay-event="update" >编辑\n' +
                        '              </a>\n' +
                        '              <a class="layui-btn layui-btn-radius layui-btn-sm layui-bg-red" lay-event="delete">删除\n' +
                        '              </a>\n' +
                        '               <a class="layui-btn layui-btn-radius layui-btn-sm" style="background-color: #31bdec" lay-event="moreOperate">\n' +
                        '               更多<i class="layui-icon layui-icon-down"></i>\n' +
                        '               </a>\n' +
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
            }else if ("moreOperate" === layEvent) {

                let dropdownArr = [
                    {
                        id: 'copy',
                        templet: '<span style="color: #FFFFFF">复制</span>',
                    },
                    {
                        id: 'exeOnce',
                        templet: '<span style="color: #FFFFFF">执行一次</span>',
                    },
                    {
                        id: 'jobLog',
                        // templet: '<span style="color: #FFFFFF">查询日志</span>',
                        templet: '<li><a _href="page-log?jobId='+ data.id +'"><span style="color: #FFFFFF">查询日志</span></a></li>\n',
                    },
                    {
                        id: 'registerNode',
                        templet: '<span style="color: #FFFFFF">注册节点</span>',
                    },
                    {
                        id: 'nextExeTime',
                        templet: '<span style="color: #FFFFFF">下次执行时间</span>',
                    }
                ]

                if (!_.eq('BEAN',data.glueType) && !_.eq('KETTLE_KTR',data.glueType) && !_.eq('KETTLE_KJB',data.glueType)) {
                    dropdownArr.push({id: 'glueIde', templet: '<span style="color: #FFFFFF">GLUE IDE</span>'})
                }

                dropdown.render({
                    elem: this, // 触发事件的 DOM 对象
                    show: true, // 外部事件触发即显示
                    className: 'site-dropdown',
                    data: dropdownArr,
                    click: function(menuDate, othis){
                        var menu = menuDate.id;
                        if (_.eq("registerNode", menu)) {
                            showRegisterNode(data);
                        }else if (_.eq("nextExeTime", menu)) {
                            showNextExeTime(data);
                        }else if (_.eq("exeOnce", menu)) {
                            exeOnce(data);
                        }else if (_.eq("copy", menu)) {
                            let newDate = _.cloneDeep(data);
                            newDate.id = null;
                            change("新增任务", newDate);
                        }else if (_.eq("jobLog", menu)) {
                            showJobLog();
                        }else if (_.eq('glueIde', menu)) {
                            updateJobInfoWebIde(data);
                        }
                    },
                    align: 'right', // 右对齐弹出
                    style: 'box-shadow: 1px 1px 10px rgb(0 0 0 / 12%);' // 设置额外样式
                })
            }
        });
    });
}

/**
 * 编辑 jobcode
 * @param data 数据
 */
function updateJobInfoWebIde(data) {
    var dropdown = layui.dropdown;
    var form = layui.form;

    let defaultGlueSource = getDefaultGlueSource(data.glueType);
    let codeMirrorMode = getCodeMirrorMode(data.glueType);
    let glueType = findGlueTypeTitle(data.glueType);
    let title = '【' + glueType + '】 ' + data.name;

    layer.open({
        type: 1,
        title: title,
        shadeClose: false,
        shade: 0.8,
        btn: ['提交'],
        btn1: function (index, layero, that) {
            let value = form.val('layui-code-double-edit-form');
            let isSus = saveGlueCode(data.id, data.glueType, CodeEditor.getValue(), value.glueDescription);
            $("#code-edit-double-form")[0].reset();
            dropdown.close('codeDoubleMoreOperate');
            if (isSus) {
                search();
                layer.close(index);
            }else {
                return false;
            }
        },
        area: ['91%', '800px'],
        content: $("#code-double-edit"),
        success: function (index) {
            form.val('layui-code-double-edit-form', {
                'glueDescription': data.glueDescription,
            });
            updateJobInfoWebIdeDropdown(data);
            CodeEditor.init('code-double-box', 'code-double-code', codeMirrorMode, defaultGlueSource);
            form.render();
        },
        cancel: function (index, layero, that) {
            CodeEditor.setValue('');
            $("#code-edit-double-form")[0].reset();
            dropdown.close('codeDoubleMoreOperate');
            // form.render();
            layer.close(index);
        },
    });
}

/**
 * 保存glue code
 * @param jobId 任务ID
 * @param glueSource 类型
 * @param glueSource 源码
 * @param glueDescription 源码描述
 */
function saveGlueCode(jobId, glueType, glueSource, glueDescription) {
    let param = {
        "jobId": jobId,
        "glueType": glueType,
        "glueSource": glueSource,
        "description": glueDescription,
    }
    let res = http.post("/glue-log", param);
    if (!isSuccess(res.code)) {
        message.error(res.message);
        return false;
    }
    return true;
}

function updateJobInfoWebIdeDropdown(data) {
    var dropdown = layui.dropdown;
    var form = layui.form;
    let resArr = http.getPath('/glue-log/job/' + data.id);
    if (_.isEmpty(resArr)) return;
    let showDatas = [{'id': data.id, 'jobId': data.id,
        'glueSource': data.glueSource,
        'glueDescription': data.glueDescription}];
    let dropdownArr = [{
        id: data.id,
        templet: '<span style="color: #FFFFFF">' + data.glueDescription + '</span>',
    }];
    for (let res of resArr) {
        dropdownArr.push({
            id: res.id,
            templet: '<span style="color: #FFFFFF">' + res.description + '</span>',
        });
        showDatas.push({'id': res.id, 'jobId': data.id, 'glueSource': res.glueSource,
            'glueDescription': res.description});
    }
    dropdown.render({
        elem: '#codeDoubleMoreOperate', // 触发事件的 DOM 对象
        show: true, // 外部事件触发即显示
        className: 'site-dropdown',
        data: dropdownArr,
        click: function(menuDate, othis){
            var menu = menuDate.id;
            for (let showData of showDatas) {
                if (_.eq(showData.id, menu)) {
                    CodeEditor.init('code-double-box', 'code-double-code',
                        getCodeMirrorMode(data.glueType), showData.glueSource);
                    form.val('layui-code-double-edit-form', {
                        'glueDescription': showData.glueDescription,
                    });
                }
            }
        },
        align: 'right', // 右对齐弹出
        style: 'box-shadow: 1px 1px 10px rgb(0 0 0 / 12%);' // 设置额外样式
    })
}

/**
 * 查询模式类型
 * @param glueType 模式类型
 * @returns {string} 显示值
 */
function findGlueTypeTitle(glueType) {
    if (_.eq('BEAN', glueType)) {
        return 'BEAN';
    }else if (_.eq('GLUE_GROOVY', glueType)) {
        return 'GLUE(Java)';
    }else if (_.eq('GLUE_SHELL', glueType)) {
        return 'GLUE(Shell)';
    }else if (_.eq('GLUE_PYTHON', glueType)) {
        return 'GLUE(Python)';
    }else if (_.eq('GLUE_PHP', glueType)) {
        return 'GLUE(PHP)';
    }else if (_.eq('GLUE_NODEJS', glueType)) {
        return 'GLUE(Nodejs)';
    }else if (_.eq('GLUE_POWERSHELL', glueType)) {
        return 'GLUE(PowerShell)';
    }else if (_.eq('KETTLE_KTR', glueType)) {
        return 'kettle(ktr)';
    }else if (_.eq('KETTLE_KJB', glueType)) {
        return 'kettle(kjb)';
    }else {
        return 'BEAN';
    }
}

/**
 * 显示任务日志
 */
function showJobLog() {
    console.log("查询日志");

}

/**
 * 执行一次
 * @param oldData 数据
 */
function exeOnce(oldData) {
    let form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.7) + 'px', ($(window).height() - 300) + 'px'],
        fix: false, //不固定
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: "执行一次",
        content: $('#trigger-job-form'),
        success: function (index) {
            form.render();
        },
        cancel: function (index, layero, that) {
            $("#trigger-job--form-form")[0].reset();
            form.render();
            return true;
        },
    });

    form.on('submit(triggerJob)', function (data) {
        let field = data.field;

        let addresses = null;
        if (!_.isEmpty(field.addresses)) {
            addresses = _.split(field.addresses, ",");
        }

        let param = {
            "jobInfoId": oldData.id,
            "executorParam": _.isEmpty(data.executorParam) ? null : data.executorParam,
            "addresses": addresses,
        }

        let res = http.patchBody("/job/trigger", param);
        if (!isSuccess(res.code)) {
            message.error(res.message);
            return false;
        }
        return true;
    });
}

/**
 * 显示下次执行时间
 * @param data 数据
 */
function showNextExeTime(data) {
    let result = http.getPath("/job/next-trigger/" + data.id);
    if (!_.isEmpty(result)) {
        let html = '<div style="text-align: center; font-size: 20px">';
        for (let value of result) {
            html += '<span class="badge bg-green">' + value + '</span><br>';
        }
        html += '</div>';

        layer.alert(html, {
            title: '下次执行时间',
            skin: 'layui-layer-molv',
            area: ['400px', 'auto'],
        });
    }
}

/**
 * 显示注册节点
 * @param data 数据
 */
function showRegisterNode(data) {
    let html = '<div style="text-align: center; font-size: 20px">';
    let registryList = data.jobGroup.addresses;
    if (!_.isEmpty(registryList)) {
        for (let index in registryList) {
            html += '<span class="badge bg-green">' + registryList[index] + '</span><br>';
        }
    }
    html += '</div>';

    layer.alert(html, {
        title: '注册节点',
        skin: 'layui-layer-molv',
        area: ['400px', 'auto'],
    });
}

/**
 * 修改状态
 * @param id 主键
 * @param status 状态
 */
function updateStatus(id, status) {
    if (status) {
        http.patchPath('/job/start/' + id);
    } else {
        http.patchPath('/job/stop/' + id);
    }
    pageSearch(1, 50);
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
    let groupId = $("#select-jobGroup").find("option:selected").val();
    let name = $("#name").val();
    let status = $("#select-status").find("option:selected").val();
    let executorHandler = $("#executorHandler").val();
    let author = $("#author").val();

    let pageDTO = {
        'currentPage': currentPage,
        'pageSize': pageSize,
        'groupId': groupId,
        'name': name,
        'executorHandler': executorHandler,
        'author': author,
        'triggerStatus': status,
    };
    return http.get("/job", pageDTO);
}

/**
 * 修改，新增公共方法
 * @param title 标题
 * @param oldData 回显数据(修改时使用)
 */
function change(title, oldData) {
    let form = layui.form;
    layer.open({
        type: 1,
        area: [($(window).width() * 0.8) + 'px', ($(window).height() - 50) + 'px'],
        fix: false, //不固定
        shadeClose: true,
        shade: 0.4,
        maxmin: true,
        title: title,
        content: $('#key-form'),
        success: function (index) {
            searchJobGroup("#add-group")
                .then(res => {
                    if (!_.isEmpty(oldData)) {
                        scheduleTypeSelect(oldData.scheduleType);
                        glueTypeSelect(oldData.glueType, true);

                        form.val("layui-key-form", {
                            "groupId": oldData.jobGroup.id,
                            "name": oldData.name,
                            "author": oldData.author,
                            "alarmEmail": oldData.alarmEmail,
                            "scheduleType": oldData.scheduleType,
                            "misfireStrategy": oldData.misfireStrategy,
                            "scheduleConfCron": oldData.scheduleConf,
                            "scheduleConfFixDelay": oldData.scheduleConf,
                            "executorRouteStrategy": oldData.executorRouteStrategy,
                            "executorHandler": oldData.executorHandler,
                            "executorParam": oldData.executorParam,
                            "executorBlockStrategy": oldData.executorBlockStrategy,
                            "executorTimeout": oldData.executorTimeout,
                            "executorFailRetryCount": oldData.executorFailRetryCount,
                            "glueType": oldData.glueType,
                            "triggerStatus": oldData.triggerStatus,
                            "telephone": oldData.telephone,
                            "description": oldData.description,
                        });
                    }
                    form.render();
                });
            initJobInfo(_.isEmpty(oldData) ? null : oldData.childJobIds);
        },
        cancel: function (index, layero, that) {
            $('#add-group').empty();
            $('#add-child-jobIds').empty();
            $("#key-form-form")[0].reset();
            form.render();
            return true;
        },
    });

    form.on('select(schedule-type-selected-filter)', function (data) {
        var elem = data.elem; // 获得 select 原始 DOM 对象
        var value = data.value; // 获得被选中的值
        var othis = data.othis; // 获得 select 元素被替换后的 jQuery 对象
        scheduleTypeSelect(value);
    });

    form.on('select(glue-type-selected-filter)', function (data) {
        var elem = data.elem; // 获得 select 原始 DOM 对象
        var value = data.value; // 获得被选中的值
        var othis = data.othis; // 获得 select 元素被替换后的 jQuery 对象
        glueTypeSelect(value, !_.isEmpty(oldData) ? true : false);
    });

    layui.$('#for-glue-source').on('click', function(){
        openWebIde(form.val('layui-key-form'), false);
    });

    validate(form);
    form.on('submit(submit)', function (data) {

        let field = data.field;
        let scheduleType = $("#add-schedule-type").find("option:selected").val();
        if (_.eq('CRON', scheduleType)) {
            field.scheduleConf = field.scheduleConfCron;
        } else if (_.eq('NONE', scheduleType)) {
            field.scheduleConf = null;
        } else if (_.eq('FIX_RATE', scheduleType)) {
            field.scheduleConf = field.scheduleConfFixDelay;
        }

        if ((_.eq('NONE', scheduleType) || _.eq('FIX_RATE', scheduleType)) && _.isEmpty(field.scheduleConf)) {
            message.warning("调度配置不能为空");
            return false;
        }

        var dep = xmSelect.get('#key-child-jobIds', true).getValue();
        let childJobIds = [];
        if (!_.isEmpty(dep)) {
            for (let childJobsKey of dep) {
                childJobIds.push(childJobsKey.value);
            }
        }
        field.childJobIds = childJobIds;
        let glueType = $("#add-glue-type").find("option:selected").val();
        if (_.eq('BEAN', glueType) && _.isEmpty(field.executorHandler)) {
            message.warning("JobHandler不能为空");
            return false;
        } else if (!_.eq('KETTLE_KTR', glueType) && !_.eq('KETTLE_KJB', glueType)) {
            if (_.isEmpty(field.glueSource)) {
                message.warning("执行代码不能为空");
                return false;
            }
            if (_.isEmpty(field.glueDescription)) {
                message.warning("执行代码描述不能为空");
                return false;
            }
        }else {

        }

        if (!_.isEmpty(oldData.id)) field.id = oldData.id;

        delete field.scheduleConfCron;
        delete field.scheduleConfFixDelay;

        let res = (_.isEmpty(oldData) || _.isEmpty(oldData.id))
            ? http.post("/job", field)
            : http.put("/job", field);
        if (!isSuccess(res.code)) {
            message.error(res.message);
            return false;
        }
        return true;
    });
}

/**
 * 运行模式选择器
 * @param glueType 运行模式
 * @param isUpdate 是否是修改
 */
function glueTypeSelect(glueType, isUpdate) {
    if (_.eq('BEAN', glueType)) {
        $("#glue-conf-Handler").show();
        $("#glue-source").hide();
        $('#glue-kettle').hide();
    } else if (!_.eq('KETTLE_KTR', glueType)
        && !_.eq('KETTLE_KJB', glueType)) {
        if (isUpdate) {
            $("#glue-source").hide();
        }else {
            $("#glue-source").show();
        }
        $("#glue-conf-Handler").hide();
        $('#glue-kettle').hide();
    }else if (_.eq('KETTLE_KTR', glueType || _.eq('KETTLE_KJB', glueType))) {
        $('#glue-kettle').show();
        $("#glue-conf-Handler").hide();
        $("#glue-source").hide();
        multiSelector.init('#for-glue-kettle', 'kettleId', []);
    }
}

/**
 * 调度类型 选择器
 * @param scheduleType 调度类型
 */
function scheduleTypeSelect(scheduleType) {
    if (_.eq('CRON', scheduleType)) {
        $("#schedule-conf-cron").show();
        $("#schedule-conf-fixdelay").hide();
    } else if (_.eq('NONE', scheduleType)) {
        $("#schedule-conf-cron").hide();
        $("#schedule-conf-fixdelay").hide();
    } else if (_.eq('FIX_RATE', scheduleType)) {
        $("#schedule-conf-cron").hide();
        $("#schedule-conf-fixdelay").show();
    }
}

/**
 * 打开WEB IDE
 * @param data 数据
 * @param isUpdate 是否是修改
 */
function openWebIde(data, isUpdate) {
    var form = layui.form;
    if (_.isEmpty(data.groupId)) {
        message.warning("请先选择执行器");
        return;
    }

    if (_.isEmpty(data.name)) {
        message.warning("请先填写任务名");
        return;
    }

    let defaultGlueSource = getDefaultGlueSource(data.glueType);
    let codeMirrorMode = getCodeMirrorMode(data.glueType);
    let glueType = findGlueTypeTitle(data.glueType);
    let title = '【' + glueType + '】 ' + data.name;

    layer.open({
        type: 1,
        title: title,
        shadeClose: false,
        shade: 0.8,
        btn: ['提交'],
        btn1: function (index, layero, that) {
            form.val('layui-key-form', {
                "glueSource": CodeEditor.getValue(),
                'glueDescription': $('#for-glue-description').val(),
            })
            CodeEditor.setValue('');
            $("#code-edit-form")[0].reset();
            form.render();
            layer.close(index);
        },
        area: ['91%', '800px'],
        content: $("#code-edit"),
        success: function (index) {
            CodeEditor.init('box', 'code', codeMirrorMode, defaultGlueSource);
            form.render();
        },
        cancel: function (index, layero, that) {
            CodeEditor.setValue('');
            $("#code-edit-form")[0].reset();
            form.render();
            layer.close(index);
        },
    });
}

/**
 * 获取编辑器类型
 * @param glueType 模式类型
 * @returns {string} 显示值
 */
function getCodeMirrorMode(glueType) {
    let value = '';
    if (_.eq('GLUE_GROOVY', glueType)) {
        value = 'text/x-java';
    }else if (_.eq('GLUE_SHELL', glueType)) {
        value = 'text/x-sh';
    }else if (_.eq('GLUE_PYTHON', glueType)) {
        value = 'text/x-python';
    }else if (_.eq('GLUE_PHP', glueType)) {
        value = 'text/x-php';
    }else if (_.eq('GLUE_NODEJS', glueType)) {
        value = 'text/javascript';
    }else if (_.eq('GLUE_POWERSHELL', glueType)) {
        value = 'powershell';
    }
    return value;
}

/**
 * 获取默认源码
 * @param glueType 模式类型
 * @returns {string} 显示值
 */
function getDefaultGlueSource(glueType) {
    if (_.eq('GLUE_GROOVY', glueType)) {
        return 'package com.xxl.job.service.handler;\n' +
            '\n' +
            'import com.xxl.job.core.context.XxlJobHelper;\n' +
            'import com.xxl.job.core.handler.JobHandler;\n' +
            '\n' +
            'public class DemoGlueJobHandler extends JobHandler {\n' +
            '\n' +
            '\t@Override\n' +
            '\tpublic void execute() throws Exception {\n' +
            '\t\tXxlJobHelper.log("XXL-JOB, Hello World.");\n' +
            '\t}\n' +
            '\n' +
            '}';
    }else if (_.eq('GLUE_SHELL', glueType)) {
        return '#!/bin/bash\n' +
            'echo "xxl-job: hello shell"\n' +
            '\n' +
            'echo "脚本位置：$0"\n' +
            'echo "任务参数：$1"\n' +
            'echo "分片序号 = $2"\n' +
            'echo "分片总数 = $3"\n' +
            '<#--echo "参数数量：$#"\n' +
            'for param in $*\n' +
            'do\n' +
            '    echo "参数 : $param"\n' +
            '    sleep 1s\n' +
            'done-->\n' +
            '\n' +
            'echo "Good bye!"\n' +
            'exit 0';
    }else if (_.eq('GLUE_PYTHON', glueType)) {
        return '#!/usr/bin/python\n' +
            '# -*- coding: UTF-8 -*-\n' +
            'import time\n' +
            'import sys\n' +
            '\n' +
            'print "xxl-job: hello python"\n' +
            '\n' +
            'print "脚本位置：", sys.argv[0]\n' +
            'print "任务参数：", sys.argv[1]\n' +
            'print "分片序号：", sys.argv[2]\n' +
            'print "分片总数：", sys.argv[3]\n' +
            '<#--for i in range(1, len(sys.argv)):\n' +
            '\ttime.sleep(1)\n' +
            '\tprint "参数", i, sys.argv[i]-->\n' +
            '\n' +
            'print "Good bye!"\n' +
            'exit(0)\n' +
            '<#--\n' +
            'import logging\n' +
            'logging.basicConfig(level=logging.DEBUG)\n' +
            'logging.info("脚本文件：" + sys.argv[0])\n' +
            '-->';
    }else if (_.eq('GLUE_PHP', glueType)) {
        return '<?php\n' +
            '\n' +
            '    echo "xxl-job: hello php  \\n";\n' +
            '\n' +
            '    echo "脚本位置：$argv[0]  \\n";\n' +
            '    echo "任务参数：$argv[1]  \\n";\n' +
            '    echo "分片序号 = $argv[2]  \\n";\n' +
            '    echo "分片总数 = $argv[3]  \\n";\n' +
            '\n' +
            '    echo "Good bye!  \\n";\n' +
            '    exit(0);\n' +
            '\n' +
            '?>';
    }else if (_.eq('GLUE_NODEJS', glueType)) {
        return '#!/usr/bin/env node\n' +
            'console.log("xxl-job: hello nodejs")\n' +
            '\n' +
            'var arguments = process.argv\n' +
            '\n' +
            'console.log("脚本位置: " + arguments[1])\n' +
            'console.log("任务参数: " + arguments[2])\n' +
            'console.log("分片序号: " + arguments[3])\n' +
            'console.log("分片总数: " + arguments[4])\n' +
            '<#--for (var i = 2; i < arguments.length; i++){\n' +
            '\tconsole.log("参数 %s = %s", (i-1), arguments[i]);\n' +
            '}-->\n' +
            '\n' +
            'console.log("Good bye!")\n' +
            'process.exit(0)';
    }else if (_.eq('GLUE_POWERSHELL', glueType)) {
        return 'Write-Host "xxl-job: hello powershell"\n' +
            '\n' +
            'Write-Host "脚本位置: " $MyInvocation.MyCommand.Definition\n' +
            'Write-Host "任务参数: "\n' +
            '\tif ($args.Count -gt 2) { $args[0..($args.Count-3)] }\n' +
            'Write-Host "分片序号: " $args[$args.Count-2]\n' +
            'Write-Host "分片总数: " $args[$args.Count-1]\n' +
            '\n' +
            'Write-Host "Good bye!"\n' +
            'exit 0';
    }
    return '';
}

/**
 * 任务下拉
 */
function initJobInfo(oldVal) {
    let jobInfos = http.get("/job", {'currentPage': -1,}).records;
    let val = [];

    for (let jobInfo of jobInfos) {
        if(!_.isEmpty(oldVal) && _.includes(oldVal, jobInfo.id)){
            val.push({name:jobInfo.name, value:jobInfo.id, selected: true, disabled: false});
        }else {
            val.push({name:jobInfo.name, value:jobInfo.id, selected: false, disabled: false});
        }
    }

    multiSelector.init('#key-child-jobIds', 'childJobIds', val);
    layui.use(['cron'], function () {
        var $ = layui.$;
        var cron = layui.cron;
        cron.render({
            elem: "#for-schedule-conf-cron",
            value: $("#for-schedule-conf-cron").val(),
            done: function (cronStr) {
                $("#for-schedule-conf-cron").val(cronStr);
            },
        });
    });

}

/**
 * 校验字段
 * @param form 表单对象
 */
function validate(form) {
    form.verify({
        groupId: function (value, item) {
            if (_.isNil(value) || _.isEmpty(value)) {
                return '执行器不能为空';
            }
        },
        name: function (value, item) {
            if (/(^_)|(__)|(_+$)/.test(value)) return '任务名首尾不能出现 _ 下划线';
            if (/^\d+$/.test(value)) return '任务名不能全为数字';
        },
        author: function (value, item) {
            if (/(^_)|(__)|(_+$)/.test(value)) return '负责人首尾不能出现 _ 下划线';
            if (/^\d+$/.test(value)) return '负责人不能全为数字';
        },
        triggerStatus: function (value, item) {
            if (_.isNil(value) || _.isEmpty(value)) return '是否生效不能为空';
            if (!_.isNumber(_.toNumber(value))) return '是否生效只能是数字';
        },
        scheduleType: function (value, item) {
            if (_.isNil(value) || _.isEmpty(value)) return '调度类型不能为空';
        },
        glueType: function (value, item) {
            if (_.isNil(value) || _.isEmpty(value)) return '运行模式不能为空';
        },

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
            http.delBody("/job/batch", ids);
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
        http.delPath("/job/" + obj.id);
        layer.close(index);
        search();
    });
}

/**
 * 清空条件
 */
function clean() {
    $("#select-jobGroup option[value='']").prop("selected", true);
    $("#select-status option[value='']").prop("selected", true);
    $("#executorHandler").val('');
    $("#author").val('');

    layui.use('form', function(){
        var form = layui.form;
        form.render();
    });
    search();
}



