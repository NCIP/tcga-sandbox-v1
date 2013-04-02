package gov.nih.nci.ncicb.tcga.dcc.qclive.standalone;

import com.thoughtworks.xstream.XStream;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.service.Level2DataCacheEnqueuerI;
import org.apache.commons.io.IOUtils;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * Schedules level2 jobs
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class JOBGenerator {
    private Level2DataCacheEnqueuerI level2DataCacheEnqueuer;

    public Level2DataCacheEnqueuerI getLevel2DataCacheEnqueuer() {
        return level2DataCacheEnqueuer;
    }

    public void setLevel2DataCacheEnqueuer(Level2DataCacheEnqueuerI level2DataCacheEnqueuer) {
        this.level2DataCacheEnqueuer = level2DataCacheEnqueuer;
    }

    public void scheduleCacheGeneratorJobs() throws SchedulerException, IOException {
        List<Level2DataFilterBean> jobList = getJobList();
        try {
            for (Level2DataFilterBean level2DataFilterBean : jobList) {
                JobDetail jobDetail = level2DataCacheEnqueuer.addJob(level2DataFilterBean);
                level2DataCacheEnqueuer.scheduleTrigger(jobDetail);
                System.out.println(" Schduled job for " + level2DataFilterBean);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
        } catch (SchedulerException se) {
            System.out.println(" Could not schedule job. " + se.toString());
            throw se;
        }
    }

    private ArrayList<Level2DataFilterBean> getJobList() throws IOException {

        FileReader fileReader = null;
        try {
            // get the data from XML file
            File xmlFile = new File(CacheGeneratorApp.DATA_FILE);
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileReader = new FileReader(xmlFile);
            long fileSize = xmlFile.length();

            char[] dataInBytes = new char[(int) fileSize];
            fileReader.read(dataInBytes);
            String jobDataAsXMLString = new String(dataInBytes);

            // convert to Object
            XStream xstream = new XStream();
            xstream.alias("Level2DataFilterBean", Level2DataFilterBean.class);
            return (ArrayList<Level2DataFilterBean>) xstream.fromXML(jobDataAsXMLString);

        } catch (IOException e) {
            System.out.println(" Error reading file " + CacheGeneratorApp.DATA_FILE + e.getMessage());
            throw e;
        } finally {
            IOUtils.closeQuietly(fileReader);
        }

    }
}
