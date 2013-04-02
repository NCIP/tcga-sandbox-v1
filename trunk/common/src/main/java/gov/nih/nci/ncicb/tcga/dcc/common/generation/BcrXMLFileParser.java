package gov.nih.nci.ncicb.tcga.dcc.common.generation;

import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template.ElementFilterType;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template.TcgaBcrDataTemplate;
import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template.TemplateType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses the given xml document as per the given template
 * This class is used by BioTabFileGenerator to extract xml element names
 * and values.
 * This class is thread safe if the APIs are called in
 * in the following order
 *  - initializeParser
 *  - getDocument
 *  - parseDocument
 *  - cleanupParser
 * Please make sure to call the cleanup Parser after parsing is done. Otherwise thread
 * local variables will not be cleaned-up.
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BcrXMLFileParser {
    private static final int INCLUDE_ELEMENTS = 1;
    private static final int IGNORE_ELEMENTS = 2;
    private DocumentBuilderFactory documentBuilderFactory;
    private XPathFactory xpathFactory;
    // When key element is not provided in the template,generated key will be used
    // to store the elements data in the order. The key will be generated in the asc order
    private static final ThreadLocal<String> generatedKey = new ThreadLocal<String>();
    private static final ThreadLocal<DocumentBuilder>  documentBuilder = new ThreadLocal<DocumentBuilder>();
    private static final ThreadLocal<XPath>  xpath = new ThreadLocal<XPath>();
    private final Log logger = LogFactory.getLog(getClass());

    /**
     * Initialize documentBuilderFactory and xpathFactory.
     * This API should be called only from the spring configuration file as an init-method.
     */
    public void init() {
        // initialize document builder
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // initialize xpathFactory
        xpathFactory = XPathFactory.newInstance();
    }

    /**
     * This is the first API that should be called before calling any other APIs
     * This initializes the documentBuilder and xpathfactory
     * @throws ParserConfigurationException
     */
    public void initializeParser() throws ParserConfigurationException {
        documentBuilder.set(documentBuilderFactory.newDocumentBuilder());
        xpath.set(xpathFactory.newXPath());
        generatedKey.set("");
    }

    /**
     * Creates document object for the given xml file.
     * initializeParser API should be called before calling this API.
     * @param xmlFile
     * @return The DOM {@link Document} obtained from parsing the file
     * @throws {@link SAXException}
     * @throws {@link IOException}
     */
    public Document getDocument(final String xmlFile) throws ParserConfigurationException,SAXException,IOException{
        
            final DocumentBuilder documentBuilder = getDocumentBuilder();
            if(documentBuilder == null){
                initializeParser();
            }
            // As we are reusing the same document builder and xpath reset them
            documentBuilder.reset();
            return documentBuilder.parse(xmlFile);                
    }

    /**
     * Creates dynamic data nodes xpath from the given parent path.
     * @param document
     * @param parentPath
     * @return list of dynamic nodes xpath
     * @throws XPathExpressionException
     */

    public List<String> getDynamicDataNodes(final Document document,
                                            final String parentPath)throws XPathExpressionException{
        Set<String> dynamicNodes = new HashSet<String>();
        final XPath xpath = getXPath();
        xpath.reset();
        final NodeList dataNodes = (NodeList) xpath.evaluate(parentPath, document, XPathConstants.NODESET);
        for(int i=0; i < dataNodes.getLength(); i++){
            final Node node = dataNodes.item(i);
            if( node.getNodeType() == Node.ELEMENT_NODE ){
                String name = node.getNodeName();
                //remove name space
                name = name.substring(name.indexOf(":")+1);
                //create the data node path
                final String path = parentPath.replace("*",name);
                dynamicNodes.add(path);
            }
        }
        return new ArrayList<String>(dynamicNodes);
    }
    /**
     * Parses given xml document as per the template and returns elements names and values.
     *
     * @param document is the DOM {@link Document} that represents the input XML file
     * @param tcgaBcrDataTemplate is a {@link TcgaBcrDataTemplate} representing which elements within the
     * input XML Document should be returned that matches the template specification
     * @param fileName that is used for parsing document
     * @return Map<String,String> A Map of template key names to their corresponding value matches within
     * the Document
     * @throws XPathExpressionException
     */

    public Map<String,Map<String,String>> parseDocument(final Document document,
                                                        final TcgaBcrDataTemplate tcgaBcrDataTemplate,
                                                        final String fileName) throws XPathExpressionException{
        final Map<String,Map<String,String>> elementDataByKeyName = new HashMap<String, Map<String,String>>();
        final XPath xpath = getXPath();
        xpath.reset();

        // sometimes the parent node can be empty, in that case get only data node elements
        if(tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getParentNode().getAbsoluteXPath() != null){
            final List<String> includedElementsList = tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getParentNode().getElementsToBeIncluded().getElementName();
            final String alternateAttribute = tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getDataNode().getAlternateAttributeValue();
            if(alternateAttribute == null){
                throw new XPathExpressionException(tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType()+" template does not contain alternate attribute. Please specify the dataNode->alternateAttributeValue in the template");
            }
            try{
            	final NodeList parentNodes = (NodeList) xpath.evaluate(tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getParentNode().getAbsoluteXPath(), document, XPathConstants.NODESET);
		    	
            	for(int i=0; i < parentNodes.getLength(); i++){
				             // get parent elements
		            final Map<String,String> parentElementDataByName = getElementsData(parentNodes.item(i),
		                                                                                includedElementsList,
		                                                                                alternateAttribute,
		                                                                                ElementFilterType.INCLUDE);
		            extractDataNodeElements(tcgaBcrDataTemplate,
		                    parentNodes.item(i),
		                    elementDataByKeyName,
		                    parentElementDataByName);
		        }
            	
            }catch (XPathExpressionException e){
            	
            	logger.error(
            			" XPathExpression while evaluating an expression  " 
            			+ tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getParentNode().getAbsoluteXPath()
            			+ " for Biotab file type " + tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType() 
            			+ " for this file " + fileName);
            	
            	throw e;
            }           

        }else{
            Map<String,String> parentElementDataByName = new HashMap<String,String>();
            extractDataNodeElements(tcgaBcrDataTemplate,
                    document,
                    elementDataByKeyName,
                    parentElementDataByName);

        }


        return elementDataByKeyName;
    }


    /**
     * Traverse through the parentNode object using an {@link XPath} query obtained from the {@link TcgaBcrDataTemplate}.
     * Retrieve a {@link NodeList} of child nodes that match the XPath query.
     * Add the parentElementDataByName to the child nodes list found and
     * key them off a generated String separated by underscores consisting of element names.
     * @param tcgaBcrDataTemplate
     * @param parentNode
     * @param elementDataByKeyName
     * @param parentElementDataByName
     * @throws XPathExpressionException
     */
    private void extractDataNodeElements(final TcgaBcrDataTemplate tcgaBcrDataTemplate,
                                        final Object parentNode,
                                        final Map<String,Map<String,String>> elementDataByKeyName,
                                        final Map<String,String> parentElementDataByName) throws XPathExpressionException{

        final List<String> elementsList  = tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getDataNode().getElements().getElementName();
        final String alternateAttribute = tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getDataNode().getAlternateAttributeValue();
        final ElementFilterType filterType = tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getDataNode().getElements().getFilterType();
        final NodeList childNodes = (NodeList) getXPath().evaluate(tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getDataNode().getRelativePath(),
                parentNode,
                XPathConstants.NODESET);

        // For each child get the child elements
        for(int j=0; j < childNodes.getLength(); j++){

            final Map<String,String> childElementDataByName = getElementsData(childNodes.item(j),
                                                                            elementsList,
                                                                            alternateAttribute,
                                                                            filterType);
            // add parent elements
            childElementDataByName.putAll(parentElementDataByName);

            // now store the child element data in  elementDataByKeyName indexed by key element names
            String key = null;
            final TemplateType templateType = tcgaBcrDataTemplate.getType();
            // for dynamic templates get the file suffix as key
            if (templateType != null &&
                    templateType.equals(TemplateType.DYNAMIC)) {
                final String attributeName = tcgaBcrDataTemplate.getTcgaBcrData().getXmlData().getDataNode().getDynamicFileSuffixAttribute();
                if(!StringUtils.isEmpty(attributeName)) {
                    final Attr attr  = ((Element)childNodes.item(j)).getAttributeNode(attributeName);
                    if(attr != null){
                        key = attr.getValue();
                    }
                }
                // if key is null, log error message
                if(key == null) {
                    logger.error("Error occurred while generating biotab files for "
                    + tcgaBcrDataTemplate.getTcgaBcrData().getBiotabFileType()
                    + ". the dynamic attribute value is not specified in the xml file.");
                }
            }
            if(key == null){

                key = getKeyElementValues(childElementDataByName,
                    tcgaBcrDataTemplate.getTcgaBcrData().getBiotabData().getKeyColumns().getColumnName());
            }


            final Map<String,String> existingData = elementDataByKeyName.get(key);

            if(existingData != null){
                childElementDataByName.putAll(existingData);
            }
            // store the data in elementDataByKeyName
            elementDataByKeyName.put(key,childElementDataByName);
        }
    }

    /**
     * Extracts elements name and value from the xml node and stores them in the hash map.
     * If the filter type is include, then it extracts only the elements in the filter element list.
     * If the filter type is ignore, then it extracts elements other than in the filter element list.
     * @param node
     * @param filterElementsList
     * @param alternateAttribute
     * @param filterType
     * @returns map which contains the extracted elements name and data.
     */
    private Map<String,String> getElementsData(final Node node,
                                               final List<String> filterElementsList,
                                               final String alternateAttribute,
                                               final ElementFilterType filterType){

        final Map<String,String> elementDataByName = new HashMap<String,String>();

        final NodeList  childElements = node.getChildNodes();
        for(int i=0; i < childElements.getLength() ; i++){
            String elementName = getElementNameWithoutNameSpace(childElements.item(i).getNodeName());
            elementName = (elementName.contains(":")?elementName.substring(elementName.indexOf(":")+1):elementName);

            Boolean parseNode = false;

            if(filterType.equals(ElementFilterType.INCLUDE)){
                parseNode = filterElementsList.contains(elementName);
            }else if(filterType.equals(ElementFilterType.IGNORE)){
                parseNode = !filterElementsList.contains(elementName);
            }
            if(parseNode){
                extractData(childElements.item(i),alternateAttribute, elementDataByName);
            }
        }
        return elementDataByName;
    }

    /**
     * Recursive API which extracts all the elements name and value for the given XML node.
     * If the value is empty then, it gets the alternate attribute value.
     * If the element has more than one value it stores them with comma delimiter
     * @param node
     * @param alternateAttribute
     * @param dataMap
     * @return A Map of element name to values
     */
    private Map<String,String> extractData(final Node node,
                                           final String alternateAttribute,
                                           final Map<String,String> dataMap){
        if(node == null){
            return dataMap;
        }
        if( node.getNodeType() == Node.ELEMENT_NODE &&
                (!node.hasChildNodes()
                || (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE))){
            // extract data only from nodes which contains procurement attribute
            final Attr attr  = ((Element)node).getAttributeNode(alternateAttribute);
            String value = node.getTextContent();
            if(StringUtils.isEmpty(value) && attr != null){
                final String attributeValue = attr.getValue();
                // if value is null get the attribute value
                if(!alternateAttribute.isEmpty()){
                    value = "["+attributeValue+"]";
                }
            }
            // if value already exists for the same same element, add delimiter
            value = (StringUtils.isEmpty(value))?"null":value;
            //if value contains \n or \t replace them with blank
            value = value.replaceAll("\n","");
            value = value.replaceAll("\t","");
            final String nodeName = getElementNameWithoutNameSpace(node.getNodeName());
            final String existingValue = dataMap.get(nodeName);
            value = (existingValue == null)?value:existingValue+"|"+value;
            dataMap.put(nodeName,value);
        }else{
            if(node.getNodeType() == Node.ELEMENT_NODE){
                NodeList elementList = node.getChildNodes();
                for(int i=0; i < elementList.getLength();i++){
                    extractData(elementList.item(i),alternateAttribute, dataMap);
                }
            }
        }
        return dataMap;
    }

    /**
     * Given a List of strings, if each String exists as a key in the input elementsDataByName,
     * append it to a generated key separated by the _ character
     * @param elementsDataByName
     * @param keyElements
     * @return A generated key consisting of found matches of keyElements in elementsDataByName key
     */
    private String getKeyElementValues(final Map<String,String> elementsDataByName,
                                       final List<String> keyElements){
        final StringBuilder key = new StringBuilder();
        if(!keyElements.isEmpty()){
            for(final String keyElement: keyElements){
                if(elementsDataByName.get(keyElement) != null){
                    key.append(elementsDataByName.get(keyElement))
                    .append("_");
                }else{
                    logger.warn("XML File doesn't contain key element " + keyElement);
                    key.append(" ");
                }

            }
            if(key.toString().endsWith("_")){
                key.deleteCharAt(key.length()-1);
            }
        }else{
            key.append(getGeneratedKey());
        }
        return key.toString();
    }

    private String getElementNameWithoutNameSpace(final String elementName){
        return (elementName.contains(":")?elementName.substring(elementName.indexOf(":")+1):elementName);
    }

    public void cleanupParser(){
        documentBuilder.remove();
        xpath.remove();
        generatedKey.remove();
    }


    private DocumentBuilder getDocumentBuilder(){
        return documentBuilder.get();
    }


    private XPath getXPath(){
        return xpath.get();
    }

    /**
     * Generates key in the ascending order. The generated key will looks like A,B,... Z,ZA,ZB,...
     * @return generated key
     */
    protected String getGeneratedKey(){
        final StringBuffer generatedKeyInASCOrder = new StringBuffer(generatedKey.get());

        Character lastChar = (generatedKeyInASCOrder.toString().isEmpty())?' ':generatedKeyInASCOrder.charAt(generatedKeyInASCOrder.length()-1);
        Character nextChar = (lastChar.equals(' ')|| (lastChar.equals('Z') )) ?'A':Character.toChars( lastChar+1)[0];
        if(lastChar.equals('Z') || lastChar.equals(' ')){
            generatedKeyInASCOrder.append(nextChar);
        }else{
            generatedKeyInASCOrder.replace(generatedKeyInASCOrder.length()-1,generatedKeyInASCOrder.length(),nextChar.toString());
        }
        generatedKey.set(generatedKeyInASCOrder.toString());
        return generatedKey.get();
    }
}
