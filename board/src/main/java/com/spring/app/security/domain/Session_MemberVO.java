package com.spring.app.security.domain;

// HttpSession(세션)에 저장할 MemberVO 정보를 가진 클래스
public class Session_MemberVO {

	private String member_id;
	private String member_name;
		
	public String getMember_id() {
		return member_id;
	}
	
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	
	public String getMember_name() {
		return member_name;
	}
	
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
		
}
