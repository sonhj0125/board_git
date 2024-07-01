<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String ctxPath = request.getContextPath();
%>

<style type="text/css">

</style>    

<script type="text/javascript">

  $(document).ready(function(){
	   
	   // 삭제완료 버튼
	   $("button#btnDelete").click(function(){
		   
		   // 글암호 유효성 검사      
		   const pw = $("input:password[name='pw']").val();
		   if(pw == ""){
			   alert("글암호를 입력하세요!!");
			   return; // 종료
		   }
		   else {
			   if("${requestScope.boardvo.pw}" != pw) {
			   	   alert("입력하신 글암호가 올바르지 않습니다.");
			   	   return; // 종료
			   }
		   }
		   
		   if(confirm("정말로 글을 삭제하시겠습니까?")) {
			
			   // 폼(form)을 전송(submit)
			   const frm = document.delFrm;
			   frm.method = "post";
			   frm.action = "<%= ctxPath%>/delEnd.action";
			   frm.submit();   
		   }
		   
	   });
	   
  });// end of $(document).ready(function(){})-----------

</script>


<div style="display: flex;">
  <div style="margin: auto; padding-left: 3%;">
     
       <h2 style="margin-bottom: 30px;">글삭제</h2>
     
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
            <button type="button" class="btn btn-secondary btn-sm mr-3" id="btnDelete">삭제완료</button>
            <button type="button" class="btn btn-secondary btn-sm" onclick="javascript:history.back()">취소</button>  
        </div>
        
     </form>
     
  </div>
</div>
    