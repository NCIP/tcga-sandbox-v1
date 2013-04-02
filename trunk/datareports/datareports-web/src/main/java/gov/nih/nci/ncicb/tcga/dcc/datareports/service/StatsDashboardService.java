/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.BubbleXYZ;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Category;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Chart;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.ChartDataRow;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Dataset;

import java.util.List;

/**
 * Stats Dashboard Service interface
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface StatsDashboardService {

    /**
     * get chart object for drill down archive downloaded
     *
     * @param disease
     * @param label
     * @return Chart
     */
    public Chart getChartForDrillDownArchiveDownloaded(String disease, String label);

    /**
     * get chart object for total archive downloaded
     *
     * @return Chart
     */
    public Chart getChartForTotalArchiveDownloaded();

    /**
     * get chart object for drill down archive received
     *
     * @param disease
     * @param label
     * @return Chart
     */
    public Chart getChartForDrillDownArchiveReceived(String disease, String label);

    /**
     * get chart object for total archive received
     *
     * @return Chart
     */
    public Chart getChartForTotalArchiveReceived();

    /**
     * get chart object for pie charts
     *
     * @param type
     * @return Chart
     */
    public Chart getChartForFilterPieChart(final String type);

    /**
     * get chart object for drill down pie charts
     *
     * @param type
     * @param selection
     * @return Chart
     */
    public Chart getChartForDrillDownFilterPieChart(final String type, final String selection);

    /**
     * get datasets for archives
     *
     * @param link
     * @param isDD
     * @param type
     * @param label
     * @param list
     * @param list2
     * @return list of Dataset
     */
    public List<Dataset> getDatasetListForArchives(boolean link, boolean isDD, String type, String label,
                                                   List<ChartDataRow> list, List<ChartDataRow> list2);

    /**
     * get categories for archives
     *
     * @param list
     * @return list of Category
     */
    public List<Category> getCategoryListForArchives(List<ChartDataRow> list);

    /**
     * get drill down number of archives downloaded
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getNumberArchivesDownloadedDrillDown(final String disease);

    /**
     * get drill down size of archives downloaded
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getSizeArchivesDownloadedDrillDown(final String disease);

    /**
     * get total number of archives downloaded
     *
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getNumberArchivesDownloadedTotal();

    /**
     * get total size of archives downloaded
     *
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getSizeArchivesDownloadedTotal();

    /**
     * get drill down number of archives received
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getNumberArchivesReceivedDrillDown(final String disease);

    /**
     * get drill down size of archives received
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getSizeArchivesReceivedDrillDown(final String disease);

    /**
     * get total number of archives received
     *
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getNumberArchivesReceivedTotal();

    /**
     * get total size of archives received
     *
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getSizeArchivesReceivedTotal();

    /**
     * get filter pie chart
     *
     * @param type
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getFilterPieChart(final String type);

    /**
     * get bubble chart data for batches
     *
     * @return list of BubbleXYZ
     */
    public List<BubbleXYZ> getBubbleChartBatch();

    /**
     * get chart object for batches bubble chart
     *
     * @return chart
     */
    public Chart getChartForBubbleBatch();

    /**
     * get bubble chart data for platforms
     *
     * @return list of BubbleXYZ
     */
    public List<BubbleXYZ> getBubbleChartPlatformType();

    /**
     * get chart object for platform bubble chart
     *
     * @param size
     * @return chart
     */
    public Chart getChartForBubblePlatformType(final String size);

    /**
     * get chart object for platform bubble chart
     *
     * @return Category
     */
    public List<Category> getCategoryForBubblePlatformType();

    /**
     * get drill down filter pie chart
     *
     * @param type
     * @param selection
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getFilterPieChartDrillDown(final String type, final String selection);

    /**
     * get cumulative drill down number of archives downloaded
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getCumulativeNumberArchivesDownloadedDrillDown(String disease);

    /**
     * get cumulative drill down size of archives downloaded
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getCumulativeSizeArchivesDownloadedDrillDown(String disease);

    /**
     * get cumulative drill down number of archives received
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getCumulativeNumberArchivesReceivedDrillDown(String disease);

    /**
     * get cumulative drill down size of archives received
     *
     * @param disease
     * @return list of ChartDataRow
     */
    public List<ChartDataRow> getCumulativeSizeArchivesReceivedDrillDown(String disease);

}
