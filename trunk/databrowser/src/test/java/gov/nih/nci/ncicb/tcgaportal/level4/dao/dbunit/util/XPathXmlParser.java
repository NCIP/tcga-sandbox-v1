package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class XPathXmlParser implements XmlParser {

    private InputSource inputSource;
    private XPath xPath;

    public XPathXmlParser() {
        final XPathFactory factory = XPathFactory.newInstance();
        setXPath(factory.newXPath());
    }

    public XPath getXPath() {
        return xPath;
    }

    public void setXPath(final XPath xPath) {
        this.xPath = xPath;
    }

    /**
     * Set the XML file we want to use and set it to the input source.
     *
     * @param file the xml file we want to parse
     * @throws java.io.FileNotFoundException
     */
    public void setFile(final File file) throws FileNotFoundException {
        inputSource = new InputSource(new FileInputStream(file));
    }

    public NodeList getNodes(final Document doc,
                             final String xPathExpression) throws XPathExpressionException, TransformerException {
        return org.apache.xpath.XPathAPI.selectNodeList(doc, xPathExpression);
    }

    public XPathExpression compileXpathExpression(final String expression) throws XPathExpressionException {
        return getXPath().compile(expression);
    }

    public Document parseXmlFile(final String filename,
                                 final boolean validating) throws ParserConfigurationException, IOException, SAXException {
        // Create a builder factory
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        // Create the builder and parse the file
        return factory.newDocumentBuilder().parse(new File(filename));
    }

}
