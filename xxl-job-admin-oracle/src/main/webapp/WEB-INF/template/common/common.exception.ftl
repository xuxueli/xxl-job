<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>应用程序异常 (error)</title> 
    <style type="text/css"> 
        body { background-color: #fff; color: #666; text-align: center; font-family: arial, sans-serif; }
        div.dialog {
            width: 80%;
            padding: 1em 4em;
            margin: 4em auto 0 auto;
            border: 1px solid #ccc;
            border-right-color: #999;
            border-bottom-color: #999;
        }
        h1 { font-size: 100%; color: #f00; line-height: 1.5em; }
    </style>
    
</head> 
</head>
<body> 

	<div class="dialog"> 
	    <h1>应用程序异常</h1> 
	    <p>${exceptionMsg}</p>
		<a href="javascript:window.location.href='${request.contextPath}/'">返 回</a>
	    </p> 
	</div>

</body>
</html>