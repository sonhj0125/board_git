package com.spring.app.security.loginsuccess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.spring.app.security.domain.Session_MemberVO;
import com.spring.app.security.service.MemberService;



// ===== #265. 스프링보안12 =====
// 로그인 성공후 세션 및 쿠키등의 부가적인 처리를 할 수 있도록 해주는 클래스를 작성한다.

/* 로그인 전 정보를 Cache 하도록 해야한다. 
   ==> 로그인 되지 않은 상태에서 로그인 상태에서만 사용할 수 있는 페이지로 이동할 경우 에는
             로그인 인증 페이지로 이동하고 로그인이 성공 되어진 후에는 로그인 하기 전의 페이지로 이동하도록 한다.
*/
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Autowired
	private MemberService memberService; 
	
	
	/*
	   Spring Security는 로그인이 성공한 뒤에 부가적인 작업을 해야하는 경우
	     부가적인 작업에 대해 기술할 내용은 onAuthenticationSuccess() 메소드를 재정의 하여 기술하면 되도록 되어있다.
	   onAuthenticationSuccess() 메소드는 3가지 파라미터를 가지는데
	     
	     그 첫번째 파라미터로 HttpServletRequest request 는 
	         웹으로 부터 넘어온 Request 값을 가져올 수 있고(request.getParameter() 메소드),
	     
	     두번째 파라미터로 HttpServletResponse response 는 
	         출력을 기술할 수 있으며(response.getWriter().println() 메소드),
	     
	     세번째 파라미터로 Authentication authentication 는 인증을 성공했기 때문에 
	         로그인한 회원의 회원정보(authentication.getPrincipal() 메소드)를 가져올수 있다. 
	     
	     지금 예를 든 것은 일부 예를 든것이고, HttpServletRequest 객체, HttpServletResponse 객체, Authentication 객체를 이용해서 가져올 수 있는 정보를 이용해 할 수있는 일은 
	     모두다 할수 있다고 보면 된다. 예를 들어 회원별로 방문수 증가 작업을 해야 한다면 Authentication 객체를 이용해서 
	     로그인 되어진 회원아이디를 가져온뒤 이것을 이용해서 DB에 작업하면 되는 것이다.      
	 
	 >>>>>> 우리는 로그인 할때 로그인 성공후 이동할 URL을 지정하여 해당 URL로 이동하는 기능을 넣도록 하겠다.
	        이 기능을 하는데 있어서 몇가지 우선순위를 잡을 것이다. 다음이 그 우선순위이다.
	    1. 지정된 Request Parameter(loginRedirect)에 로그인 작업을 마친 뒤 redirect 할 URL을 지정했다면 이 URL로 redirect 하도록 한다.
	    2. 만약 지정된 Request Parameter에 지정된 URL이 없다면 세션에 저장된 URL로 redirect 하도록 한다.
	    3. 그러나 만약 세션에 저장된 URL도 없다면 Request의 REFERER 헤더 값을 읽어서 로그인 페이지를 방문하기 전 페이지의 URL을 읽어서 거기로 이동하도록 한다.
	       (REFERER 기능 사용 여부는 설정 가능하도록 한다. 이 기능 설정을 해야 하는 이유는 밑에서 별도록 설명하도록 하겠다.)
	    4. 위의 3가지 경우 모두 만족하는것이 없다면 MyAuthenticationSuccessHandler 클래스에 있는 defaultUrl 속성에 지정된 URL로 이동하도록 한다.
	    
	        이런 우선 순위를 생각해두고 이 기능을 구현해보도록 한다.
	 */
	
	private boolean useReferer;
	private String defaultUrl;
	
    public MyAuthenticationSuccessHandler() { }
	
	public MyAuthenticationSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }
		
	public boolean isUseReferer() {
		return useReferer;
	}

	public void setUseReferer(boolean useReferer) {
		this.useReferer = useReferer;
	}
	
	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
	
	
