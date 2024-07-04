package com.spring.app.employees.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

// === #204. DAO 인터페이스에서 @Mapper 를 단다.
@Mapper // @Mapper 어노테이션을 붙여서 DAO 역할의 Mapper 인터페이스 파일을 만든다. Inter_EmpDAO_Mapper 인터페이스를 구현한 DAO 클래스를 생성하면 오류가 뜨므로 절대로 DAO 클래스를 생성하면 안된다.!!!
		// @Mapper 어노테이션을 사용하면 빈으로 등록되며 Service단에서 @Autowired 하여 사용할 수 있게 된다.
public interface EmpDAO {

	// === SqlSessionTemplate 을 사용하지 않는 Mapper Interface 예제(myBatis 3.0 이상 부터 사용가능함) === // 
	
	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 
	List<String> deptIdList();

	// employees 테이블에서 조건에 만족하는 사원들을 가져오기
	List<Map<String, String>> employeeList(Map<String, Object> paraMap);


	
	
}
