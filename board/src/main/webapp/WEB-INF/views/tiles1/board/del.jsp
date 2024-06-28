<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
   String ctxPath = request.getContextPath();
%>

<style type="text/css">

</style>    

<script type="text/javascript">

$(document).ready(function() {
	  

	// 삭제하기 버튼 클릭 시
	$("button#btnDel").click(function() {
	 
		// 글암호 유효성 검사
		const pw = $("input:password[name='pw']").val();
		
		if(pw == "") {
			alert("글암호를 입력하세요!");
			return; // 종료
		 
		} else if(pw != "${requestScope.boardvo.pw}") {
			
			alert("입력하신 글암호가 올바르지 않습니다.");
			$("input:password[name='pw']").val(""); // 암호 입력한 것 비우기
			return; // 종료
		}
		
		if(confirm("정말 삭제하시겠습니까?")) {
		
			// 폼(form)을 전송(submit)
			const frm = document.delFrm;
			frm.method = "post";
			frm.action = "<%=ctxPath%>/delEnd.action";
			frm.submit();
		}
		
	}); // end of $("button#btnDel").click(function() {}) -------------
     
});// end of $(document).ready(function(){})-----------

</script>


<div style="display: flex;">
	<div style="margin: auto; padding-left: 3%;">


		<h2 style="margin-bottom: 30px;">글 삭제</h2>
		
		<form name="delFrm">
			<table style="width: 1024px" class="table table-bordered">
				<tr>
					<th style="width: 15%; background-color: #DDDDDD;">글암호</th>
					<td>
						<input type="hidden" name="seq" value="${requestScope.boardvo.seq}" />
						<input type="password" name="pw" maxlength="20" />
					</td>
				</tr>
			</table>

			<div style="margin: 20px;">
				<button type="button" class="btn btn-secondary btn-sm mr-3" id="btnDel">삭제하기</button>
				<button type="button" class="btn btn-secondary btn-sm" onclick="javascript:history.back()">취소</button>
			</div>

		</form>

	</div>
</div>