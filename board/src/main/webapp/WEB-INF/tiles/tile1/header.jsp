<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.net.InetAddress" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> <!-- Spring security taglib을 사용 --> 

<%-- ===== #27. tile1 중 header 페이지 만들기 (#26. 은 실수로 기입하지 않아서 없음) ===== --%> 
<%
	String ctxPath = request.getContextPath();

    // === #221. (웹채팅관련3) === 
    // === 서버 IP 주소 알아오기(사용중인 IP주소가 유동IP 이라면 IP주소를 알아와야 한다.) === 
    
//    InetAddress inet = InetAddress.getLocalHost();
// String serverIP = inet.getHostAddress();
     
 // System.out.println("serverIP : " + serverIP);
 // serverIP : 192.168.0.202

 // String serverIP = "192.168.10.101";
 	String serverIP = "13.124.125.115";
 	// 자신의 EC2 퍼블릭 IPv4 주소임
 	
 	
 // String serverIP = "211.238.142.72"; 만약에 사용중인 IP주소가 고정IP 이라면 IP주소를 직접입력해주면 된다. 
 
    // === 서버 포트번호 알아오기 === //
    int portnumber = request.getServerPort();
 // System.out.println("portnumber : " + portnumber);
 // portnumber : 9099
 
    String serverName = "http://"+serverIP+":"+portnumber;
 // System.out.println("serverName : " + serverName);
 // serverName : http://192.168.0.202:9099
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
		        
		           <a class="dropdown-item" href="<%= serverName%><%=ctxPath%>/chatting/multichat.action">웹채팅</a>   
		        
		        </div>
		     </li>
		     
		     <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">게시판</a>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <a class="dropdown-item" href="<%=ctxPath%>/list.action">목록보기</a>
		    <%-- <c:if test="${not empty sessionScope.loginuser}"> --%>
		           <a class="dropdown-item" href="<%=ctxPath%>/add.action">글쓰기</a>
		    <%-- </c:if>  --%>
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
		     
  <%-- <c:if test="${sessionScope.loginuser.gradelevel == 10 }">  --%> 
		     <li class="nav-item dropdown">
		        <a class="nav-link dropdown-toggle text-info" href="#" id="navbarDropdown" data-toggle="dropdown">메일보내기(다중파일첨부)</a>  
		        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
		           <a class="dropdown-item" href="<%=ctxPath%>/emailWrite.action">email작성</a>
		        </div>
		     </li>
  <%-- </c:if>  --%> 
	 	    
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
		
	   <%-- === #49. 로그인이 성공되면 로그인한 사용자의 이메일 주소를 출력하기 === --%>
	   <c:if test="${not empty sessionScope.loginuser}">
		  <div style="float: right; font-size: 9pt;">
			 <span style="color: navy; font-weight: bold;">${sessionScope.loginuser.email}</span> 님<br>로그인중.. 
		  </div>
	   </c:if>
	   
	  <%-- ==== (#스프링보안14) Spring Security(스프링 보안) 알아보기 ==== --%>
	  <%--
	   <c:if test="${not empty sessionScope.sesMemberinfo}">
		  <div style="float: right; font-size: 9pt;">
			 <span style="color: navy; font-weight: bold;">${sessionScope.sesMemberinfo.member_name}</span> 님<br>로그인중.. 
		  </div>
	   </c:if> 
	  --%>
	</nav>
	<%-- 상단 네비게이션 끝 --%>
	

	<p class="h5" style="margin: auto">
		<marquee> [공지사항] 쌍용강북교육센터 G클래스 수강생 여러분들의 수료 후 프로그래머 입사를 진심으로 기원합니다.</marquee>
	</p>			       
    