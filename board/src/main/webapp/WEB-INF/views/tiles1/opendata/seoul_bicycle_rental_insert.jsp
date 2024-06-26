<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
   String ctxPath = request.getContextPath();
    //     /board    
%>

<style type="text/css">

.highcharts-figure,
.highcharts-data-table table {
    min-width: 320px;
   /* max-width: 800px; */
    margin: 1em auto;
}

.highcharts-data-table table {
    font-family: Verdana, sans-serif;
    border-collapse: collapse;
    border: 1px solid #ebebeb;
    margin: 10px auto;
    text-align: center;
    width: 100%;
    max-width: 500px;
}

.highcharts-data-table caption {
    padding: 1em 0;
    font-size: 1.2em;
    color: #555;
}

.highcharts-data-table th {
    font-weight: 600;
    padding: 0.5em;
}

.highcharts-data-table td,
.highcharts-data-table th,
.highcharts-data-table caption {
    padding: 0.5em;
}

.highcharts-data-table thead tr,
.highcharts-data-table tr:nth-child(even) {
    background: #f8f8f8;
}

.highcharts-data-table tr:hover {
    background: #f1f7ff;
}

input[type="number"] {
    min-width: 50px;
}



div#bar_chart_container {
    height: 400px;
}

.highcharts-figure,
.highcharts-data-table table {
    min-width: 310px;
   /* max-width: 800px;*/
    margin: 1em auto;
}

.highcharts-data-table table {
    font-family: Verdana, sans-serif;
    border-collapse: collapse;
    border: 1px solid #ebebeb;
    margin: 10px auto;
    text-align: center;
    width: 100%;
    max-width: 500px;
}

.highcharts-data-table caption {
    padding: 1em 0;
    font-size: 1.2em;
    color: #555;
}

.highcharts-data-table th {
    font-weight: 600;
    padding: 0.5em;
}

.highcharts-data-table td,
.highcharts-data-table th,
.highcharts-data-table caption {
    padding: 0.5em;
}

.highcharts-data-table thead tr,
.highcharts-data-table tr:nth-child(even) {
    background: #f8f8f8;
}

.highcharts-data-table tr:hover {
    background: #f1f7ff;
}


</style>

<script src="<%= ctxPath%>/resources/Highcharts-10.3.1/code/highcharts.js"></script>
<script src="<%= ctxPath%>/resources/Highcharts-10.3.1/code/modules/exporting.js"></script>
<script src="<%= ctxPath%>/resources/Highcharts-10.3.1/code/modules/export-data.js"></script>
<script src="<%= ctxPath%>/resources/Highcharts-10.3.1/code/modules/accessibility.js"></script>


