package com.spring.app.board.domain;

public class Seoul_bicycle_rental_VO {

	private String lendplace_id;
	private String statn_addr1;
	private String statn_addr2; 
	private double statn_lat; 
	private double statn_lnt;
	
	public String getLendplace_id() {
		return lendplace_id;
	}
	
	public void setLendplace_id(String lendplace_id) {
		this.lendplace_id = lendplace_id;
	}
	
	public String getStatn_addr1() {
		return statn_addr1;
	}
	
	public void setStatn_addr1(String statn_addr1) {
		this.statn_addr1 = statn_addr1;
	}
	
	public String getStatn_addr2() {
		return statn_addr2;
	}
	
	public void setStatn_addr2(String statn_addr2) {
		this.statn_addr2 = statn_addr2;
	}
	
	public double getStatn_lat() {
		return statn_lat;
	}
	
	public void setStatn_lat(double statn_lat) {
		this.statn_lat = statn_lat;
	}
	
	public double getStatn_lnt() {
		return statn_lnt;
	}
	
	public void setStatn_lnt(double statn_lnt) {
		this.statn_lnt = statn_lnt;
	}  
	
}
