package gov.nih.nci.ncicb.tcga.dcc.common.util;

/**
 * This class represents spring bean names.
 *
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */
public enum SpringBeanName {

    BAM_LOADER("bamLoader"),
    MAIL_ERROR_HELPER("mailErrorHelper");

    private String value;

    private SpringBeanName(final String beanName) {
        this.value = beanName;
    }

    /**
     * Get spring bean name.
     *
     * @return bean name.
     */
    public String getValue() {
        return value;
    }


}
