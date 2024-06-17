package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;


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




	// spring_test 테이블에 select 하기 (날짜 String)
	@Override
	public List<TestVO> test_select() {
	
		List<TestVO> testvoList = sqlsession.selectList("board.test_select");	// 한개는 selectone(pk), 복수개는 selectList
		
		return testvoList;
		
	} // end of public List<TestVO> test_select()



	// spring_test 테이블에 select 하기 3 (날짜 Date)
	@Override
	public List<TestVO2> test_select_vo2() {
		
		List<TestVO2> testvoList = sqlsession.selectList("board.test_select_vo2");	
		
		return testvoList;
		
	} // end of public List<TestVO2> test_select_vo2()


	// spring_test 테이블에 select 하기 2
	@Override
	public List<Map<String, String>> test_select_map() {
		
		List<Map<String, String>> mapList = sqlsession.selectList("board.test_select_map");	
		
		return mapList;
		
	} // end of public List<Map<String, String>> test_select_map()

	
	

	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 (날짜 String)
	@Override
	public int test_insert(TestVO tvo) {
		
		int n = sqlsession.insert("board.test_insert_vo", tvo);
		
		return n;
		
	} // end of public int test_insert(TestVO tvo)


	

	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 3 (날짜 Date)
	@Override
	public int test_insert_vo2(TestVO2 tvo) {
	
		int n = sqlsession.insert("board.test_insert_vo2", tvo);
		
		return n;
		
	} // end of public int test_insert_vo2(TestVO2 tvo)
	
	
	

	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 2
	@Override
	public int test_insert(Map<String, String> paraMap) {
		
		int n = sqlsession.insert("board.test_insert_map", paraMap);
		
		return n;
		
	} // end of public int test_insert(Map<String, String> paraMap)


	
	
//////////////////////////////////////////////////////////////////////////////////////
//게시판 시작
	
	
	// === # 38. 메인페이지 요청 === //
	// 시작페이지에서 이미지 캐러셀
	@Override
	public List<Map<String, String>> imgmapList() {
		
		List<Map<String, String>> imgmapList = sqlsession.selectList("board.imgmapList");	
		
		return imgmapList;
		
	} // end of public List<Map<String, String>> imgmapList()



	// === #46. 로그인 처리하기 === //
	@Override
	public MemberVO getLoginMember(Map<String, String> paraMap) {
		
		MemberVO loginuser = sqlsession.selectOne("board.getLoginMember", paraMap);
		
		//aES256.decrypt(loginuser.getEmail());
		//aES256.decrypt(loginuser.getMobile());
		
		return loginuser;
		
	} // end of public MemberVO getLoginMember(Map<String, String> paraMap)

	
	

	// === tbl_loginhistory 테이블에 로그인 기록 입력하기 === // 
	@Override
	public void insert_tbl_loginhistory(Map<String, String> paraMap) {
		
		sqlsession.insert("board.insert_tbl_loginhistory", paraMap);
		// 리턴 타입은 void로 없다.
		
	} // end of public void insert_tbl_loginhistory(Map<String, String> paraMap)
	
	
	
	


	// 마지막으로 로그인 한 날짜시간이 현재시각으로 부터 1년이 지났으면 휴면으로 지정
	@Override
	public void updateIdle(String userid) {	// update도 int가 나오지만 DB에만 넣어주면 되기 때문에 void로..
		
		sqlsession.update("board.updateIdle", userid);
		
	} // end of public void updateIdle(String string)


	

	// === #56. 파일첨부가 없는 글쓰기 === //
	@Override
	public int add(BoardVO boardvo) {
		
		int n = sqlsession.insert("board.add", boardvo);
		
		return n;
		
	} // end of public int add(BoardVO boardvo)



	// === #60. 페이징 처리를 안한, 검색어가 없는 전체 글목록 보여주기 === //
	@Override
	public List<BoardVO> boardListNoSearch() {
		
		List<BoardVO> boardList = sqlsession.selectList("board.boardListNoSearch");
		
		return boardList;
		
	} // end of public List<BoardVO> boardListNoSearch()



	// === #64. 글 1개 조회하기  === //
	@Override
	public BoardVO getView(Map<String, String> paraMap) {
		
		BoardVO boardvo = sqlsession.selectOne("board.getView", paraMap);
		
		return boardvo;
		
	} // end of public BoardVO getView(Map<String, String> paraMap)



	// === #66. 글조회수 1 증가시키기 === //	 
	@Override
	public int increase_readCount(String seq) {
		
		int n = sqlsession.update("board.increase_readCount", seq);
		
		return n;
		
	} // end of public int increase_readCount(String seq)



	
	



	
	







}
