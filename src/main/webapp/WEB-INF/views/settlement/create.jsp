<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<title>모임비 정산</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />

	<form action="/settlement/createSubmit" method="post">
		<h1>모임비 정산</h1>
		<p>${party.title}</p>

		<input type="hidden" name="partyId" value="${party.partyId}">
	</form>
</body>
</html>