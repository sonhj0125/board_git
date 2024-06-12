package com.spring.app.board.model;

import java.util.List;

import com.spring.app.board.domain.TestVO;

public interface BoardDAO {

	// spring_test 테이블에 insert 하기 
	int test_insert();

	// spring_test 테이블에 select 하기 
	List<TestVO> test_select();
	
	
	
	
	
}
