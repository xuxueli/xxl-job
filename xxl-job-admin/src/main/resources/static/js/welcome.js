$(function () {
    layui.use(["countUp"], function () {
        var countUp = layui.countUp;
        initRunCont(countUp);
        initDataTrendChart();
        initScheduleTodayChart();
        initScheduleWeekChart();
        initScheduleMonthChart();
    });
})

function initRunCont(countUp) {
    let dashboardInfo = http.get("log-report/run");

    if (!_.isEmpty(dashboardInfo)) {
        var jobNumber = $(".jobNumber");
        !new countUp({
            target: jobNumber,
            endVal: dashboardInfo.jobInfoCount,
        }).start();

        var jobLogCount = $(".jobLogCount");
        !new countUp({
            target: jobLogCount,
            endVal: dashboardInfo.jobLogCount,
        }).start();

        var executorCount = $(".executorCount");
        !new countUp({
            target: executorCount,
            endVal: dashboardInfo.executorCount,
        }).start();

        var userInfoCount = $(".userInfoCount");
        !new countUp({
            target: userInfoCount,
            endVal: dashboardInfo.userInfoCount,
        }).start();
    }
}

function dataTrendOption(name, color) {
    color = color || "#00c292";
    return {
        color: color, toolbox: {show: false, feature: {saveAsImage: {}}},
        grid: {left: '-1%', right: '0', bottom: '0', top: '5px', containLabel: false},
        xAxis: [
            {type: 'category', boundaryGap: false,
            splitLine: {show: false},
            data: ['成功', '失败', '运行中']
            }
        ],
        yAxis: [{type: 'value', splitLine: {show: false}}],
        series: [{
            name: name, type: 'line', stack: 'Total', smooth: true, symbol: "none", clickable: false, areaStyle: {},
            data: [randomData(), randomData(), randomData(), randomData(), randomData(), randomData(), randomData()]
        }]
    }
}

function randomData() {
    return Math.round(Math.random() * 500);
}

function initDataTrendChart() {
    var jobInfoNumber = echarts.init($("#jobInfoNumber")[0]);
    var jobInfoLogCount = echarts.init($('#jobInfoLogCount')[0]);
    var executorCountId = echarts.init($("#executorCountId")[0]);
    var userInfoNumberId = echarts.init($('#userInfoNumberId')[0]);
    jobInfoNumber.setOption(dataTrendOption("任务数量", "#00c292"));
    jobInfoLogCount.setOption(dataTrendOption("调度次数", "#ab8ce4"));
    executorCountId.setOption(dataTrendOption("执行器数量", "#03a9f3"));
    userInfoNumberId.setOption(dataTrendOption("用户", "#fb9678"));
    echartsResize([jobInfoNumber, jobInfoLogCount, executorCountId, userInfoNumberId]);
}

/**
 * 当天调度图
 */
function initScheduleTodayChart() {

    var start = moment().startOf('day').format('x');
    var end = moment().endOf('day').format('x');

    let dashboardInfo = http.getPath("log-report/trigger/" + start + "/" + end);
    if (!_.isEmpty(dashboardInfo)) {
        var scheduleTodayChartOption = {
            color: "#03a9f3",
            xAxis: {type: 'category', data: ['成功', '失败', '进行中']},
            yAxis: {type: 'value'},
            series: [{data: [dashboardInfo.sucTotal, dashboardInfo.failTotal, dashboardInfo.runTotal], type: 'bar'}]
        };

        var todayScheduleChart = echarts.init($("#todayScheduleMap")[0], "themez");
        todayScheduleChart.setOption(scheduleTodayChartOption);
        echartsResize([todayScheduleChart]);
    }
}

/**
 * 初始化周比例图
 */
function initScheduleWeekChart() {
    var now = new Date();
    let weekOfDay = moment(now).format('E');
    let startWeek = moment(now).subtract(weekOfDay-1, 'days').startOf('day').format("x");
    let endWeek = moment(now).subtract(weekOfDay-7, 'days').endOf('day').format("x");
    let weekDashboardInfo = http.getPath("log-report/trigger/" + startWeek + "/" + endWeek);
    if (!_.isEmpty(weekDashboardInfo)) {
        var weekScheduleScaleChartOption = {
            title: {show: false, text: '调度比例图', subtext: '', x: 'center'},
            tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
            legend: {orient: 'vertical', left: 'left', data: ['成功', '失败', '进行中']},
            series: [
                {
                    name: '调度比例',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '60%'],
                    data: [
                        {value: weekDashboardInfo.sucTotal, name: '成功'},
                        {value: weekDashboardInfo.failTotal, name: '失败'},
                        {value: weekDashboardInfo.runTotal, name: '进行中'},
                    ],
                    itemStyle: {emphasis: {shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)'}}
                }
            ]
        };

        var weekScheduleScaleChart = echarts.init($("#weekScheduleScaleChart")[0], "themez");
        weekScheduleScaleChart.setOption(weekScheduleScaleChartOption);
        echartsResize([weekScheduleScaleChart]);
    }
}

/**
 * 初始化月分布图
 */
function initScheduleMonthChart() {

    var start = moment().startOf('month').format('x');
    var end = moment().endOf('month').format('x');
    let dashboardInfo = http.getPath("log-report/trigger/" + start + "/" + end);
    if (!_.isEmpty(dashboardInfo)) {
        var monthScheduleChartOption = {
            title: {show: true, text: ''},
            tooltip: {trigger: 'axis', axisPointer: {type: 'cross', label: {backgroundColor: '#6a7985'}}},
            legend: {data: ['成功', '失败', '进行中']},
            toolbox: {show: false, feature: {saveAsImage: {}}},
            grid: {left: '3%', right: '4%', bottom: '3%', containLabel: true},
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: false,
                    // data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                    data: dashboardInfo.days,
                }
            ],
            yAxis: [{type: 'value', splitLine: {show: false},}],
            series: [
                {
                    name: '成功', type: 'line', stack: 'Total', smooth: true, areaStyle: {},
                    data: dashboardInfo.sucCount,
                },
                {
                    name: '失败', type: 'line', stack: 'Total', smooth: true, areaStyle: {},
                    label: {normal: {show: true, position: 'top'}},
                    data: dashboardInfo.failCount
                },
                {
                    name: '进行中', type: 'line', stack: 'Total', smooth: true, areaStyle: {},
                    data: dashboardInfo.runCount
                },
            ]
        };

        var monthScheduleChart = echarts.init($("#monthScheduleMap")[0], "themez");
        monthScheduleChart.setOption(monthScheduleChartOption);
        echartsResize([monthScheduleChart]);
    }
}

/**
 * 主要用于对ECharts视图自动适应宽度
 */
function echartsResize(element) {
    var element = element || [];
    window.addEventListener("resize", function () {
        var isResize = localStorage.getItem("isResize");
        for (let i = 0; i < element.length; i++) {
            element[i].resize();
        }
    });
}

















