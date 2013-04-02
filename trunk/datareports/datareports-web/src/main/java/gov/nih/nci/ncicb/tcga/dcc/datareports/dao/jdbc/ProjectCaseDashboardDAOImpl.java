/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TargetCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.ProjectCaseDashboardDAO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.GBM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.NA;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.OV;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.pipelineReportJsonFilesPath;

/**
 * Jdbc DAO implementation of the Project Case dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class ProjectCaseDashboardDAOImpl implements ProjectCaseDashboardDAO, Serializable {

    protected static final Log logger = LogFactory.getLog(ProjectCaseDashboardDAOImpl.class);

    private JdbcTemplate jdbcTemplate;

    private static Method[] projectCaseClassMethods = ProjectCase.class.getMethods();
    private static List<String> requiredMethodNames = Arrays.asList(new String[]{
            "getMethylationCGCC",
            "getMicroRNACGCC",
            "getExpressionArrayCGCC",
            "getExpressionRNASeqCGCC",
            "getCopyNumberSNPCGCC",
            "getLowPassGCC",
            "getMutationGSC",
            "getMicroRNAGSC",
            "getExpressionRNASeqGSC",
            "getExomeGSC",
            "getGenomeGSC",
            "getLowPassGSC"
    });

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void refreshProjectCaseDashboardProcedure() {
        long start = System.currentTimeMillis();
        jdbcTemplate.execute(ProjectCaseDashboardConstants.QUERY_REFRESH_PROJECT_CASE_DASHBOARD_PROC);
        long duration = System.currentTimeMillis() - start;
        logger.info("refresh Project Case Dashboard procedure took: " + duration / 1000 + " seconds.");
    }

    @Override
    public List<ProjectCase> getAllProjectCasesCounts() {
        return jdbcTemplate.query(ProjectCaseDashboardConstants.QUERY_PROJECT_CASE_COUNTS, projectCaseRowMapper);
    }

    @Override
    public Integer getCompleteCasesByDisease(final String disease) {
        return jdbcTemplate.queryForInt(ProjectCaseDashboardConstants.QUERY_COMPLETE_CASE_BY_DISEASE,
                disease);

    }

    private final ParameterizedRowMapper<ProjectCase> projectCaseRowMapper =
            new ParameterizedRowMapper<ProjectCase>() {
                public ProjectCase mapRow(ResultSet rs, int i) throws SQLException {
                    final ProjectCase pc = new ProjectCase();
                    final String disease = rs.getString(1);
                    pc.setDisease(disease);

                    //CGCC Part
                    pc.setMethylationCGCC(getDisplayValue(rs.getLong(2), getTarget(disease, "methylationGCC")));
                    pc.setMicroRNACGCC(getDisplayValue(rs.getLong(3), getTarget(disease, "microRNAGCC")));
                    pc.setExpressionArrayCGCC(getDisplayValue(rs.getLong(4), getTarget(disease, "expressionArrayGCC")));
                    pc.setExpressionRNASeqCGCC(getDisplayValue(rs.getLong(5), getTarget(disease, "rnaSeqGCC")));
                    pc.setCopyNumberSNPCGCC(getDisplayValue(rs.getLong(6), getTarget(disease, "copyNumberSNPGCC")));
                    pc.setLowPassGCC(getDisplayValue(rs.getLong(12), getTarget(disease, "lowPassGCC")));

                    //GSC Part
                    pc.setMutationGSC(getDisplayValue(rs.getLong(7), getTarget(disease, "mutationGSC")));
                    pc.setMicroRNAGSC(getDisplayValue(rs.getLong(8), getTarget(disease, "microRNAGSC")));
                    pc.setExpressionRNASeqGSC(getDisplayValue(rs.getLong(9), getTarget(disease, "rnaSeqGSC")));
                    pc.setExomeGSC(getDisplayValue(rs.getLong(10), getTarget(disease, "exomeGSC")));
                    pc.setGenomeGSC(getDisplayValue(rs.getLong(11), getTarget(disease, "genomeGSC")));
                    pc.setLowPassGSC(getDisplayValue(rs.getLong(13), getTarget(disease, "lowPassGSC")));

                    //BCR Part
                    pc.setProjectedCaseBCR(getTarget(disease, "bcr"));

                    //Complete Cases
                    final Integer completeCase = getCompleteCasesByDisease(disease);
                    pc.setCompleteCases("" + completeCase);
                    pc.setIncompleteCases(getIncompleteCases(pc, completeCase));

                    final List<String> NADiseases = Arrays.asList(GBM, OV);
                    if (NADiseases.contains(pc.getDisease().toUpperCase())) {
                        pc.setCompleteCases(NA);
                        pc.setIncompleteCases(NA);
                    }
                    //Overall Progress part
                    pc.setOverallProgress(processOverallProgress(pc));

                    return pc;
                }
            };

    protected String getDisplayValue(final Long numerator, final String denominator) {
        if (NA.equals(denominator)) {
            return NA;
        } else if ("0".equals(denominator)) {
            return "0";
        } else {
            return numerator + "/" + denominator;
        }
    }

    protected String getIncompleteCases(final ProjectCase pc, final Integer completeCase) {
        Integer total;
        try {
            total = Integer.parseInt(pc.getProjectedCaseBCR());
        } catch (NumberFormatException e) {
            total = 0;
        }
        if (completeCase > total) {
            return "0";
        } else {
            return "" + (total - completeCase);
        }
    }

    protected String getTarget(final String disease, final String dataTypeGetter) {
        String res = "500";
        try {
            for (final TargetCase targetCase : getTargetCaseJson()) {
                if (disease.equalsIgnoreCase(targetCase.getDisease())) {
                    try {
                        final Method getter = GetterMethod.getGetter(TargetCase.class, dataTypeGetter);
                        final Object obj = getter.invoke(targetCase);
                        if (obj != null) {
                            res = obj.toString();
                        }
                    } catch (IllegalAccessException e) {
                        logger.info(e);
                    } catch (InvocationTargetException e) {
                        logger.info(e);
                    } catch (NoSuchMethodException e) {
                        logger.info(e);
                    }
                }
            }
        } catch (IOException e) {
            logger.info(e);
        }
        return res;
    }

    /**
     * get target cases from json file
     *
     * @return list of target cases
     * @throws java.io.IOException
     */
    protected List<TargetCase> getTargetCaseJson() throws IOException {
        final List<TargetCase> list = new LinkedList<TargetCase>();
        final String fileName = pipelineReportJsonFilesPath + ProjectCaseDashboardConstants.TARGET_CASE_JSON_FILE;
        final String json = FileUtil.readFile(new File(fileName), true);
        final JSONObject jsonObject = JSONObject.fromObject(json);
        final JSONArray targetCaseByDisease = (JSONArray) jsonObject.get("target_case_by_disease");
        for (int i = 0; i < targetCaseByDisease.size(); i++) {
            final TargetCase targetCase = new TargetCase();
            final JSONObject jo = targetCaseByDisease.getJSONObject(i);
            targetCase.setDisease((String) jo.get("disease"));
            targetCase.setBcr((String) jo.get("bcr"));
            targetCase.setGenomeGSC((String) jo.get("gsc_genome"));
            targetCase.setExomeGSC((String) jo.get("gsc_exome"));
            targetCase.setMutationGSC((String) jo.get("gsc_mutation"));
            targetCase.setRnaSeqGSC((String) jo.get("gsc_rnaseq"));
            targetCase.setMicroRNAGSC((String) jo.get("gsc_microrna"));
            targetCase.setMethylationGCC((String) jo.get("gcc_methylation"));
            targetCase.setMicroRNAGCC((String) jo.get("gcc_microrna"));
            targetCase.setExpressionArrayGCC((String) jo.get("gcc_expressionarray"));
            targetCase.setRnaSeqGCC((String) jo.get("gcc_rnaseq"));
            targetCase.setCopyNumberSNPGCC((String) jo.get("gcc_snpcopynumber"));
            targetCase.setLowPassGCC((String) jo.get("gcc_lowpass"));
            targetCase.setLowPassGSC((String) jo.get("gsc_lowpass"));
            list.add(targetCase);
        }
        return list;
    }

    protected String processOverallProgress(final ProjectCase pc) {
        final int targetCase = Integer.parseInt(pc.getProjectedCaseBCR());
        int totalColumns = requiredMethodNames.size();
        double totalRatio = 0;
        String overallRatio = "";
        try {
            for (final Method method : projectCaseClassMethods) {
                if (requiredMethodNames.contains(method.getName())) {
                    final String result = (String) method.invoke(pc);
                    if (NA.equals(result)) {
                        totalColumns--;
                    } else {
                        totalRatio += getRatio(result);
                    }
                }
            }
            totalRatio = totalRatio / totalColumns;
            overallRatio = Math.round(totalRatio * targetCase) + "/" + targetCase;

        } catch (IllegalAccessException ie) {
            logger.error(ie.getMessage(), ie);
        } catch (InvocationTargetException ive) {
            logger.error(ive.getMessage(), ive);
        }

        return overallRatio;
    }

    protected Float getRatio(final String ratio) {
        if (ratio != null) {
            try {
                final String[] ratioTab = ratio.split("/", 2);
                if ((ratioTab.length == 2) && (Float.parseFloat(ratioTab[1]) > 0.0)) {
                    final Float f = Float.parseFloat(ratioTab[0]) / Float.parseFloat(ratioTab[1]);
                    if (f > 1f) {
                        return 1f;
                    } else {
                        return f;
                    }
                }
            } catch (NumberFormatException ne) {
                logger.info("Error parsing number " + ne.getMessage());
            }
        }
        return 0f;
    }

}//End of Class
