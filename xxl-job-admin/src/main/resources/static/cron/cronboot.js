/**
 * Created by LinJ on 2015/11/5.
 * 页面功能模块js
 */



/**
 * 表达式重置为默认值
 */
function everyTime(dom) {
    var item = $("span[name=v_" + dom.name + "]");
    item.html("*");
    item.change();
}

/**
 * 表达式重置为默认值
 入参为元素的名字
 */
function everyTimeByName(v_name) {
    var item = $("span[name=" +v_name + "]");
    item.html("*");
    item.change();
}

//清除表达式的值
function clearSpan(dom){
    var item = $("span[name=v_" +dom.name + "]");
    item.html("");
    item.change();
}


/**
 * 不指定 即重置为？号
 */
function unAppoint(dom) {
    var name = dom.name;
    var val = "?";
    if (name == "year")
        val = "";
    var item = $("span[name=v_" + name + "]");
    item.html(val);
    item.change();
}

/**
 * 不指定 即重置为？号
 入参为元素的名字
 */
function unAppointByName(v_name) {

    var val = "?";
    if (v_name == "year")
        val = "";
    var item = $("span[name=" + v_name + "]");
    item.html(val);
    item.change();
}

//最后一日
function lastDay(dom){
    var item = $("span[name=v_" + dom.name + "]");
    item.html("L");
    item.change();
}

/**
 * 书写有前后的公式，包括 1-2 1/2 1#2 等
 */
function writeStartAndEnd(dom,sym) {
    var name = dom.name;
    var ns = $(dom).closest('.radiocheck').find(".numberspinner");
    var start = ns.eq(0).val();
    var end = ns.eq(1).val();
    var item = $("span[name=v_" + name + "]");
    item.html(start + sym + end);
    item.change();
}

//书写只有前的工时，包括1W 1L等
function writeEnd(dom,sym){
    var name = dom.name;
    var ns = $(dom).closest('.radiocheck').find(".numberspinner");
    var value = ns.eq(0).val();
    var item = $("span[name=v_" + name + "]");
    item.html(value + sym);
    item.change();
}


//点击div中的内容，对应radio即被选中的功能
function radioCheckByClick(dom){
    var theRadio = $(dom).find(':radio');
    theRadio.eq(0).iCheck('check');
}

//从checkList更新span的通用方法
function changeSpanFromCheckList(checkList,maxvalue,spanname){
    var vals = [];
    checkList.each(function () {
        if (this.checked) {
            vals.push(this.value);
        }
    });
    var val = "?";
    if (vals.length > 0 && vals.length < maxvalue) {
        val = vals.join(",");
    }else if(vals.length == maxvalue){
        val = "*";
    }
    var item = $("span[name=v_"+spanname+"]");
    item.html(val);
    item.change();
}

//解析cron表达式到生成器的函数
function reverseExp() {
    //获取参数中表达式的值
    var txt = $("#cron").val();
    if (txt) {
        showCronExpress();
        var regs = txt.split(' ');
        expObj(regs[0], "second");
        expObj(regs[1], "min");
        expObj(regs[2], "hour");
        expDay(regs[3], "day");
        expObj(regs[4], "month");
        expWeek(regs[5],"week");

        if (regs.length > 6) {
            $("span[name=v_year]").html(regs[6]);
            expYear(regs[6],"year");
        }
    }else {
        $("#explain_express_list").css("display","none")
    }
}
function showCronExpress() {
    var jobCron = $("#cron").val();
    if ($.trim(jobCron).length==0) {
        return;
    }
    $.ajax({
        type : 'GET',
        url : base_url +"/jobinfo/cron/calcRunTime?CronExpression="+jobCron,
        dataType : "json",
        success : function(data){
            if (data.code == 200) {
                var content = data.content;
                var html='';
                if(content&&content.length>0){
                    for (var i = 0; i < content.length; i++) {
                        html +='  <div class="row">\n' +
                            '   <div class="col-xs-12 col-sm-6 col-md-3" style="color:red">'+content[i]+'</div>\n' +
                            '      </div>'
                    }
                }else {
                    html ='  <div class="row">\n' +
                        '   <div class="col-xs-12 col-sm-6 col-md-3">Cron 表达式错误!</div>\n' +
                        '      </div>'
                }
                $("#explain_express_list").html(html);
                $("#explain_express_list").css("display","")

            }
        }
    });
}

