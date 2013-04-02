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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.ArchiveLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.PatientBarcodeValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalObject;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalTable;
import org.apache.log4j.Level;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clinical data loader class is used to load clinical
 * archives received from BCRs.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalLoaderCaller implements ArchiveLoader {

    private Map<String, ClinicalTable> cachedClinicalTables = new HashMap<String, ClinicalTable>();
    private Map<Pattern, ClinicalTable> dynamicClinicalTables;
    private ClinicalLoaderQueries clinicalLoaderQueries;
    private ArchiveQueries archiveQueries;
    private BarcodeUuidResolver barcodeUuidResolver;
    private static final String BIO_TYPE = "bio";
    private static final String PATIENT_XPATH = "//tcga_bcr/patient";
    private static final String CONTROL_XPATH = "//tcga_bcr/controls/control";
    private static final String DYNAMIC_ATTRIBUTE_NAME = "version";
    private ArchiveLogger archiveLogger;
    private Logger logger;
    private String validClinicalPlatforms;
    private PlatformTransactionManager transactionManager;
    List<String> newElements;

    private String mailTo;
    private MailSender mailSender;

    private BCRUtils bcrUtils;

    /**
     * Enum of the different file types we expect to see in clinical archives.
     */
    public enum FileType {
        Biospecimen, Clinical, Control
    }


    /**
     * Loads archives to the database. This method relies on the qcLive to create the archive records.
     *
     * @param archivesToLoad list of archives to load
     */
    @Override
    public void load(List<Archive> archivesToLoad, QcLiveStateBean stateContext) throws ClinicalLoaderException {

        if (archivesToLoad != null && archivesToLoad.size() > 0) {
            try {            	
                archivesToLoad.get(0).getArchiveFile().getCanonicalPath();
            } catch (IOException e) {
                throw new ClinicalLoaderException(" unable to get file path from first archive = " + archivesToLoad.get(0).getArchiveName());
            }
            for (final Archive archive : archivesToLoad) {
            	if(stateContext != null && stateContext.getTransactionId() != null && stateContext.getTransactionId() > 0){
            		archiveLogger.addTransactionLog(this.getClass().getSimpleName(),stateContext.getTransactionId());
            	}
            	
                DiseaseContextHolder.setDisease(archive.getTheTumor().getTumorName());
                loadArchiveByName(archive.getArchiveName(),stateContext);
                
                if(stateContext != null && stateContext.getTransactionId() != null && stateContext.getTransactionId() > 0){
                	archiveLogger.addArchiveLog(archive, " archive loading completed");
                    archiveLogger.updateTransactionLogRecordResult(stateContext.getTransactionId(), this.getClass().getSimpleName(), true);
                    archiveLogger.endTransaction(stateContext.getTransactionId(), true);
            	}
                
                              
           }

        }
    }


    /**
     * This method returns the type of loader as String
     */
    @Override
    public ArchiveLoaderType getLoaderType() {
        return ArchiveLoaderType.CLINICAL_LOADER;
    }

    /**
     * Loads an archive by name
     *
     * @param archiveName
     * @throws ClinicalLoaderException
     */
    public void loadArchiveByName(final String archiveName,QcLiveStateBean stateContext) throws ClinicalLoaderException {    	    
        newElements = new ArrayList<String>();
    	// start TX
    	DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    	transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    	TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
    	
	    // find this archive in the db
	    long archiveId = archiveQueries.getArchiveIdByName(archiveName);
	    
	    try{	    		    		    	
	        if (archiveId != -1) {
	            loadArchiveById(archiveId);
	        } else {
	            // do we need an email notification for this?
	            // if yes, throw clinical loading exception
	            logger.log(Level.ERROR, "Archive with name " + archiveName + "not found -- skipping");
	        }	        
    	}catch (Exception ex){
            logger.log(ex);
    		// if any exception is thrown in the loader, rollback partial data.
    		transactionManager.rollback(status);
    		
    		if(stateContext != null && stateContext.getTransactionId() != null && stateContext.getTransactionId() > 0){
	    		// log an error in archiveLog
	    		archiveLogger.endTransaction(stateContext.getTransactionId(), false); 
	    		archiveLogger.addErrorMessage(stateContext.getTransactionId(),archiveName, ex.getMessage());
    		}
    		//rethrow for the job to recover and run other archives
    	    throw new ClinicalLoaderException (ex.getMessage(),ex);    	        	    
    	}
    	transactionManager.commit(status);

       // if new xsd elements have been added to the database send an email with this information
       if (0 < newElements.size()) {
            sendEmail(archiveName);
       }
     }

    private void loadArchiveById(final Long archiveId) throws ClinicalLoaderException {
    	    	
    	
        final Archive archive = archiveQueries.getArchive(archiveId.intValue());
        DiseaseContextHolder.setDisease(archive.getTheTumor().getTumorName());
        // load it!

        loadArchive(archive);              
    }

    /**
     * Loads an archive to the database.
     *
     * @param archive to load
     * @throws ClinicalLoaderException
     */
    protected void loadArchive(final Archive archive) throws ClinicalLoaderException {
        if (!Experiment.TYPE_BCR.equals(archive.getExperimentType())) {
            throw new ClinicalLoaderException("Archive " + archive.getRealName() + " is not a BCR archive so can't be loaded");
        }
        // find the valid platforms for clinical archives
        String[] validPlatforms = validClinicalPlatforms.split(",");
        List validList = Arrays.asList(validPlatforms);
        if (!validList.contains(archive.getPlatform().toLowerCase())) {
            throw new ClinicalLoaderException("Archive " + archive.getRealName() + " is not a clinical platform archive so can't be loaded");
        }
        /**
         * added per ticket number # APPS-2343
         */        
        if (!Archive.STATUS_AVAILABLE.equals(archive.getDeployStatus())) {
            throw new ClinicalLoaderException("Archive " + archive.getRealName() + " does not have status 'Available' so can't be loaded");
        }

        final File[] xmlFiles = getArchiveXmlFiles(archive);
        if (xmlFiles == null || xmlFiles.length < 1) {
            // do we need an e mail notification for this?
            // if yes, throw clinical loading exception
            logger.log(Level.ERROR, "Archive " + archive.getRealName() + " has no XML files (path = " +
                    archive.getDeployDirectory() + ")");

        } else {
            for (final File xmlFile : xmlFiles) {

                final boolean isAuxiliary = getBcrUtils().isAuxiliaryFile(xmlFile);

                if(!isAuxiliary) {

                    final FileType fileType;
                    final ClinicalObject root;
                    if (xmlFile.getName().toLowerCase().contains(BIO_TYPE)) {
                        fileType = FileType.Biospecimen;
                        root = parseBiospecimenXmlFile(xmlFile, archive);
                    } else if (xmlFile.getName().toLowerCase().contains("clin")) {
                        fileType = FileType.Clinical;
                        root = parseClinicalXmlFile(xmlFile, archive);
                    } else if (xmlFile.getName().toLowerCase().contains("control")) {
                        fileType = FileType.Control;
                        root = parseControlXmlFile(xmlFile, archive);
                    } else {
                        throw new ClinicalLoaderException("File " + xmlFile.getName() + ": can't determine file type");
                    }

                    try {
                        save(root, fileType, archive);
                        // if there are new elements that have been added send an email
                    } catch (UUIDException e) {
                        throw new ClinicalLoaderException(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Parses a clinical-type XML file and returns the root patient object.  Patient children will be things like
     * drug therapies, radiation treatments, etc.  Patient attributes will be the main clinical attributes.
     *
     * @param clinicalXmlFile the clinical XML file to parse
     * @param archive         the archive where the XML file was found
     * @return the root patient object
     * @throws ClinicalLoaderException if there is an error while parsing the XML file
     */
    protected ClinicalObject parseClinicalXmlFile(final File clinicalXmlFile, final Archive archive) throws ClinicalLoaderException {
        try {
            return parsePatient(clinicalXmlFile, archive);

        } catch (ParserConfigurationException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (XPathExpressionException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        }

    }

    /**
     * Parses a control file.  First parses it like a biospecimen file to get the patient and associated biospecimens,
     * and then parses the control elements and adds them to the patient object.
     *
     * @param xmlFile the control xml file
     * @param archive the archive the file is from
     * @return ClinicalObject representing the patient, with any control elements added as children
     * @throws ClinicalLoaderException
     */
    protected ClinicalObject parseControlXmlFile(final File xmlFile, final Archive archive) throws ClinicalLoaderException {
        try {
            final ClinicalObject patient = parseBiospecimenXmlFile(xmlFile, archive);
            addControlsToPatient(xmlFile, archive, patient);
            return patient;

        } catch (ParserConfigurationException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (XPathExpressionException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        }
    }

    /**
     * Parses a biospecimen-type clinical XML file and returns the root patient object. The patient children will be
     * samples, etc.  Note since in a biospecimen-type file the patient element has no value nodes, the patient barcode
     * will be parsed from the filename.  This isn't really ideal but it's the best we can do for now.
     *
     * @param xmlFile the file to parse
     * @param archive the archive where the file was found
     * @return the root patient object
     * @throws ClinicalLoaderException if there is an error while parsing the file
     */
    protected ClinicalObject parseBiospecimenXmlFile(final File xmlFile, final Archive archive) throws ClinicalLoaderException {
        try {
            final ClinicalObject patient = parsePatient(xmlFile, archive);
            patient.setBarcode(getPatientBarcodeFromFilename(xmlFile.getName()));
            return patient;

        } catch (ParserConfigurationException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        } catch (XPathExpressionException e) {
            throw new ClinicalLoaderException(e.getMessage(), e);
        }
    }

    // protected so tests can override

    protected String getPatientBarcodeFromFilename(final String fileName) throws ClinicalLoaderException {
        final Matcher matcher = PatientBarcodeValidator.BARCODE_PATTERN.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new ClinicalLoaderException("Biospecimen file name " + fileName + " does not contain a valid patient barcode");
        }
    }

    private ClinicalObject parsePatient(final File xmlFile, final Archive archive) throws ParserConfigurationException, IOException,
            SAXException, XPathExpressionException, ClinicalLoaderException {
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        final XPath xpath = XPathFactory.newInstance().newXPath();

        final NodeList nodes = (NodeList) xpath.evaluate(PATIENT_XPATH, document, XPathConstants.NODESET);
        if (nodes.getLength() == 0) {
            throw new ClinicalLoaderException("No patient node found in file " + xmlFile.getName());
        }
        if (nodes.getLength() > 1) {
            throw new ClinicalLoaderException("More than one patient node found in file " + xmlFile.getName());
        }

        return parse(null, nodes.item(0), archive);
    }

    private void addControlsToPatient(final File xmlFile, final Archive archive, final ClinicalObject patientObject) throws ParserConfigurationException,
            IOException, SAXException, XPathExpressionException {
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        final XPath xpath = XPathFactory.newInstance().newXPath();

        // we pass in the patient as the parent even though in the XML the control is outside of patient
        // but we need to save it as a child of the patient
        final NodeList controlNodes = (NodeList) xpath.evaluate(CONTROL_XPATH, document, XPathConstants.NODESET);
        for (int i=0; i<controlNodes.getLength(); i++) {
            final ClinicalObject controlObject = parse(patientObject, controlNodes.item(i), archive);
            patientObject.addChild(controlObject);
        }
    }



    /**
     * Saves the given object, recursing through all children and saving each one.
     *
     * @param rootObject the root of the object hierarchy
     * @param fileType   the type of file that the objects were parsed from
     * @param archive    the archive the objects are in
     * @throws gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException
     *          if there was an error assigning/getting the UUID for the object
     */
    protected void save(final ClinicalObject rootObject, final FileType fileType, final Archive archive) throws UUIDException {
        saveElement(rootObject, -1, fileType, archive);

    }

    // recursive save!

    private void saveElement(final ClinicalObject clinicalObject, final long parentId, final FileType fileType, final Archive archive) throws UUIDException {

        long id = clinicalLoaderQueries.getId(clinicalObject);
        // some objects have no barcode (protocol, rna, dna)... those also don't have UUIDs
        if (clinicalObject.getBarcode() != null) {
            final Barcode barcodeDetail = barcodeUuidResolver.resolveBarcodeAndUuid(clinicalObject.getBarcode(),
                    clinicalObject.getUuid(), archive.getTheTumor(), archive.getTheCenter(), true);
            clinicalObject.setUuid(barcodeDetail.getUuid());
        }

        if (id < 1) {
            // if it's not there, insert it
            id = clinicalLoaderQueries.insert(clinicalObject, parentId, archive.getId(),newElements);
        } else {
            // if it is there, update it...
            // except if this is a patient object for a non-clinical file -- since that has no attributes
            // we want to skip the update because we don't want to delete all the attributes from the db
            if ("patient".equals(clinicalObject.getObjectType()) && fileType != FileType.Clinical) {
                clinicalLoaderQueries.addArchiveLink(clinicalObject, archive.getId());
            } else {
                clinicalLoaderQueries.update(clinicalObject, archive.getId(), newElements);
            }
        }

        // now recurse on each child object
        for (final ClinicalObject child : clinicalObject.getChildren()) {
            child.setParentId(parentId);
            child.setParentTable(clinicalObject.getClinicalTable());
            saveElement(child, id, fileType, archive);
        }

    }

    private boolean isSimpleTextElement(final Node node) {
        // either the node has no children, or it has just one child of type text node
        // (i.e. false if it has other element nodes nested inside of it
        return !node.hasChildNodes() ||
                (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE);
    }

    /**
     * for development and unit test enviroments,
     * make sure that InsertClinicalMetaData.sql script has been run before, otherwise you'll get a NPE
     */
    // recursive!
    private ClinicalObject parse(final ClinicalObject parentElement, final Node node, final Archive archive) {
        ClinicalObject clinicalObject = null;
        if (!isSimpleTextElement(node)) {
            // this node has children which have their own values, so it's not just a value node itself...

            // 1. check if this node represents a clinical object that we are interested in
            final Long parentId = getClinicalTableId(parentElement);
            final ClinicalTable clinicalTable = getClinicalTable(getNodeNameWithoutNamespace(node), parentId);
            if (clinicalTable != null) {
                // (will be null if the node doesn't represent a valid clinical table)
                clinicalObject = new ClinicalObject();
                clinicalObject.setClinicalTable(clinicalTable);
                clinicalObject.setArchive(archive);
                clinicalObject.setObjectType(getNodeNameWithoutNamespace(node));
                if(clinicalTable.isDynamic()) {
                    StringBuffer dynamicIdentifier = new StringBuffer(getNodeNameWithoutNamespace(node));
                    final Attr attr  = ((Element)node).getAttributeNode(DYNAMIC_ATTRIBUTE_NAME);
                    if(attr != null){
                        dynamicIdentifier.append("_v")
                        .append(attr.getValue());
                    }

                    clinicalObject.setDynamicIdentifier(dynamicIdentifier.toString());
                }
            }

            // now iterate through all the child nodes of this node
            final NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);

                // of the child node is an element node
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    // recursively call parse on the child
                    final ClinicalObject childElement = parse(clinicalObject == null ? parentElement : clinicalObject,
                            child, archive);
                    // if the child produced a clinical object, add it as a child to either this node or the parent node
                    if (childElement != null) {
                        if (clinicalObject != null) {
                            clinicalObject.addChild(childElement);
                        } else if (parentElement != null) {
                            // this means that the current node isn't a valid clinical object node, so we add the child
                            // object directly to the parent.  This happens for example in the case where the XML looks like this:
                            // <sample>
                            //   <portions>
                            //      <portion> ...
                            // In that case, the portion object should be added to the sample object and not to the "portions" object
                            // which isn't a valid object type anyway!
                            parentElement.addChild(childElement);
                        }
                    }
                }
            }

        } else {
            // if this is a simple value node (such as <name>Jessica</name> then add attribute to parent, if parent exists
            if (parentElement != null) {
                addAttribute(parentElement, node);
            }
        }

        return clinicalObject;
    }

    /**
     * Return the clinical table Id for the given {@link ClinicalObject} or <code>null</code> if none.
     *
     * @param clinicalObject the {@link ClinicalObject}
     * @return the clinical table Id for the given {@link ClinicalObject} or <code>null</code> if none
     */
    private Long getClinicalTableId(final ClinicalObject clinicalObject) {

        Long result = null;

        if(clinicalObject != null && clinicalObject.getClinicalTable() != null) {
            result = clinicalObject.getClinicalTable().getClinicalTableId();
        }

        return result;
    }

    private void addAttribute(final ClinicalObject element, final Node attributeNode) {
        final String attributeName = getNodeNameWithoutNamespace(attributeNode);
        final String attributeValue = attributeNode.getTextContent();

        if ((attributeValue == null || attributeValue.trim().length() == 0) &&
                clinicalLoaderQueries.elementRepresentsClinicalTable(attributeName)) {
            // this is an empty clinical object (such as tumor_pathology, etc), so should not be added as an attribute
            return;
        }

        // if the name of the node matches the name of the barcode element or uuid element for the type of object we are parsing,
        // then set the barcode or uuid to the value.  Otherwise just add a generic attribute.

        if (attributeName.equals(element.getClinicalTable().getBarcodeElementName())) {
            element.setBarcode(attributeValue);
        } else if (attributeName.equals(element.getClinicalTable().getUuidElementName())) {
            if (attributeValue != null && attributeValue.trim().length() > 0) {
                element.setUuid(attributeValue);
            }
        } else {
            element.addAttribute(attributeName, attributeValue);
        }
    }

    /**
     * Return the {@link ClinicalTable} for the given element name and parent Id.
     *
     * @param tableElementName the clinical table element name
     * @param parentId the clinical table parent Id
     * @return the {@link ClinicalTable} for the given element name and parent Id
     */
    private ClinicalTable getClinicalTable(final String tableElementName,
                                           final Long parentId) {

        if (dynamicClinicalTables == null) {
            dynamicClinicalTables = new HashMap<Pattern, ClinicalTable>();
            List<ClinicalTable> dynamicTables = clinicalLoaderQueries.getDynamicClinicalTables();
            for (final ClinicalTable clinicalTable : dynamicTables) {
                dynamicClinicalTables.put(Pattern.compile(clinicalTable.getElementNodeName()), clinicalTable);
            }
        }

        final String compositeKey = tableElementName + "<" + parentId + ">";

        if (cachedClinicalTables.containsKey(compositeKey)) {
            return cachedClinicalTables.get(compositeKey);
        } else {
            ClinicalTable  clinicalTable = null;

            for (final Pattern dynamicTablePattern : dynamicClinicalTables.keySet()) {
                if (dynamicTablePattern.matcher(tableElementName).matches()) {
                    clinicalTable = dynamicClinicalTables.get(dynamicTablePattern);
                }
            }
            if (clinicalTable == null) {
                clinicalTable = clinicalLoaderQueries.getClinicalTableForElementName(tableElementName, parentId);
            }
            cachedClinicalTables.put(compositeKey, clinicalTable);

            return clinicalTable;
        }
    }

    private String getNodeNameWithoutNamespace(final Node node) {
        String name = node.getNodeName();
        int indexOfColon = name.indexOf(":");
        if (indexOfColon > 0) {
            return name.substring(indexOfColon + 1);
        } else {
            return name;
        }
    }

    private void sendEmail(String archiveName) {
        final String subject = "New xsd elements were added from archive: " + archiveName;
        final StringBuilder body = new StringBuilder();
        body.append("The following new xsd elements were added to the database: \n");
        for (final String xsdElement : newElements) {
            body.append(xsdElement);
            body.append("\n");
        }
        mailSender.send( mailTo, null, subject, body.toString(), false );
    }

    private String getCenterEmail(Archive archive) {
        if (archive != null && archive.getTheCenter() != null) {
            return archive.getTheCenter().getCommaSeparatedEmailList();
        }
        return null;
    }
    protected File[] getArchiveXmlFiles(final Archive archive) {
        return DirectoryListerImpl.getFilesByExtension(archive.getDeployDirectory(), ".xml");
    }

    public void setClinicalLoaderQueries(final ClinicalLoaderQueries clinicalLoaderQueries) {
        this.clinicalLoaderQueries = clinicalLoaderQueries;
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    public void setArchiveLogger(ArchiveLogger archiveLogger) {
        this.archiveLogger = archiveLogger;
    }

    public void setCachedClinicalTables(Map<String, ClinicalTable> cachedClinicalTables) {
        this.cachedClinicalTables = cachedClinicalTables;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setValidClinicalPlatforms(String validClinicalPlatforms) {
        this.validClinicalPlatforms = validClinicalPlatforms;
    }

    public String getValidClinicalPlatforms() {
        return this.validClinicalPlatforms;
    }

    public void setBarcodeUuidResolver(final BarcodeUuidResolver barcodeUuidResolver) {
        this.barcodeUuidResolver = barcodeUuidResolver;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public BCRUtils getBcrUtils() {
        return bcrUtils;
    }

    public void setBcrUtils(final BCRUtils bcrUtils) {
        this.bcrUtils = bcrUtils;
    }

}
