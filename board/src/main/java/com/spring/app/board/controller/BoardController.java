package com.spring.app.board.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.CommentVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;
import com.spring.app.board.service.BoardService;
import com.spring.app.common.MyUtil;
import com.spring.app.common.Sha256;


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

// ==== #30. 컨트롤러 선언 ==== //
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
	
	
	
	
	
	@RequestMapping(value = "/test/test_select_vo2.action")
	public String test_select_vo2(HttpServletRequest request) {
		
		List<TestVO2> testvoList = service.test_select_vo2();
		
		request.setAttribute("testvoList", testvoList);
		
		return "test/test_select_vo2";
		
	} // end of public String test_select(HttpServletRequest request)
	
	
	
	
	@RequestMapping(value = "/test/test_select_map.action")
	public String test_select_map(HttpServletRequest request) {
		
		List<Map<String, String>> mapList = service.test_select_map();
		
		request.setAttribute("mapList", mapList);
		
		return "test/test_select_map";
		
	} // end of public String test_select_map(HttpServletRequest request)
	
	
	
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
	
	
	
	
	

		@GetMapping("/test/test_form4_vo2.action")
		public String test_form4_vo2() {
			
			return "test/test_form4_vo2"; 	
			
		} // end of public String test_form4_vo2()
		
		
		
		
		@PostMapping("/test/test_form4_vo2.action")
		public String test_form4_vo2(TestVO2 tvo) {
			
			int n = service.test_insert_vo2(tvo);
			
			if(n == 1) {
				
				return "redirect:/test/test_select_vo2.action";
				
			}
			else {
				return "redirect:/test/test_form4_vo2.action";
				
			}
			
		} // end of public String test_form4_vo2(TestVO tvo)
	
	
		
		
		
		
		
	// method={RequestMethod.POST} 대신 @GetMapping 사용 및 (value=) 지워도 됨
	@GetMapping("/test/test_form5.action")	// GET 방식만 허락해준다.
	public String test_form5() {
		
		return "test/test_form5"; 	
		
	} // end of public String test_form5()
		
	
	
	
	
	@PostMapping("/test/test_form5.action")	// POST 방식만 허락해준다.
	public String test_form5(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
		int n = service.test_insert(paraMap);
		
		
		if(n == 1) {
			
			return "redirect:/test/test_select_map.action";
			
		}
		else {
			return "redirect:/test/test_form5.action";
		}
	} // end of public String test_form5(TestVO tvo)
	
	
	
	
	
	
	
	
	// ======= Ajax 연습시작 ======= //
	
	@GetMapping("/test/test_form6.action")	
	public String test_form6() {
		
		return "test/test_form6"; 	
		
	} // end of public String test_form6()

	
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
	
	
	
	// === return 타입을 String 대신에 ModelAndView 를 사용  시작 === //
	
	@GetMapping(value = "/test/modelandview_select.action")
	public ModelAndView modelandview_select(ModelAndView mav) {
		
		List<TestVO> testvoList = service.test_select();
		
		mav.addObject("testvoList", testvoList);
		// 위의 것은
		// request.setAttribute("testvoList", testvoList); 와 같은 것이다.
		
		mav.setViewName("test/modelandview_select");
		// view 단 페이지의 파일명 지정하기	/WEB-INF/views/test/modelandview_select.jsp 페이지를 만들어야 한다.
		
		return mav;
		
	} // end of public String test_select(HttpServletRequest request)
	
	// === return 타입을 String 대신에 ModelAndView 를 사용  끝 === //
	
	
	
	// ************ tiles 연습 시작  ************ //
	@GetMapping("test/tiles_test1.action")
	public String tiles_test1() {
		
		return "tiles_test1.tiles1";
	//	/WEB-INF/views/tiles1/tiles_test1.jsp 페이지를 만들어야 한다.
		
	} // end of public String tiles_test1()
	
	
	
	@GetMapping("test/tiles_test2.action")
	public String tiles_test2() {
		
		return "test/tiles_test2.tiles1";
	//	/WEB-INF/views/tiles1/test/tiles_test2.jsp 페이지를 만들어야 한다.
		
	} // end of public String tiles_test2()

	
	
	@GetMapping("test/tiles_test3.action")
	public String tiles_test3() {
		
		return "test/sample/tiles_test3.tiles1";
	//	/WEB-INF/views/tiles1/test/sample/tiles_test3.jsp 페이지를 만들어야 한다.
		
	} // end of public String tiles_test3()
	
	
	
	@GetMapping("test/tiles_test4.action")
	public ModelAndView tiles_test4(ModelAndView mav) {
		
		mav.setViewName("tiles_test4.tiles2");
		// /WEB-INF/views/tiles2/tiles_test4.jsp 파일을 만들어야 한다.
		
		return mav;

	} // end of public String tiles_test4()
	
	
	
	@GetMapping("/test/tiles_test5.action")
	public ModelAndView tiles_test5(ModelAndView mav) {
		
		mav.setViewName("test/tiles_test5.tiles2");
		// /WEB-INF/views/tiles2/test/tiles_test5.jsp 파일을 만들어야 한다.
		
		return mav;

	} // end of public String tiles_test5()
	
	
	
	@GetMapping("/test/tiles_test6.action")
	public ModelAndView tiles_test6(ModelAndView mav) {
		
		mav.setViewName("test/sample/tiles_test6.tiles2");
		// /WEB-INF/views/tiles2/test/tiles_test6.jsp 파일을 만들어야 한다.
		
		return mav;

	} // end of public String tiles_test6()
	
	
	
	@GetMapping("/test/tiles_test7.action")
	public ModelAndView tiles_test7(ModelAndView mav) {
		
		mav.setViewName("test7/tiles_test7.tiles3");
		// /WEB-INF/views/tiles3/test7/side.jsp 사이드 파일을 만들어야 한다.
		// /WEB-INF/views/tiles3/test7/content/tiles_test7.jsp 컨텐트 파일을 만들어야 한다.
		
		return mav;

	} // end of public String tiles_test7()
	
	
	
	@GetMapping("/test/tiles_test8.action")
	public ModelAndView tiles_test8(ModelAndView mav) {
		
		mav.setViewName("test8/tiles_test8.tiles3");
		// /WEB-INF/views/tiles3/test8/side.jsp 사이드 파일을 만들어야 한다.
		// /WEB-INF/views/tiles3/test8/content/tiles_test8.jsp 컨텐트 파일을 만들어야 한다.
		
		return mav;

	} // end of public String tiles_test8()
	
	
	
	@GetMapping("/test/tiles_test9.action")
	public ModelAndView tiles_test9(ModelAndView mav) {
		
		mav.setViewName("test9/tiles_test9.tiles4");
		// /WEB-INF/views/tiles4/test9/content/tiles_test9.jsp 파일을 만들어야 한다.
		
		return mav;

	} // end of public String tiles_test9()
	
	// ************ tiles 연습 끝  ************ //
	
	
	// ==== **** spring 기초 끝  **** ==== //
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	// ====== ****** 게시판시작  ****** ====== //
	
	// === #36. 메인 페이지 요청 === //
	// 먼저, com.spring.app.HomeController 클래스에 가서 @Controller 를 주석 처리 한다.
	@GetMapping("/")
	public ModelAndView home(ModelAndView mav) {
		
		mav.setViewName("redirect:/index.action");
		
		return mav;
		
	} // end of public ModelAndView home(ModelAndView mav)
	
	
	
