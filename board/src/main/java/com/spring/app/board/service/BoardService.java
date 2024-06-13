package com.spring.app.board.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;

public interface BoardService {
	
	int test_insert();

	List<TestVO> test_select();
	List<TestVO2> test_select_vo2();
	List<Map<String, String>> test_select_map();

	
	int test_insert(TestVO tvo);	// 메소드의 이름은 같고, 파라미터는 다른 method의 오버로딩
	int test_insert_vo2(TestVO2 tvo);
	int test_insert(Map<String, String> paraMap);	// 메소드의 이름은 같고, 파라미터는 다른 method의 오버로딩

	
	
//////////////////////////////////////////////////////////////////////////////////////
//게시판 시작
	
	
	// 시작페이지에서 이미지 캐러셀
//	List<Map<String, String>> getImgfilenameList();

	
	// 시작페이지에서 이미지 캐러셀
	ModelAndView index(ModelAndView mav);

	
	// 로그인 처리하기
	MemberVO getLoginMember(Map<String, String> paraMap);

	


	

	



	
}
