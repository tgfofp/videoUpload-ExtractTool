<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload</title>
</head>
<body style="background-color: lightgreen">
	<br />
	<center>

		<%
			String filePath = request.getParameter("filePath");
			System.out.println(filePath);
		%>

		<%=(String) request.getAttribute("filePath")%>

		<video src=${requestScope.filePath} width="320" height="280" controls>
		<p>
			Your browser does not support the HTML5
			<code>video</code>
			element.
		</p>
		</video>

		<br />
		<h2>${requestScope.message}</h2>
	</center>
</body>
</html>