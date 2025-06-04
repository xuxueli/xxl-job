﻿(function ($) {
    // var resultsName = "";
    var inputElement;
    var displayElement;
    $.fn.extend({
        cronGen: function (options) {
            if (options == null) {
              options = {};
            }
            options = $.extend({}, $.fn.cronGen.defaultOptions, options);
            //create top menu
            var cronContainer = $("<div/>", { id: "CronContainer", style: "display:none;width:300px;height:300px;" });
            var mainDiv = $("<div/>", { id: "CronGenMainDiv", style: "width:410px;height:420px;" });
            var topMenu = $("<ul/>", { "class": "nav nav-tabs", id: "CronGenTabs" });
            $('<li/>', { 'class': 'active' }).html($('<a id="SecondlyTab" href="#Secondly">秒</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="MinutesTab" href="#Minutes">分钟</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="HourlyTab" href="#Hourly">小时</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="DailyTab" href="#Daily">日</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="MonthlyTab" href="#Monthly">月</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="WeeklyTab" href="#Weekly">周</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="YearlyTab" href="#Yearly">年</a>')).appendTo(topMenu);
            $(topMenu).appendTo(mainDiv);

            //create what's inside the tabs
            var container = $("<div/>", { "class": "container-fluid", "style": "margin-top: 30px;margin-left: -14px;" });
            var row = $("<div/>", { "class": "row-fluid" });
            var span12 = $("<div/>", { "class": "span12" });
            var tabContent = $("<div/>", { "class": "tab-content", "style": "border:0px; margin-top:-20px;" });


            //creating the secondsTab
            var secondsTab = $("<div/>", { "class": "tab-pane active", id: "Secondly" });
            var seconds1 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "1", name : "second"}).appendTo(seconds1);
            $(seconds1).append("每秒 允许的通配符[, - * /]");
            $(seconds1).appendTo(secondsTab);

            var seconds2 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "2", name : "second"}).appendTo(seconds2);
            $(seconds2).append("周期 从");
            $("<input/>",{type : "text", id : "secondStart_0", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(seconds2);
            $(seconds2).append("-");
            $("<input/>",{type : "text", id : "secondEnd_0", value : "2", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(seconds2);
            $(seconds2).append("秒");
            $(seconds2).appendTo(secondsTab);

            var seconds3 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "3", name : "second"}).appendTo(seconds3);
            $(seconds3).append("从");
            $("<input/>",{type : "text", id : "secondStart_1", value : "0", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(seconds3);
            $(seconds3).append("秒开始,每");
            $("<input/>",{type : "text", id : "secondEnd_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(seconds3);
            $(seconds3).append("秒执行一次");
            $(seconds3).appendTo(secondsTab);

            var seconds4 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "4", name : "second", id: "sencond_appoint"}).appendTo(seconds4);
            $(seconds4).append("指定");
            $(seconds4).appendTo(secondsTab);

            $(secondsTab).append('<div class="imp secondList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="0">00<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="1">01<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="2">02<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="3">03<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="4">04<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="5">05<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="6">06<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="7">07<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="8">08<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="9">09</div>');
            $(secondsTab).append('<div class="imp secondList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="10">10<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="11">11<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="12">12<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="13">13<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="14">14<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="15">15<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="16">16<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="17">17<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="18">18<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="19">19</div>');
            $(secondsTab).append('<div class="imp secondList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="20">20<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="21">21<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="22">22<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="23">23<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="24">24<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="25">25<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="26">26<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="27">27<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="28">28<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="29">29</div>');
            $(secondsTab).append('<div class="imp secondList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="30">30<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="31">31<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="32">32<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="33">33<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="34">34<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="35">35<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="36">36<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="37">37<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="38">38<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="39">39</div>');
            $(secondsTab).append('<div class="imp secondList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="40">40<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="41">41<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="42">42<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="43">43<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="44">44<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="45">45<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="46">46<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="47">47<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="48">48<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="49">49</div>');
            $(secondsTab).append('<div class="imp secondList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="50">50<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="51">51<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="52">52<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="53">53<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="54">54<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="55">55<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="56">56<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="57">57<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="58">58<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="59">59</div>');
            $("<input/>",{type : "hidden", id : "secondHidden"}).appendTo(secondsTab);
            $(secondsTab).appendTo(tabContent);

            //creating the minutesTab
            var minutesTab = $("<div/>", { "class": "tab-pane", id: "Minutes" });

            var minutes1 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "1", name : "min"}).appendTo(minutes1);
            $(minutes1).append("每分钟 允许的通配符[, - * /]");
            $(minutes1).appendTo(minutesTab);

            var minutes2 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "2", name : "min"}).appendTo(minutes2);
            $(minutes2).append("周期 从");
            $("<input/>",{type : "text", id : "minStart_0", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(minutes2);
            $(minutes2).append("-");
            $("<input/>",{type : "text", id : "minEnd_0", value : "2", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(minutes2);
            $(minutes2).append("分钟");
            $(minutes2).appendTo(minutesTab);

            var minutes3 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "3", name : "min"}).appendTo(minutes3);
            $(minutes3).append("从");
            $("<input/>",{type : "text", id : "minStart_1", value : "0", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(minutes3);
            $(minutes3).append("分钟开始,每");
            $("<input/>",{type : "text", id : "minEnd_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(minutes3);
            $(minutes3).append("分钟执行一次");
            $(minutes3).appendTo(minutesTab);

            var minutes4 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "4", name : "min", id: "min_appoint"}).appendTo(minutes4);
            $(minutes4).append("指定");
            $(minutes4).appendTo(minutesTab);

            $(minutesTab).append('<div class="imp minList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="0">00<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="1">01<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="2">02<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="3">03<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="4">04<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="5">05<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="6">06<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="7">07<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="8">08<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="9">09</div>');
            $(minutesTab).append('<div class="imp minList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="10">10<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="11">11<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="12">12<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="13">13<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="14">14<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="15">15<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="16">16<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="17">17<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="18">18<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="19">19</div>');
            $(minutesTab).append('<div class="imp minList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="20">20<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="21">21<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="22">22<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="23">23<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="24">24<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="25">25<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="26">26<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="27">27<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="28">28<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="29">29</div>');
            $(minutesTab).append('<div class="imp minList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="30">30<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="31">31<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="32">32<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="33">33<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="34">34<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="35">35<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="36">36<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="37">37<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="38">38<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="39">39</div>');
            $(minutesTab).append('<div class="imp minList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="40">40<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="41">41<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="42">42<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="43">43<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="44">44<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="45">45<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="46">46<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="47">47<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="48">48<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="49">49</div>');
            $(minutesTab).append('<div class="imp minList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="50">50<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="51">51<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="52">52<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="53">53<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="54">54<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="55">55<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="56">56<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="57">57<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="58">58<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="59">59</div>');
            $("<input/>",{type : "hidden", id : "minHidden"}).appendTo(minutesTab);
            $(minutesTab).appendTo(tabContent);

            //creating the hourlyTab
            var hourlyTab = $("<div/>", { "class": "tab-pane", id: "Hourly" });

            var hourly1 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "1", name : "hour"}).appendTo(hourly1);
            $(hourly1).append("每小时 允许的通配符[, - * /]");
            $(hourly1).appendTo(hourlyTab);

            var hourly2 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "2", name : "hour"}).appendTo(hourly2);
            $(hourly2).append("周期 从");
            $("<input/>",{type : "text", id : "hourStart_0", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(hourly2);
            $(hourly2).append("-");
            $("<input/>",{type : "text", id : "hourEnd_0", value : "2", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(hourly2);
            $(hourly2).append("小时");
            $(hourly2).appendTo(hourlyTab);

            var hourly3 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "3", name : "hour"}).appendTo(hourly3);
            $(hourly3).append("从");
            $("<input/>",{type : "text", id : "hourStart_1", value : "0", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(hourly3);
            $(hourly3).append("小时开始,每");
            $("<input/>",{type : "text", id : "hourEnd_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(hourly3);
            $(hourly3).append("小时执行一次");
            $(hourly3).appendTo(hourlyTab);

            var hourly4 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "4", name : "hour", id: "hour_appoint"}).appendTo(hourly4);
            $(hourly4).append("指定");
            $(hourly4).appendTo(hourlyTab);

            $(hourlyTab).append('<div class="imp hourList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="0">00<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="1">01<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="2">02<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="3">03<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="4">04<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="5">05</div>');
            $(hourlyTab).append('<div class="imp hourList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="6">06<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="7">07<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="8">08<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="9">09<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="10">10<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="11">11</div>');
            $(hourlyTab).append('<div class="imp hourList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="12">12<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="13">13<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="14">14<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="15">15<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="16">16<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="17">17</div>');
            $(hourlyTab).append('<div class="imp hourList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="18">18<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="19">19<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="20">20<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="21">21<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="22">22<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="23">23</div>');
            $("<input/>",{type : "hidden", id : "hourHidden"}).appendTo(hourlyTab);
            $(hourlyTab).appendTo(tabContent);


            //creating the dailyTab
            var dailyTab = $("<div/>", { "class": "tab-pane", id: "Daily" });

            var daily1 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "1", name : "day"}).appendTo(daily1);
            $(daily1).append("每天 允许的通配符[, - * / L W]");
            $(daily1).appendTo(dailyTab);

            var daily5 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "2", name : "day"}).appendTo(daily5);
            $(daily5).append("不指定");
            $(daily5).appendTo(dailyTab);

            var daily2 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "3", name : "day"}).appendTo(daily2);
            $(daily2).append("周期 从");
            $("<input/>",{type : "text", id : "dayStart_0", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(daily2);
            $(daily2).append("-");
            $("<input/>",{type : "text", id : "dayEnd_0", value : "2", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(daily2);
            $(daily2).append("日");
            $(daily2).appendTo(dailyTab);

            var daily3 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "4", name : "day"}).appendTo(daily3);
            $(daily3).append("从");
            $("<input/>",{type : "text", id : "dayStart_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(daily3);
            $(daily3).append("日开始,每");
            $("<input/>",{type : "text", id : "dayEnd_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(daily3);
            $(daily3).append("天执行一次");
            $(daily3).appendTo(dailyTab);

            var daily6 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "5", name : "day"}).appendTo(daily6);
            $(daily6).append("每月");
            $("<input/>",{type : "text", id : "dayStart_2", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(daily6);
            $(daily6).append("号最近的那个工作日");
            $(daily6).appendTo(dailyTab);

            var daily7 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "6", name : "day"}).appendTo(daily7);
            $(daily7).append("本月最后一天");
            $(daily7).appendTo(dailyTab);

            var daily4 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "7", name : "day", id: "day_appoint"}).appendTo(daily4);
            $(daily4).append("指定");
            $(daily4).appendTo(dailyTab);

            $(dailyTab).append('<div class="imp dayList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="1">01<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="2">02<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="3">03<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="4">04<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="5">05<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="6">06<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="7">07<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="8">08<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="9">09<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="10">10</div>');
            $(dailyTab).append('<div class="imp dayList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="11">11<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="12">12<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="13">13<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="14">14<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="15">15<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="16">16<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="17">17<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="18">18<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="19">19<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="20">20</div>');
            $(dailyTab).append('<div class="imp dayList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="21">21<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="22">22<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="23">23<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="24">24<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="25">25<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="26">26<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="27">27<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="28">28<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="29">29<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="30">30</div>');
            $(dailyTab).append('<div class="imp dayList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="31">31</div>');
            $("<input/>",{type : "hidden", id : "dayHidden"}).appendTo(dailyTab);
            $(dailyTab).appendTo(tabContent);


            //creating the monthlyTab
            var monthlyTab = $("<div/>", { "class": "tab-pane", id: "Monthly" });

            var monthly1 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "1", name : "month"}).appendTo(monthly1);
            $(monthly1).append("每月 允许的通配符[, - * /]");
            $(monthly1).appendTo(monthlyTab);

            var monthly2 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "2", name : "month"}).appendTo(monthly2);
            $(monthly2).append("不指定");
            $(monthly2).appendTo(monthlyTab);

            var monthly3 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "3", name : "month"}).appendTo(monthly3);
            $(monthly3).append("周期 从");
            $("<input/>",{type : "text", id : "monthStart_0", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(monthly3);
            $(monthly3).append("-");
            $("<input/>",{type : "text", id : "monthEnd_0", value : "2", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(monthly3);
            $(monthly3).append("月");
            $(monthly3).appendTo(monthlyTab);

            var monthly4 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "4", name : "month"}).appendTo(monthly4);
            $(monthly4).append("从");
            $("<input/>",{type : "text", id : "monthStart_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(monthly4);
            $(monthly4).append("月开始,每");
            $("<input/>",{type : "text", id : "monthEnd_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(monthly4);
            $(monthly4).append("月执行一次");
            $(monthly4).appendTo(monthlyTab);

            var monthly5 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "5", name : "month", id: "month_appoint"}).appendTo(monthly5);
            $(monthly5).append("指定");
            $(monthly5).appendTo(monthlyTab);

            $(monthlyTab).append('<div class="imp monthList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="1">01<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="2">02<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="3">03<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="4">04<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="5">05<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="6">06</div>');
            $(monthlyTab).append('<div class="imp monthList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="7">07<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="8">08<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="9">09<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="10">10<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="11">11<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="12">12</div>');
            $("<input/>",{type : "hidden", id : "monthHidden"}).appendTo(monthlyTab);
            $(monthlyTab).appendTo(tabContent);

            //creating the weeklyTab
            var weeklyTab = $("<div/>", { "class": "tab-pane", id: "Weekly" });

            var weekly1 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "1", name : "week"}).appendTo(weekly1);
            $(weekly1).append("每周 允许的通配符[, - * / L #]");
            $(weekly1).appendTo(weeklyTab);

            var weekly2 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "2", name : "week"}).appendTo(weekly2);
            $(weekly2).append("不指定");
            $(weekly2).appendTo(weeklyTab);

            var weekly3 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "3", name : "week"}).appendTo(weekly3);
            $(weekly3).append("周期 每周第");
            $("<input/>",{type : "text", id : "weekStart_0", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(weekly3);
            $(weekly3).append("天-第");
            $("<input/>",{type : "text", id : "weekEnd_0", value : "2", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(weekly3);
            $(weekly3).append("天");
            $(weekly3).appendTo(weeklyTab);

            var weekly4 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "4", name : "week"}).appendTo(weekly4);
            $(weekly4).append("从第");
            $("<input/>",{type : "text", id : "weekStart_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(weekly4);
            $(weekly4).append("天开始，间隔");
            $("<input/>",{type : "text", id : "weekEnd_1", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(weekly4);
            $(weekly4).append("天执行一次");
            $(weekly4).appendTo(weeklyTab);

            var weekly5 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "5", name : "week"}).appendTo(weekly5);
            $(weekly5).append("本月最后一周的第");
            $("<input/>",{type : "text", id : "weekStart_2", value : "1", style:"width:35px; height:20px; text-align: center; margin: 0 3px;"}).appendTo(weekly5);
            $(weekly5).append("天");
            $(weekly5).appendTo(weeklyTab);

            var weekly6 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "6", name : "week", id: "week_appoint"}).appendTo(weekly6);
            $(weekly6).append("指定");
            $(weekly6).appendTo(weeklyTab);

            $(weeklyTab).append('<div class="imp weekList"><input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="1">周日<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="2">周一<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="3">周二<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="4">周三<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="5">周四<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="6">周五<input type="checkbox" disabled="disabled" style="margin-left: 5px"  value="7">周六</div>');

            $("<input/>",{type : "hidden", id : "weekHidden"}).appendTo(weeklyTab);
            $(weeklyTab).appendTo(tabContent);

            //creating the yearlyTab
            var yearlyTab = $("<div/>", { "class": "tab-pane", id: "Yearly" });

            var yearly1 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "1", name : "year"}).appendTo(yearly1);
            $(yearly1).append("不指定 允许的通配符[, - * /] 非必填");
            $(yearly1).appendTo(yearlyTab);

            var yearly3 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "2", name : "year"}).appendTo(yearly3);
            $(yearly3).append("每年");
            $(yearly3).appendTo(yearlyTab);

            var yearly2 = $("<div/>",{"class":"line"});
            $("<input/>",{type : "radio", value : "3", name : "year"}).appendTo(yearly2);
            $(yearly2).append("周期从");
            $("<input/>",{type : "text", id : "yearStart_0", value : "2016", style:"width:45px; height:20px;"}).appendTo(yearly2);
            $(yearly2).append("-");
            $("<input/>",{type : "text", id : "yearEnd_0", value : "2017", style:"width:45px; height:20px;"}).appendTo(yearly2);
            $(yearly2).append("年");
            $(yearly2).appendTo(yearlyTab);
            $("<input/>",{type : "hidden", id : "yearHidden"}).appendTo(yearlyTab);
            $(yearlyTab).appendTo(tabContent);

            $(tabContent).appendTo(span12);

            //creating the button and results input
            // resultsName = $(this).prop("id");
            // $(this).prop("name", resultsName);

            var runTime = '<br style="padding-top: 10px"><label>最近运行时间: </label></br><textarea id="runTime" rows="6" style="width: 90%;resize: none;background: none;border: none;outline: none;" readonly = readonly></textarea></div>';

            $(span12).appendTo(row);
            $(row).appendTo(container);
            $(container).appendTo(mainDiv);
            $(runTime).appendTo(mainDiv);
            $(cronContainer).append(mainDiv);

            var that = $(this);

            // Hide the original input
            that.hide();

            // Replace the input with an input group
            var $g = $("<div>").addClass("input-group");
            // Add an input
            var $i = $("<input>", { type: 'text', placeholder: 'cron表达式...', name: 'cronGen_display' }).addClass("form-control").val($(that).val());
            $i.appendTo($g);
            // Add the button
            var $b = $("<button class=\"btn btn-default\"><i class=\"fa fa-edit\"></i></button>");
            // Put button inside span
            var $s = $("<span>").addClass("input-group-btn");
            $b.appendTo($s);
            $s.appendTo($g);

            $(this).before($g);

            inputElement = that;
            displayElement = $i;

            $b.popover({
                html: true,
                content: function () {
                    return $(cronContainer).html();
                },
                template: '<div class="popover" style="max-width:500px !important; width:425px;left:-341.656px;"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>',
                sanitize:false,
                placement: options.direction

            }).on('click', function (e) {
                if (inputElement.val().trim() !== '') {
                    refreshRunTime();
                }
                e.preventDefault();

                //fillDataOfMinutesAndHoursSelectOptions();
                //fillDayWeekInMonth();
                //fillInWeekDays();
                //fillInMonths();

                $.fn.cronGen.tools.cronParse(inputElement.val());

                //绑定指定事件
                $.fn.cronGen.tools.initChangeEvent();


                $('#CronGenTabs a').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                    //generate();
                });
                $("#CronGenMainDiv select,input").change(function (e) {
                    generate();
                    refreshRunTime();
                });
                $("#CronGenMainDiv input").focus(function (e) {
                    generate();
                });
                //generate();
            });
            return;
        }
    });


    var fillInMonths = function () {
        var days = [
            { text: "一月", val: "1" },
            { text: "二月", val: "2" },
            { text: "三月", val: "3" },
            { text: "四月", val: "4" },
            { text: "五月", val: "5" },
            { text: "六月", val: "6" },
            { text: "七月", val: "7" },
            { text: "八月", val: "8" },
            { text: "九月", val: "9" },
            { text: "十月", val: "10" },
            { text: "十一月", val: "11" },
            { text: "十二月", val: "12" }
        ];
        $(".months").each(function () {
            fillOptions(this, days);
        });
    };

    var fillOptions = function (elements, options) {
        for (var i = 0; i < options.length; i++)
            $(elements).append("<option value='" + options[i].val + "'>" + options[i].text + "</option>");
    };
    var fillDataOfMinutesAndHoursSelectOptions = function () {
        for (var i = 0; i < 60; i++) {
            if (i < 24) {
                $(".hours").each(function () { $(this).append(timeSelectOption(i)); });
            }
            $(".minutes").each(function () { $(this).append(timeSelectOption(i)); });
        }
    };
    var fillInWeekDays = function () {
        var days = [
            { text: "周一", val: "2" },
            { text: "周二", val: "3" },
            { text: "周三", val: "4" },
            { text: "周四", val: "5" },
            { text: "周五", val: "6" },
            { text: "周六", val: "7" },
            { text: "周天", val: "1" }
        ];
        $(".week-days").each(function () {
            fillOptions(this, days);
        });

    };
    var fillDayWeekInMonth = function () {
        var days = [
            { text: "第一个", val: "1" },
            { text: "第二个", val: "2" },
            { text: "第三个", val: "3" },
            { text: "第四个", val: "4" }
        ];
        $(".day-order-in-month").each(function () {
            fillOptions(this, days);
        });
    };
    var displayTimeUnit = function (unit) {
        if (unit.toString().length == 1)
            return "0" + unit;
        return unit;
    };
    var timeSelectOption = function (i) {
        return "<option id='" + i + "'>" + displayTimeUnit(i) + "</option>";
    };

    var generate = function () {

        var activeTab = $("ul#CronGenTabs li.active a").prop("id");
        if (activeTab == undefined) {
            return;
        }
        var results = "";
        switch (activeTab) {
            case "SecondlyTab":
                switch ($("input:radio[name=second]:checked").val()) {
                    case "1":
                        $.fn.cronGen.tools.everyTime("second");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "2":
                        $.fn.cronGen.tools.cycle("second");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "3":
                        $.fn.cronGen.tools.startOn("second");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "4":
                    	$.fn.cronGen.tools.initCheckBox("second");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                }
                break;
            case "MinutesTab":
                switch ($("input:radio[name=min]:checked").val()) {
                    case "1":
                        $.fn.cronGen.tools.everyTime("min");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "2":
                        $.fn.cronGen.tools.cycle("min");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "3":
                        $.fn.cronGen.tools.startOn("min");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "4":
                    	$.fn.cronGen.tools.initCheckBox("min");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                }
                break;
            case "HourlyTab":
                switch ($("input:radio[name=hour]:checked").val()) {
                    case "1":
                       $.fn.cronGen.tools.everyTime("hour");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "2":
                       $.fn.cronGen.tools.cycle("hour");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "3":
                        $.fn.cronGen.tools.startOn("hour");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "4":
                    	$.fn.cronGen.tools.initCheckBox("hour");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                }
                break;
            case "DailyTab":
                switch ($("input:radio[name=day]:checked").val()) {
                    case "1":
                        $.fn.cronGen.tools.everyTime("day");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "2":
                        $.fn.cronGen.tools.unAppoint("day");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "3":
                        $.fn.cronGen.tools.cycle("day");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "4":
                        $.fn.cronGen.tools.startOn("day");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "5":
                        $.fn.cronGen.tools.workDay("day");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "6":
                        $.fn.cronGen.tools.lastDay("day");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "7":
                    	$.fn.cronGen.tools.initCheckBox("day");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                }
                break;
            case "WeeklyTab":
                switch ($("input:radio[name=week]:checked").val()) {
                    case "1":
                        $.fn.cronGen.tools.everyTime("week");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "2":
                        $.fn.cronGen.tools.unAppoint("week");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "3":
                        $.fn.cronGen.tools.cycle("week");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "4":
                        $.fn.cronGen.tools.startOn("week");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "5":
                        $.fn.cronGen.tools.lastWeek("week");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "6":
                    	$.fn.cronGen.tools.initCheckBox("week");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                }
                break;
            case "MonthlyTab":
                switch ($("input:radio[name=month]:checked").val()) {
                    case "1":
                        $.fn.cronGen.tools.everyTime("month");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "2":
                        $.fn.cronGen.tools.unAppoint("month");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "3":
                        $.fn.cronGen.tools.cycle("month");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "4":
                        $.fn.cronGen.tools.startOn("month");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "5":
                    	$.fn.cronGen.tools.initCheckBox("month");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                }
                break;
            case "YearlyTab":
                switch ($("input:radio[name=year]:checked").val()) {
                    case "1":
                        $.fn.cronGen.tools.unAppoint("year");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "2":
                        $.fn.cronGen.tools.everyTime("year");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                    case "3":
                        $.fn.cronGen.tools.cycle("year");
                        results = $.fn.cronGen.tools.cronResult();
                        break;
                }
                break;
        }

        // Update original control
        inputElement.val(results);
        // Update display
        displayElement.val(results);
    };

    var refreshRunTime = function () {
        $.ajax({
            type : 'GET',
            url : base_url + "/jobinfo/nextTriggerTime",
            data : {
                "scheduleType" : 'CRON',
                "scheduleConf" : inputElement.val()
            },
            dataType : "json",
            success : function(data){
                if (data.code === 200) {
                    $('#runTime').val(data.content.join("\n"));
                } else {
                    $('#runTime').val(data.msg);
                }
            }
        });
    };

})(jQuery);

