package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMutation;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetMutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Slow test for DAMQueriesMutation
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesMutationNewSlowTest extends DBUnitTestCase {
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FILE = "portal/MafDataSetInput.xml";

    private File publicLevel2ExpectedFile = new File(TEST_DATA_FOLDER + "portal/dao/mutation/tempFileDirectory/broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_level2.maf");
    private File publicLevel3ExpectedFiles = new File(TEST_DATA_FOLDER + "portal/dao/mutation/tempFileDirectory/broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_level3.maf");
    private File protectedLevel2ExpectedFile = new File(TEST_DATA_FOLDER + "portal/dao/mutation/tempFileDirectory/broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_-_Controlled_level2.maf");
    private File protectedLevel3ExpectedFile = new File(TEST_DATA_FOLDER + "portal/dao/mutation/tempFileDirectory/broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_-_Controlled_level3.maf");


    private DAMQueriesMutation damQueriesMutation;

    public DAMQueriesMutationNewSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);

        damQueriesMutation = new DAMQueriesMutation() {
            @Override
            protected String makeTempFilename(final DataFileMutation dfm) {
                // use a fixed name for the temp file rather than a UUID so we can find the file
                return dfm.getFileName();
            }
        };
        damQueriesMutation.setDataSource(getDataSource());
        damQueriesMutation.setTempfileDirectory(TEST_DATA_FOLDER + "portal/dao/mutation/tempFileDirectory/");
    }

    public void tearDown() throws Exception {
        super.tearDown();
        publicLevel2ExpectedFile.deleteOnExit();
        publicLevel3ExpectedFiles.deleteOnExit();
        protectedLevel2ExpectedFile.deleteOnExit();
        protectedLevel3ExpectedFile.deleteOnExit();
    }

    public void testGetDataSetsForDisease() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> dataSets = damQueriesMutation.getDataSetsForDiseaseType("TEST");
        // there are 4 maf records, expect a data set for normal and for tumor
        assertEquals(8, dataSets.size());

        int foundCount = 0;
        for (final DataSet dataSet : dataSets) {
            if (dataSet.isProtected() && dataSet.getLevel().equals("3")) {
                assertEquals("11", dataSet.getPlatformTypeId());

                assertEquals("TCGA-00-0004-01A-11W-A019-09", ((DataSetMutation) dataSet).getMutationBarcode());
                assertEquals("TCGA-00-0004-10A-11W-A019-09", ((DataSetMutation) dataSet).getMatchedNormalBarcode());
                foundCount++;

                if (dataSet.getSample().equals("TCGA-00-0004-01")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, dataSet.getTumorNormal());
                } else if (dataSet.getSample().equals("TCGA-00-0004-10")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR, dataSet.getTumorNormal());
                } else {
                    fail("Unexpected data set");
                }

            } else if (dataSet.isProtected() && dataSet.getLevel().equals("2")) {
                assertEquals("11", dataSet.getPlatformTypeId());

                assertEquals("TCGA-00-0002-01A-11W-A019-09", ((DataSetMutation) dataSet).getMutationBarcode());
                assertEquals("TCGA-00-0002-10A-11W-A019-09", ((DataSetMutation) dataSet).getMatchedNormalBarcode());
                foundCount++;

                if (dataSet.getSample().equals("TCGA-00-0002-01")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, dataSet.getTumorNormal());

                } else if (dataSet.getSample().equals("TCGA-00-0002-10")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR, dataSet.getTumorNormal());
                } else {
                    fail("Unexpected data set");
                }

            } else if (!dataSet.isProtected() && dataSet.getLevel().equals("3")) {
                assertEquals("10", dataSet.getPlatformTypeId());

                assertEquals("TCGA-00-0003-01A-11W-A019-09", ((DataSetMutation) dataSet).getMutationBarcode());
                assertEquals("TCGA-00-0003-10A-11W-A019-09", ((DataSetMutation) dataSet).getMatchedNormalBarcode());
                foundCount++;

                if (dataSet.getSample().equals("TCGA-00-0003-01")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, dataSet.getTumorNormal());
                } else if (dataSet.getSample().equals("TCGA-00-0003-10")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR, dataSet.getTumorNormal());
                } else {
                    fail("Unexpected data set");
                }

            } else if (!dataSet.isProtected() && dataSet.getLevel().equals("2")) {
                assertEquals("10", dataSet.getPlatformTypeId());

                assertEquals("TCGA-00-0001-01A-11W-A019-09", ((DataSetMutation) dataSet).getMutationBarcode());
                assertEquals("TCGA-00-0001-10A-11W-A019-09", ((DataSetMutation) dataSet).getMatchedNormalBarcode());
                foundCount++;

                if (dataSet.getSample().equals("TCGA-00-0001-01")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL, dataSet.getTumorNormal());
                } else if (dataSet.getSample().equals("TCGA-00-0001-10")) {
                    assertEquals(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR, dataSet.getTumorNormal());
                } else {
                    fail("Unexpected data set");
                }
            }
        }
        assertEquals(dataSets.size(), foundCount);
    }

    public void testGetDataSetsForControl() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> controlDataSets = damQueriesMutation.getDataSetsForControls(Arrays.asList("TEST"));
        // 1 maf record for a control biospecimen
        assertEquals(1, controlDataSets.size());
        assertFalse(controlDataSets.get(0).isProtected());
        assertEquals("2", controlDataSets.get(0).getLevel());
        assertEquals("10", controlDataSets.get(0).getPlatformTypeId());
        assertEquals("TCGA-AV-0007-20A-11W-A019-09", ((DataSetMutation) controlDataSets.get(0)).getMatchedNormalBarcode());
        assertEquals("TCGA-AV-0007-20A-11W-A019-09", ((DataSetMutation) controlDataSets.get(0)).getMutationBarcode());
    }

    public void testGetDataFiles() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> dataFiles =
                damQueriesMutation.getFileInfoForSelectedDataSets(damQueriesMutation.getDataSetsForDiseaseType("TEST"),
                        true);

        assertEquals(4, dataFiles.size());

        checkDataFile(dataFiles.get(0), Arrays.asList("TCGA-00-0001-01A-11W-A019-09"), "10", "selected_samples",
                "broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_level2.maf", "2", "40", "10", 175, "TEST", false);
        checkDataFile(dataFiles.get(1), Arrays.asList("TCGA-00-0003-01A-11W-A019-09"), "10", "selected_samples",
                "broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_level3.maf", "3", "40", "10", 175, "TEST", false);
        checkDataFile(dataFiles.get(2), Arrays.asList("TCGA-00-0002-01A-11W-A019-09"), "10", "selected_samples",
                "broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_-_Controlled_level2.maf", "2", "41", "11",
                175, "TEST", true);
        checkDataFile(dataFiles.get(3), Arrays.asList("TCGA-00-0004-01A-11W-A019-09"), "10", "selected_samples",
                "broad.mit.edu__Illumina_Genome_Analyzer_DNA_Sequencing_-_Controlled_level3.maf", "3", "41", "11", 175,
                "TEST", true);
    }

    private void checkDataFile(final DataFile dataFile, final List<String> barcodeList, final String centerId,
                               final String displaySample, final String filename,
                               final String level, final String platformId, final String platformTypeId,
                               final long size, final String disease, final boolean isProtected) {
        assertEquals(barcodeList.toString(), dataFile.getBarcodes().toString());
        assertEquals(centerId, dataFile.getCenterId());
        assertEquals(displaySample, dataFile.getDisplaySample());
        assertEquals(filename, dataFile.getFileName());
        assertEquals(level, dataFile.getLevel());
        assertEquals(platformId, dataFile.getPlatformId());
        assertEquals(platformTypeId, dataFile.getPlatformTypeId());
        assertEquals(size, dataFile.getSize());
        assertEquals(disease, dataFile.getDiseaseType());
        assertEquals(isProtected, dataFile.isProtected());
    }

    public void testWriteFiles() throws DataAccessMatrixQueries.DAMQueriesException, IOException {
        final List<DataFile> dataFiles =
                damQueriesMutation.getFileInfoForSelectedDataSets(damQueriesMutation.getDataSetsForDiseaseType("TEST"),
                        true);
        damQueriesMutation.addPathsToSelectedFiles(dataFiles);

        checkFile(publicLevel2ExpectedFile, "A2M\t0\tbroad.mit.edu\t36\t12\t9123535\t9123535\t+\tMissense_Mutation\t" +
                "SNP\tT\tC\tC\trs669\tUnknown\tTCGA-00-0001-01A-11W-A019-09\tTCGA-00-0001-10A-11W-A019-09\tC\t" +
                "C\t\t\t\t\t\tUnknown\tSomatic\tPhase I\tCapture\tSequenom\t\t\tIllumina GAIIx\tuuid1\tuuid2\t" +
                "level_2.somatic.maf\tbroad.mit.edu_TEST.IlluminaGA_DNASeq.Level_2.1.1.0\t3");

        checkFile(publicLevel3ExpectedFiles, "A2M\t0\tbroad.mit.edu\t36\t12\t9123535\t9123535\t+\tMissense_Mutation\t" +
                "SNP\tT\tC\tC\trs669\tUnknown\tTCGA-00-0003-01A-11W-A019-09\tTCGA-00-0003-10A-11W-A019-09\tC\t" +
                "C\t\t\t\t\t\tValid\tSomatic\tPhase I\tCapture\tSequenom\t\t\tIllumina GAIIx\tuuid3\tuuid4\t" +
                "level_3.somatic.maf\tbroad.mit.edu_TEST.IlluminaGA_DNASeq.Level_3.1.0.0\t3");

        checkFile(protectedLevel2ExpectedFile, "A2M\t0\tbroad.mit.edu\t36\t12\t9123535\t9123535\t+\tMissense_Mutation\t" +
                "SNP\tT\tC\tC\trs669\tUnknown\tTCGA-00-0002-01A-11W-A019-09\tTCGA-00-0002-10A-11W-A019-09\tC\t" +
                "C\t\t\t\t\t\tUnknown\tGermline\tPhase I\tCapture\tSequenom\t\t\tIllumina GAIIx\tuuid3\tuuid4\t" +
                "level_2.protected.maf\tbroad.mit.edu_TEST.IlluminaGA_DNASeq_Cont.Level_2.1.0.0\t3");

        checkFile(protectedLevel3ExpectedFile, "A2M\t0\tbroad.mit.edu\t36\t12\t9123535\t9123535\t+\tMissense_Mutation\t" +
                "SNP\tT\tC\tC\trs669\tUnknown\tTCGA-00-0004-01A-11W-A019-09\tTCGA-00-0004-10A-11W-A019-09\tC\t" +
                "C\t\t\t\t\t\tValid\tGermline\tPhase I\tCapture\tSequenom\t\t\tIllumina GAIIx\tuuid3\tuuid4\t" +
                "level_3.protected.maf\tbroad.mit.edu_TEST.IlluminaGA_DNASeq_Cont.Level_3.1.0.0\t3");
    }

    private void checkFile(final File file, final String expectedDataLine) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        final String header = bufferedReader.readLine();
        assertEquals("Hugo_Symbol\tEntrez_Gene_Id\tCenter\tNcbi_Build\tChrom\tStart_Position\tEnd_Position\tStrand\t" +
                "Variant_Classification\tVariant_Type\tReference_Allele\tTumor_Seq_Allele1\tTumor_Seq_Allele2\t" +
                "Dbsnp_Rs\tDbsnp_Val_Status\tTumor_Sample_Barcode\tMatch_Norm_Sample_Barcode\tMatch_Norm_Seq_Allele1\t" +
                "Match_Norm_Seq_Allele2\tTumor_Validation_Allele1\tTumor_Validation_Allele2\t" +
                "Match_Norm_Validation_Allele1\tMatch_Norm_Validation_Allele2\tVerification_Status\tValidation_Status\t" +
                "Mutation_Status\tSequencing_Phase\tSequence_Source\tValidation_Method\tScore\tBam_File\tSequencer\t" +
                "Tumor_Sample_UUID\tMatch_Norm_Sample_UUID\tFile_Name\tArchive_Name\tLine_Number",
                header);
        final String dataLine = bufferedReader.readLine();
        assertEquals(expectedDataLine, dataLine);
        assertNull(bufferedReader.readLine());
    }
}
