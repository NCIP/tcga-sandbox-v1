package com.fusioncharts.sampledata;

public enum ChartType {
	// add extra values to each enum if desired
	AREA2D("Area2D"), COLUMN2D("Column2D"), COLUMN3D("Column3D"), PIE3D("Pie3D"), PIE2D(
			"Pie2D"), LINE("Line"), MSCOLUMN2D("MSColumn2D"), MSCOLUMN3D(
			"MSColumn3D"), MSLINE("MSLine"), MSCOMBI2D("MSCombi2D"), MSCOMBI3D(
			"MSCombi3D"), MSCOLUMNLINE3D("MSColumnLine3D"), STACKEDCOLUMN3D(
			"StackedColumn3D"), MSCOLUMN3DLINEDY("MSColumn3DLineDY");

	private String chartName;

	private String fileName;

	ChartType(String chartName) {
		this.chartName = chartName;
		this.fileName = chartName + ".swf";
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return this.chartName;
	}

}
