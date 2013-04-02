package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Bean to hold shipped biospecimen element details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ShippedBiospecimenElement {
    public static final String SHIPPED_ELEMENT_TYPE_NAME_SAMPLE_TYPE_CODE = "sample_type_code";
    public static final String SHIPPED_ELEMENT_TYPE_NAME_SAMPLE_SEQUENCE = "sample_sequence";
    public static final String SHIPPED_ELEMENT_TYPE_NAME_PORTION_SEQUENCE = "portion_sequence";
    public static final String SHIPPED_ELEMENT_TYPE_NAME_PLATE_ID = "plate_id";
    public static final String SHIPPED_ELEMENT_TYPE_ANALYTE_TYPE_CODE = "analyte_code";

    private Long shippedBiospecimenElementId;
    private Long shippedBiospecimenId;
    private Integer elementTypeId;
    private String elementValue;
    private String elementName;

    public Long getShippedBiospecimenElementId() {
        return shippedBiospecimenElementId;
    }

    public void setShippedBiospecimenElementId(Long shippedBiospecimenElementId) {
        this.shippedBiospecimenElementId = shippedBiospecimenElementId;
    }

    public Long getShippedBiospecimenId() {
        return shippedBiospecimenId;
    }

    public void setShippedBiospecimenId(Long shippedBiospecimenId) {
        this.shippedBiospecimenId = shippedBiospecimenId;
    }

    public Integer getElementTypeId() {
        return elementTypeId;
    }

    public void setElementTypeId(Integer elementTypeId) {
        this.elementTypeId = elementTypeId;
    }

    public String getElementValue() {
        return elementValue;
    }

    public void setElementValue(String elementValue) {
        this.elementValue = elementValue;
    }

    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return elementName;
    }
}
