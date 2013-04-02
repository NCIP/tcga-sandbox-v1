package gov.nih.nci.ncicb.tcga.dcc.common.generation;

import gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template.TcgaBcrDataTemplate;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for BcrXMLFileParser
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BcrXMLFileParserFastTest {

    private static final String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String sampleXMLFile = SAMPLE_DIR
            + "bcrXmlFiles" + File.separator + "intgen.org_biospecimen.TCGA-00-1111.xml";

    private static final String templatesDir =
            Thread.currentThread().getContextClassLoader().getResource("schema").getPath() + File.separator;

    private BcrXMLFileParser bcrXMLFileParser;
    private static ApplicationContext appContext;
    private static final String appContextFile = "samples/applicationContext-fast.xml";
    private String templateFile = "protocol.xml";

    @Before
    public void setup() throws Exception {
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        bcrXMLFileParser = (BcrXMLFileParser) appContext.getBean("bcrXmlFileParser");
        bcrXMLFileParser.initializeParser();
    }

    @Test
    public void getDocument() throws Exception {
        assertNotNull(bcrXMLFileParser.getDocument(sampleXMLFile));
    }

    @Test
    public void parseDocument() throws Exception {
        final Map<String, Map<String, String>> expectedData = new HashMap<String, Map<String, String>>();
        Map<String, String> expectedElements = new HashMap<String, String>();
        expectedElements.put("protocol_file_name", "[Not Available]");
        expectedElements.put("experimental_protocol_type", "mirVana (Allprep DNA) RNA");
        expectedElements.put("bcr_sample_barcode", "TCGA-00-1111-01A");
        expectedElements.put("protocol_name", "[Not Available]");
        expectedElements.put("bcr_analyte_barcode", "TCGA-00-1111-01A-11R");
        expectedData.put("TCGA-00-1111-01A_TCGA-00-1111-01A-11R", expectedElements);

        expectedElements = new HashMap<String, String>();
        expectedElements.put("protocol_file_name", "[Not Available]");
        expectedElements.put("experimental_protocol_type", "aDNA Preparation Type");
        expectedElements.put("bcr_sample_barcode", "TCGA-00-1111-01A");
        expectedElements.put("protocol_name", "[Not Available]");
        expectedElements.put("bcr_analyte_barcode", "TCGA-00-1111-01A-11D");
        expectedData.put("TCGA-00-1111-01A_TCGA-00-1111-01A-11D", expectedElements);

        expectedElements = new HashMap<String, String>();
        expectedElements.put("protocol_file_name", "[Not Available]");
        expectedElements.put("experimental_protocol_type", "aDNA Preparation Type");
        expectedElements.put("bcr_sample_barcode", "TCGA-00-1111-10A");
        expectedElements.put("protocol_name", "[Not Available]");
        expectedElements.put("bcr_analyte_barcode", "TCGA-00-1111-10A-02D");
        expectedData.put("TCGA-00-1111-10A_TCGA-00-1111-10A-02D", expectedElements);


        Document document = bcrXMLFileParser.getDocument(sampleXMLFile);
        final Map<String, Map<String, String>> actualData = bcrXMLFileParser.parseDocument(document, getTemplate(), sampleXMLFile);
        assertEquals(3, actualData.size());
        assertTrue(expectedData.keySet().containsAll(actualData.keySet()));
        for (final String key : expectedData.keySet()) {
            assertEquals(5, actualData.get(key).keySet().size());
            assertTrue(expectedData.get(key).keySet().containsAll(actualData.get(key).keySet()));

            for (final String elementName : expectedData.get(key).keySet()) {
                assertEquals(expectedData.get(key).get(elementName), actualData.get(key).get(elementName));
            }
        }

    }

    @Test
    public void getGeneratedKey() {
        String key = bcrXMLFileParser.getGeneratedKey();
        assertTrue("A".equals(key));
        key = bcrXMLFileParser.getGeneratedKey();
        assertTrue("B".equals(key));
        key = bcrXMLFileParser.getGeneratedKey();
        assertTrue("C".equals(key));

    }

    public TcgaBcrDataTemplate getTemplate() throws Exception {

        TcgaBcrDataTemplate result = null;
        InputStream inputStream = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("gov.nih.nci.ncicb.tcga.dcc.common.jaxb.generated.template");
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            //noinspection IOResourceOpenedButNotSafelyClosed
            inputStream = new FileInputStream(templatesDir + templateFile);
            result = (TcgaBcrDataTemplate) unmarshaller.unmarshal(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }
}
