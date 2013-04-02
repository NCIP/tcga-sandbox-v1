package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for ProteinArrayLevelTwoDataFileValidator
 *
 * @author srinivasand
 *
 */
@RunWith(JMock.class)
public class ProteinArrayLevelTwoDataFileValidatorFastTest {
	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_FILE_DIR = 
    	SAMPLES_DIR + "qclive" + File.separator + "proteinArrayValidator" + File.separator + "level2" + File.separator + "filenameTest";

	private final ProteinArrayLevelTwoDataFileValidator validator = new ProteinArrayLevelTwoDataFileValidator();
    private final Mockery mockery = new JUnit4Mockery();
	private final Archive archive = new Archive();
    private QcContext qcContext;
	
    private CenterQueries mockCenterQueries;
    private PlatformQueries mockPlatformQueries;
    private TumorQueries mockDiseaseQueries;
    
	@Before
	public void setUp() {
		mockCenterQueries = mockery.mock(CenterQueries.class);
		mockPlatformQueries = mockery.mock(PlatformQueries.class);
		mockDiseaseQueries = mockery.mock(TumorQueries.class);
		validator.setCenterQueries(mockCenterQueries);
		validator.setPlatformQueries(mockPlatformQueries);
		validator.setDiseaseQueries(mockDiseaseQueries);
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
        qcContext = new QcContext();
		qcContext.setArchive(archive);
	}
    
    @Test
	public void testProcessFileGoodName() throws ProcessorException {
		final String fileName = TEST_FILE_DIR + File.separator + "mdanderson.org_OV.MDA_RPPA_Core.SuperCurve.Level_2.11.28.1.txt";
		final File f = new File(fileName);
    	final String domainName = "mdanderson.org";
    	final String platformName = "MDA_RPPA_Core";
    	final String centerType = "center";
    	final String tumorType = "OV";
    	final Platform p = new Platform();
    	final Tumor t = new Tumor();
    	t.setTumorName("OV");
    	archive.setTheTumor(t);
    	p.setCenterType("center");
		mockery.checking(new Expectations() {{
    		exactly(1).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(1).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    		exactly(1).of(mockDiseaseQueries).getTumorForName(tumorType);
    		will(returnValue(t));
		}});
		assertTrue(validator.processFile(f, qcContext));
        assertEquals(0, qcContext.getErrorCount());
	}
    
