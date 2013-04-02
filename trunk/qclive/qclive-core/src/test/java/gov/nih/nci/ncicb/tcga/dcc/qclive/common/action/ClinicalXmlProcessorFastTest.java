/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for ClinicalXmlProcessor
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ClinicalXmlProcessorFastTest {

    private Mockery context = new JUnit4Mockery();
    private BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl();
    private BCRIDProcessor mockBcridProcessor = context.mock(BCRIDProcessor.class);
    private BCRDataService bcrDataService = context.mock(BCRDataService.class);
        
    private UUIDDAO uuidDaoService = context.mock(UUIDDAO.class);
    private Archive archive = new Archive();
    private Tumor tumor = new Tumor();
    private Map<String, BCRID> bcrIds = new HashMap<String, BCRID>();
    private List<String[]> aliquotsFromFile;
    private File fakeFile = new File("fakeFilePath");
    private ClinicalXmlProcessor processor;
    private QcContext qcContext;

    private static final Integer PRETEND_SHIPPED_PORTION_TYPE_ID = 2;

    @Before
    public void setup() throws ParseException, UUIDException {
        archive.setExperimentType(Experiment.TYPE_BCR);
        archive.setPlatform("bio");
        
        tumor.setTumorId(1);
        archive.setTheTumor(tumor);        
        Map<String,Long> fileIdList    =   new HashMap<String,Long>();
        fileIdList.put("file1",1L);
        fileIdList.put("file2",2L);
        fileIdList.put("fakeFilePath", 3l);
        archive.setFilenameToIdMap(fileIdList);
        final Center fakeCenter = new Center();
        fakeCenter.setCenterId(1);
        archive.setTheCenter(fakeCenter);
        // create BCR IDs for all barcodes in the test file
        aliquotsFromFile = new ArrayList<String[]>();
        aliquotsFromFile.add(new String[]{"TCGA-00-0000-01A-01D-0000-01", "2011-09-19", "11111111-2222-3333-4444-abcdefabcdef", "12"});
        aliquotsFromFile.add(new String[]{"TCGA-01-0000-01A-01D-0000-01", "2011-09-19", "aaaaaaaa-BBBB-cccc-dddd-123456789012", "12"});
        aliquotsFromFile.add(new String[]{"TCGA-02-0000-01A-01D-0000-01", "2011-09-19", "abcdabcd-1234-1234-5678-112233445566", "12"});

        // add an expectation for each barcode
        int id = 1;
        for (final String[] aliquotInfo :aliquotsFromFile) {
            final BCRID bcrId = makeBcrId(aliquotInfo[0], id++, aliquotInfo[2]);
            context.checking(new Expectations() {{
                one(bcrDataService).parseAliquotBarcode(aliquotInfo[0]);
                will(returnValue(bcrId));
                one(bcrDataService).handleAliquotBarcode(bcrId, archive, fakeFile);
            }});
        }        

        processor = new TestableClinicalXmlProcessor();
        processor.setBcrDataService(bcrDataService);
        processor.setDiseaseUuidDAOService(uuidDaoService);
        processor.setUuidDAOService(uuidDaoService);
        processor.setBcrProcessor(bcridProcessor);
        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }

    private static Matcher<List<BiospecimenToFile>> expectedBiospecimens(final Map<String, BCRID> bcrIds) {
        return new TypeSafeMatcher<List<BiospecimenToFile>>() {

            public boolean matchesSafely(final List<BiospecimenToFile> biospecimenToFiles) {
                if (bcrIds.size() != biospecimenToFiles.size()) {
                    return false;
                }
                List<Integer> expectedBiospecimenIds = new ArrayList<Integer>();
                for (final BCRID bcrid : bcrIds.values()) {
                    expectedBiospecimenIds.add(bcrid.getId());
                }

                for (final BiospecimenToFile bf : biospecimenToFiles) {
                    if (! expectedBiospecimenIds.contains(bf.getBiospecimenId())) {
                        return false;
                    }
                }
                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("biospecimens passed in have expected IDs");
            }
        };
    }

    private BCRID makeBcrId(final String barcode, final int barcodeId, final String uuid) throws ParseException {    	
        final BCRID id = bcridProcessor.parseAliquotBarcode(barcode);
        bcrIds.put(barcode, id);
        id.setId(barcodeId);
        id.setUUID(uuid);
        return id;
    }

    @Test
    public void test() throws Processor.ProcessorException, TransformerException, IOException, SAXException, XPathExpressionException, ParseException, ParserConfigurationException {
        final List<ShippedBiospecimen> biospecimens = new ArrayList<ShippedBiospecimen>();
        ShippedBiospecimen biospecimen = new ShippedBiospecimen();
        biospecimen.setUuid("uuid-1");
        biospecimen.setBarcode("barcode-1");
        biospecimen.setShippedBiospecimenId(123L);
        biospecimens.add(biospecimen);

        processor.setBcrProcessor(mockBcridProcessor);
        // note this test is with the UUID values populated as if they were in the XML file
        context.checking(new Expectations() {{
            one(bcrDataService).findAllAliquotsInFile(fakeFile);
            will(returnValue(aliquotsFromFile));
            one(bcrDataService).findAllShippedPortionsInFile(fakeFile);
            will(returnValue(biospecimens));
            one(bcrDataService).handleShippedBiospecimens(biospecimens, PRETEND_SHIPPED_PORTION_TYPE_ID,archive,fakeFile);
            one(bcrDataService).addBioSpecimenToFileAssociations(with(expectedBiospecimens(bcrIds)), with(tumor));            
            allowing(bcrDataService).getShippedItemId(ShippedBiospecimen.SHIPPED_ITEM_NAME_PORTION);
            will(returnValue(PRETEND_SHIPPED_PORTION_TYPE_ID));
            
            one (mockBcridProcessor).getPatientUUIDfromFile(fakeFile);
            will (returnValue(""));
            exactly(2).of(uuidDaoService).addParticipantFileUUIDAssociation("", 3l);
                       
        }});
        processor.processFile(fakeFile, qcContext);
        context.assertIsSatisfied();
        // make sure UUID was converted to lowercase
        assertEquals("aaaaaaaa-bbbb-cccc-dddd-123456789012", bcrIds.get("TCGA-01-0000-01A-01D-0000-01").getUUID());

        for (BCRID bcrid : bcrIds.values()) {
            assertEquals(new Integer(12), bcrid.getBatchNumber());
        }
    }

    @Test
    public void testDatabaseException() throws Exception {
    	
        context.checking(new Expectations() {{
            one(bcrDataService).findAllAliquotsInFile(fakeFile);
            will(returnValue(aliquotsFromFile));
            one(bcrDataService).findAllShippedPortionsInFile(with(any(File.class)));
            will(returnValue(new ArrayList<ShippedBiospecimen>()));

            one(bcrDataService).addBioSpecimenToFileAssociations(with(expectedBiospecimens(bcrIds)), with(tumor));
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new DataIntegrityViolationException("error!")));            
        }});
        try{
            processor.execute(archive, qcContext);
        }catch(Processor.ProcessorException pe){
            assertTrue(pe.getMessage().contains("Unexpected database error."));
        }
    }

    @Test
    public void testUUIDsNotSet() throws Exception {    	
    	
        // make them all null to simulate that the file didn't contain UUID values
        for (final BCRID bcrid : bcrIds.values()) {
            bcrid.setUUID(null);
        }
        
        processor.setBcrProcessor(mockBcridProcessor);       
        final BCRID bcrId = makeBcrId("TCGA-00-0000-01A-01D-0000-01", 1, "abcdabcd-1234-1234-5678-112233445566");
        
        context.checking(new Expectations() {{
            one(bcrDataService).findAllAliquotsInFile(fakeFile);
            will(returnValue(aliquotsFromFile));
            one(bcrDataService).findAllShippedPortionsInFile(with(any(File.class)));
            will(returnValue(new ArrayList<ShippedBiospecimen>()));
            one(bcrDataService).addBioSpecimenToFileAssociations(with(expectedBiospecimens(bcrIds)), with(tumor));            
            one (mockBcridProcessor).getPatientUUIDfromFile(fakeFile);
            will (returnValue(""));
            exactly(2).of(uuidDaoService).addParticipantFileUUIDAssociation("", 3l);            
        }});
        processor.execute(archive, qcContext);
    }
   
    
    /**
     *  Testable version of the processor, which doesn't go to the file system.
     */
    class TestableClinicalXmlProcessor extends ClinicalXmlProcessor {

        /**
         * @param archive the archive
         * @return an array with one fake file
         */
        protected File[] getFilesForExtension(final Archive archive) {
            return new File[]{fakeFile};
        }
    }
}
