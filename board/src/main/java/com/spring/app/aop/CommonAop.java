package com.spring.app.aop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.map.HashedMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.service.BoardService;
import com.spring.app.common.MyUtil;
import com.spring.app.employees.service.EmpService;

//=== #53. 공통관심사 클래스(Aspect 클래스) 생성하기 === //
//AOP(Aspect Oriented Programming) 

@Aspect		// 공통 관심사 클래스(Aspect 클래스)로 등록된다.
@Component	// bean으로 등록된다.
public class CommonAop {
	
	// ★★★★★ 중요 !!!
	
	// ===== Before Advice(보조업무) 만들기 ====== // 
	/*
		주업무(<예: 글쓰기, 글수정, 댓글쓰기, 직원목록조회 등등>)를 실행하기 앞서서  
       	이러한 주업무들은 먼저 로그인을 해야만 사용가능한 작업이므로
       	주업무에 대한 보조업무<예: 로그인 유무검사> 객체로 로그인 여부를 체크하는
       	관심 클래스(Aspect 클래스)를 생성하여 포인트컷(주업무)과 어드바이스(보조업무)를 생성하여
       	동작하도록 만들겠다.
	*/   
	
	// === Pointcut(주업무)을 설정해야 한다. === //
	// Pointcut 이란 공통관심사<예: 로그인 유무검사>를 필요로 하는 메소드를 말한다.
	@Pointcut("execution(public * com.spring.app..*Controller.requiredLogin_*(..) )")
																		   // ..은 메소드에 파라미터가 있든 없든 상관없다는 의미
	// 예) com.spring.app.board.controller.BoardController.requiredLogin_add(ModelAndView mav)
	// 예) com.spring.app.employees.controller.EmpController.requiredLogin_empList()
	public void requiredLogin() {}
	
