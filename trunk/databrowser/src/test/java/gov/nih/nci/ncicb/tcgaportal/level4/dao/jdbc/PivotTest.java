/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.Level4QueriesJDBCImplDBUnitConfig;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultBlank;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AggregateMutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ExpressionType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MethylationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.NonMutationAnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;
import gov.nih.nci.ncicb.tcgaportal.level4.util.FishersExactImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.util.PearsonCorrelationCalculator;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests against the PivotMaster DAO class, going through the Level4Queries interface implemented by Level4QueriesJDBCImpl.
 * This class is currently not a dbunit test,  but goes against the Dev database.
 * It could be converted into a dbunit test soon. However, since constructing dbunit datasets is very
 * time consuming, for purposes of rapid development it was deemed better to use the Dev db for now.
 *
 * @author Last updated by: $Author$
 * @version $Rev: 5360 $
 */

public class PivotTest extends Level4QueriesJDBCImplDBUnitConfig {

    private FilterSpecifier filter;
    private Level4QueriesJDBCImpl queries;

    static String testDbFileName_Gene = "Level4Queries.xml";

    public PivotTest() {
        super(testDbFileName_Gene);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new Level4QueriesJDBCImpl(1000, new PearsonCorrelationCalculator(),
                new FishersExactImpl(), dataSource);
        queries.setLogger(new ProcessLogger());
        queries.setFetchSize(1000);
        queries.setCorrelationCalculator(new PearsonCorrelationCalculator());
    }

    private void setDefaultLimits(NonMutationAnomalyType nmat) {
        nmat.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        nmat.setLowerLimit(-0.5);
        nmat.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        nmat.setUpperLimit(0.5);
        nmat.setFrequency(0.2F);
    }

    private void setDefaultLimitsLowFreq(NonMutationAnomalyType nmat) {
        nmat.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        nmat.setLowerLimit(-0.5);
        nmat.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        nmat.setUpperLimit(0.5);
        nmat.setFrequency(0.001F);  //everything that's not zero
    }

    private void checkResults(List<ColumnType> ctypes, Results results) {
        assertNotNull(results);
        assertTrue(results.getActualRowCount() > 0);

        int expectedPages = results.getActualRowCount() / results.getRowsPerPage();
        if (results.getActualRowCount() % results.getRowsPerPage() != 0) {
            expectedPages++;
        }
        assertEquals(expectedPages, results.getTotalPages());
        assertEquals(results.getTotalPages(), results.getGatheredPages());

        assertEquals(results.getActualRowCount(), results.getTotalRowCount());
        assertEquals(results.getActualRowCount(), results.getGatheredRows());

        int ipicked = 0;
        for (ColumnType ctype : ctypes) {
            if (ctype.isPicked()) ipicked++;
        }

        assertEquals(ipicked, results.getColumnTypes().size());
        for (ColumnType ctype : results.getColumnTypes()) {
            assertTrue(ctype.isPicked());
        }

        for (int irow = 0; irow < results.getActualRowCount() && irow < 50; irow++) {
            ResultRow row = results.getRow(irow);
            ResultValue[] columnVals = row.getColumnResults();
            assertEquals(ipicked, columnVals.length);
            for (int icol = 0; icol < ipicked; icol++) {
                assertTrue(columnVals[icol] instanceof ResultDouble || columnVals[icol] instanceof ResultBlank);
            }

            System.out.print(row.getName() + "\t");
            for (int icol = 0; icol < ipicked; icol++) {
                System.out.print(columnVals[icol].toString() + "\t");
            }
//            for (String key : row.getRowAnnotations().keySet()) {
//                System.out.print(row.getRowAnnotation(key).toString() + "\t");
//            }
            System.out.println();
        }
    }

    //stuff to run the regular search so I can look at the query

    Results runAnomalyFilter(FilterSpecifier filter) throws Throwable {
        MockCallback callback = new MockCallback();
        queries.getAnomalyResults(filter, callback);
        while (!callback.done) {
            Thread.sleep(1000);
        }
        if (callback.caughtException != null) {
            throw callback.caughtException;
        }
        return callback.results;
    }

    //do I get the same rows as a corresponding search in the other mode

    void checkPivotAgainstSummaryResults(Results pivotResults, Results summaryResults) {
        assertNotNull(pivotResults);
        assertNotNull(summaryResults);
        assertTrue(pivotResults.getActualRowCount() > 0);
        assertEquals(pivotResults.getActualRowCount(), summaryResults.getActualRowCount());

        for (int irow = 0; irow < pivotResults.getActualRowCount(); irow++) {
            ResultRow pivotRow = pivotResults.getRow(irow);
            ResultRow summaryRow = summaryResults.getRow(irow);
            assertEquals(pivotRow.getName(), summaryRow.getName());
        }
    }

