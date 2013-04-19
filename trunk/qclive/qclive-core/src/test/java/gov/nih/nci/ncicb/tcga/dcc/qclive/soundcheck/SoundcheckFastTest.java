/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractListProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractMafFileVersionDispatcher;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveExpander;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.MageTabExperimentChecker;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ArchiveNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ArraySdrfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.BiospecimenXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.DNASeqSdrfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.GscIdfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MageTabExperimentValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ClinicalXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ControlArchiveValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ExperimentValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.GscExperimentValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.GccIdfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.Maf2FileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MafFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MafFileValidatorDispatcher;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MiRNASeqFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MiRNASeqIsoformFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqExonFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqGeneFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqJunctionFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMGeneNormalizedFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMGeneResultsFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMIsoformFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMIsoformNormalizedFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.SdrfValidatorDispatcher;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.TraceFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.VcfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote.RemoteDomainNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote.RemotePlatformValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote.RemoteTumorTypeValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.LoggerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DateComparator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfHeaderDefinitionStore;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfHeaderDefinitionStorePropertyFileImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.RemoteCenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl.BiospecimenIdWsQueriesImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl.ValidationWebServiceQueriesImpl;
import gov.nih.nci.system.applicationservice.ApplicationException;
import org.apache.log4j.Level;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for Soundcheck
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class SoundcheckFastTest {

    private static final String SAMPLE_DIR = Thread.currentThread().getContextClassLoader()
            .getResource("samples").getPath() + File.separator;
    private static final String ARCHIVE_DIRECTORY = SAMPLE_DIR + "qclive" + File.separator + "soundcheck" + File.separator + "tarAndTarGz" + File.separator;
    private static final String TEST_DIR = SAMPLE_DIR + "qclive" + File.separator + "mafFileValidator" + File.separator;

    private Mockery mockery = new JUnit4Mockery();
    private Soundcheck soundcheck;
    private RemoteValidationHelper mockRemoteValidationHelper = mockery.mock(RemoteValidationHelper.class);
    private Logger mockLogger;

    @Before
    public void setup() {
        soundcheck = new Soundcheck();
        soundcheck.setVerbose(false);
        QcContext context = new QcContext();
        context.setNoRemote(true);
        context.setStandaloneValidator(true);
        // logger with no destinations will not actually log anything
        Logger logger = new LoggerImpl();
        context.setLogger(logger);
        soundcheck.setLogger(logger);
        soundcheck.setQcContext(context);
        Soundcheck.setUseRemoteValidation(false);

        mockLogger = mockery.mock(Logger.class);
    }

    @Test
    public void testMakeGdacValidator() throws NoSuchFieldException, IllegalAccessException {
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);
        final ExperimentValidator gdacValidator = soundcheck.makeValidator(Experiment.TYPE_GDAC, false);
        assertNotNull(gdacValidator);
        List<Processor<Archive, Boolean>> listValidators = getListProcessors(gdacValidator);
        assertEquals(1, listValidators.size());
        assertTrue(listValidators.get(0) instanceof ManifestValidator);
    }

    @Test
    public void testMakeCgccValidatorWhenNoRemote() throws NoSuchFieldException, IllegalAccessException {

        soundcheck.setRemoteValidationHelper(null); // -noremote does not set the remoteValidationHelper

        final ExperimentValidator cgccExperimentValidator = soundcheck.makeValidator(Experiment.TYPE_CGCC, false);
        assertNotNull(cgccExperimentValidator);

        final List<Processor<Experiment, Boolean>> inputValidators = getInputValidators(cgccExperimentValidator);
        assertNotNull(inputValidators);

        for (final Processor<Experiment, Boolean> inputValidator : inputValidators) {
            if (inputValidator instanceof MageTabExperimentValidator) {
                assertNull(((MageTabExperimentValidator) inputValidator).getExperimentQueries());
            }
        }

        final List<Processor<Archive, Boolean>> processors = getListProcessors(cgccExperimentValidator);
        assertNotNull(processors);

        for(final Processor<Archive, Boolean> processor : processors) {

            if(processor instanceof VcfValidator) {

                final VcfValidator vcfValidator = (VcfValidator) processor;
                assertNotNull(vcfValidator);

                final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator = vcfValidator.getQcLiveBarcodeAndUUIDValidator();
                assertNotNull(qcLiveBarcodeAndUUIDValidator);

                final String validUuid = "69de087d-e31d-4ff5-a760-6be8da96b6e2";
                final String invalidUuid = "invalid-uuid";

                assertFalse(qcLiveBarcodeAndUUIDValidator.validateUUIDFormat(invalidUuid));
                assertTrue(qcLiveBarcodeAndUUIDValidator.validateUUIDFormat(validUuid));

                final String invalidBarcode = "invalid-barcode";
                final String validBarcode = "TCGA-00-0000-00A-00A-0000-00";

                assertNotNull(qcLiveBarcodeAndUUIDValidator.validateBarcodeFormat(invalidBarcode, "file.txt", "Aliquot"));
                assertNull(qcLiveBarcodeAndUUIDValidator.validateBarcodeFormat(validBarcode, "file.txt", "Aliquot"));

                assertFalse(qcLiveBarcodeAndUUIDValidator.validateUuid(invalidUuid, soundcheck.getQcContext(), "file.txt", false));
                assertTrue(qcLiveBarcodeAndUUIDValidator.validateUuid(validUuid, soundcheck.getQcContext(), "file.txt", false));
            }
        }
    }

    @Test
    public void testMakeCgccValidator() throws NoSuchFieldException, IllegalAccessException {
    	soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);
        final ExperimentValidator cgccExperimentValidator = soundcheck.makeValidator(Experiment.TYPE_CGCC, false);
        assertNotNull(cgccExperimentValidator);

        final List<Processor<Experiment, Boolean>> inputValidators = getInputValidators(cgccExperimentValidator);
        assertEquals(inputValidators.size(), 2);
        assertTrue(inputValidators.get(0) instanceof MageTabExperimentChecker);
        assertTrue(inputValidators.get(1) instanceof MageTabExperimentValidator);

        final MageTabExperimentChecker mageTabExperimentChecker = (MageTabExperimentChecker) inputValidators.get(0);
        final MageTabExperimentValidator mageTabExperimentValidator = (MageTabExperimentValidator) inputValidators.get(1);

        assertNull(mageTabExperimentChecker.getArchiveQueries());
        assertNotNull(mageTabExperimentChecker.getManifestParser());
        assertNotNull(mageTabExperimentChecker.getCenterQueries());

        assertNotNull(mageTabExperimentValidator.getManifestParser());
        assertNotNull(mageTabExperimentValidator.getExperimentQueries());
        assertNotNull(mageTabExperimentValidator.getMatrixParser());
        assertNotNull(mageTabExperimentValidator.getMatrixValidator());
        assertTrue(mageTabExperimentValidator.isRemote());

        final List<Processor<Archive, Boolean>> listProcessors = getListProcessors(cgccExperimentValidator);

        assertNotNull(listProcessors);
        assertEquals(13, listProcessors.size());

        final Object manifestValidatorObject = listProcessors.get(0);
        final Object sdrfValidatorDispatcherObject = listProcessors.get(1);
        final Object idfValidatorObject = listProcessors.get(2);
        final Object rnaSeqGeneFileValidatorObject = listProcessors.get(3);
        final Object rnaSeqExonFileValidatorObject = listProcessors.get(4);
        final Object rnaSeqJunctionFileValidatorObject = listProcessors.get(5);
        final Object rnaSeqRSEMGeneNormalizedFileValidatorObject = listProcessors.get(6);
        final Object rnaSeqRSEMGeneResultsFileValidatorObject = listProcessors.get(7);
        final Object rnaSeqRSEMIsoformNormalizedFileValidatorObject = listProcessors.get(8);
        final Object rnaSeqRSEMIsoformFileValidatorObject = listProcessors.get(9);
        final Object miRNASeqFileValidatorObject = listProcessors.get(10);
        final Object miRNASeqIsoformFileValidatorObject = listProcessors.get(11);
        final Object vcfValidatorObject = listProcessors.get(12);

        assertTrue(manifestValidatorObject instanceof ManifestValidator);

        assertTrue(sdrfValidatorDispatcherObject instanceof SdrfValidatorDispatcher);
        final SdrfValidatorDispatcher sdrfValidatorDispatcher = (SdrfValidatorDispatcher) sdrfValidatorDispatcherObject;

        assertTrue(idfValidatorObject instanceof GccIdfValidator);

        assertTrue(rnaSeqGeneFileValidatorObject instanceof RNASeqGeneFileValidator);
        final RNASeqGeneFileValidator rnaSeqGeneFileValidator = (RNASeqGeneFileValidator) rnaSeqGeneFileValidatorObject;
        assertNotNull(rnaSeqGeneFileValidator.getChromInfoUtils());
        assertNotNull(rnaSeqGeneFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(rnaSeqGeneFileValidator.getBarcodeTumorValidator());
        assertNotNull(rnaSeqGeneFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(rnaSeqExonFileValidatorObject instanceof RNASeqExonFileValidator);
        final RNASeqExonFileValidator rnaSeqExonFileValidator = (RNASeqExonFileValidator) rnaSeqExonFileValidatorObject;
        assertNotNull(rnaSeqExonFileValidator.getChromInfoUtils());
        assertNotNull(rnaSeqExonFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(rnaSeqExonFileValidator.getBarcodeTumorValidator());
        assertNotNull(rnaSeqExonFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(rnaSeqJunctionFileValidatorObject instanceof RNASeqJunctionFileValidator);
        final RNASeqJunctionFileValidator rnaSeqJunctionFileValidator = (RNASeqJunctionFileValidator) rnaSeqJunctionFileValidatorObject;
        assertNotNull(rnaSeqJunctionFileValidator.getChromInfoUtils());
        assertNotNull(rnaSeqJunctionFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(rnaSeqJunctionFileValidator.getBarcodeTumorValidator());
        assertNotNull(rnaSeqJunctionFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(rnaSeqRSEMGeneNormalizedFileValidatorObject instanceof RNASeqRSEMGeneNormalizedFileValidator);
        final RNASeqRSEMGeneNormalizedFileValidator rnaSeqRSEMGeneNormalizedFileValidator = (RNASeqRSEMGeneNormalizedFileValidator) rnaSeqRSEMGeneNormalizedFileValidatorObject;
        assertNotNull(rnaSeqRSEMGeneNormalizedFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(rnaSeqRSEMGeneNormalizedFileValidator.getBarcodeTumorValidator());
        assertNotNull(rnaSeqRSEMGeneNormalizedFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(rnaSeqRSEMGeneResultsFileValidatorObject instanceof RNASeqRSEMGeneResultsFileValidator);
        final RNASeqRSEMGeneResultsFileValidator rnaSeqRSEMGeneResultsFileValidator = (RNASeqRSEMGeneResultsFileValidator) rnaSeqRSEMGeneResultsFileValidatorObject;
        assertNotNull(rnaSeqRSEMGeneResultsFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(rnaSeqRSEMGeneResultsFileValidator.getBarcodeTumorValidator());
        assertNotNull(rnaSeqRSEMGeneResultsFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(rnaSeqRSEMIsoformNormalizedFileValidatorObject instanceof RNASeqRSEMIsoformNormalizedFileValidator);
        final RNASeqRSEMIsoformNormalizedFileValidator rnaSeqRSEMIsoformNormalizedFileValidator = (RNASeqRSEMIsoformNormalizedFileValidator) rnaSeqRSEMIsoformNormalizedFileValidatorObject;
        assertNotNull(rnaSeqRSEMIsoformNormalizedFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(rnaSeqRSEMIsoformNormalizedFileValidator.getBarcodeTumorValidator());
        assertNotNull(rnaSeqRSEMIsoformNormalizedFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(rnaSeqRSEMIsoformFileValidatorObject instanceof RNASeqRSEMIsoformFileValidator);
        final RNASeqRSEMIsoformFileValidator rnaSeqRSEMIsoformFileValidator = (RNASeqRSEMIsoformFileValidator) rnaSeqRSEMIsoformFileValidatorObject;
        assertNotNull(rnaSeqRSEMIsoformFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(rnaSeqRSEMIsoformFileValidator.getBarcodeTumorValidator());
        assertNotNull(rnaSeqRSEMIsoformFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(miRNASeqFileValidatorObject instanceof MiRNASeqFileValidator);
        final MiRNASeqFileValidator miRNASeqFileValidator = (MiRNASeqFileValidator) miRNASeqFileValidatorObject;
        assertNotNull(miRNASeqFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(miRNASeqFileValidator.getBarcodeTumorValidator());
        assertNotNull(miRNASeqFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(miRNASeqIsoformFileValidatorObject instanceof MiRNASeqIsoformFileValidator);
        final MiRNASeqIsoformFileValidator miRNASeqIsoformFileValidator = (MiRNASeqIsoformFileValidator) miRNASeqIsoformFileValidatorObject;
        assertNotNull(miRNASeqIsoformFileValidator.getQcLiveBarcodeAndUUIDValidator());
        assertNotNull(miRNASeqIsoformFileValidator.getBarcodeTumorValidator());
        assertNotNull(miRNASeqIsoformFileValidator.getSeqDataFileValidationErrorMessagePropertyType());

        assertTrue(vcfValidatorObject instanceof VcfValidator);
        final VcfValidator vcfValidator = (VcfValidator) vcfValidatorObject;

        // Check VCF version
        assertNotNull(vcfValidator);
        assertEquals("1.1", vcfValidator.getTcgaVcfVersion());

        final VcfHeaderDefinitionStore headerDefinitionStore = ((TcgaVcfFileHeaderValidator) vcfValidator.getVcfFileHeaderValidator()).getVcfHeaderDefinitionStore();
        assertNotNull(headerDefinitionStore);
        assertTrue(headerDefinitionStore instanceof VcfHeaderDefinitionStorePropertyFileImpl);

        // Check for one of the validators whether the barcode validator has been initialized (should be the same for all other validators that need it).
        assertNotNull(sdrfValidatorDispatcher);

        final ArraySdrfValidator arraySdrfValidator = ((ArraySdrfValidator) sdrfValidatorDispatcher.getArraySdrfValidator());
        assertNotNull(arraySdrfValidator);

        final QcLiveBarcodeAndUUIDValidatorImpl qcLiveBarcodeAndUUIDValidator = (QcLiveBarcodeAndUUIDValidatorImpl) arraySdrfValidator.getQcLiveBarcodeAndUUIDValidator();
        assertNotNull(qcLiveBarcodeAndUUIDValidator);

        final BiospecimenIdWsQueriesImpl biospecimenIdWsQueries = (BiospecimenIdWsQueriesImpl) qcLiveBarcodeAndUUIDValidator.getBiospecimenIdWsQueries();
        assertNotNull(biospecimenIdWsQueries);
        assertNotNull(biospecimenIdWsQueries.getBaseBiospecimenJsonWs());
        assertNotNull(biospecimenIdWsQueries.getClient());

        final ValidationWebServiceQueriesImpl validationWebServiceQueries = (ValidationWebServiceQueriesImpl) qcLiveBarcodeAndUUIDValidator.getValidationWebServiceQueries();
        assertNotNull(validationWebServiceQueries);
        assertNotNull(validationWebServiceQueries.getBaseValidationWebServiceURL());
        assertNotNull(validationWebServiceQueries.getRestfulWebserviceClient());
        assertNotNull(validationWebServiceQueries.getWebServiceBatchSize());
    }

    @Test
    public void testCheckDefaultMafValidator()
            throws NoSuchFieldException, IllegalAccessException, ProcessorException, ApplicationException{

        final String archiveFilename = TEST_DIR + "good/standalone.tar.gz";
        checkMafValidator(archiveFilename);
    }

    @Test
    public void testCheckMafValidator1Dot0()
            throws NoSuchFieldException, IllegalAccessException, ProcessorException, ApplicationException{

        final String archiveFilename = TEST_DIR + "mafV1_0/standalone/valid.tar.gz";
        checkMafValidator(archiveFilename);
    }

    @Test
    public void testCheckMafValidator2Dot4ValidSomatic()
            throws NoSuchFieldException, IllegalAccessException, ProcessorException, ApplicationException{

        soundcheck.getQcContext().setPlatformName("IlluminaGA_DNASeq");
        final String archiveFilename = TEST_DIR + "mafV2_4/standalone/valid/somatic.tar.gz";
        checkMafValidator(archiveFilename);
    }

    @Test
    public void testCheckMafValidator2Dot4ValidProtected()
            throws NoSuchFieldException, IllegalAccessException, ProcessorException, ApplicationException{

        soundcheck.getQcContext().setPlatformName("IlluminaGA_DNASeq_Cont");
        final String archiveFilename = TEST_DIR + "mafV2_4/standalone/valid/protectedMaf.tar.gz";
        checkMafValidator(archiveFilename);
    }

    @Test
    public void testCheckMafValidator2Dot3()
            throws NoSuchFieldException, IllegalAccessException, ProcessorException, ApplicationException{

        final String archiveFilename = TEST_DIR + "mafV2_3/standalone/valid.tar.gz";
        checkMafValidator(archiveFilename);
    }

    @Test
    public void testCheckRnaSeqMafValidator()
            throws NoSuchFieldException, IllegalAccessException, ProcessorException, ApplicationException {

        final String archiveFilename = TEST_DIR + "mafRnaSeqV1_0/standalone/valid.tar.gz";
        checkMafValidator(archiveFilename);
    }

    @Test
    public void testCheckMafValidator2Dot2Invalid()
            throws NoSuchFieldException, IllegalAccessException, ApplicationException {

        final String archiveFilename = TEST_DIR + "mafV2_4/standalone/invalid/mafV2_2.tar.gz";
        try {
            checkMafValidator(archiveFilename);
            fail("ProcessorException was not thrown.");

        } catch (final ProcessorException e) {
            final String expectedErrorMessage = "MAF spec version '2.2' is not supported";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testCheckMafValidatorInvalidVersionHeader()
            throws NoSuchFieldException, IllegalAccessException, ApplicationException {

        final String archiveFilename = TEST_DIR + "mafV2_4/standalone/invalid/mafVSquirrel.tar.gz";
        try {
            checkMafValidator(archiveFilename);
            fail("ProcessorException was not thrown.");

        } catch (final ProcessorException e) {
            final String expectedErrorMessage = "MAF spec version must be specified in the first line of the " +
                    "file with the format '#version X' where X is the version designation";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testCheckMafValidator2Dot4InvalidFilenameForSomaticMaf()
            throws NoSuchFieldException, IllegalAccessException, ApplicationException {

        soundcheck.getQcContext().setPlatformName("IlluminaGA_DNASeq");
        final String archiveFilename = TEST_DIR + "mafV2_4/standalone/invalid/invalidSomaticFileName.tar.gz";
        try {
            checkMafValidator(archiveFilename);
            fail("ProcessorException was not thrown.");

        } catch (final ProcessorException e) {
            final String expectedErrorMessage = "Failed processing maf file center_disease_platform_invalid.germ.somatic.maf. " +
                    "Somatic maf files must not have 'germ' or 'protected'  text in the filename.";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testCheckMafValidator2Dot4InvalidFilenameForProtectedMaf()
            throws NoSuchFieldException, IllegalAccessException, ApplicationException {

        soundcheck.getQcContext().setPlatformName("IlluminaGA_DNASeq_Cont");
        final String archiveFilename = TEST_DIR + "mafV2_4/standalone/invalid/invalidProtectedFileName.tar.gz";
        try {
            checkMafValidator(archiveFilename);
            fail("ProcessorException was not thrown.");

        } catch (final ProcessorException e) {
            final String expectedErrorMessage = "Failed processing maf file center_disease_platform_invalid.germ.somatic.protected.maf. " +
                    "Protected maf files must not have 'somatic' text in the filename.";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    /**
     * Checks that the MAF files in the given archive are valid.
     *
     *
     * @param archiveFilename the archive file name (the directory in which to find the MAF files appended with .tar.gz)
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws ProcessorException
     */
    private void checkMafValidator(final String archiveFilename)
            throws NoSuchFieldException, IllegalAccessException, ProcessorException {

        Soundcheck.setUseRemoteValidation(false);
        final ExperimentValidator gscExperimentValidator = soundcheck.makeValidator(Experiment.TYPE_GSC, false);
        assertNotNull(gscExperimentValidator);

        final List<Processor<Archive, Boolean>> listProcessors = getListProcessors(gscExperimentValidator);
        MafFileValidatorDispatcher mafDispatcher = (MafFileValidatorDispatcher)listProcessors.get(1);

        final Archive archive = new Archive(archiveFilename);
        archive.setExperimentType(Experiment.TYPE_GSC);
        final QcContext qcContext = soundcheck.getQcContext();
        qcContext.setArchive(archive);
        archive.setDeployLocation(archiveFilename);

        archive.setArchiveType(Archive.TYPE_LEVEL_2);
        qcContext.setCenterConvertedToUUID(true);
        final Boolean result = mafDispatcher.execute(archive, qcContext);

        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testMakeGscValidator() throws NoSuchFieldException, IllegalAccessException {

        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);
        final ExperimentValidator gscExperimentValidator = soundcheck.makeValidator(Experiment.TYPE_GSC, false);
        assertNotNull(gscExperimentValidator);

        final List<Processor<Archive, Boolean>> listProcessors = getListProcessors(gscExperimentValidator);

        assertNotNull(listProcessors);
        assertEquals(6, listProcessors.size());
        assertTrue(listProcessors.get(0) instanceof ManifestValidator);
        assertTrue(listProcessors.get(1) instanceof MafFileValidatorDispatcher);
        assertTrue(listProcessors.get(2) instanceof TraceFileValidator);
        assertTrue(listProcessors.get(3) instanceof VcfValidator);
        assertTrue(listProcessors.get(4) instanceof DNASeqSdrfValidator);
        assertTrue(listProcessors.get(5) instanceof GscIdfValidator);

        final List<Processor<Experiment, Boolean>> inputValidators = getInputValidators(gscExperimentValidator);

        assertNotNull(inputValidators);
        assertEquals(3, inputValidators.size());
        assertTrue(inputValidators.get(0) instanceof MageTabExperimentChecker);
        assertTrue(inputValidators.get(1) instanceof MageTabExperimentValidator);
        assertTrue(inputValidators.get(2) instanceof GscExperimentValidator);

        final MageTabExperimentChecker mageTabExperimentChecker = (MageTabExperimentChecker) inputValidators.get(0);
        final MageTabExperimentValidator mageTabExperimentValidator = (MageTabExperimentValidator) inputValidators.get(1);

        assertNull(mageTabExperimentChecker.getArchiveQueries());
        assertNotNull(mageTabExperimentChecker.getManifestParser());
        assertNotNull(mageTabExperimentChecker.getCenterQueries());

        assertNotNull(mageTabExperimentValidator.getManifestParser());
        assertNotNull(mageTabExperimentValidator.getExperimentQueries());
        assertNotNull(mageTabExperimentValidator.getMatrixParser());
        assertNotNull(mageTabExperimentValidator.getMatrixValidator());
        assertTrue(mageTabExperimentValidator.isRemote());

        final Map<String, Processor<File, Boolean>> mafHandlers = getMafHandlers((AbstractMafFileVersionDispatcher<Boolean>)listProcessors.get(1));
        assertNotNull(((MafFileValidator)mafHandlers.get("1.0")).getChromInfoUtils());
        assertNotNull(((MafFileValidator)mafHandlers.get("2.3")).getChromInfoUtils());

        // Check VCF version
        final VcfValidator vcfValidator = (VcfValidator)listProcessors.get(3);
        assertNotNull(vcfValidator);
        assertEquals("1.1", vcfValidator.getTcgaVcfVersion());

        // Check for one of the validators whether the barcode validator has been initialized (should be the same for all other validators that need it).
        final MafFileValidator mafFileValidator = (MafFileValidator) mafHandlers.get("1.0");
        assertNotNull(mafFileValidator);

        final QcLiveBarcodeAndUUIDValidatorImpl qcLiveBarcodeAndUUIDValidator = (QcLiveBarcodeAndUUIDValidatorImpl) mafFileValidator.getBarcodeValidator();
        assertNotNull(qcLiveBarcodeAndUUIDValidator);

        final Maf2FileValidator maf2FileValidator = (Maf2FileValidator) mafHandlers.get("2.3");
        assertNotNull(maf2FileValidator.getCenterQueries());
        assertTrue(maf2FileValidator.getCenterQueries() instanceof RemoteCenterQueries);

        final BiospecimenIdWsQueriesImpl biospecimenIdWsQueries = (BiospecimenIdWsQueriesImpl) qcLiveBarcodeAndUUIDValidator.getBiospecimenIdWsQueries();
        assertNotNull(biospecimenIdWsQueries);
        assertNotNull(biospecimenIdWsQueries.getBaseBiospecimenJsonWs());
        assertNotNull(biospecimenIdWsQueries.getClient());

        final ValidationWebServiceQueriesImpl validationWebServiceQueries = (ValidationWebServiceQueriesImpl) qcLiveBarcodeAndUUIDValidator.getValidationWebServiceQueries();
        assertNotNull(validationWebServiceQueries);
        assertNotNull(validationWebServiceQueries.getBaseValidationWebServiceURL());
        assertNotNull(validationWebServiceQueries.getRestfulWebserviceClient());
        assertNotNull(validationWebServiceQueries.getWebServiceBatchSize());
    }
    
    @Test
    public void testMakeBcrValidator() throws NoSuchFieldException, IllegalAccessException {
    	soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);
        final ExperimentValidator bcrExperimentValidator = soundcheck.makeValidator(Experiment.TYPE_BCR, false);
        assertNotNull(bcrExperimentValidator);

        final List<Processor<Archive, Boolean>> listProcessors = getListProcessors(bcrExperimentValidator);

        assertNotNull(listProcessors);
        assertEquals(listProcessors.size(), 4);
        assertTrue(listProcessors.get(0) instanceof ManifestValidator);        
        assertTrue(listProcessors.get(1) instanceof ClinicalXmlValidator);
        assertTrue(listProcessors.get(2) instanceof BiospecimenXmlValidator);
        assertTrue(listProcessors.get(3) instanceof ControlArchiveValidator);

        // Check for one of the validators whether the barcode validator has been initialized (should be the same for all other validators that need it).
        final ClinicalXmlValidator clinicalXmlValidator = (ClinicalXmlValidator) listProcessors.get(1);

        final QcLiveBarcodeAndUUIDValidatorImpl qcLiveBarcodeAndUUIDValidator = (QcLiveBarcodeAndUUIDValidatorImpl) clinicalXmlValidator.getBarcodeAndUUIDValidator();
        assertNotNull(qcLiveBarcodeAndUUIDValidator);


        final BiospecimenIdWsQueriesImpl biospecimenIdWsQueries = (BiospecimenIdWsQueriesImpl) qcLiveBarcodeAndUUIDValidator.getBiospecimenIdWsQueries();
        assertNotNull(biospecimenIdWsQueries);
        assertNotNull(biospecimenIdWsQueries.getBaseBiospecimenJsonWs());
        assertNotNull(biospecimenIdWsQueries.getClient());

        final ValidationWebServiceQueriesImpl validationWebServiceQueries = (ValidationWebServiceQueriesImpl) qcLiveBarcodeAndUUIDValidator.getValidationWebServiceQueries();
        assertNotNull(validationWebServiceQueries);
        assertNotNull(validationWebServiceQueries.getBaseValidationWebServiceURL());
        assertNotNull(validationWebServiceQueries.getRestfulWebserviceClient());
        assertNotNull(validationWebServiceQueries.getWebServiceBatchSize());

        assertNotNull(clinicalXmlValidator.getBcrUtils());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMakeUnSupportedValidator() {
        soundcheck.makeValidator("not-a-type", false);
    }

    @Test
    public void testDectectExperimentTypeNoRemote() throws ApplicationException {
        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("jhu-usc.edu", ""));
        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("lbl.gov", ""));
        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("mskcc.org", ""));
        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("hudsonalpha.org", ""));
        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("unc.edu", ""));
        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("hms.harvard.edu", ""));
        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("broad.mit.edu", ""));
        assertEquals(Experiment.TYPE_GSC, soundcheck.detectExperimentType("broad.mit.edu", "ABI"));
        assertEquals(Experiment.TYPE_BCR, soundcheck.detectExperimentType("intgen.org", "bio"));
        assertEquals(Experiment.TYPE_GSC, soundcheck.detectExperimentType("hgsc.bcm.edu", ""));
        assertEquals(Experiment.TYPE_GSC, soundcheck.detectExperimentType("genome.wustl.edu", ""));
        assertEquals(Experiment.TYPE_GDAC, soundcheck.detectExperimentType("", "fh_analyses"));
        assertEquals(Experiment.TYPE_GDAC, soundcheck.detectExperimentType("", "fh_stddata"));
        assertEquals(Experiment.TYPE_GDAC, soundcheck.detectExperimentType("", "fh_reports"));

        boolean caught = false;
        try {
            soundcheck.detectExperimentType("not-a-center.org", "test");
        }
        catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    @Test
    public void testDetectExperimentTypeWithRemote() throws ApplicationException {
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);
        mockery.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getCenterTypeForPlatform("PLATFORM");
            will(returnValue(Experiment.TYPE_CGCC));
        }});

        assertEquals(Experiment.TYPE_CGCC, soundcheck.detectExperimentType("CENTER", "PLATFORM"));
    }

    @Test
    public void testParseArgs() {
        String[] args = new String[1];
        args[0] = "arg1";
        Map<String, String> parsedArgs = Soundcheck.parseArgs(args);
        assertEquals(1, parsedArgs.size());
        assertTrue(parsedArgs.containsKey(Soundcheck.FILE_PARAMETER_KEY));
        assertEquals("arg1", parsedArgs.get(Soundcheck.FILE_PARAMETER_KEY));
    }

    @Test
    public void testParseArgsWithFlags() {
        String[] args = new String[3];
        args[0] = "param";
        args[1] = "-flag1";
        args[2] = "--flag2";

        Map<String, String> parsedArgs = Soundcheck.parseArgs(args);
        assertEquals(3, parsedArgs.size());
        assertTrue(parsedArgs.containsKey("flag1"));
        assertTrue(parsedArgs.containsKey("flag2"));
        assertTrue(parsedArgs.containsKey(Soundcheck.FILE_PARAMETER_KEY));
    }

    @Test
    public void testParseArgsWithOptions() {
        String[] args = new String[4];
        args[0] = "--flag1";
        args[1] = "-option1";
        args[2] = "option1_val";
        args[3] = "param";

        Map<String, String> parsedArgs = Soundcheck.parseArgs(args);
        assertEquals(3, parsedArgs.size());
        assertTrue(parsedArgs.containsKey("flag1"));
        assertTrue(parsedArgs.containsKey("option1"));
        assertTrue(parsedArgs.containsKey(Soundcheck.FILE_PARAMETER_KEY));
        assertEquals("option1_val", parsedArgs.get("option1"));
    }

    @Test
    public void testParseArgsMixedCase() {
        String[] args = new String[3];
        args[0] = "param";
        args[1] = "-Flag1";
        args[2] = "--FLAG2";

        Map<String, String> parsedArgs = Soundcheck.parseArgs(args);
        assertEquals(3, parsedArgs.size());
        assertTrue(parsedArgs.containsKey("flag1"));
        assertTrue(parsedArgs.containsKey("flag2"));
        assertTrue(parsedArgs.containsKey(Soundcheck.FILE_PARAMETER_KEY));
    }

    @Test
    public void testInitUploadValidator() {
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);

        @SuppressWarnings("unchecked")
        final Processor<File, Archive> mockUploadChecker = (Processor<File, Archive>) mockery.mock(Processor.class);

        mockery.checking(new Expectations() {{
            one(mockUploadChecker).addInputValidator(with(any(MD5Validator.class)));
            one(mockUploadChecker).addOutputValidator(with(any(ArchiveNameValidator.class)));
            one(mockUploadChecker).addOutputValidator(with(any(RemoteDomainNameValidator.class)));
            one(mockUploadChecker).addOutputValidator(with(any(RemotePlatformValidator.class)));
            one(mockUploadChecker).addOutputValidator(with(any(RemoteTumorTypeValidator.class)));
            one(mockUploadChecker).addPostProcessor(with(any(ArchiveExpander.class)));
        }});
        soundcheck.initUploadChecker(mockUploadChecker, false);

    }

    @Test
    public void testAddExistingArchivesToExperiment() throws ApplicationException, Processor.ProcessorException {
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);

        Experiment experiment = new Experiment();
        experiment.setCenterName("DOMAIN");
        experiment.setPlatformName("PLATFORM");
        experiment.setTumorName("TUMOR");
        Archive archive_1_1 = makeArchive("1", "1");
        archive_1_1.setDeployStatus(Archive.STATUS_UPLOADED);
        Archive archive_2_0 = makeArchive("2", "0");
        archive_2_0.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(archive_1_1);
        experiment.addArchive(archive_2_0);

        final List<gov.nih.nci.ncicb.tcga.dccws.Archive> latestArchives = new ArrayList<gov.nih.nci.ncicb.tcga.dccws.Archive>();
        gov.nih.nci.ncicb.tcga.dccws.Archive archive1 = new gov.nih.nci.ncicb.tcga.dccws.Archive();
        archive1.setSerialIndex(1);
        archive1.setRevision(0);
        archive1.setName("center_disease.platform.Level_1.1.0.0");
        archive1.setDeployLocation("center_disease.platform.Level_1.1.0.0.tar.gz");
        archive1.setDeployStatus(Archive.STATUS_AVAILABLE);
        gov.nih.nci.ncicb.tcga.dccws.Archive archive3 = new gov.nih.nci.ncicb.tcga.dccws.Archive();
        archive3.setSerialIndex(3);
        archive3.setRevision(0);
        archive3.setName("center_disease.platform.Level_1.3.0.0");
        archive3.setDeployLocation("center_disease.platform.Level_1.3.0.0.tar.gz");
        archive3.setDeployStatus(Archive.STATUS_AVAILABLE);
        latestArchives.add(archive1);
        latestArchives.add(archive3);

        mockery.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getLatestArchives("TUMOR", "DOMAIN", "PLATFORM");
            will(returnValue(latestArchives));
        }});

        soundcheck.addExistingArchivesToExperiment(experiment);
        mockery.assertIsSatisfied();

        assertEquals(3, experiment.getArchives().size());
        assertEquals(1, experiment.getPreviousArchives().size());
        assertEquals("1", experiment.getPreviousArchives().get(0).getSerialIndex());
        assertEquals("0", experiment.getPreviousArchives().get(0).getRevision());

        assertEquals(1, experiment.getArchivesForStatus(Archive.STATUS_AVAILABLE).size());
        Archive archive_3_0 = experiment.getArchivesForStatus(Archive.STATUS_AVAILABLE).get(0);
        assertEquals("3", archive_3_0.getSerialIndex());
        assertEquals("0", archive_3_0.getRevision());
    }

    @Test
    public void testAddExistingArchivesToExperimentWithLaterVersion() throws ApplicationException {
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);

        Experiment experiment = new Experiment();
        experiment.setCenterName("DOMAIN");
        experiment.setPlatformName("PLATFORM");
        experiment.setTumorName("TUMOR");

        Archive archive_1_0 = makeArchive("1", "0");
        archive_1_0.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(archive_1_0);

        // make return value for mock validation helper
        final List<gov.nih.nci.ncicb.tcga.dccws.Archive> latestArchives = new ArrayList<gov.nih.nci.ncicb.tcga.dccws.Archive>();
        gov.nih.nci.ncicb.tcga.dccws.Archive wsArchive_1_1 = new gov.nih.nci.ncicb.tcga.dccws.Archive();
        wsArchive_1_1.setSerialIndex(1);
        wsArchive_1_1.setRevision(1);
        wsArchive_1_1.setName("center_disease.plaform.Level_1.1.1.0");
        wsArchive_1_1.setDeployLocation("center_disease.plaform.Level_1.1.1.0.tar.gz");
        wsArchive_1_1.setDeployStatus(Archive.STATUS_AVAILABLE);
        latestArchives.add(wsArchive_1_1);

        mockery.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getLatestArchives("TUMOR", "DOMAIN", "PLATFORM");
            will(returnValue(latestArchives));
        }});

        try {
            soundcheck.addExistingArchivesToExperiment(experiment);
            fail("Exception was expected to be thrown");
        } catch (Processor.ProcessorException e) {
            // this is what we expected
            mockery.assertIsSatisfied();
        }
    }

    @Test
    public void testAddExistingArchivesToExperimentNotAvailable() throws ApplicationException, Processor.ProcessorException {
        // make sure that if an existing archive is not set as available, we don't use it for validation purposes
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);

        Experiment experiment = new Experiment();
        experiment.setCenterName("DOMAIN");
        experiment.setPlatformName("PLATFORM");
        experiment.setTumorName("TUMOR");

        Archive archive_1_1 = makeArchive("1", "1");
        archive_1_1.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(archive_1_1);

        // make return value for mock validation helper
        final List<gov.nih.nci.ncicb.tcga.dccws.Archive> latestArchives = new ArrayList<gov.nih.nci.ncicb.tcga.dccws.Archive>();
        gov.nih.nci.ncicb.tcga.dccws.Archive wsArchive_1_0 = new gov.nih.nci.ncicb.tcga.dccws.Archive();
        wsArchive_1_0.setSerialIndex(1);
        wsArchive_1_0.setRevision(0);
        wsArchive_1_0.setName("center_disease.platform.Level_1.1.0.0");
        wsArchive_1_0.setDeployLocation("center_disease.platform.Level_1.1.0.0.tar.gz");
        wsArchive_1_0.setDeployStatus(Archive.STATUS_IN_REVIEW);
        latestArchives.add(wsArchive_1_0);

        mockery.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getLatestArchives("TUMOR", "DOMAIN", "PLATFORM");
            will(returnValue(latestArchives));
        }});

        soundcheck.addExistingArchivesToExperiment(experiment);
        mockery.assertIsSatisfied();

        // the existing archive should not have been added to the Experiment, because it is not Available
        assertEquals(1, experiment.getArchives().size());
        assertTrue(experiment.getArchives().contains(archive_1_1));
    }

    @Test
    public void testAddExistingArchivesToExperimentWithSameVersion() throws ApplicationException {
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);

        Experiment experiment = new Experiment();
        experiment.setCenterName("DOMAIN");
        experiment.setPlatformName("PLATFORM");
        experiment.setTumorName("TUMOR");

        Archive archive_1_0 = makeArchive("1", "0");
        archive_1_0.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(archive_1_0);

        // make return value for mock validation helper
        final List<gov.nih.nci.ncicb.tcga.dccws.Archive> latestArchives = new ArrayList<gov.nih.nci.ncicb.tcga.dccws.Archive>();
        gov.nih.nci.ncicb.tcga.dccws.Archive wsArchive_1_0 = new gov.nih.nci.ncicb.tcga.dccws.Archive();
        wsArchive_1_0.setSerialIndex(1);
        wsArchive_1_0.setRevision(0);
        wsArchive_1_0.setDeployStatus(Archive.STATUS_AVAILABLE);
        wsArchive_1_0.setName("center_disease.platform.Level_1.1.0.0");
        wsArchive_1_0.setDeployLocation("center_disease.platform.Level_1.1.0.0.tar.gz");
        latestArchives.add(wsArchive_1_0);

        mockery.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getLatestArchives("TUMOR", "DOMAIN", "PLATFORM");
            will(returnValue(latestArchives));
        }});

        try {
            soundcheck.addExistingArchivesToExperiment(experiment);
            fail("Exception was expected to be thrown");
        } catch (Processor.ProcessorException e) {
            // this is what we expected
            mockery.assertIsSatisfied();
        }
    }

    @Test
    public void testAddExistingArchivesToExperimentOld() throws ApplicationException, Processor.ProcessorException {
        // test when have an old style archive... we don't want to add those to the Experiment
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);

        Experiment experiment = new Experiment();
        experiment.setCenterName("DOMAIN");
        experiment.setPlatformName("PLATFORM");
        experiment.setTumorName("TUMOR");

        Archive archive_1_3 = makeArchive("1", "3");
        archive_1_3.setDeployStatus(Archive.STATUS_UPLOADED);
        archive_1_3.setArchiveType(Archive.TYPE_LEVEL_1);
        experiment.addArchive(archive_1_3);

        // make return value for mock validation helper
        final List<gov.nih.nci.ncicb.tcga.dccws.Archive> latestArchives = new ArrayList<gov.nih.nci.ncicb.tcga.dccws.Archive>();
        gov.nih.nci.ncicb.tcga.dccws.Archive wsArchive_1_0 = new gov.nih.nci.ncicb.tcga.dccws.Archive();
        wsArchive_1_0.setSerialIndex(1);
        wsArchive_1_0.setRevision(0);
        wsArchive_1_0.setName("center_disease.platform.1.0.0"); // CLASSIC archive
        wsArchive_1_0.setDeployLocation("center_disease.platform.1.0.0.tar.gz");
        wsArchive_1_0.setDeployStatus(Archive.STATUS_AVAILABLE);
        latestArchives.add(wsArchive_1_0);

        mockery.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getLatestArchives("TUMOR", "DOMAIN", "PLATFORM");
            will(returnValue(latestArchives));
        }});

        soundcheck.addExistingArchivesToExperiment(experiment);
        assertEquals(1, experiment.getArchives().size()); // old one should not be added
    }

    @Test
    public void testAddExistingArchivesToExperimentOldLaterVersion() throws ApplicationException, Processor.ProcessorException {
        soundcheck.setRemoteValidationHelper(mockRemoteValidationHelper);

        Experiment experiment = new Experiment();
        experiment.setCenterName("DOMAIN");
        experiment.setPlatformName("PLATFORM");
        experiment.setTumorName("TUMOR");

        Archive archive_1_3 = makeArchive("1", "3");
        archive_1_3.setDeployStatus(Archive.STATUS_UPLOADED);
        archive_1_3.setArchiveType(Archive.TYPE_LEVEL_1);
        experiment.addArchive(archive_1_3);

        // make return value for mock validation helper
        final List<gov.nih.nci.ncicb.tcga.dccws.Archive> latestArchives = new ArrayList<gov.nih.nci.ncicb.tcga.dccws.Archive>();
        gov.nih.nci.ncicb.tcga.dccws.Archive wsArchive_1_3 = new gov.nih.nci.ncicb.tcga.dccws.Archive();
        wsArchive_1_3.setName("center_disease.platform.1.0.0"); // CLASSIC archive
        wsArchive_1_3.setDeployLocation("center_disease.platform.1.0.0.tar.gz");
        wsArchive_1_3.setSerialIndex(1);
        wsArchive_1_3.setRevision(3);
        wsArchive_1_3.setDeployStatus(Archive.STATUS_AVAILABLE);
        latestArchives.add(wsArchive_1_3);

        mockery.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getLatestArchives("TUMOR", "DOMAIN", "PLATFORM");
            will(returnValue(latestArchives));
        }});

        try {
            soundcheck.addExistingArchivesToExperiment(experiment);
            fail("Exception was expected to be thrown");
        } catch (Processor.ProcessorException e) {
            // this is what we expected
            mockery.assertIsSatisfied();
        }
    }   
    @Test
    public void testCheckBcrValidatorValues () throws NoSuchFieldException, IllegalAccessException{
    	final ExperimentValidator bcrExperimentValidator = soundcheck.makeValidator(Experiment.TYPE_BCR, false);        
        final List<Processor<Archive, Boolean>> listProcessors = getListProcessors(bcrExperimentValidator);
        ClinicalXmlValidator xmlValidator = (ClinicalXmlValidator)listProcessors.get(1);
        assertNotNull(xmlValidator.getShippedPortionIdProcessor());
        assertNull(xmlValidator.getCodeTableQueries());
        assertEquals("//portions/shipment_portion", xmlValidator.getShipmentPortionPath());
        assertEquals("bcr_shipment_portion_uuid", xmlValidator.getBcrShipmentPortionUuidElementName());
        assertEquals("center_id", xmlValidator.getCenterIdElementName());
        assertEquals("plate_id",xmlValidator.getPlateIdElementName());
        assertEquals("shipment_portion_bcr_aliquot_barcode", xmlValidator.getShipmentPortionBcrAliquotBarcodeElementName());
        assertEquals("2\\.6(\\.\\d*)?", xmlValidator.getqCliveXMLSchemaValidator().getValidXsdVersionPattern());
        assertEquals("tcga-data\\.nci\\.nih\\.gov", xmlValidator.getqCliveXMLSchemaValidator().getValidXsdDomainPattern());
        assertEquals("bcr", xmlValidator.getqCliveXMLSchemaValidator().getValidXsdPrefixPattern());

        final List<String> actualDatesToValidate = xmlValidator.getDatesToValidate();
        final List<String> expectedDatesToValidate = Arrays.asList("birth", "last_known_alive", "death", "last_followup",
                "initial_pathologic_diagnosis", "tumor_progression", "tumor_recurrence", "new_tumor_event_after_initial_treatment",
                "additional_surgery_locoregional_procedure", "additional_surgery_metastatic_procedure", "form_completion", "procedure",
                "radiation_treatment_start", "radiation_treatment_end", "drug_treatment_start", "drug_treatment_end", "radiation_therapy_start",
                "radiation_therapy_end", "drug_therapy_start", "drug_therapy_end", "collection", "sample_procurement", "shipment", "creation");
        assertEquals(expectedDatesToValidate, actualDatesToValidate);

        final List<DateComparator> dateComparators = xmlValidator.getDateComparators();
        assertEquals(1, dateComparators.size());
        final DateComparator dateComparator = dateComparators.get(0);
        assertNotNull(dateComparator);
        assertEquals("last_followup", dateComparator.getLeftOperandName());
        assertEquals("initial_pathologic_diagnosis", dateComparator.getRightOperandName());
        assertEquals(DateComparator.Operator.GE, dateComparator.getOperator());

        BCRIDProcessorImpl bcridProcessor = (BCRIDProcessorImpl) xmlValidator.getBcridProcessor();
        assertEquals("//aliquots/aliquot", bcridProcessor.getAliquotElementXPath());
        assertEquals("bcr_aliquot_barcode", bcridProcessor.getAliquotBarcodeElement());
        assertEquals("bcr_aliquot_uuid", bcridProcessor.getAliquotUuidElement());
        assertEquals("day_of_shipment", bcridProcessor.getShipDayElement());
        assertEquals("month_of_shipment", bcridProcessor.getShipMonthElement());
        assertEquals("year_of_shipment", bcridProcessor.getShipYearElement());
        assertNotNull(bcridProcessor.getShippedPortionElementXPath());
        assertNotNull(bcridProcessor.getShippedPortionBarcodeElement());
        assertNotNull(bcridProcessor.getShippedPortionUuidElement());
        assertNotNull(bcridProcessor.getShippedPortionShipDayElement());
        assertNotNull(bcridProcessor.getShippedPortionShipMonthElement());
        assertNotNull(bcridProcessor.getShippedPortionShipYearElement());
    }

    @Test
    public void testGetHelpMessage() {

        final String expectedHelpMessage = "\n" +
                "Usage: validate[.sh/.bat] 'archive or directory to validate' -[useuuid|usebarcode] [-bypass] [-noremote] [-centertype CGCC|GSC|BCR|GDAC] [-magetab]\n" +
                "\nIf the first argument is a directory, all files in the directory ending with '.tar' and '.tar.gz' will be validated.\n" +
                "\nFor CGCC submissions, the first argument MUST be a directory containing all archives you intend to submit, including the updated mage-tab archive. \n" +
                "For non-CGCC archives, the argument may be either a single archive file or a directory containing multiple archives.\n" +
                "\n" +
                "During the UUID Transition, either -useuuid or -usebarcode must be specified. If -useuuid is used the validator will assume the archive has been converted to use UUIDs.\n" +
                "\nIf the -bypass flag is set, MD5 checks will be skipped, and existing expanded directories will be used.\n" +
                "Once your archive passes using the -bypass flag, please run it again without -bypass to verify the final MD5 value.\n" +
                "\nWithout the -noremote flag, the validator will use the DCC web service to validate certain parts of the archive.\n" +
                "If you are running without an internet connection or are getting errors related to connecting to the DCC web service, run with the -noremote to disable this feature.\n" +
                "\nThe -centertype argument is optional; the program will normally get the center type from the DCC web service or based on the center/platform name. Valid values are CGCC, GSC, BCR, or GDAC.\n" +
                "\nThe -magetab flag is optional; For GSC archives, it indicates that a mage-tab archive must be provided (unless the GSC archive does not contain MAF files)\n" +
                "\n\nThe latest version of the validator can be found here: https://wiki.nci.nih.gov/display/TCGA/Download+TCGA+Archive+Validator\n";

        assertEquals(expectedHelpMessage,Soundcheck.getHelpMessage());
    }

    @Test
    public void testSoundcheckAcceptingTarAndTarGz() {

        class SoundcheckFake extends Soundcheck {

            @Override
            public void execute(final List<File> experimentFiles,
                                final boolean bypass) {

                assertNotNull(experimentFiles);
                assertEquals(2, experimentFiles.size());

                final File file1 = experimentFiles.get(0);
                final File file2 = experimentFiles.get(1);
                assertNotNull(file1);
                assertNotNull(file2);

                final List<String> filenames = new ArrayList<String>();
                filenames.add(file1.getName());
                filenames.add(file2.getName());

                assertTrue(filenames.contains("test.tar"));
                assertTrue(filenames.contains("test.tar.gz"));
            }
        }

        final String[] args = {"applicationName", ARCHIVE_DIRECTORY};
        Soundcheck.runSoundcheckWith(new SoundcheckFake(), args, new LoggerImpl());
    }

    @Test
    public void testVersion() {

        final String version = "Squirrel 2.0";
        soundcheck.setVersion(version);

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "[QC Version " + version + "]");
            one(mockLogger).log(Level.INFO, Soundcheck.getHelpMessage());
        }});

        Soundcheck.runSoundcheckWith(soundcheck, new String[]{}, mockLogger);
    }

    @Test
    public void testTooManyErrorsException() {
        Soundcheck soundcheck = new Soundcheck();
        soundcheck.setLogger(mockLogger);
        soundcheck.setRemoteValidationHelper(null);
        QcContext qcContext = new QcContext();
        for (int i=0; i<=QcContext.MSGS_LIMIT; i++) {
            qcContext.addError("error" + i);
            final String message = "- error" + i + "\n";
            mockery.checking(new Expectations() {{
                one(mockLogger).log(Level.INFO, message);
            }});
        }
        soundcheck.setQcContext(qcContext);
        List<File> archives = new ArrayList<File>();
        archives.add(new File("fake"));

        mockery.checking(new Expectations() {{
            one(mockLogger).log(Level.INFO, "Unpacking fake");
            one(mockLogger).log(Level.ERROR, "FAILURE: An unexpected error occurred: Too many errors. Stopped processing the archive uninitialized archive");
            one(mockLogger).log(Level.ERROR, "If this problem persists, please report it to the DCC.");
            one(mockLogger).log(Level.INFO, "- Too many errors. Stopped processing the archive uninitialized archive\n");
            one(mockLogger).log(Level.INFO, "Validation failed\n");
            one(mockLogger).log(Level.INFO, "\nErrors:\n");
            one(mockLogger).log(Level.INFO, "\nPlease address the above issues before submitting the archives to the DCC.");
            one(mockLogger).log(Level.INFO, "\n\nThe latest version of the validator can be found here: https://wiki.nci.nih.gov/display/TCGA/Download+TCGA+Archive+Validator");
        }});

        soundcheck.execute(archives, true);

    }

    private Archive makeArchive(final String batch, final String revision) {
        Archive archive = new Archive();
        archive.setDomainName("DOMAIN");
        archive.setPlatform("PLATFORM");
        archive.setTumorType("TUMOR");
        archive.setSerialIndex(batch);
        archive.setRevision(revision);
        archive.setArchiveType(Archive.TYPE_LEVEL_1);
        archive.setRealName("center_disease.platform.type." + batch + "." + revision + ".0");
        return archive;
    }

    /**
     * Return the private field "listProcessors" from the given <code>AbstractListProcessor<I,O,Archive,Boolean></code>
     *
     * @param cgccExperimentValidator the <code>AbstractListProcessor<I,O,Archive,Boolean></code> from which to get the private "listProcessors" field
     * @return the private field "listProcessors" from the given <code>AbstractListProcessor</code>
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private List<Processor<Archive, Boolean>> getListProcessors(ExperimentValidator cgccExperimentValidator)
            throws NoSuchFieldException, IllegalAccessException {

        final Field listProcessorsField = AbstractListProcessor.class.getDeclaredField("listProcessors");
        listProcessorsField.setAccessible(true);

        final List<Processor<Archive, Boolean>> listProcessors = (List<Processor<Archive, Boolean>>)listProcessorsField.get(cgccExperimentValidator);
        return listProcessors;
    }

    /**
     * Return the private field "inputValidators" from the given <code>ExperimentValidator</code>
     *
     * @param experimentValidator the <code>ExperimentValidator</code> from which to get the protected "inputValidators" field
     * @return the protected field "inputValidators" from the given <code>AbstractListProcessor</code>
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private List<Processor<Experiment, Boolean>> getInputValidators(ExperimentValidator experimentValidator)
            throws NoSuchFieldException, IllegalAccessException {

        final Field inputValidatorsField = AbstractProcessor.class.getDeclaredField("inputValidators");
        inputValidatorsField.setAccessible(true);

        final List<Processor<Experiment, Boolean>> inputValidators = (List<Processor<Experiment, Boolean>>)inputValidatorsField.get(experimentValidator);
        return inputValidators;
    }
    
    /**
     * Return the private field "mafHandlers" from the given <code>AbstractMafFileVersionDispatcher</code>
     *
     * @param dispatcher the <code>AbstractMafFileVersionDispatcher</code> from which to get the private "mafHandlers" field
     * @return the private field "mafHandlers" from the given <code>AbstractMafFileVersionDispatcher</code>
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private Map<String, Processor<File, Boolean>> getMafHandlers(final AbstractMafFileVersionDispatcher<Boolean> dispatcher)
        throws NoSuchFieldException, IllegalAccessException {

        final Field mafHandlersField = AbstractMafFileVersionDispatcher.class.getDeclaredField("mafHandlers");
        mafHandlersField.setAccessible(true);

        final Map<String, Processor<File, Boolean>> mafHandlers = (Map<String, Processor<File, Boolean>>)mafHandlersField.get(dispatcher);
        return mafHandlers;
    }
}
