package com.spring.app.security.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.security.model.MemberDAO;
import com.spring.app.common.AES256;
import com.spring.app.security.domain.LombokMemberVO;


@Service
public class MemberService_imple implements MemberService {

	@Autowired
	private MemberDAO memberDao;
	
	@Autowired
	private AES256 aES256;

	
	@Override
	public int member_id_check(String member_id) {
		int n = memberDao.member_id_check(member_id);
		return n;
	}
	

	@Override
	public int emailDuplicateCheck(String email) {
		int n = 0;
		try {
			n = memberDao.emailDuplicateCheck(aES256.encrypt(email));
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		return n;
	}
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED, rollbackFor={Throwable.class})
	public int insert_member(LombokMemberVO membervo) throws Exception {
		
		int result = 0;
		
		membervo.setEmail(aES256.encrypt(membervo.getEmail()));  // e메일 양방향 암호화 하기 
		
		if(membervo.getHp1() != null && membervo.getHp1().trim().length() > 0 &&
		   membervo.getHp2() != null && membervo.getHp2().trim().length() > 0 &&
		   membervo.getHp3() != null && membervo.getHp3().trim().length() > 0 ) {
		   
		   membervo.setMobile(aES256.encrypt(membervo.getHp1() + "-" + membervo.getHp2() + "-" + membervo.getHp3()));
		   // 휴대폰번호 양방향 암호화 하기 
		}
					
		// 회원정보 저장하기
		int n1 = memberDao.insert_member(membervo);
		
		// 권한저장하기
		if(n1==1) {
			membervo.setAuthority("ROLE_USER");
		    result = memberDao.insert_member_authority(membervo);
		}
		
		return result;
	}
	
	
	@Override
	public void insert_security_loginhistory(Map<String, String> paraMap) {
		memberDao.insert_security_loginhistory(paraMap); 
	}


	@Override
	public Map<String, String> get_member(String member_id) {
		Map<String, String> map = memberDao.get_member(member_id);
		return map;
	}

	
}
