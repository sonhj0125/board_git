<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
   String ctxPath = request.getContextPath();
%>

<style type="text/css">
   
   	span.move  {cursor: pointer; color: navy;}
   	.moveColor {color: #660029; font-weight: bold; background-color: #ffffe6;}

	td.comment {text-align: center;}

    a {text-decoration: none !important;}
    
</style>


<script type="text/javascript">

	$(document).ready(function(){
		
		goReadComment()		// 페이지 처리 안한 댓글 읽어오기
		
		$("span.move").hover(function(e){	// mouseover
            	$(e.target).addClass("moveColor");
          	}, 
            function(e){	// mouseout
            	$(e.target).removeClass("moveColor");  
        }); // end of $("span.move").hover(function(e){})
		
		
		$("input:text[name='content']").bind("keydown", function(e){
			
			if(e.keyCode == 13) { 	// 엔터
				goAddWrite();
			}
			
		}); // end of $("input:text[name='content']").bind("keydown", function(e){})
		
		
		
	}); // end of $(document).ready(function())
	
	
	// Function Declaraction
	
	// == 댓글쓰기 == //
	function goAddWrite()	{
		
		const comment_content = $("input:text[name='content']").val().trim();
		
		if(comment_content == "") {
			alert("댓글 내용을 입력하세요.");
			return;		// 종료
		}
		
		// 첨부파일이 없는 댓글 쓰기인 경우
		goAddWrite_noAttach();
		
		
		
		// 첨부파일이 있는 댓글 쓰기인 경우
		
	} // end of function goAddWrite()
	
	
	// 첨부파일이 없는 댓글 쓰기인 경우
	function goAddWrite_noAttach() {
		<%--
        	// 보내야할 데이터를 선정하는 또 다른 방법
       		// jQuery에서 사용하는 것으로써,
       		// form태그의 선택자.serialize(); 을 해주면 form 태그내의 모든 값들을 name값을 키값으로 만들어서 보내준다. 
       		const queryString = $("form[name='addWriteFrm']").serialize();
    	--%>
    	
    	const queryString = $("form[name='addWriteFrm']").serialize();
    	
    	
		$.ajax({
			
			url:"<%=ctxPath%>/addComment.action",
		/*
			data:{"fk_userid":$("input:hidden[name='fk_userid']").val() 
	             ,"name":$("input:text[name='name']").val() 
	             ,"content":$("input:text[name='content']").val()
	             ,"parentSeq":$("input:hidden[name='parentSeq']").val()
	             },
		*/
		// 또는
			data:queryString,
			
			type:"post",
			dataType:"json",
			success:function(json){
				
			 //	console.log(JSON.stringify(json));
			 // {"name":"손혜정","n":1}
			 // 또는
			 //	{"name":"손혜정","n":0}
				
				if(json.n == 0){
					alert(json.name + "님의 포인트는 300점을 초과할 수 없으므로 댓글쓰기가 불가합니다.");	
				}
				else {
					goReadComment();		// 페이지 처리 안한 댓글 읽어오기
											// 페이지 처리 한 댓글 읽어오기	
				}
				
				
			 	$("input:text[name='content']").val("");
		
				
				
			},
			error: function(request, status, error){
		        alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
		    }
			
		}); // end of $.ajax
		
	} // end of function goAddWrite_noAttach()
	
	
	
	// 페이지 처리 안한 댓글 읽어오기
	function goReadComment() {		
		
		$.ajax({
			
			url:"<%=ctxPath%>/readComment.action",
			data:{"parentSeq":"${requestScope.boardvo.seq}"},
			dataType:"json",
			success:function(json){
				
				// console.log(JSON.stringify(json));
				// 댓글이 있는 경우 : [{"name":"손혜정","regdate":"2024-06-18 16:06:11","fk_userid":"ejss0125","seq":"30","content":"두번째 댓글이다"},{"name":"손혜정","regdate":"2024-06-18 16:05:51","fk_userid":"ejss0125","seq":"29","content":"두번째 댓글이다"},{"name":"손혜정","regdate":"2024-06-18 16:05:47","fk_userid":"ejss0125","seq":"28","content":"두번째 댓글이다"},{"name":"손혜정","regdate":"2024-06-18 15:38:54","fk_userid":"ejss0125","seq":"27","content":"나도갈래"}]
				// 댓글이 없는 경우 : []
				
				let v_html = "";
				
				if(json.length > 0) {	// 댓글이 있음
					
					$.each(json, function(index, item){
						
						v_html += "<tr>";
						v_html += 	"<td>"+item.content+"</td>";
						v_html += 	"<td class='comment'>"+item.name+"</td>";
						v_html += 	"<td class='comment'>"+item.regdate+"</td>";
						
						if( ${sessionScope.loginuser != null} && 
						   "${sessionScope.loginuser.userid}" == item.fk_userid) {
							
							v_html += "<td class='comment'><button class='btn btn-secondary btn-sm btnUpdateComment'>수정</button>&nbsp;<button class='btn btn-secondary btn-sm btnDeleteComment'>삭제</button></td>";
						
						}
						
						v_html += "</tr>";
						
					}); // end of $.each
					
				} 
				else {	// 댓글이 없음
					
					v_html += "<tr>";
					v_html += 	"<td colspan='4'>댓글이 없습니다.</td>";
					v_html += "</tr>";
					
				} // end of if(json.length > 0)
				
					
				$("tbody#commentDisplay").html(v_html);
				
				
			},
			error: function(request, status, error){
		        alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
		    }
			
		}); // end of $.ajax
		
		
		
		
	
	} // end of function goReadComment()
	
</script>

<div style="display: flex;">
	<div style="margin: auto; padding-left: 3%;">

   	<h2 style="margin-bottom: 30px;">글내용보기</h2>
   	
	   	<c:if test="${not empty requestScope.boardvo}">
	   		<table class="table table-bordered table-dark" style="width: 1024px; word-wrap: break-word; table-layout: fixed;" >
	   			<tr>
              		<th style="width: 15%">글번호</th>
               		<td>${requestScope.boardvo.seq}</td>
        		</tr>   
         
	           	<tr>
	              	<th>성명</th>
	               	<td>${requestScope.boardvo.name}</td>
	           	</tr> 
	         
	           	<tr>
              		<th>제목</th>
	               	<td>${requestScope.boardvo.subject}</td>
	           	</tr>
	           	<tr>
              		<th>내용</th>
	               	<td>
	               	<p style="word-break: break-all;">${requestScope.boardvo.content}
		            <%-- 
	                	style="word-break: break-all; 은 공백없는 긴영문일 경우 width 크기를 뚫고 나오는 것을 막는 것임. 
	                   	그런데 style="word-break: break-all; 나 style="word-wrap: break-word; 은
	                   	테이블태그의 <td>태그에는 안되고 <p> 나 <div> 태그안에서 적용되어지므로 <td>태그에서 적용하려면
	              		<table>태그속에 style="word-wrap: break-word; table-layout: fixed;" 을 주면 된다.
	            	--%>
            		</p>
            		</td>
	           	</tr>
	           	<tr>
              		<th>조회수</th>
	               	<td>${requestScope.boardvo.readCount}</td>
	           	</tr>
	           	<tr>
              		<th>날짜</th>
	               	<td>${requestScope.boardvo.regDate}</td>
	           	</tr>
	   		</table>
	   	</c:if>
	   	
	   	
	   	<c:if test="${empty requestScope.boardvo}">
	   		<div style="padding: 20px 0; font-size: 16pt; color: red;" >존재하지 않습니다.</div> 
	   	</c:if>
	   	
	   	<div class="mt-5">
	   		<%-- 글 조회수 1 증가를 위해서 view.action 대신에 view_2.action 으로 바꾼다. --%>
	   		<div style="margin-bottom: 1%;">이전글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='view_2.action?seq=${requestScope.boardvo.previousseq}'">${requestScope.boardvo.previoussubject}</span></div>
	   		<div style="margin-bottom: 1%;">다음글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='view_2.action?seq=${requestScope.boardvo.nextseq}'">${requestScope.boardvo.nextsubject}">${requestScope.boardvo.nextsubject}</span></div>
	   		<br>
	   		<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>/list.action'">전체목록보기</button>
	   		
	   		<c:if test="${not empty sessionScope.loginuser && sessionScope.loginuser.userid == requestScope.boardvo.fk_userid}">
	   			<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>/edit.action?seq=${requestScope.boardvo.seq}'">글수정하기</button>
	   			<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>/del.action?seq=${requestScope.boardvo.seq}'">글삭제하기</button>
	   		</c:if>
	   		
	   		
	   		<%-- === #83. 댓글쓰기 폼 추가 === --%>
	   		<c:if test="${not empty sessionScope.loginuser}">
	   			<h3 style="margin-top: 50px;">댓글쓰기</h3>
	   			<form name="addWriteFrm" id="addWriteFrm" style="margin-top: 20px;">
	   			
	             	<table class="table" style="width: 1024px">
	             	
	               		<tr style="height: 30px;">
	                  		<th width="10%">성명</th>
	                  		<td>
	                  			<input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userid}" readonly />
	                  			<input type="text" name="name" value="${sessionScope.loginuser.name}" readonly />
	                  		</td>
	                  	</tr>
	                  	
	                  	<tr style="height: 30px;">
                  			<th>댓글내용</th>
                  			<td>
                  				<input type="text" name="content" size="100" maxlength="1000" />
                  				
                  				<%-- 댓글에 달리는 원게시물 글번호(즉, 댓글의 부모글 글번호) --%>
                  				<input type="hidden" name="parentSeq" value="${requestScope.boardvo.seq}" readonly />
                  			</td>
                  		</tr>
                  		
                  		<tr>
			                <th colspan="2">
	                        	<button type="button" class="btn btn-success btn-sm mr-3" onclick="goAddWrite()">댓글쓰기 확인</button>
			                    <button type="reset" class="btn btn-success btn-sm">댓글쓰기 취소</button>
			                </th>
		               	</tr>
		               	
		           	</table>         
		           	
		         </form>
	   		</c:if>
	   		
	   		<%-- === #94. 댓글 내용 보여주기 === --%>
       		<h3 style="margin-top: 50px;">댓글내용</h3>
	      	<table class="table" style="width: 1024px; margin-top: 2%; margin-bottom: 3%;">
	         	<thead>
	         		<tr>
	            		<th style="text-align: center;">내용</th>
	            		<th style="width: 8%; text-align: center;">작성자</th>
	            		<th style="width: 12%; text-align: center;">작성일자</th>
	            		<th style="width: 12%; text-align: center;">수정/삭제</th>
	         		</tr>
	         	</thead>
         		<tbody id="commentDisplay">
         		
         		
         		
         		</tbody>
	      	</table>
	   		
	   		
	   		
	   	</div>
	   	
   	</div>
</div>
   	