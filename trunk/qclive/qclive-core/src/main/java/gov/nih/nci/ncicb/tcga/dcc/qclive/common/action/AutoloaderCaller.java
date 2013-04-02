package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookupFromMap;
import gov.nih.nci.ncicb.tcga.dcc.common.service.StatusCallback;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ProtocolNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.service.LoaderEnqueuer;
import gov.nih.nci.ncicb.tcga.dcc.qclive.service.LoaderStarter;
import org.quartz.SchedulerException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Calls the autoloader for each archive that was just deployed.  What should it return?
 * A boolean?  Sure, for now.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AutoloaderCaller extends AbstractSdrfHandler<List<Archive>, Boolean> implements Serializable {

    protected Boolean doWork(final List<Archive> deployedArchives, final QcContext context) throws ProcessorException {

        LoaderStarter loaderStarter = getLoaderStarter();

        List<Archive> level2Archives = new ArrayList<Archive>();
        Archive mageTabArchive = null;
        for (Archive archive : deployedArchives) {
            if (archive.getArchiveType().equals(Archive.TYPE_LEVEL_2)) {
                level2Archives.add(archive);
            } else if (archive.getArchiveType().equals(Archive.TYPE_MAGE_TAB)) {
                mageTabArchive = archive;
            }
        }

        if (mageTabArchive == null) {
            throw new ProcessorException("Mage-tab archive not present in deployed archive list");

        } else {
            for (Archive archive : level2Archives) {
                FileTypeLookup lookup = makeFileTypeLookup(archive);
                StatusCallback callback = makeStatusCallback(archive);
                try {
                    loaderStarter.queueLoaderJob(archive.getDeployDirectory(), mageTabArchive.getDeployDirectory(), lookup, callback, archive.getExperimentName());
                }
                catch (SchedulerException e) {
                    throw new ProcessorException(new StringBuilder().
                            append("Scheduler exception caught during attempt to queue job for ").
                            append(archive.getRealName()).append(": ").append(e.toString()).toString());
                }
            }
        }

        return true;
    }

    protected LoaderStarter getLoaderStarter() {
        return LoaderEnqueuer.getLoaderStarter();
    }

    public String getName() {
        return "portal data loader";
    }

    protected StatusCallback makeStatusCallback(final Archive archive) {
        return new StatusCallback() {
            public void sendStatus(final Status status) {
                //Live.getInstance().autoloaderDone(status, archive.getRealName(), archive.getId());
            }
        };
    }

    private FileTypeLookup makeFileTypeLookup(final Archive archive) throws ProcessorException {
        TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(archive.getSdrf());
        FileTypeLookupFromMap lookup = getLookupObject(archive);
        for (final String fileColumnName : FILE_COLUMN_NAMES) {
            final List<Integer> fileColumns = sdrfNavigator.getHeaderIdsForName(fileColumnName);
            for (Integer fileColumn : fileColumns) {
                // get comment columns
                Map<String, Integer> commentColumns = getFileCommentColumns(sdrfNavigator, fileColumn);
                int protocolColumn = findProtocolColumnForFileColumn(sdrfNavigator, fileColumn);
                // start with first row, not header
                for (int i = 1; i < sdrfNavigator.getNumRows(); i++) {
                    // get filename for this line and other info
                    String filename = sdrfNavigator.getValueByCoordinates(fileColumn, i);
                    String level = sdrfNavigator.getValueByCoordinates(commentColumns.get(COMMENT_DATA_LEVEL), i);
                    String include = sdrfNavigator.getValueByCoordinates(commentColumns.get(COMMENT_INCLUDE_FOR_ANALYSIS), i);
                    if (level.endsWith("2") && include.equalsIgnoreCase("yes")) {
                        if (protocolColumn != -1) {
                            String protocol = sdrfNavigator.getValueByCoordinates(protocolColumn, i);
                            ProtocolNameValidator protocolValidator = new ProtocolNameValidator(protocol);
                            String protocolType = protocolValidator.getProtocolType();
                            if (protocolType == null) {
                                throw new ProcessorException("Protocol for " + filename + " not formatted correctly (" + protocol + ")");
                            }
                            lookup.addFileType(filename, protocolValidator.getProtocolType());
                        } else {
                            throw new ProcessorException("No protocol found for " + filename);
                        }

                    }
                }
            }
        }

        return lookup;
    }

    /**
     * This method is here to make testing easier.
     *
     * @param archive the archive we are about to load
     * @return a valid FileTypeLookupFromMap for this archive
     */
    protected FileTypeLookupFromMap getLookupObject(Archive archive) {
        return new FileTypeLookupFromMap(archive.getRealName(), archive.getDomainName(), archive.getPlatform());
    }

}
