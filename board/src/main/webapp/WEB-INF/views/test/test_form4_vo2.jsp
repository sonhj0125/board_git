<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	String ctxPath = request.getContextPath();
    //     /board
%>    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>form 연습4_vo2</title>

<script type="text/javascript" src="<%=ctxPath%>/resources/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		
		const now = new Date();
	    // 자바스크립트에서 현재 날짜 시각을 알려주는 것이다.
	    
	    const year  = now.getFullYear(); // 현재 년도
	    let   month = now.getMonth() + 1; // 현재 월 ★주의★
	    let   date   = now.getDate(); // 현재 일

	    if(month < 10)
	        month = "0" + month;

	    if(date < 10)
	        date = "0" + date;
	    
	    const today = year + "-" + month + "-" + date;
	    document.querySelector("input[name='writeday']").defaultValue = today;
	    // 제이쿼리가 아닌 순수 자바스크립트로 해야 함!
	    
		
		$("form[name='testFrm']").submit(function(){
			
			const no_val = $("input:text[name='no']").val();
			const name_val = $("input:text[name='name']").val();
			
			if( no_val.trim() == "" || name_val.trim() == "" ) {
				alert("번호와 성명 모두 입력하세요!!");
			 	return false; // submit(전송)을 하지말라는 뜻이다. 
			}
			
		});
		
	});
</script>

</head>
<body>
	<div>/test/test_form4_vo2.action 페이지</div>
	<form name="testFrm" action="<%= ctxPath%>/test/test_form4_vo2.action" method="post"> 
		번호 : <input type="text" name="no" /><br>
		성명 : <input type="text" name="name" /><br>
		날짜 : <input type="date" name="writeday" /><br><br>
	    <input type="submit" value="확인" />
	    <input type="reset"  value="취소" /> 
	</form>

</body>
</html>




