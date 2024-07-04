package com.spring.app.employees.model2;

import java.util.List;
import java.util.Map;

public interface EmpDAO2 {

	// #213. Excel 파일을 업로드 하면 엑셀데이터를 데이터베이스 테이블에 insert 해주는 예제
	int add_employee_list(List<Map<String, String>> paraMapList);
	
	
}
