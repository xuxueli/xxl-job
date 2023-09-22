$(function () {
    searchJobGroup('#select-jobGroup');
    search();
})

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
                    // {
                    //     id: 'jobLog',
                    //     // templet: '<span style="color: #FFFFFF">查询日志</span>',
                    //     templet: '<li><a _href="page-jobLog?jobId='+ data.id +'"><span style="color: #FFFFFF">查询日志</span></a></li>\n',
                    // },
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

        let res = http.patchBody("job/trigger", param);
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
    let result = http.getPath("job/next-trigger/" + data.id);
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
        let registries = _.split(registryList, ",");
        for (let index in registries) {
            html += '<span class="badge bg-green">' + registries[index] + '</span><br>';
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
        http.patchPath('job/start/' + id);
    } else {
        http.patchPath('job/stop/' + id);
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
    return http.get("job", pageDTO);
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
                        glueTypeSelect(oldData.glueType, !_.isEmpty(oldData));

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
                    }else {
                        scheduleTypeSelect("CRON");
                        glueTypeSelect("BEAN", false);
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
        glueTypeSelect(value, !_.isEmpty(oldData));
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
        } else if (!_.eq('BEAN', glueType) && !_.eq('KETTLE_KTR', glueType)
            && !_.eq('KETTLE_KJB', glueType)) {
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
            ? http.post("job", field)
            : http.put("job", field);
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
 * 任务下拉
 */
function initJobInfo(oldVal) {
    let jobInfos = http.get("job", {'currentPage': -1,}).records;
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
            run: 'job/cron',
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
            http.delBody("job/batch", ids);
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
        http.delPath("job/" + obj.id);
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



