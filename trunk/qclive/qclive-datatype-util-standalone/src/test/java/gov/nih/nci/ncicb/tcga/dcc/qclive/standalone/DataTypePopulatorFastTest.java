package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import org.apache.commons.lang.ArrayUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: class documentation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DataTypePopulatorFastTest {

    private static final String APP_CONTEXT_FILE_NAME = "applicationContextTest.xml";
    private static final ApplicationContext appCtx = new ClassPathXmlApplicationContext(APP_CONTEXT_FILE_NAME);
    private Mockery context = new JUnit4Mockery();
    private ArchiveQueries mockArchiveQueries = context.mock(ArchiveQueries.class);
    private FileInfoQueries mockDiseaseFileInfoQueries= context.mock(FileInfoQueries.class,"disease");
    private FileInfoQueries mockCommonFileInfoQueries= context.mock(FileInfoQueries.class,"common");
    private DataTypeQueries mockDataTypeQueries = context.mock(DataTypeQueries.class);
    private DataTypePopulator dataTypePopulator;

    private static final String SAMPLE_DIR =
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String MAGE_TAB_ARCHIVE_LOCATION =
        SAMPLE_DIR+File.separator+"datatype"+File.separator+"unc.edu_READ.IlluminaGA_RNASeq.mage-tab.1.0.0";

    @Before
    public void setUp() {
        dataTypePopulator = ((DataTypePopulator)appCtx.getBean("dataTypePopulator"));
        dataTypePopulator.setArchiveQueries(mockArchiveQueries);
        dataTypePopulator.setCommonFileInfoQueries(mockCommonFileInfoQueries);
        dataTypePopulator.setDiseaseFileInfoQueries(mockDiseaseFileInfoQueries);
        dataTypePopulator.setDataTypeQueries(mockDataTypeQueries);
    }


    @Test
    public void populateDataTypes(){
        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getAllDataTypesId();
            will(returnValue(getAllDataTypes()));
            one(mockArchiveQueries).getMagetabArchives();
            will(returnValue(getMagetabArchives()));
            one(mockCommonFileInfoQueries).updateFileDatTypes(with(checkDataType()));
            one(mockDiseaseFileInfoQueries).updateFileDatTypes(with(checkDataType()));
        }});
        dataTypePopulator.populateDataTypes();

    }

    private Map<String,Long> getAllDataTypes(){
        final Map<String,Long> dataTypesIdByName = new HashMap<String, Long>();
        dataTypesIdByName.put("Expression-Gene",2l);
        dataTypesIdByName.put("Expression-Exon",3l);
        return dataTypesIdByName;
    }

    private List<Archive> getMagetabArchives(){
        final List<Archive> magetabArchives = new ArrayList<Archive>();
        final Archive archive = new Archive(MAGE_TAB_ARCHIVE_LOCATION+".tar.gz");
        archive.setRealName("unc.edu_READ.IlluminaHiSeq_RNASeq.Level_3.1.0.0");
        archive.setDeployLocation(MAGE_TAB_ARCHIVE_LOCATION);
        archive.setTheTumor(new Tumor());
        archive.getTheTumor().setTumorDescription("READ");

        magetabArchives.add(archive);
        return magetabArchives;
    }


    private Matcher<List<FileInfo>> checkDataType() {
        return new TypeSafeMatcher<List<FileInfo>>() {

            @Override
            public boolean matchesSafely(List<FileInfo> fileInfoList) {
                boolean returnValue = false;
                if(fileInfoList.size() != 2){
                    return false;
                }
                final Object[][] expectedValues = new Object[][]{
                    new Object[]{"UNCID_52843.TCGA-AG-3587-01A-01R-0000-07.100914_UNC8-RDR3001640_00029_FC_62HPTAAXX.6.trimmed.annotated.gene.quantification.txt",
                    "unc.edu_READ.IlluminaGA_RNASeq.Level_3.1.0.0",
                    2},
                    new Object[]{"UNCID_53197.TCGA-AG-3587-01A-01R-0000-07.100914_UNC8-RDR3001640_00029_FC_62HPTAAXX.6.trimmed.annotated.exon.quantification.txt",
                    "unc.edu_READ.IlluminaGA_RNASeq.Level_3.1.0.0",
                    3}
                };
                int row = 0;
                for(final FileInfo fileInfo: fileInfoList){
                    final Object[] actualValues = new Object[3];
                    int i=0;
                    actualValues[i++] = fileInfo.getFileName();
                    actualValues[i++] = fileInfo.getArchiveName();
                    actualValues[i++] = fileInfo.getDataTypeId();
                    if (!ArrayUtils.isEquals(expectedValues[row++], actualValues)){
                        return false;
                    }
                }
                return true;

            }

            public void describeTo(final Description description) {
                description.appendText("Valid match");
            }
        };
    }

}
