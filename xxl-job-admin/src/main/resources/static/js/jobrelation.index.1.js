
     
//    var list = document.getElementById("selectList");


//    $('#newRelationBtn').on('click', function(){
//        var relationTree = document.createTreeWalker("relationTree");
//        // var lastSelect = list.lastElementChild;
//        // var newSelect = lastSelect.cloneNode(true);
//        list.appendChild(relationTree);
//    });

//    $('#truncateSelectBtn').on('click', function(){
//        var selectArray = list.getElementsByTagName('select');
//        if(selectArray.length > 1){
//            list.removeChild(selectArray[selectArray.length - 1]);
//        }
//    });

//    $('#saveRelationBtn').on('click', function(){
//        var selectArray = list.getElementsByTagName('select');
//        var map = new Map();
//        for(var num =0; num < selectArray.length - 1; num++){
//            var parentIndex = selectArray[num].selectedIndex;
//            var parentJobId = selectArray[num].options[parentIndex].value;
//
//            var childIndex = selectArray[num + 1].selectedIndex;
//            var childJobId = selectArray[num + 1].options[childIndex].value;
//            map[parentJobId] = childJobId;
//        }
//        $.post(base_url + "/jobrelation/update", map, function(data, status) {
//            if (data.code == "200") {
//                layer.open({
//                    title: I18n.system_tips ,
//                    btn: [ I18n.system_ok ],
//                    content: I18n.system_update_suc ,
//                    icon: '1',
//                    end: function(layero, index){
//                        //window.location.reload();
//                        window.location.reload();
//                    }
//                });
//            } else {
//                layer.open({
//                    title: I18n.system_tips ,
//                    btn: [ I18n.system_ok ],
//                    content: (data.msg || I18n.system_update_fail ),
//                    icon: '2'
//                });
//            }
//        });
//
//    });

    $('#currentRelationSearchBtn').on('click', function(){
        var relationList = document.getElementById("jobList");
        var lastSelect = relationList.lastElementChild;
        var optionIndex = lastSelect.selectedIndex;
        var jobId = lastSelect.options[optionIndex].value;
        relationTable(jobId);
    });


function relationTable(jobId){
    var url = base_url + "/jobrelation/findAllRelationById";
    var param = {"jobInfoId": jobId};
//    $('#relationTree').jstree(true).refresh();
    $('#relationTree').jstree({
        "checkbox" : {
            "keep_selected_style" : false
        },
        "core" : {
          "animation" : 0,
          "check_callback" : true,
          "themes" : { "stripes" : true },
          'data' : {
            'url' : url,
            'data' : param
          }
        },
       "success" : function (resultJob) {//后台返回的参数，由于后台返回的参数jstree解析不了，

        var rootNode={"data" :{ "title": job.jobDesc},
                        "attr" : { "id" : job.id},
                        "state" : open,
                        "children" : job.childList
        };
            
           resolveJobRelation(resultJob, rootNode);
           
           return rootNode;
       },
        "types" : {
          "#" : {
            "max_children" : 5,
            "max_depth" : 10,
            "valid_children" : ["root"]
          },
          "root" : {
            "icon" : "/static/3.3.12/assets/images/tree_icon.png",
            "valid_children" : ["default"]
          },
          "default" : {
            "valid_children" : ["default","file"]
          },
          "file" : {
            "icon" : "glyphicon glyphicon-file",
            "valid_children" : []
          }
        },
        "plugins" : [
          "contextmenu", "dnd", "search",
          "state", "types", "wholerow"
        ]
      });
}

function resolveJobRelation(job, rootNode){
    
    if(job.childList == null){//student标识是叶子节点
        rootNode.state="closed";//此值是标识此节点是否有子节点的
    }else {
        var childArray = new Array();
        for (let index = 0; index < job.childList.length; index++) {
            const child = job.childList[index];
            var childNode={"data" :{ "title": child.jobDesc},
                          "attr" : { "id" : child.id},
                          "state" : open,
                          "children" : child.childList
            };
            resolveJobRelation(child, childNode);
            childArray.push(childNode);
            
        }
        rootNode.children = childArray;
    }
}