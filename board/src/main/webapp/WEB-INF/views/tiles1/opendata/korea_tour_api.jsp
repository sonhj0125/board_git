<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	String ctxPath = request.getContextPath();
    //     /board     
%>

<style type="text/css">

/* 360px 이하 */  /* 일반적으로 휴대폰 세로 */
@media screen and (max-width: 360px){
	div#search {
		padding-left: 0 !important;
	}
	
	div#nav button {
		width: 100%;
	}	
}

/* 361px ~ 767px 이하 */  /* 일반적으로 휴대폰 가로 */
@media screen and (min-width: 361px) and (max-width: 767px){
	div#search {
		padding-left: 0 !important;
	}
	
	div#nav button {
		width: 100%;
	}
}

</style>

<script type="text/javascript">

   $(document).ready(function(){
	   
	   if($("select#choice").val() == "keyword") {
		   $("input:text[name='searchword']").val("서울");
		   img_display("서울", 1);
	   }
	   
	   $(document).on("keyup", "input:text[name='searchword']", function(e){
		   if(e.keyCode == 13){
			   goSearch();
		   }
	   });
	   
	   
	   $(document).on("click", "button#btn_first", function(){
		   img_display($("input:text[name='searchword']").val(), 1);
	   });
	   
	   $(document).on("click", "button#btn_prev", function(e){
		   img_display($("input:text[name='searchword']").val(), $(e.target).val());
		// alert($("input:text[name='searchword']").val() +" , "+ $(e.target).val());   
	   });
	   
	   $(document).on("click", "button#btn_next", function(e){
		   img_display($("input:text[name='searchword']").val(), $(e.target).val());
		// alert($("input:text[name='searchword']").val() +" , "+ $(e.target).val()); 
	   });
	   
	   $(document).on("click", "button#btn_last", function(e){
		   img_display($("input:text[name='searchword']").val(), $(e.target).val());
		// alert($("input:text[name='searchword']").val() +" , "+ $(e.target).val()); 
	   });
	   
   });// end of $(document).ready(function(){})-----------

   
   function img_display(searchword, pageNo) {
	   
	   if(Number(pageNo) >= 1) {
		  
		   const encodeVal = encodeURI(searchword); //  encodeURI("서울");        encodeURI("한라산");               
           // encodeURI("문자열"); ==> "문자열"을 웹상에서 컴퓨터가 알아듣는 문자로 변환시켜준다. 
           // console.log(encodeVal); //                %EC%84%9C%EC%9A%B8       %ED%95%9C%EB%9D%BC%EC%82%B0        
   		
           const decodeVal = decodeURI(encodeVal); 
	        // decodeURI(encodeVal); ==> 웹상에서 컴퓨터가 알아듣는 문자를 사람이 알아볼수 있는 문자로 변환시켜준다.
            // console.log(decodeVal); //                서울                                         한라산   
		   
           let url_address = "";
           
           const choice = $("select#choice").val();
           if(choice == "keyword"){
        	   url_address = "http://apis.data.go.kr/B551011/PhotoGalleryService1/gallerySearchList1?serviceKey=sGD3L5Oc%2BdocgnqeQCPd%2BAc3ZVQ0MlXpc5IXGuLyOZLXDRww4NhSSGO6BKlZ1NyyZfZmCYTBlWErmOxVtuUyzg%3D%3D&MobileOS=ETC&MobileApp=AppTest&_type=json&keyword="+encodeVal+"&numOfRows=20&pageNo="+pageNo;  
           }
           else if(choice == "title"){
        	   url_address = "http://apis.data.go.kr/B551011/PhotoGalleryService1/galleryDetailList1?serviceKey=sGD3L5Oc%2BdocgnqeQCPd%2BAc3ZVQ0MlXpc5IXGuLyOZLXDRww4NhSSGO6BKlZ1NyyZfZmCYTBlWErmOxVtuUyzg%3D%3D&MobileOS=ETC&MobileApp=AppTest&_type=json&title="+encodeVal+"&numOfRows=20&pageNo="+pageNo;    
           }
            
           $.ajax({
        	   url:url_address,
        	   dataType:"json",
        	   success:function(json){
        		// console.log(JSON.stringify(json));
        		// console.log(JSON.stringify(json.response.body.items.item));
        		// console.log(JSON.stringify(json.response.body.totalCount)); 
        		
        		   const totalCount = json.response.body.totalCount; 
        		   const totalPageCount = Math.ceil(totalCount/20); // 총페이지수 
        		// alert(totalPageCount);
        		   
        		   if(pageNo > totalPageCount) {
        			   alert("사진이 없습니다.");
        			   return;
        		   }
        		   
        		   const data_arr = json.response.body.items.item;
        		   
        		   const img_arr = [];
        		   
        		   data_arr.forEach(function(item, index, arr){
        			   
        			   img_arr.push({"galWebImageUrl":item.galWebImageUrl, "galTitle":item.galTitle});
        			   
        		   });// end of data_arr.forEach(function(item, index, arr)--------------
        				   
        		// console.log(img_arr);
        		   
        		   let v_html = ``;
        		   
        		   img_arr.forEach(function(item, index, arr){
        			   
        			   v_html += `<div class="col-md-3 mb-4">
        			                 <img src="\${item.galWebImageUrl}" class="img-fluid" />
        			                 <p>\${item.galTitle}</p>
        			              </div>`;
        			   
        		   });
        		   
        		   $("div#result").html(v_html);
        		   
        		   //////////////////////////////////////////////////////////////////
        		   
        		   let v_html2 = `<div class="col-sm-3 col-md-5 text-right mb-4"><button id="btn_first" type="button" class="btn btn-danger"  value="1">맨처음</button></div>
		                          <div class="col-sm-3 col-md-1 text-right mb-4"><button id="btn_prev"  type="button" class="btn btn-primary" value="\${Number(pageNo)-1}">이전</button></div>
		                          <div class="col-sm-3 col-md-1 text-left  mb-4"><button id="btn_next"  type="button" class="btn btn-primary" value="\${Number(pageNo)+1}">다음</button></div>
		                          <div class="col-sm-3 col-md-5 text-left  mb-4"><button id="btn_last"  type="button" class="btn btn-danger"  value="\${totalPageCount}">마지막</button></div>`;
        		   
		           $("div#nav").html(v_html2);                
        		   
        	   },
        	   error: function(request, status, error){
    			  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
    	       }
           }); 
            
	   }
	   
	   else {
			alert("사진이 없습니다.");   
	   }
	   
   }// end of function img_display(searchword, pageNo)-----------
   
   
   function goSearch(){
	   
	   const searchword = $("input:text[name='searchword']").val().trim(); 
	   
	   if(searchword == ""){
		   alert("검색어를 입력해주세요!!");
		   return;
	   }
	   else{
		   img_display(searchword, 1);
	   }
	   
   }// end of function goSearch()-------------------
   
</script>


<div>
 	<div class="row mb-3 pl-5" id="search">
 		<div class="col-md-12">
 		   <p class="h3">관광명소 사진 찾아보기</p>
 		</div>
 		
 		<div class="col-md-2">
 		   <div class=" form-group">
	 		   <label>검색조건선택</label>
	 		   <select class="form-control" id="choice">
	 		      <option value="keyword">키워드</option>
	 		      <option value="title">타이틀</option>
	 		   </select>
 		   </div>
 		</div>
 		
 		<div class="col-md-2">
 		   <div class=" form-group">
 		   	   <label>검색어 입력</label>
 		   	   <input type="text" class="form-control" name="searchword" value="" />
 		   </div>
 		</div> 
 		
 		<div class="col-md-8" style="display: flex; border: solid 0px red; margin-top: 15px;">
 		    <div style="margin: auto 0;">
 		    	<button type="button" class="btn btn-success" onclick="javascript:goSearch()">찾아보기</button>
 		    </div>
 		</div>	   
 	</div>
 	
 	<div class="row" id="result"></div>
 	
 	<div class="row" id="nav"></div>
 </div>




    
    