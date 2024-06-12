package com.spring.app.board.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.board.domain.TestVO;
import com.spring.app.board.service.BoardService;

/*
	사용자 웹브라우저 요청(View)  ==> DispatcherServlet ==> @Controller 클래스 <==>> Service단(핵심업무로직단, business logic단) <==>> Model단[Repository](DAO, DTO) <==>> myBatis <==>> DB(오라클)           
	(http://...  *.action)                                  |                                                                                                                              
	 ↑                                                View Resolver(접두어+접미어)
	 |                                                      ↓
	 |                                                View단(.jsp 또는 Bean명)
	 -------------------------------------------------------| 
	
	사용자(클라이언트)가 웹브라우저에서 http://localhost:9090/board/test/test_insert.action 을 실행하면
	배치서술자인 web.xml 에 기술된 대로  org.springframework.web.servlet.DispatcherServlet 이 작동된다.
	DispatcherServlet 은 bean 으로 등록된 객체중 controller 빈을 찾아서  URL값이 "/test_insert.action" 으로
	매핑된 메소드를 실행시키게 된다.                                               
	Service(서비스)단 객체를 업무 로직단(비지니스 로직단)이라고 부른다.
	Service(서비스)단 객체가 하는 일은 Model단에서 작성된 데이터베이스 관련 여러 메소드들 중 관련있는것들만을 모아 모아서
	하나의 트랜잭션 처리 작업이 이루어지도록 만들어주는 객체이다.
	여기서 업무라는 것은 데이터베이스와 관련된 처리 업무를 말하는 것으로 Model 단에서 작성된 메소드를 말하는 것이다.
	이 서비스 객체는 @Controller 단에서 넘겨받은 어떤 값을 가지고 Model 단에서 작성된 여러 메소드를 호출하여 실행되어지도록 해주는 것이다.
	실행되어진 결과값을 @Controller 단으로 넘겨준다.
*/

/* 	
  	XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다. 
	그리고 bean의 이름(첫글자는 소문자)은 해당 클래스명이 된다. 
	즉, 여기서 bean의 이름은 boardController 이 된다. 
	여기서는 @Controller 를 사용하므로 @Component 기능이 이미 있으므로 @Component를 명기하지 않아도 BoardController 는 bean 으로 등록되어 스프링컨테이너가 자동적으로 관리해준다. 
*/



//@Component 빼버릴 수 있다!
@Controller		// @Controller 속에는 @Component 기능이 포함되어 있음
public class BoardController {
	// === #35. 의존객체 주입하기(DI: Dependency Injection) ===
	// ※ 의존객체 주입(DI : Dependency Injection : 의존객체 주입) 
    //  ==> 스프링 프레임워크는 객체를 관리해주는 컨테이너를 제공해주고 있다.
    //      스프링 컨테이너는 bean으로 등록되어진 BoardController 클래스 객체가 사용되어질때, 
    //      BoardController 클래스의 인스턴스 객체변수(의존객체)인 BoardService service 에 
    //      자동적으로 bean 으로 등록되어 생성되어진 BoardService service 객체를  
    //      BoardController 클래스의 인스턴스 변수 객체로 사용되어지게끔 넣어주는 것을 의존객체주입(DI : Dependency Injection)이라고 부른다. 
    //      이것이 바로 IoC(Inversion of Control == 제어의 역전) 인 것이다.
    //      즉, 개발자가 인스턴스 변수 객체를 필요에 의해 생성해주던 것에서 탈피하여 스프링은 컨테이너에 객체를 담아 두고, 
    //      필요할 때에 컨테이너로부터 객체를 가져와 사용할 수 있도록 하고 있다. 
    //      스프링은 객체의 생성 및 생명주기를 관리할 수 있는 기능을 제공하고 있으므로, 더이상 개발자에 의해 객체를 생성 및 소멸하도록 하지 않고
	//      객체 생성 및 관리를 스프링 프레임워크가 가지고 있는 객체 관리기능을 사용하므로 Inversion of Control == 제어의 역전 이라고 부른다.  
    //      그래서 스프링 컨테이너를 IoC 컨테이너라고도 부른다.
  
	//  IOC(Inversion of Control : 제어의 역전) 란 ?
	//  ==> 스프링 프레임워크는 사용하고자 하는 객체를 빈형태로 이미 만들어 두고서 컨테이너(Container)에 넣어둔후
	//      필요한 객체사용시 컨테이너(Container)에서 꺼내어 사용하도록 되어있다.
	//      이와 같이 객체 생성 및 소멸에 대한 제어권을 개발자가 하는것이 아니라 스프링 Container 가 하게됨으로써 
	//      객체에 대한 제어역할이 개발자에게서 스프링 Container로 넘어가게 됨을 뜻하는 의미가 제어의 역전 
	//      즉, IOC(Inversion of Control) 이라고 부른다.
	//		스프링 컨테이너가 알아서 bean을 주입시켰다가 없앴다가 해줌.
  
  
	//  === 느슨한 결합 ===
	//      스프링 컨테이너가 BoardController 클래스 객체에서 BoardService 클래스 객체를 사용할 수 있도록 
	//      만들어주는 것을 "느슨한 결합" 이라고 부른다.
	//      느스한 결합은 BoardController 객체가 메모리에서 삭제되더라도 BoardService service 객체는 메모리에서 동시에 삭제되는 것이 아니라 남아 있다.
  
