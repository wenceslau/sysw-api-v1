package com.suite.core.dto;

import java.util.List;

public class ChartBarDTO {

	private List<String> labels;
	private List<ChartBarDataSetDTO> datasets;

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<ChartBarDataSetDTO> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<ChartBarDataSetDTO> datasets) {
		this.datasets = datasets;
	}

}
