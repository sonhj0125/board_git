package com.spring.app.employees.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.employees.service.EmpService;

@Controller
@RequestMapping(value="/emp/*")
public class EmpController {

	@Autowired
	private EmpService service;
	
	
	@GetMapping("empList.action")
	public String requiredLogin_empList(HttpServletRequest request, HttpServletResponse response) { 
		
		return "emp/empList.tiles2";
		//  /WEB-INF/views/tiles2/emp/empList.jsp  페이지를 만들어야 한다.
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// === #203. 다중 체크박스를 사용시 SQL문에서 in 절을 처리하는 예제 === // 
	@GetMapping("employeeList.action")
	public String employeeList(HttpServletRequest request) {
		
		// employees 테이블에서 근무중인 사원들의 부서번호 가져오기
		List<String> deptIdList = service.deptIdList();
		
		/* System.out.println("~~~ 확인용 시작 ~~~");
		
		for(String deptId : deptIdList) {
			System.out.println(deptId);
		}
		
		System.out.println("~~~ 확인용 끝 ~~~"); */
		/*
		 	~~~ 확인용 시작 ~~~
			-9999
			10
			20
			30
			40
			50
			60
			70
			80
			90
			100
			110
			~~~ 확인용 끝 ~~~
	   */
		
		request.setAttribute("deptIdList", deptIdList);
		
		return "emp/employeeList.tiles2";
		// /WEB-INF/views/tiles2/emp/employeeList.jsp 페이지를 만들어야 한다.
		
	} // end of public String employeeList
	
	
	
	
	
	
	
	
	
}
