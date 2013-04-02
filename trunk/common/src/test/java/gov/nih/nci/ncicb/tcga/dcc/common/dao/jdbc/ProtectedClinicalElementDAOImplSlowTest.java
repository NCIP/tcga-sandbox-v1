package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ProtectedClinicalElement;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ProtectedClinicalElementDAO;

import java.util.Arrays;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class that tests the functionality of the {@link ProtectedClinicalElementDAOImpl}.
 * 
 * @author nichollsmc
 */
@Ignore("The code being tested by this unit test has been reverted for further requirements clarification. " +
		"See tickets APPS-5818 and APPS-6055 for more info.")
@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations = {"classpath:samples/applicationContext-dbunit.xml"})
public class ProtectedClinicalElementDAOImplSlowTest {
	
	@Autowired
	private ProtectedClinicalElementDAO protectedClinicalElementDAO;
	
	private static final List<String> protectedClinicalElementNames = Arrays.asList(
			"new_neoplasm_occurrence_anatomic_site_text",
			"other_metastatic_involvement_anatomic_site",
			"extrahepatic_recurrent_disease_location_text",
			"other_method_of_initial_pathological_diagnosis",
			"history_of_neoadjuvant_treatment");
	
	private Cache cache;
	
	@Before
	public void before() {
		if(cache == null) {
			CacheManager protectedElementCacheManager = CacheManager.getInstance();
			cache = protectedElementCacheManager.getCache(ProtectedClinicalElement.class.getName());
			cache.setStatisticsAccuracy(Statistics.STATISTICS_ACCURACY_BEST_EFFORT);
			cache.setStatisticsEnabled(true);
		}
		populateElements();
		cache.removeAll();
		cache.clearStatistics();
	}
	
	@After
	public void after() {
		removeAllElements();
	}
	
	@Test
	public void testAddNewElement() {
		final ProtectedClinicalElement protectedClinicalElement = new ProtectedClinicalElement();
		protectedClinicalElement.setElementName("new_protected_clinical_name");
		final Integer result = protectedClinicalElementDAO.addElement(protectedClinicalElement);
		assertNotNull(result);
	}
	
	@Test
	public void testAddExistingElement() {
		final ProtectedClinicalElement protectedClinicalElement = new ProtectedClinicalElement();
		protectedClinicalElement.setElementName("new_neoplasm_occurrence_anatomic_site_text");
		final Integer result = protectedClinicalElementDAO.addElement(protectedClinicalElement);
		assertNull(result);
	}
	
	@Test
	public void testGetExistingElementByName() {
		final ProtectedClinicalElement protectedClinicalElement = protectedClinicalElementDAO.getElementByName("extrahepatic_recurrent_disease_location_text");
		assertNotNull(protectedClinicalElement);
		assertEquals("extrahepatic_recurrent_disease_location_text", protectedClinicalElement.getElementName());
	}
	
	@Test
	public void testIsProtected() {
		boolean isProtected = protectedClinicalElementDAO.isProtected("new_protected_clinical_name");
		assertFalse(isProtected);
		isProtected = protectedClinicalElementDAO.isProtected("history_of_neoadjuvant_treatment");
		assertTrue(isProtected);
	}
	
	@Test
	public void testGetNonExistingElementByName() {
		final ProtectedClinicalElement protectedClinicalElement = protectedClinicalElementDAO.getElementByName("new_protected_clinical_name");
		assertNull(protectedClinicalElement);
	}
	
	@Test
	public void testGetElements() {
		final List<ProtectedClinicalElement> protectedClinicalElements = protectedClinicalElementDAO.getElements();
		assertEquals(protectedClinicalElementNames.size(), protectedClinicalElements.size());
		String elementName;
		for(final ProtectedClinicalElement protectedClinicalElement : protectedClinicalElements) {
			elementName = protectedClinicalElement.getElementName();
			if(!protectedClinicalElementNames.contains(elementName)) {
				fail("The element name '" + elementName + "' returned from the database does not match any of the expected values");
			}
		}
	}
	
	@Test
	public void testRemoveElementByName() {
		final String elementName = "history_of_neoadjuvant_treatment";
		ProtectedClinicalElement protectedClinicalElement = protectedClinicalElementDAO.getElementByName(elementName);
		assertNotNull(protectedClinicalElement);
		assertEquals(elementName, protectedClinicalElement.getElementName());
		protectedClinicalElementDAO.removeElementByName(elementName);
		protectedClinicalElement = protectedClinicalElementDAO.getElementByName(elementName);
		assertNull(protectedClinicalElement);
	}
	
	@Test
	public void testRetrieveCachedElement() {
	    protectedClinicalElementDAO.getElementByName("extrahepatic_recurrent_disease_location_text");
	    assertEquals(1, cache.getStatistics().getObjectCount());
	    assertEquals(1, cache.getStatistics().getCacheMisses());
	    protectedClinicalElementDAO.getElementByName("extrahepatic_recurrent_disease_location_text");
	    assertEquals(1, cache.getStatistics().getObjectCount());
	    assertEquals(1, cache.getStatistics().getCacheMisses());
	}
	
	@Test
	public void testReteiveCachedElements() {
	    protectedClinicalElementDAO.getElements();
	    assertEquals(5, cache.getStatistics().getObjectCount());
	    assertEquals(5, cache.getStatistics().getCacheMisses());
	    protectedClinicalElementDAO.getElementByName("extrahepatic_recurrent_disease_location_text"); // 6th cache query
	    assertEquals(5, cache.getStatistics().getCacheMisses());
	}
	
	private void populateElements() {
		ProtectedClinicalElement protectedClinicalElement = null;
		for(final String protectedClinicalElementName : protectedClinicalElementNames) {
			protectedClinicalElement = new ProtectedClinicalElement();
			protectedClinicalElement.setElementName(protectedClinicalElementName);
			protectedClinicalElementDAO.addElement(protectedClinicalElement);
		}
	}
	
	private void removeAllElements() {
		final List<ProtectedClinicalElement> protectedClinicalElements = protectedClinicalElementDAO.getElements();
		if(protectedClinicalElements != null && !protectedClinicalElements.isEmpty()) {
			for(final ProtectedClinicalElement protectedClinicalElement : protectedClinicalElements) {
				protectedClinicalElementDAO.removeElementByName(protectedClinicalElement.getElementName());
			}
		}
	}
}