/*
	@SuppressWarnings("unused")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request
			                          , HttpServletResponse response
			                          , Authentication authentication)
		throws IOException, ServletException {
		
		    HttpSession session = request.getSession();
		
		    String member_id = authentication.getName(); // 로그인한 아이디가 나온다.
		    
			System.out.println("~~~~ 확인용 member_id => " + member_id); 
			// ~~~~ 확인용 member_id => seoyh
			
		    System.out.println("~~~~~~ 확인용 memberService => " + memberService);
		    // ~~~~~~ 확인용 memberService => com.spring.app.security.service.MemberService_imple@629a9fa2 
		    
			// >>> 로그인한 날짜를 업데이트 하도록 함.
			memberService.update_last_login(member_id);
			
			// >>> 로그인한 사용자 정보(아이디와 성명)를 세션에 저장하도록 함.
			Map<String, String> map = memberService.get_member(member_id);
			
			Session_MemberVO session_MemberVO = new Session_MemberVO();
			session_MemberVO.setMember_id(map.get("MEMBER_ID"));
			session_MemberVO.setMember_name(map.get("MEMBER_NAME"));
									
			session.setAttribute("sesMemberinfo", session_MemberVO);
			
			// >>> 로그인이 성공되어진 후 화면에 보여질 페이지를 지정하도록 한다.		
			if (session != null) {
				
				String redirectUrl = (String)session.getAttribute("prevURLPage");
	            
	            System.out.println("~~~~ 확인용 redirectUrl => " + redirectUrl);
	            
	            if (redirectUrl != null) {
	                session.removeAttribute("prevURLPage");
	                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	             // 로그인 하기 이전페이지로 이동한다.
	            } else {
	                super.onAuthenticationSuccess(request, response, authentication);
	             // super.onAuthenticationSuccess(request, response, authentication); 메소드로 인해서 
	             // 로그인 할때 화면으로 다시 돌아간다.
	            }
	            
	        } 
			else {
	            
				super.onAuthenticationSuccess(request, response, authentication);
	        }
			
	}// end of onAuthenticationSuccess()-------------------------------------
*/	
	
	@SuppressWarnings("unused")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request
			                          , HttpServletResponse response
			                          , Authentication authentication)
		throws IOException, ServletException {
		
		    HttpSession session = request.getSession();
		
		    String member_id = authentication.getName(); // 로그인한 아이디가 나온다.
		    
		//	System.out.println("~~~~ 확인용 member_id => " + member_id); 
			// ~~~~ 확인용 member_id => sec_user1
			
		//	System.out.println("~~~~~~ 확인용 memberService => " + memberService);
		    // ~~~~~~ 확인용 memberService => com.spring.app.security.service.MemberService_imple@629a9fa2
			
		 // >>> 로그인한 날짜와 IP Address 를 저장 하도록 함.
		    Map<String, String> paraMap = new HashMap<>();
		    paraMap.put("member_id", member_id);
		    paraMap.put("clientip",  request.getRemoteAddr());
		    
		 	memberService.insert_security_loginhistory(paraMap); 
		    
		 	
			// >>> 로그인한 사용자 정보(아이디와 성명)를 세션에 저장하도록 함.
		 	Map<String, String> map = memberService.get_member(member_id);
		 	
		 	Session_MemberVO session_MemberVO = new Session_MemberVO();
			session_MemberVO.setMember_id(map.get("MEMBER_ID"));
			session_MemberVO.setMember_name(map.get("MEMBER_NAME"));
												
			session.setAttribute("sesMemberinfo", session_MemberVO);
			
			// >>> 로그인이 성공되어진 후 화면에 보여질 페이지를 지정하도록 한다.		
			if (session != null) {
				
				String redirectUrl = (String)session.getAttribute("prevURLPage");
	            
	        //  System.out.println("~~~~ 확인용 redirectUrl => " + redirectUrl);
	            // ~~~~ 확인용 redirectUrl => http://localhost:9090/board/list.action
	            
	            if (redirectUrl != null) {
	                
	            	if("http://localhost:9090/board/security/member/memberRegisterEnd.action".equals(redirectUrl)) {
	                	redirectUrl = "http://localhost:9090/board/index.action";
	                }
	                
	            	session.removeAttribute("prevURLPage");
	                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	             // 로그인 하기 이전페이지로 이동한다.
	            } else {
	                super.onAuthenticationSuccess(request, response, authentication);
	             // super.onAuthenticationSuccess(request, response, authentication); 메소드로 인해서 
	             // 로그인 할때 화면으로 다시 돌아간다.
	            }
	            
	        } 
			else {
	            
				super.onAuthenticationSuccess(request, response, authentication);
	        }
			
	}// end of onAuthenticationSuccess()-------------------------------------
	
}
