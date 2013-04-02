/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Label;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Value;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.StatsDashboardDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.FILTER_PIE_CHART;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.FILTER_PIE_CHART_CAPTION;

/**
 * Stats Dashboard Service Implementation
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class StatsDashboardServiceImpl implements StatsDashboardService {

    protected final Log logger = LogFactory.getLog(getClass());
    @Autowired
    private StatsDashboardDAO daoImpl;

    private Chart getCommonChart() {
        final Chart chart = new Chart();
        chart.setBgcolor("FFFFFF");
        chart.setShowborder("0");
        chart.setAnimation("1");
        chart.setShowvalues("0");
        chart.setShowexportdatamenuitem("1");
        return chart;
    }

    private void setBytesFormatScale(final Chart chart, final boolean isKB, final boolean isBoth) {
        chart.setFormatnumberscale(isBoth ? "1" : "0");
        chart.setDecimal("2");
        chart.setYaxisvaluedecimals("2");
        chart.setDefaultnumberscale("bits");
        chart.setNumberscaleunit(isKB ? "MB,GB,TB,PB" : "bytes,KB,MB,GB,TB");
        chart.setNumberscalevalue(isKB ? "1024,1024,1024,1024" : "8,1024,1024,1024,1024");
        chart.setSformatnumberscale("1");
        chart.setSdecimal("2");
        chart.setSyaxisvaluedecimals("2");
        chart.setSdefaultnumberscale(isKB ? "KB" : "bits");
        chart.setSnumberscaleunit(isKB ? "MB,GB,TB,PB" : "bytes,KB,MB,GB,TB");
        chart.setSnumberscalevalue(isKB ? "1024,1024,1024,1024" : "8,1024,1024,1024,1024");
    }

    @Override
    public Chart getChartForDrillDownArchiveDownloaded(final String disease, final String label) {
        final Chart chart = getCommonChart();
        chart.setCaption(disease + " " + label + " Archives Downloaded");
        chart.setXaxisname("Months");
        chart.setPyaxisname("Total " + label + " of Archives");
        chart.setSyaxisname("Cumulative " + label + " of Archives");
        chart.setPalette("5");
        chart.setSeriesnameintooltip("0");
        chart.setNumvisibleplot("24");
        if ("Size".equals(label)) {
            setBytesFormatScale(chart, false, true);
        }
        return chart;
    }

    @Override
    public Chart getChartForTotalArchiveDownloaded() {
        final Chart chart = getCommonChart();
        chart.setCaption("Total Archives Downloaded");
        chart.setSubcaption(getTotalArchiveDownloadedSubcaption());
        chart.setXaxisname("Diseases");
        chart.setPyaxisname("Total Number of Archives");
        chart.setSyaxisname("Total Size of Archives");
        chart.setPalette("5");
        chart.setSeriesnameintooltip("0");
        setBytesFormatScale(chart, false, false);
        return chart;
    }

    @Override
    public Chart getChartForDrillDownArchiveReceived(final String disease, final String label) {
        final Chart chart = getCommonChart();
        chart.setCaption(disease + " " + label + " Archives Received");
        chart.setXaxisname("Months");
        chart.setPyaxisname("Total " + label + " of Archives");
        chart.setSyaxisname("Cumulative " + label + " of Archives");
        chart.setPalette("2");
        chart.setSeriesnameintooltip("0");
        chart.setNumvisibleplot("24");
        if ("Size".equals(label)) {
            setBytesFormatScale(chart, true, true);
        }
        return chart;
    }

    @Override
    public Chart getChartForTotalArchiveReceived() {
        final Chart chart = getCommonChart();
        chart.setCaption("Total Archives Received");
        chart.setSubcaption(getTotalArchiveReceivedSubcaption());
        chart.setXaxisname("Diseases");
        chart.setPyaxisname("Total Number of Archives");
        chart.setSyaxisname("Total Size of Archives");
        chart.setPalette("2");
        chart.setSeriesnameintooltip("0");
        setBytesFormatScale(chart, true, false);
        return chart;
    }

    @Override
    public Chart getChartForFilterPieChart(final String type) {
        final Chart chart = getCommonChart();
        chart.setCaption(FILTER_PIE_CHART_CAPTION.get(type));
        chart.setShowvalues("1");
        chart.setShowpercentvalues("1");
        chart.setPalette("2");
        chart.setShowlegend("1");
        chart.setShowPlotBorder("1");
        chart.setPlotBorderThickness("1");
        chart.setPlotBorderColor("000000");
        chart.setPlotBorderAlpha("100");

        return chart;
    }

    @Override
    public Chart getChartForDrillDownFilterPieChart(String type, String selection) {
        final Chart chart = getCommonChart();
        chart.setCaption(selection + " Per Disease");
        chart.setXaxisname("Diseases");
        chart.setYaxisname("# Request");
        chart.setPalette("2");
        return chart;
    }

    @Override
    public Chart getChartForBubbleBatch() {
        final Chart chart = getCommonChart();
        chart.setCaption("Most Requested Batches");
        chart.setXaxisname("Batches");
        chart.setYaxisname("# Request");
        chart.setPalette("2");
        chart.setClipbubbles("1");
        return chart;
    }

    @Override
    public Chart getChartForBubblePlatformType(final String size) {
        final Chart chart = getCommonChart();
        chart.setCaption("Most Requested Platform Types");
        chart.setYaxisname("# Request");
        chart.setPalette("2");
        chart.setClipbubbles("1");
        chart.setXaxismaxvalue(size);
        chart.setLabeldisplay("ROTATE");
        return chart;
    }

    @Override
    public List<Dataset> getDatasetListForArchives(boolean link, boolean isDD, String type, String label,
                                                   List<ChartDataRow> list, List<ChartDataRow> list2) {
        final List<Dataset> datasetList = new LinkedList<Dataset>();
        final Dataset ds1 = new Dataset((isDD) ? "Total " + label + " of Archives" : "Total Number of Archives ");
        final Dataset ds2 = new Dataset((isDD) ? "Cumulative " + label + " of Archives" : "Total Size of Archives ");
        ds2.setParentyaxis("S");
        final List<Value> valueList1 = new LinkedList<Value>();
        final List<Value> valueList2 = new LinkedList<Value>();
        for (int i = 0; i < list.size(); i++) {
            final ChartDataRow row1 = list.get(i);
            final ChartDataRow row2 = list2.get(i);
            if (link) {
                valueList1.add(new Value(row1.getValue(), "j-ddArchiveChart-" + row1.getLabel() + "," + type));
                valueList2.add(new Value(row2.getValue(), "j-ddArchiveChart-" + row2.getLabel() + "," + type));
            } else {
                valueList1.add(new Value(row1.getValue()));
                valueList2.add(new Value(row2.getValue()));
            }
        }
        ds1.setData(valueList1);
        ds2.setData(valueList2);
        datasetList.add(ds1);
        datasetList.add(ds2);
        return datasetList;
    }

    @Override
    public List<Category> getCategoryListForArchives(final List<ChartDataRow> list) {
        final List<Category> categories = new LinkedList<Category>();
        final List<Label> labelList = new LinkedList<Label>();
        final Category ctg = new Category();
        for (final ChartDataRow row : list) {
            labelList.add(new Label(row.getLabel()));
        }
        ctg.setCategory(labelList);
        categories.add(ctg);
        return categories;
    }

    @Override
    public List<Category> getCategoryForBubblePlatformType() {
        final List<Category> categories = new LinkedList<Category>();
        final List<Label> labelList = new LinkedList<Label>();
        final Category ctg = new Category();
        final List<ChartDataRow> list = daoImpl.getFilterPieChart(FILTER_PIE_CHART.get("platformTypeFilter"));
        int i = 1;
        for (final ChartDataRow row : list) {
            if (row.getLabel() != null) {
                labelList.add(new Label(row.getLabel(), "" + i));
                i++;
            }
        }
        ctg.setCategory(labelList);
        categories.add(ctg);
        return categories;
    }

    @Override
    public List<BubbleXYZ> getBubbleChartPlatformType() {
        final List<BubbleXYZ> data = new LinkedList<BubbleXYZ>();
        final List<ChartDataRow> list = daoImpl.getFilterPieChart(FILTER_PIE_CHART.get("platformTypeFilter"));
        int i = 1;
        for (final ChartDataRow row : list) {
            if (row.getLabel() != null) {
                data.add(new BubbleXYZ("" + i, row.getValue(), row.getValue(), row.getLabel(),
                        "j-ddPieChart-platformTypeFilter," + row.getLabel(), row.getLabel() + ", " + row.getValue()));
                i++;
            }
        }
        return data;
    }

    @Override
    public List<BubbleXYZ> getBubbleChartBatch() {
        final List<BubbleXYZ> data = new LinkedList<BubbleXYZ>();
        final List<ChartDataRow> list = daoImpl.getFilterBatch();
        int i = 1;
        for (final ChartDataRow row : list) {
            final String ddLabel = row.getLabel();
            if (row.getLabel() != null) {
                final String xAxis = row.getLabel().substring(6);
                data.add(new BubbleXYZ("sified".equals(xAxis) ? "" + (list.size()) : "" + i, row.getValue(),
                        row.getValue(), row.getLabel(), "j-ddPieChart-batchFilter," + ddLabel, row.getLabel() + ", " + row.getValue()));
                i++;
            }
        }
        return data;
    }

    @Override
    public List<ChartDataRow> getFilterPieChart(final String type) {
        final List<ChartDataRow> data = new LinkedList<ChartDataRow>();
        final List<ChartDataRow> list = daoImpl.getFilterPieChart(FILTER_PIE_CHART.get(type));
        for (ChartDataRow cd : list) {
            cd.setLink("j-ddPieChart-" + type + "," + cd.getLabel());
            data.add(cd);
        }
        return data;
    }

    @Override
    public List<ChartDataRow> getNumberArchivesDownloadedDrillDown(final String disease) {
        return daoImpl.getNumberArchivesDownloadedDrillDown(disease);
    }

    @Override
    public List<ChartDataRow> getSizeArchivesDownloadedDrillDown(final String disease) {
        return daoImpl.getSizeArchivesDownloadedDrillDown(disease);
    }

    @Override
    public List<ChartDataRow> getNumberArchivesReceivedDrillDown(final String disease) {
        return daoImpl.getNumberArchivesReceivedDrillDown(disease);
    }

    @Override
    public List<ChartDataRow> getSizeArchivesReceivedDrillDown(final String disease) {
        return daoImpl.getSizeArchivesReceivedDrillDown(disease);
    }

    @Override
    public List<ChartDataRow> getNumberArchivesDownloadedTotal() {
        return daoImpl.getNumberArchivesDownloadedTotal();
    }

    @Override
    public List<ChartDataRow> getSizeArchivesDownloadedTotal() {
        return daoImpl.getSizeArchivesDownloadedTotal();
    }

    @Override
    public List<ChartDataRow> getNumberArchivesReceivedTotal() {
        return daoImpl.getNumberArchivesReceivedTotal();
    }

    @Override
    public List<ChartDataRow> getSizeArchivesReceivedTotal() {
        return daoImpl.getSizeArchivesReceivedTotal();
    }

    @Override
    public List<ChartDataRow> getFilterPieChartDrillDown(final String type, final String selection) {
        return daoImpl.getFilterPieChartDrillDown(FILTER_PIE_CHART.get(type), selection);
    }

    @Override
    public List<ChartDataRow> getCumulativeNumberArchivesDownloadedDrillDown(String disease) {
        return daoImpl.getCumulativeNumberArchivesDownloadedDrillDown(disease);
    }

    @Override
    public List<ChartDataRow> getCumulativeSizeArchivesDownloadedDrillDown(String disease) {
        return daoImpl.getCumulativeSizeArchivesDownloadedDrillDown(disease);
    }

    @Override
    public List<ChartDataRow> getCumulativeNumberArchivesReceivedDrillDown(String disease) {
        return daoImpl.getCumulativeNumberArchivesReceivedDrillDown(disease);
    }

    @Override
    public List<ChartDataRow> getCumulativeSizeArchivesReceivedDrillDown(String disease) {
        return daoImpl.getCumulativeSizeArchivesReceivedDrillDown(disease);
    }

    private String getTotalArchiveDownloadedSubcaption() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Total Archives: ").append(daoImpl.getAbsoluteTotalNumberArchiveDownloaded());
        sb.append("     Total Size: ");
        sb.append(humanReadableByteCount(daoImpl.getAbsoluteTotalSizeArchiveDownloaded()));
        return sb.toString();
    }

    private String getTotalArchiveReceivedSubcaption() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Total Archives: ").append(daoImpl.getAbsoluteTotalNumberArchiveReceived());
        sb.append("     Total Size: ");
        sb.append(humanReadableByteCount(1024 * daoImpl.getAbsoluteTotalSizeArchiveReceived()));
        return sb.toString();
    }

    //This is the most awesome bytes formater I have ever seen :)
    private String humanReadableByteCount(long bytes) {
        final int unit = 1024;
        if (bytes < unit) return bytes + " B";
        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = ("KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}//End of Class
