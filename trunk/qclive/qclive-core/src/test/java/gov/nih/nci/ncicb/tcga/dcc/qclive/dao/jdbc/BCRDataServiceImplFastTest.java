package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DateUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * BCRDataServiceImpl unit test
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */


@RunWith(JMock.class)
public class BCRDataServiceImplFastTest {

    private Mockery mockery = new JUnit4Mockery();
    private BCRDataServiceImpl bcrDataService;
    private BCRIDProcessor mockCommonBcrIdProcessor;
    private BCRIDProcessor mockDiseaseBcrIdProcessor;
    private ShippedBiospecimenQueries mockCommonShippedBiospecimenQueries;
    private ShippedBiospecimenQueries mockDiseaseShippedBiospecimenQueries;


    @Before
    public void setUp() {

        mockCommonBcrIdProcessor = mockery.mock(BCRIDProcessor.class, "common");
        mockDiseaseBcrIdProcessor = mockery.mock(BCRIDProcessor.class, "disease");
        mockCommonShippedBiospecimenQueries = mockery.mock(ShippedBiospecimenQueries.class, "commonshippedbiospecimen");
        mockDiseaseShippedBiospecimenQueries = mockery.mock(ShippedBiospecimenQueries.class, "diseaseshippedbiospecimen");

        bcrDataService = new BCRDataServiceImpl();

        bcrDataService.setCommonBcrIdProcessor(mockCommonBcrIdProcessor);
        bcrDataService.setDiseaseBcrIdProcessor(mockDiseaseBcrIdProcessor);
        bcrDataService.setCommonShippedBiospecimenQueries(mockCommonShippedBiospecimenQueries);
        bcrDataService.setDiseaseShippedBiospecimenQueries(mockDiseaseShippedBiospecimenQueries);


    }

    @Test
    public void testAddBioSpecimenToFileAssociations() {
        final List<BiospecimenToFile> biospecimenToFiles = new ArrayList<BiospecimenToFile>();
        final Tumor tumor = new Tumor();

        mockery.checking(new Expectations() {{
              one(mockCommonBcrIdProcessor).addBioSpecimenToFileAssociations(biospecimenToFiles, tumor);
              one(mockDiseaseBcrIdProcessor).addBioSpecimenToFileAssociations(biospecimenToFiles, tumor);
          }});

          bcrDataService.addBioSpecimenToFileAssociations(biospecimenToFiles,tumor);
    }

    @Test
    public void testFindAllShippedPortionsInFile()throws IOException, SAXException, ParserConfigurationException, TransformerException, XPathExpressionException, ParseException {
        final List<ShippedBiospecimen> shippedBiospecimens = new ArrayList<ShippedBiospecimen>();
        final File file = new File("test");

        mockery.checking(new Expectations() {{
              one(mockCommonBcrIdProcessor).findAllShippedPortionsInFile(file);
              will(returnValue(shippedBiospecimens));

          }});

          bcrDataService.findAllShippedPortionsInFile(file);
    }


    @Test
    public void testGetShippedItemId(){

        mockery.checking(new Expectations() {{
              one(mockCommonShippedBiospecimenQueries).getShippedItemId(ShippedBiospecimen.SHIPPED_ITEM_NAME_PORTION);
              will(returnValue(2));
          }});

          bcrDataService.getShippedItemId(ShippedBiospecimen.SHIPPED_ITEM_NAME_PORTION);
    }


    @Test
    public void testAddShippedBiospecimens() throws ParseException {

        final String shippedDateAsString = "2012-01-11";
        final Date expectedShippedDate = DateUtils.makeDate(shippedDateAsString);

        final List<ShippedBiospecimen> shippedBiospecimens = getShippedBiospecimen(expectedShippedDate);
        final Integer shippedItemId = new Integer(10);

        mockery.checking(new Expectations() {{
            one(mockCommonShippedBiospecimenQueries).addShippedBiospecimens(shippedBiospecimens,shippedItemId);
            one(mockDiseaseShippedBiospecimenQueries).addShippedBiospecimens(shippedBiospecimens,shippedItemId);
            one(mockCommonShippedBiospecimenQueries).addShippedBiospecimenElements(with(expectedBiospecimenElements()));
            one(mockDiseaseShippedBiospecimenQueries).addShippedBiospecimenElements(with(expectedBiospecimenElements()));

        }});
        bcrDataService.addShippedBiospecimens(shippedBiospecimens, shippedItemId);

    }

    @Test
    public void testAddFileRelationships() {
        final List<Long>  biospecimenIds = new ArrayList<Long>();
        final Long fileId = 1000l;

        mockery.checking(new Expectations() {{
              one(mockCommonShippedBiospecimenQueries).addFileRelationships(biospecimenIds, fileId);
              one(mockDiseaseShippedBiospecimenQueries).addFileRelationships(biospecimenIds, fileId);
          }});

          bcrDataService.addShippedBiospecimensFileRelationship(biospecimenIds, fileId);
    }


