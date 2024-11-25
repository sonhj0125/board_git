<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ===== #28. tile2 중 header 페이지 만들기 ===== --%> 
<%
	String ctxPath = request.getContextPath();
%>

    <%-- 상단 네비게이션 시작 --%>
	<nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top mx-4 py-3">
		<!-- Brand/logo --> 
		<a class="navbar-brand" href="<%= ctxPath %>/index.action" style="margin-right: 10%;"><img src="<%= ctxPath %>/resources/images/sist_logo.png" /></a>
		
		<!-- 아코디언 같은 Navigation Bar 만들기 -->
	    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#collapsibleNavbar">
	      <span class="navbar-toggler-icon"></span>
	    </button>
		
		<div class="collapse navbar-collapse" id="collapsibleNavbar">
		  <ul class="navbar-nav h6"> <%-- .h6 는 글자크기임 --%>  
		     <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">Home</a> 
		                                     <%-- .text-info 는 글자색으로 청록색임 --%>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <a class="dropdown-item" href="<%= ctxPath %>/index.action">Home</a>
		      <%-- <a class="dropdown-item" href="<%= serverName%><%=ctxPath%>/chatting/multichat.action">웹채팅</a>  --%>
		        </div>
		     </li>
		     
		     <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">로그인</a>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <c:if test="${empty sessionScope.loginuser}">
			           <a class="dropdown-item" href="#">회원가입</a>
			           <a class="dropdown-item" href="<%=ctxPath%>/login.action">로그인</a>
		           </c:if>
		           				
				   <c:if test="${not empty sessionScope.loginuser}">
					   <a class="dropdown-item" href="#">나의정보</a>
					   <a class="dropdown-item" href="<%=ctxPath%>/logout.action">로그아웃</a>
				   </c:if>
		        </div>
		     </li>
		     
		     <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">인사관리</a>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <a class="dropdown-item" href="<%=ctxPath%>/emp/employeeList.action">직원목록</a>
		           <a class="dropdown-item" href="<%=ctxPath%>/emp/chart.action">통계차트</a>
		        </div>
		     </li>
		     
  			 <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">제품정보</a>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <a class="dropdown-item" href="#">제품목록</a>
		        </div>
		     </li>
	 	    
		     <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">일정관리</a>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <a class="dropdown-item" href="<%=ctxPath%>/schedule/scheduleManagement.action">일정관리</a>
		        </div>
		     </li>
		     
   		     <li class="nav-item dropdown">
	             <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">공공데이터</a>  
	             <div class="dropdown-menu" aria-labelledby="navbarDropdown">
	                <a class="dropdown-item" href="<%=ctxPath%>/opendata/seoul_bicycle_rental.action">서울따릉이지도</a>
	                <a class="dropdown-item" href="<%=ctxPath%>/opendata/seoul_bicycle_rental_insert.action">오라클입력및조회</a>
	                <a class="dropdown-item" href="<%=ctxPath%>/opendata/korea_tour_api.action">한국관광공사사진</a>
	             </div>
          	 </li>
		     
		    <!-- ==== 인터셉터 알아보기 ====  -->
		    <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">인터셉터알아보기</a>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <a class="dropdown-item" href="<%=ctxPath%>/anyone/anyone_a.action">누구나접근_A</a>
		           <a class="dropdown-item" href="<%=ctxPath%>/anyone/anyone_b.action">누구나접근_B</a>
		           <a class="dropdown-item" href="<%=ctxPath%>/member_only/member_a.action">회원누구나접근_A</a>
		           <a class="dropdown-item" href="<%=ctxPath%>/member_only/member_b.action">회원누구나접근_B</a>
		           <a class="dropdown-item" href="<%=ctxPath%>/special_member/special_member_a.action">특정회원만접근_A</a>
		           <a class="dropdown-item" href="<%=ctxPath%>/special_member/special_member_b.action">특정회원만접근_B</a>
		        </div>
		    </li>
		    
	      	<!-- ==== #266. 스프링보안13 Spring Security(스프링 보안) 알아보기 ====  -->
          	<li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">스프링 보안</a>  
              <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                 
               <sec:authorize access="isAnonymous()">
                  <a class="dropdown-item" href="<%=ctxPath%>/security/member/memberRegister.action">회원가입</a>
                  <a class="dropdown-item" href="<%=ctxPath%>/security/member/login.action">로그인</a>
               </sec:authorize>
            
               <sec:authorize access="isAuthenticated()">
                  <a class="dropdown-item" href="<%=ctxPath%>/security/member/logout.action">로그아웃</a>
               </sec:authorize>
                 
                 <sec:authorize access="permitAll">   
                   <a class="dropdown-item" href="<%=ctxPath%>/security/everybody.action">누구나</a>
               </sec:authorize>
               
               <sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER_SPECIAL','ROLE_USER')"> <%-- 역할(권한)이 'ROLE_ADMIN','ROLE_USER_SPECIAL','ROLE_USER' 중에 하나라도 있는 회원은 아래의 메뉴가 보여지도록 한다. --%>
                   <a class="dropdown-item" href="<%=ctxPath%>/security/member/memberOnly.action">회원전용</a>
               </sec:authorize>
               
               <sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER_SPECIAL','ROLE_USER')"> <%-- 역할(권한)이 'ROLE_ADMIN','ROLE_USER_SPECIAL','ROLE_USER' 중에 하나라도 있는 회원은 아래의 메뉴가 보여지도록 한다. --%> 
                   <a class="dropdown-item" href="<%=ctxPath%>/security/member/special/special_memberOnly.action">특별회원전용</a> 
               </sec:authorize>
               
               <sec:authorize access="hasRole('ROLE_ADMIN')">  
                   <a class="dropdown-item" href="<%=ctxPath%>/security/admin/adminOnly.action">관리자전용</a>
               </sec:authorize>
              </div>
          	</li>
     	 </ul>
       </div>
		
	  <%-- === #49. 로그인이 성공되어지면 로그인되어진 사용자의 이메일 주소를 출력하기 === --%>
	   <c:if test="${not empty sessionScope.loginuser}">
		  <div style="float: right; font-size: 9pt;">
			 <span style="color: navy; font-weight: bold;">${sessionScope.loginuser.email}</span> 님<br>로그인중.. 
		  </div>
	   </c:if>
		
	</nav>
	<%-- 상단 네비게이션 끝 --%>
	

	<div style="margin: auto; padding: 5px 0 15px 0; width: 80%;">
		
		<%-- 광고 캐러젤 시작 --%>
		<div id="myCarousel" class="carousel slide" data-ride="carousel">
    	  <!-- Indicators -->
    	  <ol class="carousel-indicators">
		      <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
		      <li data-target="#myCarousel" data-slide-to="1"></li>
	      </ol>

          <!-- Wrapper for slides -->
          <div class="carousel-inner">
		      <div class="carousel-item active">
		         <img src="<%= ctxPath%>/resources/images/advertisement_01.png" alt="붐펫" class="d-block w-100">
		      </div>
		
		      <div class="carousel-item">
		         <img src="<%= ctxPath%>/resources/images/advertisement_02.png" alt="어도비" class="d-block w-100">
		      </div>
    	  </div>

	      <!-- Left and right controls -->
	      <a class="carousel-control-prev" href="#myCarousel" role="button" data-slide="prev">
	         <span class='carousel-control-prev-icon' aria-hidden='true'></span>
	         <span class="sr-only">Previous</span>
	      </a>
	      <a class="carousel-control-next" href="#myCarousel" role="button" data-slide="next">
	         <span class='carousel-control-next-icon' aria-hidden='true'></span>
	         <span class="sr-only">Next</span>
	      </a>
	   </div>
       <%-- 광고 캐러젤 끝 --%>
      
	</div>
	    