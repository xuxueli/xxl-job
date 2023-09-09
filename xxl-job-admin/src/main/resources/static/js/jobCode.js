/**
 * 编辑 jobCode
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
            CodeEditor.init('code-double-box', 'code-double-code', codeMirrorMode);
            CodeEditor.setValue(defaultGlueSource);
            form.render();
        },
        cancel: function (index, layero, that) {
            CodeEditor.destroy();
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
    let res = http.post("glue-log", param);
    if (!isSuccess(res.code)) {
        message.error(res.message);
        return false;
    }
    return true;
}

/**
 *
 * @param data
 */
function updateJobInfoWebIdeDropdown(data) {
    var dropdown = layui.dropdown;
    var form = layui.form;
    let resArr = http.getPath('glue-log/job/' + data.id);
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
                    CodeEditor.setValue(showData.glueSource);
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
            CodeEditor.init('box', 'code', codeMirrorMode);
            CodeEditor.setValue(defaultGlueSource);
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






















