/**
 * Created by LinJ on 2015/11/5.
 * 初始化页面模块js
 */

//初始化各类元素以及监听
$(function () {
    inition();
});

//页面所有的页签名称
var itemList = ["second", "min", "hour", "day", "month", "week"];

//初始化各个页签自主选择的最大值
var max_second = 59;
var max_min = 59;
var max_hour = 23;
var max_day = 31;
var max_month = 12;
var max_quarter = 12;
var max_week = 7;

function inition() {
    //初始化checkbox
    initCheckBox("l_second", 0, 59);
    initCheckBox("l_min", 0, 59);
    initCheckBox("l_hour", 0, 23);
    initCheckBox("l_day", 1, 31);
    initCheckBox("l_month", 1, 12);
    initCheckBox("l_quarter", 1, 12);
    initCheckBox("l_week", 1, 7);


    //初始化单选、复选框
    $('input').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    //解析按钮绑定函数
    $('#explain').click(function () {
        reverseExp();
    });
    //cron表达式值改变,绑定事件
    $('#cron').change(function () {
        console.log("change:"+ $('#cron').valid());
       showCronExpress();
    });
    initFirstRadio();
    initNoConfirmRadio();
    initRadio();
    initRadioCheckDiv();
    initSpinner();
    initCronFromTable();
    initListChange();
    initMonthByQuarter();

    //如果已有传输过来的值，那么放入cron字段并且反解析
    var instr = $("#transCron").val();
    if (instr) {
        $("#cron").val(instr);
        reverseExp();
        showCronExpress();
    }

}

//初始化大量的复选框 根据div的id以及数量进行初始化 每个复选框占1/12宽度
function initCheckBox(divid, start, cnt) {
    if (cnt != null && cnt > 0) {
        for (dc = start; dc <= cnt; dc++) {
            $("#" + divid).append(" <div class='col-xs-1 col-sm-1 col-md-1 p_m '> <label><input type='checkbox' value=" + dc + "> &nbsp;" + dc + "</label></div>");
        }
    }
}

//初始化每个页签第一排的radio，即*条件
function initFirstRadio() {
    $('.firstradio').on('ifChecked', function () {
        everyTime(this);
    });
    $('.unselectradio').on('ifChecked', function () {
        clearSpan(this);
    });

    //季度的第一排radio，需要指定为month
    $('.firstradioreactor').on('ifChecked', function () {
        everyTimeByName("v_month");
    });
}

//初始化不指定的radio 即？条件 以及最后一日条件
function initNoConfirmRadio() {
    $('.noconfirmradio').on('ifChecked', function () {
        unAppoint(this);
    });
    $('.lastdayradio').on('ifChecked', function () {
        lastDay(this);
    });

}

//初始化每个页签选择的的radio
function initRadio() {
    //周期选择的radio
    $('.cycleradio').on('ifChecked', function () {
        writeStartAndEnd(this, "-");
    });
    //循环选择的radio
    $('.loopradio').on('ifChecked', function () {
        writeStartAndEnd(this, "/");
    });

    //指定选择的radio 即#
    $('.designradio').on('ifChecked', function () {
        writeStartAndEnd(this, "#");
    });


    //自主选择的radio
    $('.choiceradio').on('ifChecked', function () {

        var spanname = this.name;
        var theList = $("." + spanname + "List").find(':checkbox');
        var maxvalue = eval("max_" + this.name);
        changeSpanFromCheckList(theList, maxvalue, spanname);
    });

    //最近工作日的radio
    $('.nearradio').on('ifChecked', function () {
        writeEnd(this, "W");
    });

    //最后一个周几的radio
    $('.lastradio').on('ifChecked', function () {
        writeEnd(this, "L");
    });


}

//初始化div内容监听绑定函数，即点击div，对应radio被选中
function initRadioCheckDiv() {
    $('.radiocheck').click(function () {
        radioCheckByClick(this);
    });

    $.each(itemList, function (n, value) {
        var checkList = $("." + value + "List");
        checkList.click(function () {
            var theRadio = this.closest('.radiocheck').find(':radio');
            theRadio.eq(0).iCheck('check');
        });
    });
}


//绑定数字微调器
function initSpinner() {
    //绑定数字微调器
    $(".cyclespin").spinner('changing', function (e, newVal, oldVal) {
        //trigger immediately
        writeStartAndEnd(this, "-");
    });
    $(".loopspin").spinner('changing', function (e, newVal, oldVal) {
        //trigger immediately
        writeStartAndEnd(this, "/");
    });
    $(".designspin").spinner('changing', function (e, newVal, oldVal) {
        //trigger immediately
        writeStartAndEnd(this, "#");
    });
    $(".nearspin").spinner('changing', function (e, newVal, oldVal) {
        //trigger immediately
        writeEnd(this, "W");
    });
    $(".lastspin").spinner('changing', function (e, newVal, oldVal) {
        //trigger immediately
        writeEnd(this, "L");
    });
}

//表达式结果由表格生成到cron表达式
function initCronFromTable() {
    //查找所有name以v_开头的span元素
    var vals = $("span[name^='v_']");
    var cron = $("#cron");
    vals.change(function () {
        var item = [];
        vals.each(function () {
            item.push(this.innerHTML);
        });
        cron.val(item.join(" "));
    });
}

//定义checkbox在被点击的时候的绑定函数  循环遍历页签数组
function initListChange() {
    $.each(itemList, function (n, value) {
        var checkList = $("." + value + "List").find(':checkbox');
        var maxvalue = eval("max_" + value);
        initChangeOnCheckboxList(checkList, maxvalue, value);
    });
}

//给list的checkbox绑定点击方法 入参是list，最大值，指定输出的span名称
function initChangeOnCheckboxList(checkList, maxvalue, spanname) {
    checkList.on('ifChanged', function () {
        //模拟div被点击过一次
        $(this).closest('.radiocheck').click();
        changeSpanFromCheckList(checkList, maxvalue, spanname);
    });
}

//定义季度的月份选择监听
function initMonthByQuarter() {
    var checkList = $("#quarter_spe").find(':checkbox');
    checkList.on('ifChanged', function () {
        var vals = [];
        checkList.each(function () {
            if (this.checked) {
                vals.push(this.value);
            }
        });
        var val = "?";
        val = vals.join(",");
        ary = val.split(",");
        unselectAll('l_quarter');
        for (var i = 0; i < ary.length; i++) {
            $("#l_quarter input[value='" + ary[i] + "']").iCheck('check');
        }
    });
}

//清除选中状态
function unselectAll(listId) {
    for (var i = 1; i <= 12; i++) {
        $("#l_quarter input[value='" + i + "']").iCheck('uncheck');
    }
}