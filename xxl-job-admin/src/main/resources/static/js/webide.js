var jobId = null;
var glueType = null;
$(function () {
    initWebIde();

});


function initWebIde() {
    console.log(glueType);
    console.log(jobId);
    // let glueType = getUrlParam('glueType');
    // let jobId = getUrlParam('jobId');

    let defaultGlueSource = getDefaultGlueSource(glueType);

    console.log(defaultGlueSource);

    let codeMirrorMode = getCodeMirrorMode(glueType);
    let defaultGlueRemark = getDefaultGlueRemark();

    document.getElementById("box").style.display = 'block';
    let editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: codeMirrorMode,
        //显示行号
        lineNumbers: true,
        tabSize: 4,
        indentUnit: 4,
        //设置主题
        theme: "dracula",
        //自动换行
        lineWrapping: false,
        foldGutter: true,
        value: defaultGlueSource,
        gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
        //全屏模式
        fullScreen: false,
        //括号匹配
        matchBrackets: true,
        extraKeys: {
            "Ctrl-S": function () {
                console.log('保存')
                // document.getElementById("codeEidtFormPostSubmit").click()
            },
        }
    });

    // editor.setValue(defaultGlueSource);
    let htmlWidth = document.body.clientWidth;

    editor.setSize(htmlWidth, 680);


}

/**
 * 获取URL参数
 * @param name 字段名
 * @returns {string|null}
 */
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
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
    let value = '';
   if (_.eq('GLUE_GROOVY', glueType)) {
       value = 'package com.xxl.job.service.handler;\n' +
           '\n' +
           'import com.xxl.job.core.context.XxlJobHelper;\n' +
           'import com.xxl.job.core.handler.IJobHandler;\n' +
           '\n' +
           'public class DemoGlueJobHandler extends IJobHandler {\n' +
           '\n' +
           '\t@Override\n' +
           '\tpublic void execute() throws Exception {\n' +
           '\t\tXxlJobHelper.log("XXL-JOB, Hello World.");\n' +
           '\t}\n' +
           '\n' +
           '}';
    }else if (_.eq('GLUE_SHELL', glueType)) {
       value = '#!/bin/bash\n' +
           'echo "xxl-job: hello shell"\n' +
           '\n' +
           'echo "${I18n.jobinfo_script_location}：$0"\n' +
           'echo "${I18n.jobinfo_field_executorparam}：$1"\n' +
           'echo "${I18n.jobinfo_shard_index} = $2"\n' +
           'echo "${I18n.jobinfo_shard_total} = $3"\n' +
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
       value = '#!/usr/bin/python\n' +
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
   return value;
}

/**
 * 获取glue默认描述
 * @returns {string}
 */
function getDefaultGlueRemark() {
    return 'GLUE代码初始化';
}
































