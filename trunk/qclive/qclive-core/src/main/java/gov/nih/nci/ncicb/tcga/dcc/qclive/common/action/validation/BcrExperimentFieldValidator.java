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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Checks xml tags in incoming archive against
 * against clinical_xsd_element table  returns falls if a tag in the files does not match what's in the db
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BcrExperimentFieldValidator extends AbstractProcessor<Experiment, Boolean> {

    private String clinicalPlatform;
    private ClinicalLoaderQueries clinicalLoaderQueries;
    private static final String PATIENT_XPATH = "//tcga_bcr/patient";    
        

    /**
     * Entry method for this class,
     *
     * @param experiment
     * @param context the context for this QC call
     * @return True if the tags in the experiment were successfuly validated against the db
     * @throws ProcessorException
     */
    @Override    
    protected Boolean doWork(Experiment experiment, QcContext context) throws ProcessorException {

        // set up elements cache
        List<String> clinicalElementList  = clinicalLoaderQueries.getClinicalXsdElements();             
        boolean passed = true;       
        if(!Experiment.TYPE_BCR.equals( experiment.getType() )) {
            // only checks BCR archives
            return passed;
        }               
        // if platform is "bio" then check XML files
        if(experiment.getPlatformName().equals(clinicalPlatform)) {
            for(final Archive archive : experiment.getArchivesForStatus(Archive.STATUS_UPLOADED)) {
                context.setArchive( archive );
                final File[] xmlFiles = DirectoryListerImpl.getFilesByExtension( archive.getDeployDirectory(), ClinicalXmlValidator.XML_EXTENSION );
                for (File xmlFile:xmlFiles){
                    passed = checkXmlFile(xmlFile,context,clinicalElementList) && passed;    
                }
                if (!passed){
                    archive.setDeployStatus(Archive.STATUS_INVALID);                    
                }
            }
        }
        // if the validation fails the message is printed from parse method below.
        return passed;
    }    

    /**
     * checks all tags in a file
     *  @param xmlFile to check for XSDElements
     */

    private boolean checkXmlFile(final File xmlFile,final QcContext context,List<String> clinicalElementList) throws ProcessorException {
        NodeList nodes = null;
        try{
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            final XPath xpath = XPathFactory.newInstance().newXPath();
            nodes = (NodeList) xpath.evaluate(PATIENT_XPATH, document, XPathConstants.NODESET );
        }catch (XPathException e){            
             throw new ProcessorException ("Unable to evaluate " + PATIENT_XPATH + " expression ",e);
        }catch (Exception e){
            // same handling no matter what is thrown by parser
            throw new ProcessorException ("Unable to parse file: " + xmlFile.getName(),e);
        }

        if (nodes.getLength() == 0) {
            throw new ProcessorException ("No patient node found in file " + xmlFile.getName());
        }
        if (nodes.getLength() > 1) {
            throw new ProcessorException("More than one patient node found in file " + xmlFile.getName());
        }

        return parse(nodes.item(0),context,clinicalElementList,xmlFile.getName());

    }

    /**
     *  Parses an xml file and validates elements.
     * @param node
     * @return  True if valid, false otherwise
     */
    private boolean parse (final Node node,QcContext context,List<String> clinicalElementList, String xmlFileName) {        
        if (!isSimpleTextElement(node)) {
            // now iterate through all the child nodes of this node
            final NodeList children = node.getChildNodes();
            boolean isValid = true;
            for (int i=0; i<children.getLength(); i++) {
                final Node child = children.item(i);
                 // of the child node is an element node

                // printing patients
                 if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
                    isValid = parse(child,context, clinicalElementList,xmlFileName) && isValid;
                 }
            }
            return isValid;
        }else{
            // todo: examine if there is a better way to extract name prefix
            // looks like because we're using Xpath to extract the elements, the namespace prefix is 0
            // and resulting node names have this format "prefix:name"
            String nodeName  = null;
            if (node.getNodeName().contains(":")){
                String [] nodeNameList = node.getNodeName().split(":");
                if(nodeNameList != null && nodeNameList.length > 1){
                    nodeName = nodeNameList[1];
                }
            }
            
            // if this is a simple value node (such as <name>Jessica</name> then add attribute to parent, if parent exists
            if (clinicalElementList.contains(nodeName) ){
                return true;
            }
            else{
            	String errMsg = MessageFormat.format(
            			MessagePropertyType.XML_FILE_PROCESSING_ERROR,
            			xmlFileName,
            			"XSDElement validator did not find " + nodeName + " in the database.");
            	context.addError(errMsg);
                LogFactory.getLog(getClass()).debug(errMsg);
                return false;
            }
        }
    }

    /**
     * Determines if a tag is a simple text element
     * @param node to determine if it is a simple text element
     * @return true if a node is a simple text eleement
     */
    private boolean isSimpleTextElement(final Node node) {
        // either the node has no children, or it has just one child of type text node
        // (i.e. false if it has other element nodes nested inside of it
        return !node.hasChildNodes() ||
                (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE);
    }

    /**
     * To fullfill the contract with the interface , return the validator name
     * @return
     */
    @Override
    public String getName() {
        return "BCR experiment field validation";
    }

    /**
     * Parses XSD exclusions and returns a list of elements to exclude
     * @param patternsToParse a string with pattens to parse
     * @return list of parsed
     */
    private List parseExclusions(String patternsToParse){
        if (patternsToParse != null && patternsToParse.length() > 0){
            return Arrays.asList(patternsToParse.split(","));
        }else{
            return null;
        }
    }

    public void setClinicalPlatform(String clinicalPlatform) {
        this.clinicalPlatform = clinicalPlatform;
    }

    public void setClinicalLoaderQueries(ClinicalLoaderQueries clinicalLoaderQueries) {
        this.clinicalLoaderQueries = clinicalLoaderQueries;
    }   
}
