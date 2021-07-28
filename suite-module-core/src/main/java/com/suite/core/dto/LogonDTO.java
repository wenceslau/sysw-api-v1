package com.suite.core.dto;

import java.time.LocalDate;

public class LogonDTO {

	private LocalDate dateLogon;
	private String status;
	private Long count;

	public LogonDTO() {
		super();
	}

	public LogonDTO(LocalDate dateLogon, String status, Long count) {
		super();
		this.dateLogon = dateLogon;
		this.status = status;
		this.count = count;
	}

	public LocalDate getDateLogon() {
		return dateLogon;
	}

	public void setDateLogon(LocalDate dateLogon) {
		this.dateLogon = dateLogon;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "LogonTO [dateLogon=" + dateLogon + ", status=" + status + ", count=" + count + "]";
	}
	
	

}
