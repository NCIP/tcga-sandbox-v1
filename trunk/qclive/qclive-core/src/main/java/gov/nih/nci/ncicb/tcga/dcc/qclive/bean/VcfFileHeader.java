package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import java.util.Map;

/**
 * Bean representing a single header line in a VCF file
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFileHeader {
    final private String name;
    private String value;
    private Map<String, String> valueMap;
    private Integer lineNumber;

    /**
     * Create a VcfFileHeader object of the given type.
     *
     * @param name the header's name (or type)
     */
    public VcfFileHeader(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(final Map<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(final Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getValueFor(final String key) {
        String value = null;
        if (valueMap != null) {
            value = valueMap.get(key);
        }
        return value;
    }
}
