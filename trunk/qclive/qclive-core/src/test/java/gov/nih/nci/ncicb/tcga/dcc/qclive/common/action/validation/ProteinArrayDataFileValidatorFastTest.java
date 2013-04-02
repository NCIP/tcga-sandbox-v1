package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class ProteinArrayDataFileValidatorFastTest {
	
	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	private static final String TEST_FILE_DIR = SAMPLES_DIR + "qclive/proteinArrayValidator";	
	String archiveName = TEST_FILE_DIR + "/mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.11.1.3";
	Archive archive = new Archive(archiveName);
	ProteinArrayLevelThreeDataFileValidator proteinValidator ;
	Tumor tumor;
    private QcContext qcContext;

	@Before
	public void setUp() throws ProcessorException{
		proteinValidator = new ProteinArrayLevelThreeDataFileValidator();	
		tumor = new Tumor();		
		tumor.setTumorName("OV");		
		archive.setTheTumor(tumor);	
		archive.setArchiveType(Archive.TYPE_LEVEL_3);
		archive.setExperimentType(Experiment.TYPE_CGCC);
		archive.setPlatform("MDA_RPPA_Core");
        qcContext = new QcContext();
	}
	@Test
	public void testIsValidProteinArrayDataFileName() throws ProcessorException{
		
        initializeQcContext();
		String fileName1 = "mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.blah.txt";
	    assertTrue(proteinValidator.isValidProteinArrayDataFileName(fileName1,archive,qcContext));
        assertEquals(0, qcContext.getErrorCount());
	    
		
        initializeQcContext();
		String fileName = "mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.11.1.3.txt";
	    assertTrue(proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext));
        assertEquals(0, qcContext.getErrorCount());

        initializeQcContext();
	    fileName = "mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.blah.txt";
	    assertTrue(proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext));
        assertEquals(0, qcContext.getErrorCount());

        initializeQcContext();
	    fileName = "someCenter_OV.MDA_RPPA_Core.protein_expression.Level_3.blah.txt";
	    assertTrue(proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext));
        assertEquals(0, qcContext.getErrorCount());

	    //center name is allowed to have underscores, so this is valid
        initializeQcContext();
	    fileName = "someCenter__OV.MDA_RPPA_Core.protein_expression.Level_3.blah.txt";
	    assertTrue (proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext));
        assertEquals(0, qcContext.getErrorCount());

	    //center name is allowed to have . , so this is valid
        initializeQcContext();
	    fileName = "some.Center__OV.MDA_RPPA_Core.protein_expression.Level_3.blah.txt";
	    assertTrue (proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext));
        assertEquals(0, qcContext.getErrorCount());

	    //center token at the end is allowed to have . , so this is valid
        initializeQcContext();
	    fileName = "sO123Me.Center__OV.MDA_RPPA_Core.protein_expression.Level_3.blah.......txt";
	    assertTrue (proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext));
        assertEquals(0, qcContext.getErrorCount());
	}
	
	@Test
	public void testInvalidFileExt() throws ProcessorException{			
        initializeQcContext();
		String fileName = "mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.11.1.3";
	    boolean isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);	   
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.11.1.3' is not a valid filename", qcContext.getErrors().get(0));

        initializeQcContext();
	    fileName = "mdand_OV.MDA_RPPA_Core.protein_expression.Level_3.blah.txt.txt.txt.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(isValid);
        assertEquals(0, qcContext.getErrorCount());

	    //missing filetype
        initializeQcContext();
		fileName = "mdanderson_OV.MDA_RPPA_Core.Level_3.11.1.3.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_OV.MDA_RPPA_Core.Level_3.11.1.3.txt' is not a valid filename", qcContext.getErrors().get(0));
	}
		
	@Test
	public void testInvalidLevel() throws ProcessorException{			
        initializeQcContext();
		String fileName = "mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_1.blah.txt";
	    boolean isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_1.blah.txt' is not a valid filename", qcContext.getErrors().get(0));

        initializeQcContext();
	    fileName = "mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_2.blah.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);	
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_2.blah.txt' is not a valid filename", qcContext.getErrors().get(0));

	    // missing level
        initializeQcContext();
		fileName = "merson_OV.MDA_RPPA_Core.protein_expression.11.1.3.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'merson_OV.MDA_RPPA_Core.protein_expression.11.1.3.txt' is not a valid filename", qcContext.getErrors().get(0));
	}	
	
	@Test
	public void testInvalidCenterToken() throws ProcessorException{
		// np center token , so should fail
        initializeQcContext();
		String fileName = "MDA_RPPA_Core.protein_expression.Level_3.blah.txt";
	    Boolean isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);		
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'MDA_RPPA_Core.protein_expression.Level_3.blah.txt' is not a valid filename", qcContext.getErrors().get(0));

	    // . for center name is apparently allowed
        initializeQcContext();
	    fileName = "._OV.MDA_RPPA_Core.protein_expression.Level_3...txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(isValid);	
        assertEquals(0, qcContext.getErrorCount());

	    //invalid no 2nd center token
        initializeQcContext();
		fileName = "mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);	    	   
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.txt' is not a valid filename", qcContext.getErrors().get(0));
	}
	
	@Test
	public void testInvalidTumor() throws ProcessorException{
		// LL is not set on the archive , so should fail
        initializeQcContext();
		String fileName = "mdanderson_LL.MDA_RPPA_Core.protein_expression.Level_3.blah.txt";
	    Boolean isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_LL.MDA_RPPA_Core.protein_expression.Level_3.blah.txt' is not a valid filename", qcContext.getErrors().get(0));

	    // invalid disease
        initializeQcContext();
		fileName = "mdanderson.org_TEST.MDA_RPPA_Core.protein_expression.Level_3.11.1.3.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson.org_TEST.MDA_RPPA_Core.protein_expression.Level_3.11.1.3.txt' is not a valid filename", qcContext.getErrors().get(0));
	}
	@Test
	public void testInvalidPlatform() throws ProcessorException{
        initializeQcContext();
		String fileName = "mdanderson_LL.MDA_RPPA_Core.protein_expppppression.Level_3.blah.txt";
	    Boolean isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);	
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_LL.MDA_RPPA_Core.protein_expppppression.Level_3.blah.txt' is not a valid filename", qcContext.getErrors().get(0));
	    //missing platform
        initializeQcContext();
		fileName = "mdanderson_OV.MDA_RPPA_Core.Level_3.11.1.3.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_OV.MDA_RPPA_Core.Level_3.11.1.3.txt' is not a valid filename", qcContext.getErrors().get(0));

	    //invalid platform
        initializeQcContext();
		fileName = "mdanderson_OV.MDA_RA_Core.protein_expression.Level_3.11.1.3.txt";
	    isValid =  proteinValidator.isValidProteinArrayDataFileName(fileName,archive,qcContext);
	    assertTrue(!isValid);	  
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_OV.MDA_RA_Core.protein_expression.Level_3.11.1.3.txt' is not a valid filename", qcContext.getErrors().get(0));
	}
	
	@Test
	public void testExecuteValidArhive() throws ProcessorException{
		String fileName = "/mdanderson_OV.MDA_RPPA_Core.protein_expression.Level_3.11.1.3.txt";	
		File archiveFileName = new File(archiveName + fileName);	
		QcContext ctx = new QcContext();
		ctx.setArchive(archive);
		assertTrue(proteinValidator.processFile(archiveFileName, ctx));				
        assertEquals(0, ctx.getErrorCount());
	}
	@Test
	public void testExecuteInValidArhive() throws ProcessorException{
		String fileName = "/mdanderson_GBM.MDA_RPPA_Core.protein_expression.Level_3.11.1.3.txt";	
		File archiveFileName = new File(archiveName + fileName);			
		QcContext ctx = new QcContext();
		ctx.setArchive(archive);
		assertTrue(!proteinValidator.processFile(archiveFileName, ctx));				
        assertEquals(1, ctx.getErrorCount());
        assertEquals("protein array level 3 file 'mdanderson_GBM.MDA_RPPA_Core.protein_expression.Level_3.11.1.3.txt' is not a valid filename", ctx.getErrors().get(0));
	}
	
	@Test
	public void testIgnoredFile() throws ProcessorException{		
		String fileName = "MANIfEST.txt";	
		File archiveFileName = new File(fileName);
		System.out.println ( archiveFileName.getName());
		QcContext ctx = new QcContext();
		ctx.setArchive(archive);
		assertTrue(proteinValidator.processFile(archiveFileName, ctx));
        assertEquals(0, ctx.getErrorCount());

		fileName = "DESCRIPTION.txt";	
		archiveFileName = new File(fileName);
		System.out.println ( archiveFileName.getName());	
		ctx = new QcContext();
		ctx.setArchive(archive);		
		assertTrue(proteinValidator.processFile(archiveFileName, ctx));
        assertEquals(0, ctx.getErrorCount());
	}
			
	
	@Test
	public void testIsCorrectArchiveType () throws ProcessorException{
		assertTrue(proteinValidator.isCorrectArchiveType(archive));
	}
	
	@Test
	public void testInvalidIsCorrectArchiveType () throws ProcessorException{
		archive.setArchiveType("aux");
		assertTrue(!proteinValidator.isCorrectArchiveType(archive));
	}		

    private void initializeQcContext() {
        qcContext = null;
        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }
}
