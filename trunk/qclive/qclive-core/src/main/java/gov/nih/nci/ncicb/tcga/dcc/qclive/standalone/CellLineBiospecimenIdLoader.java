package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ControlQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataSourceMaker;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseRoutingDataSource;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAOImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.CenterQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ControlQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ShippedBiospecimenQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.TumorQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.UUIDTypeQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.AliquotsToDiseases;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.BcrAliquotBarcode;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.BcrUuid;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.Control;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.ControlElementType;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolverImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.BCRDataServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.BCRIDQueriesImpl;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is used to load cell-line control aliquot IDs (barcodes and UUIDs) into the DCC databases.  It saves
 * the aliquots to dccCommon and all disease schemas, based on a list passed into the main method.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CellLineBiospecimenIdLoader {


    /**
     * Main method for running this loader.  The arguments should be:
     * 1) database URL (jdbc)
     * 2) file containing db properties, where first line has commonUsername/commonPassword, and the second line has all
     * disease schemas, comma-separated disease abbrev and username/password, such as "GBM:gbmUser/pwd,LAML:lamlUser/pwd"
     * 3) location of directory containing biospecimen XML files containing the Cell Line Control information
     *
     * The output will print the number of aliquots added to the database for each XML file found.
     *
     * @param args list of arguments: db url, properties file, and input directory
     */
    public static void main(String[] args) {
        final String dbUrl = args[0];
        final String dbProperties = args[1];

        final String fileDirStr = args[2];
        File fileDir = new File(fileDirStr);
        if (! fileDir.exists()) {
            System.out.println("Cannot read " + fileDirStr);
            System.exit(1);
        }

        final String[] xmlFileNames = fileDir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String fileName) {
                return fileName.endsWith(".xml");
            }
        });

        final List<File> xmlFiles = new ArrayList<File>();
        for (final String xmlFileName : xmlFileNames) {
            xmlFiles.add(new File(fileDir, xmlFileName));
        }

        try {
            FileReader reader = new FileReader(dbProperties);
            BufferedReader bufferedReader = new BufferedReader(reader);
            final String firstLine = bufferedReader.readLine();
            final String secondLine = bufferedReader.readLine();

            // set up datasource for common
            String[] commonUsernameAndPassword = firstLine.split("/");
            SingleConnectionDataSource commonDataSource = new SingleConnectionDataSource(dbUrl, commonUsernameAndPassword[0], commonUsernameAndPassword[1], true);

            // set up a DiseaseRoutingDataSource using the disease schema info
            DataSourceMaker dataSourceMaker = new DataSourceMaker() {
                @Override
                public DataSource makeDataSource(final String propertyString) {
                    final String[] usernameAndPassword = propertyString.split("/");
                    return new SingleConnectionDataSource(dbUrl, usernameAndPassword[0], usernameAndPassword[1], true);
                }
            };
            DiseaseRoutingDataSource diseaseRoutingDataSource = new DiseaseRoutingDataSource(dataSourceMaker, secondLine);
            diseaseRoutingDataSource.afterPropertiesSet();

            // now set up the loader
            CellLineBiospecimenIdLoader biospecimenIdLoader = new CellLineBiospecimenIdLoader();

            BCRDataServiceImpl bcrDataService = new BCRDataServiceImpl();
            BCRIDProcessor bcridProcessor = new BCRIDProcessorImpl();
            bcridProcessor.setAliquotElementXPath("//aliquots/aliquot");
            bcridProcessor.setAliquotBarcodeElement("bcr_aliquot_barcode");
            bcridProcessor.setAliquotUuidElement("bcr_aliquot_uuid");
            bcridProcessor.setShipDayElement("day_of_shipment");
            bcridProcessor.setShipMonthElement("month_of_shipment");
            bcridProcessor.setShipYearElement("year_of_shipment");
            bcrDataService.setCommonBcrIdProcessor(bcridProcessor);
            biospecimenIdLoader.setBcrDataService(bcrDataService);

            BCRIDQueriesImpl commonBcrIdQueries = new BCRIDQueriesImpl();
            commonBcrIdQueries.setDataSource(commonDataSource);
            biospecimenIdLoader.setBcridQueries(commonBcrIdQueries);

            BCRIDQueriesImpl diseaseBrcIdQueries = new BCRIDQueriesImpl();
            diseaseBrcIdQueries.setDataSource(diseaseRoutingDataSource);
            biospecimenIdLoader.setBcridQueriesDisease(diseaseBrcIdQueries);

            ShippedBiospecimenQueriesJDBCImpl commonShippedBiospecimenQueries = new ShippedBiospecimenQueriesJDBCImpl();
            commonShippedBiospecimenQueries.setDataSource(commonDataSource);
            biospecimenIdLoader.setShippedBiospecimenQueries(commonShippedBiospecimenQueries);

            ShippedBiospecimenQueriesJDBCImpl diseaseShippedBiospecimenQueries = new ShippedBiospecimenQueriesJDBCImpl();
            diseaseShippedBiospecimenQueries.setDataSource(diseaseRoutingDataSource);
            biospecimenIdLoader.setShippedBiospecimenQueriesDisease(diseaseShippedBiospecimenQueries);

            BarcodeUuidResolverImpl barcodeUuidResolver = new BarcodeUuidResolverImpl();
            barcodeUuidResolver.setBarcodeAndUUIDValidator(new CommonBarcodeAndUUIDValidatorImpl());
            UUIDDAOImpl uuiddao = new UUIDDAOImpl();
            uuiddao.setDataSource(commonDataSource);
            barcodeUuidResolver.setUuidDAO(uuiddao);
            uuiddao.setCommonBarcodeAndUUIDValidator(new CommonBarcodeAndUUIDValidatorImpl());
            UUIDServiceImpl uuidService = new UUIDServiceImpl();
            uuidService.setUuidDAO(uuiddao);
            UUIDTypeQueriesJDBCImpl uuidTypeQueries = new UUIDTypeQueriesJDBCImpl();
            uuidTypeQueries.setDataSource(commonDataSource);
            uuiddao.setUuidTypeQueries(uuidTypeQueries);
            CenterQueriesJDBCImpl centerQueries = new CenterQueriesJDBCImpl();
            centerQueries.setDataSource(commonDataSource);
            uuidService.setCenterQueries(centerQueries);
            uuidService.setTumorQueries(null);
            barcodeUuidResolver.setUuidService(uuidService);

            biospecimenIdLoader.setBarcodeUuidResolver(barcodeUuidResolver);

            ControlQueriesJDBCImpl controlQueries = new ControlQueriesJDBCImpl();
            TumorQueriesJDBCImpl tumorQueries = new TumorQueriesJDBCImpl();
            tumorQueries.setDataSource(commonDataSource);
            controlQueries.setTumorQueries(tumorQueries);
            controlQueries.setDataSource(commonDataSource);

            ControlQueriesJDBCImpl controlQueriesDisease = new ControlQueriesJDBCImpl();
            controlQueriesDisease.setDataSource(diseaseRoutingDataSource);
            TumorQueriesJDBCImpl tumorQueriesDisease = new TumorQueriesJDBCImpl();
            tumorQueriesDisease.setDataSource(diseaseRoutingDataSource);
            controlQueriesDisease.setTumorQueries(tumorQueriesDisease);

            biospecimenIdLoader.setControlQueries(controlQueries);
            biospecimenIdLoader.setControlQueriesDisease(controlQueriesDisease);

            // for each XML file, call the loader
            for (final File xmlFile : xmlFiles) {
                Center bcrCenter = null;

                // center is needed for registering UUIDs that are new to the DCC system
                if (xmlFile.getName().contains("nationwide")) {
                    bcrCenter = centerQueries.getCenterByName("nationwidechildrens.org", "BCR");
                } else if (xmlFile.getName().contains("intgen")) {
                    bcrCenter = centerQueries.getCenterByName("intgen.org", "BCR");
                }

                if (bcrCenter != null) {
                    biospecimenIdLoader.loadBiospecimenIds(xmlFile, bcrCenter, diseaseRoutingDataSource.getDiseaseNames());
                } else {
                    System.out.println("Unable to find BCR center for file " + xmlFile.getName());
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (UUIDException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }



    private BCRDataService bcrDataService;
    private ShippedBiospecimenQueries shippedBiospecimenQueries, shippedBiospecimenQueriesDisease;
    private BCRIDQueries bcridQueries, bcridQueriesDisease;
    private ControlQueries controlQueries, controlQueriesDisease;
    private BarcodeUuidResolver barcodeUuidResolver;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Parses aliquots from an XML file and loads them into biospecimen_barcode and shipped_biospecimen tables.
     *
     * @param biospecimenXmlFile the XML file containing the aliquots
     * @param bcrCenter the Center representing the BCR
     * @param diseaseNames all the diseases that the IDs should be loaded into
     *
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws ParseException
     * @throws UUIDException
     */
    public void loadBiospecimenIds(final File biospecimenXmlFile, final Center bcrCenter, final Set<Object> diseaseNames) throws TransformerException, IOException, SAXException, XPathExpressionException, ParserConfigurationException, ParseException, UUIDException, JAXBException {
        final List<String[]> aliquots = bcrDataService.findAllAliquotsInFile(biospecimenXmlFile);
        final List<ShippedBiospecimen> shippedBiospecimens = new ArrayList<ShippedBiospecimen>();
        final List<Control> controls = new ArrayList<Control>();

        final Tumor controlDisease = new Tumor(31, "CNTL");
        for (final String[] aliquotData : aliquots) {
            final String barcode = aliquotData[0];
            final String uuid = aliquotData[2].toLowerCase();
            try {

                barcodeUuidResolver.resolveBarcodeAndUuid(barcode, uuid, controlDisease, bcrCenter, false);

                if (uuid == null) {
                    // skip if UUID not found and could not be created (not expected)
                    System.out.println("No UUID given for " + barcode);
                } else {
                    // 1. make BCRID bean
                    final BCRID bcrid = bcrDataService.parseAliquotBarcode(barcode);
                    bcrid.setShippingDate(aliquotData[1]);
                    bcrid.setUUID(uuid);

                    // 2. check if already saved in db
                    int biospecimenId = bcridQueries.exists(bcrid);
                    if (biospecimenId == -1) {
                        // 3. if not, add it
                        biospecimenId = bcridQueries.addBCRID(bcrid, false);
                        bcrid.setId(biospecimenId);

                        // 4. and also for each disease
                        for (final Object disease : diseaseNames) {
                            DiseaseContextHolder.setDisease(disease.toString());
                            bcridQueriesDisease.addBCRID(bcrid, true);
                        }
                    } else {
                        bcrid.setId(biospecimenId);
                    }

                    ShippedBiospecimen shippedBiospecimen = makeShippedBiospecimenFromAliquot(bcrid);
                    shippedBiospecimen.setUuid(uuid);
                    shippedBiospecimens.add(shippedBiospecimen);

                    Control control = makeControlFromShippedBiospecimen(shippedBiospecimen);
                    controls.add(control);
                }

            } catch (UUIDException e) {
                System.out.println("Barcode/UUID conflict: " + e.getMessage());
            }

        }

        // 6. make list of all shipped_biospecimen_elements to save
        List<ShippedBiospecimenElement> allElements = new ArrayList<ShippedBiospecimenElement>();
        for (final ShippedBiospecimen shippedBiospecimen : shippedBiospecimens) {
            allElements.addAll(shippedBiospecimen.getShippedBiospecimenElements());
        }

        // 7. save shipped biospecimens
        shippedBiospecimenQueries.addShippedBiospecimens(shippedBiospecimens);
        // 8. save shipped biospecimen elements
        shippedBiospecimenQueries.addShippedBiospecimenElements(allElements);



        // 9. now repeat for disease schemas
        for (final Object disease : diseaseNames) {
            DiseaseContextHolder.setDisease(disease.toString());
            shippedBiospecimenQueriesDisease.addShippedBiospecimens(shippedBiospecimens);
            shippedBiospecimenQueriesDisease.addShippedBiospecimenElements(allElements);
        }

        // 10. now add/update control records.  note no control_to_disease records will be added because we don't know yet.
        for (final Control control : controls) {
            controlQueries.persistControl(control);
            controlQueries.updateControlForShippedBiospecimen(control);
            controlQueriesDisease.updateControlForShippedBiospecimen(control);
        }

        // that's it!
        System.out.println("Processed " + shippedBiospecimens.size() + " aliquots from " + biospecimenXmlFile.getName());
    }

    private Control makeControlFromShippedBiospecimen(final ShippedBiospecimen shippedBiospecimen) {
        final Control control = new Control();
        control.setControlElement(ControlElementType.CELL_LINE_CONTROL);
        final AliquotsToDiseases aliquotsToDiseases = new AliquotsToDiseases();
        final BcrAliquotBarcode bcrAliquotBarcode = new BcrAliquotBarcode();
        bcrAliquotBarcode.setValue(shippedBiospecimen.getBarcode());
        aliquotsToDiseases.setBcrAliquotBarcode(bcrAliquotBarcode);
        final BcrUuid bcrUuid = new BcrUuid();
        bcrUuid.setValue(shippedBiospecimen.getUuid());
        aliquotsToDiseases.setBcrAliquotUuid(bcrUuid);
        control.setAliquotsToDiseases(aliquotsToDiseases);
        return control;
    }


    private ShippedBiospecimen makeShippedBiospecimenFromAliquot(final BCRID aliquot) throws ParseException {

        ShippedBiospecimen shippedBiospecimen = new ShippedBiospecimen();
        shippedBiospecimen.setShippedBiospecimenType(ShippedBiospecimen.SHIPPED_ITEM_NAME_ALIQUOT);
        shippedBiospecimen.setShippedBiospecimenId((long) aliquot.getId());
        shippedBiospecimen.setUuid(aliquot.getUUID());
        shippedBiospecimen.setBarcode(aliquot.getFullID());

        shippedBiospecimen.setProjectCode(aliquot.getProjectName());
        shippedBiospecimen.setTssCode(aliquot.getSiteID());
        shippedBiospecimen.setParticipantCode(aliquot.getPatientID());
        shippedBiospecimen.setSampleSequence(aliquot.getSampleNumberCode());
        shippedBiospecimen.setSampleTypeCode(aliquot.getSampleTypeCode());
        shippedBiospecimen.setPortionSequence(aliquot.getPortionNumber());
        shippedBiospecimen.setAnalyteTypeCode(aliquot.getPortionTypeCode());
        shippedBiospecimen.setPlateId(aliquot.getPlateId());
        shippedBiospecimen.setBcrCenterId(aliquot.getBcrCenterId());

        if (aliquot.getShippingDate() != null) {
            shippedBiospecimen.setShippedDate(simpleDateFormat.parse(aliquot.getShippingDate()));
        }

        return shippedBiospecimen;
    }

    public void setBcrDataService(final BCRDataService bcrDataService) {
        this.bcrDataService = bcrDataService;
    }

    public void setShippedBiospecimenQueries(final ShippedBiospecimenQueries shippedBiospecimenQueries) {
        this.shippedBiospecimenQueries = shippedBiospecimenQueries;
    }

    public void setBarcodeUuidResolver(final BarcodeUuidResolver barcodeUuidResolver) {
        this.barcodeUuidResolver = barcodeUuidResolver;
    }

    public void setShippedBiospecimenQueriesDisease(final ShippedBiospecimenQueries shippedBiospecimenQueriesDisease) {
        this.shippedBiospecimenQueriesDisease = shippedBiospecimenQueriesDisease;
    }

    public void setBcridQueries(final BCRIDQueries bcridQueries) {
        this.bcridQueries = bcridQueries;
    }

    public void setBcridQueriesDisease(final BCRIDQueries bcridQueriesDisease) {
        this.bcridQueriesDisease = bcridQueriesDisease;
    }

    public void setControlQueries(final ControlQueries controlQueries) {
        this.controlQueries = controlQueries;
    }

    public void setControlQueriesDisease(final ControlQueries controlQueriesDisease) {
        this.controlQueriesDisease = controlQueriesDisease;
    }
}
