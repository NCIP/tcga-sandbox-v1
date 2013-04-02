/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BCRJson;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Count;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.GraphConfig;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.NodeData;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Output;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Position;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Size;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.Total;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.bcrpipeline.TumorTypes;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.IGC;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.NWCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.pipelineReportJsonFilesPath;

/**
 * implementation of the service layer of the pipelineReport
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class BCRPipelineReportServiceImpl implements BCRPipelineReportService {

    protected final Log logger = LogFactory.getLog(getClass());
    private String tumorList;
    private BCRJson bcrJson;
    private SimpleDateFormat InputBCRExcelDateFormater = new SimpleDateFormat("MM-yyyy");
    private SimpleDateFormat OutputBCRExcelDateFormater = new SimpleDateFormat("MMM-yyyy");
    private Comparator<ExtJsFilter> comparatorBCRDate = new Comparator<ExtJsFilter>() {
        public int compare(ExtJsFilter e1, ExtJsFilter e2) {
            int res;
            try {
                res = InputBCRExcelDateFormater.parse(e2.getId()).compareTo(InputBCRExcelDateFormater.parse(e1.getId()));
            } catch (ParseException e) {
                logger.info(e);
                res = e2.getId().compareTo(e1.getId());
            }
            return res;
        }
    };
    private FilenameFilter jsonfilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".json");
        }
    };

    @Override
    public List<NodeData> getNodeDataListData(String bcr) {
        String bcrLabel;
        if (bcr == null || "total".equals(bcr)) {
            bcrLabel = "BCRs";
        } else {
            bcrLabel = bcr.toUpperCase();
        }
        List<Output> bcrOutputList = new LinkedList<Output>();
        addToOutputList(bcrOutputList, new Output(bcrJson.getDq_init_screen(), "Initial Screen Failures", "red", "up"));
        addToOutputList(bcrOutputList, new Output(bcrJson.getSubmitted_to_bcr(), null, "true", "false", "dodgerblue"));
        addToOutputList(bcrOutputList, new Output(bcrJson.getPending_init_screen(), "Pending BCR Initial Screening", "darkorange", "down"));
        NodeData bcrs = new NodeData("igc", "Received at " + bcrLabel, "images/bcr.jpg", "cases", null, null);
        bcrs.setOutputs(bcrOutputList);

        List<Output> pathologyOutputList = new LinkedList<Output>();
        addToOutputList(pathologyOutputList, new Output(bcrJson.getDq_path(), "Pathology Failures", "red", "up"));
        addToOutputList(pathologyOutputList, new Output(bcrJson.getQual_path(), "Pathology Pass", "true", "false", "dodgerblue"));
        addToOutputList(pathologyOutputList, new Output(bcrJson.getPending_path_qc(), "Pathology Pending", "darkorange", "down"));
        NodeData pathology = new NodeData("pathology", "Pathology QC", "images/breast_cancer_slide_narrow.jpeg", "cases", null, null);
        pathology.setOutputs(pathologyOutputList);

        List<Output> molecularOutputList = new LinkedList<Output>();
        addToOutputList(molecularOutputList, new Output(bcrJson.getDq_mol(), "DQ Molecular", "red", "up"));
        addToOutputList(molecularOutputList, new Output(bcrJson.getQual_mol(), "Molecular Pass", "true", "false", "dodgerblue"));
        addToOutputList(molecularOutputList, new Output(bcrJson.getPending_mol_qc(), "Molecular Pending", "darkorange", "down"));
        NodeData molecular = new NodeData("molecular", "Molecular QC", "images/DNA_Science.jpeg", "cases", null, null);
        molecular.setOutputs(molecularOutputList);

        List<Output> genotypeOutputList = new LinkedList<Output>();
        addToOutputList(genotypeOutputList, new Output(bcrJson.getDq_genotype(), "DQ Genotype", "red", "up"));
        addToOutputList(genotypeOutputList, new Output(bcrJson.getDq_other(), "DQ Other", "red", "up"));
        addToOutputList(genotypeOutputList, new Output(bcrJson.getShipped(), "Shipped", "true", "false", "dodgerblue"));
        addToOutputList(genotypeOutputList, new Output(bcrJson.getPending_shipment(), "Awaiting Shipment", "#6082B6", "down"));
        addToOutputList(genotypeOutputList, new Output(bcrJson.getQualified_hold(), "Held", "darkorange", "down"));
        NodeData genotype = new NodeData("genotype", "Genotype/Final Review", "images/genotype.jpg", "cases", null, null);
        genotype.setOutputs(genotypeOutputList);

        List<NodeData> nodeList = new LinkedList<NodeData>();
        addToNodeList(nodeList, bcrs);
        addToNodeList(nodeList, pathology);
        addToNodeList(nodeList, molecular);
        addToNodeList(nodeList, genotype);
        if (bcrJson.getShipped() > 0) {
            nodeList.add(new NodeData("shipped", "Shipped", null, null, null, null));
        }
        return nodeList;
    }

    protected void addToOutputList(List<Output> list, Output output) {
        if (output != null && output.getCount() > 0) {
            list.add(output);
        }
    }

    protected void addToNodeList(List<NodeData> list, NodeData node) {
        if (node != null && node.getOutputs().size() > 0) {
            list.add(node);
        }
    }

    @Override
    public GraphConfig getGraphConfigData() {
        GraphConfig gc = new GraphConfig();
        gc.setRenderTo("raphgraph");
        gc.setPaperSize(new Size(1024, 600));
        gc.setScale(null);
        gc.setCenter("true");
        gc.setStartPos(new Position(1, 400));
        gc.setSquareCorners(5);
        gc.setSquareColor("60-#ccc-#eee");
        gc.setSquareSize(new Size(140, 187));
        gc.setPathHeight(187);
        gc.setPathLength(93);
        gc.setMinPathwidth(3);
        return gc;
    }

    @Override
    public Total getTotalData() {
        Total tot = new Total(14, 200, new Position(700, 1), "#d8eff6", "Total Shipped/Pending");
        tot.setCounts(new LinkedList() {{
            add(new Count(bcrJson.getReceived(), "Received"));
            add(new Count(bcrJson.getShipped(), "Shipped"));
            add(new Count(bcrJson.getPending_shipment(), "Awaiting Shipment"));
        }});
        return tot;
    }

    @Override
    public TumorTypes getTumorTypesData() {
        return new TumorTypes(14, getBlueboxWidth(), new Position(380, 1), "#d8eff6",
                "Tumors Represented", tumorList);
    }

    protected int getBlueboxWidth() {
        int res = 250;
        if (StringUtils.isNotBlank(tumorList)) {
            final String[] tab = tumorList.split(",");
            if (tab != null && tab.length > 0) {
                int tmp = 30 + tab.length * 10;
                res = (tmp > res) ? tmp : res;
            }
        }
        return res;
    }

    protected void setTumorList(List<BCRJson> bcrList) {
        tumorList = "";
        Collections.sort(bcrList, new Comparator<BCRJson>() {
            @Override
            public int compare(BCRJson bcrJson, BCRJson bcrJson1) {
                return bcrJson.getDisease().compareTo(bcrJson1.getDisease());
            }
        });
        for (BCRJson bcr : bcrList) {
            if (bcr.getDisease() != null && bcr.getDisease().length() > 0)
                tumorList += bcr.getDisease() + ", ";
        }
        tumorList = tumorList.substring(0, tumorList.lastIndexOf(", "));
    }

    @Override
    public synchronized List<ExtJsFilter> getDatesFromInputFiles() {
        final List<ExtJsFilter> res = new LinkedList<ExtJsFilter>();
        final Set<ExtJsFilter> tempSet = new HashSet<ExtJsFilter>();
        final File file = new File(pipelineReportJsonFilesPath);
        if (file.isDirectory()) {
            final String[] listOfFiles = file.list(jsonfilter);
            for (int i = 0; i < listOfFiles.length; i++) {
                final String el = listOfFiles[i];
                if (el.contains("-BCR_")) {
                    final ExtJsFilter ext = new ExtJsFilter();
                    final String str = el.substring(el.indexOf("_") + 1, el.indexOf(".json"));
                    try {
                        final Date date = InputBCRExcelDateFormater.parse(str);
                        ext.setText(OutputBCRExcelDateFormater.format(date));
                    } catch (ParseException e) {
                        logger.info(e);
                        ext.setText(str);
                    }
                    ext.setId(str);
                    tempSet.add(ext);
                }
            }
            res.addAll(tempSet);
            Collections.sort(res, comparatorBCRDate);
        }
        return res;
    }

    @Override
    public List<ExtJsFilter> getBcrData() {
        //For now hard coded ... maybe int the future,
        // if we get more bcr in the excel files we can change this implementation
        List<ExtJsFilter> res = new LinkedList<ExtJsFilter>();
        res.add(new ExtJsFilter(TOTAL, "All BCRs"));
        res.add(new ExtJsFilter(IGC, "IGC"));
        res.add(new ExtJsFilter(NWCH, "NWCH"));
        return res;
    }

    @Override
    public int readBCRInputFiles(String disease, String bcr, String date) {
        if (disease == null) {
            disease = "All";
        }
        if (date == null) {
            date = getDatesFromInputFiles().get(0).getId();
        }
        if (bcr == null) {
            bcr = getBcrData().get(0).getId();
        }
        final List<BCRJson> listIGC = getBCRJson(pipelineReportJsonFilesPath + "IGC-BCR_" + date + ".json");
        final List<BCRJson> listNWCH = getBCRJson(pipelineReportJsonFilesPath + "NWCH-BCR_" + date + ".json");
        final List<BCRJson> listAllBCR = getAllBCRJsonFile(listIGC, listNWCH);
        if (IGC.equals(bcr)) {
            return initBCRJson(listIGC, disease);
        } else if (NWCH.equals(bcr)) {
            return initBCRJson(listNWCH, disease);
        } else if (TOTAL.equals(bcr)) {
            return initBCRJson(listAllBCR, disease);
        } else {
            return 0;
        }
    }

    private int initBCRJson(final List<BCRJson> listBCR, final String disease) {
        if (listBCR.size() > 0) {
            setTumorList(listBCR);
            if ("All".equals(disease)) {
                bcrJson = getBCRJsonForAllDiseases(listBCR);
            } else {
                bcrJson = getBCRJsonForDisease(listBCR, disease);
                if (bcrJson == null) {
                    return 0;
                }
            }
            return 1;
        } else {
            return 0;
        }
    }

    protected BCRJson getBCRJsonForDisease(final List<BCRJson> listBCR, final String disease) {
        for (final BCRJson bcr : listBCR) {
            if (disease.equals(bcr.getDisease())) {
                return bcr;
            }
        }
        return null;
    }

    protected BCRJson getBCRJsonForAllDiseases(final List<BCRJson> listBCR) {
        final BCRJson resBCR = new BCRJson();
        for (final BCRJson bcr : listBCR) {
            resBCR.setDq_genotype(bcr.getDq_genotype() + (resBCR.getDq_genotype() == null ? 0 : resBCR.getDq_genotype()));
            resBCR.setDq_init_screen(bcr.getDq_init_screen() + (resBCR.getDq_init_screen() == null ? 0 : resBCR.getDq_init_screen()));
            resBCR.setDq_mol(bcr.getDq_mol() + (resBCR.getDq_mol() == null ? 0 : resBCR.getDq_mol()));
            resBCR.setDq_other(bcr.getDq_other() + (resBCR.getDq_other() == null ? 0 : resBCR.getDq_other()));
            resBCR.setDq_path(bcr.getDq_path() + (resBCR.getDq_path() == null ? 0 : resBCR.getDq_path()));
            resBCR.setPending_init_screen(bcr.getPending_init_screen() + (resBCR.getPending_init_screen() == null ? 0 : resBCR.getPending_init_screen()));
            resBCR.setPending_mol_qc(bcr.getPending_mol_qc() + (resBCR.getPending_mol_qc() == null ? 0 : resBCR.getPending_mol_qc()));
            resBCR.setPending_path_qc(bcr.getPending_path_qc() + (resBCR.getPending_path_qc() == null ? 0 : resBCR.getPending_path_qc()));
            resBCR.setPending_shipment(bcr.getPending_shipment() + (resBCR.getPending_shipment() == null ? 0 : resBCR.getPending_shipment()));
            resBCR.setQual_mol(bcr.getQual_mol() + (resBCR.getQual_mol() == null ? 0 : resBCR.getQual_mol()));
            resBCR.setQual_path(bcr.getQual_path() + (resBCR.getQual_path() == null ? 0 : resBCR.getQual_path()));
            resBCR.setReceived(bcr.getReceived() + (resBCR.getReceived() == null ? 0 : resBCR.getReceived()));
            resBCR.setShipped(bcr.getShipped() + (resBCR.getShipped() == null ? 0 : resBCR.getShipped()));
            resBCR.setSubmitted_to_bcr(bcr.getSubmitted_to_bcr() + (resBCR.getSubmitted_to_bcr() == null ? 0 : resBCR.getSubmitted_to_bcr()));
            resBCR.setQualified_hold(bcr.getQualified_hold() + (resBCR.getQualified_hold() == null ? 0 : resBCR.getQualified_hold()));
        }
        resBCR.setDisease("All");
        return resBCR;
    }

    private List<BCRJson> cloneList(List<BCRJson> bcrList) {
        List<BCRJson> clonedList = new LinkedList<BCRJson>();
        for (BCRJson bcr : bcrList) clonedList.add(new BCRJson(bcr));
        return clonedList;
    }

    protected List<BCRJson> getAllBCRJsonFile(final List<BCRJson> listIGC, final List<BCRJson> listNWCH) {
        final List<BCRJson> localBigBCR;
        final List<BCRJson> localSmallBCR;
        if (listIGC.size() > 0) {
            localBigBCR = cloneList(listIGC);
            localSmallBCR = cloneList(listNWCH);
        } else {
            localBigBCR = cloneList(listNWCH);
            localSmallBCR = cloneList(listIGC);
        }
        for (final BCRJson bigBCR : localBigBCR) {
            for (final BCRJson smallBCR : localSmallBCR) {
                if (smallBCR.getDisease().equals(bigBCR.getDisease())) {
                    bigBCR.setDq_genotype(bigBCR.getDq_genotype() + smallBCR.getDq_genotype());
                    bigBCR.setDq_init_screen(bigBCR.getDq_init_screen() + smallBCR.getDq_init_screen());
                    bigBCR.setDq_mol(bigBCR.getDq_mol() + smallBCR.getDq_mol());
                    bigBCR.setDq_other(bigBCR.getDq_other() + smallBCR.getDq_other());
                    bigBCR.setDq_path(bigBCR.getDq_path() + smallBCR.getDq_path());
                    bigBCR.setPending_init_screen(bigBCR.getPending_init_screen() + smallBCR.getPending_init_screen());
                    bigBCR.setPending_mol_qc(bigBCR.getPending_mol_qc() + smallBCR.getPending_mol_qc());
                    bigBCR.setPending_path_qc(bigBCR.getPending_path_qc() + smallBCR.getPending_path_qc());
                    bigBCR.setPending_shipment(bigBCR.getPending_shipment() + smallBCR.getPending_shipment());
                    bigBCR.setQual_mol(bigBCR.getQual_mol() + smallBCR.getQual_mol());
                    bigBCR.setQual_path(bigBCR.getQual_path() + smallBCR.getQual_path());
                    bigBCR.setReceived(bigBCR.getReceived() + smallBCR.getReceived());
                    bigBCR.setShipped(bigBCR.getShipped() + smallBCR.getShipped());
                    bigBCR.setSubmitted_to_bcr(bigBCR.getSubmitted_to_bcr() + smallBCR.getSubmitted_to_bcr());
                    bigBCR.setQualified_hold(bigBCR.getQualified_hold() + smallBCR.getQualified_hold());
                    localSmallBCR.remove(smallBCR);
                    break;
                }
            }
        }
        localBigBCR.addAll(localSmallBCR);
        return localBigBCR;
    }

    private List<BCRJson> getBCRJson(String fileName) {
        final List<BCRJson> list = new LinkedList<BCRJson>();
        try {
            final String json = FileUtil.readFile(new File(fileName), true);
            final JSONObject jsonObject = JSONObject.fromObject(json);
            final JSONArray caseByDisease = (JSONArray) jsonObject.get("case_summary_by_disease");
            for (int i = 0; i < caseByDisease.size(); i++) {
                final JSONObject jo = caseByDisease.getJSONObject(i);
                final BCRJson bcr = new BCRJson();
                bcr.setDisease((String) jo.get("tumor_abbrev"));
                bcr.setDq_genotype((Integer) jo.get("dq_genotype"));
                bcr.setDq_init_screen((Integer) jo.get("dq_init_screen"));
                bcr.setDq_mol((Integer) jo.get("dq_mol"));
                bcr.setDq_other((Integer) jo.get("dq_other"));
                bcr.setDq_path((Integer) jo.get("dq_path"));
                bcr.setPending_init_screen((Integer) jo.get("pending_init_screen"));
                bcr.setPending_mol_qc((Integer) jo.get("pending_mol_qc"));
                bcr.setPending_path_qc((Integer) jo.get("pending_path_qc"));
                bcr.setPending_shipment((Integer) jo.get("pending_shipment"));
                bcr.setQual_mol((Integer) jo.get("qual_mol"));
                bcr.setQual_path((Integer) jo.get("qual_path"));
                bcr.setReceived((Integer) jo.get("total_cases_rcvd"));
                bcr.setShipped((Integer) jo.get("shipped"));
                bcr.setSubmitted_to_bcr((Integer) jo.get("submitted_to_bcr"));
                bcr.setQualified_hold((Integer) (jo.get("qualified_hold") == null ? 0 : jo.get("qualified_hold")));
                list.add(bcr);
            }
        } catch (IOException e) {
            logger.info(e);
        }
        return list;
    }

    public BCRJson getBcrJson() {
        return bcrJson;
    }

    public void setBcrJson(BCRJson bcrJson) {
        this.bcrJson = bcrJson;
    }

}//End of Class