    @Test
    public void testAddArchiveRelationship() {
        final Long  biospecimenId = 12l;
        final Long archiveId = 1000l;

        mockery.checking(new Expectations() {{
              one(mockCommonShippedBiospecimenQueries).addArchiveRelationship(biospecimenId, archiveId);
              one(mockDiseaseShippedBiospecimenQueries).addArchiveRelationship(biospecimenId, archiveId);
          }});

          bcrDataService.addArchiveRelationship(biospecimenId, archiveId);
    }

    @Test
    public void addArchiveRelationships() {
        final List<Long>  biospecimenIds = new ArrayList<Long>();
        final Long archiveId = 1000l;

        mockery.checking(new Expectations() {{
              one(mockCommonShippedBiospecimenQueries).addArchiveRelationships(biospecimenIds, archiveId);
              one(mockDiseaseShippedBiospecimenQueries).addArchiveRelationships(biospecimenIds, archiveId);
          }});

          bcrDataService.addShippedBiospecimensArchiveRelationship(biospecimenIds, archiveId);
    }

    @Test
    public void testFindAllAliquotsInFile() throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException{
        final File file = new File("test");
        final List<String[]> aliquots = new ArrayList<String[]>();

        mockery.checking(new Expectations() {{
              one(mockCommonBcrIdProcessor).findAllAliquotsInFile(file);
              will(returnValue(aliquots));
          }});

          bcrDataService.findAllAliquotsInFile(file);
    }

    @Test
    public void handleShippedBiospecimens()  throws ParseException, UUIDException{
        final Archive archive = new Archive();
        final Tumor tumor = new Tumor();
        final Center center = new Center();
        archive.setTheCenter(center);
        archive.setTheTumor(tumor);
        final Map<String,Long> fileIdbyName = new HashMap<String,Long>();
        fileIdbyName.put("test",1l);
        archive.setFilenameToIdMap(fileIdbyName);
        archive.setId(1l);

        final File xmlFile = new File("test");

        final String shippedDateAsString = "2012-01-11";
        final Date expectedShippedDate = DateUtils.makeDate(shippedDateAsString);

        final List<ShippedBiospecimen> shippedBiospecimens = getShippedBiospecimen(expectedShippedDate);
        final Integer shippedItemId = new Integer(10);

        mockery.checking(new Expectations() {{
            one(mockCommonShippedBiospecimenQueries).addShippedBiospecimens(with(expectedBiospecimen(expectedShippedDate)),with(any(Integer.class)));
            one(mockDiseaseShippedBiospecimenQueries).addShippedBiospecimens(with(expectedBiospecimen(expectedShippedDate)),with(any(Integer.class)));

            one(mockCommonShippedBiospecimenQueries).addShippedBiospecimenElements(with(expectedBiospecimenElements()));
            one(mockDiseaseShippedBiospecimenQueries).addShippedBiospecimenElements(with(expectedBiospecimenElements()));
            one(mockCommonShippedBiospecimenQueries).addArchiveRelationships(with(expectedBiospecimenIds()),with(expectedIds()));
            one(mockDiseaseShippedBiospecimenQueries).addArchiveRelationships(with(expectedBiospecimenIds()),with(expectedIds()));

            one(mockCommonShippedBiospecimenQueries).addFileRelationships(with(expectedBiospecimenIds()),with(expectedIds()));
            one(mockDiseaseShippedBiospecimenQueries).addFileRelationships(with(expectedBiospecimenIds()),with(expectedIds()));


        }});
        bcrDataService.handleShippedBiospecimens(shippedBiospecimens, shippedItemId,archive,xmlFile);

    }

