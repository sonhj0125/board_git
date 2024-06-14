package com.spring.app.board.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;
import com.spring.app.board.model.BoardDAO;
import com.spring.app.common.AES256;

//==== #31. Service 선언 ====
//트랜잭션 처리를 담당하는 곳, 업무를 처리하는 곳, 비지니스(Business)단 
// @Component	// bean에 올린다. 생략할 수 있다!
@Service	// 역할 : Service를 말함	// @Service 속에는 @Component 기능이 포함되어 있음
public class BoardService_imple implements BoardService {

// DAO 호출해야함 ==> 의존객체 호출
// === #34. 의존객체 주입하기(DI: Dependency Injection) ===
@Autowired	// Type에 따라 알아서 Bean 을 주입해준다.
private BoardDAO dao; // 처음에는 null, 주입시키면 null 이 아니게 됨.
// BoardDAO_imple은 Type이 하나밖에 없으므로 내 맘대로 dao라고 이름 붙여서 사용가능


// === #45. 양방향 암호화 알고리즘인 AES256 를 사용하여 복호화 하기 위한 클래스 의존객체 주입하기(DI: Dependency Injection) ===
@Autowired
private AES256 aES256;
// Type 에 따라 Spring 컨테이너가 알아서 bean 으로 등록된 com.spring.board.common.AES256 의 bean 을  aES256 에 주입시켜준다. 
// 그러므로 aES256 는 null 이 아니다.
// com.spring.app.common.AES256 의 bean 은 /webapp/WEB-INF/spring/appServlet/servlet-context.xml 파일에서 bean 으로 등록시켜주었음.



    
    
    
    
	
	@Override
	public int test_insert() {
		
		int n = dao.test_insert();
		
		return n;
		
	} // end of public int test_insert()

	
	
	
	@Override
	public List<TestVO> test_select() {

		List<TestVO> testvoList = dao.test_select();
		
		return testvoList;
		
	} // end of public List<TestVO> test_select()




	@Override
	public List<TestVO2> test_select_vo2() {
		
		List<TestVO2> testvoList = dao.test_select_vo2();
		
		return testvoList;
		
	} // end of public List<TestVO2> test_select_vo2()




	@Override
	public List<Map<String, String>> test_select_map() {
		
		List<Map<String, String>> mapList = dao.test_select_map();
		
		return mapList;
		
	} // end of public List<Map<String, String>> test_select_map()




	@Override
	public int test_insert(TestVO tvo) {
		
		int n = dao.test_insert(tvo);
		
		return n;
		
	} // end of public int test_insert(TestVO tvo)




	@Override
	public int test_insert_vo2(TestVO2 tvo) {
		
		int n = dao.test_insert_vo2(tvo);
		
		return n;
		
	} // end of public int test_insert_vo2(TestVO2 tvo)



	@Override
	public int test_insert(Map<String, String> paraMap) {
		
		int n = dao.test_insert(paraMap);
		
		return n;
		
	} // end of public int test_insert(Map<String, String> paraMap)

	
	
	

//////////////////////////////////////////////////////////////////////////////////////
//게시판 시작
	
	
	// === # 37. 메인페이지 요청 === //
/*
	// 시작페이지에서 이미지 캐러셀
	@Override
	public List<Map<String, String>> getImgfilenameList() {
		
		List<Map<String, String>> imgmapList = dao.imgmapList();
		
		return imgmapList;
		
	} // end of public List<Map<String, String>> getImgfilenameList()
*/


	// 시작페이지에서 이미지 캐러셀
	@Override
	public ModelAndView index(ModelAndView mav) {
		
		List<Map<String, String>> imgmapList = dao.imgmapList();
		
		mav.addObject("imgmapList", imgmapList);
		
		mav.setViewName("main/index.tiles1");
		// /WEB-INF/views/tiles1/main/index.jsp 페이지
		
		return mav;
	} // end of public ModelAndView index(ModelAndView mav)



	// === #42. 로그인 처리하기 === //
	@Override
	public MemberVO getLoginMember(Map<String, String> paraMap) {
		
		MemberVO loginuser = dao.getLoginMember(paraMap);
		
		
		// === #48. aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. === 
	    //          또한 암호변경 메시지와 휴면처리 유무 메시지를 띄우도록 업무처리를 하도록 한다.
		
		if(loginuser != null && loginuser.getPwdchangegap() >= 3) {
			// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 
			loginuser.setRequirePwdChange(true);	// 로그인시 암호를 변경해라는 alert 를 띄우도 록 한다.
			
		}
		
		if(loginuser != null && loginuser.getLastlogingap() >= 12 && loginuser.getIdle() == 0) {
			// 마지막으로 로그인 한 날짜시간이 현재시각으로 부터 1년이 지났으면 휴면으로 지정
			loginuser.setIdle(1);
			
			// === tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기 === // 
			dao.updateIdle(paraMap.get("userid"));
			
		}
		
		
		if(loginuser != null)	{
			
			try {
				String email = aES256.decrypt(loginuser.getEmail());
				String mobile = aES256.decrypt(loginuser.getMobile());
				
				loginuser.setEmail(email);
				loginuser.setMobile(mobile);
				
			} catch (UnsupportedEncodingException | GeneralSecurityException e) {
				e.printStackTrace();
			}
			
			
		}
		
		return loginuser;
		
	} // end of public MemberVO getLoginMember(Map<String, String> paraMap)




}
