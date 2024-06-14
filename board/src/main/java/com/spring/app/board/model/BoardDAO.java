package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;

public interface BoardDAO {

	// spring_test 테이블에 insert 하기 
	int test_insert();

	// spring_test 테이블에 select 하기 (날짜 String)
	List<TestVO> test_select();
	
	// spring_test 테이블에 select 하기 3 (날짜 Date)
	List<TestVO2> test_select_vo2();
	
	// spring_test 테이블에 select 하기 2
	List<Map<String, String>> test_select_map();
	
	
	
	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 (날짜 String)
	int test_insert(TestVO tvo);
	
	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 3 (날짜 Date)
	int test_insert_vo2(TestVO2 tvo);

	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 2
	int test_insert(Map<String, String> paraMap);

	
	
//////////////////////////////////////////////////////////////////////////////////////
// 게시판 시작
	
	
	// 시작페이지에서 이미지 캐러셀
	List<Map<String, String>> imgmapList();

	// 로그인 처리하기
	MemberVO getLoginMember(Map<String, String> paraMap);

	// 마지막으로 로그인 한 날짜시간이 현재시각으로 부터 1년이 지났으면 휴면으로 지정
	void updateIdle(String userid);


	

	



	
	
	
	
	
}