//解析时分秒月
function expObj(val,type){
    //表达式结果框赋值
    $("span[name=v_"+type+"]").html(val);
    //寻找对应类型的radio列表
    var radios = $(":radio[name="+type+"]");
    var ary = null;
    //根据值的类型进行判断与对应赋值，以及radio的选中。
    if (val == "*") {
        radios.eq(0).iCheck("check");
    } else if (val.split('-').length > 1) {
        ary = val.split('-');
        $(":text[name="+type+"]").eq(0).val(ary[0]);
        $(":text[name="+type+"]").eq(1).val(ary[1]);
        radios.eq(1).iCheck("check");
    } else if (val.split('/').length > 1) {
        ary = val.split('/');
        $(":text[name="+type+"]").eq(2).val(ary[0]);
        $(":text[name="+type+"]").eq(3).val(ary[1]);
        radios.eq(2).iCheck("check");
    } else {
        if (val != "?") {
            ary = val.split(",");
            for (var i = 0; i < ary.length; i++) {
                $("." + type + "List input[value='" + ary[i] + "']").iCheck("check");
            }
        }
        radios.eq(3).iCheck("check");
    }
}

//解析日
function expDay(val,type){
    //表达式结果框赋值
    $("span[name=v_"+type+"]").html(val);
    //寻找对应类型的radio列表
    var radios = $(":radio[name="+type+"]");
    var ary = null;
    //根据值的类型进行判断与对应赋值，以及radio的选中。
    if (val == "*") {
        radios.eq(0).iCheck("check");
    }else if (val == "?") {
        radios.eq(1).iCheck("check");
    }else if (val == "L") {
        radios.eq(2).iCheck("check");
    }else if (val.split('-').length > 1) {
        ary = val.split('-');
        $(":text[name="+type+"]").eq(0).val(ary[0]);
        $(":text[name="+type+"]").eq(1).val(ary[1]);
        radios.eq(3).iCheck("check");
    } else if (val.split('/').length > 1) {
        ary = val.split('/');
        $(":text[name="+type+"]").eq(2).val(ary[0]);
        $(":text[name="+type+"]").eq(3).val(ary[1]);
        radios.eq(4).iCheck("check");
    } else if (val.split('W').length > 1) {
        ary = val.split('W');
        $(":text[name="+type+"]").eq(4).val(ary[0]);
        radios.eq(5).iCheck("check");
    }else {
        if (val != "?") {
            ary = val.split(",");
            for (var i = 0; i < ary.length; i++) {
                $("." + type + "List input[value='" + ary[i] + "']").iCheck("check");
            }
        }
        radios.eq(6).iCheck("check");
    }
}

//解析周
function expWeek(val,type){
    //表达式结果框赋值
    $("span[name=v_"+type+"]").html(val);
    //寻找对应类型的radio列表
    var radios = $(":radio[name="+type+"]");
    var ary = null;
    //根据值的类型进行判断与对应赋值，以及radio的选中。
    if (val == "*") {
        radios.eq(0).iCheck("check");
    }else if (val == "?") {
        radios.eq(1).iCheck("check");
    }else if (val.split('-').length > 1) {
        ary = val.split('-');
        $(":text[name="+type+"]").eq(0).val(ary[0]);
        $(":text[name="+type+"]").eq(1).val(ary[1]);
        radios.eq(2).iCheck("check");
    } else if (val.split('/').length > 1) {
        ary = val.split('/');
        $(":text[name="+type+"]").eq(2).val(ary[0]);
        $(":text[name="+type+"]").eq(3).val(ary[1]);
        radios.eq(3).iCheck("check");
    }else if (val.split('#').length > 1) {
        ary = val.split('#');
        $(":text[name="+type+"]").eq(4).val(ary[0]);
        $(":text[name="+type+"]").eq(5).val(ary[1]);
        radios.eq(4).iCheck("check");
    } else if (val.split('L').length > 1) {
        ary = val.split('L');
        $(":text[name="+type+"]").eq(6).val(ary[0]);
        radios.eq(5).iCheck("check");
    }else {
        if (val != "?") {
            ary = val.split(",");
            for (var i = 0; i < ary.length; i++) {
                $("." + type + "List input[value='" + ary[i] + "']").iCheck("check");
            }
        }
        radios.eq(6).iCheck("check");
    }
}

//解析年
function expYear(val,type){
    //表达式结果框赋值
    $("span[name=v_"+type+"]").html(val);
    //寻找对应类型的radio列表
    var radios = $(":radio[name="+type+"]");
    var ary = null;
    //根据值的类型进行判断与对应赋值，以及radio的选中。
    if (val == "*") {
        radios.eq(1).iCheck("check");
    }else if (val.split('-').length > 1) {
        ary = val.split('-');
        $(":text[name="+type+"]").eq(0).val(ary[0]);
        $(":text[name="+type+"]").eq(1).val(ary[1]);
        radios.eq(2).iCheck("check");
    }
}