	// === Before Advice(공통관심사, 보조업무)를 구현한다. === //
	@Before("requiredLogin()")
	public void loginCheck(JoinPoint joinpoint) { // 로그인 유무 검사를 하는 메소드 작성하기
		// JoinPoint joinpoit는 Pointcut 된 주업무의 메소드이다.
		
		// 로그인 유무를 확인하기 위해서는 request를 통해 session 을 얻어와야 한다.
		HttpServletRequest request = (HttpServletRequest)joinpoint.getArgs()[0]; // 주업무 메소드의 첫 번째 파라미터를 얻어오는 것이다.
		HttpServletResponse response = (HttpServletResponse)joinpoint.getArgs()[1]; // 주업무 메소드의 두 번째 파라미터를 얻어오는 것이다.
		
		HttpSession session = request.getSession();
		
		if(session.getAttribute("loginuser") == null) {
			String message = "먼저 로그인 하세요!";
			String loc = request.getContextPath() + "/login.action";

			request.setAttribute("message", message);
			request.setAttribute("loc", loc);
			
			// >>> 로그인 성공 후 로그인 하기 전 페이지로 돌아가는 작업 만들기 <<<
			String url = MyUtil.getCurrentURL(request);
			session.setAttribute("goBackURL", url); // 세션에 url 정보를 저장시켜둔다.
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/msg.jsp"); // request를 보낼 경로 지정
			
			try {
				dispatcher.forward(request, response); // 사용자 요청에 의해 컨테이너에서 생성된 request와 response를 jsp로 넘겨주는 역할
				
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// ===== #105. After Advice(보조업무) 만들기 ====== // 
	/*
       	주업무(<예: 글쓰기, 제품구매 등등>)를 실행한 다음에  
		회원의 포인트를 특정점수(예: 100점, 200점, 300점) 증가해 주는 것이 공통의 관심사(보조업무)라고 보자.
       	관심 클래스(Aspect 클래스)를 생성하여 포인트컷(주업무)과 어드바이스(보조업무)를 생성하여
       	동작하도록 만들겠다.
	*/
	// === Pointcut(주업무)을 설정해야 한다. === //
	// Pointcut 이란 공통관심사를 필요로 하는 메소드를 말한다.
	@Pointcut("execution(public * com.spring.app..*Controller.pointPlus_*(..) )")
	public void pointPlus() {}
	
	@Autowired  // Type에 따라 알아서 Bean 을 주입해준다.
	private BoardService service;
	
	// === After Advice(공통관심사, 보조업무)를 구현한다. === //
	// 회원의 포인트를 특정 점수(예: 100점, 200점, 300점)만큼 증가시키는 메소드 생성하기 
	@SuppressWarnings("unchecked") // 노란 줄 경고 표시 안 뜨게 하는 것
	@After("pointPlus()")
	public void pointPlus(JoinPoint joinpoint) {
		// JoinPoint joinpoit는 Pointcut 된 주업무의 메소드이다.
		
		Map<String, String> paraMap = (Map<String, String>)joinpoint.getArgs()[0];
		// 주업무 메소드의 첫 번째 파라미터를 얻어오는 것이다.
		
		service.pointPlus(paraMap);
	}
	
	
	
	// ===== #215. Around Advice(보조업무) 만들기 ====== //
	/*
		Before ----> 보조업무1
	         주업무 
	    After  ----> 보조업무2
	         
	        보조업무1 + 보조업무2 을 실행하도록 해주는 것이 Around Advice 이다.
	*/
	// == Pointcut(주업무) 을 생성한다. ==
	//    Pointcut 이란 공통관심사를 필요로 하는 메소드를 말한다.
	// Pointcut 생성 시 public 은 생략 가능하다. 접근제한자를 생략하면 public 이 있는 것으로 본다.
	// 왜냐하면 외부에서 특정메소드에 접근을 해야 하므로 접근제한자는 무조건  public 이어야 하기 때문이다.
	
	@Pointcut("execution( String com.spring.app.employees.controller.EmpController.empmanager_*(..) )")
	public void empmanager() {}
	
	@Autowired
	private EmpService empservice;
	
	// == Around Advice(공통관심사, 보조업무)를 구현한다.
	// - 직원관리와 관련된 주업무를 실행하는 데 있어서 권한이 있는지(로그인 된 사용자의 gradelevel 값이 10인지)를 알아보는 것을 보조업무로 보겠다.  
	// - 인사관리 페이지에 접속한 이후에, 인사관리 페이지에 접속한 페이지URL, 사용자ID, 접속IP주소, 접속시간을 기록으로 DB에 tbl_empManger_accessTime 테이블에 insert 하도록 한다.
	@Around("empmanager()")
	public String checkAuthority(ProceedingJoinPoint joinPoint) {
		/* Around Advice 에서는 ProceedingJoinPoint joinPoint 가
                      포인트컷 된 주업무의 메소드이다. */
		
		// ========= 보조업무 1 시작 =========== //
		// - 직원관리와 관련된 주업무를 실행하는 데 있어서 권한이 있는지(로그인 된 사용자의 gradelevel 값이 10인지)를 알아보는것을 보조업무로 보겠다. 
		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0]; 
		HttpSession session = request.getSession();
		MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");

		String viewPage = null;
		
		if (loginuser == null || loginuser.getGradelevel() < 10) {
			// 로그인 하지 않거나 로그인 된 사용자의 gradelevel 값이 10 미만 인 경우
			// 주업무를 실행하지 않고 "접근할 수 있는 권한이 없습니다." 라는 alert 메시지만 보이도록 하겠다.

			String message = "접근할 수 있는 권한이 없습니다.(Arround Advice 활용)";
			String loc = null;
			if (loginuser == null) {
				loc = request.getContextPath() + "/login.action";
			} else {
				loc = "javascript:history.back()";
			}

			request.setAttribute("message", message);
			request.setAttribute("loc", loc);

			viewPage = "msg";
			
		} else {
			// 로그인 되어진 사용자의 gradelevel 값이 10 이상인 경우 
			
			try {
				viewPage = (String)joinPoint.proceed();
				// !!!!! 중요 !!!!! 주업무 메소드(포인트컷) 가 실행되는 것이다. !!!! 
				
//				System.out.println("~~~ 확인용 viewPage => " + viewPage);
				// ~~~ 확인용 viewPage => emp/employeeList.tiles2
				
				/* 
              		joinPoint.proceed(); 은 
              		Pointcut 에 설정된 주업무 메소드를 실행하라는 것이다.
	              	지금은  String com.spring.app..EmpController.empmanager_*(..) 이다.
	               
             		Object org.aspectj.lang.ProceedingJoinPoint.proceed() throws Throwable
              		Throwable 클래스는 예외처리를 할 수 있는 최상위 클래스이다. 
              		Throwable 클래스의 자식 클래스가 Exception 과 Error 클래스이다.
	                
                  	그리고 joinPoint.proceed()메소드의 리턴값은 Object 이다.
	              	이를 통해 Aspect 로 연결된 Original Method(주업무 메소드, 지금은 EmpController 클래스의 메소드명이 empmanager_ 으로 시작하는 메소드임)의 리턴값을  
	              	형변환(캐스팅)을 통하여 받을수 있다.
	            */
				
			} catch (Throwable e) {
				e.printStackTrace();
				// ========= 보조업무 1 끝 =========== //
				
			} finally {
				
				// ========= 보조업무 2 시작 =========== //
	            // - 인사관리 페이지에 접속한 이후에, 인사관리 페이지에 접속한 페이지URL, 사용자ID, 접속IP주소, 접속시간을 기록으로 DB에 tbl_empManger_accessTime 테이블에 insert 하도록 한다.
				
				Map<String, String> paraMap = new HashedMap<>();
				
				paraMap.put("pageUrl", request.getContextPath() + MyUtil.getCurrentURL(request));
				paraMap.put("fk_userid", loginuser.getUserid());
				paraMap.put("clientIP", request.getRemoteAddr()); // request.getRemoteAddr() 이 WAS에 접속한 클라이언트 IP 주소이다. 
				
				Date now = new Date(); // 현재시각 
	            SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            String accessTime = sdfrmt.format(now);
	            
	            paraMap.put("accessTime", accessTime);
	            
	            empservice.insert_accessTime(paraMap);
	            // ========= 보조업무 2 끝 =========== //
			}
			
		}
		
		return viewPage;
		/* 
        	return viewPage; 의 뜻은 
          	주업무 메소드인 public String com.spring.app.employees.controller.EmpController.empmanager_*(..) 쪽으로 넘겨준다는 것이다.
          	그러므로 주업무 메소드의 리턴타입이 String 이므로 viewPage 타입도 String 이어야 하는 것이다.
		*/
		
	}
	
	
	
	
	
	
	
}
