package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileCopier;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * This copies files from the previous versions of all experiment archives into the new (uploaded) ones.
 * If a file isn't found it will just ignore it, assuming another validator will be run/has been run to check for
 * manifest correctness.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PreviousArchiveFileCopier extends AbstractProcessor<Archive, Boolean> {
    private ManifestParser manifestParser;

    @Override
    protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        if (archive.getDeployStatus().equals(Archive.STATUS_UPLOADED)) {
            final Archive previousArchive = context.getExperiment().getPreviousArchiveFor(archive);
            if (previousArchive != null) {
                // go through manifest of this archive, anything that doesn't exist should be copied from previous
                try {
                    copyFilesFromPrevious(archive, previousArchive, context);
                } catch (IOException e) {
                    throw new ProcessorException(e.getMessage(), e);
                } catch (ParseException e) {
                    throw new ProcessorException(e.getMessage(), e);
                }
            }
        }
        return true; // never return false -- throw exception in case of problem
    }

    private void copyFilesFromPrevious(final Archive archive, final Archive previousArchive, final QcContext context) throws IOException, ParseException, ProcessorException {
        final File manifest = new File(archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE);
        if (!manifest.exists()) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            throw new ProcessorException( new StringBuilder().append( "Archive is missing its " ).
                    append( ManifestValidator.MANIFEST_FILE ).append( " file" ).toString() );
        }
        final Map<String, String> manifestEntries = manifestParser.parseManifest(manifest);
        for (final String filename : manifestEntries.keySet()) {
            File file = new File(archive.getDeployDirectory(), filename);
            if (!file.exists()) {
                file = new File(previousArchive.getDeployDirectory(), filename);
                if (file.exists()) {
                    // copy it into the new archive's "deploy" dir which is actually the upload dir
                    FileCopier.copy(file, new File(archive.getDeployDirectory()));
                     context.getFilesCopiedFromPreviousArchive().add(file.getName());
                } // else assume manifest validator will be run and will catch this, so ignore...
            }
        }
    }

    public void setManifestParser(final ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }

    @Override
    public String getName() {
        return "previous archive file copier";
    }
}


