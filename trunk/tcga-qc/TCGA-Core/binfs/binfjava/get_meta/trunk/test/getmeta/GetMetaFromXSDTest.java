package getmeta;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

public class GetMetaFromXSDTest {
	private GetMetaFromXSD metaGetter = new GetMetaFromXSD();
	

	@Test
	public void testGotParserFromFile() {
		try {
			metaGetter.parseFile("test/samples/test-mockup.xsd");
			assertNotNull(metaGetter.parser);
		} catch (Exception e) {
			fail("metaGetter.parseFile() failed with exception message: "+e.getMessage());
		}
	}
	
	@Test
	public void testGotParserFromURI() {
		try {
			metaGetter.parseURI("http://tcga-data.nci.nih.gov/docs/xsd/BCR/tcga.nci/bcr/xml/utility/2.4/TCGA_BCR.Utility.xsd");
			assertNotNull(metaGetter.parser);
		} catch (Exception e) {
			fail("metaGetter.parseURI() failed with exception message: "+e.getMessage());	
		}
	}
	@Test
	public void testEstablishXMLReader() throws Exception {
		metaGetter.parseFile("test/samples/test-mockup.xsd");
		assertNotNull(metaGetter.xmlReader);
	}
	
	@Test 
	public void testSampleContainsMetadata() throws Exception {
		metaGetter.parseFile("test/samples/test-mockup.xsd");
		assertTrue(metaGetter.hasMetadata());
	}
	
	@Test
	public void testSampleContainsSingleMetadataElement() throws Exception {
		metaGetter.parseFile("test/samples/test-mockup.xsd");
		assertEquals(1, metaGetter.numberOfMetadataElements());
	}
	
	@Test
	public void testCheckTheMetadataElementNames() throws Exception {
		metaGetter.parseFile("test/samples/test-mockup.xsd");
		ArrayList<String> check = new ArrayList<String>();
		check.add("test_true");
		assertTrue(metaGetter.getMetadataTags().equals(check));
	}

}

