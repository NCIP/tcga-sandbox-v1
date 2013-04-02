/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.XPathXmlParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ClinicalXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveUtilImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Processor that takes a clinical archive and, for each XML file (but not Auxiliary files), obscures the dates by making them elapsed time (in days)
 * since a certain base clinical date.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalDateObscurer extends AbstractArchiveFileProcessor<Archive> {

    private static final long MILLISECONDS_PER_DAY = 86400000; // 1000 ms/sec * 60 sec/min * 60 min/hour * 24 hour/day

    private BCRUtils bcrUtils;

    /**
     * A <code>List</code> of XML tag suffixes that, when prefixed by <code>yearOfPrefix</code>, <code>monthOfPrefix</code> and <code>dayOfPrefix</code>
     * gives the dates *NOT* to obscure
     */
    private List<String> datesNotToObscure;

    /**
     * A <code>Map</code> which keys are the XML tag suffixes that, when prefixed by <code>yearOfPrefix</code>, <code>monthOfPrefix</code> and <code>dayOfPrefix</code>
     * gives the dates to obscure for which there is a CDE value, and which values are the the value to put in the <code>cde</code> attribute for the obscured date element
     */
    private Map<String, String> cdeForDatesToObscure;

    /**
     * The XML tag suffix that, when prefixed by <code>yearOfPrefix</code>, <code>monthOfPrefix</code> and <code>dayOfPrefix</code>,
     * gives the new time reference for obscuring dates in clinical XML files
     */
    private String basisDateNameForClinical;

    /**
     * The XML tag suffix that, when prefixed by <code>yearOfPrefix</code>, <code>monthOfPrefix</code> and <code>dayOfPrefix</code>,
     * gives the new time reference for obscuring dates in biospecimen XML files
     */
    private String basisDateNameForBiospecimen;

    private String elapsedElementBase;
    private String birthDateName;
    private String ageAtBasisDateCDE;
    private String ageAtPrefix;
    private static final String OWNER = "owner";

    /**
     * Name of the attribute to use when an element's value has been floored
     */
    private static final String FLOORED = "floored";

    /**
     * The patient's cutoff age at initial diagnosis. The age value should be floored to that cutoff value if it is higher.
     */
    private Integer cutoffAgeAtInitialDiagnosis;

    /**
     * The lower bound for the patient 'days to birth'.
     * If the actual patient 'days to birth' value is lower than that, it should be floored to this lower bound.
     */
    private Integer daysToBirthLowerBound;

    /**
     * The name of the XML element that holds the patient BCR barcode value
     */
    private String bcrPatientBarcodeElementName;

    /**
     * An ArchiveUtil implementation
     */
    private ArchiveUtilImpl archiveUtilImpl;

    /**
     * The name to give to the file that stores the BCR patients barcodes for patients over the cutoff age
     */
    public static final String README_FILENAME = "README_HIPAA_AGES.txt";

    /**
     * Creates an xpath expression that will return any node that matches the name composed of the suffix appended to the prefix,
     * ignoring the namespace. If there is a namespace given in the suffix, it will be left of from the xpath expression.
     *
     * For example: if "day_of_" is the prefix and "bios:collection" is the suffix,
     * this would return "//*[local-name()='day_of_collection']".
     *
     * Used by ClinicalDateObscurer and ClinicalXmlValidator for figuring out the path to the date element parts.
     *
     * @param prefix the element prefix
     * @param elementSuffixWithNamespace the suffix with optional namespace at the beginning
     * @return the xpath path to the element, ignoring namespace
     */
    public static String getXPathExpressionIgnoringNamespace(final String prefix, final String elementSuffixWithNamespace) {

        String elementSuffixNoNamespace = elementSuffixWithNamespace;

        int indexOfColon = elementSuffixWithNamespace.indexOf(":");
        if (indexOfColon > 0) {
            elementSuffixNoNamespace = elementSuffixWithNamespace.substring(indexOfColon + 1);
        }

        return "//*[local-name()='" + (prefix == null ? "" : prefix) + elementSuffixNoNamespace + "']";
    }

    /**
     * Sets the CDE to use for the "AGEAT[BASISDATE]" element in the transformed XML.
     * 
     * @param ageAtBasisDateCDE the CDE to use in the AGEAT[BASISDATE] element
     */
    public void setAgeAtBasisDateCDE(final String ageAtBasisDateCDE) {
        this.ageAtBasisDateCDE = ageAtBasisDateCDE;
    }


    public List<String> getDatesNotToObscure() {
        return datesNotToObscure;
    }

    public void setDatesNotToObscure(final List<String> datesNotToObscure) {
        this.datesNotToObscure = datesNotToObscure;
    }

    /**
     * Sets the dates *NOT* to obscure from a comma delimited list of date names
     *
     * @param dateNames a comma delimited list of date names
     */
    public void setDatesNotToObscureString(final String dateNames) {

        datesNotToObscure = new LinkedList<String>();

        if (dateNames != null) {

            final String[] dates = dateNames.split(",");

            for(final String date : dates) {
                datesNotToObscure.add(date);
            }
        }
    }

    public Map<String, String> getCdeForDatesToObscure() {
        return cdeForDatesToObscure;
    }

    public void setCdeForDatesToObscure(final Map<String, String> cdeForDatesToObscure) {
        this.cdeForDatesToObscure = cdeForDatesToObscure;
    }

    /**
     * Sets the CDE for the dates to obscure from a comma delimited string.
     *
     * The string format should have each element separated by a comma. If the date's days-to element has a CDE ID,
     * that should be put following the element name with a hash (#) separating.
     *
     * For example if the DAYSTOBIRTH element has a CDE of 1234 and the DAYSTODEATH element has no CDE,
     * then the string would look like "BIRTH#1234,DEATH".
     *
     * @param dateNames a comma delimited list of date names, with optional CDE IDs delimited by hash
     */
    public void setCdeForDatesToObscureString(final String dateNames) {

        cdeForDatesToObscure = new HashMap<String, String>();

        if (dateNames != null) {

            final String[] dates = dateNames.split(",");
            String[] dateAndCDE;

            for(final String date : dates) {

                dateAndCDE = date.split("#");
                cdeForDatesToObscure.put(dateAndCDE[0], dateAndCDE.length > 1 ? dateAndCDE[1] : "");
            }
        }
    }

    public void setAgeAtPrefix(final String ageAtPrefix) {
        this.ageAtPrefix = ageAtPrefix;
    }

    public void setDayOfPrefix(final String dayOfPrefix) {
        this.dayOfPrefix = dayOfPrefix;
    }

    public void setMonthOfPrefix(final String monthOfPrefix) {
        this.monthOfPrefix = monthOfPrefix;
    }

    public void setYearOfPrefix(final String yearOfPrefix) {
        this.yearOfPrefix = yearOfPrefix;
    }

    public Integer getCutoffAgeAtInitialDiagnosis() {
        return cutoffAgeAtInitialDiagnosis;
    }

    public void setCutoffAgeAtInitialDiagnosis(final Integer cutoffAgeAtInitialDiagnosis) {
        this.cutoffAgeAtInitialDiagnosis = cutoffAgeAtInitialDiagnosis;
    }

    public String getBcrPatientBarcodeElementName() {
        return bcrPatientBarcodeElementName;
    }

    public void setBcrPatientBarcodeElementName(final String bcrPatientBarcodeElementName) {
        this.bcrPatientBarcodeElementName = bcrPatientBarcodeElementName;
    }

    public ArchiveUtilImpl getArchiveUtilImpl() {
        return archiveUtilImpl;
    }

    public void setArchiveUtilImpl(final ArchiveUtilImpl archiveUtilImpl) {
        this.archiveUtilImpl = archiveUtilImpl;
    }

    public Integer getDaysToBirthLowerBound() {
        return daysToBirthLowerBound;
    }

    public void setDaysToBirthLowerBound(final Integer daysToBirthLowerBound) {
        this.daysToBirthLowerBound = daysToBirthLowerBound;
    }

    /**
     * Represents some of the possible Procurement Statuses of XML elements.
     */
    enum ProcurementStatus {
        Completed(0),
        Pending(1),
        Not_Applicable(10),
        Not_Requested(11),
        Not_Reported(12),
        Not_Available(13),
        Other(5);

        private final Integer rank;

        /**
         * @param theRank the rank of the procurement status
         */
        ProcurementStatus(final int theRank) {
            rank = theRank;
        }

        /**
         * Return <code>true</code> if this procurement status is worse (bigger rank)
         * than <code>otherStatus</code>
         *
         * @param otherStatus the other status to compare it to
         * @return <code>true</code> if this procurement status is worse (bigger rank)
         * than <code>otherStatus</code>
         */
        boolean isWorseThan(final ProcurementStatus otherStatus) {
            return rank.compareTo(otherStatus.rank) > 0;
        }

        /**
         * Return a String representation of a procurement status
         *
         * @param str the procurement status
         * @return a String representation of a procurement status
         */
        static ProcurementStatus fromString(final String str) {
            if (str == null) {
                return Other;
            } else if (str.toLowerCase().endsWith("completed")) {
                return Completed;
            } else if (str.toLowerCase().endsWith("pending")) {
                return Pending;
            } else if (str.toLowerCase().endsWith("not applicable")) {
                return Not_Applicable;
            } else if (str.toLowerCase().endsWith("not requested")) {
                return Not_Requested;
            } else if (str.toLowerCase().endsWith("not reported")) {
                return Not_Reported;
            } else if (str.toLowerCase().endsWith("not available")) {
                return Not_Available;
            } else {
                return Other;
            }
        }

        /**
         * Compare 2 procurement statuses and return the worse one (the one with the bigger rank).
         * If one of the procurement status is null, return the other.
         *
         * @param procurementStatus1 one of the procurement status to compare
         * @param procurementStatus2 the other procurement status to compare
         * @return the worse (bigger rank) procurement status (or one of the procurement status if the other is null)
         */
        static String compareProcurementStatuses(final String procurementStatus1, final String procurementStatus2) {
            if (procurementStatus1 == null) {
                return procurementStatus2;
            } else if (procurementStatus2 == null) {
                return procurementStatus1;
            }
            ProcurementStatus status1 = ProcurementStatus.fromString(procurementStatus1);
            ProcurementStatus status2 = ProcurementStatus.fromString(procurementStatus2);

            if (status1.isWorseThan(status2)) {
                return procurementStatus1;
            } else {
                return procurementStatus2;
            }
        }
    }    

    // these get appended to the date names to form the names of the actual date elements we are looking for
    private String dayOfPrefix;
    private String monthOfPrefix;
    private String yearOfPrefix;

    private static final String PROCUREMENT_STATUS = "procurement_status";
    private static final String XSD_VER = "xsd_ver";
    private static final String XSD_VER_VALUE = "1.16";
    private static final String TIER = "tier";
    private static final String CDE = "cde";
    private static final String PRECISION_DAY = "day";
    private static final String PRECISION_YEAR = "year";
    private static final String PRECISION_MONTH = "month";
    private static final String PRECISION = "precision";
    private static final String MONTH_WHEN_NO_MONTH = "1";
    private static final String DAY_WHEN_NO_DAY = "1";

    /**
     * Figures out what to return based in the return values of all of the processFile calls.  In thise case, we
     * just want to return the Archive as stored in the context.  (See parent class, AbstractArchiveFileProcessor.)
     *
     * @param results the results of each processFile call.  Map key = File and value = return from processFile
     * @param context the qc context
     * @return the value to return as the result of this enture process
     */
    protected Archive getReturnValue(final Map<File, Archive> results, final QcContext context) {
        return context.getArchive();
    }

    /**
     * What return value should we use if this process is not actually run on the given archive?
     * (See parent class, AbstractArchiveFileProcessor.)
     *
     * @param archive the input archive
     * @return the same archive
     */
    protected Archive getDefaultReturnValue(final Archive archive) {
        return archive;
    }

    /**
     * Gets the file extension to use.  For each file in the archive of this extension, will call processFile.
     * (See parent class, AbstractArchiveFileProcessor.)
     *
     * @return the file extension to use for getting files
     */
    protected String getFileExtension() {
        return ClinicalXmlValidator.XML_EXTENSION;
    }

    /**
     * Is this archive type the right one for this process?
     * @param archive the input archive
     * @return if the process should be run on this archive or not     
     */
    protected boolean isCorrectArchiveType(final Archive archive) {
        return Experiment.TYPE_BCR.equals(archive.getExperimentType());
    }

    /**
     * Retrieve the a Date element from the document (XML) given the element name suffix
     * and place it in a String array so that:
     *  String[0]: year
     *  String[1]: month
     *  String[2]: day
     *
     * @param document the XML document
     * @param xpath an XPath instance
     * @param elementNameSuffix the suffix of the element for which the date is to be retrieved
     * @return a String array that stores each part of the date
     * @throws ProcessorException
     */
    private String[] getElementDate(final Document document, final XPath xpath, final String elementNameSuffix) throws ProcessorException {

        String[] result = new String[3];
        result[0] = getElementValue(getXPathExpressionIgnoringNamespace(yearOfPrefix, elementNameSuffix), document, xpath);
        result[1] = getElementValue(getXPathExpressionIgnoringNamespace(monthOfPrefix, elementNameSuffix), document, xpath);
        result[2] = getElementValue(getXPathExpressionIgnoringNamespace(dayOfPrefix, elementNameSuffix), document, xpath);

        return result;
    }

    /**
     * Gets the element value given the path.
     *
     * @param elementPath xpath path to the element
     * @param document the document
     * @param xpath the xpath object
     * @return the value of the element, or null if element was empty or not found
     * @throws ProcessorException if more than one element for the path was found
     */
    private String getElementValue(final String elementPath, final Document document, final XPath xpath) throws ProcessorException {
        String value = null;
        try {
            final NodeList nodes = getNodeListFromXPathExpression(document, xpath, elementPath);

            if(nodes.getLength() > 1) {
                throw new ProcessorException("Found more than one XML element for " + elementPath + " : found " + nodes.getLength());
            } else {
                if(nodes.getLength() == 1) {
                    value = nodes.item(0).getTextContent().trim();
                    if (value.length() == 0) {
                        value = null;
                    }
                }
            }
        } catch (XPathExpressionException e) {
            // this means the namespace of the xpath wasn't found in this document -- just return null
        }
        return value;
    }

    /**
     * For the given file parameter, removes all elements with names dayOfPrefix/monthOfPrefix/yearOfPrefix[ELEMENT]
     * where ELEMENT is from the list datesToObscure.  Adds an element with name elapsedElementBase[ELEMENT] with
     * value equal to the number of days from the basis date until the given date.  (So, if the date is before the basis
     * date, the value will be negative.)  If the given date has no day then 1 is used.  If the given date has no month
     * then 1 is used.  If the given date has no year, then the elapsed days element will have no value, because the
     * calculation cannot be done.
     *
     * @param file the clinical XML file to operate on
     * @param context the context for this QC call
     * @return the original archive (pulled from the context)
     * @throws ProcessorException if an error occurs during the process
     */
    @Override
    protected Archive processFile(final File file, final QcContext context) throws ProcessorException {

        // only process files that are newly submitted, otherwise are already obscured
        if (!context.getFilesCopiedFromPreviousArchive().contains(file.getName())) {

            try {
                if(getBcrUtils().isClinicalFile(file)) {
                    processClinicalFile(file, context);
                } else if(getBcrUtils().isBiospecimenFile(file)) {
                    processBiospecimenFile(file, context);
                } else if(getBcrUtils().isAuxiliaryFile(file)) {
                    // Do nothing
                }else if(getBcrUtils().isControlFile(file)) {
                    // Do nothing
                }
                else {//Unsupported XML file
                    throw new ProcessorException("Unsupported XML file '" + file.getName() + "'");
                }

            } catch(ParserConfigurationException x) {
                throw new ProcessorException("Parser Configuration error, parsing file " + file.getName() + ": " + x.getMessage());
            } catch(SAXException x) {
                throw new ProcessorException("SAX error, parsing file " + file.getName() + ": " + x.getMessage());
            } catch(IOException x) {
                throw new ProcessorException("I/O error, parsing file " + file.getName() + ": " + x.getMessage());
            } catch(TransformerException x) {
                throw new ProcessorException("Error while dumping content of DOM Document in file '" + file.getAbsolutePath() + "'", x);
            } catch (NoSuchAlgorithmException e) {
                throw new ProcessorException(e.getMessage());
            }
        }
        
        return context.getArchive();
    }

    /**
     * Process a biospecimen file
     *
     * @param file the file to process
     * @param context the context
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ProcessorException
     * @throws TransformerException
     */
    private void processBiospecimenFile(final File file, final QcContext context)
            throws IOException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, ProcessorException, TransformerException {

        context.aboutToChangeFile(file, "Replaced biospecimen date elements with elapsed days elements");

        final XPathXmlParser xPathXmlParser = new XPathXmlParser();
        final Document document = xPathXmlParser.parseXmlFile(file, false);
        XPath xpath = xPathXmlParser.getXPath();

        //Retrieve the time reference date for biospecimen file
        final String[] basisDate = getElementDate(document, xpath, getBasisDateNameForBiospecimen());

        //Set the precision for the time reference
        final String basisPrecision = getBasisPrecision(basisDate[0], basisDate[1], basisDate[2]);

        //Obscure dates in Document
        final Document obscuredDocument = obscureElements(document, xpath, basisDate[0], basisDate[1], basisDate[2], basisPrecision,
                -1L, null, null);

        //Dump Document in file
        dumpDOMDocument(obscuredDocument, file.getAbsolutePath());
    }

    /**
     * Process a clinical file
     *
     * @param file the file to process
     * @param context the context
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ProcessorException
     * @throws TransformerException
     */
    private void processClinicalFile(final File file, final QcContext context)
            throws IOException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, ProcessorException, TransformerException {

        context.aboutToChangeFile(file, "Replaced clinical date elements with elapsed days elements");

        final XPathXmlParser xPathXmlParser = new XPathXmlParser();
        final Document document = xPathXmlParser.parseXmlFile(file, false);
        XPath xpath = xPathXmlParser.getXPath();

        //Retrieve the time reference date and birth date
        final String[] basisDate = getElementDate(document, xpath, getBasisDateNameForClinical());
        final String[] birthDate = getElementDate(document, xpath, getBirthDateName());

        //Set the precision for the time reference
        final String basisPrecision = getBasisPrecision(basisDate[0], basisDate[1], basisDate[2]);

        //Compute Patient's age based on the new time reference
        final long ageAtBasisDate = calculateAgeAtBasisDate(
                basisDate[0], basisDate[1], basisDate[2],
                birthDate[0], birthDate[1], birthDate[2],
                basisPrecision);

        //Retrieve the patient's birth year XML element attributes
        final Hashtable<String, String> patientBirthYearAttributes = getPatientBirthYearAttributes(document, xpath);

        //Obscure dates in Document
        final List<String> bcrPatientOverCutoffAgeBarcodeList = new LinkedList<String>();
        final Document obscuredDocument = obscureElements(document, xpath, basisDate[0], basisDate[1], basisDate[2], basisPrecision,
                ageAtBasisDate, patientBirthYearAttributes, bcrPatientOverCutoffAgeBarcodeList);

        //Dump Document in file
        dumpDOMDocument(obscuredDocument, file.getAbsolutePath());

        //Create a text file that lists the patients barcodes and add it to the archive
        addBcrPatientBarcodesListInArchive(bcrPatientOverCutoffAgeBarcodeList, context);
    }

    /**
     * Create a text file that lists the patients barcodes and add it to the archive
     *
     * @param bcrPatientOverCutoffAgeBarcodeList the list of barcodes of the BCR patients over the cutoff age
     * @param qcContext the context for this QC call
     */
    private void addBcrPatientBarcodesListInArchive(final List<String> bcrPatientOverCutoffAgeBarcodeList, final QcContext qcContext) {

        final Archive archive = qcContext.getArchive();

        //Prepare the file content
        final StringBuilder fileContentStringBuilder = new StringBuilder();
        for(final String bcrPatientBarcode : bcrPatientOverCutoffAgeBarcodeList) {
            fileContentStringBuilder.append(bcrPatientBarcode).append("\n");
        }

        getArchiveUtilImpl().addContentIntoNewFileToArchive(fileContentStringBuilder.toString(), README_FILENAME, archive, qcContext);
    }

    /**
     * This methods parses the XML file a Hashtable of attribute name -> attribute value
     * for the year of birth node found in the XML, with the following attributes:
     *
     * <code>OWNER</code>, <code>PROCUREMENT_STATUS</code> and <code>TIER</code>
     *
     * @param document the DOM XML Document
     * @param xpath an XPath instance
     * @return the Hashtable
     * @throws ProcessorException
     */
    private Hashtable<String, String> getPatientBirthYearAttributes(final Document document, final XPath xpath) throws ProcessorException {

        final Hashtable<String, String> result = new Hashtable<String, String>();

        try {

            final String yearOfBirthExpr = getXPathExpressionIgnoringNamespace(yearOfPrefix, getBirthDateName());

            final String yearOfBirthOwnerNodesXPathExpression = yearOfBirthExpr + "/@" + OWNER;
            final String yearOfBirthProcurementStatusNodesXPathExpression = yearOfBirthExpr + "/@" + PROCUREMENT_STATUS;
            final String yearOfBirthTierNodesXPathExpression = yearOfBirthExpr + "/@" + TIER;

            final NodeList yearOfBirthOwnerNodes = getNodeListFromXPathExpression(document, xpath, yearOfBirthOwnerNodesXPathExpression);
            final NodeList yearOfBirthProcurementStatusNodes = getNodeListFromXPathExpression(document, xpath, yearOfBirthProcurementStatusNodesXPathExpression);
            final NodeList yearOfBirthTierNodes = getNodeListFromXPathExpression(document, xpath, yearOfBirthTierNodesXPathExpression);

            //The 3 nodeList must have the same size as we are assuming that the XML validates
            //against the 'intgen.org_TCGA_ver1_17.xsd' schema
            //Also, the size can either be 0 or 1 but nothing else
            if(yearOfBirthOwnerNodes.getLength() != yearOfBirthProcurementStatusNodes.getLength()) {
                throw new ProcessorException(
                        getNodeListLengthMismatchErrorMessage(
                                yearOfBirthOwnerNodesXPathExpression,
                                yearOfBirthProcurementStatusNodesXPathExpression,
                                yearOfBirthOwnerNodes,
                                yearOfBirthProcurementStatusNodes)
                );
            }

            if(yearOfBirthOwnerNodes.getLength() != yearOfBirthTierNodes.getLength()) {
                throw new ProcessorException(
                        getNodeListLengthMismatchErrorMessage(
                                yearOfBirthOwnerNodesXPathExpression,
                                yearOfBirthTierNodesXPathExpression,
                                yearOfBirthOwnerNodes,
                                yearOfBirthTierNodes)
                );
            }

            if(yearOfBirthOwnerNodes.getLength() > 1) {
                final String xMsg = new StringBuilder("The number of nodes matching the XPath expression '")
                        .append(yearOfBirthOwnerNodesXPathExpression).append("' (Found ")
                        .append(yearOfBirthOwnerNodes.getLength())
                        .append(") is bigger than 1 when only one patient is expected")
                        .toString();
                throw new ProcessorException(xMsg);
            }

            Node yearOfBirthOwnerNode, yearOfBirthProcurementStatusNode, yearOfBirthTierNode;
            String yearOfBirthOwner, yearOfBirthProcurementStatus, yearOfBirthTier;

            //Iterate though the nodes found to build the LinkedList
            for (int nodeIndex = 0; nodeIndex < yearOfBirthOwnerNodes.getLength(); nodeIndex++) {

                yearOfBirthOwnerNode = yearOfBirthOwnerNodes.item(nodeIndex);
                yearOfBirthProcurementStatusNode = yearOfBirthProcurementStatusNodes.item(nodeIndex);
                yearOfBirthTierNode = yearOfBirthTierNodes.item(nodeIndex);

                yearOfBirthOwner = yearOfBirthOwnerNode.getTextContent().trim();
                yearOfBirthProcurementStatus = yearOfBirthProcurementStatusNode.getTextContent().trim();
                yearOfBirthTier = yearOfBirthTierNode.getTextContent().trim();

                //Update result
                result.put(OWNER, yearOfBirthOwner);
                result.put(PROCUREMENT_STATUS, yearOfBirthProcurementStatus);
                result.put(TIER, yearOfBirthTier);
            }
        } catch (XPathExpressionException e) {
            // means namespace of year of birth node not in this file
        }

        return result;
    }

    /**
     * This methods goes through the original XML document and obscure a few selected dates by changing the time reference
     * to a chosen Date element in the XML.
     *
     * @param document the XML DOM Document
     * @param xpath an XPath instance
     * @param basisYear the new time reference year
     * @param basisMonth the new time reference month
     * @param basisDay the new time reference day
     * @param basisPrecision the time reference precision
     * @param ageAtBasisDate the age of the patient based on the new time reference
     * @param patientBirthYearAttributes the birth year attributes for the patient
     * @param bcrPatientOverCutoffAgeBarcodeList a list to store the barcode of patient whose age is over the cutoff age
     * @return the Document with the dates to be obscured replaced by an elapsed time since the new time reference
     * @throws ProcessorException
     */
    private Document obscureElements(final Document document, final XPath xpath,
                                     final String basisYear, final String basisMonth, final String basisDay,
                                     final String basisPrecision, final long ageAtBasisDate,
                                     final Hashtable<String, String> patientBirthYearAttributes, final List<String> bcrPatientOverCutoffAgeBarcodeList)
            throws ProcessorException {

        //Iterate on the list of element's name suffixes to validate the different dates
        final Map<String, String> datesToObscure = getDatesToObscure(document, xpath);
        final Iterator<String> xpathSuffixIterator = datesToObscure.keySet().iterator();
        String xpathSuffix = null;
        while(xpathSuffixIterator.hasNext()) {

            xpathSuffix = xpathSuffixIterator.next();
            String elementNamePrefix = getElapsedElementBase();
            String elementNameSuffix = xpathSuffix;
            String namespacePrefix = null;
            int indexOfColon = xpathSuffix.indexOf(":");
            if (indexOfColon > 0) {
                elementNameSuffix = xpathSuffix.substring(indexOfColon + 1);
                namespacePrefix = xpathSuffix.substring(0, indexOfColon);
            }

            if (namespacePrefix == null || xpath.getNamespaceContext().getNamespaceURI(namespacePrefix) != null) {

                try {
                    final String yearOfNodesAttributesExpression = getXPathExpressionIgnoringNamespace(yearOfPrefix, xpathSuffix);
                    final String monthOfNodesAttributesExpression = getXPathExpressionIgnoringNamespace(monthOfPrefix, xpathSuffix);
                    final String dayOfNodesAttributesExpression = getXPathExpressionIgnoringNamespace(dayOfPrefix, xpathSuffix);

                    final NodeList yearOfNodes = getNodeListFromXPathExpression(document, xpath, yearOfNodesAttributesExpression);
                    final NodeList monthOfNodes = getNodeListFromXPathExpression(document, xpath, monthOfNodesAttributesExpression);
                    final NodeList dayOfNodes = getNodeListFromXPathExpression(document, xpath, dayOfNodesAttributesExpression);

                    // must have the same number of year, month, and day nodes
                    if(yearOfNodes.getLength() != monthOfNodes.getLength()) {
                        throw new ProcessorException(
                                getNodeListLengthMismatchErrorMessage(
                                        yearOfNodesAttributesExpression,
                                        monthOfNodesAttributesExpression,
                                        yearOfNodes,
                                        monthOfNodes)
                        );
                    }

                    if(yearOfNodes.getLength() != dayOfNodes.getLength()) {
                        throw new ProcessorException(
                                getNodeListLengthMismatchErrorMessage(
                                        yearOfNodesAttributesExpression,
                                        dayOfNodesAttributesExpression,
                                        yearOfNodes,
                                        dayOfNodes)
                        );
                    }

                    //Iterate through the selected element names that need to be obscured and obscure the dates
                    for (int nodeIndex = 0; nodeIndex < yearOfNodes.getLength(); nodeIndex++) {

                        final Node yearOfNode = yearOfNodes.item(nodeIndex);
                        final Node monthOfNode = monthOfNodes.item(nodeIndex);
                        final Node dayOfNode = dayOfNodes.item(nodeIndex);
                        final Node parentNode = yearOfNode.getParentNode();

                        final String yearOf = yearOfNode.getTextContent().trim();
                        final String monthOf = monthOfNode.getTextContent().trim();
                        final String dayOf = dayOfNode.getTextContent().trim();
                        
                        String monthOfProcurementStatus = null;
                        String yearOfOwner = null;
                        String yearOfProcurementStatus = null;

                        final Node yearOfProcurementStatusNode = yearOfNode.getAttributes().getNamedItem(PROCUREMENT_STATUS);
                        if (yearOfProcurementStatusNode != null) {
                            yearOfProcurementStatus = yearOfProcurementStatusNode.getTextContent().trim();
                        }

                        final Node yearOfOwnerNode = yearOfNode.getAttributes().getNamedItem(OWNER);
                        if (yearOfOwnerNode != null) {
                            yearOfOwner = yearOfOwnerNode.getTextContent().trim();
                        }

                        final Node monthOfProcurementStatusNode = monthOfNode.getAttributes().getNamedItem(PROCUREMENT_STATUS);
                        if (monthOfProcurementStatusNode != null) {
                            monthOfProcurementStatus = monthOfProcurementStatusNode.getTextContent().trim();
                        }

                        if(parentNode != null) {
                            // find the namespace from the yearOf node
                            String namespace = "";
                            String yearNodeName = yearOfNode.getNodeName();
                            if (yearNodeName.contains(":")) {
                                namespace = yearNodeName.substring(0, yearNodeName.indexOf(":") + 1); // include the ':'
                            }
                            //Update document

                            //Replace dayOfPrefix node by elapsedElementBase node

                            String elementValue = "";
                            String cdeAttributeValue = datesToObscure.get(xpathSuffix);
                            String ownerAttributeValue = yearOfOwner;

                            String elementPrecision = getPrecisionForElementDate(yearOf, monthOf, dayOf, basisPrecision);
                            boolean elementValueFloored = false;
                            if(elementPrecision.equals(PRECISION_YEAR) || elementPrecision.equals("")) {
                                // set precision to empty since we are not going to do the calculation
                                elementPrecision = "";
                            } else {
                                Date elementDate = makeDateFromParts(yearOf, monthOf, dayOf, elementPrecision);
                                Date basisDate = makeDateFromParts(basisYear, basisMonth, basisDay, elementPrecision);
                                Long elapsedDays = calculateElapsedDaysBetween(elementDate, basisDate);

                                //The 'days to birth' value needs to be floored if it's lower than a lower bound
                                if(xpathSuffix.equals(getBirthDateName()) && elapsedDays < getDaysToBirthLowerBound()) {
                                    elapsedDays = getDaysToBirthLowerBound().longValue();
                                    elementValueFloored = true;
                                }

                                elementValue = (elapsedDays==null?null:String.valueOf(elapsedDays));
                            }

                            // Procurement status should be set to 'Completed' if the element has a non blank value,
                            // otherwise it should be set to the year of's procurement status
                            final String procurementStatusAttributeValue = StringUtils.isBlank(elementValue) ? yearOfProcurementStatus : ProcurementStatus.Completed.toString();
                            final Node yearOfNodeTierAttribute = yearOfNode.getAttributes().getNamedItem(TIER);
                            final String tierAttributeValue = (yearOfNodeTierAttribute!=null?yearOfNodeTierAttribute.getTextContent().trim():null);

                            String xsdVerAttribute = getXsdVersionAttributeValue(yearOfNode);
                            final String precisionAttributeValue = elementPrecision;

                            Node daysToNode = createElementNode(document, namespace + elementNamePrefix, elementNameSuffix, elementValue,
                                    cdeAttributeValue,
                                    ownerAttributeValue,
                                    procurementStatusAttributeValue,
                                    tierAttributeValue,
                                    xsdVerAttribute,
                                    precisionAttributeValue,
                                    elementValueFloored);

                            parentNode.replaceChild(daysToNode, dayOfNode);


                            //Remove monthOfPrefix node if <code>xpathSuffix</code> is different from <code>basisDateNameForClinical</code>,
                            //otherwise replace it by a new ageAtPrefix + <code>basisDateNameForClinical</code> Element
                            if(!xpathSuffix.equals(getBasisDateNameForClinical())) {

                                removeChildNode(parentNode, monthOfNode);
                            } else {

                                boolean ageAtBasisDateValueFloored = false;
                                String ageAtBasisDateValue = "";
                                if(ageAtBasisDate != -1) {

                                    if(ageAtBasisDate > getCutoffAgeAtInitialDiagnosis()) {
                                        ageAtBasisDateValue = getCutoffAgeAtInitialDiagnosis().toString(); // the patient's age > cutoff age, floor it at the cutoff age
                                        ageAtBasisDateValueFloored = true;

                                        //add the patient barcode to the list of patient whose age is over the cutoff age
                                        bcrPatientOverCutoffAgeBarcodeList.add(getBcrPatientBarcode(document, xpath));

                                    } else {
                                        ageAtBasisDateValue = String.valueOf(ageAtBasisDate);
                                    }
                                }

                                final String ageAtOwnerAttributeValue = patientBirthYearAttributes.get(OWNER);
                                final String yearOfBirthProcurementStatus = patientBirthYearAttributes.get(PROCUREMENT_STATUS);

                                // Procurement status should be set to 'Completed' if the element has a non blank value,
                                // otherwise it should be set to the patient year of birth's procurement status
                                final String ageAtProcurementStatusAttributeValue =
                                        StringUtils.isBlank(ageAtBasisDateValue) ? yearOfBirthProcurementStatus : ProcurementStatus.Completed.toString();
                                
                                final String ageAtTierAttributeValue = patientBirthYearAttributes.get(TIER);

                                final String monthOfNodeNamespace = getNamespaceFromNodeName(monthOfNode);

                                final Node ageAtNode = createElementNode(document, monthOfNodeNamespace + ageAtPrefix, elementNameSuffix, ageAtBasisDateValue,
                                        ageAtBasisDateCDE,
                                        ageAtOwnerAttributeValue,
                                        ageAtProcurementStatusAttributeValue,
                                        ageAtTierAttributeValue,
                                        xsdVerAttribute,
                                        null,
                                        ageAtBasisDateValueFloored);

                                parentNode.replaceChild(ageAtNode, monthOfNode);
                            }

                            //Remove yearOfPrefix node if:
                            // <code>xpathSuffix</code> is different from <code>basisDateNameForClinical</code>
                            if(!xpathSuffix.equals(getBasisDateNameForClinical())) {
                                removeChildNode(parentNode, yearOfNode);
                            }

                        } else {
                            final String xMsg = "The Parent Node is null for the XPath expression: " + yearOfPrefix + xpathSuffix + "[" + nodeIndex + "]";
                            throw new ProcessorException(xMsg);
                        }
                    }

                } catch(XPathExpressionException x) {
                    throw new ProcessorException("Xpath Expression error", x);
                }
            } // else this date is in a namespace not in this document, so skip it, since it must not be in here (or would have failed validation)
        }

        return document;
    }

    private String getXsdVersionAttributeValue(final Node yearOfNode) {
        final Node yearOfNodeXsdVerAttribute = yearOfNode.getAttributes().getNamedItem("xsd_ver");
        String xsdVerAttribute = XSD_VER_VALUE;
        if (yearOfNodeXsdVerAttribute != null) {
            xsdVerAttribute = yearOfNodeXsdVerAttribute.getTextContent().trim();
            /*
             * Explanation of the following block: XSD 1.16 was the first one in which we introduced the
             * elapsed days elements.  So any date elements that were already in the XSD had their
             * days_to_ element added in XSD 1.16.  So if the year element was added before version 1.16
             * then use 1.16 as the version.  If the year was added later than 1.16, then use whatever
             * the year version is, because that should be when the elapsed element was added too.
             */
            final String[] xsdVerParts = xsdVerAttribute.split("\\.");
            if (xsdVerParts.length > 1) {
                try {
                    final Integer majorVersion = Integer.valueOf(xsdVerParts[0]);
                    final Integer minorVersion = Integer.valueOf(xsdVerParts[1]);
                    if (majorVersion == 1 && minorVersion < 16) {
                        xsdVerAttribute = XSD_VER_VALUE;
                    }
                } catch (NumberFormatException e) {
                    // continue with what we found... this should not happen, but just in case, don't die
                }
            }
        }
        return xsdVerAttribute;
    }

    /**
     * Return a standard error message when 2 XPath expressions returned 2 <code>NodeList</code> of different sizes
     *
     * @param xPathExpression1 the first XPath expression
     * @param xpathExpression2 the second XPath expression
     * @param nodeList1 the NodeList resulting from the first XPath expression
     * @param nodeList2 the NodeList resulting from the second XPath expression
     * @return a standard error message when 2 XPath expressions returned 2 <code>NodeList</code> of different sizes
     */
    private String getNodeListLengthMismatchErrorMessage(final String xPathExpression1,
                                                         final String xpathExpression2,
                                                         final NodeList nodeList1,
                                                         final NodeList nodeList2) {

        return new StringBuilder("The number of nodes matching the XPath expression '")
                .append(xPathExpression1)
                .append("' (Found ")
                .append(nodeList1.getLength())
                .append(") is different from the number of nodes matching the XPath expression '")
                .append(xpathExpression2)
                .append("' (Found ")
                .append(nodeList2.getLength())
                .append(")")
                .toString();
    }

    /**
     * Return the patient's BCR barcode
     *
     * @param document the XML DOM Document
     * @param xpath an XPath instance
     * @return the patient's BCR barcode
     * @throws ProcessorException
     */
    private String getBcrPatientBarcode(final Document document, final XPath xpath) throws ProcessorException {
        return getElementValue(getXPathExpressionIgnoringNamespace(null, getBcrPatientBarcodeElementName()), document, xpath);
    }

    /**
     * This method removes a child from its parent (how horrible) and any preceding newlines and whitespace
     *
     * @param parent the Node holdind the child to be removed
     * @param child the Node to be removed
     */
    private void removeChildNode(final Node parent, final Node child) {

        Node childPreviousSibling = child.getPreviousSibling();

        if(childPreviousSibling != null
                && childPreviousSibling.getNodeType() == Node.TEXT_NODE
                && childPreviousSibling.getTextContent().trim().equals("")) {

            //We want to remove newlines (and whitespace) preceding the child to be removed
            //so has to avoid blank lines in the XML after the child is removed
            parent.removeChild(childPreviousSibling);
        }

        //Remove child
        parent.removeChild(child);
    }

    /**
     * Creates a new DOM element which name starts with the <code>elapsedElementBase</code> value
     * and ends with the given <code>elementName</code>. This element will also have the following attributes
     * with the values for these attributes provided in the method arguments
     *
     * Note:
     * The org.w3c.dom API does not care in what order the attributes are added to a Node,
     * and the resulting DOM Document will display the attributes in alpabetical order, based
     * on the attribute's name
     *
     * @param document the document being modified
     * @param elementNamePrefix the prefix of the element name
     * @param elementNameSuffix the suffix of the element name
     * @param elementValue the value for the element created
     * @param cdeAttributeValue the value for the <code>CDE</code> attribute
     * @param ownerAttributeValue the value for the <code>OWNER</code> attribute
     * @param procurementStatusAttributeValue the value for the <code>PROCUREMENT_STATUS</code> attribute
     * @param tierAttributeValue the value for the <code>TIER</code> attribute
     * @param xsdVerAttributeValue the value for the <code>XSD_VER</code> attribute
     * @param precisionAttributeValue the value of the <code>PRECISION</code> attribute
     * @param floored <code>true</code> if the value has been floored, <code>false</code> otherwise
     * @return the new DOM element
     */
    private Node createElementNode(final Document document, final String elementNamePrefix, final String elementNameSuffix, final String elementValue,
                                   final String cdeAttributeValue,
                                   final String ownerAttributeValue,
                                   final String procurementStatusAttributeValue,
                                   final String tierAttributeValue,
                                   final String xsdVerAttributeValue,
                                   final String precisionAttributeValue,
                                   final boolean floored) {

        //Create the Element
        final Element result = document.createElement(elementNamePrefix + elementNameSuffix);

        //Set Element value
        if(elementValue != null) {

            final Text textNode = document.createTextNode(elementValue);
            result.appendChild(textNode);
        }
        //Set cde attribute
        result.setAttribute(CDE, cdeAttributeValue);

        //Set procurement_status attribute
        if (procurementStatusAttributeValue != null) {
            result.setAttribute(PROCUREMENT_STATUS, procurementStatusAttributeValue);
        }

        //Set owner attribute
        if (ownerAttributeValue != null) {
            result.setAttribute(OWNER, ownerAttributeValue);
        }

        //Set xsd_ver attribute
        result.setAttribute(XSD_VER, xsdVerAttributeValue);

        //Set tier attribute
        //
        //Note: this attribute is not allowed for the element 'DAYSTOCOLLECTION', according to the 'intgen.org_TCGA_ver1_17.xsd' schema
        //so if the attribute value provided is null it will not be added
        if(tierAttributeValue != null) {
            result.setAttribute(TIER, tierAttributeValue);
        }

        //Set precision attribute
        //
        //Note: this attribute is optional, according to the 'intgen.org_TCGA_ver1_17.xsd' schema
        //so if if the attribute value provided is null or empty, it will not be added
        if (precisionAttributeValue != null && precisionAttributeValue.length() > 0) {
            result.setAttribute(PRECISION, precisionAttributeValue);
        }

        //Add an attribute that indicate if the element's value has been floored
        if(floored) {
            result.setAttribute(FLOORED, "true");
        }

        return result;
    }

    /**
     * This method dumps the content of a W3C DOM Document in the given filename
     *
     * @param document the DOM Document to dump
     * @param filename the name of the file in which the content will be dumped
     * @throws TransformerException
     */
    public static void dumpDOMDocument(final Document document, final String filename) throws TransformerException {
        
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer();

        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(new File(filename));

        //Dumping DOM content
        transformer.transform(source, result);
    }

    /**
     * Returns the precision for a given date
     *
     * @param year the date's year
     * @param month the date's month
     * @param day the date's day
     * @param basisPrecision the precision of the date element that serves as the new time reference
     * @return the precision for the given date
     */
    private String getPrecisionForElementDate(final String year, final String month, final String day, final String basisPrecision) {

        String precision = PRECISION_DAY;

        if (day == null || day.trim().length() == 0 || (basisPrecision != null && basisPrecision.equals(PRECISION_MONTH))) {
            precision = PRECISION_MONTH;
        }

        if (month == null || month.trim().length() == 0 || (basisPrecision != null && basisPrecision.equals(PRECISION_YEAR))) {
            precision = PRECISION_YEAR;
        }

        if (year == null || year.trim().length() == 0 || basisPrecision == null ) {
            precision = "";
        }

        return precision;
    }

    /**
     * Return the patient's age based on the new time reference and the precision
     *
     * @param basisYear the new year of reference
     * @param basisMonth the new month of reference
     * @param basisDay the new day of reference
     * @param birthYear the patient's real birth year
     * @param birthMonth the patient's real birth month
     * @param birthDay the patient's real birth day
     * @param basisPrecision the precision of the new time reference
     * @return the patient's age based on the new time reference and the precision
     */
    public long calculateAgeAtBasisDate(
            final String basisYear, final String basisMonth, final String basisDay, final String birthYear,
            final String birthMonth, final String birthDay, final String basisPrecision) {

        Date basisDate = makeDateFromParts(basisYear, basisMonth, basisDay, basisPrecision);
        Date birthDate = makeDateFromParts(birthYear, birthMonth, birthDay, basisPrecision);
        if (basisDate == null || birthDate == null) {
            return -1;
        }

        // adjust the age if the month/day value is less than birth date
        long ageAtBasisDate = -1;
        if ((basisYear != null) && (birthYear != null)) {
            ageAtBasisDate = Integer.parseInt(basisYear) - Integer.parseInt(birthYear);
            boolean reduce = false;
            if ((basisMonth != null) && (birthMonth != null)) {
                if (Integer.parseInt(basisMonth) < Integer.parseInt(birthMonth)) {
                    reduce = true;
                }
                if(!reduce) {
                    if (basisMonth.equals(birthMonth)) {
                        if ((basisDay != null) &&(birthDay != null)) {
                            if(Integer.parseInt(basisDay) < Integer.parseInt(birthDay)) {
                                reduce = true;
                            }
                        }
                    }
                }
            }

            if (reduce) {
                    ageAtBasisDate--;
            }
        }

        return ageAtBasisDate;
    }

    /**
     * Returns a date built with a given year, month and day, taking into account the level of precision
     * of the new time reference date
     *
     * @param year the year
     * @param month the month
     * @param day the day
     * @param precision the precision
     * @return the date with the given year, month and day according to the precision of the new time reference date
     */
    private Date makeDateFromParts(final String year, String month, String day, final String precision) {

        Date result = null;

        if (year != null && !year.trim().equals("") && precision != null) {

            if (month == null || month.trim().equals("") || precision.equals(PRECISION_YEAR)) {
                month = MONTH_WHEN_NO_MONTH;
            }

            if (day == null || day.trim().equals("") || precision.equals(PRECISION_MONTH) || precision.equals(PRECISION_YEAR)) {
                day = DAY_WHEN_NO_DAY;
            }

            // note: SimpleDateFormat objects are not synchronized, so can't have an instance variable for this, in case
            // multiple threads are using this processor at once.  Could have one per thread, and pass it around as parameter?
            SimpleDateFormat dateFormatForParsing = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = day + "-" + month + "-" + year;

            try {
                //Update result
                result = dateFormatForParsing.parse(dateString);

            } catch (ParseException e) {
                // not sure what to do in this case... should not happen, unless non-numeric value in one of parts
                // but that should mean that the date is invalid, therefore null.  Don't think processing needs to be halted...?
            }
        }

        return result;
    }

    /**
     * Returns the number of days between 2 dates
     *
     * @param elementDate the Date for which the elapsed time is being calculated
     * @param basisDate the time reference Date
     * @return the number of days between <code>basisDate</code> (the time reference) and <code>elementDate</code>
     */
    private Long calculateElapsedDaysBetween(final Date elementDate, final Date basisDate) {
        if (elementDate == null || basisDate == null) {
            return null;
        }
        long millisecondsBetween = elementDate.getTime() - basisDate.getTime();
        return millisecondsBetween / MILLISECONDS_PER_DAY;        
    }

    /**
     * Returns the XML tag prefix to prepend to the XML tag element being obscured
     * (after having removed the <code>yearOfPrefix</code>, <code>monthOfPrefix</code> or <code>dayOfPrefix</code> part in the tag being obscured)
     *
     * @return XML tag prefix
     */
    public String getElapsedElementBase() {
        return this.elapsedElementBase;
    }

    /**
     * Sets the XML tag prefix to prepend to the XML tag element being obscured
     * (after having removed the <code>yearOfPrefix</code>, <code>monthOfPrefix</code> or <code>dayOfPrefix</code> part in the tag being obscured)
     *
     * @param elapsedElementBase the XML tag prefix to prepend to the XML tag element being obscured
     */
    public void setElapsedElementBase(final String elapsedElementBase) {
        this.elapsedElementBase = elapsedElementBase;
    }

    /**
     * Parses the given <code>Document</code> for all the date Nodes (containing <code>yearOfPrefix</code>, <code>monthOfPrefix</code> or <code>dayOfPrefix</code>);
     * Builds a <code>Map</code> out of all the dates found (as the Map keys) with an empty CDE value for each date.
     * Completes the Map with the <code>cdeForDatesToObscure</code> in order to get the CDE values for known dates.
     * Return the Map after removing all dates found in </code>datesNotToObscure</code>.
     *
     * The Map keys are the XML tag suffixes that, when prefixed by <code>yearOfPrefix</code>, <code>monthOfPrefix</code> and <code>dayOfPrefix</code>
     * give the dates to obscure, and which values are the the value to put in the <code>cde</code> attribute for the obscured date element.
     *
     * @param document the <code>Document</code> to parse
     * @param xpath an <code>XPath</code> instance
     * @return the list of the XML tag suffixes
     * @throws ProcessorException
     */
    private Map<String, String> getDatesToObscure(final Document document, final XPath xpath) throws ProcessorException {

        final Map<String, String> result = new HashMap<String, String>();

        // Initialize the map to include all dates found in the document
        final List<Node> yearOfNodes = getAllYearOfDatesFromDocument(document, xpath);
        for (final Node node : yearOfNodes) {
            result.put(getDateNameFromNodeName(node), ""); // No CDE value by default
        }

        // Update the Map with the CDE values for known dates to obscure
        result.putAll(getCdeForDatesToObscure());

        // Remove dates NOT to obscure
        for(final String dateNotToObscure : getDatesNotToObscure()) {
            result.remove(dateNotToObscure);
        }

        return result;
    }

    /**
     * Return the date name from the given <code>Node</code>, assuming that this node name matches <code>yearOfPrefix</code>
     *
     * @param node the <code>Node</code>
     * @return the date name from the given <code>Node</code>, assuming that this node name matches <code>yearOfPrefix</code>
     */
    private String getDateNameFromNodeName(final Node node) {
        return getDateNameFromNodeName(node, yearOfPrefix);
    }

    private String getDateNameFromNodeName(final Node node, final String prefix) {


        String result = "";

        final String nodeName = node.getNodeName();
        final int indexOfColon = nodeName.indexOf(prefix);

        if (indexOfColon > 0) {
            result = nodeName.substring(indexOfColon + prefix.length());
        }

        return  result;
    }

    /**
     * Return the namespace from the given <code>Node</code> (prefixed by ":" if there is a namespace)
     *
     * @param node the <code>Node</code>
     * @return the namespace from the given <code>Node</code> (prefixed by ":" if there is a namespace)
     */
    private String getNamespaceFromNodeName(final Node node) {

        String result = node.getPrefix();

        //If a namespace was found, prefix it by ":"
        if(!StringUtils.isBlank(result)) {
            result += ":";
        }

        return  result;
    }

    /**
     * Parses the given XML <code>Document</code> and return a <code>NodeList</code> of all XML Nodes that match <code>yearOfPrefix<code>.
     *
     * @param document the <code>Document</code> to parse
     * @param xpath an <code>XPath</code> instance
     * @return a <code>NodeList</code> of all XML Nodes that match <code>yearOfPrefix<code>
     * @throws ProcessorException if there are not as many nodes matching <code>monthOfPrefix<code> and <code>dayOfPrefix<code>
     */
    private List<Node> getAllYearOfDatesFromDocument(Document document, XPath xpath) throws ProcessorException {

        // Get all the date Nodes from the Document
        final String yearOfNodesAttributesExpression = getXPathExpressionForAnyElementContaining(yearOfPrefix);
        final String monthOfNodesAttributesExpression = getXPathExpressionForAnyElementContaining(monthOfPrefix);
        final String dayOfNodesAttributesExpression = getXPathExpressionForAnyElementContaining(dayOfPrefix);

        final NodeList yearOfNodes;
        final NodeList monthOfNodes;
        final NodeList dayOfNodes;

        try {
            yearOfNodes = getNodeListFromXPathExpression(document, xpath, yearOfNodesAttributesExpression);
            monthOfNodes = getNodeListFromXPathExpression(document, xpath, monthOfNodesAttributesExpression);
            dayOfNodes = getNodeListFromXPathExpression(document, xpath, dayOfNodesAttributesExpression);

            // new rule: if a year_of node has no month or day, ignore it since its not a full date
            // this means year-only elements will not be obscured
            Map<String, Node> yearOfDates = new HashMap<String, Node>();
            Set<String> monthOfDates = new HashSet<String>();
            Set<String> dayOfDates = new HashSet<String>();

            for (int i=0; i<yearOfNodes.getLength(); i++) {
                yearOfDates.put(getDateNameFromNodeName(yearOfNodes.item(i), yearOfPrefix), yearOfNodes.item(i));
            }
            for (int i=0; i<monthOfNodes.getLength(); i++) {
                monthOfDates.add(getDateNameFromNodeName(monthOfNodes.item(i), monthOfPrefix));
            }
            for (int i=0; i<dayOfNodes.getLength(); i++) {
                dayOfDates.add(getDateNameFromNodeName(dayOfNodes.item(i), dayOfPrefix));
            }

            final List<Node> dateYears = new ArrayList<Node>();
            for (final String yearOfDate : yearOfDates.keySet()) {
                if (monthOfDates.contains(yearOfDate) && dayOfDates.contains(yearOfDate)) {
                    dateYears.add(yearOfDates.get(yearOfDate));
                } // else, does not have month and day so not complete date so ignore it -- if the schema is like that, it means the date doesn't need to be obscured
            }

              return dateYears;

        } catch (final XPathExpressionException e) {
            throw new ProcessorException("Error while getting dates element from XML Document: " + e.getMessage(), e);
        }
    }

    /**
     * Return a <code>NodeList</code> of all XML Nodes matching the XPath expression in the <code>Document</code>
     *
     * @param document the <code>Document</code> to parse
     * @param xpath an <code>XPath</code> instance
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

    public String getBasisDateNameForClinical() {
        return this.basisDateNameForClinical;
    }

    public void setBasisDateNameForClinical(final String basisDateNameForClinical) {
        this.basisDateNameForClinical = basisDateNameForClinical;
    }

    public String getBasisDateNameForBiospecimen() {
        return basisDateNameForBiospecimen;
    }

    public void setBasisDateNameForBiospecimen(final String basisDateNameForBiospecimen) {
        this.basisDateNameForBiospecimen = basisDateNameForBiospecimen;
    }

    /**
     * Returns the precision for the time reference
     *
     * @param basisYear the year of the time reference
     * @param basisMonth the month of the time reference
     * @param basisDay the day of the time reference
     * @return the precision for the time reference
     */
    public String getBasisPrecision(final String basisYear, final String basisMonth, final String basisDay) {

        String result = PRECISION_DAY;

        if (basisYear == null || basisYear.length() == 0) {
            //Update result
            result = null;

        } else if (basisMonth == null || basisMonth.length() == 0) {
            //Update result
            result = PRECISION_YEAR;

        } else if (basisDay == null || basisDay.length() == 0) {
            //Update result
            result = PRECISION_MONTH;
        }

        return result;
    }

    /**
     * Sets the XML tag suffix that, when prefixed by <code>yearOfPrefix</code>, <code>monthOfPrefix</code> and <code>dayOfPrefix</code>,
     * gives the patient's birth date
     *
     * @param birthDateName the XML tag suffix
     */
    public void setBirthDateName(final String birthDateName) {
        this.birthDateName = birthDateName;
    }

    /**
     * Returns the XML tag suffix that, when prefixed by <code>yearOfPrefix</code>, <code>monthOfPrefix</code> and <code>dayOfPrefix</code>,
     * gives the patient's birth date
     *
     * @return the XML tag suffix
     */
    public String getBirthDateName() {
        return this.birthDateName;
    }

    /**
     * Returns the name of the processor in readable language.
     * @return "clinical date obscurer"
     */
    public String getName() {
        return "clinical date obscurer";
    }

    public BCRUtils getBcrUtils() {
        return bcrUtils;
    }

    public void setBcrUtils(final BCRUtils bcrUtils) {
        this.bcrUtils = bcrUtils;
    }
}
