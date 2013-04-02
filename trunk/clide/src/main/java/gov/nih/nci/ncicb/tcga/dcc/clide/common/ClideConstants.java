/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common;

import java.io.File;
import java.io.FileFilter;

/**
 * Common values that will remain unchanged in clide.
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClideConstants {


    public static final String APP_CONTEXT = "applicationContext-clide.xml";
    public static final String INTEGRATION_CONTEXT = "integrationContext-clide.xml";
    public static final double DOUBLE_HTTP_CHUNK_BUF_SIZE = 1048576.0;
    public static final String UTF8 = "UTF-8";

    public static final int DEFAULT_TIMEOUT = 300;

    /* our extention for encrypted files */
    public static final String ENC_EXT = ".enc";

    /**
     * return everything accept hidden files
     */
    public static class NonHiddenFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            return !pathname.isHidden();
        }
    }

    public static class ReadableFileFilter extends NonHiddenFileFilter {
        public boolean accept(final File file) {
            return super.accept(file)
                    && !file.isDirectory()
                    && file.canRead();
        }
    }

    /**
     * currently returns everything but our encrypted archive files
     */
    public static class ArchiveFilter extends ReadableFileFilter {
        public boolean accept(final File file) {
            return super.accept(file) &&
                    !file.getName().endsWith(ENC_EXT);
        }
    }

    /**
     * Only returns our encrypted archive files
     */
    public static class EncryptedArchiveFilter extends ReadableFileFilter {
        public boolean accept(final File file) {
            return super.accept(file) &&
                    file.getName().endsWith(ENC_EXT);
        }
    }
}