	// ===> 단단한 결합(개발자가 인스턴스 변수 객체를 필요에 의해서 생성해주던 것)
	// 		private InterBoardService service = new BoardService(); 
	// 		===> BoardController 객체가 메모리에서 삭제 되어지면  BoardService service 객체는 멤버변수(필드)이므로 메모리에서 자동적으로 삭제되어진다.
	
	@Autowired		// Type에 따라 알아서 Bean 을 주입해준다.
	private BoardService service;	// 처음에는 null, 주입시키면 null 이 아니게 됨.
	// BoardService은 Type이 하나밖에 없으므로 내 맘대로 service라고 이름 붙여서 사용가능
	// 만약에 @Qualifier("")를 사용하고 싶다면, @Qualifier("boardService_imple")이라고 사용한다. 근데, 하나밖에 없으므로 쓰지 않는다.
	
	
	// ==== **** spring 기초 시작  **** ==== //
	@RequestMapping(value = "/test/test_insert.action")	// '/'앞에  /app 즉, /board 생략되어있음!
	// http://localhost:9099/board/test/test_insert.action 와 동일한 의미
	public String test_insert(HttpServletRequest request) {
		
		int n = service.test_insert();
		
		String message = "";
		
		if(n == 1) {
			message = "데이터 입력 성공!!";
		}
		else {
			message = "데이터 입력 실패!!";
		}
		
		request.setAttribute("message", message);
		request.setAttribute("n", n);
		
		return "test/test_insert";
		// /WEB-INF/views/ 접두어(servlet-context.xml)
		// .jsp 접미어
		// 즉, /WEB-INF/views/test/test_insert.jsp 뷰단 페이지를 만들어야 한다.
	} // end of public String test_insert(HttpServletRequest request) 
	
	
	
	
	@RequestMapping(value = "/test/test_select.action")
	public String test_select(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.test_select();
		
		request.setAttribute("testvoList", testvoList);
		
		return "test/test_select";
		// /WEB-INF/views/test/test_select.jsp 페이지를 만든다.
		
	} // end of public String test_select(HttpServletRequest request)
	
	
	
	
//	@RequestMapping(value = "/test/test_form1.action", method= {RequestMethod.GET})	// GET 방식만 허락해준다.
//	@RequestMapping(value = "/test/test_form1.action", method= {RequestMethod.POST})	// POST 방식만 허락해준다.
	
	@RequestMapping(value = "/test/test_form1.action")	// GET 방식 또는 POST 방식 모두 허락해준다.
	public String test_form1(HttpServletRequest request) {
		
		String method = request.getMethod();
		
		if("GET".equalsIgnoreCase(method)) {	// GET 방식이라면
			return "test/test_form1"; 	// view 단 페이지를 띄운다.
			// /WEB-INF/views/test/test_form1.jsp 페이지를 만든다.
		}
		else {	// POST 방식이라면
			String no = request.getParameter("no");
			String name = request.getParameter("name");
			
			TestVO tvo = new TestVO();
			tvo.setNo(no);
			tvo.setName(name);
			
			int n = service.test_insert(tvo);
			
			if(n == 1) {
				// 뷰단페이지를 만들 필요가 없음! send redirect!
				return "redirect:/test/test_select.action";
				// /test/test_select.action 페이지로 이동한다.
			}
			else {
				return "redirect:/test/test_form1.action";
				// /test/test_form1.action 페이지로 이동한다.
			}
		
		}
		
		
	} // end of public String test_form1(HttpServletRequest request)
	
	
	
	
	@RequestMapping(value = "/test/test_form2.action")	// GET 방식 또는 POST 방식 모두 허락해준다.
	public String test_form2(HttpServletRequest request, TestVO tvo) {
		
		String method = request.getMethod();
		
		if("GET".equalsIgnoreCase(method)) {	// GET 방식이라면
			return "test/test_form2"; 	// view 단 페이지를 띄운다.
			// /WEB-INF/views/test/test_form2.jsp 페이지를 만든다.
		}
		else {	// POST 방식이라면
			
			int n = service.test_insert(tvo);
			
			if(n == 1) {
				// 뷰단페이지를 만들 필요가 없음! send redirect!
				return "redirect:/test/test_select.action";
				// /test/test_select.action 페이지로 이동한다.
			}
			else {
				return "redirect:/test/test_form2.action";
				// /test/test_form2.action 페이지로 이동한다.
			}
		
		}
	} // end of public String test_form2(HttpServletRequest request)


	
	
