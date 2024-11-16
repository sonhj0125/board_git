package com.spring.app.security.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.security.domain.LombokMemberVO;
import com.spring.app.security.service.MemberService;


// ==== 스프링보안 ====  //
@Controller
@RequestMapping(value="/security/*")
public class MemberController {

	@Autowired
	private MemberService memberService;
	
	// 회원가입 form 불러오기
	@GetMapping("member/memberRegister.action")
	public String memberRegister(){
		
		return "security/member/memberRegisterForm.tiles1";
	}
	
	// id 중복검사
	@ResponseBody
	@PostMapping("member/member_id_check.action")
	public String member_id_check(HttpServletRequest request){
		
		String member_id = request.getParameter("member_id");
				
		int n = memberService.member_id_check(member_id);
		
		boolean isExists = false;
		if(n==1) {
			isExists = true;
		}
		
		JSONObject jsonObj = new JSONObject(); // {}
		jsonObj.put("isExists", isExists);  // {"isExists":false}  {"isExists":true}
		
		return jsonObj.toString(); // "{"isExists":false}"  "{"isExists":true}"
	}
	
	
	// email 중복검사
	@ResponseBody
	@PostMapping(value="member/emailDuplicateCheck.action")
	public String emailDuplicateCheck(HttpServletRequest request){
		
		String email = request.getParameter("email");
				
		int n = memberService.emailDuplicateCheck(email);
		
		boolean isExists = false;
		if(n==1) {
			isExists = true;
		}
		
		JSONObject jsonObj = new JSONObject(); // {}
		jsonObj.put("isExists", isExists);  // {"isExists":false}  {"isExists":true}
		
		return jsonObj.toString(); // "{"isExists":false}"  "{"isExists":true}"
	}
	
	
	@GetMapping("member/agree.action")
	public String memberAgree(){
		
		return "member/memberAgree";
	}
	
	
	@PostMapping("member/memberRegisterEnd.action")
	public String memberRegisterEnd(LombokMemberVO membervo, HttpServletRequest request){
		
		// >>>> 사용자 패스워드 암호화 하기
		ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
	
	 // String hashedUserPwd = passwordEncoder.encodePassword(membervo.getMember_pwd(), membervo.getBirth_date() ); 
		
		// 첫번째 파라미터인 membervo.getMember_pwd() 은 사용자가 폼에서 입력한 암호로서 
		// 첫번째 파라미터인 membervo.getMember_pwd() 을 암호화할때 두번째 파라미터인 membervo.getBirth_date() 값을 첨부하여 암호화 한다. 
		// 이렇게 암호화된 결과값이 hashedUserPwd 이다. 
		// 두번째 파라미터인 membervo.getBirth_date() 을 salt 라고 부르는데 이것이 암호화키에 해당되는 것이다.  
		// 그런데 이러한 암호화키(여기서는 membervo.getBirth_date() ) 는 데이터베이스에 암호화가 안되어진 상태로 저장되므로 
		// 누군가에 의해 사용자 정보가 탈취되어지면 해독되어질 가능성이 높아지게 된다.
		// 그래서 암호화키를  membervo.getBirth_date() 을 사용하기 보다는 폼에서 입력한 값만 받아 와서 암호화 되어 해독이 불가하게 되어질 
		// membervo.getMember_pwd() 을 사용하기도 한다.
		// 그래서 암호화키를 아예 사용하지 않고 그냥 암호화 하도록 하는 편이 좋으므로 salt 를 사용하는 아래와 같이 null 로 주기도 한다.
		
		String hashedUserPwd = passwordEncoder.encodePassword(membervo.getMember_pwd(), null);
		
		membervo.setMember_pwd(hashedUserPwd);

	 // System.out.println(">>>> 암호화 hashedUserPwd : " + hashedUserPwd);
		
		try {
			  memberService.insert_member(membervo);
			  
			  request.setAttribute("success", "1");
			  
			  StringBuilder sb = new StringBuilder();
			  sb.append("<span style='font-weight: bold;'>"+ membervo.getMember_name() + "</span>님의 회원 가입이 정상적으로 처리되었습니다.<br/>");
			  sb.append("메인메뉴에서 \"스프링보안 > 로그인\" 을 클릭하여 로그인 하시기 바랍니다.<br/>");
			  
			  request.setAttribute("message", sb.toString());
			  
		} catch(Exception e) {
			request.setAttribute("success", "0");
			request.setAttribute("message", "장애가 발생되어 회원가입이 실패했습니다.");
		}
		
		return "security/member/complete.tiles1"; 
	}
	
	
	// 로그인 인증 form 불러오기 
	@GetMapping("member/login.action")
	public String login(HttpServletRequest request){
		
		// === #264. 스프링보안11 === //
		// login 인증 페이지(http://localhost:9099/board/security/member/login.action)로 이동하기 전의 URL 페이지 값을 알아내어 세션에 저장한다. 
		
		String referer = request.getHeader("referer"); 
		// Spring 에서는 request.getHeader("referer"); 를 사용하여 이전 페이지 URL을 얻어온다. 
		
	//	System.out.println("~~~ 확인용 referer => " + referer);
		// ~~~ 확인용 referer => http://localhost:9090/board/list.action
		
		HttpSession session = request.getSession();
		session.setAttribute("prevURLPage", referer);
		
	    // login 실패여부 체크하기
		String loginFail = request.getParameter("loginFail");
		
		String msg = "";
		
		if("true".equals(loginFail)) {
			msg = "로그인 실패!! 아이디 또는 암호를 잘못 입력하셨습니다.";
		}
		
		request.setAttribute("msg", msg);
		
		return "security/security_login/loginform.tiles1";
		
	}
	
	
	// 접근권한이 필요한 페이지에 접근권한이 없는 유저가 접속할 경우 이동할 주소 
	@GetMapping("noAuthorized.action")
	public String noAuthorized(){
		
		return "security/noAuthorized.tiles1";
	}
	
	
	// 누구나 접근가능 페이지(로그인 안 하더라도 접근 가능함)
	@GetMapping("everybody.action")
	public String everybody(){
		
		return "security/everybody.tiles1";
	}
	
	
	// 회원전용 페이지(로그인한 사용자는 누구나 접근 가능함)
	@GetMapping("member/memberOnly.action")
	public String memberOnly(){
		
		return "security/member/memberOnly.tiles1";
	}
	
	
	// 특별회원전용 페이지(로그인한 사용자중 특정한 사용자만 접근 가능함)
	@GetMapping("member/special/special_memberOnly.action")
	public String special_memberOnly(){
		
		return "security/member/special_memberOnly.tiles1";
	}
	
}
