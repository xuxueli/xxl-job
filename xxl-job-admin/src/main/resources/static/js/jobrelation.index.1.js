
     


    $('#currentRelationSearchBtn').on('click', function(){
        var jobDescEl = document.getElementById("jobDesc");
        var jobDesc = jobDescEl.value;
        relationTree(jobDesc);
    });

function relationTree(jobDesc){
    var url = base_url + "/jobrelation/findAllRelationByDesc";
    var param = {"jobDesc": jobDesc};
    $.ajax({
        url: url,
        data: param,
        type: 'post',
        async: false,
        dataType: 'json',
        success: function(result){
            var tree = echarts.init(document.getElementById("relationTree"));
            tree.showLoading();
            tree.hideLoading();
            tree.setOption(option = {
                                  tooltip: {    //提示框组件
                                      trigger: 'item',    //触发类型，默认：item（数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用）。可选：'axis'：坐标轴触发，主要在柱状图，折线图等会使用类目轴的图表中使用。'none':什么都不触发。
                                      triggerOn: 'mousemove'    //提示框触发的条件，默认mousemove|click（鼠标点击和移动时触发）。可选mousemove：鼠标移动时，click：鼠标点击时，none：
                                  },
                                  series: [    //系列列表
                                      {
                                          type: 'tree',    //树形结构
                                          orient: 'LR',
                                          edgeShape: 'curve',
                                          data: [result.data],    //上面从flare.json中得到的数据

                                          top: '1%',       //距离上
                                          left: '7%',      //左
                                          bottom: '1%',    //下
                                          right: '20%',    //右的距离

                                          symbolSize: 20,   //标记的大小，就是那个小圆圈，默认7

                                          label: {         //每个节点所对应的标签的样式
                                              normal: {
                                                  position: 'left',       //标签的位置
                                                  verticalAlign: 'middle',//文字垂直对齐方式，默认自动。可选：top，middle，bottom
                                                  align: 'right',         //文字水平对齐方式，默认自动。可选：top，center，bottom
                                                  fontSize: 9             //标签文字大小
                                              }
                                          },

                                          leaves: {    //叶子节点的特殊配置，如上面的树图示例中，叶子节点和非叶子节点的标签位置不同
                                              label: {
                                                  normal: {
                                                      position: 'right',
                                                      verticalAlign: 'middle',
                                                      align: 'left'
                                                  }
                                              }
                                          },

                                          expandAndCollapse: true,    //子树折叠和展开的交互，默认打开
                                          animationDuration: 550,     //初始动画的时长，支持回调函数,默认1000
                                          animationDurationUpdate: 750//数据更新动画的时长，默认300
                                      }
                                  ]
                              });
        }
    });

}



