<#macro cronHeadStyle>
    <link href="${request.contextPath}/static/cron/icheck/skin/blue.css" rel="stylesheet">
    <link href="${request.contextPath}/static/cron/button/buttons.css" rel="stylesheet">
    <style>
        .p_m{
            padding: 0px;
            margin:1px ;
            width: 50px;
        }
        .list_60{
            width: 522px;
            height:180px ;
            margin-left: 45px;
        }
        .list_24{
            width: 322px;
            height:180px ;
            margin-left: 45px;
        }
        .list_31{
            width: 418px;
            height:180px ;
            margin-left: 45px;
        }
        .list_12{
            width: 314px;
            height:180px ;
            margin-left: 45px;
        }
        .list_7{
            width: 522px;
            height:180px ;
            margin-left: 45px;
        }
        .margin-left-head{
            margin-left: 6px;
        }
        .fade_tab{
            margin-top: 8px;
        }
    </style>
</#macro>
<#macro cronContent>

<div class="container-fluid">
    <div class="col-xs-12 col-sm-12 col-md-10 col-md-offset-1">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h3 class="panel-title">生成器</h3>
            </div>
            <div class="panel-body">

                <ul id="myTab" class="nav nav-tabs">
                    <li class="active"><a href="#t_second" data-toggle="tab">秒</a></li>
                    <li><a href="#t_min" data-toggle="tab">分</a></li>
                    <li><a href="#t_hour" data-toggle="tab">时</a></li>
                    <li><a href="#t_day" data-toggle="tab">日</a></li>
                    <li><a href="#t_month" data-toggle="tab">月</a></li>
                    <li><a href="#t_quarter" data-toggle="tab">季</a></li>
                    <li><a href="#t_week" data-toggle="tab">周</a></li>
                    <li><a href="#t_year" data-toggle="tab">年</a></li>
                    <li><a href="#t_common" data-toggle="tab">常用表达式</a></li>
                </ul>


                <div id="myTabContent" class="tab-content">
                    <div class="tab-pane fade " id="t_common">
                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-8 col-sm-6 col-md-4">
                                    每隔5秒执行一次：*/5 * * * * ?
                                </div>
                            </div>

                            </p>
                        </div>
                        <div class="radiocheck">
                            <p>
                            <div class="row">
                                <div class="col-xs-8 col-sm-6 col-md-4">
                                    每隔10分钟执行一次：0 */10 * * * ?
                                </div>
                            </div>
                            </p>
                        </div>
                        <div class="radiocheck">
                            <p>
                            <div class="row">
                                <div class="col-xs-8 col-sm-6 col-md-4">
                                    每天23点执行一次：0 0 23 * * ?
                                </div>
                            </div>
                            </p>
                        </div>
                        <div class="radiocheck">
                            <p>
                            <div class="row">
                                <div class="col-xs-8 col-sm-6 col-md-4">
                                    每月凌晨2点30分执行一次：0 30 2 * * ?

                                </div>
                            </div>
                            </p>
                        </div>
                        <div class="radiocheck">
                            <p>
                            <div class="row">
                                <div class="col-xs-8 col-sm-6 col-md-4">
                                    在26分、29分执行一次：0 26,29 * * * ?

                                </div>
                            </div>
                            </p>
                        </div>
                    </div>
                    <div class="tab-pane fade fade_tab in active" id="t_second">

                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_second" name="second" class="firstradio">
                                <span class="margin-left-head"></span>每秒 <span class="span-title">允许的通配符[, - * /]</span>
                            </p></div>

                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-8 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        <input type="radio" name="second" class="cycleradio">
                                        <span class="margin-left-head"></span>周期从
                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center"
                                               class="numberspinner"
                                               name="second">
                                        <a href="javascript:;" data-spin="down"><i
                                                    class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                    class="  icon-plus-sign icon-large"></i></a> 秒
                                    </div>

                                </div>
                                <div class="col-xs-8 col-sm-6 col-md-2">
                                    <div data-trigger="spinner" class="cyclespin">
                                        到
                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center"
                                               class="numberspinner"
                                               name="second">
                                        <a href="javascript:;" data-spin="down"><i
                                                    class="  icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        秒
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        <input type="radio" name="second" class="loopradio">
                                       <span class="margin-left-head"></span>循环从
                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center" class="numberspinner"
                                               name="second">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 秒 开始
                                    </div>
                                </div>

                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        每
                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center" class="numberspinner"
                                               name="second">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        秒 执行一次
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p><input type="radio" name="second" class="choiceradio"><span class="margin-left-head"></span>自定义

                            <div class="row">
                                <div class="secondList list_60" id="l_second">

                                </div>
                            </div>
                            </p>
                        </div>
                    </div>


                    <div class="tab-pane fade_tab fade" id="t_min">
                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_minute" name="min" class="firstradio">
                                <span class="margin-left-head"></span> 每分 <span
                                        class="span-title">允许的通配符[, - * /]</span></p></div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        <input type="radio" name="min" class="cycleradio">
                                       <span class="margin-left-head"></span>周期从
                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center" class="numberspinner"
                                               name="min">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 分
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        到

                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center" class="numberspinner"
                                               name="min">
                                        <a href="javascript:;" data-spin="down"><i
                                                class="  icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        分
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        <input type="radio" name="min" class="loopradio">
                                       <span class="margin-left-head"></span>循环从
                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center" class="numberspinner"
                                               name="min">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 分开始
                                    </div>
                                </div>

                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        每
                                        <input type="text" data-max="59" value="0" style="width:35px;text-align:center" class="numberspinner"
                                               name="min">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        分 执行一次
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>

                        <div class="radiocheck">
                            <p><input type="radio" name="min" class="choiceradio"><span class="margin-left-head"></span>自定义

                            <div class="row">
                                <div class="minList list_60" id="l_min">

                                </div>
                            </div>
                            </p>
                        </div>
                    </div>

                    <div class="tab-pane fade_tab fade" id="t_hour">

                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_hour" name="hour"
                                                          class="firstradio">
                                                          <span class="margin-left-head"></span>
                             每时 <span class="span-title">允许的通配符[, - * /]</span></p></div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        <input type="radio" name="hour" class="cycleradio">
                                       <span class="margin-left-head"></span>周期从
                                        <input type="text" data-rule="hour" style="width:35px;text-align:center" class="numberspinner"
                                               name="hour">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 时
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        到
                                        <input type="text" data-rule="hour" style="width:35px;text-align:center" class="numberspinner"
                                               name="hour">
                                        <a href="javascript:;" data-spin="down"><i
                                                class="  icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        时
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        <input type="radio" name="hour" class="loopradio">
                                       <span class="margin-left-head"></span>循环从
                                        <input type="text" data-rule="hour" style="width:35px;text-align:center" class="numberspinner"
                                               name="hour">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 时 开始
                                    </div>
                                </div>

                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        每

                                        <input type="text" data-rule="hour" style="width:35px;text-align:center" class="numberspinner"
                                               name="hour">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        时 执行一次
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>

                        <div class="radiocheck">
                            <p><input type="radio" name="hour" class="choiceradio"><span class="margin-left-head"></span>自定义

                            <div class="row">
                                <div class="hourList list_24" id="l_hour">

                                </div>
                            </div>
                            </p>
                        </div>

                    </div>

                    <div class="tab-pane fade_tab fade" id="t_day">
                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_day" name="day"
                                                          class="firstradio">
                                                          <span class="margin-left-head"></span>
                            每日 <span class="span-title">允许的通配符[, - * / L W]</span></p></div>

                        <div class="radiocheck"><p><input type="radio" name="day" class="noconfirmradio">
                            <span class="margin-left-head"></span>
                            不指定(指定周时，日 需设置为不指定)</p></div>

                        <div class="radiocheck"><p><input type="radio" name="day" class="lastdayradio">
                            <span class="margin-left-head"></span>
                              月最后一日</p></div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        <input type="radio" name="day" class="cycleradio">
                                       <span class="margin-left-head"></span>周期从
                                        <input type="text" data-rule="day" style="width:35px;text-align:center" class="numberspinner"
                                               name="day">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 日
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        到

                                        <input type="text" data-rule="day" style="width:35px;text-align:center" class="numberspinner"
                                               name="day">
                                        <a href="javascript:;" data-spin="down"><i
                                                class="  icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        日
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        <input type="radio" name="day" class="loopradio">
                                       <span class="margin-left-head"></span>循环从
                                        <input type="text" data-rule="day" style="width:35px;text-align:center" class="numberspinner"
                                               name="day">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 日 开始
                                    </div>
                                </div>

                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        每

                                        <input type="text" data-rule="day" style="width:35px;text-align:center" class="numberspinner"
                                               name="day">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        日 执行一次
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>

                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-6">
                                    <div data-trigger="spinner" class="nearspin">
                                        <input type="radio" name="day" class="nearradio">
                                       <span class="margin-left-head"></span>每月&nbsp; 离
                                        <input type="text" data-rule="day" style="width:35px;text-align:center" class="numberspinner"
                                               name="day">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 日 最近工作日
                                    </div>
                                </div>

                            </div>
                            </p>
                        </div>

                        <div class="radiocheck">
                            <p><input type="radio" name="day" class="choiceradio">  <span class="margin-left-head"></span>自定义

                            <div class="row">
                                <div class="dayList list_31" id="l_day">

                                </div>
                            </div>
                            </p>
                        </div>

                    </div>

                    <div class="tab-pane fade_tab fade" id="t_month">

                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_month" name="month"
                                                          class="firstradio">
                                                          <span class="margin-left-head"></span>
                            每月 <span class="span-title">允许的通配符[, - * /]</span></p></div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        <input type="radio" name="month" class="cycleradio">
                                       <span class="margin-left-head"></span>周期从
                                        <input type="text" data-rule="month" style="width:35px;text-align:center" class="numberspinner"
                                               name="month">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 月
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        到

                                        <input type="text" data-rule="month" style="width:35px;text-align:center" class="numberspinner"
                                               name="month">
                                        <a href="javascript:;" data-spin="down"><i
                                                class="  icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        月
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        <input type="radio" name="month" class="loopradio">
                                       <span class="margin-left-head"></span>循环从
                                        <input type="text" data-rule="month" style="width:35px;text-align:center" class="numberspinner"
                                               name="month">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 月 开始
                                    </div>
                                </div>

                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        每

                                        <input type="text" data-rule="month" style="width:35px;text-align:center" class="numberspinner"
                                               name="month">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        月 执行一次
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>

                        <div class="radiocheck">
                            <p><input type="radio" name="month" class="choiceradio">  <span class="margin-left-head"></span>自定义

                            <div class="row">
                                <div class="monthList list_12" id="l_month">

                                </div>
                            </div>
                            </p>
                        </div>

                    </div>


                    <div class="tab-pane fade_tab fade" id="t_quarter">
                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_quarter" name="quarter"
                                                          class="firstradioreactor">
                                                          <span class="margin-left-head"></span>不指定</p></div>


                        <div class="radiocheck">
                            <p><input type="radio" name="quarter" class="choiceradio"><span class="margin-left-head"></span>自定义

                            <div class="row">
                                <div class="qtList" id="quarter_spe">
                                    <div class='col-xs-1 col-sm-1 col-md-2'><label><input type='checkbox'
                                                                                          value="1,4,7,10"> <span class="margin-left-head"></span>每季度第一个月</label>
                                    </div>
                                    <div class='col-xs-1 col-sm-1 col-md-2'><label><input type='checkbox'
                                                                                          value="2,5,8,11"> <span class="margin-left-head"></span>每季度第二个月</label>
                                    </div>
                                    <div class='col-xs-1 col-sm-1 col-md-2'><label><input type='checkbox'
                                                                                          value="3,6,9,12"> <span class="margin-left-head"></span>每季度第三个月</label>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="monthList list_12" id="l_quarter">
                                </div>
                            </div>
                            </p>
                        </div>
                    </div>
                    <div class="tab-pane fade_tab fade" id="t_week">
                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_week" name="week"
                                                          class="firstradio">
                                                          <span class="margin-left-head"></span> 每周 <span class="span-title">允许的通配符[, - * / L #]</span></p></div>

                        <div class="radiocheck"><p><input type="radio" name="week" class="noconfirmradio">
                            <span class="margin-left-head"></span> 不指定(指定日时，周 需设置为不指定)</p></div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        <input type="radio" name="week" class="cycleradio">
                                       <span class="margin-left-head"></span>周期从星期
                                        <input type="text" data-rule="percent" style="width:35px;text-align:center" class="numberspinner"
                                               name="week">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a>
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        到星期
                                        <input type="text" data-rule="percent" style="width:35px;text-align:center" class="numberspinner"
                                               name="week">
                                        <a href="javascript:;" data-spin="down"><i
                                                class="  icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>

                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        <input type="radio" name="week" class="loopradio">
                                       <span class="margin-left-head"></span>循环从星期
                                        <input type="text" data-rule="percent" style="width:35px;text-align:center" class="numberspinner"
                                               name="week">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 开始
                                    </div>
                                </div>

                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="loopspin">
                                        每隔
                                        <input type="text" data-rule="percent" style="width:35px;text-align:center" class="numberspinner"
                                               name="week">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        天执行一次

                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="designspin">
                                        <input type="radio" name="week" class="designradio">
                                        <span><span class="margin-left-head"></span>指定</span><span style="margin-left: 29px;">第</span>
                                        <!--<span class="margin-left-head"></span>指定&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第 -->
                                        <input type="text" data-rule="percent" style="width:35px;text-align:center" class="numberspinner"
                                               name="week">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a> 周的
                                    </div>
                                </div>

                                <div class="col-xs-12 col-sm-12 col-md-3">
                                    <div data-trigger="spinner" class="designspin">
                                        星期
                                        <input type="text" data-rule="percent" style="width:35px;text-align:center" class="numberspinner"
                                               name="week">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>

                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>

                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-6">
                                    <div data-trigger="spinner" class="lastspin">
                                        <input type="radio" name="week" class="lastradio">
                                       <span class="margin-left-head"></span>本月最后一个周
                                        <input type="text" data-rule="percent" style="width:35px;text-align:center" class="numberspinner"
                                               name="week">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a>
                                    </div>
                                </div>

                            </div>
                            </p>
                        </div>

                        <div class="radiocheck">
                            <p><input type="radio" name="week" class="choiceradio">  <span class="margin-left-head"></span>自定义

                            <div class="row">
                                <div class="weekList list_7" id="l_week">

                                </div>
                            </div>
                            </p>
                        </div>
                    </div>
                    <div class="tab-pane fade_tab fade" id="t_year">
                        <div class="radiocheck"><p><input type="radio" checked="checked" id="r_year" name="year"
                                                          class="unselectradio">
                                                          <span class="margin-left-head"></span>
                            不指定 <span class="span-title">允许的通配符[, - * /] 非必填</span></p></div>
                        <div class="radiocheck"><p><input type="radio" name="year"
                                                          class="firstradio">
                                                          <span class="margin-left-head"></span> 每年</p></div>


                        <div class="radiocheck">
                            <p>

                            <div class="row">
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        <input type="radio" name="year" class="cycleradio">
                                       <span class="margin-left-head"></span>周期从
                                        <input type="text" style="width:50px;text-align:center" class="numberspinner"
                                               name="year" value="2019">
                                        <a href="javascript:;" data-spin="down"><i
                                                class=" icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i
                                                class="  icon-plus-sign icon-large"></i></a>
                                                年
                                    </div>
                                </div>
                                <div class="col-xs-12 col-sm-6 col-md-3">
                                    <div data-trigger="spinner" class="cyclespin">
                                        到

                                        <input type="text" style="width:50px;text-align:center" class="numberspinner"
                                               name="year" value="2020">
                                        <a href="javascript:;" data-spin="down"><i
                                                class="  icon-minus-sign icon-large "></i></a>
                                        <a href="javascript:;" data-spin="up"><i class="icon-plus-sign icon-large"></i></a>
                                        年
                                    </div>
                                </div>
                            </div>
                            </p>
                        </div>


                    </div>


                </div>


                <div class="panel panel-primary" style="height:371px">
                    <div class="panel-heading">
                        <h3 class="panel-title">表达式结果</h3>
                    </div>

                    <div class="panel-body">
                        <div class="table-responsive">
                            <table class="table table-bordered">

                                <thead>
                                <tr>
                                    <th>秒</th>
                                    <th>分</th>
                                    <th>时</th>
                                    <th>日</th>
                                    <th>月</th>
                                    <th>周</th>
                                    <th>年</th>

                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><span name="v_second">*</span></td>
                                    <td><span name="v_min">*</span></td>
                                    <td><span name="v_hour">*</span></td>
                                    <td><span name="v_day">*</span></td>
                                    <td><span name="v_month">*</span></td>
                                    <td><span name="v_week">?</span></td>
                                    <td><span name="v_year"></span></td>
                                </tr>
                                </tbody>
                            </table>

                        </div>

                        <div class="well well-sm">
                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-3" style="padding-right:0px;width:107px ">
                                    <b>Cron表达式：</b>
                                </div>
                                <div class="col-xs-12 col-sm-12 col-md-7" style="padding-left:0px ">
                                    <input type="text" name="cron" style="width: 100%;" value="* * * * * ?" id="cron"/>
                                    <input type="hidden" id="transCron" name="transCron" value=""/>
                            </div>
                                <div class="col-xs-12 col-sm-12 col-md-2">
                                    <a id="explain" href="javascript:void(0)"
                                       class="button button-3d button-primary button-rounded button-small">解析到生成器</a>
                                </div>
                            </div>
                        </div>
                        <div class="well well-sm " style="display: none" id="explain_express_list">
                            <#--<div class="row">-->
                                <#--<div class="col-xs-12 col-sm-6 col-md-3"> 2019-07-12 12:00:00</div>-->
                            <#--</div>-->
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
</#macro>
<#macro cronSript>
<script src="${request.contextPath}/static/cron/icheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/cron/spinner/jquery.spinner.min.js"></script>
<script src="${request.contextPath}/static/cron/init.js"></script>
<script src="${request.contextPath}/static/cron/cronboot.js?v=1"></script>
</#macro>
