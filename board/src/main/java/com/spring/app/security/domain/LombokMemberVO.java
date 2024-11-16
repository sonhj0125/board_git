package com.spring.app.security.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LombokMemberVO {
	private String member_id;          // 회원아이디 
	private String member_pwd;         // 비밀번호 (Spring Security 에서 제공해주는 SHA-256 암호화를 사용하는 대상) 
	private String member_name;        // 회원명 
	private String email;              // 이메일 (AES-256 암호화/복호화 대상) 
	private String hp1;                // DB 컬럼명이 아님 
	private String hp2;                // DB 컬럼명이 아님
	private String hp3;                // DB 컬럼명이 아님 
	private String mobile;             // 연락처 (AES-256 암호화/복호화 대상) 
	private String postcode;           // 우편번호 
	private String address;            // 주소 
	private String detailaddress;      // 상세주소 
	private String extraaddress;       // 참고항목 
	private String gender;             // 성별   남자:1  / 여자:2 
	private String birthday;           // 생년월일 
	private String registerday;        // 가입일자 
	private String modify_date;        // 회원정보수정일자 
	private String lastpwdchangedate;  // 마지막으로 암호를 변경한 날짜
	private int enabled;               // -- Spring Security 에서는 enabled 컬럼의 값이 1이어야만 회원이 존재하는것으로 인식한다. 반드시 enabled 컬럼이 존재해야만 한다.!!! 
   
	private int num;                   // security_member_authority 테이블의 컬럼임 
	private String authority;          // security_member_authority 테이블의 컬럼임   
}
