package com.spring.app.employees.service;

import java.util.List;

public interface EmpService {

	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 
	List<String> deptIdList();

}
