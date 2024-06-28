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
<title>AJAX 연습 1</title>

<style type="text/css">
   table, th, td {
      border: solid 1px gray;
      border-collapse: collapse;
   }
</style>

<script type="text/javascript" src="<%=ctxPath%>/resources/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		
		func_ajax_select();
		 
		$("button#btnOK").click(function() {
			
			const no_val = $("input:text[name='no']").val();
         	const name_val = $("input:text[name='name']").val();
	         
			if( no_val.trim() == "" || name_val.trim() == "" ) {
				alert("번호와 성명 모두 입력하세요!");
			    return; // 종료 
			}
			
			$.ajax({
				url: "<%=ctxPath%>/test/ajax_insert.action",
				type: "post",
				data: {"no":no_val,
					   "name":name_val},
				dataType: "json",
				success: function(json) {
//					console.log(JSON.stringify(json));
					// {"n":1}
					
					if(json.n == 1) {
						func_ajax_select();
						$("input:text[name='no']").val("");
						$("input:text[name='name']").val("");
					}
				},
				error: function(request, status, error){
	            	alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
	            }
			});
			
		}); // end of $("button#btnOK").click(function() {}) ---------------------
		
	}); // end of $(document).ready(function() {}) --------------------------------
	
	
	
	function func_ajax_select() {
		
		$.ajax({
			url: "<%=ctxPath%>/test/ajax_select.action",
//			type: "get",
			dataType: "json",
			success: function(json) {
//				console.log(JSON.stringify(json));
				/*
				[{"no":"101","name":"이순신","writeday":"2024-06-11 17:18:02"},
				 {"no":"101","name":"이순신","writeday":"2024-06-11 17:27:15"},
				 {"no":"102","name":"박보영","writeday":"2024-06-12 09:38:17"},
				 {"no":"103","name":"변우석","writeday":"2024-06-12 09:38:17"},
				 {"no":"1004","name":"ê¹ë¤ì","writeday":"2024-06-12 11:34:23"},
				 {"no":"1005","name":"김다영","writeday":"2024-06-12 11:44:18"},
				 {"no":"1006","name":"다영김","writeday":"2024-06-12 12:11:31"},
				 {"no":"1007","name":"KimDaYoung","writeday":"2024-06-12 12:19:41"},
				 {"no":"1008","name":"배인혁","writeday":"2024-06-12 12:24:47"},
				 {"no":"1009","name":"차은우","writeday":"2024-06-12 12:45:30"}]
				*/
				
				let v_html = `<table>
							  	<tr>
							  		<th>번호</th>
							  		<th>입력번호</th>
							  		<th>성명</th>
							  		<th>날짜</th>
							  	</tr>
							  	<tr>`;
				
				$.each(json, function(index, item) {
					v_html += `<tr>
									<td>\${index+1}</td>
									<td>\${item.no}</td>
									<td>\${item.name}</td>
									<td>\${item.writeday}</td>
							   </tr>`;
				});
				
				v_html += `</table>`;
				
				$("div#view").html(v_html);
			},
			error: function(request, status, error){
            	alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
            }
		});
		
	} // end of function func_ajax_select() ------------------
</script>

</head>
<body>
	<h2>Ajax 연습 1</h2>
    <p>
             안녕하세요?<br>
             여기는  /board/test/test_form6.action 페이지 입니다.  
    </p>
    
	번호 : <input type="text" name="no" /><br>
	성명 : <input type="text" name="name" /><br>
	<button type="button" id="btnOK">확인</button>
	<button type="reset" id="btnCancel">취소</button>
	<br><br> 

	<div id="view"></div>
</body>
</html>




