<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String ctxPath = request.getContextPath();
%>

<style type="text/css">
    
    th {background-color: #ddd}
    
    .subjectStyle {font-weight: bold;
                   color: navy;
                   cursor: pointer; }
                   
    a {text-decoration: none !important;} /* 페이지바의 a 태그에 밑줄 없애기 */
    
</style>

<script type="text/javascript">
	
	$(document).ready(function(){
		
		
		$("span.subject").hover(function(e){	// mouse over
			
			$(e.target).addClass("subjectStyle");
			
		}, function(e){	// mouse out
			
			$(e.target).removeClass("subjectStyle");
			
		}); // end of $("span.subject").hover(function(e){})
		
		
		$("input:text[name='searchWord']").bind("keyup", function(e){
			
			if(e.keyCode == 13) {	// 엔터를 했을 경우
				goSearch();
			}

		}); // end of $("input:text[name='searchWord']").bind("keyup", function(e){})
		
		
		
		// 검색 시 검색타입, 검색 단어 그대로 유지시키기
		if(${not empty requestScope.paraMap}){
			
			$("select[name='searchType']").val("${requestScope.paraMap.searchType}");
			$("input:text[name='searchWord']").val("${requestScope.paraMap.searchWord}");
			
		}
			
		
	}); // end of $(document).ready(function(){})

	
	// Function Declaration
	function goView(seq)	{
		
		location.href = `<%=ctxPath%>/view.action?seq=\${seq}`;
		<%-- location.href = "<%=ctxPath%>/view.action?seq="+seq; --%>
		
	} // end of function goView(seq)
	
	
	function goSearch(){
		
		const frm = document.searchFrm;
<%--	frm.method = "get";
		frm.action = "<%=ctxPath%>/list.action"; --%>
		frm.submit();
		
	} // end of function goSearch()
	
	
	
	
	
	
	
	

</script>

<div style="display: flex;">
	<div style="margin: auto; padding-left: 3%;">

   	<h2 style="margin-bottom: 30px;">글목록</h2>
   
   	<table style="width: 1024px" class="table table-bordered">
      	<thead>
          	<tr>
             	<th style="width: 70px;  text-align: center;">글번호</th>
            	<th style="width: 360px; text-align: center;">제목</th>
            	<th style="width: 70px;  text-align: center;">성명</th>
            	<th style="width: 150px; text-align: center;">날짜</th>
            	<th style="width: 70px;  text-align: center;">조회수</th>
          	</tr>
      	</thead>
      	<tbody>
       		<c:if test="${not empty requestScope.boardList}">
       			<c:forEach var="boardvo" items="${requestScope.boardList}">
       				<tr>
       					<td align="center">${boardvo.seq}</td>
       					<td>
       						<c:if test="${boardvo.commentCount > 0}">
       							<span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}<span style="vertical-align: super;">[<span style="color: red; font-size: 9pt; font-style: italic; font-weight: bold;">${boardvo.commentCount}</span>]</span></span>
       						</c:if>
       						<c:if test="${boardvo.commentCount == 0}">
       							<span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}</span>
       						</c:if>
       					</td>
       					<td align="center">${boardvo.name}</td>
       					<td align="center">${boardvo.regDate}</td>
       					<td align="center">${boardvo.readCount}</td>
       				</tr>
       			</c:forEach>
       		</c:if>
       		
       		<c:if test="${empty requestScope.boardList}">
       			<tr>
       				<td colspan="5">데이터가 없습니다.</td>
       			</tr>
       		</c:if>
      	</tbody>
      
     </table>
     
     <%-- === #109. 글검색 폼 추가하기 : 글제목, 글내용, 글제목+글내용, 글쓴이로 검색을 하도록 한다.  === --%>
     <form name="searchFrm" style="margin-top: 20px;">
      	<select name="searchType" style="height: 26px;">
         	<option value="subject">글제목</option>
         	<option value="content">글내용</option>
         	<option value="subject_content">글제목+글내용</option>
         	<option value="name">글쓴이</option>
      	</select>
      	<input type="text" name="searchWord" size="40" autocomplete="off" /> 
      	<input type="text" style="display: none;"/> <%-- form 태그내에 input 태그가 오로지 1개 뿐일경우에는 엔터를 했을 경우 검색이 되어지므로 이것을 방지하고자 만든것이다. --%> 
      	<button type="button" class="btn btn-secondary btn-sm" onclick="goSearch()">검색</button>
     </form>
     
     
     
	</div>      
</div>
    
    
    