package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.io.IOException;

/**
 * Interface for SourceFileTypeFinder
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface SourceFileTypeFinder {

    /**
     * Finds the source file type for the given file, if any.
     *
     * @param fileId the id of the file whose source file type to find
     * @return the source file type, or null if none
     * @throws IOException
     */
    public String findSourceFileType(long fileId) throws IOException;
}
