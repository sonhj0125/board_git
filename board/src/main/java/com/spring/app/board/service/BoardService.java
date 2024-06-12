package com.spring.app.board.service;

import java.util.List;

import com.spring.app.board.domain.TestVO;

public interface BoardService {
	
	int test_insert();

	List<TestVO> test_select();

	int test_insert(TestVO tvo);	// 메소드의 이름은 같고, 파라미터는 다른 method의 오버로딩

	
}
