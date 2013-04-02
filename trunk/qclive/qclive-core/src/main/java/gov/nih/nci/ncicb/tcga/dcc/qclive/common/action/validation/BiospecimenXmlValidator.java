/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for biospecimen XML files.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BiospecimenXmlValidator extends AbstractArchiveFileProcessor<Boolean> {

    public static final String XML_EXTENSION = ".xml";
    public static final String BIOSPECIMEN_XML_VALIDATOR_NAME = "biospecimen XML file validation";
    public static final String CENTER_ID = "center_id";
    public static final String TISSUE_SOURCE_SITE = "tissue_source_site";
    public static final String PATIENT_ID = "patient_id";
    public static final String SAMPLE_TYPE_ID = "sample_type_id";
    public static final String VIAL_NUMBER = "vial_number";
    public static final String PORTION_NUMBER = "portion_number";
    public static final String ANALYTE_TYPE_ID = "analyte_type_id";
    public static final String PLATE_ID = "plate_id";
    public static Pattern ALPHANUMERIC_PATTERN = Pattern.compile("[A-Za-z0-9]+");

    private static final Pattern BATCH_NUMBER_VALUE_PATTERN = Pattern.compile("[0-9]+");

    private CodeTableQueries codeTableQueries;

    private BCRUtils bcrUtils;

    public void setCodeTableQueries(CodeTableQueries codeTableQueries) {
        this.codeTableQueries = codeTableQueries;
    }

    @Override
    protected Boolean processFile(File file, QcContext context) throws ProcessorException {
        context.setFile(file);
        // valid set to true so that any non-biospecimen xml file will pass as a mean to say they will not be validated.
        //The clinical xml validator is already supposed to take care of those non-biospecimen xml files.
        Boolean valid = true;
        if (file!=null && (getBcrUtils().isBiospecimenFile(file) || getBcrUtils().isControlFile(file))) {
            try {
                XPathXmlParser xPathXmlParser = new XPathXmlParser();
                // namespace-aware false
                Document document = xPathXmlParser.parseXmlFile(file, false, false);
                XPath xpath = xPathXmlParser.getXPath();
                //Validation continues even if one of the check methods fails validation
                checkTissueSourceSite(file, context, document, xpath);
                checkPatienId(file, context, document, xpath);
                checkSampleTypeId(file, context, document, xpath);
                checkVialNumber(file, context, document, xpath);
                checkPortionNumber(file, context, document, xpath);
                checkAnalyteTypeId(file, context, document, xpath);
                checkPlateId(file, context, document, xpath);
                checkCenterId(file, context, document, xpath);

                if (getBcrUtils().isControlFile(file)) {
                    checkControls(file, context, document, xpath);
                }

                checkBatchId(file, context, document, xpath);

            } catch (IOException e) {
                valid = false;
                createError("I/O error reading", file, context, e);
            }
            catch (ParserConfigurationException e) {
                valid = false;
                createError("Parser error reading", file, context, e);
            }
            catch (SAXException e) {
                valid = false;
                createError("Error in", file, context, e);
            }
            catch (XPathExpressionException e) {
                valid = false;
                createError("Error evaluating", file, context, e);
            }
        }
        if (context.getErrorCount()>0){
            valid = false;
        }
        if (!valid) {
            context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
        }
        return valid;
    }

    private void checkBatchId(final File file, final QcContext context, final Document document, final XPath xpath) throws XPathExpressionException {
        final String archiveBatchNumber = context.getArchive().getSerialIndex();
        final NodeList batchNodes = getNodeListFromXPathExpression(document, xpath, "//admin/batch_number");
        if (batchNodes == null || batchNodes.getLength() == 0) {
            createError("batch_number is missing", file, context);
        } else {
            final String xmlBatchNumber = batchNodes.item(0).getTextContent().trim();
            if (xmlBatchNumber.length() == 0) {
                createError("batch_number is empty", file, context);
            } else {
                final Matcher batchMatcher = BATCH_NUMBER_VALUE_PATTERN.matcher(xmlBatchNumber);
                if (batchMatcher.find()) {
                    final String batch = batchMatcher.group(0);
                    
                    if (! batch.equals(archiveBatchNumber)) {
                        createError("batch_number is " + batch + " but archive has serial index " + archiveBatchNumber, file, context);
                    }
                } else {
                    createError("batch_number does not contain a batch number", file, context);
                }
            }
        }
    }

    private void checkControls(final File file, final QcContext context, final Document document, final XPath xpath) throws XPathExpressionException {
        final NodeList aliquotBarcodeNodes = getNodeListFromXPathExpression(document, xpath, "//aliquot/bcr_aliquot_barcode");

        final NodeList aliquotUuidNodes = getNodeListFromXPathExpression(document, xpath, "//aliquot/bcr_aliquot_uuid");

        final Map<String, String> aliquotBarcodeToUuid = new HashMap<String, String>();
        final Set<String> aliquotUuidsWithoutBarcodes = new HashSet<String>();
        for (int i=0; i<aliquotBarcodeNodes.getLength(); i++) {
            final String barcode = aliquotBarcodeNodes.item(i).getTextContent().trim();
            final String uuid = aliquotUuidNodes.item(i).getTextContent().trim();
            if (uuid == null || uuid.length() < 1) {
                createError("aliquot UUID may not be blank (" + barcode + ")", file, context);
            } else {
                if (barcode == null || barcode.length() < 1) {
                    aliquotUuidsWithoutBarcodes.add(uuid);
                } else {
                    aliquotBarcodeToUuid.put(barcode, uuid);
                }
            }
        }

        final NodeList controlBarcodeNodes = getNodeListFromXPathExpression(document, xpath, "//control//bcr_aliquot_barcode");
        final NodeList controlUuidNodes = getNodeListFromXPathExpression(document, xpath, "//control//bcr_aliquot_uuid");
        final Map<String, String> controlBarcodeToUuid = new HashMap<String, String>();
        final Set<String> controlUuidsWithoutBarcodes = new HashSet<String>();
        for (int i=0; i<controlBarcodeNodes.getLength(); i++) {
            final String barcode = controlBarcodeNodes.item(i).getTextContent().trim();
            final String uuid = controlUuidNodes.item(i).getTextContent().trim();
            if (uuid == null || uuid.length() < 1) {
                createError("control UUID may not be blank (" + barcode + ")" , file, context);
            } else {
                if (barcode == null || barcode.length() < 1) {
                    controlUuidsWithoutBarcodes.add(uuid);
                } else {
                    controlBarcodeToUuid.put(barcode, uuid);
                }
            }
        }


        for (final String controlUuid : controlUuidsWithoutBarcodes) {
            if (! aliquotUuidsWithoutBarcodes.contains(controlUuid) && !aliquotBarcodeToUuid.containsValue(controlUuid)) {
                createError("control with UUID " + controlUuid + " does not have a corresponding 'aliquot' block", file, context);
            }
        }

        for (final String aliquotUuid : aliquotUuidsWithoutBarcodes) {
            if (!controlUuidsWithoutBarcodes.contains(aliquotUuid) && !controlBarcodeToUuid.containsValue(aliquotUuid)) {
                createError("aliquot with UUID " + aliquotUuid + " does not have a corresponding 'control' block", file, context);
            }
        }


        for (final String aliquotBarcode : aliquotBarcodeToUuid.keySet()) {
            final String aliquotUuid = aliquotBarcodeToUuid.get(aliquotBarcode);
            final String controlUuid = controlBarcodeToUuid.get(aliquotBarcode);

            if (!controlUuidsWithoutBarcodes.contains(aliquotUuid)) {
                if (controlUuid == null) {
                    createError("aliquot " + aliquotBarcode + " (" + aliquotUuid + ") does not have a corresponding 'control' block", file, context);
                } else if (! aliquotUuid.equals(controlUuid)) {
                    createError("aliquot " + aliquotBarcode + " (" + aliquotUuid + ") has a different UUID in its 'control' block: " + controlUuid, file, context);
                }
            }
        }

        for (final String controlBarcode : controlBarcodeToUuid.keySet()) {
            final String controlUuid = controlBarcodeToUuid.get(controlBarcode);

            if (! aliquotBarcodeToUuid.containsKey(controlBarcode) && ! aliquotUuidsWithoutBarcodes.contains(controlUuid)) {
                createError("control " + controlBarcode + " (" + controlUuid + ") does not have a corresponding 'aliquot' block", file, context);
            }
        }

    }

    /**
     * Check the validity of all center id elements in the biospecimen xml file.
     *
     * @param file the biospecimen xml file
     * @param context the QcLive context
     * @param document the DOM document
     * @param xpath {@link XPath} object
     * @throws XPathExpressionException if an exception occurs while traversing the XML file
     */
    private void checkCenterId(final File file,
                               final QcContext context,
                               final Document document,
                               final XPath xpath)
            throws XPathExpressionException {

        final NodeList centerIdNodes = getNodeListFromElement(CENTER_ID, document, xpath);
        final Set<String> centerIds = new HashSet<String>();

        // Update centerIds
        for (int i = 0; i < centerIdNodes.getLength(); i++) {

            final Node centerIdNode = centerIdNodes.item(i);
            final String centerId = centerIdNode.getTextContent().trim();

            centerIds.add(centerId);
        }

        // Validate the center Ids
        for (final String centerId : centerIds) {

            if (centerId.length() == 0 || !codeTableQueries.bcrCenterIdExists(centerId)) {
                createError("BCR centerId " + centerId + " is not a valid BCR center code", file, context);
            }
        }
    }

    /**
     * check the validity of all plate id elements in the biospecimen xml
     *
     * @param file
     * @param context
     * @param document
     * @param xpath
     * @throws XPathExpressionException
     */
    private void checkPlateId(final File file, final QcContext context,
                                 final Document document, final XPath xpath)
            throws XPathExpressionException {
        final NodeList plateIdNodes = getNodeListFromElement(PLATE_ID, document, xpath);
        for (int i = 0; i < plateIdNodes.getLength(); i++) {
            final Node plateIdNode = plateIdNodes.item(i);
            final String plateId = plateIdNode.getTextContent().trim();
            if (plateId.length() == 0 || !ALPHANUMERIC_PATTERN.matcher(plateId).matches()) {
                createError("plateId " + plateId + " is not alphanumeric", file, context);
            }
        }
    }

    /**
     * Check the validity of all analyte type id elements in the biospecimen xml.
     *
     * @param file     the biospecimen xml file
     * @param context  the QcLive context
     * @param document the DOM document
     * @param xpath    {@link XPath} object
     * @throws XPathExpressionException if an exception occurs while traversing the XML file
     */
    private void checkAnalyteTypeId(final File file,
                                    final QcContext context,
                                    final Document document,
                                    final XPath xpath)
            throws XPathExpressionException {

        final NodeList analyteTypeIdNodes = getNodeListFromElement(ANALYTE_TYPE_ID, document, xpath);
        final Set<String> analyteTypeIds = new HashSet<String>();

        for (int i = 0; i < analyteTypeIdNodes.getLength(); i++) {
            final Node analyteTypeIdNode = analyteTypeIdNodes.item(i);
            final String analyteTypeId = analyteTypeIdNode.getTextContent().trim();

            // Update analyteTypeIds
            analyteTypeIds.add(analyteTypeId);
        }

        // Validate the analyte type Ids
        for (final String analyteTypeId : analyteTypeIds) {
            if (analyteTypeId.length() == 0 || !codeTableQueries.portionAnalyteExists(analyteTypeId)) {
                createError("AnalyteTypeId " + analyteTypeId + " is not a valid analyte type code", file, context);
            }
        }
    }

    /**
     * check the validity of all portion number elements in the biospecimen xml
     *
     * @param file
     * @param context
     * @param document
     * @param xpath
     * @throws XPathExpressionException
     */
    private void checkPortionNumber(final File file, final QcContext context,
                                       final Document document, final XPath xpath)
            throws XPathExpressionException {
        final NodeList portionNumberNodes = getNodeListFromElement(PORTION_NUMBER, document, xpath);
        for (int i = 0; i < portionNumberNodes.getLength(); i++) {
            final Node portionNumberNode = portionNumberNodes.item(i);
            final String portionNumber = portionNumberNode.getTextContent().trim();
            if (portionNumber.length() == 0 || !ALPHANUMERIC_PATTERN.matcher(portionNumber).matches()) {
                createError("portionNumber " + portionNumber + " is not alphanumeric", file, context);
            }
        }
    }

    /**
     * check the validity of all vial number elements in the biospecimen xml
     *
     * @param file
     * @param context
     * @param document
     * @param xpath
     * @throws XPathExpressionException
     */
    private void checkVialNumber(final File file, final QcContext context,
                                    final Document document, final XPath xpath)
            throws XPathExpressionException {
        final NodeList vialNumberNodes = getNodeListFromElement(VIAL_NUMBER, document, xpath);
        for (int i = 0; i < vialNumberNodes.getLength(); i++) {
            final Node vialNumberNode = vialNumberNodes.item(i);
            final String vialNumber = vialNumberNode.getTextContent().trim();
            if (vialNumber.length() == 0 || !ALPHANUMERIC_PATTERN.matcher(vialNumber).matches()) {
                createError("vialNumber " + vialNumber + " is not alphanumeric", file, context);
            }
        }
    }

    /**
     * Check the validity of all sample type id elements in the biospecimen xml.
     *
     * @param file     the biospecimen xml file
     * @param context  the QcLive context
     * @param document the DOM document
     * @param xpath    {@link XPath} object
     * @throws XPathExpressionException if an exception occurs while traversing the XML file
     */
    private void checkSampleTypeId(final File file,
                                   final QcContext context,
                                   final Document document,
                                   final XPath xpath)
            throws XPathExpressionException {

        final NodeList sampleTypeIdNodes = getNodeListFromElement(SAMPLE_TYPE_ID, document, xpath);
        final Set<String> sampleTypeIds = new HashSet<String>();

        for (int i = 0; i < sampleTypeIdNodes.getLength(); i++) {
            final Node sampleTypeIdNode = sampleTypeIdNodes.item(i);
            final String sampleTypeId = sampleTypeIdNode.getTextContent().trim();

            // Update sampleTypeIds
            sampleTypeIds.add(sampleTypeId);
        }

        // Validate the sample type Ids
        for (final String sampleTypeId : sampleTypeIds) {
            if (sampleTypeId.length() == 0 || !codeTableQueries.sampleTypeExists(sampleTypeId)) {
                createError("SampleTypeId " + sampleTypeId + " is not a valid sample type code", file, context);
            }
        }
    }

    /**
     * check the validity of all patient id elements in the biospecimen xml
     *
     * @param file
     * @param context
     * @param document
     * @param xpath
     * @throws XPathExpressionException
     */
    private void checkPatienId(final File file, final QcContext context,
                                  final Document document, final XPath xpath)
            throws XPathExpressionException {
        final NodeList patientIdNodes = getNodeListFromElement(PATIENT_ID, document, xpath);
        for (int i = 0; i < patientIdNodes.getLength(); i++) {
            final Node patientIdNode = patientIdNodes.item(i);
            final String patientId = patientIdNode.getTextContent().trim();
            if (patientId.length() == 0 || !ALPHANUMERIC_PATTERN.matcher(patientId).matches()) {
                createError("patientId " + patientId + " is not alphanumeric", file, context);
            }
        }
    }

    /**
     * Check the validity of all tissue source site elements in the biospecimen xml.
     *
     * @param file     the biospecimen xml file
     * @param context  the QcLive context
     * @param document the DOM document
     * @param xpath    {@link XPath} object
     * @throws XPathExpressionException if an exception occurs while traversing the XML file
     */
    private void checkTissueSourceSite(final File file,
                                       final QcContext context,
                                       final Document document,
                                       final XPath xpath)
            throws XPathExpressionException {

        final NodeList tssNodes = getNodeListFromElement(TISSUE_SOURCE_SITE, document, xpath);
        final Set<String> tssCodes = new HashSet<String>();

        for (int i = 0; i < tssNodes.getLength(); i++) {
            final Node tssNode = tssNodes.item(i);
            final String tss = tssNode.getTextContent().trim();

            // Update tssCodes
            tssCodes.add(tss);
        }

        //Validate tss codes
        for (final String tss : tssCodes) {
            if (tss.length() == 0 || !codeTableQueries.tssCodeExists(tss)) {
                createError("TSS " + tss + " is not a valid tissue source site code", file, context);
            }
        }
    }

    /**
     * return nodeList from chose element name
     *
     * @param element element name
     * @param document xml document
     * @param xpath
     * @return node list
     * @throws XPathExpressionException
     */
    private NodeList getNodeListFromElement(String element, Document document, XPath xpath) throws XPathExpressionException {
        return getNodeListFromXPathExpression(document, xpath, getXPathExpressionForAnyElementContaining(element));
    }

    /**
     * create error message for the qc context including exception
     *
     * @param message error message
     * @param file
     * @param context
     * @param e exception
     */
    private void createError(String message, File file, QcContext context, Exception e) {
        context.addError(MessageFormat.format(
                MessagePropertyType.XML_FILE_PROCESSING_ERROR,
                file.getName(),
                new StringBuilder().append(message).append(" '").append(file.getName())
                        .append("': ").append(e.getMessage()).toString()));
    }

    /**
     * create error message for the qc context
     *
     * @param message error message
     * @param file
     * @param context
     */
    private void createError(String message, File file, QcContext context) {
        context.addError(MessageFormat.format(
                MessagePropertyType.XML_FILE_VALIDATION_ERROR,
                file.getName(),
                message));
    }

    /**
     * Return a <code>NodeList</code> of all XML Nodes matching the XPath expression in the <code>Document</code>
     *
     * @param document        the <code>Document</code> to parse
     * @param xpath           an <code>XPath</code> instance
     * @param xpathExpression the XPath expression
     * @return a <code>NodeList</code> of all XML Nodes matching the XPath expression in the <code>Document</code>
     * @throws XPathExpressionException
     */
    private NodeList getNodeListFromXPathExpression(final Document document, final XPath xpath, final String xpathExpression)
            throws XPathExpressionException {
        return (NodeList) xpath.evaluate(xpathExpression, document, XPathConstants.NODESET);
    }

    /**
     * Return the XPath expression that will return any XML Node which name contains the given string
     *
     * @param string the string that must be contained in the XML Node returned by the XPath expression
     * @return the XPath expression that will return any Node which name contains the given string
     */
    private String getXPathExpressionForAnyElementContaining(final String string) {
        return new StringBuilder("//*[contains(name(), '").append(string).append("')]").toString();
    }

    @Override
    protected Boolean getReturnValue(Map<File, Boolean> results, QcContext context) {
        return !(results.values().contains(false));
    }

    @Override
    protected Boolean getDefaultReturnValue(Archive archive) {
        return true;
    }

    @Override
    protected String getFileExtension() {
        return XML_EXTENSION;
    }

    @Override
    protected boolean isCorrectArchiveType(Archive archive) throws ProcessorException {
        return Experiment.TYPE_BCR.equals(archive.getExperimentType());
    }

    @Override
    public String getName() {
        return BIOSPECIMEN_XML_VALIDATOR_NAME;
    }

    public BCRUtils getBcrUtils() {
        return bcrUtils;
    }

    public void setBcrUtils(final BCRUtils bcrUtils) {
        this.bcrUtils = bcrUtils;
    }
}