<script type="text/javascript">
   
   $(document).ready(function(){
      
      let is_no_data = false;   // DB에 데이터가 또 들어가는 것을 방지
      
      $.ajax({
         
         url:"<%= ctxPath%>/opendata/seoul_bicycle_rental_select.action",
           async:false, 
           dataType:"json",
           success:function(json){
              
              if(json.length == 0){
                 is_no_data = true;
              }
              else {   // 오라클 테이블에 따릉이 데이터가 입력된 경우
                 // console.log(JSON.stringify(json));
                 // [{"CNT":"245","PERCNTAGE":"7.6","GU":"송파구"},{"CNT":"223","PERCNTAGE":"6.9","GU":"강서구"},{"CNT":"195","PERCNTAGE":"6.0","GU":"강남구"},{"CNT":"186","PERCNTAGE":"5.7","GU":"영등포구"},{"CNT":"175","PERCNTAGE":"5.4","GU":"노원구"},{"CNT":"162","PERCNTAGE":"5.0","GU":"서초구"},{"CNT":"142","PERCNTAGE":"4.4","GU":"마포구"},{"CNT":"134","PERCNTAGE":"4.1","GU":"구로구"},{"CNT":"132","PERCNTAGE":"4.1","GU":"강동구"},{"CNT":"131","PERCNTAGE":"4.0","GU":"양천구"},{"CNT":"124","PERCNTAGE":"3.8","GU":"종로구"},{"CNT":"115","PERCNTAGE":"3.5","GU":"성동구"},{"CNT":"112","PERCNTAGE":"3.5","GU":"중구"},{"CNT":"110","PERCNTAGE":"3.4","GU":"은평구"},{"CNT":"106","PERCNTAGE":"3.3","GU":"성북구"},{"CNT":"104","PERCNTAGE":"3.2","GU":"중랑구"},{"CNT":"103","PERCNTAGE":"3.2","GU":"관악구"},{"CNT":"103","PERCNTAGE":"3.2","GU":"도봉구"},{"CNT":"102","PERCNTAGE":"3.1","GU":"용산구"},{"CNT":"102","PERCNTAGE":"3.1","GU":"광진구"},{"CNT":"101","PERCNTAGE":"3.1","GU":"동대문구"},{"CNT":"94","PERCNTAGE":"2.9","GU":"서대문구"},{"CNT":"88","PERCNTAGE":"2.7","GU":"동작구"},{"CNT":"81","PERCNTAGE":"2.5","GU":"금천구"},{"CNT":"70","PERCNTAGE":"2.2","GU":"강북구"}]
              
                 const pie_data_arr = [];
                 const bar_data_arr = [];
                 
                 let v_html = `<table class="table table-bordered table-striped table-sm">
                                 <thead class="thead-dark text-center">
                                   <tr>
                                     <th>지역구명</th>
                                     <th>따릉이대여소개수</th>
                                     <th>퍼센티지</th>
                                   </tr>
                                 </thead>
                                 <tbody>
                               `;
              
                 $.each(json, function(index, item) {
                    
                    v_html += `<tr>
                                 <td>\${item.GU}</td>
                                 <td align='right'>\${item.CNT}</td>
                                 <td align='right'>\${item.PERCNTAGE}</td>
                             </tr>`;
                             
                             
                    ////////////////////////////////////////////////////////////
                    
                 	const pie_data = {};
                 	
                 	if(index == 0){
                 		pie_data.sliced = true;
                 		pie_data.selected = true;
                 	}
                 	pie_data.name = item.GU;
                 	pie_data.y = Number(item.PERCNTAGE);
                             
                    pie_data_arr.push(pie_data);
                    
                    ////////////////////////////////////////////////////////////
                    
                    const bar_data = [];
                    // bar_data.push(item.GU);
                   	bar_data.push("<span class='gu' style='font-weight:bold; cursor:pointer;'>"+item.GU+"</span>");
                    bar_data.push(Number(item.CNT));
                    
                    bar_data_arr.push(bar_data);
                    
                 }); // end of $.each(json, function(index, item))
                 
                 v_html += `</tbody>
                             </table>`;
                             
                 $("div#tbl").html(v_html); 
                 
                 //////////////////////////////////////////////////////////////////////////
                 
                 <%-- === 파이 차트 시작 === --%>
                 Highcharts.chart('pie_chart_container', {
                       chart: {
                           plotBackgroundColor: null,
                           plotBorderWidth: null,
                           plotShadow: false,
                           type: 'pie'
                       },
                       title: {
                           text: '2024년 서울특별시 따릉이 대여소 지역구별 배포현황'
                       },
                       tooltip: {
                           pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                       },
                       accessibility: {
                           point: {
                               valueSuffix: '%'
                           }
                       },
                       plotOptions: {
                           pie: {
                               allowPointSelect: true,
                               cursor: 'pointer',
                               dataLabels: {
                                   enabled: true,
                                   format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                               }
                           }
                       },
                       series: [{
                           name: '배포점유율',
                           colorByPoint: true,
                           data: pie_data_arr 	// <=== 만들어야 하는 것 
                       }]
                 });
                 <%-- === 파이 차트 끝 === --%>
                 
                 //////////////////////////////////////////////////////////////////////////
                 
                 <%-- === 막대 차트 시작 === --%>
                 
                 Highcharts.chart('bar_chart_container', {
                	    chart: {
                	        type: 'column'
                	    },
                	    title: {
                	        text: '2024년 서울특별시 따릉이 대여소 지역구별 배포현황(구명을 클릭해보세요.)'
                	    },
                	    subtitle: {
                	        text: 'Source: <a href="https://data.seoul.go.kr/dataList/OA-21235/S/1/datasetView.do" target="_blank">서울시 따릉이 대여소 마스터정보</a>'
                	    },
                	    xAxis: {
                	        type: 'category',
                	        labels: {
                	            rotation: -45,
                	            style: {
                	                fontSize: '13px',
                	                fontFamily: 'Verdana, sans-serif'
                	            }
                	        }
                	    },
                	    yAxis: {
                	        min: 0,
                	        title: {
                	            text: 'Population (millions)'
                	        }
                	    },
                	    legend: {
                	        enabled: false
                	    },
                	    tooltip: {
                	        // pointFormat: 'Population in 2021: <b>{point.y:.1f} millions</b>'
                	        pointFormat: '2024년 대여소 개수: <b>{point.y} 개</b>'
                	    },
                	    series: [{
                	        name: 'Population',
                	        data: bar_data_arr,		// <=== 만들어야 하는 것 
                	        dataLabels: {
                	            enabled: true,
                	            rotation: -90,
                	            color: '#FFFFFF',
                	            align: 'right',
                	            format: '{point.y:.1f}', // one decimal
                	            y: 10, // 10 pixels down from the top
                	            style: {
                	                fontSize: '13px',
                	                fontFamily: 'Verdana, sans-serif'
                	            }
                	        }
                	    }]
                	});
                 
                 <%-- === 막대 차트 끝 === --%>
                 
                 //////////////////////////////////////////////////////////////////////////
                 
                 // ==== 막대차트에서 구이름을 클릭했을 경우 이벤트 발생시키기 ==== //
                 $(document).on("click", "tspan.gu", function(e){
                	 // alert("헤헤헤");
                	 
                	 const text = $(e.target).text();
                	 alert(`seoul_bicycle_rental 테이블에서 "\${text}"에 속한 주소지 정보를 Ajax 로 알아와서 뿌려주자.`);
                	 
                 });
                 
                 
                 
                 
                 
              
              } // end of if(json.length == 0)
              
           },
           error: function(request, status, error){
                alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
            }
         
      }); // end of $.ajax
      
      
      
      if(is_no_data == true){   // 데이터 값이 없다면
         
         $.ajax({
            
            url:"<%=ctxPath%>/opendata/seoul_bicycle_rental_JSON.action",
            async:false,   // 동기방식(하나하나 불러와야하기때문에)
            dataType:"json",
            success:function(json){
               // console.log(JSON.stringify(json));
                   // JSON.stringify(json) 은 자바스크립트의 객체(배열)인 json 을 string 타입으로 변경시켜주는 것이다. 
                   
                   let totalCnt = 0;
                   
                   $.each(json, function(index, item){
                      
                      $.ajax ({   // DB에 insert 해줌
                         
                         url:"<%= ctxPath%>/opendata/seoul_bicycle_rental_insert_END.action",
                         type:"post",
                         async:false,
                         data:item,
                         dataType:"json",
                         success:function(json){
                            
                             if(json.n == 1){
                                totalCnt++;
                             }
                         },
                         error: function(request, status, error){
                              alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
                         }
                           
                      }); // end of $.ajax
                      
                      
                   }); // end of $.each
                   
                   // alert("데이터 입력 건수 totalCnt : " + totalCnt);   // 대략 25초 정도 소요된다.
                   // 데이터 입력 건수 totalCnt : 3296
                   
            },
            error: function(request, status, error){
                   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
               }
            
         }); // end of $.ajax
         
      } // end of if(is_no_data == true)
      
            
           
      
      
   }); // end of $(document).ready(function(){})-------------------


</script>    
    
    
<div style="width: 90%; margin: 0 auto; padding: 1% 0;">
    <div class="row">
       <div class="col-md-6 offset-md-3">
         <p class="text-center h3 text-success pb-3">서울특별시 따릉이대여소 구별 배포현황 2024</p>
       </div>
    </div>
    <div class="row">
       <div class="col-md-3" id="tbl"></div>
       <div class="col-md-9">
           <%-- 파이차트 시작 --%>
           <div class="border">
             <figure class="highcharts-figure">
                <div id="pie_chart_container"></div>
                <p class="text-center">
                         서울특별시 따릉이 대여소 지역구별 배포현황 파이차트 그래프
                </p>
            </figure>
           </div>
           <%-- 파이차트 끝 --%>
           
           <%-- 막대차트 시작 --%>
           <div class="border">
            <figure class="highcharts-figure">
                <div id="bar_chart_container"></div>
                <p class="text-center">
                          서울특별시 따릉이 대여소 지역구별 배포현황 막대차트 그래프
                </p>
            </figure>       
           </div>
           <%-- 막대차트 끝 --%>
       </div>
    </div>
 </div>    
    