package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveDeletionBean;
import org.apache.log4j.Level;

import java.io.File;
import java.util.Date;


/**
 * Deletes archive file.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveDeletionJob implements QueueJob<ArchiveDeletionBean> {

    protected ProcessLoggerI logger;

    public ProcessLoggerI getLogger() {
        return logger;
    }

    public void setLogger(final ProcessLoggerI logger) {
        this.logger = logger;
    }

    /**
     * Run the job and throw any exception that might arise so that JobDelegate can re-throw it
     *
     * @param archiveDeletionBean an <code>ArchiveDeletionBean</code> that holds the name of the archive to be deleted
     * @throws Exception
     */
    public void run(final ArchiveDeletionBean archiveDeletionBean) throws Exception {

        final File file = new File(archiveDeletionBean.getArchiveName());

        if (file.exists()) {

            final boolean successfullyDeleted = file.delete();
            getLogger().logToLogger(Level.INFO,
                    new StringBuilder("Archive ")
                            .append(!successfullyDeleted?"un":"")
                            .append("successfully deleted ")
                            .append(archiveDeletionBean.getArchiveName())
                            .append("at ")
                            .append(new Date())
                            .toString()
            );

        } else {

            getLogger().logToLogger(Level.INFO,
                    new StringBuilder("File does not exist: ")
                            .append(archiveDeletionBean.getArchiveName())
                            .append(" ")
                            .append(new Date())
                            .toString()
            );
        }
    }
}
