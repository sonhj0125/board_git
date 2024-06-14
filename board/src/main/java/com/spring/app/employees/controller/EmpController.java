package com.spring.app.employees.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/emp/*")		// post, get 둘다 허용인데, value 안의 주소가 Mapping 앞에 붙는다.
// 공통적으로 똑같은 /emp/를 뺄 수 있다!
public class EmpController {

	
	
	@GetMapping("empList.action")
	public String requiredLogin_empList(HttpServletRequest request) {
	
		return "";
		
	} // end of public String empList()
	
	
	
	
}