    @Test
    public void handleAliquotBarcode() throws ParseException, UUIDException{
        final String barcode = "TCGA-A3-1234-01A-02D-6789-20";
		final BCRID bcrID = parseAliquotBarcode(barcode);
        final int[] biospecimenToArchiveId = new int[1];
        final Archive archive = new Archive();
        final Tumor tumor = new Tumor();
        final Center center = new Center();
        archive.setTheCenter(center);
        archive.setTheTumor(tumor);
        final File xmlFile = new File("test");

        final String shippedDateAsString = "2012-01-11";
        final Date expectedShippedDate = DateUtils.makeDate(shippedDateAsString);

        bcrID.setId(100);
        bcrID.setUUID(UUID.randomUUID().toString());
        bcrID.setArchiveId(1l);
        bcrID.setShippingDate(shippedDateAsString);

        final Map<String,Long> fileIdbyName = new HashMap<String,Long>();
        fileIdbyName.put("test",1l);
        archive.setFilenameToIdMap(fileIdbyName);
        archive.setId(1l);
        mockery.checking(new Expectations() {{
            one(mockCommonBcrIdProcessor).storeBcrBarcode(with(any(BCRID.class)),with(any(Boolean.class)),with(any( (new int[1]).getClass())),with(any(Tumor.class)),with(any(Center.class)));
            one(mockDiseaseBcrIdProcessor).storeBcrBarcode(with(any(BCRID.class)),with(any(Boolean.class)),with(any((new int[1]).getClass())),with(any(Tumor.class)),with(any(Center.class)));
            one(mockCommonShippedBiospecimenQueries).addShippedBiospecimens(with(expectedBiospecimen(expectedShippedDate)),with(any(Integer.class)));
            one(mockDiseaseShippedBiospecimenQueries).addShippedBiospecimens(with(expectedBiospecimen(expectedShippedDate)),with(any(Integer.class)));

            one(mockCommonShippedBiospecimenQueries).addShippedBiospecimenElements(with(expectedBiospecimenElements()));
            one(mockDiseaseShippedBiospecimenQueries).addShippedBiospecimenElements(with(expectedBiospecimenElements()));
            one(mockCommonShippedBiospecimenQueries).addArchiveRelationships(with(expectedBiospecimenIds()),with(expectedIds()));
            one(mockDiseaseShippedBiospecimenQueries).addArchiveRelationships(with(expectedBiospecimenIds()),with(expectedIds()));

            one(mockCommonShippedBiospecimenQueries).addFileRelationships(with(expectedBiospecimenIds()),with(expectedIds()));
            one(mockDiseaseShippedBiospecimenQueries).addFileRelationships(with(expectedBiospecimenIds()),with(expectedIds()));


        }});
        bcrDataService.handleAliquotBarcode(bcrID, archive,xmlFile);

    }


    @Test
    public void testStoreBarcode() throws ParseException, UUIDException{
        final String barcode = "TCGA-A3-1234-01A-02D-6789-20";
		final BCRID bcrID = parseAliquotBarcode(barcode);
        final Integer bcrIdFromCommon = 100;
        final Tumor tumor = new Tumor();


        mockery.checking(new Expectations() {{
            one(mockCommonBcrIdProcessor).storeBarcode(bcrID,false,-1,tumor);
            one(mockDiseaseBcrIdProcessor).storeBarcode(bcrID,true,bcrIdFromCommon,tumor);


        }});
        bcrDataService.storeBarcode(bcrID, bcrIdFromCommon,tumor);

    }

    @Test
    public void testMakeShippedBiospecimenFromAliquot() throws ParseException {
        final BCRID aliquot = new BCRID();
        aliquot.setId(123);
        aliquot.setFullID("TCGA-AV-1234-01W-01A-4567-20");
        aliquot.setProjectName("TCGA");
        aliquot.setSiteID("AV");
        aliquot.setPatientID("1234");
        aliquot.setSampleID("01W");
        aliquot.setSampleNumberCode("01");
        aliquot.setSampleTypeCode("W");
        aliquot.setPortionID("01A");
        aliquot.setPortionNumber("01");
        aliquot.setPortionTypeCode("A");
        aliquot.setPlateId("4567");
        aliquot.setBcrCenterId("20");

        aliquot.setShippingDate("2010-10-12");
        aliquot.setBatchNumber(35);

        final ShippedBiospecimen shippedBiospecimen = bcrDataService.makeShippedBiospecimenFromAliquot(aliquot);

        assertEquals(ShippedBiospecimen.SHIPPED_ITEM_NAME_ALIQUOT, shippedBiospecimen.getShippedBiospecimenType());
        assertEquals(aliquot.getFullID(), shippedBiospecimen.getBarcode());
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(sdf.parse(aliquot.getShippingDate()), shippedBiospecimen.getShippedDate());
        assertEquals(aliquot.getBatchNumber(), shippedBiospecimen.getBatchNumber());

        assertEquals(aliquot.getProjectName(), shippedBiospecimen.getProjectCode());
        assertEquals(aliquot.getSiteID(), shippedBiospecimen.getTssCode());
        assertEquals(aliquot.getPatientID(), shippedBiospecimen.getParticipantCode());
        assertEquals(aliquot.getSampleNumberCode(), shippedBiospecimen.getSampleSequence());
        assertEquals(aliquot.getSampleTypeCode(), shippedBiospecimen.getSampleTypeCode());
        assertEquals(aliquot.getPortionNumber(), shippedBiospecimen.getPortionSequence());
        assertEquals(aliquot.getPortionTypeCode(), shippedBiospecimen.getAnalyteTypeCode());
        assertEquals(aliquot.getPlateId(), shippedBiospecimen.getPlateId());
        assertEquals(aliquot.getBcrCenterId(), shippedBiospecimen.getBcrCenterId());
    }

