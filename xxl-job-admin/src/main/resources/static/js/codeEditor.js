var CodeEditor = (function () {
    var editor;
    function init(displayDivId, textareaDivId, mode, initValue) {
        document.getElementById(displayDivId).style.display = 'block';
        editor = CodeMirror.fromTextArea(document.getElementById(textareaDivId), {
            mode: mode,
            //显示行号
            lineNumbers: true,
            tabSize: 4,
            indentUnit: 4,
            //设置主题
            theme: "dracula",
            //自动换行
            lineWrapping: false,
            foldGutter: true,
            value: null,
            gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter", 'CodeMirror-lint-markers'],
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
        editor.setSize('auto', 680);
        editor.setValue(initValue);
    }

    function setValue(value) {
        return editor.setValue(value);
    }

    function getValue() {
        return editor.getValue();
    }

    function destroy() {
        editor.setValue('');
        editor.clearHistory();
    }

    return {
        init: init,
        setValue: setValue,
        getValue: getValue,
        destroy: destroy,
    }


})();























