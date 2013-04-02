/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;

import javax.sql.DataSource;
import java.util.List;

/**
 * Pull data from the DB to be used to be loaded again later
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public interface SampleSummaryReportDAO {


    public void setDataSource(DataSource dataSource);

    /**
     * A query to return all of the rows in the Sample Summary table for the specific tumor type
     *
     * @param tumorAbbreviation the abbreviation of the tumor as represented in the DB
     * @return a series of rows to populate the Sample Summary table report
     */
    public List<SampleSummary> getSampleSummaryRows(final String tumorAbbreviation);


    /**
     * A query to get all of the rows in the Sample Summary table
     * <p/>
     * To get the columns in the right order
     * <p/>
     * select * from data_summary_report_detail order by center_name,center_type,portion_analyte
     *
     * @return a series of rows to populate the table
     */
    public List<SampleSummary> getSampleSummaryRows();


    /**
     * A query to return all of the samples for the Samples BCR Sent column
     *
     * @param tumorAbbr       cancer type abbreviation
     * @param centerName      e.g. broad.mit.edu
     * @param centerType      e.g. CGCC
     * @param portion_analyte e.g. D
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesBCRSent(String tumorAbbr,
                                                         String centerName,
                                                         String centerType,
                                                         String portion_analyte);

    /**
     * A query to return all of the samples for the center sent column in the Sample Summary report
     *
     * @param tumorAbbr       cancer type abbreviation
     * @param centerName      e.g. broad.mit.edu
     * @param centerType      e.g. CGCC
     * @param platform        e.g. Genome_Wide_SNP_6
     * @param portion_analyte e.g. D
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesCenterSent(String tumorAbbr,
                                                            String centerName,
                                                            String centerType,
                                                            String portion_analyte,
                                                            String platform);

    /**
     * A query to return all of the samples for the samples unaccounted for BCR columns in the Sample Summary report
     *
     * @param tumorAbbr       cancer type abbreviation
     * @param centerName      e.g. broad.mit.edu
     * @param centerType      e.g. CGCC
     * @param platform        e.g. Genome_Wide_SNP_6
     * @param portion_analyte e.g. D
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesUnaccountedForBCR(String tumorAbbr,
                                                                   String centerName,
                                                                   String centerType,
                                                                   String portion_analyte,
                                                                   String platform);

    /**
     * A query to return all of the samples for the samples unaccounted for center columns in the Sample Summary report
     *
     * @param tumorAbbr       cancer type abbreviation
     * @param centerName      e.g. broad.mit.edu
     * @param centerType      e.g. CGCC
     * @param portion_analyte e.g. D
     * @param platform        e.g. Genome_Wide-SNP_6
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesUnaccountedForCenter(String tumorAbbr,
                                                                      String centerName,
                                                                      String centerType,
                                                                      String portion_analyte,
                                                                      String platform);

    /**
     * A query to return all of the samples for a given level "total" column in the Sample Summary report
     *
     * @param tumorAbbr       cancer type abbreviation
     * @param centerName      e.g. broad.mit.edu
     * @param centerType      e.g. CGCC
     * @param platform        e.g. Genome_Wide_SNP_6
     * @param portion_analyte e.g. D
     * @param level           1, 2 or 3
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForLevelTotal(String tumorAbbr,
                                                String centerName,
                                                String centerType,
                                                String portion_analyte,
                                                String platform,
                                                int level);

}
