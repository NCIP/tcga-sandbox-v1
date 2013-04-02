package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractSdrfHandler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main class to populate file specific data type into file_info table
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataTypePopulator {

    private static final Logger logger = Logger.getLogger(DataTypePopulator.class);

    private static final String APP_CONTEXT_FILE_NAME = "applicationContext.xml";


    private ArchiveQueries archiveQueries;
    private DataTypeQueries dataTypeQueries;
    private FileInfoQueries commonFileInfoQueries;
    private FileInfoQueries diseaseFileInfoQueries;

    /**
     * Main method to populate file specific data type id into file info table
     * @param args
     */
    public static void main(String[] args) {

        // Initialize the Spring context
        final ApplicationContext appCtx = new ClassPathXmlApplicationContext(APP_CONTEXT_FILE_NAME);

        final DataTypePopulator dataTypePopulator = (DataTypePopulator) appCtx.getBean("dataTypePopulator");
        dataTypePopulator.populateDataTypes();

    }

    /**
     * Parses each sdrf file to get specific data type id and updates file_info table
     * with specific data type id
     */
    public void populateDataTypes() {
        // get all the available mage-tab archives
        final List<Archive> mageTabArchives = archiveQueries.getMagetabArchives();
        //get all the datatypes name and Id
        final Map<String,Long> dataTypesIdByName = dataTypeQueries.getAllDataTypesId();
        final Set<String> errors = new HashSet<String>();
        final Set<String> warnings = new HashSet<String>();

        // parse each sdrf file and update the file info table with specific data type id
        for(final Archive mageTabArchive: mageTabArchives){
            logger.info("Processing "+mageTabArchive.getDeployLocation()+" sdrf file....");
            try{
                // read the sdrf file
                TabDelimitedContentNavigator sdrfNavigator = getSdrfFile(mageTabArchive.getExplodedArchiveDirectoryLocation());
                // for each filename column get the corresponding archive name, data type and update the
                // file_info table with the corresponding data type id
                for (int columnIndex = 0; columnIndex < sdrfNavigator.getHeaders().size(); columnIndex++) {
                    final String columnName = sdrfNavigator.getHeaders().get(columnIndex);

                    if (columnName.endsWith(AbstractSdrfHandler.FILE_COLUMN_SUFFIX)) {
                        // get the comment column for archive name, data type
                        final Map<String, Integer> commentColumns = getFileCommentColumns(sdrfNavigator, columnIndex);
                        boolean proceed = true;
                        if(commentColumns.get(AbstractSdrfHandler.COMMENT_ARCHIVE_NAME) == null){
                            errors.add(mageTabArchive.getExplodedArchiveDirectoryLocation()+ "- \t"+ AbstractSdrfHandler.COMMENT_ARCHIVE_NAME+ " column doesn't exist for file column "+ columnName);                            proceed = false;
                            proceed = false;
                        }

                        if(commentColumns.get(AbstractSdrfHandler.COMMENT_DATA_TYPE) == null){
                            errors.add(mageTabArchive.getExplodedArchiveDirectoryLocation()+"  - \t"+ AbstractSdrfHandler.COMMENT_DATA_TYPE+ " column doesn't exist for file column "+ columnName);
                            proceed = false;
                        }
                        if(proceed){
                            final List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
                            // get the filenames, archivenames, datatypes
                            for (int rowIndex = 1; rowIndex < sdrfNavigator.getNumRows(); rowIndex++) {
                                final String filename = sdrfNavigator.getValueByCoordinates(columnIndex, rowIndex);
                                final String archivename = sdrfNavigator.getValueByCoordinates(commentColumns.get(AbstractSdrfHandler.COMMENT_ARCHIVE_NAME), rowIndex);
                                final String dataTypeName = sdrfNavigator.getValueByCoordinates(commentColumns.get(AbstractSdrfHandler.COMMENT_DATA_TYPE), rowIndex);
                                if(dataTypesIdByName.get(dataTypeName) != null){
                                    final FileInfo fileInfo= new FileInfo();
                                    fileInfo.setFileName(filename);
                                    fileInfo.setArchiveName(archivename);
                                    fileInfo.setDataTypeId(dataTypesIdByName.get(dataTypeName).intValue());
                                    fileInfoList.add(fileInfo);
                                }else if(!dataTypeName.equals("->")){
                                    warnings.add(mageTabArchive+"\t DataType \t"+dataTypeName+"\tnot found in the database");
                                }
                            }

                            // update  file specific data type into common database and disease database
                            if(fileInfoList.size() > 0){
                                commonFileInfoQueries.updateFileDatTypes(fileInfoList);
                                DiseaseContextHolder.setDisease(mageTabArchive.getTheTumor().getTumorName());;
                                diseaseFileInfoQueries.updateFileDatTypes(fileInfoList);
                            }

                            logger.info("Updated "+fileInfoList.size()+" records.");
                        }
                    }
                }
            }catch(Exception e){
                errors.add(e.getMessage());
            }


        }

        for(String error:errors){
            logger.error(error);
        }
        for(String warning: warnings){
            logger.warn(warning);
        }

    }

    @Autowired
    public void setArchiveQueries(ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    @Autowired
    public void setDataTypeQueries(DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }

    @Autowired
    public void setCommonFileInfoQueries(FileInfoQueries commonFileInfoQueries) {
        this.commonFileInfoQueries = commonFileInfoQueries;
    }

    @Autowired
    public void setDiseaseFileInfoQueries(FileInfoQueries diseaseFileInfoQueries) {
        this.diseaseFileInfoQueries = diseaseFileInfoQueries;
    }

    /**
     * Get the sdrf file
     * @param deployLocation
     * @return sdrf file object
     * @throws Exception
     */
    private TabDelimitedContentNavigator getSdrfFile(final String deployLocation) throws Exception{

        final File[] sdrfFiles = DirectoryListerImpl.getFilesByExtension(deployLocation, AbstractSdrfHandler.SDRF_EXTENSION);
        if(sdrfFiles == null || sdrfFiles.length < 1) {
            // whatever calls this checker knows that an exception means failure, while return with Pending means wait longer...
            throw new Processor.ProcessorException( "The MAGE-TAB archive "+deployLocation+" does not contain an SDRF" );
        }

        final File sdrfFile = sdrfFiles[0];
        // parse the SDRF
        TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
        sdrfParser.setTabDelimitedContent( sdrf );
        sdrfParser.loadTabDelimitedContent( sdrfFile,true );
        sdrfParser.loadTabDelimitedContentHeader();
        TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent( sdrf );
        return sdrfNavigator;
    }

    /**
     * Get comment columns for the given file column
     * @param sdrfNavigator
     * @param fileColumn
     * @return list of comment column and the corresponding file column indexes
     */
    protected Map<String, Integer> getFileCommentColumns(final TabDelimitedContentNavigator sdrfNavigator,
                                                         final int fileColumn) {
        final Map<String, Integer> commentColumnIndex = new HashMap<String, Integer>();
        int columnIndex = fileColumn + 1;
        while (columnIndex < sdrfNavigator.getHeaders().size()) {
            if (sdrfNavigator.getHeaders().get(columnIndex).startsWith("Comment [")) {
                commentColumnIndex.put(sdrfNavigator.getHeaders().get(columnIndex), columnIndex);
            } else {
                break;
            }
            columnIndex++;
        }
        return commentColumnIndex;
    }

}
