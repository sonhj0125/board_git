package com.spring.app.board.domain;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class TestVO2 {

	// field
	private String no, name;
	
	
	/*
    	@DateTimeFormat 어노테이션은 Spring에서 지원하는 어노테이션으로서, 
     	스프링 프레임워크에서 날짜와 시간을 나타내는 문자열을  자바의 java.util.Date, java.util.Calendar 등과 같은 날짜 및 시간 객체로 변환할 때 사용된다.
	*/
//	@DateTimeFormat(iso = ISO.DATE_TIME)
//  또는
//	@DateTimeFormat(pattern="yyyyMMddHHmmss")
//  또는
	@DateTimeFormat(pattern="yyyy-MM-dd")	// 넘겨주는 데이터 형식이 일치해야 400 오류가 안뜸
	// @DateTimeFormat(pattern="yyyy-MM-dd") 을 생략하면 폼태그로 부터 입력받은 타입은 String 인데 writeday 는 Date 이라서 타입이 맞지 않으므로 "HTTP 상태 400 – 잘못된 요청" 이라고 나온다.
	private Date writeday;					// writeday 에 들어올 문자열은 pattern 에 정의된 yyyy-MM-dd 형식이어야 한다.!!
	
	
	// getter, setter
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getWriteday() {
		return writeday;
	}

	public void setWriteday(Date writeday) {
		this.writeday = writeday;
	}
	
	
	
	
	
	
}
