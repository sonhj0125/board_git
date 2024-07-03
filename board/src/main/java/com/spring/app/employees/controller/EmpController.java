package com.spring.app.employees.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.app.employees.service.EmpService;

@Controller
@RequestMapping(value="/emp/*")
public class EmpController {

	@Autowired
	private EmpService service;
	
	
	@GetMapping("empList.action")
	public String requiredLogin_empList(HttpServletRequest request, HttpServletResponse response) { 
		
		return "emp/empList.tiles2";
		//  /WEB-INF/views/tiles2/emp/empList.jsp  페이지를 만들어야 한다.
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// === #203. 다중 체크박스를 사용시 SQL문에서 in 절을 처리하는 예제 === // 
	@GetMapping("employeeList.action")
	public String employeeList(HttpServletRequest request,
							  @RequestParam(defaultValue = "") String str_deptId,
							  @RequestParam(defaultValue = "") String gender) {
		
		// employees 테이블에서 근무중인 사원들의 부서번호 가져오기
		List<String> deptIdList = service.deptIdList();
		
		/* System.out.println("~~~ 확인용 시작 ~~~");
		
		for(String deptId : deptIdList) {
			System.out.println(deptId);
		}
		
		System.out.println("~~~ 확인용 끝 ~~~"); */
		/*
		 	~~~ 확인용 시작 ~~~
			-9999
			10
			20
			30
			40
			50
			60
			70
			80
			90
			100
			110
			~~~ 확인용 끝 ~~~
	   */
		
			// System.out.println("~~~ 확인용 str_deptId : " + str_deptId);
			// System.out.println("~~~ 확인용 gender : " + gender);
			
			Map<String, Object> paraMap = new HashMap<>();
			
			if(!"".equals(str_deptId)) {
				String[] arr_deptId = str_deptId.split("\\,");
				paraMap.put("arr_deptId", arr_deptId);
				
				request.setAttribute("str_deptId", str_deptId);	// 뷰단에서 선택한 부서번호값을 유지시키기 위한 것
			}

			if(!"".equals(gender)) {
				paraMap.put("gender", gender);
				
				request.setAttribute("gender", gender);	// 뷰단에서 선택한 성별을 유지시키기 위한 것
			}
			
		
			List<Map<String, String>> employeeList = service.employeeList(paraMap);
		      
		     
			request.setAttribute("deptIdList", deptIdList);
			request.setAttribute("employeeList", employeeList);
		
		return "emp/employeeList.tiles2";
		// /WEB-INF/views/tiles2/emp/employeeList.jsp 페이지를 만들어야 한다.
		
	} // end of public String employeeList
	
	
	
	// === #208. 웹에서 보여지는 결과물을 Excel 파일로 다운받기 예제 === //
	@PostMapping(value="downloadExcelFile.action")
	public String downloadExcelFile(@RequestParam(defaultValue = "") String str_deptId,
									@RequestParam(defaultValue = "") String gender,
									Model model) {
		
		
		/* System.out.println("~~~ 확인용 시작 ~~~");
		
		for(String deptId : deptIdList) {
			System.out.println(deptId);
		}
		
		System.out.println("~~~ 확인용 끝 ~~~"); */
		/*
		 	~~~ 확인용 시작 ~~~
			-9999
			10
			20
			30
			40
			50
			60
			70
			80
			90
			100
			110
			~~~ 확인용 끝 ~~~
	   */
		
			// System.out.println("~~~ 확인용 str_deptId : " + str_deptId);
			// System.out.println("~~~ 확인용 gender : " + gender);
			
			Map<String, Object> paraMap = new HashMap<>();
			
			if(!"".equals(str_deptId)) {
				String[] arr_deptId = str_deptId.split("\\,");
				paraMap.put("arr_deptId", arr_deptId);
				
			}

			if(!"".equals(gender)) {
				paraMap.put("gender", gender);
				
			}
			
			service.employeeList_to_Excel(paraMap, model);
		      
			return "excelDownloadView";
			// "excelDownloadView" 접두어 접미어
			// excelDownloadView 은 /WEB-INF/spring/appServlet/servlet-context.xml 파일에서 
			// #19. 에서 기술된 bean의 id 값이다.
		
		} // end of public String downloadExcelFile
	

	
	
	
}
