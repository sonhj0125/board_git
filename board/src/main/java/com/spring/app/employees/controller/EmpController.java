package com.spring.app.employees.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/emp/*") // /emp/empList.action 과 같이 경로를 다 /emp/ 이하로 설정
public class EmpController {
	
	// service, dao 추가 必
	
	@GetMapping("empList.action") // 상단의 RequestMapping 으로 인해 /emp/empList.action 에서 /emp/ 생략 가능
	public String requiredLogin_empList(HttpServletRequest request, HttpServletResponse response) {
		
		return "emp/empList.tiles2";
		// /WEB-INF/views/tiles2/emp/empList.jsp
	}
	
	
	@GetMapping("chart.action")
	public String chart() {
		
		return "";
	}
	
}
