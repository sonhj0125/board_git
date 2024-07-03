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
		
		// 검색하기 버튼 클릭시
		$("button#btnSearch").click(function(){
			
			// const arr_deptId = []; 또는
			const arr_deptId = new Array();
			
			$("input:checkbox[name='deptId']:checked").each(function(index, item){
				
				arr_deptId.push($(item).val());
				
			}); // end of $("input:checkbox[name='deptId']:checked").each(function(index, item{})
			
			const str_deptId = arr_deptId.join();
			
			// console.log("~~~ 확인용 str_deptId : ", str_deptId);
			// ~~~ 확인용 str_deptId :  -9999
			// ~~~ 확인용 str_deptId :  10,20,50,60
			
			const frm = document.searchFrm;
			frm.str_deptId.value = str_deptId;
			
			// frm.method = "get";		// 기본이 get 방식
			// frm.action = "<%=ctxPath%>/emp/employeeList.action";	// action 에 해당하는 URL 을 쓰지 않으면 기본은 현재 페이지 URL이 된다.
			frm.submit();
			
		}); // end of $("button#btnSearch").click(function(){})
		
		
		// 검색한 조건 유지시키기
		// === 부서번호 체크박스 유지시키기 시작 === ///
		const str_deptId = "${requestScope.str_deptId}";
		
		// console.log("~~~ 확인용 str_deptId => ", str_deptId);
		// ~~~ 확인용 str_deptId =>  
		// ~~~ 확인용 str_deptId =>  -9999,20,30,50
		
		if(str_deptId != ""){
			
			const arr_deptId = str_deptId.split(",");
			// [-9999,20,30,50]
			
			$("input:checkbox[name='deptId']").each(function(index, elmt){
			
				for(let i=0; i<arr_deptId.length; i++){
					
					if($(elmt).val() == arr_deptId[i]){
						$(elmt).prop("checked", true);
						break;
					}
				} // end of for
				
			}); // end of $("input:checkbox[name='deptId']").each(function(index, elmt){})
			
		} // end of if(str_deptId != "")
		// === 부서번호 체크박스 유지시키기 끝 === ///
		
		
		// === 성별 유지시키기 시작 === ///
		const gender = "${requestScope.gender}";
		
		if(gender != ""){
			
			$("select[name='gender']").val(gender);
			
		} // end of if(gender != "")
		// === 성별 유지시키기 끝 === ///
		
		
		// === #207. Excel 파일로 다운받기 시작 === //
		$("button#btnExcel").click(function(){
			
			const arr_deptId = new Array();
			
			$("input:checkbox[name='deptId']:checked").each(function(index, item){
				
				arr_deptId.push($(item).val());
				
			}); // end of $("input:checkbox[name='deptId']:checked").each(function(index, item{})
			
			const str_deptId = arr_deptId.join();
		
			
			const frm = document.searchFrm;
			frm.str_deptId.value = str_deptId;
			
			frm.method = "post";		
			frm.action = "<%=ctxPath%>/emp/downloadExcelFile.action";
			frm.submit();
			
			
			
		}); // end of $("button#btnExcel").click(function(){})
		
		
		
		// === Excel 파일로 다운받기 끝 === //
		
		
		
		
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
     		
     		<input type="hidden" name="str_deptId" />
     		
     		<select name="gender" style="height: 30px; width: 120px; margin: 10px 30px 0 0;"> 
         		<option value="">성별선택</option>
         		<option>남</option>
         		<option>여</option>
       		</select>
     		<button type="button" class="btn btn-secondary btn-sm" id="btnSearch">검색하기</button>
       		&nbsp;&nbsp;
     		<button type="button" class="btn btn-success btn-sm" id="btnExcel">Excel 파일로 저장</button>
     		
     		
     	</form>
     	
     	<br>
     	
     	<table id="emptbl">
	      	<thead>
		        <tr>
		            <th>부서번호</th>
		            <th>부서명</th>
		            <th>사원번호</th>
		            <th>사원명</th>
		            <th>입사일자</th>
		            <th>월급</th>
		            <th>성별</th>
		            <th>나이</th>
		        </tr>
	      	</thead>
	      	<tbody>
	      	<c:if test="${not empty requestScope.employeeList}"></c:if>
	      		<c:forEach var="map" items="${requestScope.employeeList}">
	      			<tr>
	      				<td style="text-align: center;">${map.department_id}</td>	<%-- department_id 은 hr_mapper_interface.xml 에서 정의해준 HashMap의 키이다. --%>
	      				<td>${map.department_name}</td>
	      				<td style="text-align: center;">${map.employee_id}</td>
	      				<td>${map.fullname}</td>
	      				<td style="text-align: center;">${map.hire_date}</td>
	      				<td style="text-align: right;"><fmt:formatNumber value="${map.monthsal}" pattern="#,###" /></td>
	      				<td style="text-align: center;">${map.gender}</td>
	      				<td style="text-align: center;">${map.age}</td>
	      			</tr>
	      		</c:forEach>
     		</tbody>
       </table>
     	
  	</div>
</div>
    