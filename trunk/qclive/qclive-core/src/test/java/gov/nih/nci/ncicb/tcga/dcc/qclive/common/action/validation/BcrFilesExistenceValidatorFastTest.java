package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Test class for BcrFilesExistenceValidator
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BcrFilesExistenceValidatorFastTest {
    private final Mockery context = new JUnit4Mockery();
    private PlatformQueries mockPlatformQueries;
    private ShippedBiospecimenQueries mockShipBioQueries;
    private BcrFilesExistenceValidator validator;
    private ArchiveQueries mockArchiveQueries;
    private ManifestParser mockManifestParser;
    private Archive archive;
    private QcContext qcContext;
    private static final String TEST_DATA_FOLDER = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator + "qclive"+File.separator;


    @Before
    public void setup() {
        mockPlatformQueries = context.mock(PlatformQueries.class);
        mockArchiveQueries = context.mock(ArchiveQueries.class);
        mockManifestParser = context.mock(ManifestParser.class);
        mockShipBioQueries = context.mock(ShippedBiospecimenQueries.class);

        qcContext = new QcContext();
        validator = new BcrFilesExistenceValidator();
        context.assertIsSatisfied();
        archive = new Archive();
        validator.setPlatformQueries(mockPlatformQueries);
        validator.setArchiveQueries(mockArchiveQueries);
        validator.setManifestParser(mockManifestParser);
        validator.setShippedBiospecimenQueries(mockShipBioQueries);
        archive.setArchiveFile(new File("intgen.org_COAD.bio.Level_1.36.6.0.tar.gz"));
        archive.setPlatform("bio");
        archive.setTumorType("COAD");
        archive.setDomainName("intgen.org");
        archive.setSerialIndex("36");
        archive.setDeployLocation("intgen.org_COAD.bio.Level_1.36.6.0.tar.gz");
        qcContext.setArchive(archive);
    }

    @After
    public void tearDown(){
    	// clean up an optional metadata file    	
    	try{
		    final File metadataFile = new File(archive.getDeployDirectory(), BcrFilesExistenceValidator.BCR_REMOVED_FILES);
		    if (metadataFile.exists()){
		    	metadataFile.delete();
		    }
    	}catch (Exception e){
    		/* do nothing,  a NPE can happen if a test clears out
    		 the deploy location in Archive object , ignore it */
    	}
    	
	         
    }
    
    @Test
    public void testValidBCRArchive() throws Processor.ProcessorException, IOException, ParseException {
        final File manifest = new File(archive.getDeployDirectory(), ConstantValues.MANIFEST_NAME);
        context.checking(new Expectations() {{
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForCenterType(Experiment.TYPE_BCR)));
            one(mockArchiveQueries).getLatestArchiveId(archive.getArchiveNameUpToSerialIndex());
            will(returnValue(100L));
            one(mockArchiveQueries).getFilesForArchive(100L);
            will(returnValue(getLatestArchiveXMLFileList()));
            one(mockManifestParser).parseManifest(manifest);
            will(returnValue(getCurrentValidArchiveXMLFileList()));
        }});

        validator.execute(archive, qcContext);
    }       

    @Test
    public void testInValidBCRArchive() throws Processor.ProcessorException, IOException, ParseException {
        final File manifest = new File(archive.getDeployDirectory(), ConstantValues.MANIFEST_NAME);
        final Map<String,String> redactedParticipantsList  = new HashMap<String,String>();
    	redactedParticipantsList.put("3514", "intgen.org_clinical.TCGA-AA-3514.xml");
    	redactedParticipantsList.put("3519", "intgen.org_biospecimen.TCGA-AA-3519.xml");   
    	final String[] returnRedactedParticipantsList={};
        context.checking(new Expectations() {{
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForCenterType(Experiment.TYPE_BCR)));
            one(mockArchiveQueries).getLatestArchiveId(archive.getArchiveNameUpToSerialIndex());
            will(returnValue(100L));
            one(mockArchiveQueries).getFilesForArchive(100L);
            will(returnValue(getLatestArchiveXMLFileList()));
            one(mockManifestParser).parseManifest(manifest);
            will(returnValue(getCurrentInValidArchiveXMLFileList()));
            one (mockShipBioQueries).getRedactedParticipants(redactedParticipantsList.keySet());
            will (returnValue(Arrays.asList(returnRedactedParticipantsList)));
        }});
        try {
            validator.execute(archive, qcContext);
            fail("Exception was not thrown");
        } catch (Processor.ProcessorException pe) {
            assertEquals(1, qcContext.getErrorCount());
            assertTrue(qcContext.getErrors().get(0).contains("intgen.org_clinical.TCGA-AA-3514.xml,intgen.org_clinical.TCGA-AA-3519.xml"));
        }
    }
    
    @Test
    public void testInValidBCRArchiveMissingFiles() throws Processor.ProcessorException, IOException, ParseException {
        final File manifest = new File(archive.getDeployDirectory(), ConstantValues.MANIFEST_NAME);
        final Map<String,String> redactedParticipantsList  = new HashMap<String,String>();
    	redactedParticipantsList.put("3514", "intgen.org_clinical.TCGA-AA-3514.xml");
    	redactedParticipantsList.put("3519", "intgen.org_biospecimen.TCGA-AA-3519.xml");   
    	final String[] returnRedactedParticipantsList={"3514"};
        context.checking(new Expectations() {{
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForCenterType(Experiment.TYPE_BCR)));
            one(mockArchiveQueries).getLatestArchiveId(archive.getArchiveNameUpToSerialIndex());
            will(returnValue(100L));
            one(mockArchiveQueries).getFilesForArchive(100L);
            will(returnValue(getLatestArchiveXMLFileList()));
            one(mockManifestParser).parseManifest(manifest);
            will(returnValue(getCurrentInValidArchiveXMLFileList()));
            one (mockShipBioQueries).getRedactedParticipants(redactedParticipantsList.keySet());
            will (returnValue(Arrays.asList(returnRedactedParticipantsList)));
        }});
        try {
            validator.execute(archive, qcContext);
            fail("Exception was not thrown");
        } catch (Processor.ProcessorException pe) {
            assertEquals(1, qcContext.getErrorCount());
            assertTrue(qcContext.getErrors().get(0).contains("intgen.org_clinical.TCGA-AA-3519.xml"));
        }
    }
    

    @Test
    public void testInValidBCRArchiveWithRedactedFile() throws Processor.ProcessorException, IOException, ParseException {    	
    	archive.setDeployLocation(new File(".tar.gz").getAbsolutePath());
        final File manifest = new File(archive.getDeployDirectory(), ConstantValues.MANIFEST_NAME);
        final Map<String,String> redactedParticipantsList  = new HashMap<String,String>();
    	redactedParticipantsList.put("3514", "intgen.org_clinical.TCGA-AA-3514.xml");
    	redactedParticipantsList.put("3519", "intgen.org_biospecimen.TCGA-AA-3519.xml");   
    	final String[] returnRedactedParticipantsList={};
    	
    	// add metadata file
    	createMetadataFile(redactedParticipantsList.values());    	    	
    	
        context.checking(new Expectations() {{
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForCenterType(Experiment.TYPE_BCR)));
            one(mockArchiveQueries).getLatestArchiveId(archive.getArchiveNameUpToSerialIndex());
            will(returnValue(100L));
            one(mockArchiveQueries).getFilesForArchive(100L);
            will(returnValue(getLatestArchiveXMLFileList()));
            one(mockManifestParser).parseManifest(manifest);
            will(returnValue(getCurrentInValidArchiveXMLFileList()));
            one (mockShipBioQueries).getRedactedParticipants(redactedParticipantsList.keySet());
            will (returnValue(Arrays.asList(returnRedactedParticipantsList)));
        }});
        /* should not throw an exception , the file contains redacted files*/
        validator.execute(archive, qcContext);      
    }
    
    @Test
    public void testInValidRedactedFileAndDb() throws Processor.ProcessorException, IOException, ParseException {    	
    	archive.setDeployLocation(new File(".tar.gz").getAbsolutePath());
        final File manifest = new File(archive.getDeployDirectory(), ConstantValues.MANIFEST_NAME);
        final Map<String,String> redactedParticipantsList  = new HashMap<String,String>();
        redactedParticipantsList.put("3514", "intgen.org_clinical.TCGA-AA-3514.xml");
    	redactedParticipantsList.put("3519", "intgen.org_clinical.TCGA-AA-3519.xml");    	
    	final String[] returnRedactedParticipantsList={"3514"};
    	
    	// items to return from file
    	Map<String,String> returnFromFileMap = new HashMap<String,String>();
    	returnFromFileMap.put("3519","intgen.org_clinical.TCGA-AA-3519.xml");    	
    	// add metadata file
    	createMetadataFile(returnFromFileMap.values());    	    	
    	
        context.checking(new Expectations() {{
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForCenterType(Experiment.TYPE_BCR)));
            one(mockArchiveQueries).getLatestArchiveId(archive.getArchiveNameUpToSerialIndex());
            will(returnValue(100L));
            one(mockArchiveQueries).getFilesForArchive(100L);
            will(returnValue(getLatestArchiveXMLFileList()));
            one(mockManifestParser).parseManifest(manifest);
            will(returnValue(getCurrentInValidArchiveXMLFileList()));
            one (mockShipBioQueries).getRedactedParticipants(redactedParticipantsList.keySet());
            will (returnValue(Arrays.asList(returnRedactedParticipantsList)));
        }});
        /* should not throw an exception , the db contains one redacted file and the file another*/
        validator.execute(archive, qcContext);      
    }
    
    @Test
    public void testOtherArchives() throws Processor.ProcessorException, IOException, ParseException {
        archive = new Archive();
        archive.setArchiveFile(new File("jhu-usc.edu_READ.HumanMethylation27.Level_2.1.0.0.tar.gz"));
        archive.setPlatform("HumanMethylation27");
        archive.setTumorType("READ");
        archive.setDomainName("jhu-usc.edu");
        archive.setSerialIndex("1");
        archive.setDeployLocation("");
        qcContext.setArchive(archive);
        context.checking(new Expectations() {{
            one(mockPlatformQueries).getPlatformForName(archive.getPlatform());
            will(returnValue(getPlatformForCenterType(Experiment.TYPE_CGCC)));
        }});
        validator.execute(archive, qcContext);

    }
    
    @Test
    public void testRemoveRedactedFiles() throws ProcessorException{    	
    	final Map<String,String> redactedParticipantsList  = new HashMap<String,String>();
    	redactedParticipantsList.put("3514", "intgen.org_clinical.TCGA-AA-3514.xml");
    	redactedParticipantsList.put("3560", "intgen.org_biospecimen.TCGA-AA-3560.xml");
    	redactedParticipantsList.put("3519", "intgen.org_clinical.TCGA-AA-3519.xml");
    	redactedParticipantsList.put("3558", "intgen.org_biospecimen.TCGA-AA-3558.xml");
    	
    	final String[] returnRedactedParticipantsList={"3558","3560"};
    	context.checking(new Expectations() {{
            one(mockShipBioQueries).getRedactedParticipants(redactedParticipantsList.keySet());            
            will(returnValue(Arrays.asList(returnRedactedParticipantsList)));
        }});    	
    	List<String> returnList = validator.removeRedactedFiles(getXMLFileList(),archive,qcContext);
    	assertTrue(returnList.get(0).equals("intgen.org_clinical.TCGA-AA-3514.xml"));
    	assertTrue(returnList.get(1).equals("intgen.org_clinical.TCGA-AA-3519.xml"));    
    }

    @Test
    public void testFileRegex(){
    	 Matcher xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher("intgen.org_clinical.TCGA-AA-3A19.xml");
    	
    	if(!xmlFileNameMatcher.matches()){
            fail();
        }
    	xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher("intgen.org_biospecimen.TCGA-AA-3a19.xml");
    	if(!xmlFileNameMatcher.matches()){
            fail();
        }
    	xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher("nationwidechildrens.org_biospecimen.TCGA-C5-A0TN.xml");
    	if(!xmlFileNameMatcher.matches()){
            fail();
        }
    }
    
    @Test
    public void testFailFileRegex(){
    	Matcher xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher("intgen.org_clinical.TCGA-1A-35919.xml");    	
    	if(xmlFileNameMatcher.matches()){
            fail();
        }
    	xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher("intgen.org_biospecimen.TCGA-3519.xml");
    	if(xmlFileNameMatcher.matches()){
            fail();
        }
    	xmlFileNameMatcher = BcrFilesExistenceValidator.XML_FILE_NAME_PATTERN.matcher("intgen.org_biospecimen.TCGA--AA3519.xml");
    	if(xmlFileNameMatcher.matches()){
            fail();
        }
    }
    
    @Test
    public void testRemoveRedactedFilesEmptyList() throws ProcessorException{    	    	
    	final Map<String,String> redactedParticipantsList  = new HashMap<String,String>();
     	redactedParticipantsList.put("3514", "intgen.org_clinical.TCGA-AA-3514.xml");
     	redactedParticipantsList.put("3560", "intgen.org_clinical.TCGA-AA-3560.xml");
     	redactedParticipantsList.put("3519", "intgen.org_clinical.TCGA-AA-3519.xml");
     	redactedParticipantsList.put("3558", "intgen.org_biospecimen.TCGA-AA-3558.xml");        	
    	final String[] returnRedactedParticipantsList={"3558","3560"};
    	context.checking(new Expectations() {{
            one(mockShipBioQueries).getRedactedParticipants(redactedParticipantsList.keySet());            
            will(returnValue(Arrays.asList(returnRedactedParticipantsList)));
        }});    	
    	List<String> returnList = validator.removeRedactedFiles(getXMLFileList(),archive,qcContext);
    	assertTrue(returnList.get(0).equals("intgen.org_clinical.TCGA-AA-3514.xml"));
    	assertTrue(returnList.get(1).equals("intgen.org_clinical.TCGA-AA-3519.xml"));    
    }



    @Test
    public void testRemoveRedactedFilesBadFileName() throws ProcessorException{    	    	    	
    	List<String> fileList = getXMLFileList();
    	fileList.set(0,"intgen.org_biospecimen.TCGA-1A-35-58.xml");
    	try{
    		List<String> returnList = validator.removeRedactedFiles(fileList,archive,qcContext);
    		fail();
    	}catch (ProcessorException e){
    		assertEquals(e.getMessage(),"An error occurred while processing archive 'intgen.org_COAD.bio.Level_1.36.6.0':" +
    				"  File intgen.org_biospecimen.TCGA-1A-35-58.xml does not follow BCR XML file name pattern ");
    	}
    	
    }

     @Test
     public void testRedactedFilesFileNameNotInLatestArchive() throws ProcessorException, IOException {
         archive.setDeployLocation(new File(TEST_DATA_FOLDER + ".tar.gz").getAbsolutePath());
         final List <String> latestArchiveFiles = getXMLFileList();
         final Map<String,String> redactedFilesList  = new HashMap<String,String>();
         redactedFilesList.put("3514", "intgen.org_clinical.TCGA-01-0491.xml");
         createMetadataFile(redactedFilesList.values());
         List<String> returnList = validator.getParticipantsFromMetadataFile(archive,latestArchiveFiles,qcContext);
         assertEquals(1,qcContext.getWarningCount());
     }
    @Test
    public void testGetCurrentArchiveXMLFileListExceptions() throws ProcessorException, IOException, ParseException{
    	final File manifest = new File(archive.getDeployDirectory(), ConstantValues.MANIFEST_NAME);
    	context.checking(new Expectations() {{    		
    		 one(mockManifestParser).parseManifest(manifest);
             will(throwException(new IOException()));
        }});   
    	try{ 
    		validator.getCurrentArchiveXMLFileList(archive);
    		fail();
    	}catch (ProcessorException e){
    		assertEquals(e.getMessage(),"Error reading the manifest file intgen.org_COAD.bio.Level_1.36.6.0" + File.separator + "MANIFEST.txtnull");
    	}
    	context.checking(new Expectations() {{    		
   		 one(mockManifestParser).parseManifest(manifest);
            will(throwException(new ParseException("can't parse",1)));
       }});   
	   	try{ 
	   		validator.getCurrentArchiveXMLFileList(archive);
	   		fail();
	   	}catch (ProcessorException e){
	   		assertEquals(e.getMessage(),"Error parsing the manifest file intgen.org_COAD.bio.Level_1.36.6.0" + File.separator + "MANIFEST.txtcan't parse");
	   	}
    	
    }
    
    @Test (expected=ProcessorException.class)
    public void testCheckFileBadName () throws IOException, ProcessorException{
    	archive.setDeployLocation(new File(".tar.gz").getAbsolutePath());
        final List <String> latestArchiveFiles = new ArrayList<String>();
        latestArchiveFiles.add("intgen.org_clinical.TCGA-01-0491.xml");
    	final Map<String,String> redactedParticipantsList  = new HashMap<String,String>();
     	redactedParticipantsList.put("3514", "intgen.org_clinical.TCGA-AA-35b14.xml");
     	redactedParticipantsList.put("3519", "intgen.org_biospecimen.TCGA-A1-35b19.xml");        	
    	createMetadataFile(redactedParticipantsList.values());
    	validator.getParticipantsFromMetadataFile(archive,getXMLFileList(),qcContext);
    }
    
    @Test
    public void testGetName(){
    	assertEquals (validator.getName(),"Previous bcr archive files existence validation");
    }
    
    private void createMetadataFile(Collection<String> filesToInsert) throws IOException{
    	BufferedWriter out  = null;
    	try{
	    	final File metadataFile = new File(archive.getDeployDirectory(), BcrFilesExistenceValidator.BCR_REMOVED_FILES);
	    	metadataFile.createNewFile();
	        out =  new BufferedWriter(new FileWriter(metadataFile));
	        
	        for(String fileName:filesToInsert){
		        out.write(fileName);
		        out.newLine();
	        }
    	}finally{
    		if (out != null){
    			out.close();
    		}
    	}                                    
    	
    }
    
    
    
    private List<String> getXMLFileList(){
    	List<String> xmlFileList = new ArrayList<String>();     
    	xmlFileList.add("intgen.org_biospecimen.TCGA-AA-3558.xml");                
    	xmlFileList.add("intgen.org_biospecimen.TCGA-AA-3560.xml");                
    	xmlFileList.add("intgen.org_clinical.TCGA-AA-3514.xml");
    	xmlFileList.add("intgen.org_clinical.TCGA-AA-3519.xml");        
        return xmlFileList;
    }
    
    private Platform getPlatformForCenterType(final String CenterType) {
        final Platform platform = new Platform();
        platform.setCenterType(CenterType);
        return platform;
    }


    private List<FileInfo> getLatestArchiveXMLFileList() {
        List<FileInfo> xmlFileList = new ArrayList<FileInfo>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("intgen.org_biospecimen.TCGA-AA-3558.xml");
        xmlFileList.add(fileInfo);
        fileInfo = new FileInfo();
        fileInfo.setFileName("intgen.org_biospecimen.TCGA-AA-3560.xml");
        xmlFileList.add(fileInfo);
        fileInfo = new FileInfo();
        fileInfo.setFileName("intgen.org_clinical.TCGA-AA-3514.xml");
        xmlFileList.add(fileInfo);
        fileInfo = new FileInfo();
        fileInfo.setFileName("intgen.org_clinical.TCGA-AA-3519.xml");
        xmlFileList.add(fileInfo);
        return xmlFileList;
    }

    private Map<String, String> getCurrentValidArchiveXMLFileList() {
        Map<String, String> xmlFileList = new HashMap<String, String>();
        xmlFileList.put("intgen.org_biospecimen.TCGA-AA-3558.xml", "d41d8cd98f00b204e9800998ecf8427e");
        xmlFileList.put("intgen.org_biospecimen.TCGA-AA-3560.xml", "f8cf53da5b2e970fa19be7a2fdc13c3d");
        xmlFileList.put("intgen.org_clinical.TCGA-AA-3514.xml", "d80eac868d2f90dbe2ddf919a89baecd");
        xmlFileList.put("intgen.org_clinical.TCGA-AA-3519.xml", "b0814b35707fd8ee25f8225bf8f1a99b");
        return xmlFileList;
    }

    private Map<String, String> getCurrentInValidArchiveXMLFileList() {
        Map<String, String> xmlFileList = new HashMap<String, String>();
        xmlFileList.put("intgen.org_biospecimen.TCGA-AA-3558.xml", "d41d8cd98f00b204e9800998ecf8427e");
        xmlFileList.put("intgen.org_biospecimen.TCGA-AA-3560.xml", "f8cf53da5b2e970fa19be7a2fdc13c3d");
        return xmlFileList;
    }
}
