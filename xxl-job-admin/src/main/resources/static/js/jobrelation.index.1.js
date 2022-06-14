
     


    $('#currentRelationSearchBtn').on('click', function(){
        var jobDescEl = document.getElementById("jobDesc");
        var jobDesc = jobDescEl.value;
        relationTree(jobDesc);
    });


function relationTree(jobDesc){
    var url = base_url + "/jobrelation/findAllRelationByDesc";
    var param = {"jobDesc": jobDesc};
    var chart = echarts.init(document.getElementById("relationTree"));

}



