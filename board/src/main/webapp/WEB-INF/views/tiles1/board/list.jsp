<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
	$(document).ready(function() {

		// 글제목 hover 이벤트
		$("span.subject").hover(function(e) { // mouseover
			$(e.target).addClass("subjectStyle");
			
		}, function(e) { // mouseout
			$(e.target).removeClass("subjectStyle");
			
		});
		
		$("input:text[name='searchWord']").bind("keyup", function(e) {
			if(e.keyCode == 13) {
				goSearch();
			}
		});
		
		
		// 검색 시 검색 조건, 검색어 값 유지시키기
		if(${not empty requestScope.paraMap}) {
			
			$("select[name='searchType']").val("${requestScope.paraMap.searchType}");
			$("input:text[name='searchWord']").val("${requestScope.paraMap.searchWord}");
		}
		
		
		<%-- === #115. 검색어 입력 시 자동 글 완성하기 2 === --%>
		$("div#displayList").hide();
		
		$("input:text[name='searchWord']").keyup(function() {
			
			const wordLength = $(this).val().trim().length;
			// 검색어에서 공백을 제거한 길이를 알아온다.
			
			if(wordLength == 0) {
				$("div#displayList").hide();
				// 검색어가 공백이거나 검색어 입력 후 백스페이스키를 눌러서 검색어를 모두 지우면 검색된 내용이 안 나오도록 해야 한다.
				
			} else {
				
				if($("select[name='searchType']").val() == "subject" || 
				   $("select[name='searchType']").val() == "name") {
					
					$.ajax({
						url: "<%=ctxPath%>/wordSearchShow.action",
					 // type: "get",
						data: {
							"searchType": $("select[name='searchType']").val(),
							"searchWord": $(this).val() // $("input:text[name='searchWord']").val()
						},
						dataType: "json",
						success: function(json) {
//							console.log(JSON.stringify(json));
							/*
								[{"word":"java는 재밌다"},
								 {"word":"jAVA를 배우고 있어요"},
								 {"word":"다음 달부터 JAVA를 배우려고 합니다"},
								 {"word":"Java 개발자 취업률 어떤가요?"},
								 {"word":"저는 javascript가 어려워요"},
								 {"word":"웹을 하려면 jaVaScript를 해야 하나요?"}]
							//	또는
								[]
							*/
							
							<%-- === #120. 검색어 입력 시 자동 글 완성하기 7 === --%>
							if(json.length > 0) {
								// 검색된 데이터가 있는 경우
								
								let v_html = ``;
								
								$.each(json, function(index, item) {
									const word = item.word;
									// word ==> 다음 달부터 JAVA를 배우려고 합니다
									// word ==> jAVA를 배우고 있어요
									// word ==> 웹을 하려면 jaVaScript를 해야 하나요?
									
								 // word.toLowerCase()은 word를 모두 소문자로 변경하는 것이다.
									// word ==> 다음 달부터 java를 배우려고 합니다
									// word ==> java를 배우고 있어요
									// word ==> 웹을 하려면 javascript를 해야 하나요?
									
									const idx = word.toLowerCase().indexOf($("input:text[name='searchWord']").val().toLowerCase());
									// 만약 검색어가 JavA 라면
									/*
									다음 달부터 java를 배우려고 합니다	   는 idx가 7이다.
									java를 배우고 있어요				   는 idx가 0이다.
									웹을 하려면 javascript를 해야 하나요? 는 idx가 7이다.
									*/
									
									const len = $("input:text[name='searchWord']").val().length;
									// 검색어(JavA)의 길이 len은 4가 된다.
									
									/*
										console.log("~~~~~ 시작 ~~~~~");
										console.log(word.substring(0, idx)); 	   // 검색어(JavA) 앞까지의 글자 ==> "웹을 하려면"
										console.log(word.substring(idx, idx+len)); // 검색어(JavA) 글자 ==> "jaVa"
										console.log(word.substring(idx+len)); 	   // 검색어(JavA) 뒤부터 끝까지 글자 ==> "Script를 해야 하나요?"
										console.log("~~~~~ 끝 ~~~~~");
									*/
									
									const result = word.substring(0, idx) + "<span style='color: purple;'>" + word.substring(idx, idx+len) + "</span>" + word.substring(idx+len)
									
									v_html += `<span style="cursor:pointer;" class="result">\${result}</span><br>`;
									
								}); // end of $.each() ----------------------------------------------
								
								const input_width = $("input[name='searchWord']").css("width"); // 검색어 input 태그 width 값 알아오기
								
								$("div#displayList").css({"width":input_width}); // '검색 결과 div'의 width 크기를 검색어 입력 input 태그의 width 와 일치시키기
								
								$("div#displayList").html(v_html);
								$("div#displayList").show();
							}
						},
						error: function(request, status, error){
							alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
						}
					});
				}
			}
			
		}); // end of $("input:text[name='searchWord']").keyup(function() {}) -------------------------
		
		
		<%-- === #121. 검색어 입력 시 자동 글 완성하기 8 === --%>
		$(document).on("click", "span.result", function(e) {
			const word = $(e.target).text();
			$("input[name='searchWord']").val(word); // 텍스트 박스에 검색된 문자열을 입력해준다.
			$("div#displayList").hide();
		});
		
	}); // end of $(document).ready(function() {}) ------------------
	
	
	// Function Declaration
	
	function goView(seq) {
		
    	const goBackURL = "${requestScope.goBackURL}";
		// "/list.action?searchType=subject&searchWord=정화&currentShowPageNo=3" 넘어와서 goBackURL 에 담긴 값이 이거라는 것이다.
		// & 가 종결자라서 goBackURL 속에는 "/list.action?searchType=subject" 까지만 담긴다.
      
		<%-- 		  
		 	아래처럼 get 방식으로 보내면 안된다. 왜냐하면 get방식에서 &는 전송될 데이터의 구분자로 사용되기 때문이다.       
			location.href = `<%= ctxPath%>/view.action?seq=\${seq}&goBackURL=\${goBackURL}`; /* 백틱이니까 $ 앞에 \ 붙이기 */
			location.href = "<%= ctxPath%>/view.action?seq="+seq; 로도 쓸 수 있음
		--%>
      
		<%-- 그러므로 & 를 글자 그대로 인식하는 post 방식으로 보내야 한다. 
			  아래의 $132. 에 표기된 form 태그를 먼저 만든다. --%>
		const frm = document.goViewFrm;
		frm.seq.value = seq;
		frm.goBackURL.value = goBackURL;		
		
		if(${not empty requestScope.paraMap}) { // 검색 조건이 있을 경우
			frm.searchType.value = "${requestScope.paraMap.searchType}";
			frm.searchWord.value = "${requestScope.paraMap.searchWord}";
		}
		  
		frm.method = "post";
		frm.action = "<%= ctxPath%>/view.action";
		frm.submit();
		
	} // end of function goView(seq) ---------------------
	
	function goSearch() {
		
		const frm = document.searchFrm;
		<%--
		frm.method = "get";
		frm.action = "<%=ctxPath%>/list.action";
		--%>
		frm.submit();
		
	} // end of function goSearch() -------------------
	
