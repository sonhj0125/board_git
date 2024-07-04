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
			
			$("input:checkbox[name='deptId']:checked").each(function(index, elmt){
				
				arr_deptId.push($(elmt).val());
				
			}); // end of $("input:checkbox[name='deptId']:checked").each(function(index, elmt{})
			
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
		
		
		
		// 검색하기 버튼 클릭시(ajax)
		$("button#btnSearchAjax").click(function(){
			
			const arr_deptId = [];
			
			$("input:checkbox[name='deptId']:checked").each(function(index, elmt){
				
				arr_deptId.push($(elmt).val());
				
			}); // end of $("input:checkbox[name='deptId']:checked").each(function(index, elmt{})
			
			const str_deptId = arr_deptId.join();
			
	         $.ajax({
	             url: "${pageContext.request.contextPath}/emp/employeeListJSON.action",
	             data: {
	                "str_deptId": str_deptId,
	                "gender": $("select[name='gender']").val()
	             },
	             dataType: "json",
	             success: function(json) {
				 // console.log(JSON.stringify(json));
	             /*
	                [{"monthsal":"4400","gender":"여","department_id":"10","department_name":"Administration","employee_id":"200","fullname":"Jennifer Whalen","hire_date":"2003-09-17","age":"45"},
	                 {"monthsal":"13000","gender":"여","department_id":"20","department_name":"Marketing","employee_id":"201","fullname":"Michael Hartstein","hire_date":"2004-02-17","age":"44"},
	                 {"monthsal":"6000","gender":"여","department_id":"20","department_name":"Marketing","employee_id":"202","fullname":"Pat Fay","hire_date":"2005-08-17","age":"37"},
	                 {"monthsal":"11000","gender":"여","department_id":"30","department_name":"Purchasing","employee_id":"114","fullname":"Den Raphaely","hire_date":"2002-12-07","age":"56"},
	                 {"monthsal":"3100","gender":"남","department_id":"30","department_name":"Purchasing","employee_id":"115","fullname":"Alexander Khoo","hire_date":"2003-05-18","age":"62"},
	                 {"monthsal":"2900","gender":"남","department_id":"30","department_name":"Purchasing","employee_id":"116","fullname":"Shelli Baida","hire_date":"2005-12-24","age":"62"},
	                 {"monthsal":"2800","gender":"여","department_id":"30","department_name":"Purchasing","employee_id":"117","fullname":"Sigal Tobias","hire_date":"2005-07-24","age":"62"},
	                 {"monthsal":"2600","gender":"남","department_id":"30","department_name":"Purchasing","employee_id":"118","fullname":"Guy Himuro","hire_date":"2006-11-15","age":"45"},
	                 {"monthsal":"2500","gender":"남","department_id":"30","department_name":"Purchasing","employee_id":"119","fullname":"Karen Colmenares","hire_date":"2007-08-10","age":"44"}]
	             */
	                
	                let v_html = ``;
	                
	                json.forEach(function(item, index, array) {
	                   v_html += `<tr>
	                                <td style="text-align: center;">\${item.department_id}</td>   <%-- department_id 은 hr_mapper_interface.xml 에서 정의해준 HashMap의 키이다. --%>
	                                <td>\${item.department_name}</td>
	                                <td style="text-align: center;">\${item.employee_id}</td>
	                                <td>\${item.fullname}</td>
	                                <td style="text-align: center;">\${item.hire_date}</td>
	                                <td style="text-align: right;">\${Number(item.monthsal).toLocaleString('en')}</td>
	                                <td style="text-align: center;">\${item.gender}</td>
	                                <td style="text-align: center;">\${item.age}</td>
	                               </tr>`;
	                });
	                
	                $("table#emptbl tbody").html(v_html);
	                
	             },
	             error: function(request, status, error){
	                   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
	                }
	          });
			
			
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
		
		
		
		
		// ==== #210. 엑셀관련파일 업로드 하기 시작  ====
		$("button#btn_upload_excel").click(function(){
			
			if($("input#upload_excel_file").val() == ""){
				alert("업로드할 엑셀파일을 선택하세요!!");
				return;
			}
			else {
				let formData = new FormData($("form[name='excel_upload_frm']").get(0));   // 폼태그에 작성된 모든 데이터 보내기  
	            // jQuery선택자.get(0) 은 jQuery 선택자인 jQuery Object 를 DOM(Document Object Model) element 로 바꿔주는 것이다. 
	            // DOM element 로 바꿔주어야 순수한 javascript 문법과 명령어를 사용할 수 있게 된다.
	            
	            // 또는
	            // let formData = new FormData(document.getElementById("excel_upload_frm")); // 폼태그에 작성된 모든 데이터 보내기  
				$.ajax({
					
                    url:"<%= request.getContextPath()%>/emp/uploadExcelFile.action",
                    type:"post",
                    data:formData,
                    processData:false,  // 파일 전송시 설정 
                    contentType:false,  // 파일 전송시 설정 
                    dataType:"json",
                    success:function(json){
                       	// console.log("~~~ 확인용 : " + JSON.stringify(json));
                        // ~~~ 확인용 : {"result":1}
                        if(json.result == 1) {
                           alert("엑셀파일 업로드 성공했습니다.^^");
                        }
                        else {
                           alert("엑셀파일 업로드 실패했습니다.ㅜㅜ");
                        }
                    },
                    error: function(request, status, error){
                    	alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
                    }
                    
                }); // end of $.ajax
	            
	            
	            
	            
			}
			
			
		}); // end of $("button#btn_upload_excel").click(function(){})
		
		
		// ==== 엑셀관련파일 업로드 하기 끝  ====
			
		
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
       		<button type="button" class="btn btn-info btn-sm" id="btnSearchAjax">검색하기(ajax)</button>
       		&nbsp;&nbsp;
     		<button type="button" class="btn btn-success btn-sm" id="btnExcel">Excel 파일로 저장</button>
     		
     	</form>
     	
     	<br>
     	
     	
     	
     	<%-- ==== #209. 엑셀관련파일 업로드 하기 시작 ==== --%>
	   	<form style="margin-bottom: 10px;" name="excel_upload_frm" method="post" enctype="multipart/form-data" >
     		<input type="file" id="upload_excel_file" name="excel_file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" />
	      	<br>
	      	<button type="button" class="btn btn-info btn-sm" id="btn_upload_excel" style="margin-top: 1%;">Excel 파일 업로드 하기</button>
	   	</form>
	   	<%-- ==== 엑셀관련파일 업로드 하기 끝 ==== --%>
     	
     	
     	
     	
     	
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
    