package com.spring.app.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spring.app.security.service.MemberService;

// ==== 스프링보안 ====  //
@Controller
@RequestMapping(value="/security/admin/*")
public class AdminController {

	@Autowired
	private MemberService memberService;
	
	
	// 관리자전용 페이지(관리자 권한이 있는 사용자만 접근 가능함)
	@GetMapping(value="adminOnly.action")
	public String adminOnly(){
		
		return "security/admin/adminOnly.tiles1";
	}
	
}
