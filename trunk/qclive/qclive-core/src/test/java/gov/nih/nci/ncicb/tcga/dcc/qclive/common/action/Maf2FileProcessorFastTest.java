/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.MafInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.MafInfoQueries;

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for MafFileProcessor
 * 
 * @author Robert Sfeir Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class Maf2FileProcessorFastTest {

	private Mockery context = new JUnit4Mockery();
	private FileInfoQueries mockFileInfoQueries = context
			.mock(FileInfoQueries.class);
	private MafInfoQueries mockMafInfoQueries = context
			.mock(MafInfoQueries.class);
	private BCRDataService bcrDataService= context.mock(BCRDataService.class);
	private QcContext qcContext;
	private MafInfo mafInfo;
	private MafFileProcessor processor;
	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	@Before
	public void setup() {
		final Archive archive = new Archive();
		archive.setId(1L);
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		archive.setExperimentType(Experiment.TYPE_GSC);
		Center center = new Center();
		center.setCenterId(10);
		archive.setTheCenter(center);
		qcContext = new QcContext();
		qcContext.setArchive(archive);
		Map<String, Long> fileNameToIdList = new HashMap<String, Long>();
		fileNameToIdList.put("file1", 1L);
		fileNameToIdList.put("file2", 2L);
		archive.setFilenameToIdMap(fileNameToIdList);

		mafInfo = new MafInfo();
		processor = new TestableMaf2FileProcessor(mafInfo);
		processor.setFileInfoQueries(mockFileInfoQueries);
		processor.setMafInfoQueries(mockMafInfoQueries);
		processor.setBcrDataService(bcrDataService);
	}

	@Test
	public void testMaf2() throws Processor.ProcessorException, UUIDException, ParseException {
        qcContext.setCenterConvertedToUUID(false);
		File maf2File = new File(SAMPLE_DIR + "qclive/mafFileValidator/goodMaf2/testMaf2.maf");
        qcContext.getArchive().getFilenameToIdToMap().put("testMaf2.maf", 5L);
		context.checking(new Expectations() {
			{
				one(mockFileInfoQueries).getFileId("testMaf2.maf", 1L);
				will(returnValue(5L));

                one(bcrDataService).getBiospecimenIds(Arrays.asList("TCGA-25-1317-01A-01W-0490-10", "TCGA-25-1317-00A-01W-0490-10", "TCGA-25-1317-00A-00W-0490-10"));
                will(returnValue(Arrays.asList(300, 301, 302)));

                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));
                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(5L));

				exactly(7).of(mockMafInfoQueries).addMaf(mafInfo); // 7 lines in
																	// maf file
				allowing(bcrDataService).parseAliquotBarcode(with(any(String.class)));
				will(returnBCRID());
			}
		});
		processor.execute(maf2File, qcContext);
		assertValues(qcContext, mafInfo);
	}

    @Test
	public void testMaf2UUIDS() throws Processor.ProcessorException, UUIDException, ParseException {
        qcContext.setCenterConvertedToUUID(true);
		File maf2File = new File(SAMPLE_DIR + "qclive/mafFileValidator/goodMaf2/mafWithUUIDHeader.maf");
        qcContext.getArchive().getFilenameToIdToMap().put("mafWithUUIDHeader.maf", 5L);
		context.checking(new Expectations() {
			{
				one(mockFileInfoQueries).getFileId("mafWithUUIDHeader.maf", 1L);
				will(returnValue(5L));

                one(bcrDataService).getShippedBiospecimenIds(Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74a", "5760a312-43d7-42fb-b03d-b2c6728ab74b"));
                will(returnValue(Arrays.asList(300L, 301L)));

                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));
                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(5L));

				exactly(1).of(mockMafInfoQueries).addMaf(mafInfo); // 7 lines in
																	// maf file
				allowing(bcrDataService).parseAliquotBarcode(with(any(String.class)));
				will(returnBCRID());
			}
		});
		processor.execute(maf2File, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

	private static BCRIDProcessorImpl bcridProcessorForParsing = new BCRIDProcessorImpl();

	private static Action returnBCRID() {
		return new Action() {

			@Override
			public Object invoke(final Invocation invocation) throws Throwable {
				return bcridProcessorForParsing.parseAliquotBarcode(invocation
						.getParameter(0).toString());
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("parses first parameter into a BCRID");
			}
		};
	}

	@Test(expected = Processor.ProcessorException.class)
	public void testMaf2ProcessFileHeaderFailure()
			throws Processor.ProcessorException {
		File mafFile = new File(SAMPLE_DIR
				+ "qclive/mafFileValidator/badMaf2/testBadMaf2Header.maf");

		context.checking(new Expectations() {
			{
				one(mockFileInfoQueries).getFileId("testBadMaf2Header.maf", 1L);
				will(returnValue(5L));
				exactly(0).of(mockMafInfoQueries).addMaf(mafInfo); // Should
																	// fail
																	// hence not
																	// do
																	// anything
																	// in maf
																	// file
			}
		});
		processor.doWork(mafFile, qcContext);
		assertValues(qcContext, mafInfo);
	}

	private void assertValues(final QcContext qcContext, final MafInfo mafInfo) {
		assertEquals(0, qcContext.getErrorCount());
		assertEquals(0, qcContext.getWarningCount());
		assertEquals(new Integer(10), mafInfo.getCenterID()); // make sure was
																// set from
																// archive, not
																// from file!
		assertEquals(new Long(5), mafInfo.getFileID());
		assertEquals("NOL9", mafInfo.getHugoSymbol());
		assertEquals(new Integer(79707), mafInfo.getEntrezGeneID());
		assertEquals("36", mafInfo.getNcbibuild());
		assertEquals("1", mafInfo.getChromosome());
		assertEquals(new Integer(6533111), mafInfo.getStartPosition());
		assertEquals(new Integer(6533111), mafInfo.getEndPosition());
		assertEquals("+", mafInfo.getStrand());
		assertEquals("DEL", mafInfo.getVariantType());
		assertEquals("T", mafInfo.getReferenceAllele());
		assertEquals("-", mafInfo.getTumorSeqAllele1());
		assertEquals("-", mafInfo.getTumorSeqAllele2());
		assertEquals("", mafInfo.getDbsnpRS());
		assertEquals("", mafInfo.getDbSNPValStatus());
		assertEquals("TCGA-25-1317-00A-01W-0490-10",
				mafInfo.getTumorSampleBarcode());
		assertEquals("TCGA-25-1317-01A-01W-0490-10",
				mafInfo.getMatchNormalSampleBarcode());
		assertEquals("T", mafInfo.getMatchNormSeqAllele1());
		assertEquals("T", mafInfo.getMatchNormSeqAllele2());
		assertEquals("", mafInfo.getTumorValidationAllele1());
		assertEquals("", mafInfo.getTumorValidationAllele2());
		assertEquals("", mafInfo.getMatchNormValidationAllele1());
		assertEquals("", mafInfo.getMatchNormValidationAllele2());
		assertEquals("Unknown", mafInfo.getVerificationStatus());
		assertEquals("Unknown", mafInfo.getValidationStatus());
		assertEquals("Somatic", mafInfo.getMutationStatus());
		assertEquals("genome.wustl.edu", mafInfo.getCenterName());
		assertEquals("Capture", mafInfo.getSequenceSource());
		assertEquals("1", mafInfo.getScore());
		assertEquals("dbGAP", mafInfo.getBamFile());
		assertEquals("Illumina GAIIx", mafInfo.getSequencer());
		assertEquals("Phase_III", mafInfo.getSequencingPhase());
	}

	class TestableMaf2FileProcessor extends Maf2FileProcessor {

		private MafInfo mafInfo;

		TestableMaf2FileProcessor(final MafInfo mafInfo) {
			this.mafInfo = mafInfo;
		}

		protected MafInfo getMafInfo() {
			return mafInfo;
		}
	}

}
