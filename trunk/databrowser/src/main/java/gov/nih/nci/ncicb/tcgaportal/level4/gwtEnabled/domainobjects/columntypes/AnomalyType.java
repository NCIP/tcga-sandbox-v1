/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

/**
 * A ColumnType representing an AnomalyColumn, not a Correlation
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

/**
 * Abstract class for any column type that represents an anomaly, as opposed to a correlation.
 * All anomaly types have platform type, center, platform etc.
 */
public abstract class AnomalyType extends ColumnType {

    public enum GeneticElementType {
        Gene() {
            public String toString() {
                return "gene";
            }
        }, miRNA() {
            public String toString() {
                return "miRNA";
            }
        }, MethylationProbe() {
            public String toString() {
                return "methylation probe";
            }
        }
    }

    private int platformType;   //data_type.data_type_id
    private int anomalyTypeId; // L4_anomaly_type.anomaly_type_id
    private int dataSetId;      // L4_anomaly_data_set.anomaly_data_set_id

    //could be just looked up when needed, but more convenient to carry them here, and there aren't many instances
    private String displayPlatformType;
    private String displayCenter;
    private String displayPlatform;

    protected float frequency;

    private GeneticElementType geneticElementType;

    public AnomalyType() {
        this(GeneticElementType.Gene); //default
    }

    public AnomalyType(GeneticElementType geType) {
        this.geneticElementType = geType;
        frequency = getDefaultRatioThreshold();
    }

    public int getPlatformType() {
        return platformType;
    }

    public void setPlatformType(int platformType) {
        this.platformType = platformType;
    }

    public String getDisplayCenter() {
        return displayCenter;
    }

    public void setDisplayCenter(String displayCenter) {
        this.displayCenter = displayCenter;
    }

    public String getDisplayPlatformType() {
        return displayPlatformType;
    }

    public void setDisplayPlatformType(String displayPlatformType) {
        this.displayPlatformType = displayPlatformType;
    }

    public String getDisplayPlatform() {
        return displayPlatform;
    }

    public void setDisplayPlatform(String displayPlatform) {
        this.displayPlatform = displayPlatform;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AnomalyType)) {
            return false;
        }
        AnomalyType at = (AnomalyType) o;
        return at.getDataSetId() == getDataSetId(); //todo  is this really all you need?
    }

    public int getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(int dataSetId) {
        this.dataSetId = dataSetId;
    }

    public int getAnomalyTypeId() {
        return anomalyTypeId;
    }

    public void setAnomalyTypeId(int anomalyTypeId) {
        this.anomalyTypeId = anomalyTypeId;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        if (frequency < 0 || frequency > 1) {
            throw new IllegalArgumentException("Frequency must be between 0% and 100%");
        }
        this.frequency = frequency;
    }

    public GeneticElementType getGeneticElementType() {
        return geneticElementType;
    }

    public void setGeneticElementType(GeneticElementType geneticElementType) {
        this.geneticElementType = geneticElementType;
    }

    protected abstract float getDefaultRatioThreshold();

    public Object cloneColumn() {
        AnomalyType column = (AnomalyType) super.cloneColumn();
        column.setGeneticElementType(getGeneticElementType());
        column.setPlatformType(getPlatformType());
        column.setDisplayPlatformType(getDisplayPlatformType());
        column.setDisplayCenter(getDisplayCenter());
        column.setDisplayPlatform(getDisplayPlatform());
        column.setDataSetId(getDataSetId());
        column.setAnomalyTypeId(getAnomalyTypeId());
        column.setFrequency(getFrequency());
        return column;
    }
}
