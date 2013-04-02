package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.Level2DataQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.service.Level2DataServiceI;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderQueries;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for Level2DataCacheGenerator
 * 
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class Level2DataCacheGeneratorFastTest {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	private static final String SAMPLE_TEMP_DIR = SAMPLE_DIR + "qclive"
			+ File.separator;

	private static final int platformId = 1;
	private static final int centerId = 1;

	private Mockery context = new JUnit4Mockery();
	private Level2DataCacheGenerator level2DataCacheGenerator;
	private Level2DataQueries level2DataQueries;
	private LoaderQueries loaderQueries;
	private Level2DataServiceI level2DataService;
	private MailErrorHelper mockMailHelper = context
			.mock(MailErrorHelper.class);

	@Before
	public void setup() {
		level2DataCacheGenerator = new Level2DataCacheGenerator();
		level2DataQueries = context.mock(Level2DataQueries.class);
		loaderQueries = context.mock(LoaderQueries.class);
		level2DataService = context.mock(Level2DataServiceI.class);

		level2DataCacheGenerator.setLevel2DataQueries(level2DataQueries);
		level2DataCacheGenerator.setLoaderQueries(loaderQueries);
		level2DataCacheGenerator.setLevel2DataService(level2DataService);
		level2DataCacheGenerator.setCacheFileDirectory(SAMPLE_DIR);
		level2DataCacheGenerator.setCacheFileDistroDirectory(SAMPLE_DIR);
		level2DataCacheGenerator.setTmpCacheFileDirectory(SAMPLE_TEMP_DIR);
		level2DataCacheGenerator.setErrorMailSender(mockMailHelper);
	}

	@Test
	public void testGenerateDataFile() throws Exception {
		final Level2DataFilterBean level2DataFilterBean = getLevel2DataFilterBean();
		final List<String> sourceFileTypeList = Arrays.asList(new String[] {
				"detection-p-value", "signal_intensity" });
		final String cacheFileName1 = "GBM_level2_jhu-usc.edu_HumanMethylation27_detection-p-value.txt";
		final String cacheFileName2 = "GBM_level2_jhu-usc.edu_HumanMethylation27_signal_intensity.txt";
		final String compressedCacheFileName1 = "GBM_level2_jhu-usc.edu_HumanMethylation27_detection-p-value.tar.gz";
		final String compressedCacheFileName2 = "GBM_level2_jhu-usc.edu_HumanMethylation27_signal_intensity.tar.gz";

		final File cacheTempFile1 = createDataFile(SAMPLE_TEMP_DIR
				+ cacheFileName1);
		final File cacheTempFile2 = createDataFile(SAMPLE_TEMP_DIR
				+ cacheFileName2);

		context.checking(new Expectations() {
			{
				one(loaderQueries).lookupPlatformId(
						level2DataFilterBean.getPlatformName());
				will(returnValue(1));
				one(loaderQueries).lookupCenterId(
						level2DataFilterBean.getCenterDomainName(), 1);
				will(returnValue(1));
				one(level2DataQueries).getExperimentSourceFileTypes(
						level2DataFilterBean.getExperimentIdList());
				will(returnValue(sourceFileTypeList));
				one(level2DataService).generateDataFile(1, 1,
						sourceFileTypeList.get(0), cacheTempFile1.getPath());
				will(returnValue(cacheTempFile1));
				one(level2DataService).generateDataFile(1, 1,
						sourceFileTypeList.get(1), cacheTempFile2.getPath());
				will(returnValue(cacheTempFile2));
				one(level2DataQueries).updateDataSetUseInDAMStatus(
						level2DataFilterBean.getExperimentIdList());
			}
		});
		level2DataCacheGenerator.generateCacheFiles(level2DataFilterBean);

		final File cacheFile1 = new File(SAMPLE_DIR + cacheFileName1);
		final File cacheFile2 = new File(SAMPLE_DIR + cacheFileName2);
		final File compressedCacheFile1 = new File(SAMPLE_DIR
				+ compressedCacheFileName1);
		final File compressedCacheFile2 = new File(SAMPLE_DIR
				+ compressedCacheFileName2);
		assertTrue(cacheFile1.exists());
		assertTrue(cacheFile2.exists());
		assertTrue(compressedCacheFile1.exists());
		assertTrue(compressedCacheFile2.exists());

		FileUtil.deleteDir(cacheFile1);
		FileUtil.deleteDir(cacheFile2);
		FileUtil.deleteDir(compressedCacheFile1);
		FileUtil.deleteDir(compressedCacheFile2);

	}

	private Level2DataFilterBean getLevel2DataFilterBean() {
		final Set experimentIds = new HashSet(Arrays.asList(new Long[] {
				(long) 1, (long) 2 }));
		final Level2DataFilterBean level2DataFilterBean = new Level2DataFilterBean();
		level2DataFilterBean.setDiseaseAbbreviation("GBM");
		level2DataFilterBean.setCenterDomainName("jhu-usc.edu");
		level2DataFilterBean.setPlatformName("HumanMethylation27");
		level2DataFilterBean.setExperimentIdList(experimentIds);
		return level2DataFilterBean;
	}

	private File createDataFile(final String fileName) throws IOException {
		final String data = "Hybridization REF\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0014-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05\tTCGA-02-0060-01A-01D-0186-05"
				+ "CompositeElement REF\tCall\tBeta_Value\tSignal\tNA\t0.2935\tCall\tBeta_Value\tSignal\tNA\t0.2935"
				+ "CN_052529\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678\t0.678"
				+ "CN_052529\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999\t0.999";
		final File dataFile = new File(fileName);
		FileUtil.writeContentToFile(data, dataFile);

		return dataFile;
	}

}
