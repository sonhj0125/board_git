<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    String ctxPath = request.getContextPath();
%>

<div class="container text-center">
		<div>${message}</div>
		
		<c:if test='${requestScope.success eq "1"}'>
			<div class="mt-3">    
			    <a href="<%= ctxPath%>/security/member/login.action" class="btn btn-secondary">"스프링보안 > 로그인" 으로 이동</a>          
			</div>  
        </c:if>
        
        <c:if test='${requestScope.success eq "0"}'>
			<div class="mt-3">    
			    <a href="<%= ctxPath%>/index.action" class="btn btn-secondary">메인페이지로 이동</a>          
			</div>  
        </c:if>
</div>
