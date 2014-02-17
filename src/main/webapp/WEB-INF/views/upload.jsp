<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<h1>Video uploader test</h1>
	<h2>Upload a video to server</h2>
	<input type="file" id="uploadBtn" value="Choose a Video"/>
	<hr />
	<h2>Upload a video to youtube</h2>
	<input type="button" id="uploadYTBtn" value="Upload to YouTube"/>
	
	
	<script type="text/javascript" src="//code.jquery.com/jquery-2.1.0.js"></script>
	<script type="text/javascript" src="//underscorejs.org/underscore.js"></script>
	
	<script type="text/javascript">
	
		$(document).ready(function(){
			$("#uploadYTBtn").on("click", function(evt) {
				$.ajax({
		            url: "./uploadStaticVideo",
		            type: 'GET'
		        }).success(function(receiveddata) {
		        	console.log("SUCCESS. " + receiveddata);
		        }).fail(function(err){
		        	console.log("FAIL. " + err);
		        });
			});
			
			$("#uploadBtn").on("change", function(evt) {
		        var data = new FormData();
		        var files = 0;
		        _.each($(evt.currentTarget.files), function(file) {
		            data.append("file", file);
		            files++;
		        });
		        $.ajax({
		            url: "./uploadVideo",
		            data: data,
		            cache: false,
		            contentType: false, //"multipart/form-data",
		            processData: false,
		            type: 'POST'
		        }).success(function(receiveddata) {
		        	console.log("SUCCESS. " + receiveddata);
		        }).fail(function(err){
		        	console.log("FAIL. " + err);
		        });
			});
		});
	        
	
	</script>
</body>
</html>