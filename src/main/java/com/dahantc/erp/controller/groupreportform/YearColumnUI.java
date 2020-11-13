package com.dahantc.erp.controller.groupreportform;

import java.math.BigDecimal;

public class YearColumnUI {

	private String projectName;

	private BigDecimal[] yearData;

	private String fluctuation = "0";

	public YearColumnUI(int yearCount, String projectName) {
		yearData = new BigDecimal[yearCount];
		for (int i = 0; i < yearCount; i++) {
			yearData[i] = new BigDecimal(0);
		}
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public BigDecimal[] getYearData() {
		return yearData;
	}

	public void setYearData(BigDecimal[] yearData) {
		this.yearData = yearData;
	}

	public String getFluctuation() {
		return fluctuation;
	}

	public void setFluctuation(String fluctuation) {
		this.fluctuation = fluctuation;
	}

}
