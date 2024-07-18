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

	// Excel 파일을 업로드하면 엑셀 데이터를 데이터베이스 테이블에 insert 해주는 예제
	int add_employeeList(List<Map<String, String>> paraMapList);

	// 인사관리 페이지에 접속한 이후에, 인사관리 페이지에 접속한 페이지URL, 사용자ID, 접속IP주소, 접속시간을 기록으로 DB에 tbl_empManger_accessTime 테이블에 insert 하도록 한다.
	void insert_accessTime(Map<String, String> paraMap);
	
	// employees 테이블에서 부서명별 인원수 및 퍼센티지 가져오기
	List<Map<String, String>> employeeCntByDeptname();
	
	// employees 테이블에서 성별 인원수 및 퍼센티지 가져오기 
	List<Map<String, String>> employeeCntByGender();

	// employees 테이블에서 성별 입사년도별 인원수 가져오기
	String employeeCntByGenderHireYear();

	// 특정 부서명에 근무하는 직원들의 성별 인원수 퍼센티지 가져오기
	String genderCntSpecialDeptname(String deptname);

	// 페이지별 사용자별 접속통계 가져오기
	String pageurlUsername();
}
