package com.spring.app.board.domain;

import org.springframework.web.multipart.MultipartFile;

// == #82. 댓글용 VO 생성하기
//    먼저, 오라클에서 tbl_comment 테이블을 생성한다.
//    또한 tbl_board 테이블에 commentCount 컬럼을 추가한다. ===== 
public class CommentVO {

	private String seq;        // 댓글번호
	private String fk_userid;  // 사용자ID
	private String name;       // 성명
	private String content;    // 댓글내용
	private String regDate;    // 작성일자
	private String parentSeq;  // 원게시물 글번호
	private String status;     // 글삭제여부
	
	
	// ==== #194. 댓글쓰기에 있어서 파일첨부가 있는 것 시작 ==== //
	
	// !!! 먼저, 댓글쓰기에 파일첨부까지 한 것을 위해서 오라클에서 tbl_comment 테이블에 3개 컬럼(fileName, orgFilename, fileSize)을 추가한 다음에 아래의 작업을 한다. !!!  
	private MultipartFile attach;
	/* form 태그에서 type="file" 인 파일을 받아서 저장되는 필드이다. 
	       진짜파일 ==> WAS(톰캣) 디스크에 저장됨.
                조심할것은 MultipartFile attach 는 오라클 데이터베이스 tbl_comment 테이블의 컬럼이 아니다.   
	   /board/src/main/webapp/WEB-INF/views/tiles1/board/view.jsp 파일에서 input type="file" 인 name 의 이름(attach)과  
	       동일해야만 파일첨부가 가능해진다.!!!!
     */
		
	private String fileName;    // WAS(톰캣)에 저장될 파일명(2024070109291535243254235235234.png) 
	private String orgFilename; // 진짜 파일명(강아지.png)  // 사용자가 파일을 업로드 하거나 파일을 다운로드 할때 사용되어지는 파일명  
	private String fileSize;    // 파일크기 
	// ==== 댓글쓰기에 있어서 파일첨부가 있는 것 끝 ==== //
	
	public String getSeq() {
		return seq;
	}
	
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public String getFk_userid() {
		return fk_userid;
	}
	
	public void setFk_userid(String fk_userid) {
		this.fk_userid = fk_userid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getRegDate() {
		return regDate;
	}
	
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	
	public String getParentSeq() {
		return parentSeq;
	}
	
	public void setParentSeq(String parentSeq) {
		this.parentSeq = parentSeq;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public MultipartFile getAttach() {
		return attach;
	}

	public void setAttach(MultipartFile attach) {
		this.attach = attach;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOrgFilename() {
		return orgFilename;
	}

	public void setOrgFilename(String orgFilename) {
		this.orgFilename = orgFilename;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
	
}
