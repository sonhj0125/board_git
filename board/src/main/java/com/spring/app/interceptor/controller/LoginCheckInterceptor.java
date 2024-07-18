package com.spring.app.interceptor.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

// ==== #인터셉터관련3. ====
public class LoginCheckInterceptor implements HandlerInterceptor {
	
	/* 
	    preHandle() 메소드는 지정된 컨트롤러의 동작 이전에 호출된다. 
	    preHandle() 메소드에서 false를 리턴하면 다음 내용(Controller의 동작)을 실행하지 않는다. 
	    true를 리턴하면 다음 내용(Controller의 동작)을 실행하게 된다.
	*/
	// Object handler는 Dispatcher의 HandlerMapping 이 찾아준 Controller Class 객체

	/* 
	    [참고]
	    postHandle() 메소드 : 클라이언트의 요청을 처리한 뒤에 호출된다. 
	                                            컨트롤러에서 예외가 발생되면 실행되지 않는다.

		afterCompletion() 메소드 : 클라이언트 요청을 마치고 클라이언트에서 뷰를 통해 응답을 전송한 뒤 실행이 된다. 
                               	  뷰를 생성할 때에 예외가 발생할 경우에도 실행이 된다. 
	*/
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// 로그인 여부 검사
		HttpSession session = request.getSession();

		if (session.getAttribute("loginuser") == null) {

			// 로그인이 되지 않은 상태
			String message = "먼저 로그인 하세요(인터셉터활용)~~~";
			String loc = request.getContextPath() + "/login.action";

			request.setAttribute("message", message);
			request.setAttribute("loc", loc);

			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/msg.jsp");

			try {
				dispatcher.forward(request, response);

			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		return true;
	}
	/*
	 * 다음으로 /WEB-INF/spring/appServlet/servlet-context.xml 파일에 가서
	 * LoginCheckInterceptor 클래스를 빈으로 올려주어야 한다.
	 */
}
