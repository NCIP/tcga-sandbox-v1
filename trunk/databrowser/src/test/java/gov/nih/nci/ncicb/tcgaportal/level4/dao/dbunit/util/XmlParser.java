package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface XmlParser {

    void setFile(File file) throws FileNotFoundException;

    NodeList getNodes(Document doc, String xPathExpression) throws XPathExpressionException, TransformerException;

    Document parseXmlFile(String filename,
                          boolean validating) throws ParserConfigurationException, IOException, SAXException;

    XPathExpression compileXpathExpression(String expression) throws XPathExpressionException;

}
