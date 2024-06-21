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
		
		// goReadComment()		// 페이지 처리 안한 댓글 읽어오기
		
		// === #144. Ajax 로 불러온 댓글 내용들을 페이징 처리하기 === //
		goViewComment(1);	// 페이징 처리 한 댓글 읽어오기
		
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
		
		
		
		
		// ===== 댓글 수정 ===== // 
		let origin_comment_content = "";
		
		$(document).on("click", "button.btnUpdateComment", function(e){
		
			const $btn = $(e.target);
			
			if($(e.target).text() == "수정") {
		
				// alert("댓글수정");
				// alert($(e.target).parent().parent().children("td:nth-child(2)").text()); // 수정 전 댓글내용
				
				const $content = $(e.target).parent().parent().children("td:nth-child(2)");	// $content => 선택자를 의미
				origin_comment_content = $(e.target).parent().parent().children("td:nth-child(2)").text();
				
				$content.html(`<input id='comment_update' type='text' value='\${origin_comment_content}' size='40'	/>`);	// 댓글 내용을 수정할 수 있도록 input 태그를 만들어준다.
				
				
				$(e.target).text("완료").removeClass("btn-secondary").addClass("btn-info");
				$(e.target).next().next().text("취소").removeClass("btn-secondary").addClass("btn-danger");
				
				$(document).on("keydown", "input#comment_update", function(e){
					
					if(e.keyCode == 13) {
						// alert("엔터!");
						// alert($btn.text());	// 완료
						$btn.click();
						
					} // end of if(e.keyCode == 13)
					
				}); // end of $(document).on("keydown", "input#comment_update", function(e){})
				
			}
			else if($(e.target).text() == "완료") {
				
				// alert("댓글수정완료");
				// alert($(e.target).next().val()); // 수정해야 할 댓글 시퀀스 번호
				// alert($(e.target).parent().parent().children("td:nth-child(2)").children("input").val()); // 수정 후 댓글 내용
				
				const content = $(e.target).parent().parent().children("td:nth-child(2)").children("input").val();
				
				$.ajax({
					
					url:"${pageContext.request.contextPath}/updateComment.action",
					type:"post",
					data:{"seq":$(e.target).next().val()
						, "content":content},
					dataType:"json",
					success:function(json){
						
						// $(e.target).parent().parent().children("td:nth-child(2)").html(content);
						
						// goReadComment();	// 페이징 처리 안한 댓글 읽어오기
						goViewComment(1);	// 페이징 처리 한 댓글 읽어오기
						
						$(e.target).text("수정").removeClass("btn-info").addClass("btn-secondary");
						$(e.target).next().next().text("삭제").removeClass("btn-danger").addClass("btn-secondary");
						
					},
					error: function(request, status, error){
				        alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
				    }
					
				}); // end of $.ajax
				
			} // end of ($(e.target).text() == "수정")
			
		}); // end of $(document).on("click", "button.btnUpdateComment", function(e){})

		
		
		
		
	    // ===== 댓글 수정취소 / 댓글삭제  ===== //
	    $(document).on('click', "button.btnDeleteComment", function(e){
	    	
	         if($(e.target).text() == "취소") {
	            //  alert("댓글 수정 취소 누름!");
	            //  alert($(e.target).parent().parent().children("td:nth-child(2)").html());
	            
	            const $content = $(e.target).parent().parent().children("td:nth-child(2)");
	            $content.html(`\${origin_comment_content}`);
	            
	            $(e.target).text("삭제").removeClass("btn-danger").addClass("btn-secondary");
	               $(e.target).prev().prev().text("수정").removeClass("btn-info").addClass("btn-secondary");
	            
	            
	            
	         }
	         else if($(e.target).text() == "삭제") { // 댓글을 삭제하면 comment
	            //   alert("댓글삭제");
	            //   alert($(e.target).prev().val()); // 삭제해야할 시퀀스 번호
	            
	            if(confirm("정말로 삭제하시겠습니까?")) {
	               
	               $.ajax({
	                  url:"${pageContext.request.contextPath}/deleteComment.action",
	                  type:"post",
	                  data:{"seq":$(e.target).prev().val()
	                      ,"parentSeq":"${requestScope.boardvo.seq}"},
	                  dataType:"json",
	                  success:function(json){
	                     // goReadComment();	// 페이징 처리 안 한 댓글 읽어오기(delete 하면 select 해와야함 그래서 이 함수 호출)
	                     goViewComment(1);		// 페이징 처리 한 댓글 읽어오기
	                  },
	                  error: function(request, status, error){
	                        alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
	                  }
	                  
	               }); // end of $.ajax
	               
	            } // end of if(confirm)
	            	
	         } // end of if($(e.target).text() == "취소")
	         
	      }); // end of $(document).on('click', "button.btnDeleteComment", function(e){})
		
		
		
		
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
					// goReadComment();		// 페이지 처리 안한 댓글 읽어오기
					goViewComment(1);		// 페이지 처리 한 댓글 읽어오기	
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
						
						if( ${sessionScope.loginuser != null} && "${sessionScope.loginuser.userid}" == item.fk_userid ) {
							
							v_html += "<td class='comment'><button class='btn btn-secondary btn-sm btnUpdateComment'>수정</button><input type='hidden' value='"+item.seq+"'/>&nbsp;<button class='btn btn-secondary btn-sm btnDeleteComment'>삭제</button></td>";
						
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
	
	
	
	
	
	<%-- === #145. Ajax 로 불러온 댓글 내용들을 페이징 처리하기 ===  --%>
	
	function goViewComment(currentShowPageNo){
		
		$.ajax({
			
			url:"<%=ctxPath%>/commentList.action",
			data:{"parentSeq":"${requestScope.boardvo.seq}",
				  "currentShowPageNo":currentShowPageNo},
			dataType:"json",
			success:function(json){
				
				// console.log(JSON.stringify(json));
				// [{"name":"손혜정","regdate":"2024-06-21 12:17:58","fk_userid":"ejss0125","seq":"51","content":"열두번째 댓글입니다."},{"name":"손혜정","regdate":"2024-06-21 12:17:55","fk_userid":"ejss0125","seq":"50","content":"열한번째 댓글입니다."},{"name":"손혜정","regdate":"2024-06-21 12:17:51","fk_userid":"ejss0125","seq":"49","content":"열번째 댓글입니다."},{"name":"손혜정","regdate":"2024-06-21 12:17:48","fk_userid":"ejss0125","seq":"48","content":"아홉번째 댓글입니다."},{"name":"손혜정","regdate":"2024-06-21 12:17:42","fk_userid":"ejss0125","seq":"47","content":"여덟번째 댓글입니다."}]
				
				let v_html = "";
				
				if(json.length > 0){
					
					$.each(json, function(index, item){
						
						v_html += "<tr>";
						v_html += 	"<td class='comment'>"+(item.totalCount - (currentShowPageNo - 1) * item.sizePerPage - index)+"</td>";
						
						<%--  
							>>>	페이징 처리시 보여주는 순번 공식 <<<
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
					
			                
		                v_html += 	"<td>"+item.content+"</td>";
						v_html += 	"<td class='comment'>"+item.name+"</td>";
						v_html += 	"<td class='comment'>"+item.regdate+"</td>";
						
						if( ${sessionScope.loginuser != null} && "${sessionScope.loginuser.userid}" == item.fk_userid ) {
							
							v_html += "<td class='comment'><button class='btn btn-secondary btn-sm btnUpdateComment'>수정</button><input type='hidden' value='"+item.seq+"'/>&nbsp;<button class='btn btn-secondary btn-sm btnDeleteComment'>삭제</button></td>";
						
						}
						
						v_html += "</tr>";
						
					}); // end of $.each 
			                
				}
				else {
					
					v_html += "<tr>";
					v_html += 	"<td colspan='5' class='comment'>댓글이 없습니다.</td>";
					v_html += "</tr>";
				}
				
				$("tbody#commentDisplay").html(v_html);
				
				// === # 154. 페이지바 함수 호출 === // 
				const totalPage = Math.ceil(json[0].totalCount/json[0].sizePerPage);	// Math.floor은 작은 값으로 내리기, Math.ceil은 큰 값으로 올리기
			
				// console.log("totalPage : ", totalPage);
				// totalPage : 3
				
				makeCommentPageBar(currentShowPageNo, totalPage);
				
			},
			error: function(request, status, error){
		        alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
		    }
			
		}); // end of $.ajax
		
	} // end of function goViewComment
	
	
	
	
	   // === #153. 페이지바 함수 만들기 ===
	   function makeCommentPageBar(currentShowPageNo, totalPage) {
	      
	      const blockSize = 10;
	      // blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수이다.
	      /*
	                      1  2  3  4  5  6  7  8  9 10 [다음][마지막]  -- 1개블럭
	         [맨처음][이전]  11 12 13 14 15 16 17 18 19 20 [다음][마지막]  -- 1개블럭
	         [맨처음][이전]  21 22 23
	      */
	      
	      let loop = 1;
	      /*
	          loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
	       */
	      
	      let pageNo = Math.floor((currentShowPageNo - 1)/blockSize) * blockSize + 1;
	      // *** !! 공식이다. !! *** //
	      /*
	         currentShowPageNo 가 3페이지 라면 pageNo 는 1 이 되어야 한다.
	            ((3 - 1)/10) * 10 + 1;
	            ( 2/10 ) * 10 + 1;
	            ( 0.2 ) * 10 + 1;
	            Math.floor( 0.2 ) * 10 + 1;  // 소수부가 있을시 Math.floor(0.2) 은 0.2 보다 작은 최대의 정수인 0을 나타낸다.
	            0 * 10 + 1 
	            1
	            
	            currentShowPageNo 가 11페이지 이라면 pageNo 는 11 이 되어야 한다.
	            ((11 - 1)/10) * 10 + 1;
	            ( 10/10 ) * 10 + 1;
	            ( 1 ) * 10 + 1;
	            Math.floor( 1 ) * 10 + 1;  // 소수부가 없을시 Math.floor(1) 은 그대로 1 이다.
	            1 * 10 + 1
	            11
	            
	            currentShowPageNo 가 20페이지 이라면 pageNo 는 11 이 되어야 한다.
	            ((20 - 1)/10) * 10 + 1;
	            ( 19/10 ) * 10 + 1;
	            ( 1.9 ) * 10 + 1;
	            Math.floor( 1.9 ) * 10 + 1;  // 소수부가 있을시 Math.floor(1.9) 은 1.9 보다 작은 최대의 정수인 1을 나타낸다.
	            1 * 10 + 1
	            11
	         
	            
	            1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
	            11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
	            21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
	            
	            currentShowPageNo         pageNo
	           ----------------------------------
	                 1                      1 = Math.floor((1 - 1)/10) * 10 + 1
	                 2                      1 = Math.floor((2 - 1)/10) * 10 + 1
	                 3                      1 = Math.floor((3 - 1)/10) * 10 + 1
	                 4                      1
	                 5                      1
	                 6                      1
	                 7                      1 
	                 8                      1
	                 9                      1
	                 10                     1 = Math.floor((10 - 1)/10) * 10 + 1
	                
	                 11                    11 = Math.floor((11 - 1)/10) * 10 + 1
	                 12                    11 = Math.floor((12 - 1)/10) * 10 + 1
	                 13                    11 = Math.floor((13 - 1)/10) * 10 + 1
	                 14                    11
	                 15                    11
	                 16                    11
	                 17                    11
	                 18                    11 
	                 19                    11 
	                 20                    11 = Math.floor((20 - 1)/10) * 10 + 1
	                 
	                 21                    21 = Math.floor((21 - 1)/10) * 10 + 1
	                 22                    21 = Math.floor((22 - 1)/10) * 10 + 1
	                 23                    21 = Math.floor((23 - 1)/10) * 10 + 1
	                 ..                    ..
	                 29                    21
	                 30                    21 = Math.floor((30 - 1)/10) * 10 + 1
	      
	      
	          1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
	          11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
	          21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
	          
	          currentShowPageNo         pageNo
	         ----------------------------------
	               1                      1 = ((1 - 1)/10) * 10 + 1
	               2                      1 = ((2 - 1)/10) * 10 + 1
	               3                      1 = ((3 - 1)/10) * 10 + 1
	               4                      1
	               5                      1
	               6                      1
	               7                      1 
	               8                      1
	               9                      1
	               10                     1 = ((10 - 1)/10) * 10 + 1
	              
	               11                    11 = ((11 - 1)/10) * 10 + 1
	               12                    11 = ((12 - 1)/10) * 10 + 1
	               13                    11 = ((13 - 1)/10) * 10 + 1
	               14                    11
	               15                    11
	               16                    11
	               17                    11
	               18                    11 
	               19                    11 
	               20                    11 = ((20 - 1)/10) * 10 + 1
	               
	               21                    21 = ((21 - 1)/10) * 10 + 1
	               22                    21 = ((22 - 1)/10) * 10 + 1
	               23                    21 = ((23 - 1)/10) * 10 + 1
	               ..                    ..
	               29                    21
	               30                    21 = ((30 - 1)/10) * 10 + 1
	      */
	      
	      let pageBar_HTML = "<ul style='list-style:none;'>";
	      
	      // === [맨처음][이전] 만들기 === //
	      if(pageNo != 1) {
	         pageBar_HTML += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='javascript:goViewComment(1)'>[맨처음]</a></li>";
	         pageBar_HTML += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='javascript:goViewComment("+ (pageNo-1) + ")'>[이전]</a></li>"; 
	      }
	      
	      while( !(loop > blockSize || pageNo > totalPage) ) {
	         
	         if(pageNo == currentShowPageNo) {
	            pageBar_HTML += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
	         }
	         else {
	            pageBar_HTML += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='javascript:goViewComment(" + pageNo + ")'>"+pageNo+"</a></li>"; 
	         }
	         
	         loop++;
	         pageNo++;
	         
	      }// end of while------------------------
	      
	      // === [다음][마지막] 만들기 === //
	      if(pageNo <= totalPage) {
	         pageBar_HTML += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='javascript:goViewComment("+ pageNo + ")'>[다음]</a></li>";
	         pageBar_HTML += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='javascript:goViewComment("+ totalPage + ")'>[마지막]</a></li>"; 
	      }
	      
	      pageBar_HTML += "</ul>";
	      
	      // === #156. 댓글 페이지바 출력하기 ===
	      $("div#pageBar").html(pageBar_HTML);
	      
	   } // end of function makeCommentPageBar(currentShowPageNo) --------------------
	
	
	
	
	
	
	<%-- === #139. 이전글제목, 다음글제목 보기 --%>
	function goView(seq)	{
	
		const goBackURL = "${requestScope.goBackURL}";
		// goBackURL : "/list.action?searchType=subject&searchWord=정화&currentShowPageNo=3"

	<%--			
		// 아래처럼 get 방식으로 보내면 안된다. 왜냐하면 get방식에서 &는 전송될 데이터의 구분자로 사용되기 때문이다. 
		location.href = `<%=ctxPath%>/view.action?seq=\${seq}&goBackURL=\${goBackURL}`;
	--%>
		
	<%-- 	
		그러므로 & 를 글자 그대로 인식하는 post 방식으로 보내야 한다.
		아래에 #132 에 표기된 form 태그를 먼저 만든다. --%>
		const frm = document.goViewFrm;
		frm.seq.value = seq;
		frm.goBackURL.value = goBackURL;
		
		if(${not empty requestScope.paraMap}) {	// 검색조건이 있을 경우
			
			frm.searchType.value = "${requestScope.paraMap.searchType}";
			frm.searchWord.value = "${requestScope.paraMap.searchWord}";
			
		}
		
		frm.method = "post";
		frm.action = "<%=ctxPath%>/view_2.action";
		frm.submit();
		
			
	} // end of function goView(seq)
	
	
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
	   	<%-- 
	   		<div style="margin-bottom: 1%;">이전글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='view_2.action?seq=${requestScope.boardvo.previousseq}'">${requestScope.boardvo.previoussubject}</span></div>
	   		<div style="margin-bottom: 1%;">다음글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='view_2.action?seq=${requestScope.boardvo.nextseq}'">${requestScope.boardvo.nextsubject}">${requestScope.boardvo.nextsubject}</span></div>
	   	--%>
	   	
		   	<%-- === #137. 이전글제목, 다음글제목 보기  --%>
		   	<%--  // >>> 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  시작    <<< // --%>
		   	<div style="margin-bottom: 1%;">이전글제목&nbsp;&nbsp;<span class="move" onclick="goView(${requestScope.boardvo.previousseq})">${requestScope.boardvo.previoussubject}</span></div>
		   	<div style="margin-bottom: 1%;">다음글제목&nbsp;&nbsp;<span class="move" onclick="goView(${requestScope.boardvo.nextseq})">${requestScope.boardvo.nextsubject}</span></div>
		   	<%--  // >>> 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  끝    <<< // --%>
	   	
	   		
	   		<br>
	   		<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>/list.action'">전체목록보기</button>
	   		
	   		<%-- === #135. 특정글을 조회한 후 "검색된결과목록보기" 버튼을 클릭했을 때 돌아갈 페이지를 만들기 위함. --%>
	   		<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>${requestScope.goBackURL}'">검색된결과목록보기</button>
	   		
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
	            		<th style="width:6%">순번</th>
	            		<th style="text-align: center;">내용</th>
	            		<th style="width: 8%; text-align: center;">작성자</th>
	            		<th style="width: 12%; text-align: center;">작성일자</th>
	            		<th style="width: 12%; text-align: center;">수정/삭제</th>
	         		</tr>
	         	</thead>
         		<tbody id="commentDisplay"></tbody>
	      	</table>
	   		
	   		<%-- === #155. 댓글페이지바가 보여지는 곳 === --%>
	   		<div style="display: flex; margin-bottom: 50px;">
          		<div id="pageBar" style="margin: auto; text-align: center;"></div>
       		</div>
	   		
	   		
	   	</div>
	   	
   	</div>
</div>
   	
   	
<%-- === #138. 	페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
         	          사용자가 "검색된결과목록보기" 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
          		현재 페이지 주소를 뷰단으로 넘겨준다.  --%>
<form name="goViewFrm">
	<input type="hidden" name="seq" />
	<input type="hidden" name="goBackURL" />
	<input type="hidden" name="searchType" />
	<input type="hidden" name="searchWord" />
</form>       	
   	
   	
   	
   	
   	
   	