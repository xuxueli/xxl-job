//////////////////////////////////
//初始化选择树形结构
//
function initChoosableTree(treeId, dataUrl, options, callback) {
    var glyph_opts = {
        preset: "bootstrap3",
        map: {}
    };
    var default_opts = {
        selectMode: 1
    };

    var curSelectMode = options.selectMode ? options.selectMode : default_opts.selectMode;
    // Initialize Fancytree
    $("#" + treeId).fancytree({
        extensions: ["dnd5", "edit", "glyph", "wide"],
        activeVisible: false,
        checkbox: true,
        selectMode: curSelectMode,  //// 1:single, 2:multi, 3:multi-hier
        dnd5: {
            dragStart: function (node, data) {
                return true;
            },
            dragEnter: function (node, data) {
                return true;
            },
            dragDrop: function (node, data) {
                data.otherNode.copyTo(node, data.hitMode);
            }
        },
        glyph: glyph_opts,
        //source: treeData,
        source: {url: dataUrl},
        postProcess: function (event, data) {
            //请求返回结果处理
            //var orgResponse = data.response;
            var response = data.response;
            if (response.code === ResultCode_OK) {
                console.log(response.data)
                data.result = response.data; //fancytree 返回的是JSON对象，不需解析

                if(callback != null){
                    callback(treeId);
                }

            } else {
                console.error(response.msg);
                data.result = {
                    error: "ERROR #" + response.code + ": " + response.msg
                }
            }
        },

        // toggleEffect: { effect: "drop", options: {direction: "left"}, duration: 400 },
        wide: {
            iconWidth: "1em",       // Adjust this if @fancy-icon-width != "16px"
            iconSpacing: "0.5em",   // Adjust this if @fancy-icon-spacing != "3px"
            labelSpacing: "0.1em",  // Adjust this if padding between icon and label != "3px"
            levelOfs: "1.5em"       // Adjust this if ul padding != "16px"
        },

        icon: function (event, data) {
            // if( data.node.isFolder() ) {
            //    return "glyphicon glyphicon-book";
            // }
        },
        /*lazyLoad: function(event, data) {
           var curNode = data.node;
           data.result = {
             url: "/getSubData",
             data: {key: curNode.key}
           }
        },*/
        tooltip: function (event, data) {
            // Create dynamic tooltips
            return data.node.title;
        },
        click: function (event, data) {
            //logEvent(event, data, ", targetType=" + data.targetType);
            // return false to prevent default behavior (i.e. activation, ...)
            //return false;
        },
        collapse: function (event, data) {
            //logEvent(event, data);
        },
        createNode: function (event, data) {
            // Optionally tweak data.node.span or bind handlers here
            //logEvent(event, data);
        },
        dblclick: function (event, data) {
            //logEvent(event, data);
            //data.node.toggleSelect();
        },

    });

}

///////////////////////////////////////////////
// 初始化导航树
// treeId ： 树ID
// dataUrl ： 获取树形数据的网址
// callbackFunc : 标题点击后触发的回调函数
//
function initNaviTree(treeId, dataUrl, callbackFunc) {
    var glyph_opts = {
        preset: "bootstrap3",
        map: {}
    };
    // Initialize Fancytree
    $("#" + treeId).fancytree({
        extensions: ["dnd5", "edit", "glyph", "wide"],
        checkbox: false, //false，不显示选择框
        selectMode: 3,  //多选
        dnd5: {
            dragStart: function (node, data) {
                return true;
            },
            dragEnter: function (node, data) {
                return true;
            },
            dragDrop: function (node, data) {
                data.otherNode.copyTo(node, data.hitMode);
            }
        },
        glyph: glyph_opts,
        //source: treeData,
        source: {url: dataUrl},
        postProcess: function (event, data) {
            //请求返回结果处理
            //var orgResponse = data.response;
            var response = data.response;
            if (response.code === "success") {
                data.result = response.data; //fancytree 返回的是JSON对象，不需解析
            } else {
                console.error(response.msg);
                data.result = {
                    error: "ERROR #" + response.code + ": " + response.msg
                }
            }
        },

        // toggleEffect: { effect: "drop", options: {direction: "left"}, duration: 400 },
        wide: {
            iconWidth: "1em",       // Adjust this if @fancy-icon-width != "16px"
            iconSpacing: "0.5em",   // Adjust this if @fancy-icon-spacing != "3px"
            labelSpacing: "0.1em",  // Adjust this if padding between icon and label != "3px"
            levelOfs: "1.5em"       // Adjust this if ul padding != "16px"
        },

        icon: true,
        /*lazyLoad: function(event, data) {
           var curNode = data.node;
           data.result = {
             url: "/getSubData",
             data: {key: curNode.key}
           }
        },*/
        tooltip: function (event, data) {
            // Create dynamic tooltips
            return data.node.title;
        },
        click: function (event, data) {
            //logEvent(event, data, ", targetType=" + data.targetType);
            // return false to prevent default behavior (i.e. activation, ...)
            //return false;
            var node = data.node;
            //alert("nodeObj = "  + node +  "      nodeKey = " + node.key  + ", targetType=" + data.targetType);
            var curNode = data.node;
            //orgEvent = data.originalEvent;

            //alert(data.targetType);

            if (data.targetType === "title") {
                //标题点击事件
                if (curNode != null && callbackFunc != null) {
                    var dataObj = {id: curNode.key, name: curNode.title};
                    //window[callbackFunc](dataObj);  //使用回调函数处理点击事件
                    callbackFunc(dataObj);  //使用回调函数处理点击事件

                }

            }

        },
        collapse: function (event, data) {
            //logEvent(event, data);
        },
        createNode: function (event, data) {
            // Optionally tweak data.node.span or bind handlers here
            //logEvent(event, data);
        },
        dblclick: function (event, data) {
            //logEvent(event, data);
            //data.node.toggleSelect();
        },

    });

}


