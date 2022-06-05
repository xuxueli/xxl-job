
     
    var list = document.getElementById("selectList");


    $('#newRelationBtn').on('click', function(){
        var relationTree = document.createTreeWalker("relationTree");
        // var lastSelect = list.lastElementChild;
        // var newSelect = lastSelect.cloneNode(true);
        list.appendChild(relationTree);
    });

    $('#truncateSelectBtn').on('click', function(){
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

    $('#currentRelationSearchBtn').on('click', function(){
        var relationList = document.getElementById("jobList");
        var lastSelect = relationList.lastElementChild;
        var optionIndex = lastSelect.selectedIndex;
        var jobId = lastSelect.options[optionIndex].value;
        relationTable(jobId);
    });


function relationTable(jobId){
    $("#relation_list").dataTable({
		"deferRender": true,
		"processing" : true,
	    "serverSide": true,
		"ajax": {
			url: base_url + "/jobrelation/findAllRelation",
			type:"post",
	        data : jobId
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,	// scroll x，close self-adaption
	    "columns": [
	                {
	                	"data": 'jobDesc',
						"visible" : true,
						"width":'60%'
					}
	            ]
	});
}