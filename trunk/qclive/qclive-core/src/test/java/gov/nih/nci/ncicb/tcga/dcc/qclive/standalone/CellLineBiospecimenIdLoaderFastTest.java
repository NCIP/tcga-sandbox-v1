package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ControlQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * Test for CellLineBiospecimenIdLoader
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class CellLineBiospecimenIdLoaderFastTest {

    private final Mockery context = new JUnit4Mockery();
    private CellLineBiospecimenIdLoader biospecimenIdLoader;

    private BarcodeUuidResolver mockBarcodeUuidResolver;
    private BCRIDQueries mockBcridQueries;
    private BCRIDQueries mockBcridQueriesDisease;
    private ShippedBiospecimenQueries mockShippedBiospecimenQueries;
    private ShippedBiospecimenQueries mockShippedBiospecimenQueriesDisease;
    private BCRDataService mockBcrDataService;
    private ControlQueries mockControlQueries;
    private ControlQueries mockControlQueriesDisease;

    private final BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl();

    @Before
    public void setUp() throws Exception {

        biospecimenIdLoader = new CellLineBiospecimenIdLoader();
        mockBarcodeUuidResolver = context.mock(BarcodeUuidResolver.class);
        mockBcridQueries = context.mock(BCRIDQueries.class, "commonBcrIdQueries");
        mockBcridQueriesDisease = context.mock(BCRIDQueries.class, "diseaseBcrIdQueries");
        mockShippedBiospecimenQueries = context.mock(ShippedBiospecimenQueries.class, "commonShippedBiospecimenQueries");
        mockShippedBiospecimenQueriesDisease = context.mock(ShippedBiospecimenQueries.class, "diseaseShippedBiospecimenQueries");
        mockBcrDataService = context.mock(BCRDataService.class);
        mockControlQueries = context.mock(ControlQueries.class, "commonControlQueries");
        mockControlQueriesDisease = context.mock(ControlQueries.class, "diseaseControlQueries");

        biospecimenIdLoader.setBarcodeUuidResolver(mockBarcodeUuidResolver);
        biospecimenIdLoader.setBcridQueries(mockBcridQueries);
        biospecimenIdLoader.setBcridQueriesDisease(mockBcridQueriesDisease);
        biospecimenIdLoader.setShippedBiospecimenQueries(mockShippedBiospecimenQueries);
        biospecimenIdLoader.setShippedBiospecimenQueriesDisease(mockShippedBiospecimenQueriesDisease);
        biospecimenIdLoader.setBcrDataService(mockBcrDataService);
        biospecimenIdLoader.setControlQueries(mockControlQueries);
        biospecimenIdLoader.setControlQueriesDisease(mockControlQueriesDisease);
    }

    @Test
    public void testLoadBiospecimenIds() throws Exception {
        final Center fakeCenter = new Center();
        fakeCenter.setCenterId(11);
        final File fakeFile = new File("fake");

        final Set<Object> diseaseNames = new HashSet<Object>();
        diseaseNames.add("DIS1");
        diseaseNames.add("DIS2");

        final Tumor cntl = new Tumor(31, "CNTL");

        final List<String[]> aliquotsFromFile = Arrays.asList(
                new String[]{"TCGA-AV-A03D-20A-01D-A00Y-05", null, "9b50c10a-1d15-4c73-9523-44ce5c5319cd"},
                new String[]{"TCGA-AV-A03D-20A-01D-A058-05", "2010-11-08", "2CF89B0D-E7CF-453C-B88B-DA0B46A92B0A"},
                new String[]{"TCGA-AV-A03D-20A-01D-A059-01", "2010-11-08", "FE557B9E-A63B-4C53-B84D-2C628DBB40BD"}
        );

        context.checking(new Expectations() {{

            one(mockBcrDataService).findAllAliquotsInFile(fakeFile);
            will(returnValue(aliquotsFromFile));

            int aliquotId = 1;
            for (final String[] aliquot : aliquotsFromFile) {
                one(mockBcrDataService).parseAliquotBarcode(aliquot[0]);
                will(returnValue(bcridProcessor.parseAliquotBarcode(aliquot[0])));

                one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(aliquot[0], aliquot[2].toLowerCase(), cntl, fakeCenter, false);
                one(mockBcridQueries).exists(with(expectedBcrId(aliquot[0], aliquot[2].toLowerCase())));
                will(returnValue(-1));

                one(mockBcridQueries).addBCRID(with(expectedBcrId(aliquot[0], aliquot[2].toLowerCase())), with(false));
                will(returnValue(aliquotId));
                exactly(diseaseNames.size()).of(mockBcridQueriesDisease).addBCRID(with(expectedBcrId(aliquot[0], aliquot[2].toLowerCase())), with(true));

                one(mockControlQueries).persistControl(with(expectedControl(aliquot[0], aliquot[2].toLowerCase())));
                one(mockControlQueries).updateControlForShippedBiospecimen(with(expectedControl(aliquot[0], aliquot[2].toLowerCase())));
                one(mockControlQueriesDisease).updateControlForShippedBiospecimen(with(expectedControl(aliquot[0], aliquot[2].toLowerCase())));

                aliquotId++;
            }

            one(mockShippedBiospecimenQueries).addShippedBiospecimens(with(expectedShippedBiospecimens(aliquotsFromFile)));
            one(mockShippedBiospecimenQueries).addShippedBiospecimenElements(with(expectedShippedBiospecimenElements(aliquotsFromFile)));
            exactly(diseaseNames.size()).of(mockShippedBiospecimenQueriesDisease).addShippedBiospecimens(with(expectedShippedBiospecimens(aliquotsFromFile)));
            exactly(diseaseNames.size()).of(mockShippedBiospecimenQueriesDisease).addShippedBiospecimenElements(with(expectedShippedBiospecimenElements(aliquotsFromFile)));


        }});

        biospecimenIdLoader.loadBiospecimenIds(fakeFile, fakeCenter, diseaseNames);

    }

    private static Matcher<Control> expectedControl(final String barcode, final String uuid) {
        return new org.hamcrest.TypeSafeMatcher<Control>() {
            @Override
            public boolean matchesSafely(final Control control) {
                return control.getAliquotsToDiseases().getBcrAliquotBarcode().getValue().equals(barcode) &&
                        control.getAliquotsToDiseases().getBcrAliquotUuid().getValue().endsWith(uuid);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("control that has barcode " + barcode + " and uuid " + uuid);
            }
        };
    }


    private static Matcher<List<ShippedBiospecimenElement>> expectedShippedBiospecimenElements(final List<String[]> aliquotsFromFile) {
        return new TypeSafeMatcher<List<ShippedBiospecimenElement>>() {
            @Override
            public boolean matchesSafely(final List<ShippedBiospecimenElement> elementList) {
                int elementNum = 0;
                for (final String[] aliquot : aliquotsFromFile) {
                    java.util.regex.Matcher m = CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(aliquot[0]);
                    if (m.matches()) {
                        final String sampleTypeCode = m.group(CommonBarcodeAndUUIDValidatorImpl.SAMPLE_TYPE_CODE_GROUP);
                        final String sampleSequence = m.group(CommonBarcodeAndUUIDValidatorImpl.SAMPLE_NUMBER_GROUP);
                        final String portionSequence = m.group(CommonBarcodeAndUUIDValidatorImpl.PORTION_NUMBER_GROUP);
                        final String analyteTypeCode = m.group(CommonBarcodeAndUUIDValidatorImpl.PORTION_ANALYTE_GROUP);
                        final String plateId = m.group(CommonBarcodeAndUUIDValidatorImpl.PLATE_ID_GROUP);

                        assertEquals(sampleTypeCode, elementList.get(elementNum++).getElementValue());
                        assertEquals(sampleSequence, elementList.get(elementNum++).getElementValue());
                        assertEquals(portionSequence, elementList.get(elementNum++).getElementValue());
                        assertEquals(analyteTypeCode, elementList.get(elementNum++).getElementValue());
                        assertEquals(plateId, elementList.get(elementNum++).getElementValue());
                    } else {
                        fail(aliquot[0]);
                    }
                }
                return true;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("checks for expected shipped biospecimen elements");
            }
        };
    }

    private static Matcher<List<ShippedBiospecimen>> expectedShippedBiospecimens(final List<String[]> aliquotsFromFile) {
        return new TypeSafeMatcher<List<ShippedBiospecimen>>() {
            @Override
            public boolean matchesSafely(final List<ShippedBiospecimen> shippedBiospecimens) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                for (int i=0; i<aliquotsFromFile.size(); i++) {
                    ShippedBiospecimen shippedBiospecimen = shippedBiospecimens.get(i);
                    assertEquals(new Long(i+1), shippedBiospecimen.getShippedBiospecimenId());
                    assertEquals(aliquotsFromFile.get(i)[0], shippedBiospecimen.getBarcode());
                    assertEquals(aliquotsFromFile.get(i)[2].toLowerCase(), shippedBiospecimen.getUuid());
                    if (shippedBiospecimen.getShippedDate() == null) {
                        assertNull(aliquotsFromFile.get(i)[1]);
                    } else {
                        assertEquals(aliquotsFromFile.get(i)[1], sdf.format(shippedBiospecimen.getShippedDate()));
                    }
                }

                return true;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("check things");
            }
        };
    }

    private static Matcher<BCRID> expectedBcrId(final String barcode, final String uuid) {
        return new TypeSafeMatcher<BCRID>() {
            @Override
            public boolean matchesSafely(final BCRID bcrid) {
                assertEquals(barcode, bcrid.getFullID());
                assertEquals(uuid, bcrid.getUUID());
                return barcode.equals(bcrid.getFullID()) && uuid.equals(bcrid.getUUID());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("matches barcode " + barcode);
            }
        };
    }
}
