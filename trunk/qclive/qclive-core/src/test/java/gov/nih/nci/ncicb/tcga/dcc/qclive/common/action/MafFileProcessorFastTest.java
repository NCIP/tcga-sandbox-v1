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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.MafInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.MafInfoQueries;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import java.io.File;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Test class for MafFileProcessor
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MafFileProcessorFastTest {

	private Mockery context = new JUnit4Mockery();
	private FileInfoQueries mockFileInfoQueries = context.mock(FileInfoQueries.class);
	private MafInfoQueries mockMafInfoQueries = context.mock(MafInfoQueries.class);
	private BCRDataService bcrDataService = context.mock(BCRDataService.class);
	private QcContext qcContext;
	private Tumor theTumor;
	private MafInfo mafInfo;
	private MafFileProcessor processor;
	private List<Integer> bcrIdList;
	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	@Before
	public void setup() {
		final Archive archive = new Archive();
		archive.setId(1L);
		archive.setArchiveType(Archive.TYPE_LEVEL_2);
		archive.setExperimentType(Experiment.TYPE_GSC);
		theTumor = new Tumor();
		archive.setTheTumor(theTumor);

		Map<String, Long> fileNameToIdList = new HashMap<String, Long>();
		fileNameToIdList.put("file1", 1L);
		fileNameToIdList.put("file2", 2L);
		archive.setFilenameToIdMap(fileNameToIdList);

		Center center = new Center();
		center.setCenterId(10);
		archive.setTheCenter(center);
		qcContext = new QcContext();
		qcContext.setArchive(archive);

		mafInfo = new MafInfo();
		processor = new TestableMafFileProcessor(mafInfo);
		processor.setFileInfoQueries(mockFileInfoQueries);
		processor.setMafInfoQueries(mockMafInfoQueries);
		processor.setBcrDataService(bcrDataService);

		bcrIdList = new ArrayList<Integer>();
		bcrIdList.add(1);
	}

    @Test
    public void testMafWithUUIDS() throws ParseException, Processor.ProcessorException {
        final File mafFile = new File(SAMPLE_DIR + "qclive/mafFileValidator/good/mafWithUUIDHeader.maf");
        // TCGA-00-0000-03A-00B-0000-00	TCGA-00-0000-19C-00D-0000-00

        final BCRID bcrId1 = new BCRID();
        bcrId1.setFullID("TCGA-00-0000-03A-00B-0000-00");
        final BCRID bcrId2 = new BCRID();
        bcrId2.setFullID("TCGA-00-0000-19C-00D-0000-00");

        qcContext.getArchive().getFilenameToIdToMap().put("mafWithUUIDHeader.maf", 5L);
        qcContext.setCenterConvertedToUUID(true);
        context.checking(new Expectations() {
			{
				one(mockFileInfoQueries).getFileId("mafWithUUIDHeader.maf", 1L);
				will(returnValue(5L));

                one(mockMafInfoQueries).fileIdExistsInMafInfo(5L);
                will(returnValue(false));

                one(bcrDataService).getShippedBiospecimenIds(Arrays.asList("2e276db0-a903-46c0-8892-620fc0e94de8", "2e276db0-a903-46c0-8892-620fc0e94de6"));
                will(returnValue(Arrays.asList(100L, 101L)));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-03A-00B-0000-00");
                will(returnValue(bcrId1));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-19C-00D-0000-00");
                will(returnValue(bcrId2));

                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(5L));
                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));

				one(mockMafInfoQueries).addMaf(mafInfo); // 1 line in maf file

			}
		});
		processor.execute(mafFile, qcContext);
		assertEquals(0, qcContext.getErrorCount());
		assertEquals(0, qcContext.getWarningCount());
    }

	@Test
	public void testMafWithBarcodes() throws Processor.ProcessorException, UUIDException,ParseException {
		File mafFile = new File(SAMPLE_DIR + "qclive/mafFileValidator/good/test.maf");
        final BCRID bcrId1 = new BCRID();
        bcrId1.setFullID("TCGA-00-0000-03A-00B-0000-00");
        final BCRID bcrId2 = new BCRID();
        bcrId2.setFullID("TCGA-00-0000-19C-00D-0000-00");

        qcContext.getArchive().getFilenameToIdToMap().put("test.maf", 5L);
        qcContext.setCenterConvertedToUUID(false);
		context.checking(new Expectations() {
            {
                one(mockFileInfoQueries).getFileId("test.maf", 1L);
                will(returnValue(5L));

                one(mockMafInfoQueries).fileIdExistsInMafInfo(5L);
                will(returnValue(false));

                one(bcrDataService).getBiospecimenIds(Arrays.asList("TCGA-00-0000-03A-00B-0000-00", "TCGA-00-0000-19C-00D-0000-00"));
                will(returnValue(Arrays.asList(200, 201)));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-03A-00B-0000-00");
                will(returnValue(bcrId1));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-19C-00D-0000-00");
                will(returnValue(bcrId2));

                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));
                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(5L));

                exactly(3).of(mockMafInfoQueries).addMaf(mafInfo); // 3 lines in maf file
            }
        });
		processor.execute(mafFile, qcContext);
		assertEquals(0, qcContext.getErrorCount());
		assertEquals(0, qcContext.getWarningCount());
		assertEquals(new Integer(10), mafInfo.getCenterID()); // make sure was
																// set from
																// archive, not
																// from file!
		assertEquals(new Long(5), mafInfo.getFileID());
		assertEquals("AAA", mafInfo.getHugoSymbol());
		assertEquals(new Integer(123), mafInfo.getEntrezGeneID());
		assertEquals("36.1", mafInfo.getNcbiBuild());
		assertEquals("X", mafInfo.getChromosome());
		assertEquals(new Integer(1), mafInfo.getStartPosition());
		assertEquals(new Integer(4), mafInfo.getEndPosition());
		assertEquals("+", mafInfo.getStrand());
		assertEquals("Missense_Mutation", mafInfo.getVariantClassification());
		assertEquals("Del", mafInfo.getVariantType());
		assertEquals("TCGA", mafInfo.getReferenceAllele());
		assertEquals("TCG-A", mafInfo.getTumorSeqAllele1());
		assertEquals("AGCT", mafInfo.getTumorSeqAllele2());
		assertEquals("dnsnp", mafInfo.getDbsnpRS());
		assertEquals("unknown", mafInfo.getDbSNPValStatus());
		assertEquals("TCGA-00-0000-03A-00B-0000-00", mafInfo.getTumorSampleBarcode());
		assertEquals("TCGA-00-0000-19C-00D-0000-00", mafInfo.getMatchNormalSampleBarcode());
		assertEquals("TCG-A", mafInfo.getMatchNormSeqAllele1());
		assertEquals("AGCT", mafInfo.getMatchNormSeqAllele2());
		assertEquals("TCG-A", mafInfo.getTumorValidationAllele1());
		assertEquals("AGCT", mafInfo.getTumorValidationAllele2());
		assertEquals("TCGA", mafInfo.getMatchNormValidationAllele1());
		assertEquals("TCGA", mafInfo.getMatchNormValidationAllele2());
		assertEquals("Unknown", mafInfo.getVerificationStatus());
		assertEquals("Unknown", mafInfo.getValidationStatus());
		assertEquals("Somatic", mafInfo.getMutationStatus());
	}

    @Test
    public void testMafSameFileTwoArchives() throws Processor.ProcessorException, ParseException {
        File mafFile1 = new File(SAMPLE_DIR + "qclive/mafFileValidator/good/test.maf");
        File mafFile2 = new File(SAMPLE_DIR + "qclive/mafFileValidator/good/test.maf");
        final BCRID bcrId1 = new BCRID();
        bcrId1.setFullID("TCGA-00-0000-03A-00B-0000-00");
        final BCRID bcrId2 = new BCRID();
        bcrId2.setFullID("TCGA-00-0000-19C-00D-0000-00");

        qcContext.getArchive().getFilenameToIdToMap().put("test.maf", 5L);
        qcContext.setCenterConvertedToUUID(false);
        context.checking(new Expectations() {
            {
                one(mockFileInfoQueries).getFileId("test.maf", 1L);
                will(returnValue(5L));

                one(mockMafInfoQueries).fileIdExistsInMafInfo(5L);
                will(returnValue(false));

                one(bcrDataService).getBiospecimenIds(Arrays.asList("TCGA-00-0000-03A-00B-0000-00", "TCGA-00-0000-19C-00D-0000-00"));
                will(returnValue(Arrays.asList(200, 201)));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-03A-00B-0000-00");
                will(returnValue(bcrId1));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-19C-00D-0000-00");
                will(returnValue(bcrId2));

                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));
                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(5L));

                exactly(3).of(mockMafInfoQueries).addMaf(mafInfo); // 3 lines in maf file
            }
        });
        processor.execute(mafFile1, qcContext);
        context.checking(new Expectations() {
            {
                one(mockFileInfoQueries).getFileId("test.maf", 1L);
                will(returnValue(99L));

                one(mockMafInfoQueries).fileIdExistsInMafInfo(99L);
                will(returnValue(true));

                FileInfo fi = new FileInfo();
                one(mockFileInfoQueries).getFileForFileId(99L);
                will(returnValue(fi));

                Archive a = new Archive();
                one(mockFileInfoQueries).getLatestArchiveContainingFile(fi);
                will(returnValue(a));

                exactly(0).of(mockMafInfoQueries).addMaf(mafInfo); // 3 lines in maf file
            }
        });
        processor.execute(mafFile2, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testMafDeleteExistingMafInfoIfBadPreviousInsert() throws Processor.ProcessorException, ParseException {
        File mafFile = new File(SAMPLE_DIR + "qclive/mafFileValidator/good/test.maf");
        final BCRID bcrId1 = new BCRID();
        bcrId1.setFullID("TCGA-00-0000-03A-00B-0000-00");
        final BCRID bcrId2 = new BCRID();
        bcrId2.setFullID("TCGA-00-0000-19C-00D-0000-00");

        qcContext.getArchive().getFilenameToIdToMap().put("test.maf", 5L);
        qcContext.setCenterConvertedToUUID(false);
        context.checking(new Expectations() {
            {
                one(mockFileInfoQueries).getFileId("test.maf", 1L);
                will(returnValue(5L));

                one(mockMafInfoQueries).fileIdExistsInMafInfo(5L);
                will(returnValue(true));

                one(mockFileInfoQueries).getFileForFileId(5L);
                will(returnValue(null));

                one(mockMafInfoQueries).deleteMafInfoForFileId(5L);

                one(bcrDataService).getBiospecimenIds(Arrays.asList("TCGA-00-0000-03A-00B-0000-00", "TCGA-00-0000-19C-00D-0000-00"));
                will(returnValue(Arrays.asList(200, 201)));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-03A-00B-0000-00");
                will(returnValue(bcrId1));

                one(bcrDataService).parseAliquotBarcode("TCGA-00-0000-19C-00D-0000-00");
                will(returnValue(bcrId2));

                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));
                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(5L));

                exactly(3).of(mockMafInfoQueries).addMaf(mafInfo); // 3 lines in maf file
            }
        });
        processor.execute(mafFile, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    private static Matcher<List<BCRID>> expectedBcrIds(final String... barcodes) {
        return new TypeSafeMatcher<List<BCRID>>() {
            @Override
            public boolean matchesSafely(final List<BCRID> bcrIds) {
                for(int i=0; i<barcodes.length; i++) {
                    assertEquals(barcodes[i], bcrIds.get(i).getFullID());
                }
                return true;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("bcrids have expected barcodes");
            }
        };
    }

	class TestableMafFileProcessor extends MafFileProcessor {

		private MafInfo mafInfo;

		TestableMafFileProcessor(final MafInfo mafInfo) {
			this.mafInfo = mafInfo;
		}

		protected MafInfo getMafInfo() {
			return mafInfo;
		}
	}
}
