package com.spring.app.employees.service;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

public interface EmpService {

	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 
	List<String> deptIdList();

	// employees 테이블에서 조건에 만족하는 사원들을 가져오기
	List<Map<String, String>> employeeList(Map<String, Object> paraMap);

	// employees 테이블에서 조건에 만족하는 사원들을 가져와서 Excel 파일로 만들기
	void employeeList_to_Excel(Map<String, Object> paraMap, Model model);

}
