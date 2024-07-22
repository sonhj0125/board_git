package com.spring.app.board.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.CommentVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.Seoul_bicycle_rental_VO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;
import com.spring.app.board.service.BoardService;
import com.spring.app.common.FileManager;
import com.spring.app.common.GoogleMail;
import com.spring.app.common.MyUtil;
import com.spring.app.common.Sha256;

/*
	사용자 웹브라우저 요청(View)  ==> DispatcherServlet ==> @Controller 클래스 <==>> Service단(핵심업무로직단, business logic단) <==>> Model단[Repository](DAO, DTO) <==>> myBatis <==>> DB(오라클)           
	(http://...  *.action)                                  |                                                                                                                              
	 ↑                                                View Resolver
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

// ==== #30. 컨트롤러 선언 ====
// @Component
/* XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다. 
      그리고 bean의 이름(첫글자는 소문자)은 해당 클래스명이 된다. 
      즉, 여기서 bean의 이름은 boardController 이 된다. 
      여기서는 @Controller 를 사용하므로 @Component 기능이 이미 있으므로 @Component를 명기하지 않아도 BoardController 는 bean 으로 등록되어 스프링컨테이너가 자동적으로 관리해준다. 
*/
@Controller
public class BoardController {

	// === #35. 의존객체 주입하기(DI: Dependency Injection) ===
    // ※ 의존객체주입(DI : Dependency Injection) 
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
	
	//  IOC(Inversion of Control) 란 ?
	//  ==> 스프링 프레임워크는 사용하고자 하는 객체를 빈형태로 이미 만들어 두고서 컨테이너(Container)에 넣어둔후
	//      필요한 객체사용시 컨테이너(Container)에서 꺼내어 사용하도록 되어있다.
	//      이와 같이 객체 생성 및 소멸에 대한 제어권을 개발자가 하는것이 아니라 스프링 Container 가 하게됨으로써 
	//      객체에 대한 제어역할이 개발자에게서 스프링 Container로 넘어가게 됨을 뜻하는 의미가 제어의 역전 
	//      즉, IOC(Inversion of Control) 이라고 부른다.
	
	
	//  === 느슨한 결합 ===
	//      스프링 컨테이너가 BoardController 클래스 객체에서 BoardService 클래스 객체를 사용할 수 있도록 
	//      만들어주는 것을 "느슨한 결합" 이라고 부른다.
	//      느스한 결합은 BoardController 객체가 메모리에서 삭제되더라도 BoardService service 객체는 메모리에서 동시에 삭제되는 것이 아니라 남아 있다.
	
	// ===> 단단한 결합(개발자가 인스턴스 변수 객체를 필요에 의해서 생성해주던 것)
	// private InterBoardService service = new BoardService(); 
	// ===> BoardController 객체가 메모리에서 삭제 되어지면  BoardService service 객체는 멤버변수(필드)이므로 메모리에서 자동적으로 삭제되어진다.
	
