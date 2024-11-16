package com.spring.app.security.model;

import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.security.domain.LombokMemberVO;


@Repository
public class MemberDAO_imple implements MemberDAO {
	  
  /*	
	@Resource
	private SqlSessionTemplate sqlsession;  // 로컬DB mymvc_user 에 연결
  */
  // 또는
	@Autowired
  	@Qualifier("sqlsession") // /board/src/main/webapp/WEB-INF/spring/root-context.xml 의 bean에서 id 가 sqlsession 인 bean 을 주입해라는 뜻이다.             
	private SqlSessionTemplate sql;
	// import 할때 org.springframework.beans.factory.annotation.Qualifier 으로 해야 한다.	
	
	@Override
	public int member_id_check(String member_id) {
		int n = sql.selectOne("security_member.member_id_check", member_id);
		return n;
	}
	
	@Override
	public int emailDuplicateCheck(String email) {
		int n = sql.selectOne("security_member.emailDuplicateCheck", email);
		return n;
	}
	
	@Override 
	public int insert_member(LombokMemberVO membervo) throws Exception {
		int n = sql.insert("security_member.insert_member", membervo);
		return n;
	}
	
	@Override
	public void insert_security_loginhistory(Map<String, String> paraMap) {
		sql.insert("security_member.insert_security_loginhistory", paraMap);
	}

	@Override
	public int insert_member_authority(LombokMemberVO membervo) throws Exception {
		int n = sql.insert("security_member.insert_member_authority", membervo);
		return n;
	}

	@Override
	public Map<String, String> get_member(String member_id) {
		Map<String, String> map = sql.selectOne("security_member.get_member", member_id);
		return map;
	}
	
}
