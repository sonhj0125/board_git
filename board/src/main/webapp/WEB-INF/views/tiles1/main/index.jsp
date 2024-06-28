<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String ctxPath = request.getContextPath();
%>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-10 offset-md-1">
		
			<div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
				<ol class="carousel-indicators">
					<c:if test="${not empty requestScope.mapList}">
						<c:forEach items="${requestScope.mapList}" varStatus="status">
						
							<c:if test="${status.index == 0}">
								<li data-target="#carouselExampleIndicators" data-slide-to="${status.index}" class="active"></li> 
							</c:if>
							
							<c:if test="${status.index > 0}">
								<li data-target="#carouselExampleIndicators" data-slide-to="${status.index}"></li>
							</c:if>
							
						</c:forEach>
					</c:if>
				</ol>
				
				<div class="carousel-inner">
					<c:if test="${not empty requestScope.mapList}">
						<c:forEach var="map" items="${requestScope.mapList}" varStatus="status">
							<c:if test="${status.index == 0}">
								<div class="carousel-item active">
									<img src="<%=ctxPath%>/resources/images/${map.imgfilename}" class="d-block w-100" alt="...">
									<div class="carousel-caption d-none d-md-block"> 
									    <h5 style="color: black;">${map.productname}</h5>
								  	</div>
								</div>
							</c:if>
							
							<c:if test="${status.index > 0}">
								<div class="carousel-item">
									<img src="<%=ctxPath%>/resources/images/${map.imgfilename}" class="d-block w-100" alt="...">
									<div class="carousel-caption d-none d-md-block"> 
									    <h5 style="color: black;">${map.productname}</h5>
								  	</div>
								</div>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
				<a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
					<span class="carousel-control-prev-icon" aria-hidden="true"></span>
					<span class="sr-only">Previous</span>
				</a>
				<a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
					<span class="carousel-control-next-icon" aria-hidden="true"></span>
					<span class="sr-only">Next</span>
				</a>
			</div>
		
		</div>
	</div>
</div>