    @Test
    public void testPivotErrorNoColumns() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        filter.setColumnTypes(ctypes);
        try {
            Results results = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "ABCD4", filter);
            Assert.fail();
        } catch (QueriesException e) {
        }
    }

    @Test
    public void testPivotErrorTooManyColumns() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        ctypes.get(0).setPicked(true);
        ctypes.get(1).setPicked(true);
        filter.setColumnTypes(ctypes);
        try {
            Results results = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "ABCD4", filter);
            Assert.fail();
        } catch (QueriesException e) {
        }
    }

    @Test
    public void testPivotErrorWrongMode() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        ctypes.get(0).setPicked(true);
        filter.setColumnTypes(ctypes);
        try {
            Results results = queries.getPivotResults(FilterSpecifier.ListBy.Pathways, "ABCD4", filter);
            Assert.fail();
        } catch (QueriesException e) {
        }
    }

    @Test
    public void testPivotErrorNullRowName() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        ctypes.get(0).setPicked(true);
        filter.setColumnTypes(ctypes);
        try {
            Results results = queries.getPivotResults(FilterSpecifier.ListBy.Pathways, null, filter);
            Assert.fail();
        } catch (QueriesException e) {
        }
    }

    @Test
    public void testPivotErrorNullFilter() throws QueriesException {
        try {
            Results results = queries.getPivotResults(FilterSpecifier.ListBy.Pathways, "EGFR", null);
            Assert.fail();
        } catch (QueriesException e) {
        }
    }

    //should fail if you try to pivot a correlation

    @Test
    public void testPivotErrorCorrelationColumn() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CorrelationType corrType = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CorrelationType) {
                corrType = (CorrelationType) ctype;
                break;
            }
        }
        corrType.setPicked(true);
        filter.setColumnTypes(ctypes);
        try {
            Results results = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "ABCD4", filter);
            Assert.fail();
        } catch (QueriesException e) {
        }
    }


    @Test
    public void testPivotAgainstSummary_CNByGene() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);
        setDefaultLimitsLowFreq(cntype1);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "EGFR", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        summaryFilter.setGeneList("EGFR");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_CNByPatient() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);
        setDefaultLimitsLowFreq(cntype1);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0038", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Genes);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        summaryFilter.setPatientList("TCGA-02-0038");
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    //for this test to work, the gene (specified as a gene list in the summary search) must have only one miRNA

    @Test
    public void testPivotAgainstSummary_CNMirnaByGene() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);
        setDefaultLimitsLowFreq(cntype1);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "ABCB7", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        summaryFilter.setGeneList("ABCB7");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_CNMirnaByPatient() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);
        setDefaultLimitsLowFreq(cntype1);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0332", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Genes);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        summaryFilter.setPatientList("TCGA-02-0332");
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_ExpByGene() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        ExpressionType exptype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof ExpressionType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                exptype = (ExpressionType) ctype;
                break;
            }
        }
        exptype.setPicked(true);
        setDefaultLimitsLowFreq(exptype);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "ABCB7", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        summaryFilter.setGeneList("ABCB7");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_ExpByPatient() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        ExpressionType exptype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof ExpressionType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                exptype = (ExpressionType) ctype;
                break;
            }
        }
        exptype.setPicked(true);
        setDefaultLimitsLowFreq(exptype);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0001", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Genes);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        summaryFilter.setPatientList("TCGA-02-0001");
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    //for this test to work, the gene (specified as a gene list in the summary search) must have only one miRNA

    @Test
    public void testPivotAgainstSummary_ExpMirnaByGene() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        ExpressionType exptype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof ExpressionType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                exptype = (ExpressionType) ctype;
                break;
            }
        }
        exptype.setPicked(true);
        setDefaultLimitsLowFreq(exptype);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "FCXW4", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        summaryFilter.setGeneList("FCXW4");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_ExpMirnaByPatient() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        ExpressionType exptype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof ExpressionType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                exptype = (ExpressionType) ctype;
                break;
            }
        }
        exptype.setPicked(true);
        setDefaultLimitsLowFreq(exptype);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0281", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Genes);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        summaryFilter.setPatientList("TCGA-02-0281");
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    //for this test to work, the gene (specified as a gene list in the summary search) must have only one methylation probe

    @Test
    public void testPivotAgainstSummary_MethylationByGene() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        MethylationType methtype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof MethylationType) {
                methtype = (MethylationType) ctype;
                break;
            }
        }
        methtype.setPicked(true);
        setDefaultLimitsLowFreq(methtype);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "AKR1CL2", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        summaryFilter.setGeneList("AKR1CL2");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_MethylationByPatient() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        MethylationType methtype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof MethylationType) {
                methtype = (MethylationType) ctype;
                break;
            }
        }
        methtype.setPicked(true);
        setDefaultLimitsLowFreq(methtype);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0281", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Genes);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        summaryFilter.setPatientList("TCGA-02-0281");
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_MutationNonsenseByGene() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        AggregateMutationType muttype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof AggregateMutationType && ((AggregateMutationType) ctype).getCategory() == MutationType.Category.Nonsense) {
                muttype = (AggregateMutationType) ctype;
                break;
            }
        }
        muttype.setPicked(true);
        muttype.setFrequency(0.001F);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "FMXW4", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        summaryFilter.setGeneList("FMXW4");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_MutationNonsenseByPatient() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        AggregateMutationType muttype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof AggregateMutationType && ((AggregateMutationType) ctype).getCategory() == MutationType.Category.Nonsense) {
                muttype = (AggregateMutationType) ctype;
                break;
            }
        }
        muttype.setPicked(true);
        muttype.setFrequency(0.001F);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0281", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Genes);
        summaryFilter.setDisease("GBM");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        summaryFilter.setPatientList("TCGA-02-0281");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_MutationAnyNonSilentByGene() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        AggregateMutationType muttype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof AggregateMutationType && ((AggregateMutationType) ctype).getCategory() == MutationType.Category.AnyNonSilent) {
                muttype = (AggregateMutationType) ctype;
                break;
            }
        }
        muttype.setPicked(true);
        muttype.setFrequency(0.001F);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "FPXW4", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Patients);
        summaryFilter.setDisease("GBM");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        summaryFilter.setGeneList("FPXW4");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testPivotAgainstSummary_MutationAnyNonSilentByPatient() throws Throwable {
        //pivot search on one column
        FilterSpecifier pivotFilter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        AggregateMutationType muttype = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof AggregateMutationType && ((AggregateMutationType) ctype).getCategory() == MutationType.Category.AnyNonSilent) {
                muttype = (AggregateMutationType) ctype;
                break;
            }
        }
        muttype.setPicked(true);
        muttype.setFrequency(0.001F);
        pivotFilter.setColumnTypes(ctypes);
        Results pivotResults = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0281", pivotFilter);
        checkResults(ctypes, pivotResults);

        FilterSpecifier summaryFilter = new FilterSpecifier();
        summaryFilter.setColumnTypes(ctypes);
        summaryFilter.setListBy(FilterSpecifier.ListBy.Genes);
        summaryFilter.setDisease("GBM");
        summaryFilter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        summaryFilter.setPatientList("TCGA-02-0281");
        summaryFilter.setGeneListOptions(FilterSpecifier.GeneListOptions.All);
        Results summaryResults = runAnomalyFilter(summaryFilter);

        checkPivotAgainstSummaryResults(pivotResults, summaryResults);
    }

    @Test
    public void testGetPivotPatientUpperLimitOnly() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);

        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.None);
        cntype1.setLowerLimit(0.0);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cntype1.setUpperLimit(0.2);
        cntype1.setFrequency(0.2F);

        filter.setColumnTypes(ctypes);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setGeneList("ABCC2, ABI1,CRABP2, EGFR");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-08-0531", filter);
        checkResults(ctypes, results);
        assertEquals(2, results.getActualRowCount());
        assertEquals("CRABP2", results.getRow(0).getName());
        assertEquals("EGFR", results.getRow(1).getName());
    }

    @Test
    public void testGetPivotPatientNullLimits() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);

        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.None);
        cntype1.setLowerLimit(0.0);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.None);
        cntype1.setUpperLimit(0.0);
        cntype1.setFrequency(0.2F);

        filter.setColumnTypes(ctypes);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setGeneList("EGFR, CRABP2, PRG2, CDK4");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-08-0531", filter);
        checkResults(ctypes, results);
        assertEquals(4, results.getActualRowCount());
    }


    @Test
    public void testGetPivotGenePatientList() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);

        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cntype1.setLowerLimit(-0.0);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cntype1.setUpperLimit(0.0);
        cntype1.setFrequency(0.2F);

        filter.setColumnTypes(ctypes);
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filter.setPatientList("TCGA-02-0038, , TCGA-02-0281, TCGA-02-0332,TCGA-08-0531");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Genes, "EGFR", filter);
        checkResults(ctypes, results);
        assertEquals(4, results.getActualRowCount());
        assertEquals("TCGA-02-0038", results.getRow(0).getName());
        assertEquals("TCGA-02-0281", results.getRow(1).getName());
        assertEquals("TCGA-02-0332", results.getRow(2).getName());
        assertEquals("TCGA-08-0531", results.getRow(3).getName());
    }

    @Test
    public void testGetPivotPatientGeneList() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);

        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cntype1.setLowerLimit(-0.0);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cntype1.setUpperLimit(0.0);
        cntype1.setFrequency(0.2F);

        filter.setColumnTypes(ctypes);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setGeneList("CDK4, EGFR");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0038", filter);
        checkResults(ctypes, results);
        assertEquals(2, results.getActualRowCount());
        assertEquals("CDK4", results.getRow(0).getName());
        assertEquals("EGFR", results.getRow(1).getName());
    }

    @Test
    public void testGetPivotPatientGeneListMirna() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.miRNA) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);

        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cntype1.setLowerLimit(-0.0);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.GTE);
        cntype1.setUpperLimit(0.0);
        cntype1.setFrequency(0.2F);

        filter.setColumnTypes(ctypes);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setGeneList("AACS,ABCB7");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0332", filter);
        checkResults(ctypes, results);
        assertEquals(2, results.getActualRowCount());
        for (int i = 0; i < 1; i++) {
            assertEquals("AACS", results.getRow(i).getName());
        }
        for (int i = 1; i < 2; i++) {
            assertEquals("ABCB7", results.getRow(i).getName());
        }
    }

    @Test
    public void testGetPivotPatientLowerLimitOnly() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);

        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.LTE);
        cntype1.setLowerLimit(0.0);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.None);
        cntype1.setFrequency(0.2F);

        filter.setColumnTypes(ctypes);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setGeneList("ABCC2, ACCN3, ACHE,EGFR");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-02-0332", filter);
        checkResults(ctypes, results);
        assertEquals(1, results.getActualRowCount());
        assertEquals("EGFR", results.getRow(0).getName());
    }

    @Test
    public void testGetPivotPatientNoOperator() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);

        cntype1.setLowerOperator(UpperAndLowerLimits.Operator.None);
        cntype1.setUpperOperator(UpperAndLowerLimits.Operator.None);
        cntype1.setFrequency(0.2F);

        filter.setColumnTypes(ctypes);
        filter.setGeneListOptions(FilterSpecifier.GeneListOptions.List);
        filter.setGeneList("EGFR, CRABP2, PRG2, CDK4");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "TCGA-08-0531", filter);
        checkResults(ctypes, results);
        assertEquals(4, results.getActualRowCount());
    }

    @Test
    public void testNoRows() throws QueriesException {
        FilterSpecifier filter = new FilterSpecifier();
        List<ColumnType> ctypes = queries.getColumnTypes("GBM");
        CopyNumberType cntype1 = null;
        for (ColumnType ctype : ctypes) {
            if (ctype instanceof CopyNumberType
                    && ((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                cntype1 = (CopyNumberType) ctype;
                break;
            }
        }
        cntype1.setPicked(true);
        setDefaultLimits(cntype1);

        filter.setColumnTypes(ctypes);
        filter.setPatientListOptions(FilterSpecifier.PatientListOptions.List);
        filter.setPatientList("TCGA-02-0001, TCGA-02-0003, TCGA-02-0007, TCGA-02-0009, TCGA-02-0015, TCGA-02-0021, TCGA-02-0023, TCGA-02-0043, TCGA-02-0046, TCGA-02-0054, TCGA-02-0075, TCGA-02-0080, TCGA-02-0083, TCGA-02-0086, TCGA-02-0102, TCGA-02-0106, TCGA-02-0116, TCGA-02-0260, TCGA-02-0269, TCGA-02-0271, TCGA-02-0285, TCGA-02-0317, TCGA-02-0324, TCGA-02-0430, TCGA-02-0446");
        Results results = queries.getPivotResults(FilterSpecifier.ListBy.Patients, "ABCF2", filter);
        assertNotNull(results);
        assertEquals(0, results.getActualRowCount());
    }

}
