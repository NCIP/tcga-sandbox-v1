package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import java.io.File;

/**
 * Utilities for BCR archives
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BCRUtils {

    /**
     * Return <code>true</code> if the given {@link File} is a clinical file, <code>false</code> otherwise.
     *
     * @param file the file
     * @return <code>true</code> if the given {@link File} is a clinical file, <code>false</code> otherwise
     */
    public boolean isClinicalFile(final File file);

    /**
     * Return <code>true</code> if the given {@link File} is a Biospecimen file, <code>false</code> otherwise.
     *
     * @param file the file
     * @return <code>true</code> if the given {@link File} is a Biospecimen file, <code>false</code> otherwise
     */
    public boolean isBiospecimenFile(final File file);

    /**
     * Return <code>true</code> if the given {@link File} is an Auxiliary file, <code>false</code> otherwise.
     *
     * @param file
     * @return <code>true</code> if the given {@link File} is an Auxiliary file, <code>false</code> otherwise
     */
    public boolean isAuxiliaryFile(final File file);

    /**
     * Return <code>true</code> if the given {@link File} is an cell line control file, <code>false</code> otherwise.
     *
     * @param file
     * @return <code>true</code> if the given {@link File} is an cell line control file, <code>false</code> otherwise
     */
    public boolean isControlFile(final File file);
}