/*	
	@GetMapping("/index.action")
	public ModelAndView index(ModelAndView mav) {

		List<Map<String, String>> imgmapList = service.getImgfilenameList();
		
		mav.addObject("imgmapList", imgmapList);
		
		mav.setViewName("main/index.tiles1");
		// /WEB-INF/views/tiles1/main/index.jsp 페이지
		
		return mav;
		
	} // end of public ModelAndView index(ModelAndView mav)
*/	
	
	// 또는
	@GetMapping("/index.action")
	public ModelAndView index(ModelAndView mav) {
		
		mav = service.index(mav);
		
		return mav;
		
	} // end of public ModelAndView index(ModelAndView mav)
	
	
	
	
	// === #40. 로그인 폼 페이지 요청 === //
	@GetMapping("/login.action")
	public ModelAndView login(ModelAndView mav) {
		
		mav.setViewName("login/loginform.tiles1");
		// /WEB-INF/views/tiles1/login/loginform.jsp 페이지
		
		return mav;
		
	} // end of public ModelAndView login(ModelAndView mav)
	
	
	
/*	
 * 컨트롤러에서 로그인 처리해주기.
	// === #41. 로그인 처리하기 === //
	@PostMapping("/loginEnd.action")
	public ModelAndView loginEnd(ModelAndView mav, HttpServletRequest request) {
		
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		
		// === 클라이언트의 IP 주소를 알아오는 것 === //
		String clientip = request.getRemoteAddr();
				
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", Sha256.encrypt(pwd));
		paraMap.put("clientip", clientip);
		
		MemberVO loginuser = service.getLoginMember(paraMap);
	
		if(loginuser == null) {	// 로그인 실패 시
			
			String message = "아이디 또는 암호가 틀립니다.";
			String loc = "javascript:history.back()";
			
			mav.addObject("message", message);
			mav.addObject("loc", loc);
			
			mav.setViewName("msg");
			// 	/WEB-INF/views/msg.jsp 파일을 생성한다.
			
			return mav;
			
		}
		else {	// 아이디와 암호가 존재하는 경우
			
			if(loginuser.getIdle() == 1) { // 로그인 한지 1년이 경과한 경우
				
				String message = "로그인을 한지 1년이 지나서 휴면상태로 되었습니다.\\n관리자에게 문의 바랍니다.";
				String loc = request.getContextPath()+"/index.action";
				// 원래는 위와 같이 index.action 이 아니라 휴면의 계정을 풀어주는 페이지로 잡아주어야 한다.
				
				mav.addObject("message", message);
				mav.addObject("loc", loc);
				
				mav.setViewName("msg");
				
			}
			else { // 로그인 한지 1년 이내인 경우
			
				HttpSession session = request.getSession();	
				// 메모리에 생성되어져 있는 session 을 불러온다.
				
				session.setAttribute("loginuser", loginuser);
				// session(세션)에 로그인 되어진 사용자 정보인 loginuser 을 키 이름을 "loginuser" 으로 저장시켜두는 것이다.
				
				
				if(loginuser.isRequirePwdChange() == true)	{	// 암호를 마지막으로 변경한 것이 3개월이 경과한 경우
					
					String message = "비밀번호를 변경하신지 3개월이 지났습니다.\\n암호를 변경하시는 것을 추천합니다.";
					String loc = request.getContextPath()+"/index.action";
					// 원래는 위와 같이 index.action 이 아니라 사용자의 비밀번호를 변경해주는 페이지로 잡아주어야 한다.
	               
					mav.addObject("message", message);
					mav.addObject("loc", loc);
	               
					mav.setViewName("msg");
				
				}
				else {	// 암호를 마지막으로 변경한 것이 3개월 이내인 경우
					
					// 로그인을 해야만 접근할 수 있는 페이지에 로그인을 하지 않은 상태에서 접근을 시도한 경우 
		            // "먼저 로그인을 하세요!!" 라는 메시지를 받고서 사용자가 로그인을 성공했다라면
		            // 화면에 보여주는 페이지는 시작페이지로 가는 것이 아니라
		            // 조금전 사용자가 시도하였던 로그인을 해야만 접근할 수 있는 페이지로 가기 위한 것이다.
					String goBackURL = (String)session.getAttribute("goBackURL");
					
					if(goBackURL != null) {
						mav.setViewName("redirect:"+goBackURL);
						session.removeAttribute("goBackURL");	// 세션에서 반드시 제거해주어야 한다.
					}
					else {
						mav.setViewName("redirect:/index.action");	// 시작페이지로 이동
						
					} // end of if(goBackURL != null)
					
				} // end of if(loginuser.isRequirePwdChange() == true)
				
			} // end of if(loginuser.getIdle() == 1)
			
		} // end of if(loginuser == null)
		
		return mav;
		
	} // end of public ModelAndView loginEnd(ModelAndView mav)
*/	
	// === 또는  서비스가 로그인 처리하기 === //
	@PostMapping("/loginEnd.action")
	public ModelAndView loginEnd(ModelAndView mav, HttpServletRequest request) {
		
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		
		// === 클라이언트의 IP 주소를 알아오는 것 === //
		String clientip = request.getRemoteAddr();
				
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", Sha256.encrypt(pwd));
		paraMap.put("clientip", clientip);
		
		mav = service.loginEnd(paraMap, mav, request);		// 사용자가 입력한 값들을 Map 에 담아서 서비스 객체에게 넘겨 처리하도록 한다.
	
		
		return mav;
		
	} // end of public ModelAndView loginEnd(ModelAndView mav)
	
	

