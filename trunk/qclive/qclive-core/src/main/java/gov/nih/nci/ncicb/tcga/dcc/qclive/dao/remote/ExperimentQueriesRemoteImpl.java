package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.remote;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Remote implementation of ExperimentQueries.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExperimentQueriesRemoteImpl implements ExperimentQueries {
    private RemoteValidationHelper remoteValidationHelper;

    public ExperimentQueriesRemoteImpl(final RemoteValidationHelper remoteValidationHelper) {
        this.remoteValidationHelper = remoteValidationHelper;
    }

    /**
     * NOT IMPLEMENTED IN THIS CLASS.
     *
     * @param experimentName the name of the experiment to get
     * @return null
     */
    @Override
    public Experiment getExperiment(final String experimentName) {
        return null;
    }

    /**
     * NOT IMPLEMENTED IN THIS CLASS.
     *
     * @param archiveName the name of the archive
     * @return null
     */
    @Override
    public Experiment getExperimentForSingleArchive(final String archiveName) {
        return null;
    }

    /**
     * Get a map of all data files for all latest archives in the given experiment.
     *
     * @param experimentName the experiment name (center_disease.platform)
     * @return map of Archive and FileInfo Lists for the experiment
     */
    @Override
    public Map<Archive, List<FileInfo>> getExperimentDataFiles(final String experimentName) {
        final Map<Archive, List<FileInfo>> dataFiles = new HashMap<Archive, List<FileInfo>>();
        final Matcher experimentNameMatcher = Experiment.EXPERIMENT_NAME_PATTERN.matcher(experimentName);
        if (experimentNameMatcher.matches()) {
            final String centerName = experimentNameMatcher.group(1);
            final String diseaseName = experimentNameMatcher.group(2);
            final String platformName = experimentNameMatcher.group(3);

            try {
                final List<gov.nih.nci.ncicb.tcga.dccws.Archive> experimentWsArchives = remoteValidationHelper.getLatestArchives(diseaseName, centerName, platformName);
                for (final gov.nih.nci.ncicb.tcga.dccws.Archive wsArchive : experimentWsArchives) {
                    final Archive archive = new Archive();
                    archive.setId(Long.valueOf(wsArchive.getId()));
                    archive.setRealName(wsArchive.getName());

                    final List<gov.nih.nci.ncicb.tcga.dccws.FileInfo> archiveWsFiles = remoteValidationHelper.getArchiveDataFiles(wsArchive);
                    final List<FileInfo> archiveFiles = new ArrayList<FileInfo>();
                    for (final gov.nih.nci.ncicb.tcga.dccws.FileInfo wsFile : archiveWsFiles) {
                        final FileInfo fileInfo = new FileInfo();
                        fileInfo.setId(Long.valueOf(wsFile.getId()));
                        fileInfo.setFileName(wsFile.getName());
                        archiveFiles.add(fileInfo);
                    }

                    dataFiles.put(archive, archiveFiles);
                }
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }
        return dataFiles;
    }
}
