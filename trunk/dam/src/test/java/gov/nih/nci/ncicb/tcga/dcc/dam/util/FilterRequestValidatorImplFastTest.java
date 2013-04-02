package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import static org.junit.Assert.assertArrayEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test class for FilterRequestValidatorImpl.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class FilterRequestValidatorImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private FilterRequestValidatorImpl filterRequestValidator;
    private CenterQueries mockCenterQueries;
    private DataTypeQueries mockDataTypeQueries;
    private CommonBarcodeAndUUIDValidator mockBarcodeValidator;
    private PlatformQueries mockPlatformQueries;

    @Before
    public void setUp() {
        filterRequestValidator = new FilterRequestValidatorImpl();
        mockCenterQueries = context.mock(CenterQueries.class);
        mockDataTypeQueries = context.mock(DataTypeQueries.class);
        mockBarcodeValidator = context.mock(CommonBarcodeAndUUIDValidator.class);
        mockPlatformQueries = context.mock(PlatformQueries.class);
        filterRequestValidator.setCenterQueries(mockCenterQueries);
        filterRequestValidator.setDataTypeQueries(mockDataTypeQueries);
        filterRequestValidator.setBarcodeValidator(mockBarcodeValidator);
        filterRequestValidator.setPlatformQueries(mockPlatformQueries);
    }

    @Test
    public void testGetValidAvailabilitySelections() {
        assertArrayEquals(new String[]{"A", "N", "P", "NA"}, filterRequestValidator.getValidAvailabilitySelections(new String[]{"A", "N", "P", "NA"}));
        assertArrayEquals(new String[0], filterRequestValidator.getValidAvailabilitySelections(new String[]{"a", "n", "p", "na"}));
        assertArrayEquals(new String[0], filterRequestValidator.getValidAvailabilitySelections(new String[]{"walrus", "antelope", "emu"}));
        assertArrayEquals(new String[]{"A"}, filterRequestValidator.getValidAvailabilitySelections(new String[]{"hello", "A"}));
    }

    @Test
    public void testBatchSelectionIsValid() {
        assertArrayEquals(new String[]{"Unclassified", "Batch 1", "Batch 12", "Batch 34"},
                filterRequestValidator.getValidBatchSelections(new String[]{"Unclassified", "Batch 1", "Batch 12", "Batch 34"}));
        assertArrayEquals(new String[]{"Batch 12"}, filterRequestValidator.getValidBatchSelections(new String[]{"something", "Batch 12"}));
        assertArrayEquals(new String[0], filterRequestValidator.getValidBatchSelections(new String[]{"Batch -4", "Batch A"}));
    }

    @Test
    public void testGetValidCenterSelections() {
        final List<Center> centers = new ArrayList<Center>();
        centers.add(makeCenter(1, "BI", "broad.mit.edu"));
        centers.add(makeCenter(2, "HMS", "harvard.edu"));

        context.checking(new Expectations() {{
            atLeast(1).of(mockCenterQueries).getCenterList();
            will(returnValue(centers));
        }});
        assertArrayEquals(new String[]{"1"},
                filterRequestValidator.getValidCenterSelections(new String[]{"1", "HMS", "broad.mit.edu"}));
        assertArrayEquals(new String[]{"1"},
                filterRequestValidator.getValidCenterSelections(new String[]{"1", "30"}));
        assertArrayEquals(new String[0],
                filterRequestValidator.getValidCenterSelections(new String[]{"abc", "banana"}));
    }

    private Center makeCenter(final Integer id, final String shortName, final String domain) {
        final Center center = new Center();
        center.setCenterId(id);
        center.setShortName(shortName);
        center.setCenterName(domain);
        return center;
    }

    @Test
    public void testGetValidLevelSelections() {
        assertArrayEquals(new String[]{"1", "2", "3", "C"},
                filterRequestValidator.getValidLevelSelections(new String[]{"1", "2", "3", "C"}));
        assertArrayEquals(new String[]{"2"},
                filterRequestValidator.getValidLevelSelections(new String[]{"11", "2", "Z"}));
    }

    @Test
    public void testGetValidPlatformTypeSelections() {
        final Collection<Map<String, Object>> dataTypes = new ArrayList<Map<String, Object>>();
        dataTypes.add(makeDataType(4, "SNP"));
        dataTypes.add(makeDataType(1, "Expression-Genes"));

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getAllDataTypes();
            will(returnValue(dataTypes));
        }});
        assertArrayEquals(new String[]{"4"},
                filterRequestValidator.getValidPlatformTypeSelections(new String[]{"Expression-Genes", "4"}));
        assertArrayEquals(new String[]{"1"},
                filterRequestValidator.getValidPlatformTypeSelections(new String[]{"123", "something", "1"}));
    }

    private Map<String, Object> makeDataType(final int id, final String dataTypeName) {
        final Map<String, Object> dataTypeMap = new HashMap<String, Object>();
        dataTypeMap.put("data_type_id", id);
        dataTypeMap.put("name", dataTypeName);
        return dataTypeMap;
    }

    @Test
    public void testGetValidProtectedStatusSelections() {
        assertArrayEquals(new String[]{"N", "P"},
                filterRequestValidator.getValidProtectedStatusSelections(new String[]{"N", "P", ":)", ":("}));
    }

    @Test
    public void testGetValidSampleSelections() {
        context.checking(new Expectations() {{
            one(mockBarcodeValidator).validateSampleBarcodeFormat("TCGA-11-1234-01");
            will(returnValue(true));
            one(mockBarcodeValidator).validateSampleBarcodeFormat("TCGA-34-5678-10");
            will(returnValue(true));
            one(mockBarcodeValidator).validateSampleBarcodeFormat("TCGA-ZZ-1234-01");
            will(returnValue(true));
            one(mockBarcodeValidator).validateSampleBarcodeFormat("TCGA-YY-5678-10");
            will(returnValue(true));
            one(mockBarcodeValidator).validateSampleBarcodeFormat("TCGA-AB-8765-*");
            will(returnValue(false));
            one(mockBarcodeValidator).validateSampleBarcodeFormat("blah");
            will(returnValue(false));
        }});
        // only sample barcodes -- need to use barcode validator
        assertArrayEquals(new String[]{"TCGA-11-1234-01", "TCGA-34-5678-10"},
                filterRequestValidator.getValidSampleSelections("TCGA-11-1234-01, TCGA-34-5678-10"));
        assertArrayEquals(new String[]{"TCGA-AB-8765-*", "TCGA-ZZ-1234-01", "TCGA-YY-5678-10"},
                filterRequestValidator.getValidSampleSelections("TCGA-AB-8765-*,TCGA-ZZ-1234-01,TCGA-YY-5678-10,blah"));
    }

    @Test
    public void testGetValidTumorNormalSelections() {
        assertArrayEquals(new String[]{
                DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL,
                DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR,
                DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL,
                DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL
        },
                filterRequestValidator.getValidTumorNormalSelections(new String[]{
                        DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL,
                        DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR,
                        DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL,
                        DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL,
                        "hi",
                        "123"
                })
        );
    }

    @Test
    public void testGetValidPlatformSelections() {
        final List<Platform> availablePlatforms = new ArrayList<Platform>();
        availablePlatforms.add(makePlatform(1, "ABI"));
        availablePlatforms.add(makePlatform(2, "HumanMethylation27"));
        availablePlatforms.add(makePlatform(3, "GenomeWideSNP6"));
        context.checking(new Expectations() {{
            one(mockPlatformQueries).getPlatformList();
            will(returnValue(availablePlatforms));
        }});
        assertArrayEquals(new String[]{"ABI", "HumanMethylation27"},
                filterRequestValidator.getValidPlatformSelections(new String[]{"ABI", "HumanMethylation27", "Penguin", "3"}));
    }

    private Platform makePlatform(final int id, final String name) {
        final Platform platform = new Platform();
        platform.setPlatformId(id);
        platform.setPlatformName(name);
        return platform;
    }

}
