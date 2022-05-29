
     
    var list = document.getElementById("selectList");


    $('#newRelationBtn').on('click', function(){
        var lastSelect = list.lastElementChild;
        var newSelect = lastSelect.cloneNode(true);
        list.appendChild(newSelect);
    });

    $('#truncateRelationBtn').on('click', function(){
        var selectArray = list.getElementsByTagName('select');
        if(selectArray.length > 1){
            list.removeChild(selectArray[selectArray.length - 1]);
        }
        
    });

    $('#saveRelationBtn').on('click', function(){
        var selectArray = list.getElementsByTagName('select');
        var map = new Map();
        for(var num =0; num < selectArray.length - 1; num++){
            var parentIndex = selectArray[num].selectedIndex;
            var parentJobId = selectArray[num].options[parentIndex].value;

            var childIndex = selectArray[num + 1].selectedIndex;
            var childJobId = selectArray[num + 1].options[childIndex].value;
            map[parentJobId] = childJobId;
        }
        alert(JSON.stringify(map));
        $.post(base_url + "/jobrelation/update", map, function(data, status) {
            if (data.code == "200") {
                layer.open({
                    title: I18n.system_tips ,
                    btn: [ I18n.system_ok ],
                    content: I18n.system_update_suc ,
                    icon: '1',
                    end: function(layero, index){
                        //window.location.reload();
                        window.location.reload();
                    }
                });
            } else {
                layer.open({
                    title: I18n.system_tips ,
                    btn: [ I18n.system_ok ],
                    content: (data.msg || I18n.system_update_fail ),
                    icon: '2'
                });
            }
        });
        
    });
