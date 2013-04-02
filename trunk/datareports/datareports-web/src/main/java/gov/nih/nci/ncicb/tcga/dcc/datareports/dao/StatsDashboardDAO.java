/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.ChartDataRow;

import javax.sql.DataSource;
import java.util.List;

/**
 * dao layer of the stats dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface StatsDashboardDAO {

    public void setDataSource(DataSource dataSource);

    public void refreshStatsDashboardProcedure();

    /**
     * get total Number of archives Downloaded
     *
     * @return List of chart data row
     */
    public List<ChartDataRow> getNumberArchivesDownloadedTotal();

    /**
     * get total Size of archives Downloaded
     *
     * @return List of chart data row
     */
    public List<ChartDataRow> getSizeArchivesDownloadedTotal();

    /**
     * get total Number of archives Received
     *
     * @return List of chart data row
     */
    public List<ChartDataRow> getNumberArchivesReceivedTotal();

    /**
     * get total Size of archives Received
     *
     * @return List of chart data row
     */
    public List<ChartDataRow> getSizeArchivesReceivedTotal();

    /**
     * get drill down Number of archives Downloaded
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getNumberArchivesDownloadedDrillDown(String disease);

    /**
     * get drill down Size of archives Downloaded
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getSizeArchivesDownloadedDrillDown(String disease);

    /**
     * get drill down Number of archives Received
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getNumberArchivesReceivedDrillDown(String disease);

    /**
     * get drill down Size of archives Received
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getSizeArchivesReceivedDrillDown(String disease);

    /**
     * get cumulative drill down Number of archives Downloaded
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getCumulativeNumberArchivesDownloadedDrillDown(String disease);

    /**
     * get cumulative drill down Size of archives Downloaded
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getCumulativeSizeArchivesDownloadedDrillDown(String disease);

    /**
     * get cumulative drill down Number of archives Received
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getCumulativeNumberArchivesReceivedDrillDown(String disease);

    /**
     * get cumulative drill down Size of archives Received
     *
     * @param disease
     * @return List of chart data row
     */
    public List<ChartDataRow> getCumulativeSizeArchivesReceivedDrillDown(String disease);

    /**
     * get Filter data for Pie charts
     *
     * @param type
     * @return List of chart data row
     */
    public List<ChartDataRow> getFilterPieChart(String type);

    /**
     * get Filter data for Batch
     *
     * @return List of chart data row
     */
    public List<ChartDataRow> getFilterBatch();

    /**
     * get drill down of Pie charts
     *
     * @param type
     * @param selection
     * @return List of chart data row
     */
    public List<ChartDataRow> getFilterPieChartDrillDown(String type, String selection);

    /**
     * get Absolute Total Number Archive Downloaded
     *
     * @return number
     */
    public Long getAbsoluteTotalNumberArchiveDownloaded();

    /**
     * get Absolute Total Size Archive Downloaded
     *
     * @return number
     */
    public Long getAbsoluteTotalSizeArchiveDownloaded();

    /**
     * get Absolute Total Number Archive Received
     *
     * @return number
     */
    public Long getAbsoluteTotalNumberArchiveReceived();

    /**
     * get Absolute Total Size Archive Received
     *
     * @return number
     */
    public Long getAbsoluteTotalSizeArchiveReceived();

}