	@Autowired  // Type에 따라 알아서 Bean 을 주입해준다.
	private BoardService service;
	
	
	// === #175. 파일 업로드 및 파일 다운로드를 해주는 FileManager 클래스 의존객체 주입하기(DI : Dependency Injection) ===
	@Autowired  // Type에 따라 알아서 Bean 을 주입해준다.
	private FileManager fileManager;
	
	
	// ==== **** spring 기초 시작  **** ===== //
	@RequestMapping(value = "/test/test_insert.action")  // http://localhost:9099/board/test/test_insert.action 
	public String test_insert(HttpServletRequest request) {
		
		int n = service.test_insert();
		
		String message = "";
		
		if(n==1) {
			message = "데이터 입력 성공!!";
		}
		else {
			message = "데이터 입력 실패!!";
		}
		
		request.setAttribute("message", message);
		request.setAttribute("n", n);
		
		return "test/test_insert";  
		//   /WEB-INF/views/test/test_insert.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value = "/test/test_select.action")  // http://localhost:9099/board/test/test_select.action 
	public String test_select(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.test_select();
		
		request.setAttribute("testvoList", testvoList);
		
		return "test/test_select";
		//   /WEB-INF/views/test/test_select.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value = "/test/test_select_vo2.action")  // http://localhost:9099/board/test/test_select.action 
	public String test_select_vo2(HttpServletRequest request) {
		
		List<TestVO2> testvoList = service.test_select_vo2();
		
		request.setAttribute("testvoList", testvoList);
		
		return "test/test_select_vo2";
		//   /WEB-INF/views/test/test_select_vo2.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value = "/test/test_select_map.action")   
	public String test_select_map(HttpServletRequest request) {
		
		List<Map<String, String>> mapList = service.test_select_map();
		
		request.setAttribute("mapList", mapList);
		
		return "test/test_select_map";
		//   /WEB-INF/views/test/test_select_map.jsp 페이지를 만들어야 한다.
	}
	
	
 //	@RequestMapping(value = "/test/test_form1.action", method= {RequestMethod.GET})  // 오로지 GET방식만 허락하는 것임.
 //	@RequestMapping(value = "/test/test_form1.action", method= {RequestMethod.POST}) // 오로지 POST방식만 허락하는 것임.
	@RequestMapping(value = "/test/test_form1.action") // GET방식 또는 POST방식 둘 모두 허락하는 것임.
	public String test_form1(HttpServletRequest request) {
		
		String method = request.getMethod();
		
		if("GET".equalsIgnoreCase(method)) { // GET 방식이라면
			return "test/test_form1"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form1.jsp 페이지를 만들어야 한다.
		}
		else { // POST 방식이라면
			String no = request.getParameter("no");
			String name = request.getParameter("name");
			
			TestVO tvo = new TestVO();
			tvo.setNo(no);
			tvo.setName(name);
			
			int n = service.test_insert(tvo);
			
			if(n==1) {
				return "redirect:/test/test_select.action";
				//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
			else {
				return "redirect:/test/test_form1.action";
				//  /test/test_form1.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
		}
	}
	
	
	@RequestMapping(value = "/test/test_form2.action") // GET방식 또는 POST방식 둘 모두 허락하는 것임.
	public String test_form2(HttpServletRequest request, TestVO tvo) {
		
		String method = request.getMethod();
		
		if("GET".equalsIgnoreCase(method)) { // GET 방식이라면
			return "test/test_form2"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form2.jsp 페이지를 만들어야 한다.
		}
		else { // POST 방식이라면
			
			int n = service.test_insert(tvo);
			
			if(n==1) {
				return "redirect:/test/test_select.action";
				//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
			else {
				return "redirect:/test/test_form2.action";
				//  /test/test_form2.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
		}
	}
	
	
	@RequestMapping(value = "/test/test_form3.action", method= {RequestMethod.GET}) // GET방식 만 허락하는 것임.
	public String test_form3() {
		
		return "test/test_form3"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form3.jsp 페이지를 만들어야 한다.
	}
		
	@RequestMapping(value = "/test/test_form3.action", method= {RequestMethod.POST}) // POST방식 만 허락하는 것임.
	public String test_form3(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		if(n==1) {
			return "redirect:/test/test_select.action";
			//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			return "redirect:/test/test_form3.action";
			//  /test/test_form3.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
	}
	
	
	@GetMapping("/test/test_form4.action") // GET방식 만 허락하는 것임.
	public String test_form4() {
		
		return "test/test_form4"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form4.jsp 페이지를 만들어야 한다.
	}
		
	@PostMapping("/test/test_form4.action") // POST방식 만 허락하는 것임.
	public String test_form4(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		if(n==1) {
			return "redirect:/test/test_select.action";
			//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			return "redirect:/test/test_form4.action";
			//  /test/test_form4.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
	}	
	
	
	@GetMapping("/test/test_form4_vo2.action") 
	public String test_form4_vo2() {
		
		return "test/test_form4_vo2"; 
	}
		
	@PostMapping("/test/test_form4_vo2.action") 
	public String test_form4_vo2(TestVO2 tvo) {
		
		int n = service.test_insert_vo2(tvo);
		
		if(n==1) {
			return "redirect:/test/test_select_vo2.action";
			
		}
		else {
			return "redirect:/test/test_form4_vo2.action";
		}
	}
	
	
	@GetMapping("/test/test_form5.action") // GET방식 만 허락하는 것임.
	public String test_form5() {
		
		return "test/test_form5"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form5.jsp 페이지를 만들어야 한다.
	}
		
	@PostMapping("/test/test_form5.action") // POST방식 만 허락하는 것임.
	public String test_form5(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
		int n = service.test_insert(paraMap);
		
		if(n==1) {
			return "redirect:/test/test_select_map.action";
			//  /test/test_select_map.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			return "redirect:/test/test_form5.action";
			//  /test/test_form5.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
	}
	
	
	// ======= Ajax  연습시작  ======= //
	@GetMapping("/test/test_form6.action") 
	public String test_form6() {
		
		return "test/test_form6"; 
	}
	
	
  /*
    @ResponseBody 란?
	  메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단 페이지를 통해서 출력되는 것이 아니라 
	 return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON 값을 Return 할때 많이 사용된다.
  */ 
	@ResponseBody
	@PostMapping("/test/ajax_insert.action")
	public String ajax_insert(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		JSONObject jsonObj = new JSONObject(); // {}
		jsonObj.put("n", n); // {"n":1}
		
		return jsonObj.toString();  // "{"n":1}" 
	}
	
	
	/*
    @ResponseBody 란?
	  메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단 페이지를 통해서 출력되는 것이 아니라 
	 return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON 값을 Return 할때 많이 사용된다.
	 
    >>> 스프링에서 json 또는 gson을 사용한 ajax 구현시 데이터를 화면에 출력해 줄때 한글로 된 데이터가 '?'로 출력되어 한글이 깨지는 현상이 있다. 
               이것을 해결하는 방법은 @RequestMapping 어노테이션의 속성 중 produces="text/plain;charset=UTF-8" 를 사용하면 
               응답 페이지에 대한 UTF-8 인코딩이 가능하여 한글 깨짐을 방지 할 수 있다. <<< 
    */	
	@ResponseBody
	@GetMapping(value="/test/ajax_select.action", produces="text/plain;charset=UTF-8")
	public String ajax_select() {
		
		List<TestVO> testvoList = service.test_select();
		
		JSONArray jsonArr = new JSONArray(); //  [] 
		
		if(testvoList != null) {
			for(TestVO vo : testvoList) {
				JSONObject jsonObj = new JSONObject();     // {} 
				jsonObj.put("no", vo.getNo());             // {"no":"101"} 
				jsonObj.put("name", vo.getName());         // {"no":"101", "name":"이순신"}
				jsonObj.put("writeday", vo.getWriteday()); // {"no":"101", "name":"이순신", "writeday":"2024-06-11 17:27:09"}
				
				jsonArr.put(jsonObj); // [{"no":"101", "name":"이순신", "writeday":"2024-06-11 17:27:09"}]
			}// end of for------------------------
		}
		
		return jsonArr.toString(); // "[{"no":"101", "name":"이순신", "writeday":"2024-06-11 17:27:09"}]" 
		                           // 또는 "[]"
	}
	// ======= Ajax  연습끝  ======= //
	
	
	// === return 타입을  String 대신에 ModelAndView 를 사용해 보겠습니다. 시작  === //
	@GetMapping(value = "/test/modelandview_select.action")   
	public ModelAndView modelandview_select(ModelAndView mav) {
		
		List<TestVO> testvoList = service.test_select();
		
		mav.addObject("testvoList", testvoList);
	//  위의 것은	
	//	request.setAttribute("testvoList", testvoList); 와 같은 것이다. 
		
		mav.setViewName("test/modelandview_select");
	//  view 단 페이지의 파일명 지정하기
	//	/WEB-INF/views/test/modelandview_select.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	// === return 타입을  String 대신에 ModelAndView 를 사용해 보겠습니다. 끝  === //
	
	
	// *******   tiles 연습 시작     ******* //
	@GetMapping("/test/tiles_test1.action")
	public String tiles_test1() {
		
		return "tiles_test1.tiles1";
	    //	/WEB-INF/views/tiles1/tiles_test1.jsp 페이지를 만들어야 한다.
		
	}
	
	
	@GetMapping("/test/tiles_test2.action")
	public String tiles_test2() {
		
		return "test/tiles_test2.tiles1";
	    //	/WEB-INF/views/tiles1/test/tiles_test2.jsp 페이지를 만들어야 한다.
		
	}
	
	
	@GetMapping("/test/tiles_test3.action")
	public String tiles_test3() {
		
		return "test/sample/tiles_test3.tiles1";
	    // /WEB-INF/views/tiles1/test/sample/tiles_test3.jsp 페이지를 만들어야 한다.
		
	}
	
	
	@GetMapping("/test/tiles_test4.action")
	public ModelAndView tiles_test4(ModelAndView mav) {
		
		mav.setViewName("tiles_test4.tiles2");
		//  /WEB-INF/views/tiles2/tiles_test4.jsp 페이지를 만들어야 한다.
		
		return mav;
	    
	}
	
	
	@GetMapping("/test/tiles_test5.action")
	public ModelAndView tiles_test5(ModelAndView mav) {
		
		mav.setViewName("test/tiles_test5.tiles2");
		//  /WEB-INF/views/tiles2/test/tiles_test5.jsp 페이지를 만들어야 한다.
		
		return mav;
	    
	}
	
	@GetMapping("/test/tiles_test6.action")
	public ModelAndView tiles_test6(ModelAndView mav) {
		
		mav.setViewName("test/sample/tiles_test6.tiles2");
		//  /WEB-INF/views/tiles2/test/sample/tiles_test6.jsp 페이지를 만들어야 한다.
		
		return mav;
	    
	}
	
	
	@GetMapping("/test/tiles_test7.action")
	public ModelAndView tiles_test7(ModelAndView mav) {
		
		mav.setViewName("test7/tiles_test7.tiles3");
		//  /WEB-INF/views/tiles3/test7/side.jsp 페이지를 만들어야 한다.
		//  /WEB-INF/views/tiles3/test7/content/tiles_test7.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	@GetMapping("/test/tiles_test8.action")
	public ModelAndView tiles_test8(ModelAndView mav) {
		
		mav.setViewName("test8/tiles_test8.tiles3");
		//  /WEB-INF/views/tiles3/test8/side.jsp 페이지를 만들어야 한다.
		//  /WEB-INF/views/tiles3/test8/content/tiles_test8.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	@GetMapping("/test/tiles_test9.action")
	public ModelAndView tiles_test9(ModelAndView mav) {
		
		mav.setViewName("test9/tiles_test9.tiles4");
		// /WEB-INF/views/tiles4/test9/content/tiles_test9.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	// *******   tiles 연습 끝     ******* //
	
	// ==== **** spring 기초 끝  **** ===== //
	
	/////////////////////////////////////////////////////
	
	
	
	
	// ======= ****** 게시판 시작 ****** ======== //
	
	// === #36. 메인 페이지 요청 === //
	// 먼저, com.spring.app.HomeController 클래스에 가서 @Controller 을 주석처리한다.
	@GetMapping("/")
	public ModelAndView home(ModelAndView mav) {
	    mav.setViewName("redirect:/index.action");
		return mav;
	}
	
/*	
	@GetMapping("/index.action")
	public ModelAndView index(ModelAndView mav) {
		
		List<Map<String, String>> mapList = service.getImgfilenameList();
		
		mav.addObject("mapList", mapList);
		mav.setViewName("main/index.tiles1");
		//  /WEB-INF/views/tiles1/main/index.jsp  페이지를 만들어야 한다.
		
		return mav;
	}
*/	
	// === 또는 === //
	@GetMapping("/index.action")
	public ModelAndView index(ModelAndView mav) {
		mav = service.index(mav);
		return mav;
	}
	
	
	// === #40. 로그인 폼 페이지 요청 === //
	@GetMapping("/login.action")
	public ModelAndView login(ModelAndView mav) {
		mav.setViewName("login/loginform.tiles1");
		//  /WEB-INF/views/tiles1/login/loginform.jsp  페이지를 만들어야 한다.
		return mav;
	}
	
	
	// === #41. 로그인 처리하기 === //
 /*	
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
		
		if(loginuser == null) { // 로그인 실패시
			String message = "아이디 또는 암호가 틀립니다.";
			String loc = "javascript:history.back()";
			
			mav.addObject("message", message);
			mav.addObject("loc", loc);
			
			mav.setViewName("msg");
			//  /WEB-INF/views/msg.jsp 파일을 생성한다.
		}
		else { // 아이디와 암호가 존재하는 경우 
			
			if(loginuser.getIdle() == 1) { // 로그인 한지 1년이 경과한 경우
				
				String message = "로그인을 한지 1년이 지나서 휴면상태로 되었습니다.\\n관리자에게 문의 바랍니다.";
				String loc = request.getContextPath()+"/index.action";
				// 원래는 위와 같이 index.action 이 아니라 휴면의 계정을 풀어주는 페이지로 잡아주어야 한다. 
				
				mav.addObject("message", message);
				mav.addObject("loc", loc);
				
				mav.setViewName("msg");
			}
			else{ // 로그인 한지 1년 이내인  경우
			   
				HttpSession session = request.getSession();
				// 메모리에 생성되어져 있는 session 을 불러온다.
				
				session.setAttribute("loginuser", loginuser); 
				// session(세션)에 로그인 되어진 사용자 정보인 loginuser 을 키이름을 "loginuser" 으로 저장시켜두는 것이다. 
				
				if(loginuser.isRequirePwdChange() == true) { // 암호를 마지막으로 변경한 것이 3개월이 경과한 경우
					
					String message = "비밀번호를 변경하신지 3개월이 지났습니다.\\n암호를 변경하시는 것을 추천합니다.";
					String loc = request.getContextPath()+"/index.action";
					// 원래는 위와 같이 index.action 이 아니라 사용자의 비밀번호를 변경해주는 페이지로 잡아주어야 한다.
					
					mav.addObject("message", message);
					mav.addObject("loc", loc);
					
					mav.setViewName("msg");
					
				}
				else { // 암호를 마지막으로 변경한 것이 3개월 이내인 경우
					
					// 로그인을 해야만 접근할 수 있는 페이지에 로그인을 하지 않은 상태에서 접근을 시도한 경우 
					// "먼저 로그인을 하세요!!" 라는 메시지를 받고서 사용자가 로그인을 성공했다라면
					// 화면에 보여주는 페이지는 시작페이지로 가는 것이 아니라
					// 조금전 사용자가 시도하였던 로그인을 해야만 접근할 수 있는 페이지로 가기 위한 것이다.
					String goBackURL = (String) session.getAttribute("goBackURL"); 
					
					if(goBackURL != null) {
						mav.setViewName("redirect:"+goBackURL);
						session.removeAttribute("goBackURL"); // 세션에서 반드시 제거해주어야 한다.
					}
					else {
						mav.setViewName("redirect:/index.action"); // 시작페이지로 이동
					}
				}
			}
		}
		
		return mav;
	}
 */	
	// ==== 또는 ==== //
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
		
		mav = service.loginEnd(paraMap, mav, request); // 사용자가 입력한 값들을 Map 에 담아서 서비스 객체에게 넘겨 처리하도록 한다. 
		
		return mav;
	}
	
	
	// === #50. 로그아웃 처리하기 === //
/*	
	@GetMapping("/logout.action")
	public ModelAndView logout(ModelAndView mav, HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		session.invalidate();
		
		String message = "로그아웃 되었습니다.";
		String loc = request.getContextPath()+"/index.action";
		
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
	}
*/	
	// === 또는 === //
	@GetMapping("/logout.action")
	public ModelAndView logout(ModelAndView mav, HttpServletRequest request) {
		
		mav = service.logout(mav, request);
		return mav;
	}
	
	// === #51. 게시판 글쓰기 폼페이지 요청 === //
	@GetMapping("/add.action")
	public ModelAndView requiredLogin_add(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// === #162. 답변 글쓰기가 추가된 경우 시작 ===
		String subject = "[답변] " + request.getParameter("subject");
		String groupno = request.getParameter("groupno");
		String fk_seq = request.getParameter("fk_seq");
		String depthno = request.getParameter("depthno");
		
		/*
	       	view.jsp 에서 "답변글쓰기" 를 할 때 글제목에 [ 또는 ] 이 들어간 경우 아래와 같은 오류가 발생한다.
	    	
	       	HTTP 상태 400 – 잘못된 요청
          	메시지 요청 타겟에서 유효하지 않은 문자가 발견되었습니다. 유효한 문자들은 RFC 7230과 RFC 3986에 정의되어 있습니다.
	          
	          해결책은 
	          톰캣의 C:\apache-tomcat-9.0.79\conf\server.xml 에서 
	       <Connector port="9099" URIEncoding="UTF-8" protocol="HTTP/1.1"
	           connectionTimeout="20000"
	           redirectPort="8443" /> 
	                  에 가서
	        <Connector port="9099" URIEncoding="UTF-8" protocol="HTTP/1.1"
	           connectionTimeout="20000"
	           redirectPort="8443"
	           relaxedQueryChars="[]()^|&quot;" />  
	                 
	                  와 같이 relaxedQueryChars="[]()^|&quot;" 을 추가해주면 된다.
	   */
		
		if(fk_seq == null) {
			fk_seq = ""; // 원글 글쓰기인 경우
		}
		
		mav.addObject("subject", subject);
		mav.addObject("groupno", groupno);
		mav.addObject("fk_seq", fk_seq);
		mav.addObject("depthno", depthno);
		// === 답변 글쓰기가 추가된 경우 끝 ===
		
		mav.setViewName("board/add.tiles1");
		//  /WEB-INF/views/tiles1/board/add.jsp  페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	// === #54. 게시판 글쓰기 완료 요청 === //
	@PostMapping("/addEnd.action")
//	public ModelAndView addEnd(ModelAndView mav, BoardVO boardvo) { // <== After Advice 를 사용하기 전
//	public ModelAndView pointPlus_addEnd(Map<String, String> paraMap, ModelAndView mav, BoardVO boardvo) { // <== After Advice 를 사용하기
	public ModelAndView pointPlus_addEnd(Map<String, String> paraMap, ModelAndView mav, BoardVO boardvo, MultipartHttpServletRequest mrequest) { // <== After Advice 를 사용하기
	/*
	    form 태그의 name 명과  BoardVO 의 필드명이 같다면 
	    request.getParameter("form 태그의 name명"); 을 사용하지 않더라도
	        자동적으로 BoardVO boardvo 에 set 된다.
	*/
		
	/*
		=== #171. 파일첨부가 된 글쓰기 이므로  
       	먼저 위의  public ModelAndView pointPlus_addEnd(Map<String,String> paraMap, ModelAndView mav, BoardVO boardvo) { 을 
       	주석처리 한 이후에 아래와 같이 한다.
       	MultipartHttpServletRequest mrequest 를 사용하기 위해서는 
       	먼저 /Board/src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml 에서     
       	#21 파일업로드 및 파일다운로드에 필요한 의존객체 설정하기 를 해두어야 한다.  
	*/
		
	/*
	        웹페이지에 요청 form이 enctype="multipart/form-data" 으로 되어 있어서 Multipart 요청(파일처리 요청)이 들어올 때 
	        컨트롤러에서는 HttpServletRequest 대신  MultipartHttpServletRequest 인터페이스를 사용해야 한다.
	    MultipartHttpServletRequest 인터페이스는 HttpServletRequest 인터페이스와  MultipartRequest 인터페이스를 상속받고 있다.
	        즉, 웹 요청 정보를 얻기 위한 getParameter()와 같은 메소드와 Multipart(파일처리) 관련 메소드를 모두 사용가능하다.
	*/
		
		// === 사용자가 쓴 글에 파일이 첨부되어 있는지, 아니면 파일 첨부가 되지 않은 것인지 구분을 지어주어야 한다. ===
		// === #173. !!! 첨부 파일이 있는 경우 작업 시작 !!! ===
		MultipartFile attach = boardvo.getAttach();
		
		if(attach != null) {
			// attach(첨부파일)가 비어 있지 않으면(즉, 첨부파일이 있는 경우라면) 
	         
			/*
	            1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다. 
	            >>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
              	우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
              	조심할 것은  Package Explorer 에서  files 라는 폴더를 만드는 것이 아니다.       
			*/
			
			// WAS 의 webapp 의 절대경로를 알아와야 한다. 
			HttpSession session = mrequest.getSession(); 
			String root = session.getServletContext().getRealPath("/"); 
			
//			System.out.println("~~~ 확인용 webapp 의 절대경로 => " + root);
			// ~~~ 확인용 webapp 의 절대경로 => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\
			
			String path = root + "resources" + File.separator + "files";
			/*  
				File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
				운영체제가 Windows 이라면 File.separator 는  "\" 이고,
				운영체제가 UNIX, Linux, 매킨토시(맥) 이라면  File.separator 는 "/" 이다. 
		    */
			
			// path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
			//   System.out.println("~~~ 확인용 path => " + path);
			//  ~~~ 확인용 path => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\files
			
			/*
				2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기
			*/
			String newFileName = "";
			// WAS(톰캣)의 디스크에 저장될 파일명
			
			byte[] bytes = null;
			// 첨부파일의 내용물을 담는 것
			
			long fileSize = 0;
			// 첨부파일의 크기
			
			try {
				bytes = attach.getBytes();
				// 첨부파일의 내용물을 읽어오는 것
				
				String originalFilename = attach.getOriginalFilename();
				// attach.getOriginalFilename() 이 첨부파일명의 파일명(예: 강아지.png) 이다.
				
//				System.out.println("~~~ 확인용 originalFilename => " + originalFilename); 
	            // ~~~ 확인용 originalFilename => LG_싸이킹청소기_사용설명서.pdf
				
				newFileName = fileManager.doFileUpload(bytes, originalFilename, path);
				// 첨부된 파일을 업로드하는 것이다.
				
//				System.out.println("~~~ 확인용 newFileName => " + newFileName);
				// ~~~ 확인용 newFileName => 2024062712074212811435674800.jpg
				
				
			/*
            	3. BoardVO boardvo 에 fileName 값과 orgFilename 값과 fileSize 값을 넣어주기  
			*/
				boardvo.setFileName(newFileName);
				// WAS(톰캣)에 저장될 파일명 (2024062712074212811435674800.jpg)
				
				boardvo.setOrgFilename(originalFilename);
				// 게시판 페이지에서 첨부된 파일(쉐보레전면.jpg)을 보여줄 때 사용.
	            // 또한 사용자가 파일을 다운로드 할 때 사용되는 파일명으로 사용.
				
				fileSize = attach.getSize(); // 첨부파일의 크기 (단위는 byte이다.)
				boardvo.setFileSize(String.valueOf(fileSize));
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		// === !!! 첨부 파일이 있는 경우 작업 끝 !!! ===
		
//		int n = service.add(boardvo); // <== 파일첨부가 없는 글쓰기 
		
		// === #176. 파일 첨부가 있는 글쓰기 또는 파일 첨부가 없는 글쓰기로 나누어서 service 호출하기 시작 ===
		//		먼저 위의 int n = service.add(boardvo); 부분을 주석 처리하고 난 후 아래와 같이 한다.
		
		int n = 0;
		
		if(attach.isEmpty()) { // 파일이 null이거나 공백인 경우 모두 포함
			// 파일 첨부가 없는 경우라면
			service.add(boardvo); // <== 파일첨부가 없는 글쓰기
			
		} else {
			// 파일 첨부가 있는 경우라면
			n = service.add_withFile(boardvo);
		}
		
		// === 파일 첨부가 있는 글쓰기 또는 파일 첨부가 없는 글쓰기로 나누어서 service 호출하기 끝 ===
		
		if(n==1) {
			mav.setViewName("redirect:/list.action");
		    //  /list.action 페이지로 redirect(페이지이동)하라는 말이다.
		}
		
		else {
			mav.setViewName("board/error/add_error.tiles1");
			//  /WEB-INF/views/tiles1/board/error/add_error.jsp 파일을 생성한다.
		}
		
		// ===== #104. After Advice 를 사용하기 ====== //
		//             글쓰기를 한 이후에는 회원의 포인트를 100점 증가 
		paraMap.put("userid", boardvo.getFk_userid());
		paraMap.put("point", "100");
		
		return mav;
	}
	
	
	// === #58. 글목록 보기 페이지 요청 === //
	@GetMapping("/list.action")
	public ModelAndView list(ModelAndView mav, HttpServletRequest request) {
		
		List<BoardVO> boardList = null;
		
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
		
		
		// === 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 === //
	//	boardList = service.boardListNoSearch();
		
		
		// === #110. 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 === // 
	/*
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		
	//	System.out.println("~~~ 확인용 searchType : " + searchType);
		
		  ~~~ 확인용 searchType : null 
		  ~~~ 확인용 searchType : subject
		  ~~~ 확인용 searchType : content
		  ~~~ 확인용 searchType : subject_content
		  ~~~ 확인용 searchType : subject_content
		  ~~~ 확인용 searchType : name
		
		
	//	System.out.println("~~~ 확인용 searchWord : " + searchWord);
		
		  ~~~ 확인용 searchWord : null 
		  ~~~ 확인용 searchWord : 연습
		  ~~~ 확인용 searchWord : 테스트 
		  ~~~ 확인용 searchWord : 테스트 
		  ~~~ 확인용 searchWord : 테스트 
		  ~~~ 확인용 searchWord : 
		
		
		if(searchType == null) {
			searchType = "";
		}
		
		if(searchWord == null) {
			searchWord = "";
		}
		
		if(searchWord != null) {
			searchWord = searchWord.trim();
			// "    연습   " ==> "연습"
			// "        " ==> ""  
		}
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		
		boardList = service.boardListSearch(paraMap);
	*/
	
	//////////////////////////////////////////////////////	
		
	// === #122. 페이징 처리를 한 검색어가 있는 전체 글목록 보여주기 === //
	
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
			// "    연습   " ==> "연습"
			// "        " ==> ""  
		}
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
				
		// 먼저, 총 게시물 건수(totalCount)를 구해와야 한다.
		// 총 게시물 건수(totalCount)는  검색조건이 있을 때와 없을때로 나뉘어진다.
		int totalCount = 0;        // 총 게시물 건수
		int sizePerPage = 10;      // 한 페이지당 보여줄 게시물 건수 
		int currentShowPageNo = 0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정함. 
		int totalPage = 0;         // 총 페이지수(웹브라우저상에서 보여줄 총 페이지 개수, 페이지바) 
		
		// 총 게시물 건수(totalCount)
		totalCount = service.getTotalCount(paraMap);
	//	System.out.println("~~~ 확인용 totalCount : " + totalCount); 
	/*
		~~~ 확인용 totalCount : 14
		~~~ 확인용 totalCount : 6
		~~~ 확인용 totalCount : 2
		~~~ 확인용 totalCount : 0
		~~~ 확인용 totalCount : 11
	*/	
		
		// 만약에 총 게시물 건수(totalCount)가 124 개 이라면 총 페이지수(totalPage)는 13 페이지가 되어야 한다.
		// 만약에 총 게시물 건수(totalCount)가 120 개 이라면 총 페이지수(totalPage)는 12 페이지가 되어야 한다.
		totalPage = (int) Math.ceil((double)totalCount/sizePerPage); 
		// (double)124/10 ==> 12.4  ==> Math.ceil(12.4) ==> 13.0  ==> 13
		// (double)120/10 ==> 12.0  ==> Math.ceil(12.0) ==> 12.0  ==> 12 
		
		if(str_currentShowPageNo == null) {
			// 게시판에 보여지는 초기화면
			
			currentShowPageNo = 1;
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
			}
		}
		
		
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
		// 글목록 가져오기(페이징 처리 했으며, 검색어가 있는 것 또는 검색어 없는것 모두 포함 한 것이다.
		
		mav.addObject("boardList", boardList);
		
		// 검색시 검색조건 및 검색어 값 유지시키기
		if("subject".equals(searchType) ||
		   "content".equals(searchType) ||
		   "subject_content".equals(searchType) ||
		   "name".equals(searchType)) {
			
			mav.addObject("paraMap", paraMap);
		}
		
		
		// ==== #129. 페이지바 만들기 ==== //
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
		
		// === [맨처음][이전] 만들기 === //
		if(pageNo != 1) {
			pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo=1'>[맨처음]</a></li>";
			pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'>[이전]</a></li>"; 
		}
		
		while( !(loop > blockSize || pageNo > totalPage) ) {
			
			if(pageNo == currentShowPageNo) {
				pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
			}
			else {
				pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>"; 
			}
			
			loop++;
			pageNo++;
		}// end of while------------------------
		
		// === [다음][마지막] 만들기 === //
		if(pageNo <= totalPage) {
			pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>[다음]</a></li>";
			pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+totalPage+"'>[마지막]</a></li>"; 
		}
		
		pageBar += "</ul>";
		
		mav.addObject("pageBar", pageBar);
		
		
		// === #131. 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
		//           사용자가 "검색된결과목록보기" 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
		//           현재 페이지 주소를 뷰단으로 넘겨준다.
		String goBackURL = MyUtil.getCurrentURL(request);
	//	System.out.println("~~~ 확인용(list.action) goBackURL : " + goBackURL); 
		/*
		    ~~~ 확인용(list.action) goBackURL : /list.action
		    ~~~ 확인용(list.action) goBackURL : /list.action?searchType=&searchWord=&currentShowPageNo=5 
		    ~~~ 확인용(list.action) goBackURL : /list.action?searchType=subject&searchWord=java
		    ~~~ 확인용(list.action) goBackURL : /list.action?searchType=name&searchWord=정화
		    ~~~ 확인용(list.action) goBackURL : /list.action?searchType=subject&searchWord=정화&currentShowPageNo=3   
		*/
		mav.addObject("goBackURL", goBackURL);
		
		////////////////////////////////////////////////////////
		// '페이징 처리 시 보여주는 순번'에 필요한 변수들
		mav.addObject("totalCount", totalCount);
		mav.addObject("currentShowPageNo", currentShowPageNo);
		mav.addObject("sizePerPage", sizePerPage);
		////////////////////////////////////////////////////////
		
		mav.setViewName("board/list.tiles1");
		//  /WEB-INF/views/tiles1/board/list.jsp 파일을 생성한다.
		
		return mav;
	}
	
	
	
	// === #62. 글 1개를 보여주는 페이지 요청 === //
//	@GetMapping("/view.action")
	@RequestMapping("/view.action") // === #133. 특정글을 조회한 후 "검색된결과목록보기" 버튼을 클릭했을 때 돌아갈 페이지를 만들기 위함.  === //
	public ModelAndView view(ModelAndView mav, HttpServletRequest request) {
		
		String seq = "";
		String goBackURL = "";
		String searchType = "";
		String searchWord = "";
		
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		// redirect 되어서 넘어온 데이터가 있는지 꺼내어 와본다.
		
		if(inputFlashMap != null) { // redirect 되어서 넘어온 데이터가 있다면
			
			@SuppressWarnings("unchecked") // 경고 표시를 하지 말라는 뜻이다.
			Map<String, String> redirect_map = (Map<String, String>) inputFlashMap.get("redirect_map");
			// "redirect_map" 값은  /view_2.action 에서  redirectAttr.addFlashAttribute("키", 밸류값); 을 할때 준 "키" 이다. 
			// "키" 값을 주어서 redirect 되어서 넘어온 데이터를 꺼내어 온다. 
			// "키" 값을 주어서 redirect 되어서 넘어온 데이터의 값은 Map<String, String> 이므로 Map<String, String> 으로 casting 해준다.

			
//			System.out.println("~~~ 확인용 seq : " + redirect_map.get("seq"));  
			seq = redirect_map.get("seq");
			
			// === #143. 이전글제목, 다음글제목 보기 시작 ===
			searchType = redirect_map.get("searchType"); // 무조건 영어이므로 URL Decode 필요없음
			
			try {
				searchWord = URLDecoder.decode(redirect_map.get("searchWord"), "UTF-8"); // 한글데이터가 포함되어 있으면 반드시 한글로 복구해 주어야 한다.
				goBackURL = URLDecoder.decode(redirect_map.get("goBackURL"), "UTF-8"); // 한글데이터가 포함되어 있으면 반드시 한글로 복구해 주어야 한다.
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
//			System.out.println("~~~ 확인용 searchType : " + searchType);
//			System.out.println("~~~ 확인용 searchWord : " + searchWord);
//			System.out.println("~~~ 확인용 goBackURL : " + goBackURL);
			/*
				~~~ 확인용 searchType : name
				~~~ 확인용 searchWord : 엄정화
				~~~ 확인용 goBackURL : /list.action?searchType=name&searchWord=%EC%97%84%EC%A0%95%ED%99%94
			*/
			// === #143. 이전글제목, 다음글제목 보기 끝 ===
			
		}
		
		////////////////////////////////////////////////////////////
		
		else { // redirect 되어서 넘어온 데이터가 아닌 경우 
			
			// == 조회하고자 하는 글번호 받아오기 ==
        	
        	// 글목록보기인 /list.action 페이지에서 특정 글제목을 클릭하여 특정글을 조회해온 경우  
        	// 또는 
        	// 글목록보기인 /list.action 페이지에서 특정 글제목을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는 원본이 form 을 사용해서 POST 방식으로 넘어온 경우이므로 "양식 다시 제출 확인" 이라는 대화상자가 뜨게 되며 "계속" 이라는 버튼을 클릭하면 이전에 입력했던 데이터를 자동적으로 입력해서 POST 방식으로 진행된다. 그래서  request.getParameter("seq"); 은 null 이 아닌 번호를 입력받아온다.     
			// 그런데 "이전글제목" 또는 "다음글제목" 을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는 원본이 /view_2.action 을 통해서 redirect 되어진 경우이므로 form 을 사용한 것이 아니라서 "양식 다시 제출 확인" 이라는 alert 대화상자가 뜨지 않는다. 그래서  request.getParameter("seq"); 은 null 이 된다. 
			seq = request.getParameter("seq");
		 // System.out.println("~~~~~~ 확인용 seq : " + seq);
        	// ~~~~~~ 확인용 seq : 213
        	// ~~~~~~ 확인용 seq : null
			
			// === #134. 특정글을 조회한 후 "검색된결과목록보기" 버튼을 클릭했을 때 돌아갈 페이지를 만들기 위함. === //
			goBackURL = request.getParameter("goBackURL");
//			System.out.println("~~~ 확인용(view.action) goBackURL :" + goBackURL);
		/*
		 	잘못된 것 (get방식일 경우)
		 	~~~ 확인용(view.action) goBackURL :/list.action?searchType=
		 	
		 	올바른 것 (post방식일 경우)
		 	~~~ 확인용(view.action) goBackURL :/list.action?searchType=subject&searchWord=%EC%A0%95%ED%99%94&currentShowPageNo=3
		*/
			
			
			// >>> 글목록에서 검색된 글내용일 경우 이전글제목, 다음글제목은 검색된 결과물 내의 이전글과 다음글이 나오도록 하기 위한 것이다. 시작 <<< // 
			searchType = request.getParameter("searchType");
			searchWord = request.getParameter("searchWord");
			
			if(searchType == null) {
				searchType = "";
			}
			
			if(searchWord == null) {
				searchWord = "";
			}
			
//			System.out.println("~~~ 확인용(view.action) searchType : " + searchType);
			// ~~~ 확인용(view.action) searchType : 
			// ~~~ 확인용(view.action) searchType : subject
			
//			System.out.println("~~~ 확인용(view.action) searchWord : " + searchWord);
			// ~~~ 확인용(view.action) searchWord : 
			// ~~~ 확인용(view.action) searchWord : java
			
			// >>> 글목록에서 검색된 글내용일 경우 이전글제목, 다음글제목은 검색된 결과물 내의 이전글과 다음글이 나오도록 하기 위한 것이다. 끝 <<< // 
		}
		
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
			
			// >>> 글목록에서 검색된 글내용일 경우 이전글제목, 다음글제목은 검색된 결과물 내의 이전글과 다음글이 나오도록 하기 위한 것이다. 시작 <<< //
			paraMap.put("searchType", searchType);
			paraMap.put("searchWord", searchWord);
			// >>> 글목록에서 검색된 글내용일 경우 이전글제목, 다음글제목은 검색된 결과물 내의 이전글과 다음글이 나오도록 하기 위한 것이다. 끝 <<< //
			
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
			
			if("yes".equals( (String)session.getAttribute("readCountPermission") )) {
			// 글목록보기인 /list.action 페이지를 클릭한 다음에 특정글을 조회해온 경우이다.
				
				boardvo = service.getView(paraMap);
				// 글 조회수 증가와 함께 글 1개를 조회를 해오는 것
			//	System.out.println("~~ 확인용 글내용 : " + boardvo.getContent());
				
				session.removeAttribute("readCountPermission");
				// 중요함!! session 에 저장된 readCountPermission 을 삭제한다. 
			}
			
			else {
				// 글목록에서 특정 글제목을 클릭하여 본 상태에서
			    // 웹브라우저에서 새로고침(F5)을 클릭한 경우이다.
			//	System.out.println("글목록에서 특정 글제목을 클릭하여 본 상태에서 웹브라우저에서 새로고침(F5)을 클릭한 경우");
				
				boardvo = service.getView_no_increase_readCount(paraMap);
				// 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것
				
			 // 또는 redirect 해주기 (예 : 버거킹 www.burgerking.co.kr 메뉴소개)
			 /*	
				mav.setViewName("redirect:/list.action");
				return mav;
			 */	
				
				if(boardvo == null) {
					mav.setViewName("redirect:/list.action");
					return mav;
				}
			}
			
			mav.addObject("boardvo", boardvo);
			
			// === #140. 이전글제목, 다음글제목 보기 ===
			mav.addObject("paraMap", paraMap);
			
			mav.setViewName("board/view.tiles1");
			//  /WEB-INF/views/tiles1/board/view.jsp 파일을 생성한다.
			
		} catch (NumberFormatException e) {
			mav.setViewName("redirect:/list.action");
		}
		
		return mav;
	}
	
	
//	@GetMapping("/view_2.action")
	@PostMapping("/view_2.action")
	public ModelAndView view_2(ModelAndView mav, HttpServletRequest request, RedirectAttributes redirectAttr) {
		
		// 조회하고자 하는 글번호 받아오기
		String seq = request.getParameter("seq");
		
		// === #141. 이전글제목, 다음글제목 보기  시작 ===
		String goBackURL = request.getParameter("goBackURL");
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		
		/* 
		    redirect:/ 를 할 때 "한글데이터는 0에서 255까지의 허용 범위 바깥에 있으므로 인코딩될 수 없습니다" 라는 
		    java.lang.IllegalArgumentException 라는 오류가 발생한다.
	     	이것을 방지하려면 아래와 같이 하면 된다.
		*/
		try {
	         searchWord = URLEncoder.encode(searchWord, "UTF-8");
	         goBackURL = URLEncoder.encode(goBackURL, "UTF-8");
	         
//	         System.out.println("~~~~ view_2.action 의  URLEncoder.encode(searchWord, \"UTF-8\") : " + searchWord);
	         // ~~~~ view_2.action 의  URLEncoder.encode(searchWord, "UTF-8") : %EC%84%9C%EC%98%81%ED%95%99
	         
//	         System.out.println("~~~~ view_2.action 의  URLEncoder.encode(goBackURL, \"UTF-8\") : " + goBackURL);
	         // ~~~~ view_2.action 의  URLEncoder.encode(goBackURL, "UTF-8") : %2Flist.action%3FsearchType%3Dname+searchWord%3D%25EC%2584%259C%25EC%2598%2581%25ED%2595%2599+currentShowPageNo%3D11 
	         
//	         System.out.println(URLDecoder.decode(searchWord, "UTF-8")); // URL인코딩 된 한글을 원래 한글모양으로 되돌려주는 것임.
	         // 김다영
	         
//	         System.out.println(URLDecoder.decode(goBackURL, "UTF-8"));  // URL인코딩 된 한글을 원래 한글모양으로 되돌려주는 것임.
	         // /list.action?searchType=name searchWord=%EC%84%9C%EC%98%81%ED%95%99 currentShowPageNo=11

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// === #141. 이전글제목, 다음글제목 보기  끝 ===
		
		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		
		// ==== redirect(GET방식임) 시 데이터를 넘길때 GET 방식이 아닌 POST 방식처럼 데이터를 넘기려면 RedirectAttributes 를 사용하면 된다. 시작 ==== //
		/////////////////////////////////////////////////////////////////////////////////
		Map<String, String> redirect_map = new HashMap<>();
		redirect_map.put("seq", seq);
		
		// === #142. 이전글제목, 다음글제목 보기 시작 ===
		redirect_map.put("goBackURL", goBackURL);
		redirect_map.put("searchType", searchType);
		redirect_map.put("searchWord", searchWord);
		// === #142. 이전글제목, 다음글제목 보기 끝 ===
		
		
		redirectAttr.addFlashAttribute("redirect_map", redirect_map);
		// redirectAttr.addFlashAttribute("키", 밸류값); 으로 사용하는데 오로지 1개의 데이터만 담을 수 있으므로 여러개의 데이터를 담으려면 Map 을 사용해야 한다. 
		
		mav.setViewName("redirect:/view.action"); // 실제로 redirect:/view.action 은 POST 방식이 아닌 GET 방식이다.
        /////////////////////////////////////////////////////////////////////////////////
		// ==== redirect(GET방식임) 시 데이터를 넘길때 GET 방식이 아닌 POST 방식처럼 데이터를 넘기려면 RedirectAttributes 를 사용하면 된다. 끝 ==== //
		
		return mav;
	}
	
	
	// === #71. 글을 수정하는 페이지 요청 === //
	@GetMapping("/edit.action")
	public ModelAndView requiredLogin_edit(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 글 수정해야 할 글번호 가져오기
		String seq = request.getParameter("seq");
		
		String message = "";
		
		try {
			Integer.parseInt(seq);
			
			// 글 수정해야 할 글 1개 내용가져오기
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			
			BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
			// 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것
			
			if(boardvo == null) {
				message = "글 수정이 불가합니다.";
			}
			else {
				HttpSession session = request.getSession();
				MemberVO loginuser = (MemberVO) session.getAttribute("loginuser"); 
				
				if( !loginuser.getUserid().equals(boardvo.getFk_userid()) ) {
					message = "다른 사용자의 글은 수정이 불가합니다.";
				}
				else {
					// 자신의 글을 수정할 경우
					// 가져온 1개글을 글수정할 폼이 있는 view 단으로 보내준다.
					mav.addObject("boardvo", boardvo);
					mav.setViewName("board/edit.tiles1");
					
					return mav;
				}
			}
			
		} catch (NumberFormatException e) {
			message = "글 수정이 불가합니다.";
		}
		
		String loc = "javascript:history.back()";
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
	}
	
	
	// === #72. 글을 수정하는 페이지 완료하기 === //
	@PostMapping("/editEnd.action")
	public ModelAndView editEnd(ModelAndView mav, BoardVO boardvo, HttpServletRequest request) {
		
		int n = service.edit(boardvo);
		
		if(n==1) {
			mav.addObject("message", "글 수정 성공!!");
			mav.addObject("loc", request.getContextPath()+"/view.action?seq="+boardvo.getSeq());
			mav.setViewName("msg");
		}
		
		return mav;
	}
	
	
	
	// === #76. 글을 삭제하는 페이지 요청 === //
	@GetMapping("/del.action")
	public ModelAndView requiredLogin_del(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 글 삭제해야 할 글번호 가져오기
		String seq = request.getParameter("seq");
		
		String message = "";
		
		try {
			Integer.parseInt(seq);
			
			// 글 삭제해야 할 글 1개 내용가져오기
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			
			BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
			// 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것
			
			if(boardvo == null) {
				message = "글 삭제가 불가합니다.";
			}
			else {
				HttpSession session = request.getSession();
				MemberVO loginuser = (MemberVO) session.getAttribute("loginuser"); 
				
				if( !loginuser.getUserid().equals(boardvo.getFk_userid()) ) {
					message = "다른 사용자의 글은 삭제가 불가합니다.";
				}
				else {
					// 자신의 글을 삭제할 경우
					// 가져온 1개글을 글삭제할 폼이 있는 view 단으로 보내준다.
					mav.addObject("boardvo", boardvo);
					mav.setViewName("board/del.tiles1");
					
					return mav;
				}
			}
			
		} catch (NumberFormatException e) {
			message = "글 삭제가 불가합니다.";
		}
		
		String loc = "javascript:history.back()";
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
	}
	
	
	// === #77. 글을 삭제하는 페이지 완료하기 === //
	@PostMapping("/delEnd.action")
	public ModelAndView delEnd(ModelAndView mav, HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		
		/////////////////////////////////////////////////////////////
		// === #184. 파일 첨부가 된 글이라면 글 삭제 시 먼저 첨부파일을 삭제해주어야 한다. 시작 ===
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("searchType", "");
		paraMap.put("searchWord", "");
		
		BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
		
		String fileName = boardvo.getFileName();
		// 202407010918442666853860700.pdf <= 이것은 WAS(톰캣) 디스크에 저장된 파일명이다.
		
		if(fileName != null && !"".equals(fileName)) {
			
			// 첨부파일이 저장되어 있는 WAS(톰캣) 디스크 경로명을 알아와야만 다운로드를 해줄 수 있다.
			// 이 경로는 우리가 파일첨부를 위해서 /addEnd.action 에서 설정해두었던 경로와 똑같아야 한다. 
			// WAS 의 webapp 의 절대경로를 알아와야 한다. 
			HttpSession session = request.getSession(); 
			String root = session.getServletContext().getRealPath("/"); 
			
//			System.out.println("~~~ 확인용 webapp 의 절대경로 => " + root);
			// ~~~ 확인용 webapp 의 절대경로 => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\
			
			String path = root + "resources" + File.separator + "files";
			/*  
				File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
				운영체제가 Windows 이라면 File.separator 는  "\" 이고,
				운영체제가 UNIX, Linux, 매킨토시(맥) 이라면  File.separator 는 "/" 이다. 
		    */
			
			// path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
			//   System.out.println("~~~ 확인용 path => " + path);
			//  ~~~ 확인용 path => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\files
			
			paraMap.put("path", path); // 삭제해야 할 파일이 저장된 경로
			paraMap.put("fileName", fileName); // 삭제해야 할 파일 이름
			
		}
		
		// === #184. 파일 첨부가 된 글이라면 글 삭제 시 먼저 첨부파일을 삭제해주어야 한다. 끝 ===
		/////////////////////////////////////////////////////////////
		
//		int n = service.del(seq);
		
		// === #185. 파일 첨부가 된 경우, 먼저 위의  int n = service.del(seq); 을 주석 처리한다.===
		int n = service.del(paraMap);
		
		if(n==1) {
			mav.addObject("message", "글 삭제 성공!!");
			mav.addObject("loc", request.getContextPath()+"/list.action");
			mav.setViewName("msg");
		}
		
		return mav;
	}
	
	
	// === #84. 댓글쓰기(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/addComment.action", produces="text/plain;charset=UTF-8") 
	public String addComment(CommentVO commentvo) {
		// 댓글쓰기에 첨부파일이 없는 경우 
		
		int n = 0;
		
		try {
			n = service.addComment(commentvo);
			// 댓글쓰기(insert) 및 원게시물(tbl_board 테이블)에 댓글의 개수 증가(update 1씩 증가)하기 
			// 이어서 회원의 포인트를 50점을 증가하도록 한다. (tbl_member 테이블에 point 컬럼의 값을 50 증가하도록 update 한다.) 
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
		JSONObject jsonObj = new JSONObject(); // {}
		jsonObj.put("n", n); // {"n":1} 또는 {"n":0}
		jsonObj.put("name", commentvo.getName()); // {"n":1, "name":"엄정화"} 또는 {"n":0, "name":"서영학"}
		
		return jsonObj.toString(); 
		// "{"n":1, "name":"엄정화"}" 또는 "{"n":0, "name":"서영학"}" 
	}
	
	
	// === #90. 원게시물에 딸린 댓글들을 조회해오기(Ajax 로 처리) === //
	@ResponseBody
	@GetMapping(value="/readComment.action", produces="text/plain;charset=UTF-8") 
	public String readComment(HttpServletRequest request) {
		
		String parentSeq = request.getParameter("parentSeq"); 
		
		List<CommentVO> commentList = service.getCommentList(parentSeq); 
		
		JSONArray jsonArr = new JSONArray(); // [] 
		
		if(commentList != null) {
			for(CommentVO cmtvo : commentList) {
				JSONObject jsonObj = new JSONObject();          // {} 
				jsonObj.put("seq", cmtvo.getSeq());             // {"seq":1}
				jsonObj.put("fk_userid", cmtvo.getFk_userid()); // {"seq":1, "fk_userid":"seoyh"}
				jsonObj.put("name", cmtvo.getName());           // {"seq":1, "fk_userid":"seoyh","name":"서영학"}
				jsonObj.put("content", cmtvo.getContent());     // {"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ"}
				jsonObj.put("regdate", cmtvo.getRegDate());     // {"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ","regdate":"2024-06-18 15:36:31"}
				
				jsonArr.put(jsonObj);
			}// end of for-----------------------
		}
		
		return jsonArr.toString(); // "[{"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ","regdate":"2024-06-18 15:36:31"}]"
		                           // 또는
		                           // "[]"
	}
	
	
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
		
		return jsonObj.toString(); // "{"n":1}"
	}
	
	
	// === #100. 댓글 삭제(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/deleteComment.action", produces="text/plain;charset=UTF-8") 
	public String deleteComment(HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		String parentSeq = request.getParameter("parentSeq");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("parentSeq", parentSeq);
		
		int n=0;
		try {
			n = service.deleteComment(paraMap);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		return jsonObj.toString(); // "{"n":1}"
	}
	
	
	// === #116. 검색어 입력시 자동글 완성하기 3 ===
	@ResponseBody
	@GetMapping(value="/wordSearchShow.action", produces="text/plain;charset=UTF-8") 
	public String wordSearchShow(HttpServletRequest request) {
		
		String searchType = request.getParameter("searchType");
		String searchWord = request.getParameter("searchWord");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		
		List<String> wordList = service.wordSearchShow(paraMap); 
		
		JSONArray jsonArr = new JSONArray(); // []
		
		if(wordList != null) {
			for(String word : wordList) {
				JSONObject jsonObj = new JSONObject(); // {} 
				jsonObj.put("word", word);
				
				jsonArr.put(jsonObj); // [{},{},{}]
			}// end of for-----------------
		}
		
		return jsonArr.toString();
	}
	
	
	// === #146. 원게시물에 달린 댓글 내용들을 페이징 처리하기 ===
	@ResponseBody
	@GetMapping(value="/commentList.action", produces="text/plain;charset=UTF-8")
	public String commentList(HttpServletRequest request) {

		String parentSeq = request.getParameter("parentSeq");
		String currentShowPageNo = request.getParameter("currentShowPageNo");
		
		if(currentShowPageNo == null) {
			currentShowPageNo = "1";
		}
		
		int sizePerPage = 5; // 한 페이지당 5개의 댓글을 보여준다.
		
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
		
		List<CommentVO> commentList = service.getCommentList_Paging(paraMap);
		int totalCount = service.getCommentTotalCount(parentSeq); // 페이징 처리 시 보여주는 순번을 나타내기 위한 것.
		
		JSONArray jsonArr = new JSONArray(); // [] 
		
		if(commentList != null) {
			for(CommentVO cmtvo : commentList) {
				JSONObject jsonObj = new JSONObject();          // {} 
				jsonObj.put("seq", cmtvo.getSeq());             // {"seq":1}
				jsonObj.put("fk_userid", cmtvo.getFk_userid()); // {"seq":1, "fk_userid":"seoyh"}
				jsonObj.put("name", cmtvo.getName());           // {"seq":1, "fk_userid":"seoyh","name":"서영학"}
				jsonObj.put("content", cmtvo.getContent());     // {"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ"}
				jsonObj.put("regdate", cmtvo.getRegDate());     // {"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ","regdate":"2024-06-18 15:36:31"}
				
				jsonObj.put("totalCount", totalCount); // 페이징 처리 시 보여주는 순번을 나타내기 위한 것.
				jsonObj.put("sizePerPage", sizePerPage); // 페이징 처리 시 보여주는 순번을 나타내기 위한 것.
				
				// === #197. 댓글 읽어오기에 있어 첨부파일 기능을 넣은 경우 시작 ===
				jsonObj.put("fileName", cmtvo.getFileName());
				jsonObj.put("orgFilename", cmtvo.getOrgFilename());
				jsonObj.put("fileSize", cmtvo.getFileSize());
				// === 댓글 읽어오기에 있어 첨부파일 기능을 넣은 경우 끝 ===
				
				jsonArr.put(jsonObj);
			}// end of for-----------------------
		}
		
		return jsonArr.toString(); // "[{"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ","regdate":"2024-06-18 15:36:31"}]"
		                           // 또는
		                           // "[]"
	} // end of commentList.action --------------------------------------------------
		
	
	// === #183. 첨부파일 다운로드 받기 ===
	@GetMapping("/download.action")
	public void requiredLogin_download(HttpServletRequest request, HttpServletResponse response) {
		
		String seq = request.getParameter("seq");
		// 첨부파일이 있는 글번호
		
		/*
			첨부파일이 있는 글번호에서
		   	20231124124825759362098213700.pdf 처럼
	    	이러한 fileName 값을 DB에서 가져와야 한다.
	    	또한 orgFilename 값도 DB에서 가져와야 한다.
		*/
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("searchType", "");
		paraMap.put("searchWord", "");

		// **** 웹브라우저에 출력하기 시작 ****
		// HttpServletResponse response 객체는 전송되어 온 데이터를 조작해서 결과물을 나타내고자 할 때 쓰인다.
		
		response.setContentType("text/html; charset=UTF-8"); // 한글 깨짐 방지
		
		PrintWriter out = null;
		// out은 웹브라우저에 기술하는 대상체라고 생각하자.
		
		try {
			Integer.parseInt(seq);
			
			BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
			
			if(boardvo == null || (boardvo != null && boardvo.getFileName() == null)) {
				// 시퀀스에 해당하는 글이 없거나, 글은 있지만 첨부파일명이 없을 경우
				
				out = response.getWriter();
				// out은 웹브라우저에 기술하는 대상체라고 생각하자.
				
				out.println("<script type='text/javascript'>alert('존재하지 않는 글번호이거나 첨부파일이 없으므로 파일 다운로드가 불가합니다.'); history.back();</script>");
	            return;
	            
			} else {
				// 정상적으로 다운로드를 할 경우
				
				String fileName = boardvo.getFileName();
				// 2024062809204389191207324300.jpg => 이것이 바로 WAS(톰캣)디스크에 저장된 파일명이다.
				
				String orgFilename = boardvo.getOrgFilename();
				// 쉐보레전면.jpg => 다운로드 시 보여줄 파일명
				
				
				// 첨부파일이 저장되어 있는 WAS(톰캣) 디스크 경로명을 알아와야만 다운로드를 해줄 수 있다.
				// 이 경로는 우리가 파일첨부를 위해서 /addEnd.action 에서 설정해두었던 경로와 똑같아야 한다. 
				// WAS 의 webapp 의 절대경로를 알아와야 한다. 
				HttpSession session = request.getSession(); 
				String root = session.getServletContext().getRealPath("/"); 
				
//				System.out.println("~~~ 확인용 webapp 의 절대경로 => " + root);
				// ~~~ 확인용 webapp 의 절대경로 => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\
				
				String path = root + "resources" + File.separator + "files";
				/*  
					File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
					운영체제가 Windows 이라면 File.separator 는  "\" 이고,
					운영체제가 UNIX, Linux, 매킨토시(맥) 이라면  File.separator 는 "/" 이다. 
			    */
				
				// path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
				//   System.out.println("~~~ 확인용 path => " + path);
				//  ~~~ 확인용 path => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\files
				
				
				
				// ***** file 다운로드 하기 ***** //
	            boolean flag = false; // file 다운로드 성공, 실패인지 여부를 알려주는 용도 
	            flag = fileManager.doFileDownload(fileName, orgFilename, path, response); 
	            // file 다운로드 성공 시 flag 는 true,
	            // file 다운로드 실패 시 flag 는 false 를 가진다.
	            
	            if(!flag) {
					// 다운로드가 실패한 경우 메시지를 띄워준다.
					out = response.getWriter();
					// out 은 웹브라우저에 기술하는 대상체라고 생각하자.

					out.println("<script type='text/javascript'>alert('파일 다운로드가 실패되었습니다.'); history.back();</script>");
	            }
				
			}
			
		} catch (NumberFormatException | IOException e) {
			
			try {
				out = response.getWriter();
				// out은 웹브라우저에 기술하는 대상체라고 생각하자.
				
				out.println("<script type='text/javascript'>alert('파일 다운로드가 불가합니다.'); history.back();</script>");
				
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
		}
		
	}
	
	
	// === #188. 스마트에디터. 드래그앤드롭을 이용한 다중 사진 파일 업로드  ===
	@PostMapping("/image/multiplePhotoUpload.action")
	public void multiplePhotoUpload(HttpServletRequest request, HttpServletResponse response) {
		
		/*
    		1. 사용자가 보낸 파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
    		>>>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
         	우리는 WAS 의 webapp/resources/photo_upload 라는 폴더로 지정해준다.
	 	*/
 
		// WAS 의 webapp 의 절대경로를 알아와야 한다.
		HttpSession session = request.getSession();
		String root = session.getServletContext().getRealPath("/");
		String path = root + "resources" + File.separator + "photo_upload";
		// path 가 첨부파일들을 저장할 WAS(톰캣)의 폴더가 된다.
		
//		System.out.println("~~~ 확인용 path => " + path);
		//  ~~~ 확인용 path => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\photo_upload
	
		File dir = new File(path);
		
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		try {
			String filename = request.getHeader("file-name"); // 파일명(문자열)을 받는다 - 일반 원본파일명
			// 네이버 스마트에디터를 사용한 파일업로드시 싱글파일업로드와는 다르게 멀티파일업로드는 파일명이 header 속에 담겨져 넘어오게 되어있다. 
	         
			/*
	             [참고]
	             HttpServletRequest의 getHeader() 메소드를 통해 클라이언트 사용자의 정보를 알아올 수 있다. 
	   
	            request.getHeader("referer");           // 접속 경로(이전 URL)
	            request.getHeader("user-agent");        // 클라이언트 사용자의 시스템 정보
	            request.getHeader("User-Agent");        // 클라이언트 브라우저 정보 
	            request.getHeader("X-Forwarded-For");   // 클라이언트 ip 주소 
	            request.getHeader("host");              // Host 네임  예: 로컬 환경일 경우 ==> localhost:9090    
			 */
	         
//			System.out.println(">>> 확인용 filename ==> " + filename);
			// >>> 확인용 filename ==> berkelekle%EB%8B%A8%EA%B0%80%EB%9D%BC%ED%8F%AC%EC%9D%B8%ED%8A%B803.jpg 
	        
			InputStream is = request.getInputStream(); // is는 네이버 스마트 에디터를 사용하여 사진첨부하기 된 이미지 파일임.
	         
			String newFilename = fileManager.doFileUpload(is, filename, path);
			
			String ctxPath = request.getContextPath(); // /board

			String strURL = "";
			strURL += "&bNewLine=true&sFileName=" + newFilename;
			strURL += "&sFileURL=" + ctxPath + "/resources/photo_upload/" + newFilename;

			// === 웹브라우저 상에 사진 이미지를 쓰기 === //
			PrintWriter out = response.getWriter();
			out.print(strURL);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	// === #193. 파일 첨부가 있는 댓글쓰기(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/addComment_withAttach.action", produces="text/plain;charset=UTF-8") 
	public String addComment_withAttach(CommentVO commentvo, MultipartHttpServletRequest mrequest) {
		
		// 댓글쓰기에 첨부파일이 있는 경우
		// !!! 먼저 CommentVO 클래스에 가서 첨부파일을 위한 필드를 추가한다. !!! 
		
		// =========== !!! 첨부파일 업로드 시작 !!! ============ // 
	      MultipartFile attach = commentvo.getAttach();
	      
	      if( !attach.isEmpty() ) {
	         // attach(첨부파일)가 비어 있지 않으면(즉, 첨부파일이 있는 경우라면) 
	         
	         /*
	            1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다. 
	            >>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
	                      우리는 WAS의 webapp/resources/files 라는 폴더로 지정해준다.
	                      조심할 것은  Package Explorer 에서  files 라는 폴더를 만드는 것이 아니다.       
	         */
	         // WAS 의 webapp 의 절대경로를 알아와야 한다. 
	         HttpSession session = mrequest.getSession(); 
	         String root = session.getServletContext().getRealPath("/"); 
	                  
	         //   System.out.println("~~~ 확인용 webapp 의 절대경로 => " + root); 
	         //   ~~~ 확인용 webapp 의 절대경로 => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\ 
	                  
	         String path = root+"resources" + File.separator + "files";
	         /* File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
	                  운영체제가 Windows 이라면 File.separator 는  "\" 이고,
	                  운영체제가 UNIX, Linux, 매킨토시(맥) 이라면  File.separator 는 "/" 이다. 
	          */
	                  
	         // path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
	         //   System.out.println("~~~ 확인용 path => " + path);
	         //  ~~~ 확인용 path => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\files 
	                  
	         /*
	             2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기  
	         */
	         String newFileName = "";
	         // WAS(톰캣)의 디스크에 저장될 파일명 
	         
	         byte[] bytes = null;
	         // 첨부파일의 내용물을 담는 것
	         
	         long fileSize = 0;
	         // 첨부파일의 크기 
	         
	         
	         try {
	            bytes = attach.getBytes();
	            // 첨부파일의 내용물을 읽어오는 것
	            
	            String originalFilename = attach.getOriginalFilename();
	            // attach.getOriginalFilename() 이 첨부파일명의 파일명(예: 강아지.png) 이다.
	            
	         //   System.out.println("~~~ 확인용 originalFilename => " + originalFilename); 
	            // ~~~ 확인용 originalFilename => LG_싸이킹청소기_사용설명서.pdf 
	            
	            newFileName = fileManager.doFileUpload(bytes, originalFilename, path); 
	            // 첨부되어진 파일을 업로드 하는 것이다.
	            
	         //   System.out.println("~~~ 확인용 newFileName => " + newFileName); 
	            // ~~~ 확인용 newFileName => 20231124113600755016855987700.pdf 
	                     
	                  
	            /*
	                3. CommentVO commentvo 에 fileName 값과 orgFilename 값과 fileSize 값을 넣어주기  
	            */
	            commentvo.setFileName(newFileName);
	            // WAS(톰캣)에 저장된 파일명(20231124113600755016855987700.pdf)
	                     
	            commentvo.setOrgFilename(originalFilename);
	            // 게시판 페이지에서 첨부된 파일(LG_싸이킹청소기_사용설명서.pdf)을 보여줄 때 사용.
	            // 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용.
	                     
	            fileSize = attach.getSize();  // 첨부파일의 크기(단위는 byte임) 
	            commentvo.setFileSize(String.valueOf(fileSize));
	                     
	         } catch (Exception e) {
	            e.printStackTrace();
	         }   
	      }
	      // =========== !!! 첨부파일 업로드 끝 !!! ============ //
		
		// 댓글쓰기에 첨부파일이 없는 경우 
		
		int n = 0;
		
		try {
			n = service.addComment(commentvo);
			// 댓글쓰기(insert) 및 원게시물(tbl_board 테이블)에 댓글의 개수 증가(update 1씩 증가)하기 
			// 이어서 회원의 포인트를 50점을 증가하도록 한다. (tbl_member 테이블에 point 컬럼의 값을 50 증가하도록 update 한다.) 
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
		JSONObject jsonObj = new JSONObject(); // {}
		jsonObj.put("n", n); // {"n":1} 또는 {"n":0}
		jsonObj.put("name", commentvo.getName()); // {"n":1, "name":"엄정화"} 또는 {"n":0, "name":"서영학"}
		
		return jsonObj.toString(); 
		// "{"n":1, "name":"엄정화"}" 또는 "{"n":0, "name":"서영학"}" 
	}
	
	
	// ==== #199. 파일첨부가 있는 댓글쓰기에서 파일 다운로드 받기 ====
	@GetMapping("/downloadComment.action")
	public void requiredLogin_downloadComment(HttpServletRequest request, HttpServletResponse response) {

		String seq = request.getParameter("seq");
		// 첨부파일이 있는 글번호
		
		/*
			첨부파일이 있는 글번호에서
		   	20231124124825759362098213700.pdf 처럼
	    	이러한 fileName 값을 DB에서 가져와야 한다.
	    	또한 orgFilename 값도 DB에서 가져와야 한다.
		*/
		
		// **** 웹브라우저에 출력하기 시작 ****
		// HttpServletResponse response 객체는 전송되어 온 데이터를 조작해서 결과물을 나타내고자 할 때 쓰인다.
		
		response.setContentType("text/html; charset=UTF-8"); // 한글 깨짐 방지
		
		PrintWriter out = null;
		// out은 웹브라우저에 기술하는 대상체라고 생각하자.
		
		try {
			Integer.parseInt(seq);
			
			CommentVO commentvo = service.getCommentOne(seq);
			
			if(commentvo == null || (commentvo != null && commentvo.getFileName() == null)) {
				// 시퀀스에 해당하는 댓글이 없거나, 댓글은 있지만 첨부파일명이 없을 경우
				
				out = response.getWriter();
				// out은 웹브라우저에 기술하는 대상체라고 생각하자.
				
				out.println("<script type='text/javascript'>alert('존재하지 않는 글번호이거나 첨부파일이 없으므로 파일 다운로드가 불가합니다.'); history.back();</script>");
	            return;
	            
			} else {
				// 정상적으로 다운로드를 할 경우
				
				String fileName = commentvo.getFileName();
				// 2024062809204389191207324300.jpg => 이것이 바로 WAS(톰캣)디스크에 저장된 파일명이다.
				
				String orgFilename = commentvo.getOrgFilename();
				// 쉐보레전면.jpg => 다운로드 시 보여줄 파일명
				
				
				// 첨부파일이 저장되어 있는 WAS(톰캣) 디스크 경로명을 알아와야만 다운로드를 해줄 수 있다.
				// 이 경로는 우리가 파일첨부를 위해서 /addEnd.action 에서 설정해두었던 경로와 똑같아야 한다. 
				// WAS 의 webapp 의 절대경로를 알아와야 한다. 
				HttpSession session = request.getSession(); 
				String root = session.getServletContext().getRealPath("/"); 
				
//				System.out.println("~~~ 확인용 webapp 의 절대경로 => " + root);
				// ~~~ 확인용 webapp 의 절대경로 => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\
				
				String path = root + "resources" + File.separator + "files";
				/*  
					File.separator 는 운영체제에서 사용하는 폴더와 파일의 구분자이다.
					운영체제가 Windows 이라면 File.separator 는  "\" 이고,
					운영체제가 UNIX, Linux, 매킨토시(맥) 이라면  File.separator 는 "/" 이다. 
			    */
				
				// path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
				//   System.out.println("~~~ 확인용 path => " + path);
				//  ~~~ 확인용 path => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\files
				
				
				
				// ***** file 다운로드 하기 ***** //
	            boolean flag = false; // file 다운로드 성공, 실패인지 여부를 알려주는 용도 
	            flag = fileManager.doFileDownload(fileName, orgFilename, path, response); 
	            // file 다운로드 성공 시 flag 는 true,
	            // file 다운로드 실패 시 flag 는 false 를 가진다.
	            
	            if(!flag) {
					// 다운로드가 실패한 경우 메시지를 띄워준다.
					out = response.getWriter();
					// out 은 웹브라우저에 기술하는 대상체라고 생각하자.

					out.println("<script type='text/javascript'>alert('파일 다운로드가 실패되었습니다.'); history.back();</script>");
	            }
				
			}
			
		} catch (NumberFormatException | IOException e) {
			
			try {
				out = response.getWriter();
				// out은 웹브라우저에 기술하는 대상체라고 생각하자.
				
				out.println("<script type='text/javascript'>alert('파일 다운로드가 불가합니다.'); history.back();</script>");
				
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	
	
	@GetMapping(value="/opendata/korea_tour_api.action")
	public String korea_tour() {
		
		return "opendata/korea_tour_api.tiles1";
		// /WEB-INF/views/tiles1/opendata/korea_tour_api.jsp 페이지를 만들어야 한다.
	}
	
	
	@ResponseBody
	@GetMapping(value = "/opendata/seoul_bicycle_rental_JSON.action", produces="text/plain;charset=UTF-8")
	public String seoul_bicycle_rental_JSON(HttpServletRequest request) throws IOException, ParseException {
		
		/*
      		서울시 따릉이대여소 마스터 정보
	      	https://data.seoul.go.kr/dataList/OA-21235/S/1/datasetView.do
         	에서 내려받기(JSON) 한 것임.
	     */
	    
	    /*
	        JSON 파일을 읽고, 파싱하여 값을 읽어오려면 json.simple.JSONParser 을 사용하면 된다. 
         	먼저, 라이브러리를 추가한다 (MAVEN)
         	<dependency>
          		<groupId>com.googlecode.json-simple</groupId>
              	<artifactId>json-simple</artifactId>
              	<version>1.1.1</version>
           	</dependency>
           	
           	※ org.json은 JSON 파일을 읽어서 파싱해주는 기능이 없음 ※
	    */
		
		// json 파일이 저장되어 있는 WAS(톰캣)의 디스크 경로명을 알아와야 한다. 
        // WAS 의 webapp 의 절대경로를 알아와야 한다.
		HttpSession session = request.getSession();
		String root = session.getServletContext().getRealPath("/");
		// /board/src/main/webapp/
		
		String jsonFilePath = root + "resources" + File.separator + "seoul_opendata" + File.separator + "seoul_bicycle_rental.json";
		// File.separator 은 경로의 구분자로서, 윈도우라면 "\"를 말하고,
		// Mac, Unix, Linux 라면 "/"를 말하는 것이다.
		
		// jsonFilePath가 json 파일이 된다.
//		System.out.println("~~~ 확인용 jsonFilePath : " + jsonFilePath);
		// ~~~ 확인용 jsonFilePath : C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\seoul_opendata\seoul_bicycle_rental.json
		
		JSONParser parser = new JSONParser();
		// import 시 org.json.simple.parser.JSONParser 로 해야 한다.
		
		// JSON 파일 읽기
		Reader reader = new FileReader(jsonFilePath);
		org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject)parser.parse(reader);
		// 클래스 import 종류 주의하기 ★★★
		
		org.json.simple.JSONArray jsonArr = (org.json.simple.JSONArray)jsonObj.get("DATA"); // seoul_bicycle_rental.json 에 있는 DATA
		
		return jsonArr.toString();
	}
	
	
	@GetMapping(value="/opendata/seoul_bicycle_rental.action")
	public String opendata_seoul_bicycle_rental() {
      
		return "opendata/seoul_bicycle_rental.tiles1";
		//  /WEB-INF/views/tiles1/opendata/seoul_bicycle_rental.jsp 페이지를 만들어야 한다.
	}
	
	
	@GetMapping("/opendata/seoul_bicycle_rental_insert.action")
	public String seoul_bicycle_rental_insert() {
		
		return "opendata/seoul_bicycle_rental_insert.tiles1";
		//  /WEB-INF/views/tiles1/opendata/seoul_bicycle_rental_insert.jsp 페이지를 만들어야 한다.
	}
	
	
	@ResponseBody
	@PostMapping("/opendata/seoul_bicycle_rental_insert_END.action")
	public String seoul_bicycle_rental_insert_END(Seoul_bicycle_rental_VO vo) {
		
		int n = service.insert_seoul_bicycle_rental(vo);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		return jsonObj.toString();
	}
	
	
	@ResponseBody
	@GetMapping(value = "/opendata/seoul_bicycle_rental_select.action")
	public String seoul_bicycle_rental_select() {

		List<Map<String, String>> mapList = service.select_seoul_bicycle_rental();

		JSONArray jsonArr = new JSONArray();

		if (mapList != null) {
			for (Map<String, String> map : mapList) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("GU", map.get("GU"));
				jsonObj.put("CNT", map.get("CNT"));
				jsonObj.put("PERCNTAGE", map.get("PERCNTAGE"));

				jsonArr.put(jsonObj);
			} // end of for ----------------------
		}

		return jsonArr.toString();
	}
	
	
	// ==== #222. (웹채팅관련4) ====
	@GetMapping("/chatting/multichat.action")
	public String requiredLogin_multichat(HttpServletRequest request, HttpServletResponse response) {
		
		return "chatting/multichat.tiles1";
	}
		
		
	///////////////////// === #인터셉터관련1. 시작 === ///////////////////////////////////////////////
	@GetMapping(value="/anyone/anyone_a.action")
	public String anyone_a() {
	
		return "interceptor_test/anyone/anyone_a.tiles1";
	}
	
	@GetMapping(value="/anyone/anyone_b.action")
	public String anyone_b() {
	
		return "interceptor_test/anyone/anyone_b.tiles1";
	}
	
	@GetMapping(value="/member_only/member_a.action")
	public String member_a() {
	
		return "interceptor_test/member/member_a.tiles1";
	}
	
	@GetMapping(value="/member_only/member_b.action")
	public String member_b() {
	
		return "interceptor_test/member/member_b.tiles1";
	}
	
	@GetMapping(value="/special_member/special_member_a.action")
	public String special_member_a() {
	
		return "interceptor_test/special_member/special_member_a.tiles1";
	}
	
	@GetMapping(value="/special_member/special_member_b.action")
	public String special_member_b() {
	
		return "interceptor_test/special_member/special_member_b.tiles1";
	}
	///////////////////// === #인터셉터관련1. 끝 === ///////////////////////////////////////////////   
		
	
	
	// === #241. 다중 파일첨부가 있는 복수 사용자에게 이메일 보내기 시작 === //
	@GetMapping(value="/emailWrite.action")
	   public String emailWrite() {
	      
	      return "email/emailWrite.tiles1";
	       //  /WEB-INF/views/tiles1/email/emailWrite.jsp 페이지를 만들어야 한다.
	}
	
	
	// === #243. 빈으로 등록 되어진 GoogleMail 클래스 DI(주입) 하기 === //
	@Autowired
	private GoogleMail mail;
	
	
	
	@ResponseBody
	@PostMapping(value="/emailWrite.action", produces="text/plain;charset=UTF-8")
	public String emailWrite(MultipartHttpServletRequest mtp_request) {
		
		String recipient = mtp_request.getParameter("recipient");
		String subject = mtp_request.getParameter("subject");
		String content = mtp_request.getParameter("content");
		
		List<MultipartFile> fileList = mtp_request.getFiles("file_arr");		// !! 주의 !!  getFile은 1개만 받는 것. getFiles는 다중(List)의 파일로 받음
		// "file_arr" 은 /board/src/main/webapp/WEB-INF/views/tiles1/email/emailWrite.jsp 페이지의 314 라인에 보여지는 formData.append("file_arr", item); 의 값이다.
		// MultipartFile interface는 Spring에서 업로드된 파일을 다룰 때 사용되는 인터페이스로 파일의 이름과 실제 데이터, 파일 크기 등을 구할 수 있다.
	       
		
		
		/*
		   >>>> 첨부파일이 업로드 되어질 특정 경로(폴더)지정해주기
		                    우리는 WAS 의 webapp/resources/email_attach_file 라는 폴더로 지정해준다.
		*/
		// WAS 의 webapp 의 절대경로를 알아와야 한다.
		HttpSession session = mtp_request.getSession();
		String root = session.getServletContext().getRealPath("/");
		String path = root + "resources"+File.separator+"email_attach_file";
		// path 가 첨부파일들을 저장할 WAS(톰캣)의 폴더가 된다.
		  
		// System.out.println("~~~~ 확인용 path => " + path);
		// ~~~~ 확인용  path 의 절대경로 => C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\email_attach_file
				
		File dir = new File(path);	// email_attach_file 폴더가 없으면 최초로 만들기
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		// >>>> 첨부파일을 위의 path 경로에 올리기 <<<< //
	    String[] arr_attachFilename = null; // 첨부파일명들을 기록하기 위한 용도
		
	    if(fileList != null && fileList.size() > 0) {
	    	
	    	arr_attachFilename = new String[fileList.size()];
	    	
	    	for(int i=0; i<fileList.size(); i++) {
	    		MultipartFile mtfile = fileList.get(i);
	    		// System.out.println("파일명 : " + mtfile.getOriginalFilename() + " / 파일크기 : " + mtfile.getSize());
	    		/*
	    		  	파일명 : 01.png / 파일크기 : 93231
					파일명 : 02.png / 파일크기 : 70876
					파일명 : 03.png / 파일크기 : 223414
	    		*/
	    		
	    		try {
	    			/*
	                   	File 클래스는 java.io 패키지에 포함되며, 입출력에 필요한 파일이나 디렉터리를 제어하는 데 사용된다.
            			파일과 디렉터리의 접근 권한, 생성된 시간, 경로 등의 정보를 얻을 수 있는 메소드가 있으며, 
                		새로운 파일 및 디렉터리 생성, 삭제 등 다양한 조작 메서드를 가지고 있다.
	                */
	    			
		    		// === MultipartFile 을 File 로 변환하여 탐색기 저장폴더에 저장하기 시작 === //
		    		File attachFile = new File(path + File.separator + mtfile.getOriginalFilename());
		    		mtfile.transferTo(attachFile);	// !!!! 이것이 파일을 업로드 해주는 것이다 !!!! 
		    		/*
	                  	form 태그로 부터 전송받은 MultipartFile mtfile 파일을 지정된 대상 파일(attachFile)로 전송한다.
                   		만약에 대상 파일(attachFile)이 이미 존재하는 경우 먼저 삭제된다.
		    		*/
		    		// 탐색기에서  C:\NCS\workspace_spring_framework\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\board\resources\email_attach_file에 가보면
		    		// 첨부한 파일이 생성되어져 있음을 확인할 수 있다.
		    		// === MultipartFile 을 File 로 변환하여 탐색기 저장폴더에 저장하기 끝 === //
		    		
		    		arr_attachFilename[i] = mtfile.getOriginalFilename();	// 배열속에 첨부파일명들을 기록한다.
		    		
	    		} catch(Exception e) {
	    			e.printStackTrace();
	    		}
	    		
	    	} // end of for
	    	
	    }

    	//System.out.println("recipient : " + recipient);
    	//System.out.println("subject : " + subject);
    	//System.out.println("content : " + content);
    	/*
    	  	recipient : gpwjd1wldms@naver.com;ejss0125@naver.com
			subject : 첨부파일이 있는 메일 보내기 연습
			content : <p>첨부파일이 있는 메일 보내기 연습 입니다.&nbsp;</p>
    	*/
    	
	    JSONObject jsonObj = new JSONObject();
	    
	    String[] arr_recipient = recipient.split("\\;");
	    
	    for(String recipient_email : arr_recipient) {
	    	
	    	Map<String, Object> paraMap = new HashMap<>();
	    	paraMap.put("recipient", recipient_email);
	    	paraMap.put("subject", subject);
	    	paraMap.put("content", content);
	    	
	    	if(fileList != null && fileList.size() > 0) {
	    		paraMap.put("path", path);		// path 는 첨부파일들이 저장된 WAS(톰캣)의 폴더의 경로명이다.
	    		paraMap.put("arr_attachFilename", arr_attachFilename);		// arr_attachFilename 은 첨부파일명들이 저장된 배열이다. 
	    	}
	    	
	    	try {
	    		
				mail.sendmail_withFile(paraMap);
				
				jsonObj.put("result", 1);		// 정상이라면,
				
			} catch (Exception e) {
				e.printStackTrace();
				
				jsonObj.put("result", 0);		// 정상이 아니면,
				break;
			}
	    	
	    } // end of for
	    
	    
	    // 메일 전송 후 업로드한 첨부파일 지우기
	    if(arr_attachFilename != null) {
	    	
	         for(String attachFilename : arr_attachFilename) {
	            try {
	               fileManager.doFileDelete(attachFilename, path);
	            } catch (Exception e) {
	               e.printStackTrace();
	            }
	         } // end of for----------------------
	         
	      }
	    
		return jsonObj.toString();	// "{"result":1}"
		 
	}
	// =======  다중 파일첨부가 있는 복수 사용자에게 이메일 보내기 끝  ======= //
	
	
	@GetMapping(value="/emailWrite/done.action")
	public String emailWrite_done() {
      
		return "email/emailWrite_done.tiles1";
		//  /WEB-INF/views/tiles1/email/emailWrite_done.jsp 페이지를 만들어야 한다.
	}
	
	
	// === #245. Spring Scheduler(스프링스케줄러02)를 사용하여 특정 URL 사이트로 연결하기 === //
	@GetMapping("/branchTimeAlarm.action")
	public ModelAndView branchTimeAlarm(ModelAndView mav, HttpServletRequest request) {
		String message = "12시 50분!! 즐거운 점심시간입니다.";
		String loc = request.getContextPath() + "/index.action";
		
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}