////////////////////////////////////
// 获取FancyTree所有选中的节点信息。
// 返回信息为JSON数组， 基本属性为 ID 和 Name
function getChoosedNodeList(treeId) {
    if (treeId == null || treeId == "") {
        return null;
    }

    var choosedNodes = $("#" + treeId).fancytree("getTree").getSelectedNodes();

    var resultData = new Array();
    for (var i = 0; i < choosedNodes.length; i++) {
        resultData.push({sid: choosedNodes[i].key, name: choosedNodes[i].title});
    }

    return resultData;

}

////////////////////////////////////
//获取FancyTree所有选中的节点ID，
//各节点ID之间用","分隔。
function getChoosedNodeIdList(treeId) {
    if (treeId == null || treeId == "") {
        return null;
    }

    var nodes = $("#" + treeId).fancytree("getTree").getSelectedNodes();

    var allKeys = $.map(nodes, function (node) {
        //return "[" + node.key + "]: '" + node.title + "'";
        return node.key;
    });

    var choosedIds = allKeys.join(", ");
    //alert(choosedIds);

    return choosedIds;
}

/////////////////////////////////////////
// 展开所有的树节点
// treeId ：树的ID;
function expandAllByTreeId(treeId) {
    var treeObj = $("#" + treeId).fancytree("getTree");

    // 展开所有的树节点
    treeObj.visit(function (node) {
        node.setExpanded(true);
    });
}


///////////////////////////////////////////////////////
//展开指定ID的树节点
// treeObj ： 树对象；
// nodeId ：树节点ID;
function expandNodesById(treeObj, nodeId) {
    if (treeObj == null || nodeId == null) {
        return;
    }

    var curNode = treeObj.getNodeByKey(nodeId); //根据ID获取当前节点
    if (curNode != null) {
        curNode.setExpanded(true);
    }
}

////////////////////////////////////////////
//内部方法，展开指定节点的所有上级节点
function _expandAllParentNodesByObj(treeObj, curNode) {
    if (treeObj == null || curNode == null) {
        return;
    }

    var parentNodeList = curNode.getParentList(true, true);
    if (parentNodeList != null) {
        for (var i = 0; i < parentNodeList.length; i++) {
            parentNodeList[i].setExpanded(true);
        }

    }

}


////////////////////////////////////////////////////////
//展开指定ID树上指定节点的所有上级节点
// treeId : 树ID；
// nodeId : 节点ID；
// isExpandSelf : 是否展开自己。
function expandAllParentNodesById(treeId, nodeId, isExpandSelf) {

    var treeObj = $("#" + treeId).fancytree("getTree");

    if (nodeId != null && nodeId != "") {
        var curNode = treeObj.getNodeByKey(nodeId); //根据ID获取当前节点

        if (curNode != null) {
            _expandAllParentNodesByObj(treeObj, curNode);

            curNode.setActive(true);
            curNode.setFocus(true);
            curNode.setSelected(true);

            curNode.setExpanded(isExpandSelf); //展开或者关闭当前节点
        }
    }

}


/**
 *

 activeVisible: true, // Make sure, active nodes are visible (expanded)
 aria: true, // Enable WAI-ARIA support
 autoActivate: true, // Automatically activate a node when it is focused using keyboard
 autoCollapse: false, // Automatically collapse all siblings, when a node is expanded
 autoScroll: false, // Automatically scroll nodes into visible area
 clickFolderMode: 4, // 1:activate, 2:expand, 3:activate and expand, 4:activate (dblclick expands)
 checkbox: false, // Show checkboxes
 debugLevel: 2, // 0:quiet, 1:normal, 2:debug
 disabled: false, // Disable control
 focusOnSelect: false, // Set focus when node is checked by a mouse click
 escapeTitles: false, // Escape `node.title` content for display
 generateIds: false, // Generate id attributes like <span id='fancytree-id-KEY'>
 idPrefix: "ft_", // Used to generate node id´s like <span id='fancytree-id-<key>'>
 icon: true, // Display node icons
 keyboard: true, // Support keyboard navigation
 keyPathSeparator: "/", // Used by node.getKeyPath() and tree.loadKeyPath()
 minExpandLevel: 1, // 1: root node is not collapsible
 quicksearch: false, // Navigate to next node by typing the first letters
 rtl: false, // Enable RTL (right-to-left) mode
 selectMode: 2, // 1:single, 2:multi, 3:multi-hier
 tabindex: "0", // Whole tree behaves as one single control
 titlesTabbable: false, // Node titles can receive keyboard focus
 tooltip: false // Use title as tooltip (also a callback could be specified)

 */