	@RequestMapping(value = "/test/test_form3.action", method={RequestMethod.GET})	// GET 방식만 허락해준다.
	public String test_form3() {
		
		return "test/test_form3"; 	// view 단 페이지를 띄운다.
		// /WEB-INF/views/test/test_form3.jsp 페이지를 만든다.
		
	} // end of public String test_form3()
	
	
	
	
	@RequestMapping(value = "/test/test_form3.action", method={RequestMethod.POST})	// POST 방식만 허락해준다.
	public String test_form3(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		if(n == 1) {
			// 뷰단페이지를 만들 필요가 없음! send redirect!
			return "redirect:/test/test_select.action";
			// /test/test_select.action 페이지로 이동한다.
		}
		else {
			return "redirect:/test/test_form3.action";
			// /test/test_form3.action 페이지로 이동한다.
		}
	} // end of public String test_form3(TestVO tvo)
	
	
	
	// method={RequestMethod.POST} 대신 @GetMapping 사용 및 (value=) 지워도 됨
	@GetMapping("/test/test_form4.action")	// GET 방식만 허락해준다.
	public String test_form4() {
		
		return "test/test_form4"; 	// view 단 페이지를 띄운다.
		// /WEB-INF/views/test/test_form4.jsp 페이지를 만든다.
		
	} // end of public String test_form4()
	
	
	
	
	@PostMapping("/test/test_form4.action")	// POST 방식만 허락해준다.
	public String test_form4(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		if(n == 1) {
			// 뷰단페이지를 만들 필요가 없음! send redirect!
			return "redirect:/test/test_select.action";
			// /test/test_select.action 페이지로 이동한다.
		}
		else {
			return "redirect:/test/test_form4.action";
			// /test/test_form4.action 페이지로 이동한다.
		}
	} // end of public String test_form4(TestVO tvo)
	


	
	
	// ======= Ajax 연습시작 ======= //
	
	@GetMapping("/test/test_form5.action")	
	public String test_form5() {
		
		return "test/test_form5"; 	
		
	} // end of public String test_form5()

	
	@ResponseBody // toString으로 문자열로 바뀐 것("{"n":1}") 결과물 그대로 뷰단에 보여주기 위해 해줌
	@PostMapping("/test/ajax_insert.action")
	public String ajax_insert(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		JSONObject jsonObj = new JSONObject();	// {}
		jsonObj.put("n", n);					// {"n":1}
		
		return jsonObj.toString();				// "{"n":1}"
		
	
	} // end of public String ajax_insert()
	
	
	/*
	    @ResponseBody 란?
	     	메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단 페이지를 통해서 출력되는 것이 아니라 
	    	return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON 값을 Return 할때 많이 사용된다.
	    
	    >>> 스프링에서 json 또는 gson을 사용한 ajax 구현시 데이터를 화면에 출력해 줄때 한글로 된 데이터가 '?'로 출력되어 한글이 깨지는 현상이 있다. 
           	이것을 해결하는 방법은 @RequestMapping 어노테이션의 속성 중 produces="text/plain;charset=UTF-8" 를 사용하면 
           	응답 페이지에 대한 UTF-8 인코딩이 가능하여 한글 깨짐을 방지 할 수 있다. <<< 
	 */   
	
	@ResponseBody	// 결과물 그대로 뷰단에 보여주기 위해 해줌
	@GetMapping(value="/test/ajax_select.action", produces="text/plain;charset=UTF-8")
	public String ajax_select() {
		
		List<TestVO> testvoList = service.test_select();
		
		JSONArray jsonArr = new JSONArray();	// [] 배열
		
		if(testvoList != null) {
			
			for(TestVO vo : testvoList) {
				
				JSONObject jsonObj = new JSONObject();	// {} 객체
				jsonObj.put("no", vo.getNo());
				jsonObj.put("name", vo.getName() );
				jsonObj.put("writeday", vo.getWriteday());
				
				jsonArr.put(jsonObj);
				
			} // end of for(TestVO vo : testvoList)s
			
		} // end of if(testvoList != null)
		
		
		return jsonArr.toString();
		
	} // end of public String ajax_select()
	
	// ======= Ajax 연습끝 ======= //
	
	
	
	
	// ************ tiles 연습 시작  ************ //
	@RequestMapping(value = "/test/test_form3.action", method={RequestMethod.GET})
	public String test_form_tiles() {
		
		// ==> tiles의 형태에 따라 1, 2, 3, 4로 나뉨 <==
		// return "member/adult/user.tiles1"; 	
		// /WEB-INF/views/tiles1/member/adult/user.jsp 라고 뷰단 페이지를 만든다는 것.!! a,b까지 폴더. 이후 c.jsp 파일이 들어있다.
		// return "*/*.tiles1";
		// return "*.tiles1";
		
		// return "cart/mycart.tiles2";
		// /WEB-INF/views/tiles2/cart/mycart.jsp
		
		// return "*.tiles2";
		// return "*/*/*.tiles2"; 
		
		return "order/myorder";
		// /WEB-INF/views/order/myorder.jsp
		
	} // end of public String test_form3()
	
	
	
	
	

	// ************ tiles 연습 끝  ************ //
	
	
	// ==== **** spring 기초 끝  **** ==== //
	
}
