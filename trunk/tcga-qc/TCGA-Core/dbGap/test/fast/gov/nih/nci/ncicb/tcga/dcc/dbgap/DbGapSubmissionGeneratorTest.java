/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.dao.DbGapQueries;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.dao.DbGapQueriesJDBCImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test for dbgap submission generator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class DbGapSubmissionGeneratorTest extends DbGapTestParent {
    private Mockery context = new JUnit4Mockery();
    private ClinicalMetaQueries clinicalMetaQueries;
    private String locationForFiles = System.getProperty("user.dir") + "/dbGap/test/samples/submissionGenerator";
    private DbGapSubmissionGenerator generator;
    private DbGapQueries dbGapQueries;

    @Before
    public void setup() {
        clinicalMetaQueries = context.mock(ClinicalMetaQueries.class);
        dbGapQueries = context.mock(DbGapQueries.class);
        Map<String, ClinicalMetaQueries> clinicalMetaQueriesMap = new HashMap<String, ClinicalMetaQueries>();
        clinicalMetaQueriesMap.put("GBM", clinicalMetaQueries);
        Map<String, DbGapQueries> dbGapQueriesMap = new HashMap<String, DbGapQueries>();
        dbGapQueriesMap.put("GBM", dbGapQueries);
        generator = new DbGapSubmissionGenerator(clinicalMetaQueriesMap, dbGapQueriesMap);
        generator.setDiseaseTypes(Arrays.asList("GBM"));
        generator.setLocation(locationForFiles);
    }

    private void setupFullTest() {
        // set up what meta queries will return
        final ClinicalMetaQueries.ClinicalFile subjectsFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjectsFile, "BCRPATIENTBARCODE", "Patient barcode, same as SUBJID");

        final ClinicalMetaQueries.ClinicalFile samplesFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(samplesFile, "BCRALIQUOTBARCODE", "Equvalent to SAMPID");
        addFileColumn(samplesFile, "AMOUNT", "Amount of sample in uL");
        addFileColumn(samplesFile, "GELIMAGEFILE", "Name of image file for analyte");

        final ClinicalMetaQueries.ClinicalFile slidesFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(slidesFile, "BCRSLIDEBARCODE", "Slide id");
        addFileColumn(slidesFile, "BCRALIQUOTBARCODE", "Sample id");
        addFileColumn(slidesFile, "PERCENTTUMORCELLS", "Percent of cells determined to be tumor");

        final ClinicalMetaQueries.ClinicalFile subjToSampFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjToSampFile, "BCRPATIENTBARCODE", "Equivalent to SUBJID");
        addFileColumn(subjToSampFile, "BCRALIQUOTBARCODE", "Equivalent to SAMPID");

        final ClinicalMetaQueries.ClinicalFile subjDrugFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjDrugFile, "BCRPATIENTBARCODE", "Equivalent to SUBJID");
        addFileColumn(subjDrugFile, "DRUGNAME", "Drug name");

        final ClinicalMetaQueries.ClinicalFile subjExamFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjExamFile, "BCRPATIENTBARCODE", "Equivalent to SUBJID");
        addFileColumn(subjExamFile, "KARNOFSKYPERFORMANCESCORE", "Karnofsky performance score at examination");

        final ClinicalMetaQueries.ClinicalFile subjRadFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjRadFile, "BCRPATIENTBARCODE", "Equivalent to SUBJID");
        addFileColumn(subjRadFile, "RADIATIONDOSAGE", "Dosage per treatment");

        final ClinicalMetaQueries.ClinicalFile subjSurgFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjSurgFile, "BCRPATIENTBARCODE", "Equivalent to SUBJID");
        addFileColumn(subjSurgFile, "PROCEDURETYPE", "Type of procedure");

        final ClinicalMetaQueries.ClinicalFile subjInfoFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjInfoFile, "BCRPATIENTBARCODE", "Patient barcode, same as SUBJID");
        addFileColumn(subjInfoFile, "GENDER", "Patient gender", "Male", "Female", "Unspecified", "");
        addFileColumn(subjInfoFile, "VITALSTATUS", "Vital status of patient", "Alive", "Dead", "Unknown", "");

        context.checking( new Expectations() {{
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId(), true, null);
            will(returnValue(subjectsFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Samples.getFileId(), true, "GBM");
            will(returnValue(samplesFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Slides.getFileId(), true, "GBM");
            will(returnValue(slidesFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.SubjectsToSamples.getFileId(), true, null);
            will(returnValue(subjToSampFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.SubjectsDrugs.getFileId(), true, "GBM");
            will(returnValue(subjDrugFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.SubjectsExaminations.getFileId(), true, "GBM");
            will(returnValue(subjExamFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.SubjectsRadiations.getFileId(), true, "GBM");
            will(returnValue(subjRadFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.SubjectsSurgeries.getFileId(), true, "GBM");
            will(returnValue(subjSurgFile));
            allowing(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.SubjectsInfo.getFileId(), true, "GBM");
            will(returnValue(subjInfoFile));

            one(dbGapQueries).getClinicalData(subjectsFile);
            will(returnValue(Arrays.asList( makeList("1234", "GBM", "1"), makeList("5678", "GBM", "1"), makeList("0000", "GBM", "1"))));
            one(dbGapQueries).getClinicalData(subjInfoFile);
            will(returnValue(Arrays.asList( makeList("1234", "F", "Alive"), makeList("5678", "M", "Dead"), makeList("0000", "F", "Dead"))));
            one(dbGapQueries).getClinicalData(samplesFile);
            will(returnValue(Arrays.asList( makeList("123456789", "12", "file1"), makeList("987654321", "8", "file2"))));
            one(dbGapQueries).getClinicalData(slidesFile);
            will(returnValue(Arrays.asList( makeList("slide1", "aliquot1", "50%"), makeList("slide1", "aliquot2", "50%"), makeList("slide2", "aliquot1", "4%"))));
            one(dbGapQueries).getClinicalData(subjToSampFile);
            will(returnValue(Arrays.asList(makeList("1234", "aliquot1"), makeList("5678", "aliquot2"))));
            one(dbGapQueries).getClinicalData(subjDrugFile);
            will(returnValue(Arrays.asList(makeList("1234", "drug1"), makeList("5678", "drug2"), makeList("1234", "drug3"))));
            one(dbGapQueries).getClinicalData(subjExamFile);
            will(returnValue(Arrays.asList(makeList("1234", "70"), makeList("5678", "80"), makeList("5678", "90"))));
            one(dbGapQueries).getClinicalData(subjRadFile);
            will(returnValue(Arrays.asList(makeList("1234", "1000"), makeList("1234", "2000"), makeList("5678", "3000"))));
            one(dbGapQueries).getClinicalData(subjSurgFile);
            will(returnValue(Arrays.asList(makeList("1234", "surg"), makeList("1234", "surg2"), makeList("1234", "surg3"))));
        }});
    }

    private List<String> makeList(final String... items) {
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(items));
        return list;
    }


    @Test
    public void test() throws IOException {
        setupFullTest();
        
        String[] expectedFiles = {"tcga_manifest.txt", "tcga_samp_GBM_dd.txt",
                "tcga_slide_GBM_dd.txt", "tcga_subj_dd.txt", "tcga_subj_samp_dd.txt",
                "tcga_subj_rad_GBM_dd.txt", "tcga_subj_drugs_GBM_dd.txt", "tcga_subj_exams_GBM_dd.txt",
                "tcga_subj_surg_GBM_dd.txt",
                "tcga_samp_GBM.txt", "tcga_slide_GBM.txt", "tcga_subj.txt", "tcga_subj_samp.txt",
                "tcga_subj_rad_GBM.txt", "tcga_subj_drugs_GBM.txt", "tcga_subj_exams_GBM.txt", "tcga_subj_surg_GBM.txt",
                "tcga_subj_info_GBM.txt", "tcga_subj_info_GBM_dd.txt"};
        try {
            generator.run();
            context.assertIsSatisfied();

            // check actual generated files
            for (final String expectedFile : expectedFiles) {
                String expectedContent = getFileContent(locationForFiles + "/expected", expectedFile);
                String actualContent = getFileContent(locationForFiles, expectedFile);
                assertEquals(expectedContent, actualContent);
            }
        } finally {
            for (final String expectedFile : expectedFiles) {
                File createdFile = new File(locationForFiles, expectedFile);
                if (createdFile.exists()) {
                    createdFile.deleteOnExit();
                }
            }
        }
    }

    @Test
    public void testMakeGenerator() {
        FakeDataSource fakeDataSource1 = new FakeDataSource();
        FakeDataSource fakeDataSource2 = new FakeDataSource();
        FakeDataSource fakeDataSource3 = new FakeDataSource();
        Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
        dataSources.put("DS1", fakeDataSource1);
        dataSources.put("DS2", fakeDataSource2);
        dataSources.put("DS3", fakeDataSource3);

        FakeDataSource fakeCommonDataSource = new FakeDataSource();

        DbGapSubmissionGenerator gen = DbGapSubmissionGenerator.makeGenerator("location", dataSources, fakeCommonDataSource);
        assertNotNull(gen.getDataDictionaryGenerator());
        assertNotNull(gen.getDataFileGenerator());
        assertNotNull(gen.getDataDictionaryGenerator().getClinicalMetaQueries());
        assertEquals(3, gen.getDataDictionaryGenerator().getClinicalMetaQueries().size());
        assertEquals(fakeCommonDataSource, ((DbGapQueriesJDBCImpl)gen.getDataFileGenerator().getDbGapQueries().get("DS1")).getDataSource());
        assertNotNull(((DbGapQueriesJDBCImpl)gen.getDataFileGenerator().getDbGapQueries().get("DS1")).getTissueSourceSiteQueries());
        assertNotNull(((DbGapQueriesJDBCImpl)gen.getDataFileGenerator().getDbGapQueries().get("DS1")).getAnnotationQueries());
    }

    @Test
    public void testMakeDataSources() throws ClassNotFoundException, IOException {
        String testFile = locationForFiles + "/fake.db.properties";
        Map<String, DataSource> dataSources = DbGapSubmissionGenerator.makeDataSources(testFile);
        assertEquals(3, dataSources.size());
        assertNotNull(dataSources.get("GBM"));
        assertNotNull(dataSources.get("OV"));
        assertNotNull(dataSources.get("TEST"));
        assertEquals("gbmUrl", ((SingleConnectionDataSource)dataSources.get("GBM")).getUrl());
        assertEquals("gbmUser", ((SingleConnectionDataSource)dataSources.get("GBM")).getUsername());
        assertEquals("gbmPassword", ((SingleConnectionDataSource)dataSources.get("GBM")).getPassword());
    }

    private String getFileContent(final String path, final String file) throws IOException {
        StringBuilder content = new StringBuilder();
        final BufferedReader in = new BufferedReader( new FileReader( new File(path, file) ) );
        String str;
        while(( str = in.readLine() ) != null) {
            content.append(str);
            content.append("\n");
        }
        in.close();
        return content.toString();
    }

    class FakeDataSource implements DataSource {

        public Connection getConnection() throws SQLException {
            return null;
        }

        public Connection getConnection(final String username, final String password) throws SQLException {
            return null;
        }

        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public void setLogWriter(final PrintWriter out) throws SQLException {
        }

        public void setLoginTimeout(final int seconds) throws SQLException {
        }

        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        public <T> T unwrap(final Class<T> iface) throws SQLException {
            return null;
        }

        public boolean isWrapperFor(final Class<?> iface) throws SQLException {
            return false;
        }
    }
}