    @Test
	public void testProcessFileBadName() throws ProcessorException {
		final String fileName = TEST_FILE_DIR + File.separator + "badfilename.txt";
		final File f = new File(fileName);
    	final String domainName = "mdanderson.org";
    	final String platformName = "MDA_RPPA_Core";
    	final String centerType = "center";
    	final String tumorType = "OV";
    	final Platform p = new Platform();
    	final Tumor t = new Tumor();
    	t.setTumorName("OV");
    	archive.setTheTumor(t);
    	p.setCenterType("center");
		mockery.checking(new Expectations() {{
    		exactly(0).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(0).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    		exactly(0).of(mockDiseaseQueries).getTumorForName(tumorType);
    		will(returnValue(t));
		}});
		assertFalse(validator.processFile(f, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'badfilename.txt' is not a valid filename", qcContext.getErrors().get(0));
	}
    
    @Test
	public void testProcessFileManifest() throws ProcessorException {
		final String fileName = TEST_FILE_DIR + File.separator + "manifest.txt";
		final File f = new File(fileName);
    	final String domainName = "mdanderson.org";
    	final String platformName = "MDA_RPPA_Core";
    	final String centerType = "center";
    	final String tumorType = "OV";
    	final Platform p = new Platform();
    	final Tumor t = new Tumor();
    	t.setTumorName("OV");
    	archive.setTheTumor(t);
    	p.setCenterType("center");
		mockery.checking(new Expectations() {{
    		exactly(0).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(0).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    		exactly(0).of(mockDiseaseQueries).getTumorForName(tumorType);
    		will(returnValue(t));
		}});
		assertTrue(validator.processFile(f, qcContext));
        assertEquals(0, qcContext.getErrorCount());
	}
    
    @Test
	public void testCenterTypeGood() {
		final String platformName = "MDA_RPPA_Core";
    	final Platform p = new Platform();
		p.setCenterType("foo");
		mockery.checking(new Expectations() {{
			exactly(1).of(mockPlatformQueries).getPlatformForName(platformName);
			will(returnValue(p));
		}});
		String centerType = validator.getCenterType(platformName);
		assertEquals("foo", centerType);
	}
    
    @Test
    public void testCenterTypeNullPName() {
    	final String platformName = null;
    	final Platform p = new Platform();
		p.setCenterType("foo");
    	mockery.checking(new Expectations() {{
    		exactly(0).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    	}});
    	String centerType = validator.getCenterType(platformName);
    	assertNull(centerType);
    }

    @Test
    public void testCenterTypeQueryNull() {
    	final String platformName = "MDA_RPPA_Core";
    	final Platform p = new Platform();
    	p.setCenterType(null);
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    	}});
    	String centerType = validator.getCenterType(platformName);
    	assertNull(centerType);
    }

    @Test
    public void testCenterIdGood() {
    	final String domainName = "intgen.org";
    	final String centerType = "center";
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    	}});
    	Integer centerId = validator.getCenterId(domainName, centerType);
    	assertEquals(new Integer(1), centerId);
    }
    
    @Test
    public void testCenterIdNullDomainName() {
    	final String domainName = null;
    	final String centerType = "center";
    	mockery.checking(new Expectations() {{
    		exactly(0).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    	}});
    	Integer centerId = validator.getCenterId(domainName, centerType);
    	assertEquals(null, centerId);
    }
    
    @Test
    public void testCenterIdNullCenterType() {
    	final String domainName = "inten.org";
    	final String centerType = null;
    	mockery.checking(new Expectations() {{
    		exactly(0).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    	}});
    	Integer centerId = validator.getCenterId(domainName, centerType);
    	assertEquals(null, centerId);
    }
    
    @Test
    public void testvalidateTumorTypeGood() {
        initializeQcContext();
        String fileName = "testfilename.txt";
    	final Tumor t = new Tumor();
    	final String tumorType = "OV";
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockDiseaseQueries).getTumorForName(tumorType);
    		will(returnValue(t));
    	}});
    	boolean bRet = validator.validateTumorType(tumorType, fileName, qcContext);
    	assertTrue(bRet);
        assertEquals(0, qcContext.getErrorCount());
    }
    
    @Test
    public void testvalidateTumorTypeNull() {
        initializeQcContext();
        String fileName = "testfilename.txt";
    	final String tumorType = "OV.BAD";
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockDiseaseQueries).getTumorForName(tumorType);
    		will(returnValue(null));
    	}});
    	boolean bRet = validator.validateTumorType(tumorType, fileName, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'testfilename.txt' has an invalid disease name 'OV.BAD'", qcContext.getErrors().get(0));
    }
    
    @Test
    public void testvalidateDomainNameGood() {
        initializeQcContext();
        String fileName = "testfilename.txt";
    	final String domainName = "intgen.org";
    	final String platformName = "MDA_RPPA_Core";
    	final String centerType = "foo";
    	final Platform p = new Platform();
		p.setCenterType(centerType);
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(1).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    	}});
    	boolean bRet = validator.validateDomainName(domainName, platformName, fileName, qcContext);
    	assertTrue(bRet);
        assertEquals(0, qcContext.getErrorCount());
    }
    
    @Test
    public void testvalidateDomainNameBadCenterType() {
        initializeQcContext();
        String fileName = "testfilename.txt";
    	final String domainName = "intgen.org";
    	final String platformName = "MDA_RPPA_Core.BAD";
    	final String centerType = null;
    	final Platform p = new Platform();
		p.setCenterType(centerType);
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(0).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    	}});
    	boolean bRet = validator.validateDomainName(domainName, platformName, fileName, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'testfilename.txt' has an invalid platform name 'MDA_RPPA_Core.BAD'", qcContext.getErrors().get(0));
    }
    
    @Test
    public void testvalidateDomainBadCenterId() {
        initializeQcContext();
        String fileName = "testfilename.txt";
    	final String domainName = "intgen.org.BAD";
    	final String platformName = "MDA_RPPA_Core";
    	final String centerType = "foo";
    	final Platform p = new Platform();
		p.setCenterType(centerType);
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(1).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(0));
    	}});
    	boolean bRet = validator.validateDomainName(domainName, platformName, fileName, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'testfilename.txt' has an invalid domain name 'intgen.org.BAD'", qcContext.getErrors().get(0));
    }

    @Test
    public void testIsValidProteinArrayDataFileNameGood() {
    	final String domainName = "mdanderson.org";
    	final String platformName = "MDA_RPPA_Core";
    	final String centerType = "center";
    	final String tumorType = "OV";
    	final String fname = "mdanderson.org_OV.MDA_RPPA_Core.SuperCurve.Level_2.11.28.1.txt";
    	final Platform p = new Platform();
    	final Tumor t = new Tumor();
    	t.setTumorName("OV");
    	archive.setTheTumor(t);
    	p.setCenterType("center");
    	mockery.checking(new Expectations() {{
    		exactly(1).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(1).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    		exactly(1).of(mockDiseaseQueries).getTumorForName(tumorType);
    		will(returnValue(t));
    	}});
    	Boolean bRet = validator.isValidProteinArrayDataFileName(fname, archive, qcContext);
    	assertTrue(bRet);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testIsValidProteinArrayDataFileNameBad() {
    	final String domainName = "mdanderson.org";
    	final String platformName = "MDA_RPPA_Core";
    	final String centerType = "center";
    	final String tumorType = "OV";
    	final Platform p = new Platform();
    	final Tumor t = new Tumor();
    	t.setTumorName("OV");
    	archive.setTheTumor(t);
    	p.setCenterType("center");
    	mockery.checking(new Expectations() {{
    		exactly(0).of(mockPlatformQueries).getPlatformForName(platformName);
    		will(returnValue(p));
    		exactly(0).of(mockCenterQueries).findCenterId(domainName, centerType);
    		will(returnValue(1));
    		exactly(0).of(mockDiseaseQueries).getTumorForName(tumorType);
    		will(returnValue(t));
    	}});

        initializeQcContext();
    	String fname = "mdanderson.orgOV.MDA_RPPA_Core.SuperCurve.Level_2.11.28.1.txt";
    	Boolean bRet = validator.isValidProteinArrayDataFileName(fname, archive, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'mdanderson.orgOV.MDA_RPPA_Core.SuperCurve.Level_2.11.28.1.txt' is not a valid filename", qcContext.getErrors().get(0));

        initializeQcContext();
        fname = "mdanderson.org_OVMDA_RPPA_Core.SuperCurve.Level_2.11.28.1.txt";
    	bRet = validator.isValidProteinArrayDataFileName(fname, archive, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'mdanderson.org_OVMDA_RPPA_Core.SuperCurve.Level_2.11.28.1.txt' is not a valid filename", qcContext.getErrors().get(0));

        initializeQcContext();
    	fname = "mdanderson.org_OV.MDA_RPPACore.SuperCurve.Level_2.11.28.1.txt";
    	bRet = validator.isValidProteinArrayDataFileName(fname, archive, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'mdanderson.org_OV.MDA_RPPACore.SuperCurve.Level_2.11.28.1.txt' is not a valid filename", qcContext.getErrors().get(0));

        initializeQcContext();
    	fname = "mdanderson.org_OV.MDA_RPPA_CoreSuperCurve.Level_2.11.28.1.txt";
    	bRet = validator.isValidProteinArrayDataFileName(fname, archive, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'mdanderson.org_OV.MDA_RPPA_CoreSuperCurve.Level_2.11.28.1.txt' is not a valid filename", qcContext.getErrors().get(0));

        initializeQcContext();
    	fname = "mdanderson.org_OV.MDA_RPPA_Core.SuperCurveLevel_2.11.28.1.txt";
    	bRet = validator.isValidProteinArrayDataFileName(fname, archive, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'mdanderson.org_OV.MDA_RPPA_Core.SuperCurveLevel_2.11.28.1.txt' is not a valid filename", qcContext.getErrors().get(0));

        initializeQcContext();
    	fname = "mdanderson.org_OV.MDA_RPPA_Core.SuperCurve.Level_211.28.1.txt";
    	bRet = validator.isValidProteinArrayDataFileName(fname, archive, qcContext);
    	assertFalse(bRet);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'mdanderson.org_OV.MDA_RPPA_Core.SuperCurve.Level_211.28.1.txt' is not a valid filename", qcContext.getErrors().get(0));
    }

    @Test
    public void testWithUUIDAsCenterToken() {
    	final Tumor tumor = new Tumor();
    	tumor.setTumorName("BRCA");
    	archive.setTheTumor(tumor);
        final Platform platform = new Platform();
        platform.setCenterType("CGCC");

        mockery.checking(new Expectations() {{
    		one(mockPlatformQueries).getPlatformForName("MDA_RPPA_Core");
    		will(returnValue(platform));
    		one(mockCenterQueries).findCenterId("mdanderson.org", "CGCC");
    		will(returnValue(1));
    		one(mockDiseaseQueries).getTumorForName("BRCA");
    		will(returnValue(tumor));
    	}});

        final boolean isValid = validator.isValidProteinArrayDataFileName("mdanderson.org_BRCA.MDA_RPPA_Core.SuperCurve.Level_2.010B8D0C-9E26-464A-9820-87356E82DEBC.txt",
                archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testWithReallyBadFilename() {
        final Tumor tumor = new Tumor();
    	tumor.setTumorName("TEST");
    	archive.setTheTumor(tumor);
        assertFalse(validator.isValidProteinArrayDataFileName("squirrel!", archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 2 file 'squirrel!' is not a valid filename", qcContext.getErrors().get(0));

    }

    private void initializeQcContext() {
        qcContext = null;
        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }
}
