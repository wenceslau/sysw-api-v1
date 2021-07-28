package com.suite.core.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ChartBarDataSetDTO {

	@JsonIgnore
	private Integer order;
	private String label;
	private String backgroundColor;
	private String borderColor;
	private List<Long> data;

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public List<Long> getData() {
		return data;
	}

	public void setData(List<Long> data) {
		this.data = data;
	}

}
