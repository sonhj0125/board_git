package com.spring.app.employees.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.app.employees.model.EmpDAO;

@Service
public class EmpService_imple implements EmpService {

	@Autowired
	private EmpDAO dao;

	
	// employees 테이블에서 근무중인 사원들의 부서번호 가져오기 
	@Override
	public List<String> deptIdList() {
		
		List<String> deptIdList = dao.deptIdList();
		return deptIdList;
		
	} // end of public List<String> deptIdList
	
	
	
	
}
