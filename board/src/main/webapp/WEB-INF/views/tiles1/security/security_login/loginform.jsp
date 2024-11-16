<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	String ctxPath = request.getContextPath();
%>

<script type="text/javascript">

  $(document).ready(function(){
	  
	  $("button#btnLOGIN").click(function(){
		  func_Login();
	  });
	  
	  
	  $("input#member_pwd").keydown(function(event){
		  
		  if(event.keyCode == 13) { // 엔터를 했을 경우
			  func_Login();
		  }
	  });
	  
  });// end of $(document).ready(function(){})------------------------------
  
  
  // Function Declaration
  
  function func_Login() {
	  
	  const member_id = $("input#member_id").val(); 
	  const member_pwd = $("input#member_pwd").val(); 
	
	  if(member_id.trim()=="") {
	 	 alert("아이디를 입력하세요!!");
		 $("input#member_id").val(""); 
		 $("input#member_id").focus();
		 return; // 종료 
	  }
	
	  if(member_pwd.trim()=="") {
		 alert("비밀번호를 입력하세요!!");
		 $("input#member_pwd").val(""); 
		 $("input#member_pwd").focus();
		 return; // 종료 
	  }
	  
	  const frm = document.loginFrm;
	  
	  frm.action = "<%= ctxPath%>/security/member/loginEnd.action";  
	  /* 
	     Spring Security 를 사용한 로그인 처리를 하는 것이므로 
	     Controller 인 com.spring.app.security.controller.MemberController 파일에는 
	     URL "member/loginEnd.action" 을 넣지 않고,
	     /board/src/main/webapp/WEB-INF/spring/securityConfig/security-context.xml 파일에서 
	     URL인 "member/loginEnd.action" 을 처리해주는 것이다.!!!!
	     <form-login 에 가보면 login-processing-url="/security/member/loginEnd.action" 이 있다. 
	  */
	  frm.method = "POST";
	  frm.submit();
	  
  }// end of function func_Login()------------------------
  
</script>

<div class="container">
	<div class="row" style="display: flex; border: solid 0px red;">
		<div class="col-md-8 col-md-offset-2" style="margin: auto; border: solid 0px blue;">
			<h2 class="text-muted">Spring Security(스프링 보안) 로그인</h2>
			<hr style="border: solid 1px orange">
			
			<div class="col-md-12 text-danger">${requestScope.msg}</div>
			
			<form name="loginFrm" class="mt-5">
				<div class="form-row">    
				    <div class="form-group col-md-6">
						<label class="text-muted font-weight-bold" for="member_id">아이디</label>
						<input type="text" class="form-control" name="member_id" id="member_id" value=""/> <%-- 부트스트랩에서 input 태그에는 항상 class form-control 이 사용되어져야 한다. --%>
		            </div>
		
					<div class="form-group col-md-6">
						<label class="text-muted font-weight-bold text-muted" for="member_pwd">비밀번호</label>
						<input type="password" class="form-control" name="member_pwd" id="member_pwd" value="" /> 
					</div>
				</div>
			</form>
		</div>
		
		<div class="col-md-8 col-md-offset-2" style="margin: auto; display: flex; border: solid 0px blue;">
			<div style="margin: auto; border: solid 0px blue;">
				<button style="width: 150px; height: 40px;" class="btn btn-primary" type="button" id="btnLOGIN">확인</button>
			</div>
		</div>
	</div>
</div>  
    