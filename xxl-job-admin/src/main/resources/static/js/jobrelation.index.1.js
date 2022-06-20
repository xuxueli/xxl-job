

var tree;

function relationTree(jobId){
    var url = base_url + "/jobrelation/findAllRelationById";
    var param = {"jobId": jobId};
    $.ajax({
        url: url,
        data: param,
        type: 'post',
        async: false,
        dataType: 'json',
        success: function(result){
        if(tree != null && tree != "" && tree != undefined){
            tree.dispose();
        }

        tree = echarts.init(document.getElementById("relationTree"));
        var option = option = {
                       tooltip: {
                         trigger: 'item',
                         triggerOn: 'mousemove'
                       },
                       series: [
                         {
                           type: 'tree',
                           id: 0,
                           name: 'relation',
                           data: [result.data],
                           top: '10%',
                           left: '8%',
                           bottom: '22%',
                           right: '20%',
                           symbolSize: 7,
                           edgeShape: 'polyline',
                           edgeForkPosition: '63%',
                           initialTreeDepth: 3,
                           lineStyle: {
                             width: 2
                           },
                           label: {
                             backgroundColor: '#91c7ae',
                             position: 'left',
                             verticalAlign: 'middle',
                             align: 'right'
                           },
                           leaves: {
                             label: {
                               position: 'right',
                               verticalAlign: 'middle',
                               align: 'left'
                             }
                           },
                           emphasis: {
                             focus: 'descendant'
                           },
                           expandAndCollapse: true,
                           animationDuration: 550,
                           animationDurationUpdate: 750
                         }
                       ]
                     };
            option && tree.setOption(option);
            tree.on('click', function(params) {
              if (params.componentType === 'markPoint') {
                // 点击到了 markPoint 上
                if (params.seriesIndex === 5) {
                  // 点击到了 index 为 5 的 series 的 markPoint 上。
                }
              } else if (params.componentType === 'series') {
                if (params.seriesType === 'graph') {
                  if (params.dataType === 'edge') {
                    // 点击到了 graph 的 edge（边）上。
                  } else {
                    // 点击到了 graph 的 node（节点）上。
                  }
                }
              }
            });
        }
    });

}



