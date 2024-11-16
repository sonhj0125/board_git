package com.spring.app.security.service;

import java.util.Map;

import com.spring.app.security.domain.LombokMemberVO;

public interface MemberService {

	int member_id_check(String member_id);

	int emailDuplicateCheck(String email);
	
	int insert_member(LombokMemberVO membervo) throws Exception;
	
	void insert_security_loginhistory(Map<String, String> paraMap);
	
	Map<String, String> get_member(String member_id);

	
	
}
