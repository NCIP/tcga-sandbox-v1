package gov.nih.nci.ncicb.tcga.dcc.datareports.web.selenium;

import com.thoughtworks.selenium.SeleneseTestCase;

public class SampleSummarySeleniumFastTest extends SeleneseTestCase {
	public void setUp() throws Exception {
		//setUp("http://localhost:8080/datareports/sampleSummary.htm", "*chrome");
	}

	public void testSampleSummarySeleniumTest() throws Exception {
//		selenium.open("/datareports/sampleSummary.htm");
//		verifyTrue(selenium.isTextPresent("DCC Data Summary"));
//		assertTrue(selenium.isElementPresent("top_header"));
//		assertEquals("The Cancer Genome Atlas Data Portal", selenium.getTitle());
//		assertTrue(selenium.isElementPresent("//table[@id='summarySamples']/thead/tr[1]/td"));
//		verifyTrue(selenium.isTextPresent("Cancer Type"));
//		verifyTrue(selenium.isTextPresent("Center"));
//		verifyTrue(selenium.isTextPresent("Analyte"));
//		verifyTrue(selenium.isTextPresent("Platform"));
//		verifyTrue(selenium.isTextPresent("Sample IDs BCR Reported Sending to Center"));
//		verifyTrue(selenium.isTextPresent("Sample IDs DCC Received from Center"));
//		verifyTrue(selenium.isTextPresent("Unaccounted for BCR Sample IDs that Center Reported"));
//		verifyTrue(selenium.isTextPresent("Unaccounted for Center Sample IDs that BCR Reported"));
//		verifyTrue(selenium.isTextPresent("Sample IDs with Level 1 Data"));
//		verifyTrue(selenium.isTextPresent("Sample IDs with Level 2 Data"));
//		verifyTrue(selenium.isTextPresent("Sample IDs with Level 3 Data"));
//		verifyTrue(selenium.isTextPresent("Level 4 Submitted (Y/N)"));
//		verifyTrue(selenium.isTextPresent("exact:Disclaimer: The above table(s) reflect as accurately as possible the sample IDs and ID annotations submitted to the DCC to date. At the present time, the intended platform for a given aliquot can only be inferred from the disease and the identity of the GSC/CGCC that is encoded in the aliquot ID. If a given GSC/CGCC is using only one platform for a given disease, then this inference will be accurate. If the GSC/CGCC is using more than one platform for a disease, the DCC cannot accurately report ID counts per platform per disease. Of course, once a GSC/CGCC submits molecular data for an aliquot to the DCC, then the platform is known. In Phase 2 of the TCGA project there will be a standard operating procedure for a GSC/CGCC to report the intended platform for a given aliquot before submitting the molecular data."));
//		verifyTrue(selenium.isTextPresent("exact:A value of \"Undetermined\" for Platform indicates that the DCC has not received data for the indicated sample-analytes\n * = Although not in the latest current archive, level 4 data has been submitted"));
//		verifyTrue(selenium.isTextPresent("exact:* = Although not in the latest current archive, level 4 data has been submitted"));
//		assertTrue(selenium.isElementPresent("//img[@alt='National Human Genome Research Institute']"));
//		assertTrue(selenium.isElementPresent("//img[@alt='National Cancer Institute']"));
//		assertTrue(selenium.isElementPresent("//img[@alt='National Institutes of Health']"));
//		assertTrue(selenium.isElementPresent("//img[@alt='Department of Health and Human Services']"));
//		assertTrue(selenium.isElementPresent("//img[@alt='FirstGov.gov']"));
//		verifyTrue(selenium.isElementPresent("//img[@alt='csv']"));
//		verifyTrue(selenium.isElementPresent("//img[@alt='jexcel']"));
//		selenium.click("link=509");
//		selenium.waitForPageToLoad("30000");
//		verifyTrue(selenium.isTextPresent("DCC Data Detailed View"));
//		verifyTrue(selenium.isElementPresent("link=Back to Sample Summary Page"));
//		assertEquals("Back to Sample Summary Page", selenium.getText("link=Back to Sample Summary Page"));
//		verifyTrue(selenium.isTextPresent("Sample Detailed"));
//		verifyTrue(selenium.isTextPresent("Results 1 - 50 of 509."));
//		assertEquals("Results 1 - 50 of 509.", selenium.getText("//table[@id='detailedSamples']/tbody[2]/tr/td"));
//		verifyTrue(selenium.isElementPresent("//img[@alt='csv']"));
//		verifyTrue(selenium.isElementPresent("//img[@alt='jexcel']"));
//		verifyTrue(selenium.isElementPresent("//div[@id='results_table']/fieldset/legend"));
//		selenium.click("link=Back to Sample Summary Page");
//		selenium.waitForPageToLoad("30000");
//		verifyTrue(selenium.isTextPresent("Results 1 - 47 of 47."));
//		selenium.click("link=327");
//		selenium.waitForPageToLoad("30000");
//		assertTrue(selenium.isElementPresent("link=Back to Sample Summary Page"));
//		assertTrue(selenium.isElementPresent("//table[@id='detailedSamples']/caption"));
//		verifyTrue(selenium.isTextPresent("Tumor Abbreviation: GBM"));
//		verifyTrue(selenium.isTextPresent("Center Name: broad.mit.edu"));
//		verifyTrue(selenium.isTextPresent("Center Type: CGCC"));
//		verifyTrue(selenium.isTextPresent("Portion Analyte: R"));
//		verifyTrue(selenium.isTextPresent("exact:Platform: HT_HG-U133A"));
//		selenium.click("link=Back to Sample Summary Page");
//		selenium.click("link=338");
//		selenium.click("link=Back to Sample Summary Page");
//		selenium.click("link=64");
//		assertEquals("Results 1 - 50 of 64.", selenium.getText("//table[@id='detailedSamples']/tbody[2]/tr/td"));
//		selenium.click("link=Back to Sample Summary Page");
//		selenium.click("//tr[@id='summarySamples_row23']/td[9]/a");
//		assertEquals("Results 1 - 50 of 487.", selenium.getText("//table[@id='detailedSamples']/tbody[2]/tr/td"));
//		assertTrue(selenium.isElementPresent("//div[@id='results_table']/p"));
//		assertEquals("Disclaimer: The above table(s) reflect as accurately as possible the sample IDs and ID annotations submitted to the DCC to date. At the present time, the intended platform for a given aliquot can only be inferred from the disease and the identity of the GSC/CGCC that is encoded in the aliquot ID. If a given GSC/CGCC is using only one platform for a given disease, then this inference will be accurate. If the GSC/CGCC is using more than one platform for a disease, the DCC cannot accurately report ID counts per platform per disease. Of course, once a GSC/CGCC submits molecular data for an aliquot to the DCC, then the platform is known. In Phase 2 of the TCGA project there will be a standard operating procedure for a GSC/CGCC to report the intended platform for a given aliquot before submitting the molecular data.", selenium.getText("//div[@id='results_table']/p"));
//		selenium.click("link=Back to Sample Summary Page");
//		selenium.click("link=171");
//		assertEquals("Results 1 - 50 of 171.", selenium.getText("//table[@id='detailedSamples']/tbody[2]/tr/td"));
//		selenium.click("link=Back to Sample Summary Page");
//		verifyTrue(selenium.isTextPresent("Sample Summary"));
//		verifyTrue(selenium.isTextPresent("DCC Data Summary"));
//		verifyTrue(selenium.isTextPresent("Results 1 - 47 of 47."));
	}
}
