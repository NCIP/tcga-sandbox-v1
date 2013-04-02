package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataMatrixFileBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Map;

/**
 * Implementation for Level2Service queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level2DataServiceImpl implements Level2DataService{
  private FileInfoQueries diseaseFileInfoQueries;
    private FileInfoQueries commonFileInfoQueries;
    private FileArchiveQueries diseaseFileArchiveQueries;
    private FileArchiveQueries commonFileArchiveQueries;
    private BCRIDQueries diseaseBCRIDQueries;
    private BCRIDQueries commonBCRIDQueries;
    private DataMatrixQueries dataMatrixQueries;
    private DriverManagerDataSource dccCommonDataSource;
    private DriverManagerDataSource diseaseDataSource;

    public Map<String,List<DataMatrixFileBean>> getMultipleAliquotDataMatrixFiles(){
        return dataMatrixQueries.getMultipleAliquotDataMatrixFiles();
    }

    public String getSdrfFilePathForExperiment(final String centerName,
                                               final String platformName,
                                               final String diseaseAbbreviation){
        return diseaseFileInfoQueries.getSdrfFilePathForExperiment(centerName,
                platformName,
                diseaseAbbreviation);
    }

    public void addFiles(final List<FileInfo> filesInfo){
        commonFileInfoQueries.addFiles(filesInfo);
        diseaseFileInfoQueries.addFiles(filesInfo);
    }

    public void deleteFiles(final List<Long> fileIds){
        commonFileInfoQueries.deleteFiles(fileIds);
        diseaseFileInfoQueries.deleteFiles(fileIds);
    }

    public void addFileToArchiveAssociations(final List<FileToArchive> fileToArchives){
        commonFileArchiveQueries.addFileToArchiveAssociations(fileToArchives);
        diseaseFileArchiveQueries.addFileToArchiveAssociations(fileToArchives);
    }

    public void deleteFileToArchiveAssociations(final List<Long> fileIds, final Long archiveId){
        commonFileArchiveQueries.deleteFileToArchiveAssociations(fileIds,archiveId);
        diseaseFileArchiveQueries.deleteFileToArchiveAssociations(fileIds,archiveId);
    }

    public void updateBiospecimenToFileAssociations(final List<BiospecimenToFile> biospecimenToFiles){
        commonBCRIDQueries.updateBiospecimenToFileAssociations(biospecimenToFiles);
        diseaseBCRIDQueries.updateBiospecimenToFileAssociations(biospecimenToFiles);

    }

    public Map<String,Integer> getBiospecimenIdsForBarcodes(final List<String> barcodes){
        return diseaseBCRIDQueries.getBiospecimenIdsForBarcodes(barcodes);
    }

    @Autowired
    public void setDiseaseFileInfoQueries(FileInfoQueries diseaseFileInfoQueries) {
        this.diseaseFileInfoQueries = diseaseFileInfoQueries;
    }

    @Autowired
    public void setCommonFileInfoQueries(FileInfoQueries commonFileInfoQueries) {
        this.commonFileInfoQueries = commonFileInfoQueries;
    }

    @Autowired
    public void setDiseaseFileArchiveQueries(FileArchiveQueries diseaseFileArchiveQueries) {
        this.diseaseFileArchiveQueries = diseaseFileArchiveQueries;
    }

    @Autowired
    public void setCommonFileArchiveQueries(FileArchiveQueries commonFileArchiveQueries) {
        this.commonFileArchiveQueries = commonFileArchiveQueries;
    }

    @Autowired
    public void setDiseaseBCRIDQueries(BCRIDQueries diseaseBCRIDQueries) {
        this.diseaseBCRIDQueries = diseaseBCRIDQueries;
    }

    @Autowired
    public void setCommonBCRIDQueries(BCRIDQueries commonBCRIDQueries) {
        this.commonBCRIDQueries = commonBCRIDQueries;
    }

    @Autowired
    public void setDataMatrixQueries(DataMatrixQueries dataMatrixQueries) {
        this.dataMatrixQueries = dataMatrixQueries;
    }

    @Autowired
    public void setDccCommonDataSource(DriverManagerDataSource dccCommonDataSource) {
        this.dccCommonDataSource = dccCommonDataSource;
    }

    @Autowired
    public void setDiseaseDataSource(DriverManagerDataSource diseaseDataSource) {
        this.diseaseDataSource = diseaseDataSource;
    }

    public void validateConnections(){
        validateConnectionParameters(dccCommonDataSource,"dcccommon");
        validateConnectionParameters(diseaseDataSource,"disease");
        if(getDatabaseEnvironment().isEmpty()){
            throw new RuntimeException("Invalid environment. You can run this tool only on dev,stage,qa or production.");
        }
        if(getDisease().isEmpty()){
            throw new RuntimeException("Invalid disease. Please check username for disease database");
        }

    }

    private void validateConnectionParameters(final DriverManagerDataSource driverManagerDataSource,
                                              final String database){

        final String url = driverManagerDataSource.getUrl();
        if(url.isEmpty()){
            throw new IllegalArgumentException("Connection URL for "+database+" database is empty. Please specify connection URL in tcgaDBConnect.properties ");
        }
        final String username = driverManagerDataSource.getUsername();
        if(username.isEmpty()){
            throw new IllegalArgumentException("Connection username for "+database+" database is empty. Please specify connection username in tcgaDBConnect.properties ");
        }
        final String password = driverManagerDataSource.getPassword();
        if(password.isEmpty()){
            throw new IllegalArgumentException("Connection password for "+database+" database is empty. Please specify connection password in tcgaDBConnect.properties ");
        }

    }

    public String getDatabaseEnvironment(){

        if(diseaseDataSource.getUrl() != null){
            final String database = diseaseDataSource.getUrl().substring(diseaseDataSource.getUrl().lastIndexOf(":")+1);
            if(database.toUpperCase().equals("TCGAPRD2")){
                return "Production";
            }
            if(database.toUpperCase().equals("TCGASTG")){
                return "Stage";
            }
            if(database.toUpperCase().equals("TCGAQA")){
                return "QA";
            }
            if(database.toUpperCase().equals("TCGADEV")){
                return "Dev";
            }

        }
        return "";
    }

    public String getDisease(){
        final String userName = diseaseDataSource.getUsername();
        if(userName.contains("tcga")){
            return userName.substring(4);
        }
        if(userName.contains("maint")){
            return userName.substring(0,userName.length()-4);
        }

        return "";
    }
}
