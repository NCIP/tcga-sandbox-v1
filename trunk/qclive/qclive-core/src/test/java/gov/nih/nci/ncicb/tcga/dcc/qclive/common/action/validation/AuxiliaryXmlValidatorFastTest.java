/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;

import java.io.File;
import java.text.ParseException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for testing the functionality of the {@link AuxiliaryXmlValidator}.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version
 */
@RunWith(JMock.class)
public class AuxiliaryXmlValidatorFastTest {
	
	private final Mockery mockeryContext = new JUnit4Mockery();
	
	private Archive archive;
	private Tumor tumor;
	private Center center;
	private QcContext qcContext;
	private AuxiliaryXmlValidator auxiliaryXmlValidator;
	private BCRUtils mockBcrUtils;
	private CodeTableQueries mockCodeTableQueries;
	
	private static final String SAMPLES_DIR = 
			Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	
	private static final String TEST_XML_DIR = 
			SAMPLES_DIR + 
			"qclive" + File.separator + 
			"auxiliaryXmlValidator" + File.separator;
	
	@Before
	public void before() throws ParseException, UUIDException {
		
		// Create the test archive
		archive = new Archive();
		archive.setExperimentType(Experiment.TYPE_BCR);
		
		// Add tumor to test archive
		tumor = new Tumor();
        tumor.setTumorName("COAD");
        archive.setTheTumor(tumor);
        
        // Add center to test archive
        center = new Center();
        center.setCenterId(1);
        archive.setTheCenter(center);
        
        // Create the QcContext
        qcContext = new QcContext();
        
        // Create AuxiliaryXmlValidator, and set the necessary fields
        mockBcrUtils = mockeryContext.mock(BCRUtils.class);
        mockCodeTableQueries = mockeryContext.mock(CodeTableQueries.class);
        auxiliaryXmlValidator = new AuxiliaryXmlValidator();
        auxiliaryXmlValidator.setBcrUtils(mockBcrUtils);
        auxiliaryXmlValidator.setCodeTableQueries(mockCodeTableQueries);
    }
		
	@Test
	public void testValidAuxXml() throws ProcessorException {
		
		final File validAuxXml = new File(TEST_XML_DIR + "nationwidechildrens.org_auxiliary.TCGA-BC-R2D2.xml");
		
		mockeryContext.checking(new Expectations() {{
				exactly(1).of(mockBcrUtils).isAuxiliaryFile(with(validAuxXml));
				will(returnValue(true));
				
				exactly(1).of(mockCodeTableQueries).tssCodeExists("BC");
				will(returnValue(true));
			}});
		
		assertTrue(auxiliaryXmlValidator.processFile(validAuxXml, qcContext));
		assertEquals(0, qcContext.getErrorCount());
	}
	
	@Test
	public void testAuxXmlInvalidBarcodeFmt() throws ProcessorException {
		
		final File auxXmlInvalidBarcodeFmt = new File(TEST_XML_DIR + "nationwidechildrens.org_auxiliary.TCGA-BC-AR15.xml");
		
		mockeryContext.checking(new Expectations() {{
				exactly(1).of(mockBcrUtils).isAuxiliaryFile(with(auxXmlInvalidBarcodeFmt));
				will(returnValue(true));
			}});
		
		assertFalse(auxiliaryXmlValidator.processFile(auxXmlInvalidBarcodeFmt, qcContext));
		assertEquals(1, qcContext.getErrorCount());
	}
	
	@Test
	public void testAuxXmlInvalidPatientUUID() throws ProcessorException {
		
		final File auxXmlInvalidPatientUUIDFmt = new File(TEST_XML_DIR + "nationwidechildrens.org_auxiliary.TCGA-BC-M1A1.xml");
		
		mockeryContext.checking(new Expectations() {{
				exactly(1).of(mockBcrUtils).isAuxiliaryFile(with(auxXmlInvalidPatientUUIDFmt));
				will(returnValue(true));
			}});
		
		assertFalse(auxiliaryXmlValidator.processFile(auxXmlInvalidPatientUUIDFmt, qcContext));
		assertEquals(1, qcContext.getErrorCount());
	}
	
	@Test
	public void testAuxXmlInvalidAliquotUUID() throws ProcessorException {
		
		final File auxXmlInvalidAliquotUUIDFmt = new File(TEST_XML_DIR + "nationwidechildrens.org_auxiliary.TCGA-BC-AR10.xml");
		
		mockeryContext.checking(new Expectations() {{
				exactly(1).of(mockBcrUtils).isAuxiliaryFile(with(auxXmlInvalidAliquotUUIDFmt));
				will(returnValue(true));
			}});
		
		assertFalse(auxiliaryXmlValidator.processFile(auxXmlInvalidAliquotUUIDFmt, qcContext));
		assertEquals(1, qcContext.getErrorCount());
	}
	
	@Test
	public void testAuxXmlInvalidPatientIdTSS() throws ProcessorException {
		
		final File auxXmlInvalidInvalidPatientIdTSS = new File(TEST_XML_DIR + "nationwidechildrens.org_auxiliary.TCGA-AC-M6A2.xml");
		
		mockeryContext.checking(new Expectations() {{
			exactly(1).of(mockBcrUtils).isAuxiliaryFile(with(auxXmlInvalidInvalidPatientIdTSS));
			will(returnValue(true));
			
			exactly(1).of(mockCodeTableQueries).tssCodeExists("AC");
			will(returnValue(false));
		}});
		
		assertFalse(auxiliaryXmlValidator.processFile(auxXmlInvalidInvalidPatientIdTSS, qcContext));
		assertEquals(2, qcContext.getErrorCount());
	}
	
	@Test
	public void testAuxXmlInvalidTSS() throws ProcessorException {
		
		final File auxXmlInvalidInvalidTSS = new File(TEST_XML_DIR + "nationwidechildrens.org_auxiliary.TCGA-A1-MR56.xml");
		
		mockeryContext.checking(new Expectations() {{
			exactly(1).of(mockBcrUtils).isAuxiliaryFile(with(auxXmlInvalidInvalidTSS));
			will(returnValue(true));
		}});
		
		assertFalse(auxiliaryXmlValidator.processFile(auxXmlInvalidInvalidTSS, qcContext));
		assertEquals(1, qcContext.getErrorCount());
	}
}
