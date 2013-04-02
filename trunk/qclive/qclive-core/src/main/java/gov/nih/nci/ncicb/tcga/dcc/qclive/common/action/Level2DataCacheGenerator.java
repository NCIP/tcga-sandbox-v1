package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.Level2DataQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.service.Level2DataService;
import gov.nih.nci.ncicb.tcga.dcc.common.service.Level2DataServiceI;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.Messages;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderQueries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Class which generate level2 data cache files for the given disease,platform,center and experiment Ids.
 * It generates cache files for each source file type for the given disease,platform,center,experiment ids
 * data sets.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level2DataCacheGenerator implements Level2DataCacheGeneratorI {

    private final Log logger = LogFactory.getLog(getClass());
    private LoaderQueries loaderQueries;
    private Level2DataQueries level2DataQueries;
    private Level2DataServiceI level2DataService;
    private String cacheFileDirectory;
    private String tmpCacheFileDirectory;
    private String cacheFileDistroDirectory;
    private MailErrorHelper errorMailSender;

    public LoaderQueries getLoaderQueries() {
        return loaderQueries;
    }

    public void setLoaderQueries(LoaderQueries loaderQueries) {
        this.loaderQueries = loaderQueries;
    }

    public Level2DataServiceI getLevel2DataService() {
        return level2DataService;
    }

    public void setLevel2DataService(Level2DataServiceI level2DataService) {
        this.level2DataService = level2DataService;
    }

    public Level2DataQueries getLevel2DataQueries() {
        return level2DataQueries;
    }

    public void setLevel2DataQueries(Level2DataQueries level2DataQueries) {
        this.level2DataQueries = level2DataQueries;
    }

    public String getCacheFileDirectory() {
        return cacheFileDirectory;
    }

    public void setCacheFileDirectory(String cacheFileDirectory) {
        this.cacheFileDirectory = cacheFileDirectory;
    }

    public String getTmpCacheFileDirectory() {
        return tmpCacheFileDirectory;
    }

    public void setTmpCacheFileDirectory(String tmpCacheFileDirectory) {
        this.tmpCacheFileDirectory = tmpCacheFileDirectory;
    }

    public MailErrorHelper getErrorMailSender() {
        return errorMailSender;
    }

    public void setErrorMailSender(MailErrorHelper errorMailSender) {
        this.errorMailSender = errorMailSender;
    }

    public String getCacheFileDistroDirectory() {
        return cacheFileDistroDirectory;
    }

    public void setCacheFileDistroDirectory(String cacheFileDistroDirectory) {
        this.cacheFileDistroDirectory = cacheFileDistroDirectory;
    }

    /**
     * Generates cache files for the given level2Data filter. The cache file is generated in the
     * tmp dir and then moved to cache dir. Also the cache file is compressed and moved to distro dir.
     * The following filter should be set in the filter bean.
     * - disease
     * - platformName
     * - centerDomainName
     * - Experiment Ids
     *
     * @param level2DataFilterBean
     * @throws Exception
     */

    public void generateCacheFiles(Level2DataFilterBean level2DataFilterBean) throws Exception {
        final StringBuilder errorMessages = new StringBuilder();

        try {
            setDiseaseContext(level2DataFilterBean.getDiseaseAbbreviation());
            final int platformId = loaderQueries.lookupPlatformId(level2DataFilterBean.getPlatformName());
            final int centerId = loaderQueries.lookupCenterId(level2DataFilterBean.getCenterDomainName(), platformId);

            // get experiment source file types from the data set table
            List<String> sourceFileTypes = level2DataQueries.getExperimentSourceFileTypes(level2DataFilterBean.getExperimentIdList());


            final String cacheTmpDirPath = getTmpDirectoryPath();

            // for each file type populate the cache
            for (final String sourceFileType : sourceFileTypes) {
                // get datafile name
                final String cacheFileName = Level2DataService.getFileName(level2DataFilterBean.getDiseaseAbbreviation(),
                        level2DataFilterBean.getPlatformName(),
                        level2DataFilterBean.getCenterDomainName(),
                        sourceFileType);
                final String tmpCacheFile = cacheTmpDirPath + cacheFileName;
                logger.info("Generating cache " + tmpCacheFile + "started.");
                // generate cache file in the tmp dir
                level2DataService.generateDataFile(platformId,
                        centerId,
                        sourceFileType,
                        tmpCacheFile);

                // compress generated cache file and move it to cache distro dir
                final String compressedFile = getCacheFileDistroDirectory() +
                        FileUtil.getFilenameWithoutExtension(cacheFileName, Level2DataService.FILE_EXTENSION) +
                        ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
                try {
                    FileUtil.createCompressedFile(tmpCacheFile, compressedFile);
                } catch (IOException e) {
                    // record this error and send an email after all the files are generated
                    errorMessages.append(MessageFormat.format(Messages.FILE_COMPRESS_ERR_MSG, compressedFile, e.toString()));
                }
                // move cache file from tmpCache dir to cache dir
                FileUtil.move(tmpCacheFile, getCacheFileDirectory() + cacheFileName);
                logger.info("Generated cache " + getCacheFileDirectory() + cacheFileName);
            }
            // update daminuse field in dataset table
            level2DataQueries.updateDataSetUseInDAMStatus(level2DataFilterBean.getExperimentIdList());
        } catch (Exception e) {
            String errMsg = MessageFormat.format(Messages.LEVEL2_DATA_GENERATION_ERR_MSG,
                    level2DataFilterBean.getDiseaseAbbreviation(),
                    level2DataFilterBean.getCenterDomainName(),
                    level2DataFilterBean.getPlatformName(),
                    level2DataFilterBean.getExperimentIdsAsString(),
                    e.toString());
            logger.error(errMsg, e);
            errorMessages.append(errMsg);

            throw e;
        } finally {
            if (errorMessages.toString().length() > 0) {
                errorMailSender.send(Messages.LEVEL2_DATA_GENERATION_ERR, errorMessages.toString());
            }
        }
    }

    private void setDiseaseContext(final String diseaseAbbreviation) {
        DiseaseContextHolder.setDisease(diseaseAbbreviation);
    }

    private String getTmpDirectoryPath() throws IOException {
        // create tmp dir if it doesn't exist
        final File cacheTmpDir = new File(getTmpCacheFileDirectory());
        if (!cacheTmpDir.exists()) {
            cacheTmpDir.mkdir();
        }
        String cacheTmpDirPath = cacheTmpDir.getCanonicalPath();
        if (!cacheTmpDirPath.endsWith(File.separator)) {
            cacheTmpDirPath += File.separator;
        }
        return cacheTmpDirPath;
    }


}
