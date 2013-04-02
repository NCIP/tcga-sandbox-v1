/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts;

/**
 * Chart bean for fusion charts
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Chart {

    private String caption;
    private String subcaption;
    private String xaxisname;
    private String yaxisname;
    private String pyaxisname;
    private String syaxisname;
    private String showvalues;
    private String showlegend;
    private String showlabels;
    private String numberprefix;
    private String formatnumberscale;
    private String sformatnumberscale;
    private String defaultnumberscale;
    private String sdefaultnumberscale;
    private String numberscalevalue;
    private String snumberscalevalue;
    private String numberscaleunit;
    private String snumberscaleunit;
    private String palette;
    private String labeldisplay;
    private String slantlabels;
    private String animation;
    private String seriesnameintooltip;
    private String numvisibleplot;
    private String useroundedges;
    private String showpercentvalues;
    private String showborder;
    private String bgcolor;
    private String showexportdatamenuitem;
    private String decimal;
    private String sdecimal;
    private String yaxisvaluedecimals;
    private String syaxisvaluedecimals;
    private String clipbubbles;
    private String xaxismaxvalue;
    private String showPlotBorder;
    private String plotBorderColor;
    private String plotBorderThickness;
    private String plotBorderAlpha;

    public Chart() {
    }

    public Chart(String caption, String subcaption, String xaxisname, String yaxisname, String showvalues, String numberprefix) {
        this.caption = caption;
        this.subcaption = subcaption;
        this.xaxisname = xaxisname;
        this.yaxisname = yaxisname;
        this.showvalues = showvalues;
        this.numberprefix = numberprefix;
    }

    public Chart(String caption, String subcaption, String xaxisname, String pyaxisname, String syaxisname, String showvalues, String numberprefix) {
        this.caption = caption;
        this.subcaption = subcaption;
        this.xaxisname = xaxisname;
        this.pyaxisname = pyaxisname;
        this.syaxisname = syaxisname;
        this.showvalues = showvalues;
        this.numberprefix = numberprefix;
    }

    public String getYaxisvaluedecimals() {
        return yaxisvaluedecimals;
    }

    public void setYaxisvaluedecimals(String yaxisvaluedecimals) {
        this.yaxisvaluedecimals = yaxisvaluedecimals;
    }

    public String getSyaxisvaluedecimals() {
        return syaxisvaluedecimals;
    }

    public void setSyaxisvaluedecimals(String syaxisvaluedecimals) {
        this.syaxisvaluedecimals = syaxisvaluedecimals;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public String getSdecimal() {
        return sdecimal;
    }

    public void setSdecimal(String sdecimal) {
        this.sdecimal = sdecimal;
    }

    public String getShowexportdatamenuitem() {
        return showexportdatamenuitem;
    }

    public void setShowexportdatamenuitem(String showexportdatamenuitem) {
        this.showexportdatamenuitem = showexportdatamenuitem;
    }

    public String getShowlegend() {
        return showlegend;
    }

    public void setShowlegend(String showlegend) {
        this.showlegend = showlegend;
    }

    public String getShowlabels() {
        return showlabels;
    }

    public void setShowlabels(String showlabels) {
        this.showlabels = showlabels;
    }

    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getShowborder() {
        return showborder;
    }

    public void setShowborder(String showborder) {
        this.showborder = showborder;
    }

    public String getShowpercentvalues() {
        return showpercentvalues;
    }

    public void setShowpercentvalues(String showpercentvalues) {
        this.showpercentvalues = showpercentvalues;
    }

    public String getNumvisibleplot() {
        return numvisibleplot;
    }

    public void setNumvisibleplot(String numvisibleplot) {
        this.numvisibleplot = numvisibleplot;
    }

    public String getUseroundedges() {
        return useroundedges;
    }

    public void setUseroundedges(String useroundedges) {
        this.useroundedges = useroundedges;
    }

    public String getPyaxisname() {
        return pyaxisname;
    }

    public void setPyaxisname(String pyaxisname) {
        this.pyaxisname = pyaxisname;
    }

    public String getSyaxisname() {
        return syaxisname;
    }

    public void setSyaxisname(String syaxisname) {
        this.syaxisname = syaxisname;
    }

    public String getSubcaption() {
        return subcaption;
    }

    public void setSubcaption(String subcaption) {
        this.subcaption = subcaption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getXaxisname() {
        return xaxisname;
    }

    public void setXaxisname(String xaxisname) {
        this.xaxisname = xaxisname;
    }

    public String getYaxisname() {
        return yaxisname;
    }

    public void setYaxisname(String yaxisname) {
        this.yaxisname = yaxisname;
    }

    public String getShowvalues() {
        return showvalues;
    }

    public void setShowvalues(String showvalues) {
        this.showvalues = showvalues;
    }

    public String getNumberprefix() {
        return numberprefix;
    }

    public void setNumberprefix(String numberprefix) {
        this.numberprefix = numberprefix;
    }

    public String getFormatnumberscale() {
        return formatnumberscale;
    }

    public void setFormatnumberscale(String formatnumberscale) {
        this.formatnumberscale = formatnumberscale;
    }

    public String getSformatnumberscale() {
        return sformatnumberscale;
    }

    public void setSformatnumberscale(String sformatnumberscale) {
        this.sformatnumberscale = sformatnumberscale;
    }

    public String getDefaultnumberscale() {
        return defaultnumberscale;
    }

    public void setDefaultnumberscale(String defaultnumberscale) {
        this.defaultnumberscale = defaultnumberscale;
    }

    public String getSdefaultnumberscale() {
        return sdefaultnumberscale;
    }

    public void setSdefaultnumberscale(String sdefaultnumberscale) {
        this.sdefaultnumberscale = sdefaultnumberscale;
    }

    public String getNumberscalevalue() {
        return numberscalevalue;
    }

    public void setNumberscalevalue(String numberscalevalue) {
        this.numberscalevalue = numberscalevalue;
    }

    public String getSnumberscalevalue() {
        return snumberscalevalue;
    }

    public void setSnumberscalevalue(String snumberscalevalue) {
        this.snumberscalevalue = snumberscalevalue;
    }

    public String getNumberscaleunit() {
        return numberscaleunit;
    }

    public void setNumberscaleunit(String numberscaleunit) {
        this.numberscaleunit = numberscaleunit;
    }

    public String getSnumberscaleunit() {
        return snumberscaleunit;
    }

    public void setSnumberscaleunit(String snumberscaleunit) {
        this.snumberscaleunit = snumberscaleunit;
    }

    public String getPalette() {
        return palette;
    }

    public void setPalette(String palette) {
        this.palette = palette;
    }

    public String getLabeldisplay() {
        return labeldisplay;
    }

    public void setLabeldisplay(String labeldisplay) {
        this.labeldisplay = labeldisplay;
    }

    public String getSlantlabels() {
        return slantlabels;
    }

    public void setSlantlabels(String slantlabels) {
        this.slantlabels = slantlabels;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getSeriesnameintooltip() {
        return seriesnameintooltip;
    }

    public void setSeriesnameintooltip(String seriesnameintooltip) {
        this.seriesnameintooltip = seriesnameintooltip;
    }

    public String getClipbubbles() {
        return clipbubbles;
    }

    public void setClipbubbles(String clipbubbles) {
        this.clipbubbles = clipbubbles;
    }

    public String getXaxismaxvalue() {
        return xaxismaxvalue;
    }

    public void setXaxismaxvalue(String xaxismaxvalue) {
        this.xaxismaxvalue = xaxismaxvalue;
    }

    public String getShowPlotBorder() {
        return showPlotBorder;
    }

    public void setShowPlotBorder(String showPlotBorder) {
        this.showPlotBorder = showPlotBorder;
    }

    public String getPlotBorderColor() {
        return plotBorderColor;
    }

    public void setPlotBorderColor(String plotBorderColor) {
        this.plotBorderColor = plotBorderColor;
    }

    public String getPlotBorderThickness() {
        return plotBorderThickness;
    }

    public void setPlotBorderThickness(String plotBorderThickness) {
        this.plotBorderThickness = plotBorderThickness;
    }

    public String getPlotBorderAlpha() {
        return plotBorderAlpha;
    }

    public void setPlotBorderAlpha(String plotBorderAlpha) {
        this.plotBorderAlpha = plotBorderAlpha;
    }

}//End of Class
