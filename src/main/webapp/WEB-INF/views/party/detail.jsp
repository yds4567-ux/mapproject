<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>모임 상세</title>
<style>
* {
	box-sizing: border-box;
}

.container {
	max-width: 1090px;
	margin: 0 auto;
	padding: 20px;
}

.party-image {
	height: 350px;
	background-color: #eee;
	border-radius: 10px;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #777;
}

.party-summary {
	position: relative;
	margin: -40px 30px 30px;
	padding: 25px;
	background-color: white;
	border-radius: 10px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
	display: flow-root;
}

.party-summary h1 {
	margin: 0 0 20px;
	font-size: 28px;
}

.party-summary p {
	margin: 0px;
	padding: 10px;
	width: 50%;
	float: left;
	font-size: 15px;
	color: #555;
}

.party-intro {
	padding: 25px 30px;
	border-bottom: 1px solid #ddd;
}

.party-intro h2 {
	margin: 0 0 15px;
	font-size: 22px;
}

.party-intro p {
	margin: 0;
	line-height: 1.7;
	white-space: pre-line;
}

.party-members {
	padding: 25px 30px;
	border-bottom: 1px solid #ddd;
}

.party-memvers h2 {
	margin: 0 0 15px;
	font-size: 22px;
}

.member {
	display: inline-block;
	padding: 10px 15px;
	margin: 0 8px 8px 0;
	background-color: #f2f2f2;
	border-radius: 20px;
	font-size: 14px;
}

.party-conditions {
	padding: 25px 30px;
}

.party-conditions h2 {
	margin: 0 0 15px;
	font-size: 22px;
}

.party-conditions p {
	margin: 0 0 10px;
	font-size: 15px;
	color: #555;
}

.party-buttons {
	text-align: center;
	margin: 30px 0;
}

#manage-btn {
	width: 170px;
	height: 45px;
	padding: 12px 20px;
	margin: 0 20px;
	background-color: black;
	color: white;
	border: 1px solid black;
	border-radius: 5px;
	font-size: 15px;
	cursor: pointer;
	vertical-align: middle;
}

#apply-btn {
	width: 170px;
	height: 45px;
	padding: 12px 20px;
	margin: 0 20px;
	background-color: black;
	color: white;
	border: 1px solid black;
	border-radius: 5px;
	font-size: 15px;
	cursor: pointer;
	vertical-align: middle;
}

#list-btn {
	width: 170px;
	height: 45px;
	padding: 12px 20px;
	background-color: white;
	color: black;
	border: 1px solid #ccc;
	border-radius: 5px;
	font-size: 15px;
	cursor: pointer;
	vertical-align: middle;
}
</style>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<div class="container">
		<div class="party-image">모임 이미지</div>

		<section class="party-summary">
			<h1>${party.title}</h1>
			<p>
				모임 날짜:
				<fmt:formatDate value="${party.meetDate}"
					pattern="yyyy.MM.dd(E) HH:mm" />
			</p>
			<p>모임장: ${hostName}</p>
			<p>모임 장소: ${party.storeName}</p>
			<p>위치: ${party.address}</p>
			<p>참여 방식: ${party.joinType == 'FCFS' ? '선착순' : '승인제'}</p>
			<p>참여 인원: ${memberCount}명 / ${party.maxPeople}명</p>
		</section>

		<section class="party-intro">
			<h2>모임 소개</h2>
			<p>${party.contents}</p>
		</section>

		<section class="party-members">
			<h2>참여 확정 멤버</h2>
			<C:forEach var="memberNames" items="${memberNames}">
				<span class="member">${memberNames}</span>
			</C:forEach>

			<C:if test="${empty memberNames}">
				<p>아직 참여 확정 멤버가 없습니다.</p>
			</C:if>
		</section>

		<section class="party-conditions">
			<h2>참여 조건</h2>

			<C:if test="${not empty party.genderRule}">
				<p>참여 성별: ${party.genderRule == 'male' ? '남자만' :
          party.genderRule == 'female' ? '여자만' : '제한 없음'}
				</p>
			</C:if>
			<C:if test="${party.minAge != null}">
				<p>최소 나이: ${party.minAge}세</p>
			</C:if>
			<C:if test="${party.maxAge != null}">
				<p>최대 나이: ${party.maxAge}세</p>
			</C:if>
			<C:if test="${not empty party.question}">
				<p>참여 신청 질문: ${party.question}</p>
			</C:if>
		</section>
		<div class="party-buttons">
			<C:if
				test="${not empty sessionScope.loginId and sessionScope.loginId == party.hostId}">
				<button type="button" id="manage-btn"
					onclick="location.href='/party/applications?partyId=${party.partyId}'">신청관리</button>
			</C:if>

			<C:if test="${sessionScope.loginId != party.hostId}">
				<button type="button" id="apply-btn"
					onclick="location.href='/party/apply?partyId=${party.partyId}'">
					신청하기</button>
			</C:if>
			<button type="button" id="list-btn"
				onclick="location.href='/party/list'">목록으로 돌아가기</button>
		</div>
	</div>
</body>
</html>