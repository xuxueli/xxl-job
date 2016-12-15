<body style="color:white;background-color:black;" >
<pre id='log'>
<br>
<#if result.code == 200>${result.content}
<#else>${result.msg}</#if>
</pre>
<script src="${request.contextPath}/static/adminlte/plugins/jQuery/jQuery-2.1.4.min.js"></script>

<script>	
     //var scroller = new AutoScroller(document.body);
	 var start = ${result.size}+1;
	 fetchNext($("#log"),"");
	 function fetchNext(e,url){
	 	$.post("${request.contextPath}/joblog/logDetail?id=${id}&start="+start, function(data, status) {
				if (data.code == "200") {
					 $("#log").append(data.content)
					 if(data.msg=="MORE"){
					 		start=data.size+1;
					 		setTimeout(function(){fetchNext(e,url);},1000);

					 }else{
					 		start=data.size;
					 }
				} else {
					$("#log").append(data.msg)
				}
		});
	 }
</script>

</body>