</script>

<div style="display: flex;">
	<div style="margin: auto; padding-left: 3%;">
	
		<h2 style="margin-bottom: 30px;">글목록</h2>
		
		<table style="width: 1024px" class="table table-bordered">
			<thead>
				<tr>
					<th style="width: 70px;  text-align: center;">순번</th>
					<th style="width: 70px;  text-align: center;">글번호</th>
					<th style="width: 360px; text-align: center;">제목</th>
					<th style="width: 70px;  text-align: center;">성명</th>
					<th style="width: 150px; text-align: center;">날짜</th>
					<th style="width: 70px;  text-align: center;">조회수</th>
					</tr>
			</thead>
			
			<tbody>
				<c:if test="${not empty requestScope.boardList}">
					<c:forEach var="boardvo" items="${requestScope.boardList}" varStatus="status">
						<tr>
							<td align="center">
								${(requestScope.totalCount) - (requestScope.currentShowPageNo - 1) * (requestScope.sizePerPage) - (status.index)}
							<%-- >>> 페이징 처리시 보여주는 순번 공식 <<<
								데이터개수 - (페이지번호 - 1) * 1페이지당보여줄개수 - 인덱스번호 => 순번 
								
								<예제>
								데이터개수 : 12
								1페이지당보여줄개수 : 5
								
								==> 1 페이지       
								12 - (1-1) * 5 - 0  => 12
								12 - (1-1) * 5 - 1  => 11
								12 - (1-1) * 5 - 2  => 10
								12 - (1-1) * 5 - 3  =>  9
								12 - (1-1) * 5 - 4  =>  8
								
								==> 2 페이지
								12 - (2-1) * 5 - 0  =>  7
								12 - (2-1) * 5 - 1  =>  6
								12 - (2-1) * 5 - 2  =>  5
								12 - (2-1) * 5 - 3  =>  4
								12 - (2-1) * 5 - 4  =>  3
								
								==> 3 페이지
								12 - (3-1) * 5 - 0  =>  2
								12 - (3-1) * 5 - 1  =>  1 
		                 	--%>
							</td>
							<td align="center">${boardvo.seq}</td>
							<td>
                   			<%-- === 댓글쓰기 및 답변형 및 파일첨부가 있는 게시판 시작 === --%> 
                     		<%-- 첨부파일이 없는 경우 시작 --%>
	                        <c:if test="${empty boardvo.fileName}">
	                        <%-- 답변글이 아닌 원글인 경우 시작 --%>  
	                        <c:if test="${boardvo.depthno == 0}">
	                           <c:if test="${boardvo.commentCount > 0}">
	                             <span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}<span style="vertical-align: super;">[<span style="color: red; font-size: 9pt; font-style: italic; font-weight: bold;">${boardvo.commentCount}</span>]</span></span> 
	                           </c:if>
	                           
	                           <c:if test="${boardvo.commentCount == 0}">
	                             <span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}</span>
	                           </c:if>
	                        </c:if>
		                    <%-- 답변글이 아닌 원글인 경우 끝 --%>
                        
	                        <%-- 답변글인 경우 시작 --%> 
	                        <c:if test="${boardvo.depthno > 0}">
	                           <c:if test="${boardvo.commentCount > 0}">
	                             <span class="subject" onclick="goView('${boardvo.seq}')"><span style="color: red; font-style: italic; padding-left: ${boardvo.depthno * 20}px;">└Re&nbsp;</span>${boardvo.subject}<span style="vertical-align: super;">[<span style="color: red; font-size: 9pt; font-style: italic; font-weight: bold;">${boardvo.commentCount}</span>]</span></span> 
	                           </c:if>
	                           
	                           <c:if test="${boardvo.commentCount == 0}">
	                             <span class="subject" onclick="goView('${boardvo.seq}')"><span style="color: red; font-style: italic; padding-left: ${boardvo.depthno * 20}px;">└Re&nbsp;</span>${boardvo.subject}</span>
	                           </c:if>
	                        </c:if> 
	                        <%-- 답변글인 경우 끝 --%>
	                      </c:if> 
	                     <%-- 첨부파일이 없는 경우 끝 --%>
	                     
	                     <%-- 첨부파일이 있는 경우 시작 --%>
	                     <c:if test="${not empty boardvo.fileName}"> 
                       	 <%-- 답변글이 아닌 원글인 경우 시작 --%>  
                         <c:if test="${boardvo.depthno == 0}">
                           <c:if test="${boardvo.commentCount > 0}">
                             <span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}<span style="vertical-align: super;">[<span style="color: red; font-size: 9pt; font-style: italic; font-weight: bold;">${boardvo.commentCount}</span>]</span></span>&nbsp;<img src="<%= ctxPath%>/resources/images/disk.gif" />     
                           </c:if>
                           
                           <c:if test="${boardvo.commentCount == 0}">
                             <span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}</span>&nbsp;<img src="<%= ctxPath%>/resources/images/disk.gif" /> 
                           </c:if>
                         </c:if>
                         <%-- 답변글이 아닌 원글인 경우 끝 --%>
                        
                         <%-- 답변글인 경우 시작 --%> 
                         <c:if test="${boardvo.depthno > 0}">
                           <c:if test="${boardvo.commentCount > 0}">
                             <span class="subject" onclick="goView('${boardvo.seq}')"><span style="color: red; font-style: italic; padding-left: ${boardvo.depthno * 20}px;">└Re&nbsp;</span>${boardvo.subject}<span style="vertical-align: super;">[<span style="color: red; font-size: 9pt; font-style: italic; font-weight: bold;">${boardvo.commentCount}</span>]</span></span>&nbsp;<img src="<%= ctxPath%>/resources/images/disk.gif" />  
                           </c:if>
                           
                           <c:if test="${boardvo.commentCount == 0}">
                             <span class="subject" onclick="goView('${boardvo.seq}')"><span style="color: red; font-style: italic; padding-left: ${boardvo.depthno * 20}px;">└Re&nbsp;</span>${boardvo.subject}</span>&nbsp;<img src="<%= ctxPath%>/resources/images/disk.gif" /> 
                           </c:if>
                         </c:if> 
                         <%-- 답변글인 경우 끝 --%>
                       </c:if>   
                      <%-- 첨부파일이 있는 경우 끝 --%>
                     
                      <%-- === 댓글쓰기 및 답변형 및 파일첨부가 있는 게시판 끝 === --%> 
						</td>
						<td align="center">${boardvo.name}</td>
						<td align="center">${boardvo.regDate}</td>
						<td align="center">${boardvo.readCount}</td>
					</tr>
				</c:forEach>
				</c:if>
				
				<c:if test="${empty requestScope.boardList}">
					<tr>
						<td colspan="5" style="text-align: center;">데이터가 없습니다.</td>
					</tr>
				</c:if>
			</tbody>
		</table>
		
		
		<%-- === #130. 페이지바 보여주기 === --%>
		<div align="center" style="border: solid 0px gray; width: 80%; margin: 30px auto;">
			${requestScope.pageBar}
		</div>
		
		
		<%-- #109. 글 검색 폼 추가하기 : 1.글제목, 2.글내용, 3.글제목+글내용, 4.글쓴이 로 검색하도록 한다. --%>
		<form name="searchFrm" style="margin-top: 20px;">
			<select name="searchType" style="height: 26px;">
				<option value="subject">글제목</option>
				<option value="content">글내용</option>
				<option value="subject_content">글제목+글내용</option>
				<option value="name">글쓴이</option>
			</select>
			<input type="text" name="searchWord" size="40" autocomplete="off" /> 
      		<input type="text" style="display: none;"/> <%-- form 태그 내에 input 태그가 오로지 1개일 경우에는 엔터를 했을 경우 검색이 되므로 이것을 방지하고자 만든 것이다. --%> 
      		<button type="button" class="btn btn-secondary btn-sm" onclick="goSearch()">검색</button>
		</form>
		
		
		<%-- === #114. 검색어 입력 시 자동 글 완성하기 1 === --%>
		<div id="displayList" style="border:solid 1px gray; border-top:0px; height:100px; margin-left:13.2%; margin-top:-1px; margin-bottom:30px; overflow:auto;">
		</div>
		
	</div>
</div>


<%-- === #132. 페이징 처리된 후 특정 글제목을 클릭하여 상세내용을 본 이후
     //        사용자가 "검색된결과목록보기" 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
     //        현재 페이지 주소를 뷰단으로 넘겨준다.  --%>
<form name='goViewFrm'>
	<input type="hidden" name="seq" />
	<input type="hidden" name="goBackURL" />
	<input type="hidden" name="searchType" />
	<input type="hidden" name="searchWord" />
</form>     
     
     
