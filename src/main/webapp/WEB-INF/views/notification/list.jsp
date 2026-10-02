<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<title>알림</title>
<style>
* {
	box-sizing: border-box;
}

.container {
	max-width: 800px;
	margin: 40px auto;
	padding: 30px;
}

.container h1 {
	margin: 0 0 30px;
	text-align: center;
	font-size: 26px;
}

.notification-card {
	display: flow-root;
	max-width: 400px;
	margin: 0 auto 15px;
	padding: 20px;
	border: 1px solid #ddd;
	border-radius: 8px;
}

.notification-card.unread {
	background-color: #f2f6ff;
	border-color: #c8d8f0;
}

.notification-card p {
	font-size: 14px;
	color: #666;
	margin: 0 0 12px;
	line-height: 1.6;
	overflow-wrap: anywhere;
}

.notification-message {
    font-size: 16px;
    font-weight: bold;
    color: black;
    margin-bottom: 12px;
    line-height: 1.6;
    overflow-wrap: anywhere;
}

.notification-card button {
    padding: 10px 16px;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
}

.notification-btns {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    margin-top: 20px;
}

.notification-btns form {
	margin: 0;
}

.read-btn {
	background-color: black;
	color: white;
	border: 1px solid black;
}

.view-btn {
	background-color: white;
	color: black;
	border: 1px solid #c8d8f0;
}
</style>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />

	<div class="container">
		<h1>알림</h1>

		<c:forEach var="notification" items="${notifications}">
			<div
				class="notification-card ${notification.readYn == 'N' ? 'unread' : ''}">
				<div class="notification-message">${notification.message}</div>
				<p>
					알림 시간 :
					<fmt:formatDate value="${notification.regdate}"
						pattern="yyyy.MM.dd HH:mm" />
				</p>
				<p>읽음 상태 : ${notification.readYn == 'N' ? '안 읽음' : '읽음'}</p>

				<div class="notification-btns">
					<c:if test="${notification.readYn == 'N'}">
						<form action="/notification/read" method="post">
							<input type="hidden" name="notificationId"
								value="${notification.notificationId}">
							<button type="submit" class="read-btn">읽음 처리</button>
						</form>
					</c:if>
					<c:if
						test="${notification.targetType == 'PARTY' and notification.targetId != null}">
						<button type="button" class="view-btn"
							onclick="location.href='/party/detail?partyId=${notification.targetId}'">
							모임 보기</button>
					</c:if>
				</div>
			</div>
		</c:forEach>

		<c:if test="${empty notifications}">
			<p>받은 알림이 없습니다.</p>
		</c:if>

	</div>
</body>
</html>