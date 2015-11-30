<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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

		<video src="C:\Users\gpaskov\uploads\test1.mp4" width="320"
			height="280" controls>
		<p>Your browser does not support the HTML5 video element.</p>
		</video>

		<table>
			<c:forEach items="${images}" var="item">
				<tr>
					<td><img height="100px" width="200px"
						src="<c:out value="${item}"/>" /></td>

				</tr>
			</c:forEach>
		</table>



		<br />
		<h2>${requestScope.message}</h2>
	</center>
</body>
</html>