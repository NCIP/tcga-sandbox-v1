package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataMatrixFileBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.DataMatrixSplitter;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.SdrfRewriter;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ArchiveCompressor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.Level2DataService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Standalone application to split multiple aliquots level2 data matrix files into
 * individual aliquot data matrix file and update database with new file references
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixSplitterStandalone {
	private static final Logger logger = Logger.getLogger(DataMatrixSplitterStandalone.class);
    private DataMatrixSplitter dataMatrixSplitter;
    private SdrfRewriter sdrfRewriter;
    private Level2DataService level2DataService;
    private PlatformTransactionManager diseaseTransactionManager;
    private PlatformTransactionManager commonTransactionManager;
    private ArchiveCompressor archiveCompressor;

	private static final String appContextFile = "applicationContext.xml";

	public static void main(String[] args) {
        try{
            final ApplicationContext appContext= new ClassPathXmlApplicationContext(appContextFile);
            // Get dataMatrixSplitter
            final DataMatrixSplitterStandalone dataMatrixSplitterStandalone = (DataMatrixSplitterStandalone) appContext.getBean("dataMatrixSplitterTool");
            logger.info("Validating connection parameters..");
            if(dataMatrixSplitterStandalone.validateDatabaseEnvironment()){
                long startTime = System.currentTimeMillis();
                dataMatrixSplitterStandalone.splitDataMatrixFiles();
                logger.info("Total time spent : "+ (System.currentTimeMillis() - startTime)/1000 + " Seconds.");
            }
        }catch(Exception e){
            logger.error("Error: "+e.getMessage(),e);
        }
    }

    public boolean validateDatabaseEnvironment(){
        level2DataService.validateConnections();
        final StringBuilder prompt = new StringBuilder("Do you want to run this tool for ")
                .append(level2DataService.getDisease())
                .append(" disease on ")
                .append(level2DataService.getDatabaseEnvironment())
                .append(" environment (Y/N)? ");

        Scanner in = new Scanner(System.in);
        System.out.print(prompt);
        final String userInput  = in.nextLine();
        if(!userInput.toUpperCase().equals("Y")){
            logger.info("Exiting ..");
            return false;
        }

        return true;
    }

    /**
     * Split multiple aliquots level2 data matrix files into
     * individual aliquot data matrix file and update database with new file references
     */
    public void splitDataMatrixFiles(){
        // get multiple aliquot data matrix archives
        logger.info("Getting multiple aliquot data matrix archives details from database...");
        final Map<String,List<DataMatrixFileBean>> dataMatrixFilesByArchiveName = level2DataService.getMultipleAliquotDataMatrixFiles();

        // For each archive, split multiple aliquot files into individual aliquot file and
        // update the corresponding SDRF file

        final DefaultTransactionDefinition commonTransactionDefinition = new DefaultTransactionDefinition();
    	commonTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    	TransactionStatus commonStatus = commonTransactionManager.getTransaction(commonTransactionDefinition);

        final DefaultTransactionDefinition diseaseTransactionDefinition = new DefaultTransactionDefinition();
    	diseaseTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    	TransactionStatus diseaseStatus = diseaseTransactionManager.getTransaction(diseaseTransactionDefinition);

        for(final String archiveName: dataMatrixFilesByArchiveName.keySet()){
            logger.info("Processing archive "+archiveName+"....");
            List<DataMatrixFileBean> dataMatrixFileBeans = dataMatrixFilesByArchiveName.get(archiveName);

            final Archive archive = new Archive(dataMatrixFileBeans.get(0).getArchiveDeployLocation());
            archive.setDeployLocation(dataMatrixFileBeans.get(0).getArchiveDeployLocation());
            archive.setArchiveType(Archive.TYPE_LEVEL_2);

            final QcContext qcContext = new QcContext();

            try{
                // split data matrix files
                logger.info("Splitting data matrix files...");
                long startTime = System.currentTimeMillis();
                dataMatrixSplitter.doWork(archive,qcContext);
                logger.info(" Time spent: "+ ((System.currentTimeMillis() - startTime)/1000) +" Seconds.");

                logger.info("Created DataMatrixFiles["+qcContext.getSplitDataMatrixFiles().size()+"]: ");

                for(String aliquotName: qcContext.getSplitDataMatrixFiles().keySet()){
                    final Map<String,File> splitDataMatrixFilesByOriginalDataMatrixFile = qcContext.getSplitDataMatrixFiles().get(aliquotName);
                    final StringBuilder logMsg = new StringBuilder("[")
                            .append(aliquotName)
                            .append("] = ");
                    for(String originalDataMatrixFileName:splitDataMatrixFilesByOriginalDataMatrixFile.keySet() ){
                        logger.info( logMsg.toString()+originalDataMatrixFileName+" : "+splitDataMatrixFilesByOriginalDataMatrixFile.get(originalDataMatrixFileName).getName());
                    }

                }
                startTime = System.currentTimeMillis();
                // update SDRF file
                Map<String,String> aliquotBarcodesByHybRefId = updateSDRFFile(archive,qcContext);
                logger.info(" Time spent:"+ ((System.currentTimeMillis() - startTime)/1000) +" Seconds.");
                // Update new file references
                startTime = System.currentTimeMillis();
                updateDBReferences(dataMatrixFileBeans,qcContext,aliquotBarcodesByHybRefId);
                // Remove old file references
                removeOldDBReferences(dataMatrixFileBeans);
                diseaseTransactionManager.commit(diseaseStatus);
                commonTransactionManager.commit(commonStatus);

                // create tar gz and md5 files
                 createCompressedArchiveAndMD5(archive);
                logger.info(" Time spent:"+ ((System.currentTimeMillis() - startTime)/1000) +" Seconds.");
            }catch(Processor.ProcessorException pe){
                logger.error("Error :"+ pe.getMessage());
            }catch(IOException ie){
                logger.error("Error :"+ ie.getMessage());
            }catch(NoSuchAlgorithmException e){
                logger.error("Error :"+ e.getMessage());
            }catch(Exception e){
                logger.error("Error :"+ e.getMessage());
                diseaseTransactionManager.rollback(diseaseStatus);
                commonTransactionManager.rollback(commonStatus);
            }


        }
    }

    /**
     * Update aliquots new file references in SDRF File
     * @param archive
     * @param qcContext
     * @return
     * @throws Processor.ProcessorException
     */
    private Map<String,String> updateSDRFFile(final Archive archive,
                                              final QcContext qcContext) throws Processor.ProcessorException{
        // Get SDRF archive location
        final Matcher archiveMatcher = Experiment.ARCHIVE_NAME_PATTERN.matcher(archive.getArchiveName());
        if(!archiveMatcher.matches()){
            throw new Processor.ProcessorException( "Invalid archive name "+ archive.getArchiveName());
        }
        final String centerName =     archiveMatcher.group(1);
        final String diseaseAbbreviation = archiveMatcher.group(2);
        final String platformName = archiveMatcher.group(3);

        final String sdrfLocation = level2DataService.getSdrfFilePathForExperiment(centerName,
                platformName,
                diseaseAbbreviation);

        logger.info("Updating SDRF file "+sdrfLocation+"...");
        if(sdrfLocation == null || sdrfLocation.isEmpty()) {
            // whatever calls this checker knows that an exception means failure, while return with Pending means wait longer...
            throw new Processor.ProcessorException( "The MAGE-TAB archive does not contain an SDRF" );
        }
        final File sdrfFile = new File(sdrfLocation);

        final Map<String,String> aliquotBarcodesByHybRefId = sdrfRewriter.updateSDRFFile(sdrfFile,qcContext);
        if(aliquotBarcodesByHybRefId.size() == 0){
            throw new Processor.ProcessorException(" Error: SDRF file wasn't updated");
        }
        return aliquotBarcodesByHybRefId;

    }

    /**
     * Add new file details in file_info table and file_to_archive table.
     * Update biospecimen_to_file table with new file_ids
     * @param originalDataMatrixFiles
     * @param qcContext
     * @param aliquotBarcodesByHybRefId
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */

    private void updateDBReferences(final List<DataMatrixFileBean> originalDataMatrixFiles,
                                    final QcContext qcContext,
                                    final Map<String,String> aliquotBarcodesByHybRefId) throws IOException, NoSuchAlgorithmException, Processor.ProcessorException{

        // get new aliquot files
        final Map<String,Map<String,File>> splitDataMatrixFilesByOriginalDataMatrixFileByBarcode = qcContext.getSplitDataMatrixFiles();

        final Map<String,FileInfo>  aliquotFileBeansByFileName = new HashMap<String,FileInfo>();
        final List<FileToArchive>  aliquotFileToArchiveBeans= new ArrayList<FileToArchive>();
        final Map<String,BiospecimenToFile>  biospecimenToFileByFileName = new HashMap<String,BiospecimenToFile>();

        final Map<String,Integer> biospecimenIdsByBarcode = level2DataService.getBiospecimenIdsForBarcodes(new ArrayList(aliquotBarcodesByHybRefId.values()));

        // create FileInfo Beans and FileToArchive Beans for new aliquot files
        for(final String aliquotHybRefId : splitDataMatrixFilesByOriginalDataMatrixFileByBarcode.keySet()){
            final Map<String,File> splitDataMatrixFilesByOriginalDataMatrixFile = splitDataMatrixFilesByOriginalDataMatrixFileByBarcode.get(aliquotHybRefId);
            for(final String originalDtaMatrixFileName:splitDataMatrixFilesByOriginalDataMatrixFile.keySet()){
                final  File splitDataMatrixFile = splitDataMatrixFilesByOriginalDataMatrixFile.get(originalDtaMatrixFileName);
                final FileInfo aliquotFileInfo = new FileInfo();
                final FileToArchive fileToArchive = new FileToArchive();
                final BiospecimenToFile biospecimenToFile = new BiospecimenToFile();

                aliquotFileInfo.setId((long) ConstantValues.NOT_ASSIGNED);
                aliquotFileInfo.setFileName(splitDataMatrixFile.getName());
                aliquotFileInfo.setFileSize(splitDataMatrixFile.length());
                aliquotFileInfo.setDataLevel(ConstantValues.DATA_LEVEL_2);
                aliquotFileInfo.setFileMD5(MD5Validator.getFileMD5(splitDataMatrixFile));
                aliquotFileInfo.setDataTypeId(originalDataMatrixFiles.get(0).getDataTypeId().intValue());

                fileToArchive.setFileArchiveId((long) ConstantValues.NOT_ASSIGNED);
                fileToArchive.setFileLocationURL(splitDataMatrixFile.getPath());
                fileToArchive.setArchiveId(originalDataMatrixFiles.get(0).getArchiveId());
                fileToArchive.setFileInfo(aliquotFileInfo);

                final Long originalDataMatrixFileId= getOriginalDataMatrixFileId(originalDtaMatrixFileName,originalDataMatrixFiles);
                final String aliquotBarcode = aliquotBarcodesByHybRefId.get(aliquotHybRefId);

                if(aliquotBarcode == null){
                    logger.error("Error: Could not find barcode for  "+aliquotHybRefId);
                }else{
                    biospecimenToFile.setBiospecimenId(biospecimenIdsByBarcode.get(aliquotBarcode));
                    biospecimenToFile.setOldFileId(originalDataMatrixFileId);
                    biospecimenToFileByFileName.put(aliquotFileInfo.getFileName(),biospecimenToFile);
                }
                aliquotFileBeansByFileName.put(aliquotFileInfo.getFileName(),aliquotFileInfo);
                aliquotFileToArchiveBeans.add(fileToArchive);
            }
        }

        logger.info(" Updating file_info table ....");
        // insert split data matrix files details into file_info table
        level2DataService.addFiles(new ArrayList(aliquotFileBeansByFileName.values()));
        logger.info(" Updating file_to_archive table ....");
        // insert split data matrix files  into file_to_archive
        level2DataService.addFileToArchiveAssociations(aliquotFileToArchiveBeans);
        // update biospecimentofile
        logger.info(" Updating biospecimen_to_file table ....");
        //update fileIds
        for(final String fileName:biospecimenToFileByFileName.keySet()){
            final BiospecimenToFile biospecimenToFile = biospecimenToFileByFileName.get(fileName);
            biospecimenToFile.setFileId(aliquotFileBeansByFileName.get(fileName).getId());
        }
        level2DataService.updateBiospecimenToFileAssociations(new ArrayList<BiospecimenToFile>(biospecimenToFileByFileName.values() ));
    }

    /**
     * Remove old file references from file_info table and file_to-archive table
     * @param originalDataMatrixFiles
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */

    private void removeOldDBReferences(final List<DataMatrixFileBean> originalDataMatrixFiles) throws IOException, NoSuchAlgorithmException{
        // get Ids
        final List<Long> fileIds = new ArrayList<Long>();
        for(final DataMatrixFileBean dataMatrixFileBean: originalDataMatrixFiles){
            fileIds.add(dataMatrixFileBean.getFileId());
        }

        logger.info("Removing old file ids from file_to_archive table ...");
        level2DataService.deleteFileToArchiveAssociations(fileIds,originalDataMatrixFiles.get(0).getArchiveId());
        logger.info("Removing old file ids from file_info table ...");
        level2DataService.deleteFiles(fileIds);
    }

    private Long getOriginalDataMatrixFileId(final String oldDataMatrixFileName, List<DataMatrixFileBean> dataMatrixFileBeans) throws Processor.ProcessorException{
        for(final DataMatrixFileBean dataMatrixFileBean: dataMatrixFileBeans){
            if(dataMatrixFileBean.getFileName().equals(oldDataMatrixFileName)){
                return dataMatrixFileBean.getFileId();
            }
        }
        throw new Processor.ProcessorException("Error getting file_id for multiple aliquots file "+oldDataMatrixFileName);
    }

    private void createCompressedArchiveAndMD5(final Archive archive) throws IOException,NoSuchAlgorithmException{

        FileWriter writer = null;
        try {
            final List<File> filesToBeCompressed = Arrays.asList(DirectoryListerImpl.getFilesInDir(archive.getDeployDirectory()));
            final File compressedFile = archiveCompressor.compress(filesToBeCompressed,archive.getArchiveName(),archive.getArchiveFile().getParentFile()) ;

            final File md5File = new File(archive.getDeployDirectory() + archiveCompressor.getExtension() + ".md5");
            final String archiveChecksum = MD5Validator.getFileMD5(compressedFile);
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer = new FileWriter(md5File);
            writer.write(archiveChecksum);
            writer.write("  ");
            writer.write(compressedFile.getName());
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (final IOException e) {
                    // Ignore
                }
            }
        }
    }

    @Autowired
    public void setDataMatrixSplitter(DataMatrixSplitter dataMatrixSplitter) {
        this.dataMatrixSplitter = dataMatrixSplitter;
    }
    @Autowired
    public void setSdrfRewriter(SdrfRewriter sdrfRewriter) {
        this.sdrfRewriter = sdrfRewriter;
    }
    @Autowired
    public void setLevel2DataService(Level2DataService level2DataService) {
        this.level2DataService = level2DataService;
    }
    @Autowired
    public void setDiseaseTransactionManager(PlatformTransactionManager diseaseTransactionManager) {
        this.diseaseTransactionManager = diseaseTransactionManager;
    }
    @Autowired
    public void setCommonTransactionManager(PlatformTransactionManager commonTransactionManager) {
        this.commonTransactionManager = commonTransactionManager;
    }
    @Autowired
    public void setArchiveCompressor(ArchiveCompressor archiveCompressor) {
        this.archiveCompressor = archiveCompressor;
    }
}
