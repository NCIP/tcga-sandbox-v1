package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean to hold shipped biospecimen details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ShippedBiospecimen {
    public static final String SHIPPED_ITEM_NAME_PORTION = "Shipping Portion";
    public static final String SHIPPED_ITEM_NAME_ALIQUOT = "Aliquot";

    // shipped portion biospecimen barcode which contains slide and receiving center id
    public final static Pattern SHIPPED_PORTION_BARCODE_PATTERN = Pattern.compile(CommonBarcodeAndUUIDValidatorImpl.SHIPMENT_PORTION_BARCODE_REGEXP);
    public final static int PROJECT_CODE_INDEX = 2;
    public final static int TSS_CODE_INDEX = 3;
    public final static int PARTICIPANT_CODE_INDEX = 4;
    public final static int SAMPLE_TYPE_CODE_INDEX = 6;
    public final static int SAMPLE_SEQUENCE_INDEX = 7;
    public final static int PORTION_SEQUENCE_INDEX = 8;
    public final static int PLATE_INDEX = 9;
    public final static int RECEIVING_CENTER_INDEX = 10;

    private Long shippedBiospecimenId;
    private String uuid;
    private String diseaseAbbreviation;
    private String barcode;
    private String projectCode;
    private String tssCode;
    private String participantCode;
    private String bcrCenterId;
    private Date shippedDate;

    private Boolean isViewable;
    private Boolean isRedacted;

    private String shippedBiospecimenType;
    private Integer shippedBiospecimenTypeId;

    private Integer batchNumber;

    // these are all elements...
    private ShippedBiospecimenElement sampleTypeCode;
    private ShippedBiospecimenElement sampleSequence;
    private ShippedBiospecimenElement portionSequence;
    private ShippedBiospecimenElement analyteTypeCode;
    private ShippedBiospecimenElement plateId;

    public ShippedBiospecimen() {
        isViewable = true;
        isRedacted = false;
    }

    /**
     * Gets a list of all the properties of this shipped biospecimen that are ShippedBiospecimenElement objects.
     *
     * @return list of ShippedBiospecimenElement
     */
    public List<ShippedBiospecimenElement> getShippedBiospecimenElements() {
        List<ShippedBiospecimenElement> elements = new ArrayList<ShippedBiospecimenElement>();
        if (sampleTypeCode != null) {
            elements.add(sampleTypeCode);
        }
        if (sampleSequence != null) {
            elements.add(sampleSequence);
        }
        if (portionSequence != null) {
            elements.add(portionSequence);
        }
        if (analyteTypeCode != null) {
            elements.add(analyteTypeCode);
        }
        if (plateId != null) {
            elements.add(plateId);
        }
        return elements;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        if (uuid != null) {
            this.uuid = uuid.toLowerCase();
        }
    }

    public String getDiseaseAbbreviation() {
        return diseaseAbbreviation;
    }

    public void setDiseaseAbbreviation(String diseaseAbbreviation) {
        this.diseaseAbbreviation = diseaseAbbreviation;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getAnalyteTypeCode() {
        return (analyteTypeCode == null ? null : analyteTypeCode.getElementValue());
    }

    public void setAnalyteTypeCode(final String analyteTypeCode) {
        this.analyteTypeCode = makeShippedBiospecimenElement(ShippedBiospecimenElement.SHIPPED_ELEMENT_TYPE_ANALYTE_TYPE_CODE, analyteTypeCode);
    }

    public String getSampleTypeCode() {
        return (sampleTypeCode == null ? null : sampleTypeCode.getElementValue());
    }

    public void setSampleTypeCode(String sampleTypeCodeValue) {
        sampleTypeCode = makeShippedBiospecimenElement(ShippedBiospecimenElement.SHIPPED_ELEMENT_TYPE_NAME_SAMPLE_TYPE_CODE, sampleTypeCodeValue);
    }

    public String getSampleSequence() {
        return (sampleSequence == null ? null : sampleSequence.getElementValue());
    }

    public void setSampleSequence(String sampleSequenceValue) {
        this.sampleSequence = makeShippedBiospecimenElement(ShippedBiospecimenElement.SHIPPED_ELEMENT_TYPE_NAME_SAMPLE_SEQUENCE, sampleSequenceValue);
    }

    public String getPortionSequence() {
        return (portionSequence == null ? null : portionSequence.getElementValue());
    }

    public void setPortionSequence(String portionSequenceValue) {
        this.portionSequence = makeShippedBiospecimenElement(ShippedBiospecimenElement.SHIPPED_ELEMENT_TYPE_NAME_PORTION_SEQUENCE, portionSequenceValue);
    }

    public String getPlateId() {
        return (plateId == null ? null : plateId.getElementValue());
    }

    public void setPlateId(String plateIdValue) {
        this.plateId = makeShippedBiospecimenElement(ShippedBiospecimenElement.SHIPPED_ELEMENT_TYPE_NAME_PLATE_ID, plateIdValue);
    }

    public String getBcrCenterId() {
        return bcrCenterId;
    }

    public void setBcrCenterId(final String bcrCenterId) {
        this.bcrCenterId = bcrCenterId;
    }


    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getTssCode() {
        return tssCode;
    }

    public void setTssCode(String tssCode) {
        this.tssCode = tssCode;
    }

    public String getParticipantCode() {
        return participantCode;
    }

    public void setParticipantCode(String participantCode) {
        this.participantCode = participantCode;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Long getShippedBiospecimenId() {
        return shippedBiospecimenId;
    }

    public void setShippedBiospecimenId(Long shippedBiospecimenId) {
        this.shippedBiospecimenId = shippedBiospecimenId;

        // update all elements too with the id
        List<ShippedBiospecimenElement> elements = getShippedBiospecimenElements();
        for (final ShippedBiospecimenElement element : elements) {
            element.setShippedBiospecimenId(shippedBiospecimenId);
        }
    }

    public Boolean isViewable() {
        return isViewable;
    }

    public void setViewable(Boolean viewable) {
        isViewable = viewable;
    }

    public Boolean isRedacted() {
        return isRedacted;
    }

    public void setRedacted(Boolean redacted) {
        isRedacted = redacted;
    }

    public static ShippedBiospecimen parseShippedPortionBarcode(final String shippedPortionBarcode) throws ParseException {
        final Matcher shippedPortionMatcher = SHIPPED_PORTION_BARCODE_PATTERN.matcher(shippedPortionBarcode);
        if (!shippedPortionMatcher.matches()) {
            throw new ParseException("Invalid TCGA shipped portion barcode: " + shippedPortionBarcode, 0);
        }
        final ShippedBiospecimen shippedBiospecimen = new ShippedBiospecimen();
        shippedBiospecimen.setProjectCode(shippedPortionMatcher.group(PROJECT_CODE_INDEX));
        shippedBiospecimen.setTssCode(shippedPortionMatcher.group(TSS_CODE_INDEX));
        shippedBiospecimen.setParticipantCode(shippedPortionMatcher.group(PARTICIPANT_CODE_INDEX));
        shippedBiospecimen.setSampleTypeCode(shippedPortionMatcher.group(SAMPLE_TYPE_CODE_INDEX));
        shippedBiospecimen.setSampleSequence(shippedPortionMatcher.group(SAMPLE_SEQUENCE_INDEX));
        shippedBiospecimen.setPortionSequence(shippedPortionMatcher.group(PORTION_SEQUENCE_INDEX));
        shippedBiospecimen.setPlateId(shippedPortionMatcher.group(PLATE_INDEX));
        shippedBiospecimen.setBcrCenterId(shippedPortionMatcher.group(RECEIVING_CENTER_INDEX));
        shippedBiospecimen.setBarcode(shippedPortionBarcode);
        return shippedBiospecimen;
    }

    private ShippedBiospecimenElement makeShippedBiospecimenElement(final String name, final String value) {
        final ShippedBiospecimenElement element = new ShippedBiospecimenElement();
        element.setShippedBiospecimenId(this.getShippedBiospecimenId());
        element.setElementName(name);
        element.setElementValue(value);
        return element;
    }

    public String getShippedBiospecimenType() {
        return shippedBiospecimenType;
    }

    public void setShippedBiospecimenType(final String shippedBiospecimenType) {
        this.shippedBiospecimenType = shippedBiospecimenType;
    }

    public Integer getShippedBiospecimenTypeId() {
        return shippedBiospecimenTypeId;
    }

    public void setShippedBiospecimenTypeId(final Integer shippedBiospecimenTypeId) {
        this.shippedBiospecimenTypeId = shippedBiospecimenTypeId;
    }

    public Integer getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(final Integer batchNumber) {
        this.batchNumber = batchNumber;
    }
}
