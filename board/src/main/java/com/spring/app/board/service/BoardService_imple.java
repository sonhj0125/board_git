package com.spring.app.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.spring.app.board.domain.TestVO;
import com.spring.app.board.model.BoardDAO;

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
	public int test_insert(TestVO tvo) {
		
		int n = dao.test_insert(tvo);
		
		return n;
	} // end of public int test_insert(TestVO tvo)

}
