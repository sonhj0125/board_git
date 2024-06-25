package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.CommentVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.Seoul_bicycle_rental_VO;
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



	
	// === #74. 1개 글 수정하기 === //
	@Override
	public int edit(BoardVO boardvo) {
		
		int n = sqlsession.update("board.edit", boardvo);
		
		return n;
		
	} // end of public int edit(BoardVO boardvo)



	
	// === #79. 1개 글 삭제하기  === //
	@Override
	public int del(String seq) {
		
		int n = sqlsession.delete("board.del", seq);
		
		return n;
		
	} // end of public int del(String seq)


	//////////////////////////////////////////////////////////////////////////////////
	
	// === #86. 댓글쓰기  === //
	// 댓글쓰기(tbl_comment 테이블에 insert)
	@Override
	public int addComment(CommentVO commentvo) {
		
		int n = sqlsession.insert("board.addComment", commentvo);
		
		return n;
		
	} // end of public int addComment



	// === #87.-1 tbl_board 테이블에 commentCount 컬럼이 1증가(update) === //
	@Override
	public int updateCommentCount(String parentSeq) {
		
		int n = sqlsession.update("board.updateCommentCount", parentSeq);
		
		return n;
		
	} // end of public int updateCommentCount



	// === #87.-2 tbl_member 테이블의 point 컬럼의 값을 50점을 증가(update) === //
	@Override
	public int updateMemberPoint(Map<String, String> paraMap) {
		
		int n = sqlsession.update("board.updateMemberPoint", paraMap);
		
		return n;
		
	} // end of public int updateMemberPoint
	//////////////////////////////////////////////////////////////////////////////////



	// === #92. 원 게시물에 딸린 댓글들을 조회해오기  === //
	@Override
	public List<CommentVO> getCommentList(String parentSeq) {
		
		List<CommentVO> commentList = sqlsession.selectList("board.getCommentList", parentSeq);
		
		return commentList;
		
	} // end of public List<CommentVO> getCommentList(String parentSeq)



	// === #97.댓글 수정(Ajax 로 처리) === //
	@Override
	public int updateComment(Map<String, String> paraMap) {
		
		int n = sqlsession.update("board.updateComment", paraMap);
		
		return n;
		
	} // end of public int updateComment



	// === #102.-1 댓글 수정(Ajax 로 처리) === //
	@Override
	public int deleteComment(String seq) {
		
		int n =  sqlsession.delete("board.deleteComment", seq);
		
		return n;
		
	} // end of public int deleteComment



	// === #102.-2 댓글삭제시 tbl_board 테이블에 commentCount 컬럼이 1감소(update) === //
	@Override
	public int updateCommentCount_decrease(String string) {
	
		int n =  sqlsession.update("board.updateCommentCount_decrease", string);
		
		return n;
		
	} // end of public int updateCommentCount_decrease



	// === #107. CommonAop 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것    === //
	@Override
	public void pointPlus(Map<String, String> paraMap) {
	
		sqlsession.update("board.pointPlus", paraMap);
		
	} // end of public void pointPlus(Map<String, String> paraMap)



	// === #112. 페이징 처리를 안한, 검색어가 있는 전체 글목록 보여주기    === //
	@Override
	public List<BoardVO> boardListSearch(Map<String, String> paraMap) {
		
		List<BoardVO> boardList = sqlsession.selectList("board.boardListSearch", paraMap);
		
		return boardList;
		
	} // end of public List<BoardVO> boardListSearch



	
	// === #118. 검색어 입력시 자동글 완성하기 6  === //
	@Override
	public List<String> wordSearchShow(Map<String, String> paraMap) {
		
		List<String> wordList = sqlsession.selectList("board.wordSearchShow", paraMap);
		
		return wordList;
		
	} // end of public List<String> wordSearchShow

	


	// === #124. 총 게시물 건수 (totalCount) 구하기 - 검색이 있을 때와 검색이 없을때로 나뉜다. === //
	@Override
	public int getTotalCount(Map<String, String> paraMap) {
		
		int totalCount = sqlsession.selectOne("board.getTotalCount", paraMap);
		
		return totalCount;
		
	} // end of public int getTotalCount


	
	

	// === #127. 글목록 가져오기 (페이징 처리 했으며, 검색어가 있는 것 또는 검색어 없는 것 모두 포함한 것이다.) === //
	@Override
	public List<BoardVO> boardListSearch_withPaging(Map<String, String> paraMap) {
		
		List<BoardVO> boardList = sqlsession.selectList("board.boardListSearch_withPaging", paraMap);
		
		return boardList;
		
	} // end of public List<BoardVO> boardListSearch_withPaging



	// === #148. 원게시물에 딸린 댓글내용들을 페이징 처리하기(Ajax 로 처리) === //
	@Override
	public List<CommentVO> getCommentList_paging(Map<String, String> paraMap) {
	
		List<CommentVO> commentList = sqlsession.selectList("board.getCommentList_paging", paraMap);
		
		return commentList;
		
	} // end of public List<CommentVO> getCommentList_paging



	// === #151. 페이징 처리시 보여주는 순번을 나타내기 위한 것  === //
	@Override
	public int getCommentTotalCount(String parentSeq) {
		
		int totalCount = sqlsession.selectOne("board.getCommentTotalCount", parentSeq);
		
		return totalCount;
		
	} // end of public int getCommentTotalCount



	// === 서울 따릉이 오라클 입력하기 === // 
	@Override
	public int insert_seoul_bicycle_rental(Seoul_bicycle_rental_VO vo) {
	
		int n = sqlsession.insert("board.insert_seoul_bicycle_rental", vo);
		
		return n;
		
	} // end of public int insert_seoul_bicycle_rental(Seoul_bicycle_rental_VO vo)



	// === 서울 따릉이 오라클 조회하기 === //
	@Override
	public List<Map<String, String>> select_seoul_bicycle_rental() {
	
		List<Map<String, String>> mapList = sqlsession.selectList("board.select_seoul_bicycle_rental");
		
		return mapList;
		
	} // end of public List<Map<String, String>> select_seoul_bicycle_rental
	



	
	







}