/*
	// === #50. 로그아웃 처리하기 === // 
	@GetMapping("/logout.action")
	public ModelAndView logout(ModelAndView mav, HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		session.invalidate();	// 세션을 없앰
		
		String message = "로그아웃 되었습니다.";
		String loc = request.getContextPath()+"/index.action";
		
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
		
	} // end of public ModelAndView logout(ModelAndView mav)
*/
	
	// 또는
	// === #50. 로그아웃 처리하기 === // 
		@GetMapping("/logout.action")
		public ModelAndView logout(ModelAndView mav, HttpServletRequest request) {
			
			mav = service.logout(mav, request);
			
			return mav;
			
		} // end of public ModelAndView logout(ModelAndView mav)
		
		
		
	
	
	// === #51. 게시판 글쓰기 홈페이지 요청 === // 
	@GetMapping("/add.action")
	public ModelAndView requiredLogin_add(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 게시판과 회원관리는 로그인 유무가 중요, 로그인을 해야 사용할 수 있는 기능이다. ==> AOP 사용(CommonAop.java)
		
		mav.setViewName("board/add.tiles1");
		// /WEB-INF/views/tiles1/board/add.jsp 페이지를 만들어야 한다.
		
		return mav;
		
	} // end ofpublic ModelAndView add(ModelAndView mav)
	
	
	
	
	// === #54. 게시판 글쓰기 완료 요청 === //
    @PostMapping("/addEnd.action")
