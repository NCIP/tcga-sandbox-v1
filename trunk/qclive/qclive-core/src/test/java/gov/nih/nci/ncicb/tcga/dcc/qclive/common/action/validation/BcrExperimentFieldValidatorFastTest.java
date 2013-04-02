/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;

import java.io.File;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test class for ExperimentFieldValidator
 * 
 * @author Your Name Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BcrExperimentFieldValidatorFastTest {

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private String goodArchive1 = SAMPLES_DIR + "qclive" + File.separator
			+ "bcrExperimentFieldValidator" + File.separator + "good"
			+ File.separator + "archive.tar.gz";
	private BcrExperimentFieldValidator validator = null;
	private Experiment experiment;
	private Experiment experiment2;
	private QcContext context;
	private Archive archive1;
	private Archive archive2;
	private Mockery mockContext = new JUnit4Mockery();
	final ClinicalLoaderQueries mockClinicalLoaderQueries = (ClinicalLoaderQueries) mockContext
			.mock(ClinicalLoaderQueries.class);
	final List mockList = (List) mockContext.mock(List.class);

	@Before
	public void setup() {
		validator = new BcrExperimentFieldValidator();
		validator.setClinicalLoaderQueries(mockClinicalLoaderQueries);
		validator.setClinicalPlatform("bio");

		experiment = new Experiment();
		experiment.setType(Experiment.TYPE_BCR);
		experiment.setPlatformName("bio");
		context = new QcContext();
		context.setExperiment(experiment);
		archive1 = new Archive();
		archive1.setArchiveFile(new File(goodArchive1));
		archive1.setDeployLocation(goodArchive1);
		archive1.setDeployStatus(Archive.STATUS_UPLOADED);
		experiment.addArchive(archive1);

		archive2 = new Archive();
		archive2.setArchiveFile(new File(goodArchive1));
		archive2.setDeployLocation(goodArchive1);
		archive2.setDeployStatus(Archive.STATUS_UPLOADED);
		experiment2 = new Experiment();
		experiment2.setType(Experiment.TYPE_CGCC);
		experiment2.setPlatformName("bio");
		experiment2.addArchive(archive2);
	}

	@Test
	public void testValidateNonBCR() throws Processor.ProcessorException {
		mockContext.checking(new Expectations() {
			{
				one(mockClinicalLoaderQueries).getClinicalXsdElements();
				will(returnValue(mockList));
				allowing(mockList).contains(with(any(Object.class)));
				will(returnValue(true));
				allowing(mockList).isEmpty();
				allowing(mockList).size();
			}
		});
		assertTrue(validator.execute(experiment2, context));
	}

	@Test
	public void testValidatePositive() throws Processor.ProcessorException {
		mockContext.checking(new Expectations() {
			{
				one(mockClinicalLoaderQueries).getClinicalXsdElements();
				will(returnValue(mockList));
				allowing(mockList).contains(with(any(Object.class)));
				will(returnValue(true));
				allowing(mockList).isEmpty();
				allowing(mockList).size();
			}
		});
		assertTrue(validator.execute(experiment, context));
	}

	@Test
	public void testValidateNegative() throws Processor.ProcessorException {
		mockContext.checking(new Expectations() {
			{
				one(mockClinicalLoaderQueries).getClinicalXsdElements();
				will(returnValue(mockList));
				allowing(mockList).contains(with(any(String.class)));
				will(returnValue(false));
				allowing(mockList).isEmpty();
				allowing(mockList).size();
			}
		});
		assertFalse(validator.execute(experiment, context));
	}

	@Test
	public void testGetName() {
		assertTrue("BCR experiment field validation"
				.equals(validator.getName()));
	}

}
