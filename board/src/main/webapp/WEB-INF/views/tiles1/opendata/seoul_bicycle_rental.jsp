<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	String ctxPath = request.getContextPath();
    //     /board     
%>    

<style type="text/css">
	
	div#title {
		font-size: 20pt;
	 /* border: solid 1px red; */
		padding: 12px 0;
	}
	
	div.mycontent {
  		width: 350px;
  		padding: 5px 3px;
  	}
  	
  	div.mycontent>.title {
  		font-size: 12pt;
  		font-weight: bold;
  		background-color: #d95050;
  		color: #fff;
  	}
  	
  	a {
  		text-decoration: none !important;
  	}
  	
</style>

<div align="center">
	 <div style="font-size: 20pt; margin-bottom: 20px;">서울시 따릉이대여소</div>
	 <div id="map" style="width:95%; height:600px;"></div>
</div>

<%-- 
   <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=발급받은 APP KEY(JavaScript 키)를 넣으시면 됩니다."></script> 
--%>
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=자기AppKey입력"></script>

 <script type="text/javascript">
    $(document).ready(function(){
    	
    	// 지도를 담을 영역의 DOM 레퍼런스
    	var mapContainer = document.getElementById("map");
    	
    	// 지도를 생성할때 필요한 기본 옵션
    	var options = {
        	 	center: new kakao.maps.LatLng(37.556513150417395, 126.91951995383943), // 지도의 중심좌표. 반드시 존재해야함.
        	 	<%--
        		  	center 에 할당할 값은 kakao.maps.LatLng 클래스를 사용하여 생성한다.
        		  	kakao.maps.LatLng 클래스의 2개 인자값은 첫번째 파라미터는 위도(latitude)이고, 두번째 파라미터는 경도(longitude)이다.
        		 --%>
        	 	level: 4  // 지도의 레벨(확대, 축소 정도). 숫자가 클수록 축소된다. 4가 적당함.
         };
    	
    	// 지도 생성 및 생성된 지도객체 리턴
    	var mapobj = new kakao.maps.Map(mapContainer, options);
    	
    	// 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성함. 	
    	var mapTypeControl = new kakao.maps.MapTypeControl();
    	
    	// 지도 타입 컨트롤을 지도에 표시함.
    	// kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미함.	
    	mapobj.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT); 
    	
    	// 지도 확대 축소를 제어할 수 있는 줌 컨트롤을 생성함.	
    	var zoomControl = new kakao.maps.ZoomControl();
    	
    	// 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 지도에 표시함.
    	// kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 RIGHT는 오른쪽을 의미함.	 
    	mapobj.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
    	
    	
    	// ============ 지도에 따릉이위치 마커 보여주기 시작 ============ //
    	// 따릉이 마커를 표시할 위치와 내용을 가지고 있는 객체 배열
    	var positionArr = [];
    	
    	$.ajax({
    		url:"<%= ctxPath%>/opendata/seoul_bicycle_rental_JSON.action",
    		async:false, // !!!!! 지도는 비동기 통신이 아닌 동기 통신으로 해야 한다.!!!!!!
    		dataType:"json",
    		success:function(json){
    			
    		    // console.log(JSON.stringify(json));
    			// JSON.stringify(json) 은 자바스크립트의 객체(배열)인 json 을 string 타입으로 변경시켜주는 것이다.
    			
    			$.each(json, function(index, item){
    				var position = {};
    				
    				position.content = "<div class='mycontent'>"+
    				                   "  <div class='title'><a href='https://map.kakao.com/link/to/"+item.statn_addr1+","+item.statn_lat+","+item.statn_lnt+"' style='color: #fff' target='_blank'>"+item.statn_addr1+"</a><br>"+item.statn_addr2+"</div>"+    
    				                   "</div>";
    				                   
    				position.latlng = new kakao.maps.LatLng(item.statn_lat, item.statn_lnt);
    				positionArr.push(position);
    			});// end of $.each(json, function(index, item){})---------------
    			
    		},
    		error: function(request, status, error){
    			alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
    	    }
    	});// end of $.ajax({})----------------------
    	
    	
    	// infowindowArr 은 인포윈도우를 가지고 있는 객체 배열의 용도이다. 
    	var infowindowArr = new Array();
    	
    	// === 객체 배열 만큼 마커 및 인포윈도우를 생성하여 지도위에 표시한다. === //
    	for(var i=0; i<positionArr.length; i++){
    		
    		// == 마커 생성하기 == //
			var marker = new kakao.maps.Marker({ 
				map: mapobj, 
		        position: positionArr[i].latlng   
			}); 
    		
			// 지도에 마커를 표시한다.
    		marker.setMap(mapobj);
			
    		// == 인포윈도우를 생성하기 == 
    		var infowindow = new kakao.maps.InfoWindow({
    			content: positionArr[i].content,
    			removable: true,
    			zIndex : i+1
    		});
    		
    		// 인포윈도우를 가지고 있는 객체배열에 넣기 
    		infowindowArr.push(infowindow);
    		
    		// == 마커 위에 인포윈도우를 표시하기 == //
    		// infowindow.open(mapobj, marker);
    		
    		// == 마커 위에 인포윈도우를 표시하기 == //
    		// 마커에 mouseover 이벤트와 mouseout 이벤트를 등록합니다
    	    // 이벤트 리스너로는 클로저(closure => 함수 내에서 함수를 정의하고 사용하도록 만든것)를 만들어 등록합니다 
    	    // for문에서 클로저(closure => 함수 내에서 함수를 정의하고 사용하도록 만든것)를 만들어 주지 않으면 마지막 마커에만 이벤트가 등록됩니다
    	    kakao.maps.event.addListener(marker, 'mouseover', makeOverListener(mapobj, marker, infowindow, infowindowArr)); 
    		
    	}// end of for----------------
    	// ============ 지도에 매장위치 마커 보여주기 끝 ============ //
    	
    });// end of $(document).ready(function(){})-----------------   
 
    
    // !! 인포윈도우를 표시하는 클로저(closure => 함수 내에서 함수를 정의하고 사용하도록 만든것)를 만드는 함수(카카오에서 제공해준것임)입니다 !! // 
    function makeOverListener(mapobj, marker, infowindow, infowindowArr) {
  	    return function() {
  	    	// alert("infowindow.getZIndex()-1:"+ (infowindow.getZIndex()-1));
  	    	
  	    	for(var i=0; i<infowindowArr.length; i++) {
  	    		if(i == infowindow.getZIndex()-1) {
  	    			infowindowArr[i].open(mapobj, marker);
  	    		}
  	    		else{
  	    			infowindowArr[i].close();
  	    		}
  	    	}
  	    };
  	}// end of function makeOverListener(mapobj, marker, infowindow, infowindowArr)--------
    
 </script> 
 
 
 
 
  






