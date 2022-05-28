



    $('#newRelationBtn').on('click', function(){
        var list = document.getElementById("selectList");
        var lastSelect = list.lastElementChild;
        var newSelect = lastSelect.cloneNode(true);
        list.appendChild(newSelect);
    });

    $('saveRelationBtn').on('click', function(data){

    })
