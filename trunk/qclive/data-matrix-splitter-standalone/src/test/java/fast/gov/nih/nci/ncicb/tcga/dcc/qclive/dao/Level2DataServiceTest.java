package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Level2DAO implementation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class Level2DataServiceTest {
    private Mockery context = new JUnit4Mockery();
    private FileInfoQueries mockDiseaseFileInfoQueries = context.mock(FileInfoQueries.class,"disease_fileInfo");
    private FileInfoQueries mockCommonFileInfoQueries =  context.mock(FileInfoQueries.class,"common_fileInfo");;
    private FileArchiveQueries mockDiseaseFileArchiveQueries =  context.mock(FileArchiveQueries.class,"disease_filearchive");;;
    private FileArchiveQueries mockCommonFileArchiveQueries =  context.mock(FileArchiveQueries.class,"common_filearchive");;;
    private BCRIDQueries mockDiseaseBCRIDQueries =  context.mock(BCRIDQueries.class,"disease_bcr");;;
    private BCRIDQueries mockCommonBCRIDQueries =  context.mock(BCRIDQueries.class,"common_bcr");;;
    private DataMatrixQueries mockDataMatrixQueries =  context.mock(DataMatrixQueries.class,"datamatrix");;;
    private Level2DataServiceImpl level2DataService;

    @Before
    public void setup() {
        level2DataService = new Level2DataServiceImpl();
        level2DataService.setCommonBCRIDQueries(mockCommonBCRIDQueries);
        level2DataService.setCommonFileArchiveQueries(mockCommonFileArchiveQueries);
        level2DataService.setCommonFileInfoQueries(mockCommonFileInfoQueries);
        level2DataService.setDataMatrixQueries(mockDataMatrixQueries);
        level2DataService.setDiseaseBCRIDQueries(mockDiseaseBCRIDQueries);
        level2DataService.setDiseaseFileArchiveQueries(mockDiseaseFileArchiveQueries);
        level2DataService.setDiseaseFileInfoQueries(mockDiseaseFileInfoQueries);
    }


    @Test
    public void getMultipleAliquotDataMatrixFiles() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockDataMatrixQueries).getMultipleAliquotDataMatrixFiles();
        }});
        level2DataService.getMultipleAliquotDataMatrixFiles();
    }

    @Test
    public void addFiles(){
        final List<FileInfo> files = new ArrayList<FileInfo>();
        context.checking(new Expectations() {{
            one(mockCommonFileInfoQueries).addFiles(files);
            one(mockDiseaseFileInfoQueries).addFiles(files);
        }});
        level2DataService.addFiles(files);

    }

    @Test
    public void deleteFiles(){
        final List<Long> fileIds = new ArrayList<Long>();
        context.checking(new Expectations() {{
            one(mockCommonFileInfoQueries).deleteFiles(fileIds);
            one(mockDiseaseFileInfoQueries).deleteFiles(fileIds);
        }});
        level2DataService.deleteFiles(fileIds);

    }

    @Test
    public void addFileToArchiveAssociations(){
        final List<FileToArchive> fileToArchives = new ArrayList<FileToArchive>();
        context.checking(new Expectations() {{
            one(mockCommonFileArchiveQueries).addFileToArchiveAssociations(fileToArchives);
            one(mockDiseaseFileArchiveQueries).addFileToArchiveAssociations(fileToArchives);
        }});
        level2DataService.addFileToArchiveAssociations(fileToArchives);

    }

    @Test
    public void deleteFileToArchiveAssociations(){
        final List<Long> fileIds = new ArrayList<Long>();
        final Long archiveId = 10l;

        context.checking(new Expectations() {{
            one(mockCommonFileArchiveQueries).deleteFileToArchiveAssociations(fileIds,archiveId);
            one(mockDiseaseFileArchiveQueries).deleteFileToArchiveAssociations(fileIds,archiveId);
        }});
        level2DataService.deleteFileToArchiveAssociations(fileIds,archiveId);
    }

    @Test
    public void updateBiospecimenToFileAssociations(){
        final List<BiospecimenToFile> fileInfoByBarcodes = new ArrayList<BiospecimenToFile>();
        context.checking(new Expectations() {{
            one(mockCommonBCRIDQueries).updateBiospecimenToFileAssociations(fileInfoByBarcodes);
            one(mockDiseaseBCRIDQueries).updateBiospecimenToFileAssociations(fileInfoByBarcodes);
        }});
        level2DataService.updateBiospecimenToFileAssociations(fileInfoByBarcodes);

    }


}
