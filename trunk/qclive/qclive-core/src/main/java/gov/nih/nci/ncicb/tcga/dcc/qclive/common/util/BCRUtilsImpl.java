package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import java.io.File;

/**
 * Utilities implementation for BCR archives
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BCRUtilsImpl implements BCRUtils {


    /**
     * The expected string in a clinical filename
     */
    private static final String CLINICAL = "clinical";

    /**
     * The expected string in a biospecimen filename
     */
    public static final String BIOSPECIMEN = "biospecimen";

    /**
     * The expected string in an auxiliary filename
     */
    public static final String AUXILIARY = "auxiliary";

    /**
     * The expected string in a control filename
     */
    public static final String CONTROL = "control";

    @Override
    public boolean isClinicalFile(final File file) {
        return file.getName().contains(CLINICAL);
    }

    @Override
    public boolean isBiospecimenFile(final File file) {
        return file.getName().contains(BIOSPECIMEN);
    }

    @Override
    public boolean isAuxiliaryFile(final File file) {
        return file.getName().contains(AUXILIARY);
    }

    @Override
    public boolean isControlFile(final File file){
        return file.getName().contains(CONTROL);
    }
}
