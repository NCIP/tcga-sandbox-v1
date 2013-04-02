package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Level2DataCacheGeneratorI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 * Class which handles generating level2 data cache file jobs.
 * This is defined as Stateful job so that job for the same disease_platform_center
 * will not be executed in parallel
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level2DataCacheGenerationJob implements StatefulJob, ConstantValues {

    private final Log logger = LogFactory.getLog(getClass());
    protected Level2DataCacheGeneratorI level2DataCacheGenerator;

    public Level2DataCacheGenerationJob() {
        initFields();
    }

    public void execute(final JobExecutionContext context) throws JobExecutionException {
        try {
            logger.info("Generating cache files for Level2 Data Job " + context.getJobDetail() + " started.");
            final Level2DataFilterBean filterBean = (Level2DataFilterBean) context.getJobDetail().getJobDataMap().get(DATA_BEAN);
            level2DataCacheGenerator.generateCacheFiles(filterBean);

        } catch (Throwable e) {
            throw new JobExecutionException(e);
        } finally {
            try {
                // As the job durability is set to true, the job will not get deleted automatically.
                // remove the job manually
                context.getScheduler().deleteJob(context.getJobDetail().getName(), context.getJobDetail().getGroup());
                logger.info("Generating cache files for Level2 Data Job " + context.getJobDetail() + " completed.");
            } catch (Throwable e) {
                logger.error("Level2DataCache cleanup Error " + e.toString());
            }

        }
    }

    protected void initFields() {
        level2DataCacheGenerator = (Level2DataCacheGeneratorI) SpringApplicationContext.getObject(LEVEL2_CACHE_GENERATOR_SPRING_BEAN_NAME);
    }
}
