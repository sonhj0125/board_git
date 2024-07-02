<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>   

<%
    String ctxPath = request.getContextPath();
%>

<style type="text/css">

   table#emptbl {
      width: 100%;
   }
   
   table#emptbl th, table#emptbl td {
      border: solid 1px gray;
      border-collapse: collapse;
   }
   
   table#emptbl th {
      text-align: center;
      background-color: #ccc;
   }

</style>

<script type="text/javascript">

	$(document).ready(function(){
		
		
		
		
	}); // end of $(document).ready(function(){})

</script>

<div style="display: flex; margin-bottom: 50px;">   
  	<div style="width: 80%; min-height: 1100px; margin:auto; ">

     	<h2 style="margin: 50px 0;">HR 사원정보 조회하기</h2>
     	
     	<form name="searchFrm">
     		<c:if test="${not empty requestScope.deptIdList}">
     			<span style="display: inline-block; width: 150px; font-weight: bold;">부서번호선택</span> 
     			<c:forEach var="deptId" items="${requestScope.deptIdList}" varStatus="status">
     				<label for="${status.index}">
     					<c:if test="${deptId == -9999}">부서없음</c:if>
     					<c:if test="${deptId != -9999}">${deptId}</c:if>
     				</label>
     				<input type="checkbox" id="${status.index}" name="deptId" value="${deptId}" />&nbsp;&nbsp;
     			</c:forEach>
     		</c:if>
     		
     		<select name="gender" id="gender" style="height: 30px; width: 120px; margin: 10px 30px 0 0;"> 
         		<option value="">성별선택</option>
         		<option>남</option>
         		<option>여</option>
       		</select>
     		<button type="button" class="btn btn-secondary btn-sm" id="btnSearch">검색하기</button>
       		&nbsp;&nbsp;
     		<button type="button" class="btn btn-success btn-sm" id="btnExcel">Excel 파일로 저장</button>
     	</form>
     	
     	
     	
  	</div>
</div>
    