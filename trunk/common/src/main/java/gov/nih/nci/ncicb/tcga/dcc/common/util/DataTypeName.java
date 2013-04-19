package gov.nih.nci.ncicb.tcga.dcc.common.util;

/**
 * This class represents data type names.
 *
 * @author ramanr
 *         Last updated by: ramanr
 * @version $Rev$
 */
public enum DataTypeName {

    SOMATIC_MUTATIONS("Somatic Mutations"),
    PROTECTED_MUTATIONS("Protected Mutations");

    private String value;

    private DataTypeName(final String datatypeName) {
        this.value = datatypeName;
    }

    /**
     * Get data type name.
     * @return  data type name.
     */

    public String getValue() {
        return value;
    }

}
