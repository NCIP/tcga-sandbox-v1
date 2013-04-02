/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.SchemaException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DateComparator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DateUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DateUtilsException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ShippedPortionIdProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalTable;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.QCliveXMLSchemaValidator;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ClinicalDateObscurer.getXPathExpressionIgnoringNamespace;

/**
 * Validator for clinical XML files.
 *
 * - checks that the file parses with the included XSD
 * - for non Auxiliary files, checks that each barcode passes the QcLiveBarcodeAndUUIDValidatorImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ClinicalXmlValidator extends AbstractArchiveFileProcessor<Boolean> {

	public static final String XSD_EXTENSION = ".xsd";
	public static final String XML_EXTENSION = ".xml";

    /**
     * Namespace unaware xpath expression for clinical and biospecimen patient barcodes
     */
    public static final String PATIENT_BARCODE_XPATH_EXPRESSION = "//" + "patient" + "/" + "bcr_patient_barcode";

    private QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator;
	private BarcodeTumorValidator barcodeTumorValidator;
	private BCRIDProcessor bcridProcessor;
	private ClinicalLoaderQueries clinicalLoaderQueries;
	private CodeTableQueries codeTableQueries;
	private ShippedPortionIdProcessor shippedPortionIdProcessor;

	private String dayOfPrefix;
	private String monthOfPrefix;
	private String yearOfPrefix;
	private Boolean allowLocalSchema;

    private String shipmentPortionPath;
	private String bcrShipmentPortionUuidElementName;
    private String centerIdElementName;
    private String plateIdElementName;
    private String shipmentPortionBcrAliquotBarcodeElementName;  
	private QCliveXMLSchemaValidator qCliveXMLSchemaValidator;

    private BCRUtils bcrUtils;

	/**
	 * Regular expression pattern strings for validating XSD URLs in BCR XML
	 * files
	 **/
	private final String PROTOCOL_PATTERN = "http[s]?\\:\\/\\/";
	private final String PATH_PATTERN = "(\\/(\\w|\\W)+)*\\/";	
    
    /**
     * The list of the name's last part of the elements which dates need to be validated
     */
    private List<String> datesToValidate = new ArrayList<String>();

    /**
     * The list of <code>DateComparator</code>
     */
    private List<DateComparator> dateComparators = new ArrayList<DateComparator>();

    /**
     * The set of dates that need to be compared
     */
    private Set<String> datesToCompare = new HashSet<String>();

    private UUIDService uuidService;
    private boolean uuidsRequired = false;

    public void setDayOfPrefix(final String dayOfPrefix) {
        this.dayOfPrefix = dayOfPrefix;
    }

    public void setMonthOfPrefix(final String monthOfPrefix) {
        this.monthOfPrefix = monthOfPrefix;
    }

    public void setYearOfPrefix(final String yearOfPrefix) {
        this.yearOfPrefix = yearOfPrefix;
    }

    /**
     * Sets the dates to validate by parsing a comma-separated string.  For example, "BIRTH,DEATH,DIAGNOSIS".
     *
     * @param commaSeparatedDateNames list of date names separated by commas
     */
    public void setDatesToValidateString(final String commaSeparatedDateNames) {
        if (commaSeparatedDateNames != null) {
            String[] dates = commaSeparatedDateNames.split(",");
            datesToValidate = Arrays.asList(dates);
        }
    }

    /**
     * Sets dateComparators.
     * Also sets the dates to be compared by parsing a comma-separated String.
     * For example: "a==b,c<d,a<>d" will put a, b c and d in datesToCompare
     *
     * @param commaSeparatedDateComparator the comma-separated date comparison string
     * @throws Exception if the DateComparator constructor is given invalid arguments
     */
    public void setDateComparatorsString(final String commaSeparatedDateComparator) throws Exception {

        if(commaSeparatedDateComparator != null) {

            final String[] datesComparisons = commaSeparatedDateComparator.split(",");

            for(final String datesToCompare : datesComparisons) {

                final DateComparator dateComparator = new DateComparator(datesToCompare);
                getDateComparators().add(dateComparator);

                // Update datesToCompare property adding each operand name
                // (The set ensures there is no duplicate)
                getDatesToCompare().add(dateComparator.getLeftOperandName());
                getDatesToCompare().add(dateComparator.getRightOperandName());
            }
        }
    }

    /**
     * Set the list of the name's last part of the elements which dates need to be validated
     *
     * @param datesToValidate the list of the name's last part of the elements which dates need to be validated
     */
    public void setDatesToValidate(final List<String> datesToValidate) {
        this.datesToValidate = datesToValidate;
    }

    /**
     * Return the list of the name's last part of the elements which dates need to be validated
     *
     * @return the list of the name's last part of the elements which dates need to be validated
     */
    public List<String> getDatesToValidate() {
        return this.datesToValidate;
    }

    /**
     *
     * @param qcLiveBarcodeAndUUIDValidator the barcode validator to use
     */
    public ClinicalXmlValidator(final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
    }

    protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
        return !(results.values().contains(false));
    }

    protected Boolean processFile(final File xmlFile, final QcContext context) throws ProcessorException {
        context.setFile(xmlFile);
        Boolean valid;

        try {
            // need namespace-aware document for schema validation
            XPathXmlParser xPathXmlParser = new XPathXmlParser();
            Document document = xPathXmlParser.parseXmlFile(xmlFile, false, true);
            XPath xpath = xPathXmlParser.getXPath();

            valid = checkXsdVersion(xmlFile,document,context);

            valid &= validateSchema(xmlFile, document, context);

            if(!getBcrUtils().isAuxiliaryFile(xmlFile)) { // Following validation is for Clinical and Biospecimen files only

                // only new files get date check, because previous ones already are obscured
                if (!context.getFilesCopiedFromPreviousArchive().contains(xmlFile.getName())) {

                    final Map<String, Date> dateNameToValueMap = new HashMap<String, Date>();
                    final Map<String, String> datePrecision = new HashMap<String, String>();
                    valid = checkDateValidation(xmlFile, xpath, document, context, dateNameToValueMap, datePrecision) && valid;

                    if(valid) { // Only do the dates comparison check if the dates are valid in the first place
                        valid = checkDatesComparison(dateNameToValueMap, datePrecision, xmlFile, context) && valid;
                    }
                }

                // parse again, without namespace awareness for barcode/uuid checking
                xPathXmlParser = new XPathXmlParser();
                document = xPathXmlParser.parseXmlFile(xmlFile, false, false);
                xpath = xPathXmlParser.getXPath();

                // only check barcodes if the schema is valid
                if (valid) {

                    // Check that patient barcode in file is same as barcode in filename
                    valid &= checkPatientBarcode(xmlFile.getName(), document, xpath, context);

                    if (clinicalLoaderQueries == null) {
                        // if queries is null, this is stand-alone, so just validate the aliquot barcodes
                        valid = checkAliquotBarcodes(xmlFile, context) && valid;
                    } else {
                        // otherwise, do a full UUID validation
                        valid = checkAllBarcodesAndUuids(xmlFile, context, xpath, document) && valid;
                    }
                    valid = checkShippedPortion(xmlFile, context) && valid;
                }
            }
        }
        catch (IOException e) {
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                    xmlFile.getName(),
                    new StringBuilder().append("I/O error reading '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
        }
        catch (ParserConfigurationException e) {
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                    xmlFile.getName(),
                    new StringBuilder().append("Parser error reading '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
        }
        catch (SAXException e) {
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                    xmlFile.getName(),
                    new StringBuilder().append("Error in '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
        }
        catch (TransformerException e) {
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                    xmlFile.getName(),
                    new StringBuilder().append("Error transforming '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
        }
        catch (XPathExpressionException e) {
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                    xmlFile.getName(),
                    new StringBuilder().append("Error evaluating '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
        }catch(SchemaException e){
            valid = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                    xmlFile.getName(),
                    new StringBuilder().append("Error evaluating '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));

        }

        if (!valid) {
            context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
        }

        return valid;
    }

    /**
     * Return <code>true</code> if there is a valid patient barcode contained in the given filename and if it matches the patient barcode in the file content,
     * <code>false</code> otherwise.
     *
     * Note: the patient barcode is valid if it has the expected format. It is not checked against actual values in the database.
     *
     * @param filename the filename to validate
     * @param document the document to parse
     * @param xpath <code>XPath</code> for XML parsing without namespace awareness
     * @param context the context
     * @return <code>true</code> if there is a valid patient barcode contained in the given filename and if it matches the patient barcode in the file content,
     * <code>false</code> otherwise.
     */
    private Boolean checkPatientBarcode(final String filename, final Document document, final XPath xpath, final QcContext context) throws XPathExpressionException {

        Boolean result = true;

        final String patientBarcodeFromFilename = qcLiveBarcodeAndUUIDValidator.getPatientBarcode(filename);

        if(patientBarcodeFromFilename != null) {

            final NodeList patientBarcodeNodes = (NodeList) xpath.evaluate(PATIENT_BARCODE_XPATH_EXPRESSION, document, XPathConstants.NODESET);

            if (patientBarcodeNodes != null && patientBarcodeNodes.getLength() > 0) {

                if(patientBarcodeNodes.getLength() == 1) {

                    final String patientBarcodeFromFileContent = patientBarcodeNodes.item(0).getTextContent();

                    if(!patientBarcodeFromFileContent.equals(patientBarcodeFromFilename)) { // Patient barcodes don't match

                        result = false;
                        context.addError(MessageFormat.format(
                                MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                                filename,
                                new StringBuilder("The file '").append(filename).append("' has a patient barcode [").append(patientBarcodeFromFileContent)
                                        .append("] that does not match the patient barcode found in the filename [").append(patientBarcodeFromFilename).append("].")));
                    }

                } else { // More than 1 patient barcode in XML
                    //This shouldn't happen as long as the XSD requires that element to be unique, but this check is here in case that requirement is removed
                    result = false;
                    context.addError(MessageFormat.format(
                            MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                            filename,
                            new StringBuilder("The file '").append(filename).append("' has more than one patient barcode element.")));
                }

            } else { // No patient barcode in XML
                //This shouldn't happen as long as the XSD requires that element, but this check is here in case that requirement is removed
                result = false;
                context.addError(MessageFormat.format(
                        MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                        filename,
                        new StringBuilder("The file '").append(filename).append("' does not have any patient barcode element.")));
            }

        } else { // No patient barcode in filename

            result = false;
            context.addError(MessageFormat.format(
                    MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                    filename,
                    new StringBuilder("The filename '").append(filename).append("' does not contain a patient barcode.")));
        }

        return result;
    }

    /**
     * Compare the dates that need to be compared
     *
     * @param dateNameToValueMap a Map that holds the dates that need to be compared
     * @param datePrecision a map to hold precision of dates
     * @param xmlFile the xml file
     * @param context the context
     * @return <code>true</code> if all comparisons are successful, <code>false</code> otherwise
     */
    protected boolean checkDatesComparison(final Map<String, Date> dateNameToValueMap, final Map<String, String> datePrecision,
                                           final File xmlFile, final QcContext context) {

        boolean valid  = true;

        for(final DateComparator dateComparator : getDateComparators()) {
            valid = dateComparator.compare(dateNameToValueMap, datePrecision) && valid;

            if(!valid) {
                final String expectedComparison = "expected " + dateComparator;
                context.addError(MessageFormat.format(
                        MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                        xmlFile.getName(),
                        new StringBuilder().append("Date comparison error in '").append(xmlFile.getName()).append("': ").append(expectedComparison).toString()));
            }
        }

        return valid;
    }

    private Boolean checkAllBarcodesAndUuids(final File xmlFile, final QcContext context, final XPath xpath, final Document document) throws XPathExpressionException, ProcessorException {
        boolean valid = true;
        final List<ClinicalTable> clinicalTables = clinicalLoaderQueries.getAllClinicalTables();

        final Map<String, String> barcodeToUuid = new HashMap<String, String>();
        final Map<String, String> uuidToBarcode = new HashMap<String, String>();

        // for every clinical table...
        for (final ClinicalTable clinicalTable : clinicalTables) {
            // if it stores barcodes and uuids...
            if (clinicalTable.getUuidElementName() != null && clinicalTable.getBarcodeElementName() != null) {
                // get all barcode/uuid pairs for the element representing that table
                final NodeList uuidNodes = (NodeList) xpath.evaluate("//" + clinicalTable.getElementNodeName() + "/" + clinicalTable.getUuidElementName(), document, XPathConstants.NODESET);
                final NodeList barcodeNodes = (NodeList) xpath.evaluate("//" + clinicalTable.getElementNodeName() + "/" + clinicalTable.getBarcodeElementName(), document, XPathConstants.NODESET);
                // pull out the barcode/uuid values...
                for (int i=0; i<barcodeNodes.getLength(); i++) {
                    final Node barcodeNode = barcodeNodes.item(i);
                    final String barcode = barcodeNode.getTextContent().trim();

                    String uuid = null;
                    final Node uuidNode = uuidNodes.item(i);
                    // if there is no uuid node, then it will be null, API doesn't
                    // throw exception if index is invalid.  weird!
                    if (uuidNode != null) {
                        uuid = uuidNode.getTextContent().trim();
                    }

                    if ((uuid == null || uuid.length() == 0) && areUuidsRequired() ) {
                        context.addError("No uuid given for " + clinicalTable.getElementNodeName() + " " + barcode + " but UUIDs are required");
                        valid = false;
                    }

                    String barcodeType = clinicalTable.getElementNodeName();
                    barcodeType = convertNodeNameToBarcodeType(barcodeType);
                    boolean ignoreBarcodeValidation = false;
                    // for legacy slide barcodes, if it already exists in the database do not validate the barcode
                    if(CommonBarcodeAndUUIDValidatorImpl.SLIDE_ITEM_TYPE_NAME.equals(barcodeType)){
                        ignoreBarcodeValidation = bcridProcessor.slideBarcodeExists(barcode);

                    }
                    if (!ignoreBarcodeValidation &&  ! qcLiveBarcodeAndUUIDValidator.validateAnyBarcode(barcode, context, xmlFile.getName(), false, barcodeType ) ) {
                        valid = false;
                    } else {
                        valid = checkForUuidConflicts(barcode, uuid, clinicalTable.getElementNodeName(), barcodeToUuid, uuidToBarcode, context) && valid;

                        // only do barcode-tumor validation if the barcode itself is valid -- otherwise we know for sure we won't find it
                        // also, only verify aliquot barcodes for now, since the code doesn't support other types
                        if (clinicalTable.getElementNodeName().equals("aliquot") &&
                                barcodeTumorValidator != null && !barcodeTumorValidator.barcodeIsValidForTumor(barcode, context.getArchive().getTumorType())) {
                            context.addError("Barcode '" + barcode + "' in file '" + xmlFile.getName() + "' is not part of disease set for " + context.getArchive().getTumorType());
                            valid = false;
                        }
                    }
                }
            }
        }
        return valid;
    }

    protected boolean checkForUuidConflicts(final String barcode, final String uuid, final String elementType,
                                            final Map<String, String> barcodeToUuid, final Map<String, String> uuidToBarcode,
                                            final QcContext context) {
        boolean valid = true;
        final String lowercaseUuid = (uuid == null ? null : uuid.toLowerCase());

        if (lowercaseUuid != null && lowercaseUuid.length() > 0) {
            // check to make sure UUID is valid
            if (!qcLiveBarcodeAndUUIDValidator.validateUUIDFormat(lowercaseUuid)) {
                context.addError(elementType + " " + barcode + " is assigned UUID " + lowercaseUuid + " which does not have a valid format");
                valid = false;
            } else {
                // uuid was specified. check to see if it is associated with a different barcode in the db
                final String latestBarcodeForUuid = uuidService.getLatestBarcodeForUUID(lowercaseUuid);
                final String uuidForBarcode = uuidService.getUUIDForBarcode(barcode);
                if (latestBarcodeForUuid != null && !latestBarcodeForUuid.equals(barcode)) {
                    // this is an error -- while we still use both barcode and uuids as identifiers, changing can't be done by just modifying the barcode in the file
                    context.addError(elementType + " " + barcode + " is assigned UUID " + lowercaseUuid + " in the XML but the DCC has that UUID assigned to barcode " + latestBarcodeForUuid);
                    valid = false;
                } else if (uuidForBarcode != null && !uuidForBarcode.equalsIgnoreCase(lowercaseUuid)) {
                    // a different error -- the UUID in the XML was not associated with a different barcode, but the barcode is assigned a different UUID already
                    context.addError(elementType + " " + barcode + " is assigned UUID " + lowercaseUuid + " in the XML, but the DCC has that " + elementType + " barcode associated with UUID " + uuidForBarcode);
                    valid = false;
                } else if (barcodeToUuid.containsKey(barcode) && !barcodeToUuid.get(barcode).equals(lowercaseUuid)) {
                    context.addError(elementType + " " + barcode + " is assigned UUID " + lowercaseUuid + " in the XML, but earlier in the file it was assigned UUID " + barcodeToUuid.get(barcode));
                    valid = false;
                } else if (uuidToBarcode.containsKey(lowercaseUuid) && !uuidToBarcode.get(lowercaseUuid).equals(barcode)) {
                    context.addError(elementType + " " + barcode + " is assigned UUID " + lowercaseUuid + " in the XML, but " + uuidToBarcode.get(lowercaseUuid) + " is also assigned that UUID");
                    valid = false;
                } else {
                    barcodeToUuid.put(barcode, lowercaseUuid);
                    uuidToBarcode.put(lowercaseUuid, barcode);
                }
            }
        }
        return valid;
    }

    // convert underscore to string and then capitalize each word
    private String convertNodeNameToBarcodeType(final String nodeName) {
        String[] words = nodeName.split("_");

        for (int i=0; i<words.length; i++) {
            words[i] = StringUtils.capitalize(words[i]);
        }
        return StringUtils.join(words, " ");
    }

    private Boolean checkAliquotBarcodes(final File xmlFile, final QcContext context) throws TransformerException, XPathExpressionException, IOException, SAXException, ParserConfigurationException, ProcessorException {
        // we are running stand-alone, just validate the aliquot barcodes
        boolean valid = true;
        final List<String[]> bcrIDs = bcridProcessor.findAllAliquotsInFile(xmlFile);
        for (final String[] bcrID : bcrIDs) {
            final String barcode = bcrID[0];
            if (!qcLiveBarcodeAndUUIDValidator.validate(barcode, context, xmlFile.getName())) {
                valid = false;
            }
        }
        return valid;
    }

    protected Boolean checkShippedPortion(final File xmlFile, final QcContext context) throws TransformerException, IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        boolean valid = true;
        if(shippedPortionIdProcessor.shippedPortionExists(xmlFile)) {
            final Collection<String> shippedPortionUuids = shippedPortionIdProcessor.getTextElements(xmlFile, shipmentPortionPath, bcrShipmentPortionUuidElementName);
            for(String shippedPortionUuid : shippedPortionUuids) {
                if("".equals(shippedPortionUuid) || !qcLiveBarcodeAndUUIDValidator.validateUUIDFormat(shippedPortionUuid)) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                            xmlFile.getName(),
                            new StringBuilder().append("Shipped portion uuid : '").append(shippedPortionUuid).append("' in '").append(xmlFile.getName()).append("': '").append("is not a valid shipped portion uuid").toString()));
                    valid = false;
                }
            }
            if(codeTableQueries != null) {
	            final Collection<String> centerIds = shippedPortionIdProcessor.getTextElements(xmlFile, shipmentPortionPath, centerIdElementName);
	            for(String centerId : centerIds) {
	                if("".equals(centerId) || !codeTableQueries.bcrCenterIdExists(centerId)) {
	                    context.addError(MessageFormat.format(
	                            MessagePropertyType.XML_FILE_PROCESSING_ERROR,
	                            xmlFile.getName(),
	                            new StringBuilder().append("BCR centerid : '").append(centerId).append("' in '").append(xmlFile.getName()).append("': '").append("is not a valid BCR center code").toString()));
	                    valid = false;
	                }
	            }
            }
            final Collection<String> plateIds = shippedPortionIdProcessor.getTextElements(xmlFile, shipmentPortionPath, plateIdElementName);
            for(String plateId : plateIds) {
                if("".equals(plateId)) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                            xmlFile.getName(),
                            new StringBuilder().append("plateid : '").append(plateId).append("' in '").append(xmlFile.getName()).append("': '").append("is an empty plateid").toString()));
                    valid = false;
                }
            }
            final Collection<String> bcrAliquotBarcodes = shippedPortionIdProcessor.getTextElements(xmlFile, shipmentPortionPath, shipmentPortionBcrAliquotBarcodeElementName);
            for(String bcrAliquotBarcode : bcrAliquotBarcodes) {
                //also check whether barcode is valid
                if("".equals(bcrAliquotBarcode)) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                            xmlFile.getName(),
                            new StringBuilder().append("BCR Aliquot Barcode : '").append(bcrAliquotBarcode).append("' in '").append(xmlFile.getName()).append("': '").append("is an empty barcode").toString()));
                    valid = false;
                }
                try {
                    ShippedBiospecimen.parseShippedPortionBarcode(bcrAliquotBarcode);
                } catch (ParseException e) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                            xmlFile.getName(),
                            new StringBuilder().append("BCR Aliquot Barcode : '").append(bcrAliquotBarcode).append("' in '").append(xmlFile.getName()).append("': '").append("is not a valid barcode").toString()));
                    valid = false;
                }
            }
        }
        return valid;
    }

    /**
     * This method parses the clinical XML file for dates to validate, regardless of formatting (whitespaces).
     *
     * @param xmlFile the XML file to validate
     * @param xPath the xpath object
     * @param document the document parsed from the xml
     * @param context the application context
     * @param dateNameToValueMap a Map to store the dates that need to be compared later
     * @param datePrecision map to store precision of dates (day, month, or year)
     * @return <code>true</code> if the dates are all valid, <code>false</code> otherwise
     * @throws IOException
     */
    protected boolean checkDateValidation(final File xmlFile,
                                          final XPath xPath,
                                          final Document document,
                                          final QcContext context,
                                          final Map<String, Date> dateNameToValueMap,
                                          final Map<String, String> datePrecision) throws IOException {

        boolean result = true;

        try {
            //Iterate on the list of element's name suffixes to validate the different dates
            final Iterator<String> xpathSuffixIterator = getDatesToValidate().iterator();
            String xpathSuffix;
            while (xpathSuffixIterator.hasNext()) {

                xpathSuffix = xpathSuffixIterator.next();
                int indexOfColon = xpathSuffix.indexOf(":");
                String namespacePrefix = null;
                if (indexOfColon > 0) {
                    namespacePrefix = xpathSuffix.substring(0, indexOfColon);
                }
                // only check the date if it is in a namespace contained in this document
                if (namespacePrefix == null || xPath.getNamespaceContext().getNamespaceURI(namespacePrefix) != null) {
                    //Update result
                    result = checkDateForXPath(document, xPath, xpathSuffix, context, dateNameToValueMap, datePrecision) && result;
                }
            }

        } catch (XPathExpressionException e) {

            //Update result
            result = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
            		xmlFile.getName(), 
            		new StringBuilder().append("XPath Expression error reading '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
        } catch (UnexpectedInput e) {

            //Update result
            result = false;
            context.addError(MessageFormat.format(
            		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
            		xmlFile.getName(), 
            		new StringBuilder().append("Unexpected input error reading '").append(xmlFile.getName()).append("': ").append(e.getMessage()).toString()));
        }

        return result;
    }

    /**
     * This methods takes a W3C Document as well as an XPath expression suffix (the last part of an element's name)
     * and will retrieve the 3 date fields under the following XPath expressions:
     * <p/>
     * "DAYOF" + suffix
     * "MONTHOF" + suffix
     * "YEAROF" + suffix
     * <p/>
     * It will then proceed to validate the date retrieved and return the result of this validation.
     *
     * @param document
     * @param xpath
     * @param xpathExpressionSuffix
     * @param context
     * @param dateNameToValueMap a Map to store the dates that need to be compared later
     * @param datePrecision a map to hold date precisions
     * @return <code>true</code> if the date retrieved is valid, <code>false</code> otherwise.
     * @throws XPathExpressionException
     */
    private boolean checkDateForXPath(final Document document,
                                      final XPath xpath,
                                      final String xpathExpressionSuffix,
                                      final QcContext context,
                                      final Map<String, Date> dateNameToValueMap,
                                      final Map<String, String> datePrecision) throws XPathExpressionException, UnexpectedInput {

        boolean result = true;

        final NodeList yearOfNodes = (NodeList) xpath.evaluate(getXPathExpressionIgnoringNamespace(yearOfPrefix, xpathExpressionSuffix), document, XPathConstants.NODESET);
        final NodeList monthOfNodes = (NodeList) xpath.evaluate(getXPathExpressionIgnoringNamespace(monthOfPrefix, xpathExpressionSuffix), document, XPathConstants.NODESET);
        final NodeList dayOfNodes = (NodeList) xpath.evaluate(getXPathExpressionIgnoringNamespace(dayOfPrefix, xpathExpressionSuffix), document, XPathConstants.NODESET);

        //Need to make sure that yearOfNodes.getLength() == monthOfNodes.getLength() == dayOfNodes.getLength()
        //
        //Anything different should never happen, as the number of nodes found in for the 3 instances should always be the same
        //if the XML file respects the 'intgen.org_TCGA_ver1_17.xsd' schema
        if (yearOfNodes.getLength() != monthOfNodes.getLength()) {

            final String throwableMsg = "The number of nodes named '" + getXPathExpressionIgnoringNamespace(yearOfPrefix, xpathExpressionSuffix) + "' (Found " + yearOfNodes.getLength() + ")"
                    + " is different from the number of nodes named '" + getXPathExpressionIgnoringNamespace(monthOfPrefix, xpathExpressionSuffix) + "' (Found " + monthOfNodes.getLength() + ")";

            throw new UnexpectedInput(throwableMsg);
        }

        if (yearOfNodes.getLength() != dayOfNodes.getLength()) {

            final String throwableMsg = "The number of nodes named '" + getXPathExpressionIgnoringNamespace(yearOfPrefix, xpathExpressionSuffix) + "' (Found " + yearOfNodes.getLength() + ")"
                    + " is different from the number of nodes named '" + getXPathExpressionIgnoringNamespace(dayOfPrefix, xpathExpressionSuffix) + "' (Found " + dayOfNodes.getLength() + ")";

            throw new UnexpectedInput(throwableMsg);
        }

        //Getting the YEAROF, MONTHOF and DAYOF values for each Nodes
        String yearValue = null, monthValue = null, dayValue = null;
        for (int nodeIndex = 0; result == true && nodeIndex < yearOfNodes.getLength(); nodeIndex++) {

            //We can use the same index safely because XPath always returns nodes in document order
            //(See http://www.w3.org/TR/xpath/#dt-document-order)
            yearValue = yearOfNodes.item(nodeIndex).getTextContent().trim();
            monthValue = monthOfNodes.item(nodeIndex).getTextContent().trim();
            dayValue = dayOfNodes.item(nodeIndex).getTextContent().trim();

            //Update result
            result = validateDate(xpathExpressionSuffix, yearValue, monthValue, dayValue, context, dateNameToValueMap, datePrecision);
        }

        return result;
    }
    
    public BCRIDProcessor getBcridProcessor() {
        return this.bcridProcessor;
    }

    public void setBcridProcessor(BCRIDProcessor bcridProcessor) {
        this.bcridProcessor = bcridProcessor;
    }

    public ShippedPortionIdProcessor getShippedPortionIdProcessor() {
        return this.shippedPortionIdProcessor;
    }
    
    public void setShippedPortionIdProcessor(ShippedPortionIdProcessor shippedPortionIdProcessor) {
        this.shippedPortionIdProcessor = shippedPortionIdProcessor;
    }

    public UUIDService getUuidService() {
        return this.uuidService;
    }
    
    public void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    public ClinicalLoaderQueries getClinicalLoaderQueries() {
        return this.clinicalLoaderQueries;
    }
    
    public void setClinicalLoaderQueries(final ClinicalLoaderQueries clinicalLoaderQueries) {
        this.clinicalLoaderQueries = clinicalLoaderQueries;
    }

    public CodeTableQueries getCodeTableQueries() {
        return this.codeTableQueries;
    }
    
    public void setCodeTableQueries(final CodeTableQueries codeTableQueries) {
        this.codeTableQueries = codeTableQueries;
    }

    public QcLiveBarcodeAndUUIDValidator getBarcodeAndUUIDValidator() {
        return this.qcLiveBarcodeAndUUIDValidator;
    }
    
    public void setBarcodeAndUUIDValidator(final QcLiveBarcodeAndUUIDValidatorImpl barcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = barcodeAndUUIDValidator;
    }

    public void setUuidsRequired(final boolean uuidsRequired) {
        this.uuidsRequired = uuidsRequired;
    }

    /**
     * Are uuids required to be populated in the XML?
     * @return true or false
     */
    public boolean areUuidsRequired() {
        return uuidsRequired;
    }

    /**
     * This is a custom Exception for this class
     */
    public class UnexpectedInput extends Exception {

        public UnexpectedInput(String cause) {
            super(cause);
        }
    }

    private boolean nullOrEmptyString(final String strValue) {
        return (strValue == null) || (strValue.equals(""));
    }

    /**
     * Validate the given date
     *
     * @param dataType the element suffix with the namespace
     * @param yearValue the year value
     * @param monthValue the month value
     * @param dayValue the day value
     * @param context the context
     * @param dateNameToValueMap a Map to store the dates that need to be compared later
     * @param datePrecision a map to hold date precisions
     * @return <code>true</code> if the date is valid, <code>false</code> otherwise
     */
    private boolean validateDate(final String dataType,
                                 final String yearValue, String monthValue,
                                 String dayValue,
                                 final QcContext context,
                                 final Map<String, Date> dateNameToValueMap,
                                 final Map<String, String> datePrecision) {

        String filename = context.getFile() == null ? "unknown" : context.getFile().getName();

        boolean isValid = true;
        String precision = DateComparator.PRECISION_DAY; // default
        if (nullOrEmptyString(yearValue)) {
            // if Year is not specified, month and day should not be specified
            if (!nullOrEmptyString(monthValue)) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
                		filename, 
                		getXPathExpressionIgnoringNamespace(monthOfPrefix, dataType) + " is specified, but YEAR is not specified."));
                isValid = false;
            }
            if (!nullOrEmptyString(dayValue)) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
                		filename, 
                		getXPathExpressionIgnoringNamespace(dayOfPrefix, dataType) + " is specified, but YEAR is not specified."));
                isValid = false;
            }
        }

        if ((!nullOrEmptyString(yearValue)) && (nullOrEmptyString(monthValue))) {
            // when year is specified and month is not specified, day should not be specified
            if (!nullOrEmptyString(dayValue)) {
            	context.addError(MessageFormat.format(
                		MessagePropertyType.XML_FILE_PROCESSING_ERROR, 
                		filename, 
                		getXPathExpressionIgnoringNamespace(dayOfPrefix, dataType) + " is specified, but MONTH is not specified."));
                isValid = false;
            }
        }

        if (isValid && (!nullOrEmptyString(yearValue))) {
            if (nullOrEmptyString(monthValue)) {
                monthValue = "1";
                precision = DateComparator.PRECISION_YEAR;
            }
            if (nullOrEmptyString(dayValue)) {
                dayValue = "1";
                if (!nullOrEmptyString(monthValue)) {
                    precision = DateComparator.PRECISION_MONTH;
                }
            }

            int monthValueInt = Integer.parseInt(monthValue);

            try {
                final Date date = DateUtils.validate(dayValue, Integer.toString(monthValueInt), yearValue);

                if (date == null) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                            filename,
                            new StringBuilder().append("Date '").append(monthValue).append("/").append(dayValue).append("/").append(yearValue).
                            append("' for ").append(dataType).append(" is not valid.").toString()));
                    isValid = false;
                } else {
                    // the date should not be in future
                    final Calendar now = Calendar.getInstance();
                    final Calendar tomorrow = DateUtils.getNextDayFrom(now);
                    if (!DateUtils.isDateStrictlyBeforeGivenTime(dayValue, Integer.toString(monthValueInt), yearValue, tomorrow)) {
                        context.addError(MessageFormat.format(
                                MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                                filename,
                                new StringBuilder().append("Date '").append(monthValue).append("/").append(dayValue).append("/").append(yearValue).
                                append("' is in the future, which is not valid.").toString()));
                        isValid = false;
                    } else { //The date is valid

                        if(dataType != null && getDatesToCompare().contains(dataType)) {
                            // This date needs to be compared later, add it to the map.
                            // Note: The date to be compared should not be encountered more than once per file
                            // for this to work as expected (the last value being the only one stored)
                            dateNameToValueMap.put(dataType, date);
                            datePrecision.put(dataType, precision);
                        }
                    }
                }

            } catch (final DateUtilsException e) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                        filename,
                        new StringBuilder().append("Date '").append(monthValue).append("/").append(dayValue).append("/").append(yearValue).
                        append("' for ").append(dataType).append(" does not have the expected format (").append(e.getMessage()).append(").").toString()));
                isValid = false;
            }
        }
        return isValid;
    }

    protected boolean validateSchema(final File xmlFile, final Document document, final QcContext context)
            throws IOException, SAXException,SchemaException, ParserConfigurationException {
    	
    	return qCliveXMLSchemaValidator.validateSchema(xmlFile, context, getAllowLocalSchema(), qCliveXMLSchemaValidator.getXSDURLPattern());
    }
    
    /**
     * Verifies that the XSD version in the XML file conforms to pattern
     * @param xmlFile file to check XSD version
     * @param document DOM representation of the XML file
     * @param context qcContext to store errors should they occure
     * @return true if XSD version conforms to patter, false otherwise
     */
    protected Boolean checkXsdVersion(final File xmlFile, final Document document, final QcContext context) {
    	Boolean isValid = false;    	    	
    	String schemaVersion = document.getDocumentElement().getAttribute("schemaVersion");    	
    	Pattern validXsdVersion = Pattern.compile(qCliveXMLSchemaValidator.getValidXsdVersionPattern());
    	if(StringUtils.isEmpty(schemaVersion)) {
    		context.addError(MessageFormat.format(
    				MessagePropertyType.XML_FILE_PROCESSING_ERROR,
    				xmlFile.getName(),
    				"XML did not specify a version using 'schemaVersion'"));                
    	}
    	else {
    		 if(validXsdVersion.matcher(schemaVersion).matches()) {
    			 isValid = true;
             } 
    		 else {
    			 context.addError(MessageFormat.format(
    					 MessagePropertyType.XSD_FILE_PROCESSING_ERROR,
    					 xmlFile.getName(),
    					 "Version '" + schemaVersion + "' is unsupported."));
             }
    	}
    	
    	return isValid;
    }
       
    protected Boolean getDefaultReturnValue(final Archive archive) {
        return true;
    }

    protected String getFileExtension() {
        return XML_EXTENSION;
    }

    protected boolean isCorrectArchiveType(final Archive archive) {
        return Experiment.TYPE_BCR.equals(archive.getExperimentType());
    }

    protected Source getSource(final Boolean isURL,
                               final String schema,
                               final File xmlFile) throws IOException{

        if(isURL){
            final URL schemaURL  = new URL(schema);
            return new StreamSource(schemaURL.toExternalForm());
        }else{
            //  get xsd file
            File  schemaFile = new File(new StringBuilder(xmlFile.getParent())
                    .append(File.separator)
                    .append(schema).toString());
            if (!schemaFile.exists()) {
                throw new FileNotFoundException(new StringBuilder("Schema ")
                        .append(schema)
                        .append(" was not found in the archive, even though the XML file ")
                        .append(xmlFile.getName())
                        .append(" refers to it").toString());
            }
            return new StreamSource(schemaFile);

        }
    }   

    public String getName() {
        return "clinical XML file validation";
    }

    public void setBarcodeTumorValidator(final BarcodeTumorValidator barcodeTumorValidator) {
        this.barcodeTumorValidator = barcodeTumorValidator;
    }

    public Set<String> getDatesToCompare() {
        return datesToCompare;
    }

    public void setDatesToCompare(final Set<String> datesToCompare) {
        this.datesToCompare = datesToCompare;
    }

    public List<DateComparator> getDateComparators() {
        return dateComparators;
    }

    public void setDateComparators(final List<DateComparator> dateComparators) {
        this.dateComparators = dateComparators;
    }

    public Boolean getAllowLocalSchema() {
        return allowLocalSchema;
    }

    public void setAllowLocalSchema(Boolean allowLocalSchema) {
        this.allowLocalSchema = allowLocalSchema;
    }
    
    /**
	 * @return the shipmentPortionPath
	 */
	public String getShipmentPortionPath() {
		return shipmentPortionPath;
	}

	/**
     * @param shipmentPortionPath The XPath to the shipment portion node
     */
    public void setShipmentPortionPath(String shipmentPortionPath) {
        this.shipmentPortionPath = shipmentPortionPath;
    }
	
	/**
	 * @return the bcrShipmentPortionUuidElementName
	 */
	public String getBcrShipmentPortionUuidElementName() {
		return bcrShipmentPortionUuidElementName;
	}

	/**
     * @param bcrShipmentPortionUuidElementName The bcrShipmentPortionUuid XPath element name
     */
    public void setBcrShipmentPortionUuidElementName(String bcrShipmentPortionUuidElementName) {
        this.bcrShipmentPortionUuidElementName = bcrShipmentPortionUuidElementName;
    }
    
	/**
	 * @return the centerIdElementName
	 */
	public String getCenterIdElementName() {
		return centerIdElementName;
	}
	
	/**
     * @param centerIdElementName The centerId XPath element name
     */
    public void setCenterIdElementName(String centerIdElementName) {
        this.centerIdElementName = centerIdElementName;
    }

    /**
	 * @return the plateIdElementName
	 */
	public String getPlateIdElementName() {
		return plateIdElementName;
	}
    
    /**
     * @param plateIdElementName The plateId XPath element name
     */
    public void setPlateIdElementName(String plateIdElementName) {
        this.plateIdElementName = plateIdElementName;
    }
    
	/**
	 * @return the shipmentPortionBcrAliquotBarcodeElementName
	 */
	public String getShipmentPortionBcrAliquotBarcodeElementName() {
		return shipmentPortionBcrAliquotBarcodeElementName;
	}
	
	/**
     * @param shipmentPortionBcrAliquotBarcodeElementName The shipmentPortionBcrAliquotBarcode XPath element name
     */
    public void setShipmentPortionBcrAliquotBarcodeElementName(String shipmentPortionBcrAliquotBarcodeElementName) {
        this.shipmentPortionBcrAliquotBarcodeElementName = shipmentPortionBcrAliquotBarcodeElementName;
    }	
    public QcLiveBarcodeAndUUIDValidator getQcLiveBarcodeAndUUIDValidator() {
		return qcLiveBarcodeAndUUIDValidator;
	}

	public void setQcLiveBarcodeAndUUIDValidator(
			QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
		this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
	}
	public QCliveXMLSchemaValidator getqCliveXMLSchemaValidator() {
		return qCliveXMLSchemaValidator;
	}

	public void setqCliveXMLSchemaValidator(
			QCliveXMLSchemaValidator qCliveXMLSchemaValidator) {
		this.qCliveXMLSchemaValidator = qCliveXMLSchemaValidator;
	}

    public BCRUtils getBcrUtils() {
        return bcrUtils;
    }

    public void setBcrUtils(final BCRUtils bcrUtils) {
        this.bcrUtils = bcrUtils;
    }
}
