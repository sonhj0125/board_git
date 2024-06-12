package com.spring.app.board.model;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.board.domain.TestVO;


//==== #32. Repository(DAO) 선언 ====
// @Component	// bean에 올린다.
@Repository	// 역할 : DAO를 말함		// boardDAO_imple(클래스명 - 첫글자는 소문자)이 객체 아이디가 된다.!!  // @Repository 속에는 @Component 기능이 포함되어 있음
public class BoardDAO_imple implements BoardDAO {
	// === #33. 의존객체 주입하기(DI: Dependency Injection) ===
	// >>> 의존 객체 자동 주입(Automatic Dependency Injection)은
    //     스프링 컨테이너가 자동적으로 의존 대상 객체를 찾아서 해당 객체에 필요한 의존객체를 주입하는 것을 말한다. 
    //     단, 의존객체는 스프링 컨테이너속에 bean 으로 등록되어 있어야 한다. 

    //     의존 객체 자동 주입(Automatic Dependency Injection)방법 3가지 
    //     1. @Autowired ==> Spring Framework에서 지원하는 어노테이션이다. 
    //                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다. ==> 가장 많이 사용
   
    //     2. @Resource  ==> Java 에서 지원하는 어노테이션이다.
    //                       스프링 컨테이너에 담겨진 의존객체를 주입할때 필드명(이름)을 찾아서 연결(의존객체주입)한다. ==> jdk 11은 존재하지 않음 ==> 1번만 사용!
   
    //     3. @Inject    ==> Java 에서 지원하는 어노테이션이다.
    //                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.
	
	// 과거, java 에서 private SqlSessionTemplate abc = new SqlSessionTemplate(); 이런 식으로 해왔으나, 여기선 그렇게 할 필요 없음! 아래와 같이 한다.
	
/*	
	@Autowired
	private SqlSessionTemplate abc;
	// Type 에 따라 Spring 컨테이너가 알아서 root-context.xml 에 생성된 org.mybatis.spring.SqlSessionTemplate 의 bean 을  abc 에 주입시켜준다. 
    // 그러므로 abc 는 null 이 아니다.
 	
*/
	
/*	
	@Inject
	private SqlSessionTemplate abc;
*/	
	
/*	
	@Resource
	private SqlSessionTemplate sqlsession;		// 로컬DB mymvc_user 에 연결
	
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession 인 bean을 주입하라는 뜻이다. 
    // 그러므로 sqlsession 는 null 이 아니다.
    
	@Resource
	private SqlSessionTemplate sqlsession_2;	// 로컬DB hr 에 연결
	
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession_2 인 bean을 주입하라는 뜻이다. 
    // 그러므로 sqlsession_2 는 null 이 아니다.
*/	
	

	@Autowired
	@Qualifier("sqlsession")	// "" 안에 아이디값(해당 bean) 넣기  (== MyMVC를 sqlsession에 넣어주는 것) 
	private SqlSessionTemplate sqlsession;
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession 인 bean을 주입하라는 뜻이다. 
    // 그러므로 sqlsession 는 null 이 아니다.
	
	@Autowired
	@Qualifier("sqlsession_2")	// "" 안에 아이디값(해당 bean) 넣기  (== HR를 sqlsession_2에 넣어주는 것) 
	private SqlSessionTemplate sqlsession_2;
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession_2 인 bean을 주입하라는 뜻이다. 
    // 그러므로 sqlsession_2 는 null 이 아니다.
	
	
	
	// spring_test 테이블에 insert 하기 
	@Override
	public int test_insert() {
		
		int n1 = sqlsession.insert("board.test_insert");	// "namespace.insert의 id" mapper에 namespace를 보고 간다. 
		int n2 = sqlsession_2.insert("hr.exam_insert");
		
		return n1*n2;
		
	} // end of public int test_insert()




	// spring_test 테이블에 select 하기 
	@Override
	public List<TestVO> test_select() {
	
		List<TestVO> testvoList = sqlsession.selectList("board.test_select");	// 한개는 selectone(pk), 복수개는 selectList
		
		return testvoList;
		
	} // end of public List<TestVO> test_select()

}
