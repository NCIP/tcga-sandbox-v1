package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test for AbstractSeqDataFileValidator
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AbstractSeqDataFileValidatorTest {

    private static final String SAMPLES_DIR = Thread.currentThread().getContextClassLoader().getResource("samples").getPath();

    private static final String FILE_DIR = SAMPLES_DIR + File.separator
            + "qclive" + File.separator
            + "abstractSeqDataFileValidator";

    private final Mockery context = new JUnit4Mockery();
    private QcContext qcContext;
    private AbstractSeqDataFileValidator testValidator;
    private QcLiveBarcodeAndUUIDValidator mockBarcodeAndUUIDValidator;
    private BarcodeTumorValidator mockBarcodeTumorValidator;


    @Before
    public void setUp() throws Exception {
        qcContext = new QcContext();
        testValidator = makeValidator(true, Arrays.asList("header1"), true, true, ".txt", true);
        mockBarcodeAndUUIDValidator = context.mock(QcLiveBarcodeAndUUIDValidator.class);
        mockBarcodeTumorValidator = context.mock(BarcodeTumorValidator.class);
        testValidator.setQcLiveBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidator);
        testValidator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
        testValidator.setSeqDataFileValidationErrorMessagePropertyType(MessagePropertyType.RNA_SEQ_DATA_FILE_VALIDATION_ERROR);

        final Archive fakeArchive = new Archive();
        fakeArchive.setTumorType("TEST");
        qcContext.setArchive(fakeArchive);
    }

    @Test
    public void testProcessFilePreTransitionWithBarcodeFilename() throws Processor.ProcessorException {
        testWithBarcodeFilename(false);
    }

    @Test
    public void testProcessFilePreTransitionWithUUIDFilename() throws Processor.ProcessorException {
        qcContext.setCenterConvertedToUUID(false);
        final boolean valid = testValidator.processFile(new File(FILE_DIR, "test.12345678-1234-1234-1234-abcdefabcdef.seq.txt"),
                qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
    }

    @Test
    public void testProcessFilePostTransitionWithBarcodeFilename() throws Processor.ProcessorException {
        testWithBarcodeFilename(true);
    }

    @Test
    public void testProcessFilePostTransitionWithUUIDFilename() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).isAliquotUUID("12345678-1234-1234-1234-abcdefabcdef");
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("12345678-1234-1234-1234-abcdefabcdef", "TEST");
            will(returnValue(true));
        }});
        qcContext.setCenterConvertedToUUID(true);
        final boolean valid = testValidator.processFile(new File(FILE_DIR, "test.12345678-1234-1234-1234-abcdefabcdef.seq.txt"),
                qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
    }

    @Test (expected = Processor.ProcessorException.class)
    public void testEmptyFile() throws Processor.ProcessorException {
        testValidator.processFile(new File(FILE_DIR, "blank.87654321-4321-abcd-1234-ffeeddccbbaa.seq.txt"), qcContext);
    }

    private void testWithBarcodeFilename(final boolean isCenterConverted) throws Processor.ProcessorException {
         context.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-00-0000-01A-01D-1234-00");
            will(returnValue(true));

            one(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-00-0000-01A-01D-1234-00", "TEST");
            will(returnValue(true));
        }});

        qcContext.setCenterConvertedToUUID(isCenterConverted);
        final boolean valid = testValidator.processFile(new File(FILE_DIR, "test.TCGA-00-0000-01A-01D-1234-00.seq.txt"),
                qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }


    private AbstractSeqDataFileValidator makeValidator(final boolean valueIsValid,
                                                       final List<String> expectedColumns,
                                                       final boolean returnValue,
                                                       final boolean defaultReturnValue,
                                                       final String fileExtension,
                                                       final boolean isCorrectArchiveType
    ) {
        return new AbstractSeqDataFileValidator() {
            @Override
            protected boolean valueIsValid(final String value, final String headerName, final QcContext context, final int rowNum) {
                return valueIsValid;
            }

            @Override
            protected List<String> getExpectedColumns() {
                return expectedColumns;
            }

            @Override
            protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
                return returnValue;
            }

            @Override
            protected Boolean getDefaultReturnValue(final Archive archive) {
                return defaultReturnValue;
            }

            @Override
            protected String getFileExtension() {
                return fileExtension;
            }

            @Override
            protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {
                return isCorrectArchiveType;
            }

            @Override
            public String getName() {
                return "Test Implementation";
            }
        };
    }
}
