package com.spring.app.employees.model2;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class EmpDAO2_imple implements EmpDAO2 {

	@Autowired
	@Qualifier("sqlsession_2")
	private SqlSessionTemplate sql;

	
	// === #213. Excel 파일을 업로드하면 엑셀 데이터를 데이터베이스 테이블에 insert 해주는 예제 ===
	@Override
	public int add_employeeList(List<Map<String, String>> paraMapList) {
		
		int insert_count = 0;
		
		for(Map<String, String> paraMap : paraMapList) {
			
			int n = sql.insert("hr.add_employee", paraMap);
			
			insert_count += n;
		}
		
		return insert_count;
	}
	
}
