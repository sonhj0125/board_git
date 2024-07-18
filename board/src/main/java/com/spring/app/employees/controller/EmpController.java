package com.spring.app.employees.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spring.app.employees.service.EmpService;

@Controller
@RequestMapping(value = "/emp/*") // /emp/empList.action 과 같이 경로를 다 /emp/ 이하로 설정
public class EmpController {
	
	@Autowired
	private EmpService service;
	
	@GetMapping("empList.action") // 상단의 RequestMapping 으로 인해 /emp/empList.action 에서 /emp/ 생략 가능
	public String requiredLogin_empList(HttpServletRequest request, HttpServletResponse response) {
		
		return "emp/empList.tiles2";
		// /WEB-INF/views/tiles2/emp/empList.jsp
	}
	
/*
	@GetMapping("chart.action")
	public String chart() {
		
		return "";
	}
*/
	
	/////////////////////////////////////////////////////////////////////////
	
	// ==== #203. 다중 체크박스 사용 시 sql문에서 IN절을 처리하는 예제 ====
	@GetMapping("employeeList.action")
	public String empmanager_employeeList(HttpServletRequest request,
		  				   				  @RequestParam(defaultValue = "") String str_deptId,
		  				   				  @RequestParam(defaultValue = "") String gender) {
		
		// employees 테이블에서 근무 중인 사원들의 부서번호 가져오기
		List<String> deptIdList = service.deptIdList();
		
	/*
		System.out.println("~~~ 확인용 시작 ~~~");
		for(String deptId : deptIdList) {
			System.out.println(deptId);
		}
		System.out.println("~~~ 확인용 끝 ~~~");
		// ~~~ 확인용 시작 ~~~
		// -9999
		// 10
		// 20
		// 30
		// 40
		// 50
		// 60
		// 70
		// 80
		// 90
		// 100
		// 110
		// ~~~ 확인용 끝 ~~~
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
		
	}
	
	
	@ResponseBody
	@GetMapping(value="employeeListJSON.action", produces="text/plain;charset=UTF-8")
	public String employeeListJSON(HttpServletRequest request,
								   @RequestParam(defaultValue = "") String str_deptId,
								   @RequestParam(defaultValue = "") String gender) {
		
		Map<String, Object> paraMap = new HashMap<>();
		
		if(!"".equals(str_deptId)) {
			String[] arr_deptId = str_deptId.split("\\,");
			paraMap.put("arr_deptId", arr_deptId);
			
		}
		
		if(!"".equals(gender)) {
			paraMap.put("gender", gender);
		
		}
		
		List<Map<String, String>> employeeList = service.employeeList(paraMap);
		
		request.setAttribute("employeeList", employeeList);
		
		JSONArray jsonArr = new JSONArray();

		for (Map<String, String> map : employeeList) {

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("department_id", map.get("department_id"));
			jsonObj.put("department_name", map.get("department_name"));
			jsonObj.put("employee_id", map.get("employee_id"));
			jsonObj.put("fullname", map.get("fullname"));
			jsonObj.put("hire_date", map.get("hire_date"));
			jsonObj.put("monthsal", map.get("monthsal"));
			jsonObj.put("gender", map.get("gender"));
			jsonObj.put("age", map.get("age"));

			jsonArr.put(jsonObj);
		} // end of for--------------------------------

//		System.out.println(jsonArr.toString());
		/*
		[{"monthsal":"4400","gender":"여","department_id":"10","department_name":"Administration","employee_id":"200","fullname":"Jennifer Whalen","hire_date":"2003-09-17","age":"45"},
		 {"monthsal":"8050","gender":"여","employee_id":"178","fullname":"Kimberely Grant","hire_date":"2007-05-24","age":"25"}]
		 
		// 부서번호, 부서이름이 없을 경우 키값이 아예 없음 => 뷰단에서 ajax 처리 시 undefined로 출력된다.
		*/
		
		return jsonArr.toString();
	}
	
	
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
	
	
	// === #211. Excel 파일을 업로드하면 엑셀 데이터를 데이터베이스 테이블에 insert 해주는 예제 ===
	@ResponseBody
	@PostMapping("uploadExcelFile.action")
	public String uploadExcelFile(MultipartHttpServletRequest mtp_request) {
		
		MultipartFile mtp_excel_file = mtp_request.getFile("excel_file");
		
		JSONObject jsonObj = new JSONObject();
		
		
		if(mtp_excel_file != null) {
			
			try {
				
				// == MultipartFile 을 File 로 변환하기 시작 == 
				// WAS 의 webapp 의 절대경로를 알아와야 한다.
				HttpSession session = mtp_request.getSession();
				String root = session.getServletContext().getRealPath("/");
				String path = root + "resources" + File.separator + "files";
				
				File excel_file = new File(path + File.separator + mtp_excel_file.getOriginalFilename());
				mtp_excel_file.transferTo(excel_file);
				// == MultipartFile 을 File 로 변환하기 끝 == 
				
				OPCPackage opcPackage = OPCPackage.open(excel_file);
	            /*  
					아파치 POI(Apache POI)는 아파치 소프트웨어 재단에서 만든 라이브러리로서 마이크로소프트 오피스파일 포맷을 순수 자바 언어로서 읽고 쓰는 기능을 제공한다. 
					주로 워드, 엑셀, 파워포인트와 파일을 지원하며 최근의 오피스 포맷인 Office Open XML File Formats(OOXML, 즉 xml 기반의 *.docx, *.xlsx, *.pptx 등) 이나 아웃룩, 비지오, 퍼블리셔 등으로 지원 파일 포맷을 늘려가고 있다. 
	            */
				
				XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
				
				// 첫번째 시트 불러오기
	            XSSFSheet sheet = workbook.getSheetAt(0);
	            
	            List<Map<String, String>> paraMapList = new ArrayList<>();
	            
	            for(int i=1; i<sheet.getLastRowNum()+1; i++) {
	            					// getLastRowNum = sheet의 마지막 row number
	            					// 만약 5줄이라면 1번째 줄부터 5번째 줄까지 가야 하므로 +1 을 해야 5번째까지 for문을 돌 수 있다.
	            	
	            	Map<String, String> paraMap = new HashMap<>();
	            	
	            	XSSFRow row = sheet.getRow(i);
	            	
	            	// 행이 존재하지 않으면 건너뛴다.
	            	if(row == null) {
	            		continue;
	            	}
	            	
	            	// 행의 1번째 열 (사원번호)
	            	XSSFCell cell = row.getCell(0);
	            	
	            	if(cell != null) {
	            		paraMap.put("employee_id", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 2번째 열 (이름)
	            	cell = row.getCell(1);
	            	
	            	if(cell != null) {
	            		paraMap.put("first_name", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 3번째 열 (성)
	            	cell = row.getCell(2);
	            	
	            	if(cell != null) {
	            		paraMap.put("last_name", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 4번째 열 (이메일)
	            	cell = row.getCell(3);
	            	
	            	if(cell != null) {
	            		paraMap.put("email", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 5번째 열 (연락처)
	            	cell = row.getCell(4);
	            	
	            	if(cell != null) {
	            		paraMap.put("phone_number", String.valueOf(cellReader(cell)));
	            	}
	           
	            	// 행의 6번째 열 (입사일자)
	            	cell = row.getCell(5);
	            	
	            	if(cell != null) {
	            		paraMap.put("hire_date", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 7번째 열 (직종ID)
	            	cell = row.getCell(6);
	            	
	            	if(cell != null) {
	            		paraMap.put("job_id", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 8번째 열 (기본급여)
	            	cell = row.getCell(7);
	            	
	            	if(cell != null) {
	            		paraMap.put("salary", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 9번째 열 (커미션퍼센티지)
	            	cell = row.getCell(8);
	            	
	            	if(cell != null) {
	            		paraMap.put("commission_pct", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 10번째 열 (직속상관사원번호)
	            	cell = row.getCell(9);
	            	
	            	if(cell != null) {
	            		paraMap.put("manager_id", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 11번째 열 (부서번호)
	            	cell = row.getCell(10);
	            	
	            	if(cell != null) {
	            		paraMap.put("department_id", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	// 행의 12번째 열 (주민번호)
	            	cell = row.getCell(11);
	            	
	            	if(cell != null) {
	            		paraMap.put("jubun", String.valueOf(cellReader(cell)));
	            	}
	            	
	            	
	            	paraMapList.add(paraMap);
	            	
	            } // end of for -------------------------------
	            
	            workbook.close();
	            
	            int insert_count = service.add_employeeList(paraMapList);
	            
	            if(insert_count == paraMapList.size()) {
	            	
	            	jsonObj.put("result", 1);
	            	
	            } else {
	            	jsonObj.put("result", 0);
	            }
	            
	            excel_file.delete(); // 업로드 된 파일 삭제하기
				
			} catch (Exception e) {
				e.printStackTrace();
				jsonObj.put("result", 0);
			}
			
		} else {
			jsonObj.put("result", 0);
		}
		
		return jsonObj.toString();
	}
	
	
	@SuppressWarnings("incomplete-switch")
	private static String cellReader(XSSFCell cell) {
		
		String value = "";
		CellType ct = cell.getCellType();
		
		if (ct != null) {
			
			switch (cell.getCellType()) {
				case FORMULA:
					value = cell.getCellFormula() + "";
					break;
				case NUMERIC:
					value = cell.getNumericCellValue() + "";
					break;
				case STRING:
					value = cell.getStringCellValue() + "";
					break;
				case BOOLEAN:
					value = cell.getBooleanCellValue() + "";
					break;
				case ERROR:
					value = cell.getErrorCellValue() + "";
					break;
			} // end of switch ------------------
			
		} // end of if --------------------------------------
		
		return value;
	}
	
	
	// === #235. 차트(그래프)를 보여주는 예제 ===
	@GetMapping("chart.action")
	public String empmanager_chart(HttpServletRequest request) {
		
		return "emp/chart.tiles2";
	}
	
	
    // ==== #236. 차트그리기(Ajax) 부서명별 인원수 및 퍼센티지 가져오기 ==== // 
    @ResponseBody
    @GetMapping(value="chart/employeeCntByDeptname.action", produces="text/plain;charset=UTF-8") 
    public String employeeCntByDeptname() { 
       
       List<Map<String, String>> deptnamePercentageList = service.employeeCntByDeptname();
       
       JsonArray jsonArr = new JsonArray(); // []
       // com.google.gson.JsonArray
       
       for(Map<String, String> map : deptnamePercentageList) {
          JsonObject jsonObj = new JsonObject(); // {}
          // com.google.gson.JsonArray
          
          jsonObj.addProperty("department_name", map.get("department_name")); // {"department_name":"Shipping"} 
          jsonObj.addProperty("cnt", map.get("cnt")); // {"department_name":"Shipping","cnt":"45"} 
          jsonObj.addProperty("percentage", map.get("percentage")); // {"department_name":"Shipping","cnt":"45","percentage":"40.5"} 
          
          jsonArr.add(jsonObj); // [{"department_name":"Shipping","cnt":"45","percentage":"40.5"}]
       }// end of for-------------------------
       
    /*   
       Gson gson = new Gson();
       gson.toJson(jsonArr); // "[{"department_name":"Shipping","cnt":"45","percentage":"40.5"}]" 
       return gson.toJson(jsonArr);  
    */   
       return new Gson().toJson(jsonArr); // "[{"department_name":"Shipping","cnt":"45","percentage":"40.5"}]" 
    }
    

 // ==== #237. 차트그리기(Ajax) 성별 인원수 및 퍼센티지 가져오기 ==== // 
    @ResponseBody
    @GetMapping(value="chart/employeeCntByGender.action", produces="text/plain;charset=UTF-8") 
    public String employeeCntByGender() { 
       
       List<Map<String, String>> genderPercentageList = service.employeeCntByGender();
       
       JsonArray jsonArr = new JsonArray(); // [] 
       
       for(Map<String, String> map : genderPercentageList) {
          JsonObject jsonObj = new JsonObject(); // {} 
          jsonObj.addProperty("gender", map.get("gender")); // {"gender":"남"} 
          jsonObj.addProperty("cnt", map.get("cnt")); // {"gender":"남","cnt":"58"} 
          jsonObj.addProperty("percentage", map.get("percentage")); // {"gender":"남","cnt":"58","percentage":"52.3"} 
          
          jsonArr.add(jsonObj); // [{"gender":"남","cnt":"58","percentage":"52.3"}]
       }// end of for-------------------------
       
    /*   
       Gson gson = new Gson();
       gson.toJson(jsonArr); // "[{"gender":"남","cnt":"58","percentage":"52.3"}]" 
       return gson.toJson(jsonArr);  
    */   
       return new Gson().toJson(jsonArr); // "[{"gender":"남","cnt":"58","percentage":"52.3"}]" 
    }   
   
   
    // === #238. 차트그리기(Ajax) 성별 입사년도별 인원수 가져오기 ==== //
    @ResponseBody
    @GetMapping(value="chart/employeeCntByGenderHireYear.action", produces="text/plain;charset=UTF-8") 
    public String employeeCntByGenderHireYear() {
       
       return service.employeeCntByGenderHireYear();
    }
    
    
    // === #239. 차트그리기(Ajax) 특정 부서명에 근무하는 직원들의 성별 인원수 퍼센티지 가져오기 ==== //
    @ResponseBody
    @GetMapping(value="chart/genderCntSpecialDeptname.action", produces="text/plain;charset=UTF-8")  
    public String genderCntSpecialDeptname(@RequestParam(defaultValue = "") String deptname) {
       
       return service.genderCntSpecialDeptname(deptname); 
    }
    
    
    // === #240. 차트그리기(Ajax) 페이지별 사용자별 접속통계 가져오기 ==== //
    @ResponseBody
    @GetMapping(value="chart/pageurlUsername.action", produces="text/plain;charset=UTF-8")  
    public String pageurlUsername() {
       
       return service.pageurlUsername(); 
    }
	

}
