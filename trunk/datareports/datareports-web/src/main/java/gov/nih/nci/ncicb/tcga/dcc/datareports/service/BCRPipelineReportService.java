/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.GraphConfig;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.NodeData;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Total;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.TumorTypes;

import java.util.List;

/**
 * interface containing the relevant code for the bcrpipeline report service methods
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BCRPipelineReportService {

    /**
     * get Dates from excel source file
     *
     * @return list of ExtJsFilter
     */
    public List<ExtJsFilter> getDatesFromInputFiles();

    /**
     * read excel source files
     *
     * @param disease
     * @param date
     * @return success
     */
    public int readBCRInputFiles(String disease, String date);

    /**
     * get node data for pipeline diagram
     *
     * @return list of NodeData
     */
    public List<NodeData> getNodeDataListData();

    /**
     * get graph config for pipeline diagram
     *
     * @return GraphConfig
     */
    public GraphConfig getGraphConfigData();

    /**
     * get Total data for pipeline diagram
     *
     * @return Total
     */
    public Total getTotalData();

    /**
     * get tumor types data for pipeline diagram
     *
     * @return TumorTypes
     */
    public TumorTypes getTumorTypesData();

}
