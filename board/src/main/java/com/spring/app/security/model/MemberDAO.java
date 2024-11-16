package com.spring.app.security.model;

import java.util.Map;

import com.spring.app.security.domain.LombokMemberVO;


public interface MemberDAO {
	
	int member_id_check(String member_id);
	
	int emailDuplicateCheck(String email);
	
	int insert_member(LombokMemberVO membervo) throws Exception;
	
	void insert_security_loginhistory(Map<String, String> paraMap);

	int insert_member_authority(LombokMemberVO membervo) throws Exception;
	
	Map<String, String> get_member(String member_id);
	
}
