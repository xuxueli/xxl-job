/**
 * Created by xuxueli on 17/4/24.
 */


$(function () {

    // lineChart
    var lineChart = echarts.init(document.getElementById('lineChart'));
    lineChart.setOption(lineChartDate());

    function lineChartDate() {
        var option = {
               title: {
                   text: '日期分布图'
               },
               tooltip : {
                   trigger: 'axis',
                   axisPointer: {
                       type: 'cross',
                       label: {
                           backgroundColor: '#6a7985'
                       }
                   }
               },
               legend: {
                   data:['成功调度次数','失败调度次数']
               },
               toolbox: {
                   feature: {
                       saveAsImage: {}
                   }
               },
               grid: {
                   left: '3%',
                   right: '4%',
                   bottom: '3%',
                   containLabel: true
               },
               xAxis : [
                   {
                       type : 'category',
                       boundaryGap : false,
                       data : ['2017-10-01','2017-10-02','2017-10-03','2017-10-04','2017-10-05','2017-10-06','2017-10-07']
                   }
               ],
               yAxis : [
                   {
                       type : 'value'
                   }
               ],
               series : [
                   {
                       name:'成功调度次数',
                       type:'line',
                       stack: '总量',
                       areaStyle: {normal: {}},
                       data:[820, 932, 901, 934, 1290, 1330, 1320]
                   },
                   {
                       name:'失败调度次数',
                       type:'line',
                       stack: '总量',
                       label: {
                           normal: {
                               show: true,
                               position: 'top'
                           }
                       },
                       areaStyle: {normal: {}},
                       data:[120, 132, 101, 134, 90, 230, 210]
                   }
               ],
                color:['#00A65A', '#F39C12']
        };
        return option;
    }

    // pie chart
    var pieChart = echarts.init(document.getElementById('pieChart'));
    pieChart.setOption(pieChartDate());

    function pieChartDate() {
        option = {
            title : {
                text: '调度总次数',
                /*subtext: 'subtext',*/
                x:'center'
            },
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: ['成功调度次数','失败调度次数']
            },
            series : [
                {
                    name: '访问来源',
                    type: 'pie',
                    radius : '55%',
                    center: ['50%', '60%'],
                    data:[
                        {value:800, name:'成功调度次数'},
                        {value:200, name:'失败调度次数'}
                    ],
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ],
            color:['#00A65A', '#F39C12']
        };
        return option;
    }

    // 过滤时间
    /*$('#filterTime').daterangepicker({
        autoApply:false,
        singleDatePicker:false,
        showDropdowns:false,        // 是否显示年月选择条件
        timePicker: true, 			// 是否显示小时和分钟选择条件
        timePickerIncrement: 10, 	// 时间的增量，单位为分钟
        timePicker24Hour : true,
        opens : 'left', //日期选择框的弹出位置
        ranges: {
            '最近1小时': [moment().subtract(1, 'hours'), moment()],
            '今日': [moment().startOf('day'), moment().endOf('day')],
            '昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            '最近7日': [moment().subtract(6, 'days'), moment()],
            '最近30日': [moment().subtract(29, 'days'), moment()],
            '本月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        },
        locale : {
            format: 'YYYY-MM-DD HH:mm:ss',
            separator : ' - ',
            customRangeLabel : '自定义',
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            toLabel : '结束时间',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1,
            startDate: moment().startOf('day'),
            endDate: moment().endOf('day')
        }
    });*/

});