(function($) {
    $.fn.cronGen.defaultOptions = {
        direction : 'bottom'
    };
    $.fn.cronGen.tools = {
        /**
         * 每周期
         */
        everyTime : function(dom){
            $("#"+dom+"Hidden").val("*");
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        /**
         * 不指定
         */
        unAppoint : function(dom){
            var val = "?";
            if (dom == "year")
            {
                val = "";
            }
            $("#"+dom+"Hidden").val(val);
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        /**
         * 周期
         */
        cycle : function(dom){
            var start = $("#"+dom+"Start_0").val();
            var end = $("#"+dom+"End_0").val();
            $("#"+dom+"Hidden").val(start + "-" + end);
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        /**
         * 从开始
         */
        startOn : function(dom) {
            var start = $("#"+dom+"Start_1").val();
            var end = $("#"+dom+"End_1").val();
            $("#"+dom+"Hidden").val(start + "/" + end);
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        /**
         * 最后一天
         */
        lastDay : function(dom){
            $("#"+dom+"Hidden").val("L");
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        /**
         * 每周的某一天
         */
        weekOfDay : function(dom){
            var start = $("#"+dom+"Start_0").val();
            var end = $("#"+dom+"End_0").val();
            $("#"+dom+"Hidden").val(start + "#" + end);
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        /**
         * 最后一周
         */
        lastWeek : function(dom){
            var start = $("#"+dom+"Start_2").val();
            $("#"+dom+"Hidden").val(start+"L");
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        /**
         * 工作日
         */
        workDay : function(dom) {
            var start = $("#"+dom+"Start_2").val();
            $("#"+dom+"Hidden").val(start + "W");
            $.fn.cronGen.tools.clearCheckbox(dom);
        },
        initChangeEvent : function(){
            var secondList = $(".secondList").children();
            $("#sencond_appoint").click(function(){
                if (this.checked) {
                    if ($(secondList).filter(":checked").length == 0) {
                        $(secondList.eq(0)).attr("checked", true);
                    }
                    secondList.eq(0).change();
                }
            });

            secondList.change(function() {
                var sencond_appoint = $("#sencond_appoint").prop("checked");
                if (sencond_appoint) {
                    var vals = [];
                    secondList.each(function() {
                        if (this.checked) {
                            vals.push(this.value);
                        }
                    });
                    var val = "?";
                    if (vals.length > 0 && vals.length < 59) {
                        val = vals.join(",");
                    }else if(vals.length == 59){
                        val = "*";
                    }
                    $("#secondHidden").val(val);
                }
            });

            var minList = $(".minList").children();
            $("#min_appoint").click(function(){
                if (this.checked) {
                    if ($(minList).filter(":checked").length == 0) {
                        $(minList.eq(0)).attr("checked", true);
                    }
                    minList.eq(0).change();
                }
            });

            minList.change(function() {
                var min_appoint = $("#min_appoint").prop("checked");
                if (min_appoint) {
                    var vals = [];
                    minList.each(function() {
                        if (this.checked) {
                            vals.push(this.value);
                        }
                    });
                    var val = "?";
                    if (vals.length > 0 && vals.length < 59) {
                        val = vals.join(",");
                    }else if(vals.length == 59){
                        val = "*";
                    }
                    $("#minHidden").val(val);
                }
            });

            var hourList = $(".hourList").children();
            $("#hour_appoint").click(function(){
                if (this.checked) {
                    if ($(hourList).filter(":checked").length == 0) {
                        $(hourList.eq(0)).attr("checked", true);
                    }
                    hourList.eq(0).change();
                }
            });

            hourList.change(function() {
                var hour_appoint = $("#hour_appoint").prop("checked");
                if (hour_appoint) {
                    var vals = [];
                    hourList.each(function() {
                        if (this.checked) {
                            vals.push(this.value);
                        }
                    });
                    var val = "?";
                    if (vals.length > 0 && vals.length < 24) {
                        val = vals.join(",");
                    }else if(vals.length == 24){
                        val = "*";
                    }
                    $("#hourHidden").val(val);
                }
            });

            var dayList = $(".dayList").children();
            $("#day_appoint").click(function(){
                if (this.checked) {
                    if ($(dayList).filter(":checked").length == 0) {
                        $(dayList.eq(0)).attr("checked", true);
                    }
                    dayList.eq(0).change();
                }
            });

            dayList.change(function() {
                var day_appoint = $("#day_appoint").prop("checked");
                if (day_appoint) {
                    var vals = [];
                    dayList.each(function() {
                        if (this.checked) {
                            vals.push(this.value);
                        }
                    });
                    var val = "?";
                    if (vals.length > 0 && vals.length < 31) {
                        val = vals.join(",");
                    }else if(vals.length == 31){
                        val = "*";
                    }
                   $("#dayHidden").val(val);
                }
            });

            var monthList = $(".monthList").children();
            $("#month_appoint").click(function(){
                if (this.checked) {
                    if ($(monthList).filter(":checked").length == 0) {
                        $(monthList.eq(0)).attr("checked", true);
                    }
                    monthList.eq(0).change();
                }
            });

            monthList.change(function() {
                var month_appoint = $("#month_appoint").prop("checked");
                if (month_appoint) {
                    var vals = [];
                    monthList.each(function() {
                        if (this.checked) {
                            vals.push(this.value);
                        }
                    });
                    var val = "?";
                    if (vals.length > 0 && vals.length < 12) {
                        val = vals.join(",");
                    }else if(vals.length == 12){
                        val = "*";
                    }
                    $("#monthHidden").val(val);
                }
            });

            var weekList = $(".weekList").children();
            $("#week_appoint").click(function(){
                if (this.checked) {
                    if ($(weekList).filter(":checked").length == 0) {
                        $(weekList.eq(0)).attr("checked", true);
                    }
                    weekList.eq(0).change();
                }
            });

            weekList.change(function() {
                var week_appoint = $("#week_appoint").prop("checked");
                if (week_appoint) {
                    var vals = [];
                    weekList.each(function() {
                        if (this.checked) {
                            vals.push(this.value);
                        }
                    });
                    var val = "?";
                    if (vals.length > 0 && vals.length < 7) {
                        val = vals.join(",");
                    }else if(vals.length == 7){
                        val = "*";
                    }
                   $("#weekHidden").val(val);
                }
            });
        },
        initObj : function(strVal, strid){
            var ary = null;
            var objRadio = $("input[name='" + strid + "'");
            if (strVal == "*") {
                objRadio.eq(0).attr("checked", "checked");
            } else if (strVal.split('-').length > 1) {
                ary = strVal.split('-');
                objRadio.eq(1).attr("checked", "checked");
                $("#" + strid + "Start_0").val(ary[0]);
                $("#" + strid + "End_0").val(ary[1]);
            } else if (strVal.split('/').length > 1) {
                ary = strVal.split('/');
                objRadio.eq(2).attr("checked", "checked");
                $("#" + strid + "Start_1").val(ary[0]);
                $("#" + strid + "End_1").val(ary[1]);
            } else {
                objRadio.eq(3).attr("checked", "checked");
                if (strVal != "?") {
                    ary = strVal.split(",");
                    for (var i = 0; i < ary.length; i++) {
                        $("." + strid + "List input[value='" + ary[i] + "']").attr("checked", "checked");
                    }
                    $.fn.cronGen.tools.initCheckBox(strid);
                }
            }
        },
        initDay : function(strVal) {
            var ary = null;
            var objRadio = $("input[name='day'");
            if (strVal == "*") {
                objRadio.eq(0).attr("checked", "checked");
            } else if (strVal == "?") {
                objRadio.eq(1).attr("checked", "checked");
            } else if (strVal.split('-').length > 1) {
                ary = strVal.split('-');
                objRadio.eq(2).attr("checked", "checked");
                $("#dayStart_0").val(ary[0]);
                $("#dayEnd_0").val(ary[1]);
            } else if (strVal.split('/').length > 1) {
                ary = strVal.split('/');
                objRadio.eq(3).attr("checked", "checked");
                $("#dayStart_1").val(ary[0]);
                $("#dayEnd_1").val(ary[1]);
            } else if (strVal.split('W').length > 1) {
                ary = strVal.split('W');
                objRadio.eq(4).attr("checked", "checked");
                $("#dayStart_2").val(ary[0]);
            } else if (strVal == "L") {
                objRadio.eq(5).attr("checked", "checked");
            } else {
                objRadio.eq(6).attr("checked", "checked");
                ary = strVal.split(",");
                for (var i = 0; i < ary.length; i++) {
                    $(".dayList input[value='" + ary[i] + "']").attr("checked", "checked");
                }
                $.fn.cronGen.tools.initCheckBox("day");
            }
        },
        initMonth : function(strVal) {
            var ary = null;
            var objRadio = $("input[name='month'");
            if (strVal == "*") {
                objRadio.eq(0).attr("checked", "checked");
            } else if (strVal == "?") {
                objRadio.eq(1).attr("checked", "checked");
            } else if (strVal.split('-').length > 1) {
                ary = strVal.split('-');
                objRadio.eq(2).attr("checked", "checked");
                $("#monthStart_0").val(ary[0]);
                $("#monthEnd_0").val(ary[1]);
            } else if (strVal.split('/').length > 1) {
                ary = strVal.split('/');
                objRadio.eq(3).attr("checked", "checked");
                $("#monthStart_1").val(ary[0]);
                $("#monthEnd_1").val(ary[1]);

            } else {
                objRadio.eq(4).attr("checked", "checked");

                ary = strVal.split(",");
                for (var i = 0; i < ary.length; i++) {
                    $(".monthList input[value='" + ary[i] + "']").attr("checked", "checked");
                }
                $.fn.cronGen.tools.initCheckBox("month");
            }
        },
        initWeek : function(strVal) {
            var ary = null;
            var objRadio = $("input[name='week'");
            if (strVal == "*") {
                objRadio.eq(0).attr("checked", "checked");
            } else if (strVal == "?") {
                objRadio.eq(1).attr("checked", "checked");
            } else if (strVal.split('/').length > 1) {
                ary = strVal.split('/');
                objRadio.eq(2).attr("checked", "checked");
                $("#weekStart_0").val(ary[0]);
                $("#weekEnd_0").val(ary[1]);
            } else if (strVal.split('-').length > 1) {
                ary = strVal.split('-');
                objRadio.eq(3).attr("checked", "checked");
                $("#weekStart_1").val(ary[0]);
                $("#weekEnd_1").val(ary[1]);
            } else if (strVal.split('L').length > 1) {
                ary = strVal.split('L');
                objRadio.eq(4).attr("checked", "checked");
                $("#weekStart_2").val(ary[0]);
            } else {
                objRadio.eq(5).attr("checked", "checked");
                ary = strVal.split(",");
                for (var i = 0; i < ary.length; i++) {
                    $(".weekList input[value='" + ary[i] + "']").attr("checked", "checked");
                }
                $.fn.cronGen.tools.initCheckBox("week");
            }
        },
        initYear : function(strVal) {
            var ary = null;
            var objRadio = $("input[name='year'");
            if (strVal == "*") {
                objRadio.eq(1).attr("checked", "checked");
            } else if (strVal.split('-').length > 1) {
                ary = strVal.split('-');
                objRadio.eq(2).attr("checked", "checked");
                $("#yearStart_0").val(ary[0]);
                $("#yearEnd_0").val(ary[1]);
            }
        },
        cronParse : function(cronExpress) {
            //获取参数中表达式的值
            if (cronExpress) {
                var regs = cronExpress.split(' ');
                $("#secondHidden").val(regs[0]);
                $("#minHidden").val(regs[1]);
                $("#hourHidden").val(regs[2]);
                $("#dayHidden").val(regs[3]);
                $("#monthHidden").val(regs[4]);
                $("#weekHidden").val(regs[5]);

                $.fn.cronGen.tools.initObj(regs[0], "second");
                $.fn.cronGen.tools.initObj(regs[1], "min");
                $.fn.cronGen.tools.initObj(regs[2], "hour");
                $.fn.cronGen.tools.initDay(regs[3]);
                $.fn.cronGen.tools.initMonth(regs[4]);
                $.fn.cronGen.tools.initWeek(regs[5]);

                if (regs.length > 6) {
                    $("input[name=yearHidden]").val(regs[6]);
                    $.fn.cronGen.tools.initYear(regs[6]);
                }
            }
    	},
        cronResult : function() {
            var result;
            var second = $("#secondHidden").val();
            second = second== "" ? "*":second;
            var minute = $("#minHidden").val();
            minute = minute== "" ? "*":minute;
            var hour = $("#hourHidden").val();
            hour = hour== "" ? "*":hour;
            var day = $("#dayHidden").val();
            day = day== "" ? "*":day;
            var month = $("#monthHidden").val();
            month = month== "" ? "*":month;
            var week = $("#weekHidden").val();
            week = week== "" ? "?":week;
            var year = $("#yearHidden").val();
            if(year!="")
            {
                result = second+" "+minute+" "+hour+" "+day+" "+month+" "+week+" "+year;
            }else
            {
                result = second+" "+minute+" "+hour+" "+day+" "+month+" "+week;
            }
            return result;
        },
        clearCheckbox : function(dom){
        	//清除选中的checkbox
            var list = $("."+dom+"List").children().filter(":checked");
            if ($(list).length > 0) {
            	$.each(list, function(index){
            		$(this).attr("checked", false);
            		$(this).attr("disabled", "disabled");
            		$(this).change();
            	});
            }
        },
        initCheckBox : function(dom) {
        	//移除checkbox禁用
            var list = $("."+dom+"List").children();
            if ($(list).length > 0) {
            	$.each(list, function(index){
            		$(this).removeAttr("disabled");
            	});
            }
        }
    };
})(jQuery);
