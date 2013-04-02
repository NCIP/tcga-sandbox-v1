import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class tcDataPortalFf extends SeleneseTestCase {
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://156.40.140.4:8080/tcga/");
		selenium.start();
	}

	@Test
	public void testTcDataPortal() throws Exception {
		selenium.open("/tcga/");
		verifyTrue(selenium.isElementPresent("homePageQueryDataButton"));
		verifyTrue(selenium.isElementPresent("homePageDownloadDataButton"));
		verifyTrue(selenium.isElementPresent("announcements"));
		verifyTrue(selenium.isElementPresent("moreTcgaInformation"));
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isVisible("link=Glioblastoma multiforme")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("newsArticle1")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.open("/tcga/tcgaCancerDetails.jsp?diseaseType=GBM&diseaseName=Glioblastoma multiforme");
		verifyTrue(selenium.isElementPresent("announcements"));
		verifyTrue(selenium.isElementPresent("moreTcgaInformation"));
		verifyTrue(selenium.isElementPresent("link=Run Query on Glioblastoma multiforme"));
		verifyTrue(selenium.isTextPresent("Glioblastoma multiforme: Sample Counts and Findings"));
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("link=441")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("detailsPageTableContent")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.open("/tcga/tcgaDownload.jsp");
		verifyTrue(selenium.isTextPresent("In This Section"));
		verifyTrue(selenium.isElementPresent("menuDownloadData"));
		verifyTrue(selenium.isElementPresent("controlledAccessRequirements"));
		verifyTrue(selenium.isElementPresent("userGuidesAndHelp"));
		verifyTrue(selenium.isElementPresent("dataMatrixButton"));
		verifyTrue(selenium.isElementPresent("bulkDownloadButton"));
		verifyTrue(selenium.isElementPresent("link=Open-access HTTP Directory"));
		verifyTrue(selenium.isElementPresent("link=Controlled-Access HTTP Directory"));
		selenium.open("/tcga/tcgaTools.jsp");
		assertTrue(selenium.isTextPresent("Analytical Tools"));
		verifyTrue(selenium.isTextPresent("Data Portal Tools"));
		verifyTrue(selenium.isElementPresent("menuTools"));
		verifyTrue(selenium.isElementPresent("moreTcgaInformation"));
		selenium.open("/tcga/tcgaAnalyticalTools.jsp");
		verifyTrue(selenium.isElementPresent("menuTools"));
		verifyTrue(selenium.isElementPresent("moreTcgaInformation"));
		verifyTrue(selenium.isElementPresent("link=NCI Cancer Molecular Analysis (CMA) Portal"));
		verifyTrue(selenium.isElementPresent("link=NCI Cancer Genome Workbench (CGWB)"));
		verifyTrue(selenium.isElementPresent("link=Broad Institute Integrative Genomics Viewer (IGV)"));
		verifyTrue(selenium.isElementPresent("link=MSKCC Cancer Genomics Analysis"));
		selenium.open("/tcga/tcgaAbout.jsp");
		verifyTrue(selenium.isTextPresent("About TCGA Data"));
		verifyTrue(selenium.isElementPresent("menuAbout"));
		selenium.open("/tcga/tcgaDataType.jsp");
		verifyTrue(selenium.isElementPresent("menuAbout"));
		verifyTrue(selenium.isTextPresent("Data Levels and Data Types"));
		verifyTrue(selenium.isElementPresent("link=Relationship of Data Levels to Data Types"));
		verifyTrue(selenium.isTextPresent("Data Levels"));
		verifyTrue(selenium.isTextPresent("Relationship of Data Levels to Data Types"));
		selenium.open("/tcga/tcgaPlatformDesign.jsp");
		verifyTrue(selenium.isTextPresent("Platform Design"));
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("platform0")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.open("/tcga/tcgaAccessTiers.jsp");
		verifyTrue(selenium.isElementPresent("menuAbout"));
		verifyTrue(selenium.isTextPresent("Access Tiers"));
		selenium.open("/tcga/tcgaAnnouncements.jsp");
		verifyTrue(selenium.isElementPresent("menuAbout"));
		verifyTrue(selenium.isTextPresent("Announcements"));
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("newsArticle1")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		selenium.open("/tcga/tcgaHelp.jsp");
		verifyTrue(selenium.isElementPresent("menuAbout"));
		verifyTrue(selenium.isElementPresent("reportProblem"));
		verifyTrue(selenium.isTextPresent("User Guides and Help"));
		verifyTrue(selenium.isTextPresent("Report a Problem"));
		verifyTrue(selenium.isTextPresent("Contact Data Portal Support"));
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