    private static Matcher<List<ShippedBiospecimen>> expectedBiospecimen(final Date expectedShippedDate) {
        return new TypeSafeMatcher<List<ShippedBiospecimen>>() {

            public boolean matchesSafely(final List<ShippedBiospecimen> actualShippedBiospecimens) {
                if (actualShippedBiospecimens.size() != 1) {
                    return false;
                }
                if(!"TCGA-A3-1234-01A-02D-6789-20".equals(actualShippedBiospecimens.get(0).getBarcode())){
                    return false;
                }
                if(!"TCGA".equals(actualShippedBiospecimens.get(0).getProjectCode())){
                    return false;
                }
                if(!"A3".equals(actualShippedBiospecimens.get(0).getTssCode())){
                    return false;
                }
                if(!"1234".equals(actualShippedBiospecimens.get(0).getParticipantCode())){
                    return false;
                }
                if(!"20".equals(actualShippedBiospecimens.get(0).getBcrCenterId())){
                    return false;
                }
                if(!"Aliquot".equals(actualShippedBiospecimens.get(0).getShippedBiospecimenType())){
                    return false;
                }

                final Date shippedDate = actualShippedBiospecimens.get(0).getShippedDate();
                if(!shippedDate.equals(expectedShippedDate)) {
                    return false;
                }

                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("biospecimen ");
            }
        };
    }


    private static Matcher<Long> expectedIds() {
        return new TypeSafeMatcher<Long>() {

            public boolean matchesSafely(final Long id) {
                if(id.equals(1l))
                    return true;
                return false;
            }

            public void describeTo(final Description description) {
                description.appendText("biospecimen elements");
            }
        };
    }

    private static Matcher<List<Long>> expectedBiospecimenIds() {
        return new TypeSafeMatcher<List<Long>>() {

            public boolean matchesSafely(final List<Long> ids) {
                if (ids.size() != 1) {
                    return false;
                }
                if(ids.get(0).equals(new Long(100)))
                    return true;
                return false;
            }

            public void describeTo(final Description description) {
                description.appendText("biospecimen elements");
            }
        };
    }

    private static Matcher<List<ShippedBiospecimenElement>> expectedBiospecimenElements() {
        return new TypeSafeMatcher<List<ShippedBiospecimenElement>>() {

            public boolean matchesSafely(final List<ShippedBiospecimenElement> shippedBiospecimenElements) {
                if (shippedBiospecimenElements.size() != 5) {
                    return false;
                }
                final List<String> expectedElementNames = Arrays.asList("sample_type_code","sample_sequence","portion_sequence","analyte_code","plate_id");
                final List<String> actualElementNames = new ArrayList<String>();
                for(final ShippedBiospecimenElement shippedBiospecimenElement: shippedBiospecimenElements){
                    actualElementNames.add(shippedBiospecimenElement.getElementName());
                }
                if(!expectedElementNames.containsAll(actualElementNames)){
                    return false;
                }

                return true;
            }

            public void describeTo(final Description description) {
                description.appendText("biospecimen elements");
            }
        };
    }

    /**
     * Return a {@link List} of 1 {@link ShippedBiospecimen} with the given shipped date
     *
     * @param shippedDate the shipped date
     * @return a {@link List} of 1 {@link ShippedBiospecimen} with the given shipped date
     */
    private List<ShippedBiospecimen> getShippedBiospecimen(final Date shippedDate){

        ShippedBiospecimen aliquot = new ShippedBiospecimen();
        aliquot.setUuid("uuid-60");
        aliquot.setBarcode("TCGA-A3-1234-01A-02D-6789-20");
        // type set, but not type id, so DAO has to look it up
        aliquot.setShippedBiospecimenType(ShippedBiospecimen.SHIPPED_ITEM_NAME_ALIQUOT);
        aliquot.setProjectCode("TCGA");
        aliquot.setTssCode("A3");
        aliquot.setParticipantCode("1234");
        aliquot.setSampleTypeCode("01");
        aliquot.setSampleSequence("A");
        aliquot.setPortionSequence("02");
        aliquot.setAnalyteTypeCode("D");
        aliquot.setPlateId("6789");
        aliquot.setBcrCenterId("20");
        aliquot.setShippedBiospecimenId(100l);
        aliquot.setShippedDate(shippedDate);

        return Arrays.asList(aliquot);
    }

    public BCRID parseAliquotBarcode(final String aliquotBarcode) throws ParseException {
        final BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl();
        return bcridProcessor.parseAliquotBarcode(aliquotBarcode);
    }

}


