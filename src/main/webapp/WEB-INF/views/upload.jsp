<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<h1>Video uploader test</h1>
	<h2>Login to youtube</h2>
	<!--
	<a href="https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/youtube.upload&state=any&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fyoutubeupldr%2Foauth2callback&response_type=code&client_id=706712866536-g08ec7qv779l8bivjh3kdkgjee2rduuq.apps.googleusercontent.com&approval_prompt=force&access_type=offline">
	  -->
	<a href="https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/youtube.upload&state=any&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fyoutubeupldr%2Foauth2callback&response_type=code&client_id=796519910995-715sqhgqf8csgfngnj98ka5g9o9356fc.apps.googleusercontent.com&approval_prompt=force&access_type=offline" >
		Login using google
	</a>
	<br />
	<c:if test="${not empty authToken}">
		<label>AuthToken: </label><b>${authToken}</b>
	</c:if>
	<hr />
	<h2>Obtain Refresh Token</h2>
		<form action="https://accounts.google.com/o/oauth2/token" method="post">
			<input type="text" id="code" name="code" value="" >
<!-- 			<input type="text" id="client_id" name="client_id" value="706712866536-g08ec7qv779l8bivjh3kdkgjee2rduuq.apps.googleusercontent.com"> -->
<!-- 			<input type="text" id="client_secret" name="client_secret" value="VfbWem8QwSpk-4tTClwvqgI3"> -->
			<input type="text" id="client_id" name="client_id" value="796519910995-715sqhgqf8csgfngnj98ka5g9o9356fc.apps.googleusercontent.com">
			<input type="text" id="client_secret" name="client_secret" value="AD7QUQrIdcJ5bcJs35fz-MY4">  
			<input type="text" id="redirect_uri" name="redirect_uri" value="http://localhost:8080/youtubeupldr/oauth2callback">
			<input type="text" id="grant_type" name="grant_type" value="authorization_code">
			<button type="submit">Get Refresh Token</button>
		</form>
	</a>
	<br />

	<hr />
	<h2>Upload a video to server</h2>
	<input type="text" id="accessToken" placeholder="accessToken" />
	<input type="text" id="refreshToken" placeholder="refreshToken" />
	<input type="file" id="uploadBtn" value="Choose a Video"/>
	<hr />
	<h2>Upload a video to youtube</h2>
	<input type="button" id="uploadYTBtn" value="Upload to YouTube"/>
	
	
	<script type="text/javascript" src="//code.jquery.com/jquery-2.1.0.js"></script>
	<script type="text/javascript" src="//underscorejs.org/underscore.js"></script>
	
	<script type="text/javascript">
	
		$(document).ready(function(){
			$("#uploadYTBtn").on("click", function(evt) {
				var accessTkn = $("#accessToken").val();
				var refreshTkn = $("#refreshToken").val();
		        if( accessTkn !== ""){
		        	data.append("accessToken", accessTkn);
		        }
		        if( refreshTkn !== ""){
		        	data.append("refreshToken", refreshTkn);
		        }
		        
				$.ajax({
		            url: "./uploadStaticVideo",
		            type: 'GET',
		            data : data
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
		        var accessTkn = $("#accessToken").val();
		        if( accessTkn !== ""){
		        	data.append("accessToken", accessTkn)
		        }
		        var refreshTkn = $("#refreshToken").val();
		        if( refreshTkn !== ""){
		        	data.append("refreshToken", refreshTkn);
		        }
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