//  public ModelAndView addEnd(ModelAndView mav, BoardVO boardvo) {	// <== After Advice 를 사용하기 전
    public ModelAndView pointPlus_addEnd(Map<String, String> paraMap, ModelAndView mav, BoardVO boardvo) {	// <== After Advice 를 사용하기
    	/*
        	form 태그의 name 명과  BoardVO 의 필드명이 같다라면 
        	request.getParameter("form 태그의 name명"); 을 사용하지 않더라도
                              자동적으로 BoardVO boardvo 에 set 되어진다.
    	*/
    	
    	int n = service.add(boardvo);	// 파일첨부가 없는 글쓰기
    	
    	if(n == 1) {
    		mav.setViewName("redirect:/list.action");	// 글목록 보기 /list.action 페이지로 redirect(페이지이동)해라는 말이다.
    	}
    	else {
            mav.setViewName("board/error/add_error.tiles1");
            //  /WEB-INF/views/tiles1/board/error/add_error.jsp 파일을 생성한다.
        }
    	
    	// ===== #104. After Advice를 사용하기 ====== // 
    	//			       글쓰기를 한 이후에는 회원의 포인트를 100점 올린다.
    	paraMap.put("userid", boardvo.getFk_userid());
    	paraMap.put("point", "100");
    	
    	
    	
    	return mav;
    	
    } // end of public ModelAndView addEnd(ModelAndView mav, BoardVO boardvo)
    	
    	
	
	
    // === #58. 글목록 보기 페이지 요청  === //
	@GetMapping("/list.action")
	public ModelAndView list(ModelAndView mav, HttpServletRequest request) {
		
		List<BoardVO> boardList = null;	// insert 된 것이 하나도 없을 수 있으므로 null
		
		//////////////////////////////////////////////////////
		// === #69. 글조회수(readCount)증가 (DML문 update)는
		//          반드시 목록보기에 와서 해당 글제목을 클릭했을 경우에만 증가되고,
		//          웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 되지 않도록 해야 한다.
		//          이것을 하기 위해서는 session 을 사용하여 처리하면 된다.
		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		/*
        	session 에  "readCountPermission" 키값으로 저장된 value값은 "yes" 이다.
        	session 에  "readCountPermission" 키값에 해당하는 value값 "yes"를 얻으려면 
        	반드시 웹브라우저에서 주소창에 "/list.action" 이라고 입력해야만 얻어올 수 있다. 
		*/
		
		
		//////////////////////////////////////////////////////
		
		// === 페이징 처리를 안한, 검색어가 없는 전체 글목록 보여주기 === //
		// boardList = service.boardListNoSearch();

		// === #110. 페이징 처리를 안한, 검색어가 있는 전체 글목록 보여주기  === //
		/*
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		
		// System.out.println("~~~ 확인용 searchType : " + searchType);
		// System.out.println("~~~ 확인용 searchWord : " + searchWord);
		/*
		  	~~~ 확인용 searchType : null
		  	~~~ 확인용 searchType : subject
			~~~ 확인용 searchType : name
		*/
		/*
			~~~ 확인용 searchWord : null
			~~~ 확인용 searchWord : 연습
			~~~ 확인용 searchWord : 
 		*/
		/*
		if(searchType == null) {
			searchType = "";
		}
		
		if(searchWord == null) {
			searchWord = "";
		}
		
		if(searchWord != null) {
			searchWord = searchWord.trim();
			// "      연습              " ==> "연습"
			// "              " ==> ""
		}
		*/
		/*
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		
		boardList = service.boardListSearch(paraMap);
		*/
		
		
		
		// === #122. 페이징 처리를 한, 검색어가 있는 전체 글목록 보여주기   === //
		
		
		/*  페이징 처리를 통한 글목록 보여주기는 
        
    		예를 들어 3페이지의 내용을 보고자 한다라면 
        	검색을 할 경우는 아래와 같이
 			list.action?searchType=subject&searchWord=안녕&currentShowPageNo=3 와 같이 해주어야 한다.
 
    		또는
 
        	검색이 없는 전체를 볼때는 아래와 같이 
			list.action 또는 
			list.action?searchType=&searchWord=&currentShowPageNo=3 또는 
			list.action?searchType=subject&searchWord=&currentShowPageNo=3 또는
			list.action?searchType=name&searchWord=&currentShowPageNo=3 와 같이 해주어야 한다.
		*/
		
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		String str_currentShowPageNo = request.getParameter("currentShowPageNo");
		// System.out.println("~~ 확인용 str_currentShowPageNo : " + str_currentShowPageNo);
        // ~~ 확인용 str_currentShowPageNo : null 
        // ~~ 확인용 str_currentShowPageNo : 3
        // ~~ 확인용 str_currentShowPageNo : dsfsdfdsfdsfㄴㄹㄴㅇㄹㄴ
        // ~~ 확인용 str_currentShowPageNo : -3412
        // ~~ 확인용 str_currentShowPageNo : 0
        // ~~ 확인용 str_currentShowPageNo : 32546
        // ~~ 확인용 str_currentShowPageNo : 35325234534623463454354534
		
		if(searchType == null) {
			searchType = "";
		}
		
		if(searchWord == null) {
			searchWord = "";
		}
		
		if(searchWord != null) {
			searchWord = searchWord.trim();
			// "      연습              " ==> "연습"
			// "              " ==> ""
		}
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		
		// 먼저, 총 게시물 건수 (totalCount)를 구해와야 한다.
		// 총 게시물 건수는 검색조건이 있을 때와 없을 때로 나뉘어진다.
		int totalCount = 0;				// 총 게시물 건수
		int sizePerPage = 10;			// 한 페이지당 보여줄 게시물 건수
		int currentShowPageNo = 0;		// 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함. 
		int totalPage = 0;				// 총 페이지수(웹브라우저상에서 보여줄 총 페이지 개수, 페이지바)
		
		// 총 게시물 건수 (totalCount)
		totalCount = service.getTotalCount(paraMap);
		// System.out.println("~~~ 확인용  totalCount : " + totalCount);
		// ~~~ 확인용  totalCount : 11
		// java 검색시 ~~~ 확인용  totalCount : 3
		// 글쓴이 검색시 ~~~ 확인용  totalCount : 8
		
		// 만약에 총 게시물 건수(totalCount)가 124 개 이라면 총 페이지수(totalPage)는 13 페이지가 되어야 한다.
        // 만약에 총 게시물 건수(totalCount)가 120 개 이라면 총 페이지수(totalPage)는 12 페이지가 되어야 한다.
		totalPage = (int)Math.ceil((double)totalCount/sizePerPage); 
		// (double)124/10 ==> 12.4	==> Math.ceil(12.4) ==> 13.0	==> 13
		// (double)120/10 ==> 12.0	==> Math.ceil(12.0) ==> 12.0	==> 12
		
		if(str_currentShowPageNo == null) {
			// 게시판에 보여지는 첫 화면
			
			currentShowPageNo = 1;	// 1페이지
			
		}
		else {
			
			try {
				
				currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
				
				if(currentShowPageNo < 1 || currentShowPageNo > totalPage) {
				// get 방식이므로 사용자가 str_currentShowPageNo 에 입력한 값이 0 또는 음수를 입력하여 장난친 경우 
	            // get 방식이므로 사용자가 str_currentShowPageNo 에 입력한 값이 실제 데이터베이스에 존재하는 페이지수 보다 더 큰값을 입력하여 장난친 경우
					
					currentShowPageNo = 1;
					
				}
				
			} catch (NumberFormatException e) {
			// get 방식이므로 사용자가 str_currentShowPageNo 에 입력한 값이 숫자가 아닌 문자를 입력하여 장난친 경우
				
				currentShowPageNo = 1;
				
			} // end of try_catch
			
		} // end of if(str_currentShowPageNo == null)
		
		
		// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
        /*
             currentShowPageNo      startRno     endRno
            --------------------------------------------
                 1 page        ===>    1           10
                 2 page        ===>    11          20
                 3 page        ===>    21          30
                 4 page        ===>    31          40
                 ......                ...         ...
        */
		
		int startRno = ((currentShowPageNo - 1) * sizePerPage) + 1; // 시작 행번호 
        int endRno = startRno + sizePerPage - 1; // 끝 행번호
        
        paraMap.put("startRno", String.valueOf(startRno));
        paraMap.put("endRno", String.valueOf(endRno));
		
		boardList = service.boardListSearch_withPaging(paraMap);
		// 글목록 가져오기 (페이징 처리 했으며, 검색어가 있는 것 또는 검색어 없는 것 모두 포함한 것이다.)
		
		mav.addObject("boardList", boardList);
		
		// 검색 시 검색조건 및 검색어 값 유지시키기
		if("subject".equals(searchType) ||
		   "content".equals(searchType) ||
		   "subject_content".equals(searchType) ||
		   "name".equals(searchType)) {
			
				mav.addObject("paraMap", paraMap);	
				
		}
		
		
		
		// === #129. 페이지바 만들기 === //
		
		int blockSize = 10;
		
		// blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수이다.
		/*
                       	1  2  3  4  5  6  7  8  9 10 [다음][마지막]  -- 1개블럭
         	[맨처음][이전]  11 12 13 14 15 16 17 18 19 20 [다음][마지막]  -- 1개블럭
         	[맨처음][이전]  21 22 23
		*/
		
		int loop = 1;
		
		/*
        	loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		*/
		
		int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
	    
		// *** !! 공식이다. !! *** //
	      
	    /*
	       1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
	       11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
	       21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
	       
	       currentShowPageNo         pageNo
	      ----------------------------------
	            1                      1 = ((1 - 1)/10) * 10 + 1
	            2                      1 = ((2 - 1)/10) * 10 + 1
	            3                      1 = ((3 - 1)/10) * 10 + 1
	            4                      1
	            5                      1
	            6                      1
	            7                      1 
	            8                      1
	            9                      1
	            10                     1 = ((10 - 1)/10) * 10 + 1
	           
	            11                    11 = ((11 - 1)/10) * 10 + 1
	            12                    11 = ((12 - 1)/10) * 10 + 1
	            13                    11 = ((13 - 1)/10) * 10 + 1
	            14                    11
	            15                    11
	            16                    11
	            17                    11
	            18                    11 
	            19                    11 
	            20                    11 = ((20 - 1)/10) * 10 + 1
	            
	            21                    21 = ((21 - 1)/10) * 10 + 1
	            22                    21 = ((22 - 1)/10) * 10 + 1
	            23                    21 = ((23 - 1)/10) * 10 + 1
	            ..                    ..
	            29                    21
	            30                    21 = ((30 - 1)/10) * 10 + 1
	   */
		
		String pageBar = "<ul style='list-style:none;'>";
		String url = "list.action";
		
		// === [맨처음] [이전] 만들기  === //
		if(pageNo != 1) {
			
			pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'>[맨처음]</a></li>";
			pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'>[이전]</a></li>";
			
		}

		
		while( !(loop > blockSize || pageNo > totalPage) ) {
			
			if(pageNo == currentShowPageNo) {
				
				pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; padding:2px 4px;'>"+pageNo+"</li>";
				
			}
			else {
				
				pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
				
			}
			
			loop++;
			pageNo++;
			
		} // end of	while( !(loop > blockSize || pageNo > totalPage) )
		
		
		// === [다음] [마지막] 만들기  === //
		if(pageNo <= totalPage) {
			
			pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>[다음]</a></li>";
			pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'>[마지막]</a></li>";
			
		}
		
		pageBar += "</ul>";
		
		mav.addObject("pageBar", pageBar);
		
		
		// === #131.페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
	    //          사용자가 "검색된결과목록보기" 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
	    //          현재 페이지 주소를 뷰단으로 넘겨준다.
		String goBackURL = MyUtil.getCurrentURL(request);
		// System.out.println("~~~ 확인용(list.action) goBackURL : " + goBackURL);
		/* 
		   ~~~ 확인용(list.action) goBackURL : /list.action
		   ~~~ 확인용(list.action) goBackURL : /list.action?searchType=&searchWord=&currentShowPageNo=5
		   ~~~ 확인용(list.action) goBackURL : /list.action?searchType=subject&searchWord=java
		   ~~~ 확인용(list.action) goBackURL : /list.action?searchType=subject&searchWord=정화&currentShowPageNo=3
		   ~~~ 확인용(list.action) goBackURL : /list.action?searchType=name&searchWord=정화
		*/
		mav.addObject("goBackURL", goBackURL);
		
		
		///////////////////////////////////////////////////////////////////
		
		mav.addObject("totalCount", totalCount);	 // 페이징 처리 시 보여주는 순번을 나타내기 위함
		mav.addObject("currentShowPageNo", currentShowPageNo);	 // 페이징 처리 시 보여주는 순번을 나타내기 위함
		mav.addObject("sizePerPage", sizePerPage);	 // 페이징 처리 시 보여주는 순번을 나타내기 위함
		
		
		///////////////////////////////////////////////////////////////////
		
		
		
		mav.setViewName("board/list.tiles1");
		//  /WEB-INF/views/tiles1/board/list.jsp 파일을 생성한다.
		
		return mav;
		
	} // end of public ModelAndView list(ModelAndView mav)
	
	
	
   
	// === #62. 글 1개를 보여주는 페이지 요청  === //
	//@GetMapping("/view.action")
	@RequestMapping("/view.action")	// === #133. 특정글을 조회한 후 "검색된결과목록보기" 버튼을 클릭했을 때 돌아갈 페이지를 만들기 위함. === //
	public ModelAndView view(ModelAndView mav, HttpServletRequest request) {
		
		String seq = "";
		String goBackURL = "";
		String searchType = "";
		String searchWord = "";
		
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		// redirect 되어서 넘어온 데이터가 있는지 꺼내어 와본다.
		
		if(inputFlashMap != null) {	// redirect 되어서 넘어온 데이터가 있으면
			
			@SuppressWarnings("unchecked")	// 경고 표시를 하지 말라는 뜻이다.
			Map<String, String> redirect_map = (Map<String, String>) inputFlashMap.get("redirect_map");
			// "redirect_map" 값은  /view_2.action 에서  redirectAttr.addFlashAttribute("키", 밸류값); 을 할때 준 "키" 이다. 
	        // "키" 값을 주어서 redirect 되어서 넘어온 데이터를 꺼내어 온다. 
	        // "키" 값을 주어서 redirect 되어서 넘어온 데이터의 값은 Map<String, String> 이므로 Map<String, String> 으로 casting 해준다.

	        // System.out.println("~~~ 확인용 seq : " + redirect_map.get("seq"));
	        
	        // === #143. 이전글제목, 다음글제목 보기 시작  === //
	        searchType = redirect_map.get("searchType");
	        
	        try {
	        	
	              searchWord = URLDecoder.decode(redirect_map.get("searchWord"), "UTF-8"); // 한글데이터가 포함되어 있으면 반드시 한글로 복구해야 한다. 
	              goBackURL = URLDecoder.decode(redirect_map.get("goBackURL"), "UTF-8");   // 한글데이터가 포함되어 있으면 반드시 한글로 복구해야 한다.
	              
	        } catch (UnsupportedEncodingException e) {
	             e.printStackTrace();
	        } 
	        
	        // === #144.   === //
	        
	        
	        
	        
			// System.out.println("~~~ 확인용 goBackURL : " + goBackURL);
			// System.out.println("~~~ 확인용 searchType : " + searchType);
			// System.out.println("~~~ 확인용 searchWord : " + searchWord);
			/*
				~~~ 확인용 seq : 8
				~~~ 확인용 goBackURL : %2Flist.action%3FsearchType%3Dsubject%26searchWord%3Djava
				~~~ 확인용 searchType : subject
				~~~ 확인용 searchWord : java
				--------------------------
				~~~ 확인용 seq : 180
				~~~ 확인용 goBackURL : %2Flist.action%3FsearchType%3Dname%26searchWord%3D%25EC%2597%2584%26currentShowPageNo%3D3
				~~~ 확인용 searchType : name
				~~~ 확인용 searchWord : %EC%97%84
				-------------------------------
				~~~ 확인용 seq : 206
				~~~ 확인용 goBackURL : /list.action?searchType=subject&searchWord=%EC%97%84
				~~~ 확인용 searchType : subject
				~~~ 확인용 searchWord : 엄
			*/
			// === #143. 이전글제목, 다음글제목 보기 끝  === //
			
			
			seq = redirect_map.get("seq");
			
			
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		else {	// redirect 되어서 넘어온 데이터가 아닌 경우
			// == 조회하고자 하는 글번호 받아오기 ==
	        // 글목록보기인 /list.action 페이지에서 특정 글제목을 클릭하여 특정글을 조회해온 경우  
	        // 또는 
	        // 글목록보기인 /list.action 페이지에서 특정 글제목을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는 원본이 form 을 사용해서 POST 방식으로 넘어온 경우이므로 
			// "양식 다시 제출 확인" 이라는 대화상자가 뜨게 되며 "계속" 이라는 버튼을 클릭하면 이전에 입력했던 데이터를 자동적으로 입력해서 POST 방식으로 진행된다. 
			// 그래서  request.getParameter("seq"); 은 null 이 아닌 번호를 입력받아온다.     
	        // 그런데 "이전글제목" 또는 "다음글제목" 을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는 원본이 /view_2.action 을 통해서 redirect 되어진 경우이므로 form 을 사용한 것이 아니라서 
			// "양식 다시 제출 확인" 이라는 alert 대화상자가 뜨지 않는다. 
			// 그래서  request.getParameter("seq"); 은 null 이 된다. 
			seq = request.getParameter("seq");
			// System.out.println("~~~~~~ 확인용 seq : " + seq);
            // ~~~~~~ 확인용 seq : 213
            // ~~~~~~ 확인용 seq : null
			
			
			
			
	        // === #134. 5특정글을 조회한 후 "검색된결과목록보기" 버튼을 클릭했을 때 돌아갈 페이지를 만들기 위함. === //
	        goBackURL = request.getParameter("goBackURL");
	        // System.out.println("~~~ 확인용(view.action) goBackURL :" + goBackURL);
			/*
			 * 잘못된것(get 방식일 경우)
			   ~~~ 확인용(view.action) goBackURL :/list.action?searchType=subject
			   
			 * 올바른것(post 방식일 경우)
			   ~~~ 확인용(view.action) goBackURL :/list.action?searchType=subject&searchWord=%EC%A0%95%ED%99%94&currentShowPageNo=3
			*/
			
	        
	        
	        // >>> 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  시작    <<< // 
 			searchType = request.getParameter("searchType");
 			searchWord = request.getParameter("searchWord");
 			
 			if(searchType == null) {
 				searchType = "";
 			}
 			
 			if(searchWord == null) {
 				searchWord = "";
 			}
 			
 			// System.out.println("~~~ 확인용(view.action)searchType : " + searchType);
 			// System.out.println("~~~ 확인용(view.action)searchWord : " + searchWord);
 			/*
 			  	~~~ 확인용(view.action)searchType : 
 				~~~ 확인용(view.action)searchWord : 
 				
 			 	~~~ 확인용(view.action)searchType : subject
 				~~~ 확인용(view.action)searchWord : 엄정화
 			 */
 			
 			// >>> 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  끝    <<< // 
    
		} // end of if(inputFlashMap != null)
		
		
		mav.addObject("goBackURL", goBackURL);
		
		try {
			
			Integer.parseInt(seq);
			/* 
            	"이전글제목" 또는 "다음글제목" 을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는   
            	원본이 /view_2.action 을 통해서 redirect 되어진 경우이므로 form 을 사용한 것이 아니라서   
            	"양식 다시 제출 확인" 이라는 alert 대화상자가 뜨지 않는다. 
            	그래서  request.getParameter("seq"); 은 null 이 된다. 
            	즉, 글번호인 seq 가 null 이 되므로 DB 에서 데이터를 조회할 수 없게 된다.     
            	또한 seq 는 null 이므로 Integer.parseInt(seq); 을 하면  NumberFormatException 이 발생하게 된다. 
		    */
			HttpSession session = request.getSession();
			MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
			
			String login_userid = null;
			if(loginuser != null) {
				login_userid = loginuser.getUserid();
				// login_userid 는 로그인 되어진 사용자의 userid 이다.
			}
			
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			paraMap.put("login_userid", login_userid);
			
			// >>> 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  시작    <<< // 
			paraMap.put("searchType", searchType);
			paraMap.put("searchWord", searchWord);
			// >>> 글목록에서 검색되어진 글내용일 경우 이전글제목, 다음글제목은 검색되어진 결과물내의 이전글과 다음글이 나오도록 하기 위한 것이다.  끝   <<< // 
			
			
			// === #68. !!! 중요 !!! 
            //     글1개를 보여주는 페이지 요청은 select 와 함께 
	        //     DML문(지금은 글조회수 증가인 update문)이 포함되어져 있다.
	        //     이럴경우 웹브라우저에서 페이지 새로고침(F5)을 했을때 DML문이 실행되어
	        //     매번 글조회수 증가가 발생한다.
	        //     그래서 우리는 웹브라우저에서 페이지 새로고침(F5)을 했을때는
	        //     단순히 select만 해주고 DML문(지금은 글조회수 증가인 update문)은 
	        //     실행하지 않도록 해주어야 한다. !!! === //
			
			// 위의 글목록보기 #69. 에서 session.setAttribute("readCountPermission", "yes"); 해두었다.
			BoardVO boardvo = null; 
			
			if("yes".equals((String)session.getAttribute("readCountPermission"))) {
				// 글목록보기인 /list.action 페이지를 클릭한 다음에 특정글을 조회해온 경우이다.
				
				boardvo = service.getView(paraMap);
				// 글 조회수 증가와 함께 글 1개를 조회해오는 것
				// System.out.println("~~ 확인용 글내용 : " + boardvo.getContent());
				
				session.removeAttribute("readCountPermission");
				// 중요함!! session 에 저장된 readCountPermission 을 삭제한다. 
			
			}
			else {
				// 글목록에서 특정 글제목을 클릭하여 본 상태에서
                // 웹브라우저에서 새로고침(F5)을 클릭한 경우이다.
				// System.out.println("글목록에서 특정 글제목을 클릭하여 본 상태에서 웹브라우저에서 새로고침(F5)을 클릭한 경우");
				
				boardvo = service.getView_no_increase_readCount(paraMap);
				// 글 조회수 증가는 없고, 단순히 글 1개를 조회해오는 것
				
			/*
				// 또는 redirect 해주기 (예 : 버거킹 www.burgerking.co.kr 메뉴소개)
				mav.setViewName("redirect:/list.action");
				return mav;
			*/
			}
			
			if(boardvo == null) {
	    		mav.setViewName("redirect:/list.action");	// 글목록 보기 /list.action 페이지로 redirect(페이지이동)해라는 말이다.
	    		return mav;
	    	}
	    
			mav.addObject("boardvo", boardvo);
			
			// === #140. 이전글제목, 다음글제목 보기 === //
			mav.addObject("paraMap", paraMap);
			
			
			mav.setViewName("board/view.tiles1");
			// /WEB-INF/views/tiles1/board/view.jsp 파일을 생성한다.
			
			
			
		} catch (NumberFormatException e) {
			
			mav.setViewName("redirect:/list.action");
			
		}
		
		return mav;
		
	} // end of public ModelAndView view(ModelAndView)
	


	
	
//	@GetMapping("/view_2.action")
	@PostMapping("/view_2.action")
	public ModelAndView view_2(ModelAndView mav, HttpServletRequest request, RedirectAttributes redirectAttr) {
		
		// 조회하고자 하는 글번호 받아오기
		String seq = request.getParameter("seq");
		
		// === #141. 이전글제목, 다음글제목 보기 시작 === //
		String goBackURL = request.getParameter("goBackURL");
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		
		/* 
        	redirect:/ 를 할때 "한글데이터는 0에서 255까지의 허용 범위 바깥에 있으므로 인코딩될 수 없습니다" 라는 
        	java.lang.IllegalArgumentException 라는 오류가 발생한다.
         	이것을 방지하려면 아래와 같이 하면 된다.
		*/
		try {
	         searchWord = URLEncoder.encode(searchWord, "UTF-8");
	         goBackURL = URLEncoder.encode(goBackURL, "UTF-8");
	         
	      //   System.out.println("~~~~ view_2.action 의  URLEncoder.encode(searchWord, \"UTF-8\") : " + searchWord);
	            //  ~~~~ view_2.action 의  URLEncoder.encode(searchWord, "UTF-8") : %EC%84%9C%EC%98%81%ED%95%99
	         
	      //   System.out.println("~~~~ view_2.action 의  URLEncoder.encode(goBackURL, \"UTF-8\") : " + goBackURL);
	          //   ~~~~ view_2.action 의  URLEncoder.encode(goBackURL, "UTF-8") : %2Flist.action%3FsearchType%3Dname+searchWord%3D%25EC%2584%259C%25EC%2598%2581%25ED%2595%2599+currentShowPageNo%3D11 
	         
	      //   System.out.println(URLDecoder.decode(searchWord, "UTF-8")); // URL인코딩 되어진 한글을 원래 한글모양으로 되돌려주는 것임.
	          //   손혜정
	         
	      //   System.out.println(URLDecoder.decode(goBackURL, "UTF-8"));  // URL인코딩 되어진 한글을 원래 한글모양으로 되돌려주는 것임.
	          //  /list.action?searchType=name searchWord=%EC%84%9C%EC%98%81%ED%95%99 currentShowPageNo=11
	         
	    } catch (UnsupportedEncodingException e) {
	         e.printStackTrace();
	    }
		// === #141. 이전글제목, 다음글제목 보기 끝 === //
		
		
		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		
		// ==== redirect(GET방식임)시 데이터를 넘길때 GET 방식이 아닌 POST 방식처럼 데이터를 넘기려면 RedirectAttributes 를 사용하면 된다. 시작 ==== //
	    /////////////////////////////////////////////////////////////////////////////////
		
		Map<String, String> redirect_map = new HashMap<>();
		redirect_map.put("seq", seq);
		
		// === #142. 이전글제목, 다음글제목 보기 시작 === //
		redirect_map.put("goBackURL", goBackURL);
		redirect_map.put("searchType", searchType);
		redirect_map.put("searchWord", searchWord);
		// === #142. 이전글제목, 다음글제목 보기 끝 === //
		
		redirectAttr.addFlashAttribute("redirect_map", redirect_map);
		// redirectAttr.addFlashAttribute("키", 밸류값); 으로 사용하는데 오로지 1개의 데이터만 담을 수 있으므로 여러개의 데이터를 담으려면 Map 을 사용해야 한다.
		
		mav.setViewName("redirect:/view.action");	// 실제로 redirect:/view.action 은 POST 방식이 아닌 GET 방식이다.
		
		/////////////////////////////////////////////////////////////////////////////////
		// ==== redirect(GET방식임)시 데이터를 넘길때 GET 방식이 아닌 POST 방식처럼 데이터를 넘기려면 RedirectAttributes 를 사용하면 된다. 끝 ==== //
		
		return mav;
		
	} // end of public ModelAndView view_2(ModelAndView mav, HttpServletRequest request, RedirectAttributes redirectAttr)
	
	
	
	
	
	// === #71. 글을 수정하는 페이지 요청 === // 
	@GetMapping("/edit.action")
	public ModelAndView requiredLogin_edit(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 글 수정해야 할 글번호 가져오기
		String seq = request.getParameter("seq");
		String message = "";
		
		try {
			
			Integer.parseInt(seq);
			
			// 글 수정해야 할 글 1개 내용 가져오기
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			
			BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
			// 글 조회수 증가는 없고, 단순히 글 1개를 조회해오는 것
			
			if(boardvo == null) {
				
				message = "글 수정이 불가합니다.";
				
			}
			else {
				
				HttpSession session = request.getSession();
				MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
				
				if(!loginuser.getUserid().equals(boardvo.getFk_userid())) {	// 로그인한 아이디와 글쓴이 아이디가 일치하지 않는다면
					
					message = "다른 사용자의 글은 수정이 불가합니다.";
					
				}
				else {	// 내가 쓴 글이라면, 가져온 1개 글을 글 수정할 폼이 있는 view 단으로 보내준다.
					
					mav.addObject("boardvo", boardvo);
					mav.setViewName("board/edit.tiles1");
					
					return mav;
					
				} // end of (!loginuser.getUserid().equals(boardvo.getFk_userid()))
				
			} // end of if(boardvo == null)
			
			
		} catch (NumberFormatException e) {
			
			message = "글 수정이 불가합니다.";
			
		}
		
		String loc = "javascript:history.back()";
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
		
	} // end of public ModelAndView requiredLogin_edit(HttpServletRequest request, HttpServletResponse response, ModelAndView mav)
	
	
	
	
	// === #72. 글을 수정하는 페이지 완료하기 === //
	@PostMapping("/editEnd.action")
	public ModelAndView editEnd(ModelAndView mav, BoardVO boardvo, HttpServletRequest request) {
		
		int n = service.edit(boardvo);
		
		if(n == 1) {
			mav.addObject("message", "글 수정 성공!!");
			mav.addObject("loc", request.getContextPath()+"/view.action?seq="+boardvo.getSeq());
			mav.setViewName("msg");
		}
		
		return mav;
		
	} // end of public ModelAndView editEnd(ModelAndView mav)
	
	
	
	
	
	// === #76. 글 삭제하기 페이지 요청 ===
	@GetMapping("/del.action")
	public ModelAndView requiredLogin_del(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {

		// 삭제할 글번호 가져오기
		String seq = request.getParameter("seq");
      
		String message = "";
      
		try {
			Integer.parseInt(seq);
         
			// 삭제할 글 1개의 내용 가져오기
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
         
			BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
			// 조회수 증가 없이 단순히 글 1개만 조회해오는 것
         
			if(boardvo == null) {
				message = "글 삭제가 불가합니다.";
            
			} else {
            
				HttpSession session = request.getSession();
				MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
            
			if(!loginuser.getUserid().equals(boardvo.getFk_userid())) { // 현재 로그인한 사용자가 글을 작성한 사용자와 같지 않다면
				message = "다른 사용자의 글은 삭제가 불가합니다.";
               
            } else {
            	// 자신의 글을 삭제할 경우
            	// 가져온 1개의 글을 '글 삭제하기' 폼이 있는 view단으로 보내준다.
            	mav.addObject("boardvo", boardvo);
            	mav.setViewName("board/del.tiles1");
            	// /WEB-INF/views/tiles1/board/del.jsp 파일을 생성한다.
               
               	return mav;
               	
            	}
			}
         
		} catch (NumberFormatException e) {
			message = "글 삭제가 불가합니다.";
		}
      
		String loc = "javascript:history.back()";
	  
		mav.addObject("message", message);
		mav.addObject("loc", loc);
	  
		mav.setViewName("msg"); // /WEB-INF/views/msg.jsp
	  
		return mav;
      
	} // end of public ModelAndView requiredLogin_del

	
	
	
	
	
	
	// === #72. 글을 삭제하는 페이지 완료하기 === //
	@PostMapping("/delEnd.action")
	public ModelAndView delEnd(ModelAndView mav, HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		
		int n = service.del(seq);
		
		if(n == 1) {
			mav.addObject("message", "글 삭제가 완료되었습니다.");
			mav.addObject("loc", request.getContextPath()+"/list.action");
			mav.setViewName("msg");
		}
		
		return mav;
		
	} // end of public ModelAndView editEnd(ModelAndView mav)

	
	
	
	
	// === #84. 댓글 쓰기(Ajax로 처리) === //
	@ResponseBody	// 결과물 그대로 웹에 보여주기 위함 ==> String
	@PostMapping(value="/addComment.action", produces="text/plain;charset=UTF-8")
	public String addComment(CommentVO commentvo) {
		
		int n = 0;
		
		// 댓글쓰기에 첨부파일이 없는 경우
		try {
			
			n = service.addComment(commentvo);
			// 댓글쓰기(insert) 및 원게시물(tbl_board 테이블)에 댓글의 개수 증가(update 1씩 증가)하기 
	        // 이어서 회원의 포인트를 50점을 증가하도록 한다. (tbl_member 테이블에 point 컬럼의 값을 50 증가하도록 update 한다.)
			
		} catch (Throwable e) {
		
			e.printStackTrace();
		}
		
		JSONObject jsonObj = new JSONObject();		// {}
		jsonObj.put("n", n);						// {"n":1} 또는 {"n":0}
		jsonObj.put("name", commentvo.getName());	// {"n":1, "name":"엄정화"}또는 {"n":0, "name":"손혜정"}
		
		
		return jsonObj.toString();	// "{"n":1, "name":"엄정화"}"또는 "{"n":0, "name":"손혜정"}"
		
	} // end of public String addComment(CommentVO commentvo)
	
	
	
	
	
	// === #90. 원 게시물에 딸린 댓글들을 조회해오기(Ajax로 처리) === //
	@ResponseBody
	@GetMapping(value="/readComment.action", produces="text/plain;charset=UTF-8")
	public String readComment(HttpServletRequest request) {
		
		String parentSeq = request.getParameter("parentSeq");
		
		List<CommentVO> commentList = service.getCommentList(parentSeq);
		
		JSONArray jsonArr = new JSONArray();	// []
		
		if(commentList != null) {	// 댓글이 있는 경우
			
			for(CommentVO cmtvo : commentList) {
				
				JSONObject jsonObj = new JSONObject();			// {}
				jsonObj.put("seq", cmtvo.getSeq());				// {"seq":4}
				jsonObj.put("fk_userid", cmtvo.getFk_userid());	// {"seq":4, "userid":"ejss0125"}
				jsonObj.put("name", cmtvo.getName());			// {"seq":4, "userid":"ejss0125", "name":"손혜정"}	
				jsonObj.put("content", cmtvo.getContent());		// {"seq":4, "userid":"ejss0125","name":"손혜정", "content":"나도갈래"}
				jsonObj.put("regdate", cmtvo.getRegDate() );	// {"seq":4, "userid":"ejss0125","name":"손혜정", "content":"나도갈래", "regdate":"2024-06-18 15:36:41"}
				
				jsonArr.put(jsonObj);
				
			} // end of for(CommentVO cmtvo : commentList)
		}
		
		return jsonArr.toString();	// "[{"seq":4, "userid":"ejss0125","name":"손혜정", "content":"나도갈래", "regdate":"2024-06-18 15:36:41"}]"
									// 또는
									// "[]"
		
	} // end of public String readComment
	
	
	
	
	
	// === #95. 댓글 수정(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/updateComment.action", produces="text/plain;charset=UTF-8")
	public String updateComment(HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		String content = request.getParameter("content");

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("content", content);
		
		int n = service.updateComment(paraMap);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		
		return jsonObj.toString();
		
	} // end of public String updateComment
	
	
	
	
	
	// === #100. 댓글 삭제(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/deleteComment.action", produces="text/plain;charset=UTF-8")
	public String deleteComment(HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		String parentSeq = request.getParameter("parentSeq");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("parentSeq", parentSeq);
		
		int n = 0;
		
		try {
			n = service.deleteComment(paraMap);
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		return jsonObj.toString();
		
	} // end of public String deleteComment
	
	
	
	
	// === #116. 검색어 입력시 자동글 완성하기 2  === //
	@ResponseBody
	@GetMapping(value="/wordSearchShow.action", produces="text/plain;charset=UTF-8")
	public String wordSearchShow(HttpServletRequest request) {
		
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		
		List<String> wordList = service.wordSearchShow(paraMap);
		
		JSONArray jsonArr = new JSONArray();	// []
		
		if(wordList != null) {
			
			for(String word : wordList) {
				
				JSONObject jsonObj = new JSONObject();	// {}
				jsonObj.put("word", word);
				
				jsonArr.put(jsonObj);	// [{}, {}, {}]
				
			} // end of for
			
		} // end of if(wordList != null)
		
		return jsonArr.toString();
		
	} // end of public String wordSearchShow
	
	
	
	
	// === #146. 원게시물에 딸린 댓글내용들을 페이징 처리하기 (Ajax로 처리)  === //
	@ResponseBody
	@GetMapping(value="/commentList.action", produces="text/plain;charset=UTF-8")
	public String commentList(HttpServletRequest request) {
		
		String parentSeq = request.getParameter("parentSeq");
		String currentShowPageNo = request.getParameter("currentShowPageNo");
		
		if(currentShowPageNo == null) {
			currentShowPageNo = "1";	// 1 페이지로
		}
		
		int sizePerPage = 5; 	// 한 페이지당 5개의 댓글을 보여줄 것
		
		
		// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
        /*
             currentShowPageNo      startRno     endRno
            --------------------------------------------
                 1 page        ===>    1           10
                 2 page        ===>    11          20
                 3 page        ===>    21          30
                 4 page        ===>    31          40
                 ......                ...         ...
        */
		
		int startRno = ((Integer.parseInt(currentShowPageNo) - 1) * sizePerPage) + 1; // 시작 행번호 
        int endRno = startRno + sizePerPage - 1; // 끝 행번호
        
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("parentSeq", parentSeq);
        paraMap.put("startRno", String.valueOf(startRno));
        paraMap.put("endRno", String.valueOf(endRno));
		
		List<CommentVO> commentList = service.getCommentList_paging(paraMap);
		int totalCount = service.getCommentTotalCount(parentSeq);	// 페이징 처리시 보여주는 순번을 나타내기 위한 것
		
		JSONArray jsonArr = new JSONArray();	// []
		
		if(commentList != null) {	// 댓글이 있는 경우
			
			for(CommentVO cmtvo : commentList) {
				
				JSONObject jsonObj = new JSONObject();			// {}
				jsonObj.put("seq", cmtvo.getSeq());				// {"seq":4}
				jsonObj.put("fk_userid", cmtvo.getFk_userid());	// {"seq":4, "userid":"ejss0125"}
				jsonObj.put("name", cmtvo.getName());			// {"seq":4, "userid":"ejss0125", "name":"손혜정"}	
				jsonObj.put("content", cmtvo.getContent());		// {"seq":4, "userid":"ejss0125","name":"손혜정", "content":"나도갈래"}
				jsonObj.put("regdate", cmtvo.getRegDate() );	// {"seq":4, "userid":"ejss0125","name":"손혜정", "content":"나도갈래", "regdate":"2024-06-18 15:36:41"}
				
				jsonObj.put("sizePerPage", sizePerPage);		// 페이징 처리시 보여주는 순번을 나타내기 위한 것
				jsonObj.put("totalCount", totalCount);			// 페이징 처리시 보여주는 순번을 나타내기 위한 것
				
				jsonArr.put(jsonObj);
				
			} // end of for(CommentVO cmtvo : commentList)
		}
		
		
		return jsonArr.toString();	// "[{"seq":4, "userid":"ejss0125","name":"손혜정", "content":"나도갈래", "regdate":"2024-06-18 15:36:41"}]"
									// 또는
									// "[]"
		
	} // end of public String commentList
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
