package com.spring.app.weather.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/weather/*")
public class WeatherController {

	
	// === #253. 기상청 공공데이터(오픈데이터)를 가져와서 날씨정보 보여주기 === //
	@GetMapping("weatherXML.action")
	public String weatherXML() {
		
		
		return "weather/weatherXML";
		// /WEB-INF/views/weather/weatherXML.jsp 파일을 생성한다.
	} // end of public String weatherXML
	
	
	
	// === #254. 기상청 공공데이터(오픈데이터)를 가져와서 날씨정보 차트 그리기 === //
	@ResponseBody
	@PostMapping(value="weatherXMLtoJSON.action", produces="text/plain;charset=UTF-8")
	public String weatherXMLtoJSON(HttpServletRequest request) {
		
		String str_jsonObjArr = request.getParameter("str_jsonObjArr");
		
		// System.out.println("~~~ 확인용 str_jsonObjArr : " + str_jsonObjArr);
		// ~~~ 확인용 str_jsonObjArr : [{"locationName":"속초","ta":"29.4"},{"locationName":"북춘천","ta":"26.2"},{"locationName":"철원","ta":"25.4"},{"locationName":"동두천","ta":"25.6"},{"locationName":"파주","ta":"24.8"},{"locationName":"대관령","ta":"23.3"},{"locationName":"춘천","ta":"26.1"},{"locationName":"백령도","ta":"24.6"},{"locationName":"북강릉","ta":"30.2"},{"locationName":"강릉","ta":"30.7"},{"locationName":"동해","ta":"29.2"},{"locationName":"서울","ta":"26.5"},{"locationName":"인천","ta":"25.6"},{"locationName":"원주","ta":"29.1"},{"locationName":"울릉도","ta":"29.1"},{"locationName":"수원","ta":"27.4"},{"locationName":"영월","ta":"27.9"},{"locationName":"충주","ta":"29.5"},{"locationName":"서산","ta":"27.0"},{"locationName":"울진","ta":"30.7"},{"locationName":"청주","ta":"29.9"},{"locationName":"대전","ta":"28.8"},{"locationName":"추풍령","ta":"28.1"},{"locationName":"안동","ta":"29.3"},{"locationName":"상주","ta":"29.6"},{"locationName":"포항","ta":"31.0"},{"locationName":"군산","ta":"29.6"},{"locationName":"대구","ta":"30.6"},{"locationName":"전주","ta":"30.0"},{"locationName":"울산","ta":"30.3"},{"locationName":"창원","ta":"30.1"},{"locationName":"광주","ta":"28.8"},{"locationName":"부산","ta":"30.1"},{"locationName":"통영","ta":"27.0"},{"locationName":"목포","ta":"28.2"},{"locationName":"여수","ta":"26.9"},{"locationName":"흑산도","ta":"24.4"},{"locationName":"완도","ta":"30.0"},{"locationName":"고창","ta":"29.2"},{"locationName":"순천","ta":"27.0"},{"locationName":"홍성","ta":"28.8"},{"locationName":"서청주","ta":"28.7"},{"locationName":"제주","ta":"30.3"},{"locationName":"고산","ta":"28.6"},{"locationName":"성산","ta":"29.7"},{"locationName":"서귀포","ta":"29.5"},{"locationName":"진주","ta":"28.7"},{"locationName":"강화","ta":"24.9"},{"locationName":"양평","ta":"26.4"},{"locationName":"이천","ta":"27.9"},{"locationName":"인제","ta":"25.7"},{"locationName":"홍천","ta":"25.2"},{"locationName":"태백","ta":"28.4"},{"locationName":"정선군","ta":"29.0"},{"locationName":"제천","ta":"27.1"},{"locationName":"보은","ta":"28.3"},{"locationName":"천안","ta":"28.7"},{"locationName":"보령","ta":"28.4"},{"locationName":"부여","ta":"29.1"},{"locationName":"금산","ta":"29.8"},{"locationName":"세종","ta":"28.9"},{"locationName":"부안","ta":"29.3"},{"locationName":"임실","ta":"28.3"},{"locationName":"정읍","ta":"29.7"},{"locationName":"남원","ta":"29.8"},{"locationName":"장수","ta":"27.2"},{"locationName":"고창군","ta":"28.8"},{"locationName":"영광군","ta":"29.1"},{"locationName":"김해시","ta":"29.0"},{"locationName":"순창군","ta":"28.5"},{"locationName":"북창원","ta":"30.0"},{"locationName":"양산시","ta":"30.2"},{"locationName":"보성군","ta":"28.2"},{"locationName":"강진군","ta":"29.2"},{"locationName":"장흥","ta":"28.6"},{"locationName":"해남","ta":"29.2"},{"locationName":"고흥","ta":"30.6"},{"locationName":"의령군","ta":"30.7"},{"locationName":"함양군","ta":"28.9"},{"locationName":"광양시","ta":"28.8"},{"locationName":"진도군","ta":"28.5"},{"locationName":"봉화","ta":"28.5"},{"locationName":"영주","ta":"27.8"},{"locationName":"문경","ta":"28.7"},{"locationName":"청송군","ta":"30.2"},{"locationName":"영덕","ta":"29.8"},{"locationName":"의성","ta":"30.4"},{"locationName":"구미","ta":"29.7"},{"locationName":"영천","ta":"30.0"},{"locationName":"경주시","ta":"30.9"},{"locationName":"거창","ta":"29.2"},{"locationName":"합천","ta":"29.5"},{"locationName":"밀양","ta":"31.0"},{"locationName":"산청","ta":"29.6"},{"locationName":"거제","ta":"28.0"},{"locationName":"남해","ta":"28.3"},{"locationName":"북부산","ta":"29.9"}]
		
		// -- 지역 97개 모두 차트에 그리기에는 너무 많으므로 아래처럼 작업을 하여 지역을  21개(String[] locationArr 임)로 줄여서 나타내기로 하겠다.
		
		str_jsonObjArr = str_jsonObjArr.substring(1, str_jsonObjArr.length()-1);
		
		// System.out.println("--- 확인용 str_jsonObjArr : " + str_jsonObjArr);
		//--- 확인용 str_jsonObjArr : {"locationName":"속초","ta":"29.4"},{"locationName":"북춘천","ta":"26.2"},{"locationName":"철원","ta":"25.4"},{"locationName":"동두천","ta":"25.6"},{"locationName":"파주","ta":"24.8"},{"locationName":"대관령","ta":"23.3"},{"locationName":"춘천","ta":"26.1"},{"locationName":"백령도","ta":"24.6"},{"locationName":"북강릉","ta":"30.2"},{"locationName":"강릉","ta":"30.7"},{"locationName":"동해","ta":"29.2"},{"locationName":"서울","ta":"26.5"},{"locationName":"인천","ta":"25.6"},{"locationName":"원주","ta":"29.1"},{"locationName":"울릉도","ta":"29.1"},{"locationName":"수원","ta":"27.4"},{"locationName":"영월","ta":"27.9"},{"locationName":"충주","ta":"29.5"},{"locationName":"서산","ta":"27.0"},{"locationName":"울진","ta":"30.7"},{"locationName":"청주","ta":"29.9"},{"locationName":"대전","ta":"28.8"},{"locationName":"추풍령","ta":"28.1"},{"locationName":"안동","ta":"29.3"},{"locationName":"상주","ta":"29.6"},{"locationName":"포항","ta":"31.0"},{"locationName":"군산","ta":"29.6"},{"locationName":"대구","ta":"30.6"},{"locationName":"전주","ta":"30.0"},{"locationName":"울산","ta":"30.3"},{"locationName":"창원","ta":"30.1"},{"locationName":"광주","ta":"28.8"},{"locationName":"부산","ta":"30.1"},{"locationName":"통영","ta":"27.0"},{"locationName":"목포","ta":"28.2"},{"locationName":"여수","ta":"26.9"},{"locationName":"흑산도","ta":"24.4"},{"locationName":"완도","ta":"30.0"},{"locationName":"고창","ta":"29.2"},{"locationName":"순천","ta":"27.0"},{"locationName":"홍성","ta":"28.8"},{"locationName":"서청주","ta":"28.7"},{"locationName":"제주","ta":"30.3"},{"locationName":"고산","ta":"28.6"},{"locationName":"성산","ta":"29.7"},{"locationName":"서귀포","ta":"29.5"},{"locationName":"진주","ta":"28.7"},{"locationName":"강화","ta":"24.9"},{"locationName":"양평","ta":"26.4"},{"locationName":"이천","ta":"27.9"},{"locationName":"인제","ta":"25.7"},{"locationName":"홍천","ta":"25.2"},{"locationName":"태백","ta":"28.4"},{"locationName":"정선군","ta":"29.0"},{"locationName":"제천","ta":"27.1"},{"locationName":"보은","ta":"28.3"},{"locationName":"천안","ta":"28.7"},{"locationName":"보령","ta":"28.4"},{"locationName":"부여","ta":"29.1"},{"locationName":"금산","ta":"29.8"},{"locationName":"세종","ta":"28.9"},{"locationName":"부안","ta":"29.3"},{"locationName":"임실","ta":"28.3"},{"locationName":"정읍","ta":"29.7"},{"locationName":"남원","ta":"29.8"},{"locationName":"장수","ta":"27.2"},{"locationName":"고창군","ta":"28.8"},{"locationName":"영광군","ta":"29.1"},{"locationName":"김해시","ta":"29.0"},{"locationName":"순창군","ta":"28.5"},{"locationName":"북창원","ta":"30.0"},{"locationName":"양산시","ta":"30.2"},{"locationName":"보성군","ta":"28.2"},{"locationName":"강진군","ta":"29.2"},{"locationName":"장흥","ta":"28.6"},{"locationName":"해남","ta":"29.2"},{"locationName":"고흥","ta":"30.6"},{"locationName":"의령군","ta":"30.7"},{"locationName":"함양군","ta":"28.9"},{"locationName":"광양시","ta":"28.8"},{"locationName":"진도군","ta":"28.5"},{"locationName":"봉화","ta":"28.5"},{"locationName":"영주","ta":"27.8"},{"locationName":"문경","ta":"28.7"},{"locationName":"청송군","ta":"30.2"},{"locationName":"영덕","ta":"29.8"},{"locationName":"의성","ta":"30.4"},{"locationName":"구미","ta":"29.7"},{"locationName":"영천","ta":"30.0"},{"locationName":"경주시","ta":"30.9"},{"locationName":"거창","ta":"29.2"},{"locationName":"합천","ta":"29.5"},{"locationName":"밀양","ta":"31.0"},{"locationName":"산청","ta":"29.6"},{"locationName":"거제","ta":"28.0"},{"locationName":"남해","ta":"28.3"},{"locationName":"북부산","ta":"29.9"}
		
		String[] arr_str_jsonObjArr = str_jsonObjArr.split("\\},");
		
		for(int i=0; i<arr_str_jsonObjArr.length; i++) {
			
			arr_str_jsonObjArr[i] += "}";		// {"locationName":"속초","ta":"29.4" + }
			
		} // end of for
		
		/*
		for(String jsonObj : arr_str_jsonObjArr) {
			System.out.println(jsonObj);
			 	{"locationName":"속초","ta":"29.4"}
				{"locationName":"북춘천","ta":"26.2"}
				{"locationName":"철원","ta":"25.4"}
				{"locationName":"동두천","ta":"25.6"}
				......
		} // end of for
		*/
		
		String[] locationArr = {"서울","인천","수원","춘천","강릉","청주","홍성","대전","안동","포항","대구","전주","울산","부산","창원","여수","광주","목포","제주","울릉도","백령도"};
		String result = "[";
		
		for(String jsonObj : arr_str_jsonObjArr) {
			
			for(int i=0; i<locationArr.length; i++) {
				if( jsonObj.indexOf(locationArr[i]) >= 0 && jsonObj.indexOf("북") == -1 && jsonObj.indexOf("서청주") == -1 ) {
					// 북춘천,춘천,북강릉,강릉,북창원,창원이 있으므로  "북" 이 있는 것은 제외하도록 한다. 또한 서청주(예)도 제외하도록 한다.
					result += jsonObj+",";
					break;
				} // end of if
			} // end of for(int i=0; i<locationArr.length; i++)
			
		} // end of for(String jsonObj : arr_str_jsonObjArr)
		
		// System.out.println("=== 확인용 result : " + result);
		// === 확인용 result : [{"locationName":"춘천","ta":"26.1"},{"locationName":"백령도","ta":"24.6"},{"locationName":"강릉","ta":"30.7"},{"locationName":"서울","ta":"26.5"},{"locationName":"인천","ta":"25.6"},{"locationName":"울릉도","ta":"29.1"},{"locationName":"수원","ta":"27.4"},{"locationName":"청주","ta":"29.9"},{"locationName":"대전","ta":"28.8"},{"locationName":"안동","ta":"29.3"},{"locationName":"포항","ta":"31.0"},{"locationName":"대구","ta":"30.6"},{"locationName":"전주","ta":"30.0"},{"locationName":"울산","ta":"30.3"},{"locationName":"창원","ta":"30.1"},{"locationName":"광주","ta":"28.8"},{"locationName":"부산","ta":"30.1"},{"locationName":"목포","ta":"28.2"},{"locationName":"여수","ta":"26.9"},{"locationName":"홍성","ta":"28.8"},{"locationName":"제주","ta":"30.3"},
		
		// 맨 뒤에 ,로 끝나는 것을 빼고 배열 ]로 닫는다.
		result = result.substring(0, result.length()-1);	// , 빠짐
		result = result + "]";	// ] 추가
		
		// System.out.println("=== 확인용 result : " + result);
		// === 확인용 result : [{"locationName":"춘천","ta":"26.1"},{"locationName":"백령도","ta":"24.6"},{"locationName":"강릉","ta":"30.7"},{"locationName":"서울","ta":"26.5"},{"locationName":"인천","ta":"25.6"},{"locationName":"울릉도","ta":"29.1"},{"locationName":"수원","ta":"27.4"},{"locationName":"청주","ta":"29.9"},{"locationName":"대전","ta":"28.8"},{"locationName":"안동","ta":"29.3"},{"locationName":"포항","ta":"31.0"},{"locationName":"대구","ta":"30.6"},{"locationName":"전주","ta":"30.0"},{"locationName":"울산","ta":"30.3"},{"locationName":"창원","ta":"30.1"},{"locationName":"광주","ta":"28.8"},{"locationName":"부산","ta":"30.1"},{"locationName":"목포","ta":"28.2"},{"locationName":"여수","ta":"26.9"},{"locationName":"홍성","ta":"28.8"},{"locationName":"제주","ta":"30.3"}]
		
		return result;
		
	} // end of public String weatherXMLtoJSON
	
	
	
	
}
