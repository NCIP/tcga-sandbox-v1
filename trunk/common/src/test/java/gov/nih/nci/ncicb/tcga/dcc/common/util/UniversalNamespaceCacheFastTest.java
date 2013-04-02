package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for UniversalNamespaceCache
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UniversalNamespaceCacheFastTest {
    private UniversalNamespaceCache universalNamespaceCache;

    private static final String TEST_FILE = 
    	Thread.currentThread().getContextClassLoader().getResource("samples/xml/complex_namespace.xml").getPath();

    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        final Document document = factory.newDocumentBuilder().parse(new File(TEST_FILE));
        universalNamespaceCache = new UniversalNamespaceCache(document, false);
    }

    @Test
    public void testGetNamespaceURI()  {
    	System.out.println(TEST_FILE);
        assertEquals("http://tcga.nci/bcr/xml/administration/2.3", universalNamespaceCache.getNamespaceURI("admin"));
        assertEquals("http://tcga.nci/bcr/xml/biospecimen", universalNamespaceCache.getNamespaceURI("bios"));
        assertNull(universalNamespaceCache.getNamespaceURI("bubbles"));
    }

    @Test
    public void testGetPrefix() {
        assertEquals("bios", universalNamespaceCache.getPrefix("http://tcga.nci/bcr/xml/biospecimen"));
        assertNull(universalNamespaceCache.getPrefix("dolphins"));
    }
}
