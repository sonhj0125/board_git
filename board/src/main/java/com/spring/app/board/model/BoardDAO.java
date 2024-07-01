package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.CommentVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.Seoul_bicycle_rental_VO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;

public interface BoardDAO {

	// spring_test 테이블에 insert 하기
	int test_insert();

	// spring_test 테이블에 select 하기
	List<TestVO> test_select();
	List<TestVO2> test_select_vo2();
	List<Map<String, String>> test_select_map();
	
	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 
	int test_insert(TestVO tvo);
	int test_insert_vo2(TestVO2 tvo);
	int test_insert(Map<String, String> paraMap);

	
	/////////////////////////////////////////////////////////
	
	// 시작페이지에서 캐러절을 보여주는것
	List<Map<String, String>> getImgfilenameList();

	// 로그인 처리하기
	MemberVO getLoginMember(Map<String, String> paraMap);

	// tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기 
	void updateIdle(String userid);

	// tbl_loginhistory 테이블에 로그인 기록 입력하기 
	void insert_tbl_loginhistory(Map<String, String> paraMap);
	
	// 파일첨부가 없는 글쓰기
	int add(BoardVO boardvo);

	// 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 
	List<BoardVO> boardListNoSearch();

	// 글 1개 조회하기
	BoardVO getView(Map<String, String> paraMap);

	// 글조회수 1증가 하기 
	int increase_readCount(String seq);

	// 1개글 수정하기
	int edit(BoardVO boardvo);

	// 1개글 삭제하기
	int del(String seq);

	///////////////////////////////////////////////////////////////////////////////////////////////////////  
	int addComment(CommentVO commentvo);  // 댓글쓰기(tbl_comment 테이블에 insert) 
	int updateCommentCount(String parentSeq); // tbl_board 테이블에 commentCount 컬럼이 1증가(update)
	int updateMemberPoint(Map<String, String> paraMap); // tbl_member 테이블의 point 컬럼의 값을 50점을 증가(update) 
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

	// 원게시물에 딸린 댓글들을 조회해오기
	List<CommentVO> getCommentList(String parentSeq);

	// 댓글 수정(Ajax 로 처리)
	int updateComment(Map<String, String> paraMap);

	// 댓글 삭제(Ajax 로 처리)
	int deleteComment(String string);

	// 댓글삭제시 tbl_board 테이블에 commentCount 컬럼이 1감소(update)
	int updateCommentCount_decrease(String string);
	
	// CommonAop 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것
	void pointPlus(Map<String, String> paraMap);

	// 페이징 처리를 안한 검색어가 있는 전체 글목록 보여주기 
	List<BoardVO> boardListSearch(Map<String, String> paraMap);

	// 검색어 입력시 자동글 완성하기 
	List<String> wordSearchShow(Map<String, String> paraMap);

	// 총 게시물 건수(totalCount) 구하기 - 검색이 있을 때와 검색이 없을때 로 나뉜다. 
	int getTotalCount(Map<String, String> paraMap);

	// 글목록 가져오기(페이징 처리 했으며, 검색어가 있는 것 또는 검색어 없는것 모두 포함 한 것 
	List<BoardVO> boardListSearch_withPaging(Map<String, String> paraMap);

	// 원게시물에 딸린 댓글내용들을 페이징 처리하기
	List<CommentVO> getCommentList_Paging(Map<String, String> paraMap);

	// 페이징 처리시 보여주는 순번을 나타내기 위한 것임. 
	int getCommentTotalCount(String parentSeq);

	
	// === 서울따릉이 오라클 입력 하기 === //
	int insert_seoul_bicycle_rental(Seoul_bicycle_rental_VO vo);

	// === 서울따릉이 오라클 조회 하기 === //
	List<Map<String, String>> select_seoul_bicycle_rental();

	// tbl_board 테이블에서 groupno 컬럼의 최대값 알아오기 
	int getGroupnoMax();

	// 글쓰기(첨부파일이 있는 경우)
	int add_withFile(BoardVO boardvo);

	

	

	

	
	
}
