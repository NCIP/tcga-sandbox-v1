/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.InvalidMetadataException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;


/**
 * Bean which holds meta data details
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaDataBean {
    public static final String DASH = "-";

    private Long shippedBiospecId;
    private String UUID;
    private String projectCode;
    private String tssCode;
    private String participantCode;
    private String sampleCode;
    private String vial;
    private String portionCode;
    private String analyteCode;
    private String plateId;
    private String receivingCenterId;
    private boolean isAliquot = false;
    private boolean isShippedPortion = false;
    
    public boolean isAliquot() {
		return isAliquot;
	}

	public void setAliquot(boolean isAliquot) {
		this.isAliquot = isAliquot;
	}

	public boolean isShippedPortion() {
		return isShippedPortion;
	}

	public void setShippedPortion(boolean isShippedPortion) {
		this.isShippedPortion = isShippedPortion;
	}
    
    public String getUUID() {
        return UUID;
    }   

	public Long getShippedBiospecId() {
		return shippedBiospecId;
	}

	public void setShippedBiospecId(Long shippedBiospecId) {
		this.shippedBiospecId = shippedBiospecId;
	}


	public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getTssCode() {
        return tssCode;
    }

    public void setTssCode(String tssCode) {
        this.tssCode = tssCode;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    public String getVial() {
        return vial;
    }

    public void setVial(String vial) {
        this.vial = vial;
    }

    public String getAnalyteCode() {
        return analyteCode;
    }

    public void setAnalyteCode(String analyteCode) {
        this.analyteCode = analyteCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getParticipantCode() {
        return participantCode;
    }

    public void setParticipantCode(String participantCode) {
        this.participantCode = participantCode;
    }

    public String getPortionCode() {
        return portionCode;
    }

    public void setPortionCode(String portionCode) {
        this.portionCode = portionCode;
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(String plateId) {
        this.plateId = plateId;
    }

    public String getReceivingCenterId() {
        return receivingCenterId;
    }

    public void setReceivingCenterId(String receivingCenterId) {
        this.receivingCenterId = receivingCenterId;
    }

    public String getPatientBuiltBarcode() {
        final StringBuilder builtBarcode = new StringBuilder();
        if(StringUtils.isEmpty(projectCode)){
            throw new InvalidMetadataException("Invalid project code "+projectCode);
        }
        builtBarcode.append(projectCode)
                .append(DASH);
        if(StringUtils.isEmpty(tssCode)){
            throw new InvalidMetadataException("Invalid TSS code "+tssCode);
        }
        builtBarcode.append(tssCode)
                .append(DASH);
        if(StringUtils.isEmpty(participantCode)){
            throw new InvalidMetadataException("Invalid participant code "+participantCode);
        }
        builtBarcode.append(participantCode);
        return builtBarcode.toString();
    }

    public String getSampleBuiltBarcode() {
        final StringBuilder builtBarcode = new StringBuilder(getPatientBuiltBarcode());
        if(StringUtils.isEmpty(sampleCode)){
            throw new InvalidMetadataException("Invalid sample code "+sampleCode);
        }
        builtBarcode.append(DASH)
                .append(sampleCode);
        return builtBarcode.toString();
    }

    public String getPortionBuiltBarcode() {
        final StringBuilder builtBarcode = new StringBuilder(getSampleBuiltBarcode());
        if(StringUtils.isEmpty(vial)){
            throw new InvalidMetadataException("Invalid vial "+vial);
        }
        builtBarcode.append(vial);
        if(StringUtils.isEmpty(portionCode)){
            throw new InvalidMetadataException("Invalid portion code "+portionCode);
        }

        builtBarcode.append(DASH)
                .append(portionCode);

        return builtBarcode.toString();
    }

    public String getAnalyteBuiltBarcode() {
        final StringBuilder builtBarcode = new StringBuilder(getPortionBuiltBarcode());
        if(StringUtils.isEmpty(analyteCode)){
            throw new InvalidMetadataException("Invalid analyte code "+analyteCode);
        }
        builtBarcode.append(analyteCode);
        return builtBarcode.toString();
    }

    public String getAliquotBuiltBarcode() {
        return getBuiltBarcode(getAnalyteBuiltBarcode());
    }


    public String getShippedPortionBuiltBarcode() {
        return getBuiltBarcode(getPortionBuiltBarcode());    }

    
      
    /**
     * Adds properties from another bean to this bean.
     * Only empty properties are added
     * @param beanToCombine
     */    
    public void combineMetadata(final MetaDataBean source) {    	
    	if (source != null) {    		    		
    		
    		if (StringUtils.isEmpty(UUID)){
    			UUID = source.getUUID();
    		}
    		if (StringUtils.isEmpty(projectCode)){
    			projectCode = source.getProjectCode();
    		}
    		if (StringUtils.isEmpty(tssCode)){
    			tssCode = source.getTssCode();
    		}
    		if (StringUtils.isEmpty(participantCode)){
    			participantCode = source.getParticipantCode();
    		}
    		if (StringUtils.isEmpty(sampleCode)){
    			sampleCode = source.getSampleCode();
    		}
    		if (StringUtils.isEmpty(vial)){
    			vial = source.getVial();
    		}
    		if (StringUtils.isEmpty(portionCode)){
    			portionCode = source.getPortionCode();
    		}
    		if (StringUtils.isEmpty(analyteCode)){
    			analyteCode = source.getAnalyteCode();    		
    		}    		
    		if (StringUtils.isEmpty(plateId)){
    			plateId = source.getPlateId();    		
    		}
    		if (StringUtils.isEmpty(receivingCenterId)){
    			receivingCenterId = source.getReceivingCenterId();	
    		}    		    		    		    		    
    	}    	        
    }

    private String getBuiltBarcode(final String prefixBarcode) {

        final StringBuilder builtBarcode = new StringBuilder(prefixBarcode);
       if(StringUtils.isEmpty(plateId)){
            throw new InvalidMetadataException("Invalid plate id "+plateId);
        }
        builtBarcode.append(DASH)
                .append(plateId);

        if(StringUtils.isEmpty(receivingCenterId)){
            throw new InvalidMetadataException("Invalid receiving center id "+receivingCenterId);
        }
        builtBarcode.append(DASH)
                .append(receivingCenterId);

        return builtBarcode.toString();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((UUID == null) ? 0 : UUID.hashCode());
		result = prime * result
				+ ((analyteCode == null) ? 0 : analyteCode.hashCode());
		result = prime * result + (isAliquot ? 1231 : 1237);
		result = prime * result + (isShippedPortion ? 1231 : 1237);
		result = prime * result
				+ ((participantCode == null) ? 0 : participantCode.hashCode());
		result = prime * result + ((plateId == null) ? 0 : plateId.hashCode());
		result = prime * result
				+ ((portionCode == null) ? 0 : portionCode.hashCode());
		result = prime * result
				+ ((projectCode == null) ? 0 : projectCode.hashCode());
		result = prime
				* result
				+ ((receivingCenterId == null) ? 0 : receivingCenterId
						.hashCode());
		result = prime * result
				+ ((sampleCode == null) ? 0 : sampleCode.hashCode());
		result = prime * result + ((tssCode == null) ? 0 : tssCode.hashCode());
		result = prime * result + ((vial == null) ? 0 : vial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDataBean other = (MetaDataBean) obj;
		if (UUID == null) {
			if (other.UUID != null)
				return false;
		} else if (!UUID.equals(other.UUID))
			return false;
		if (analyteCode == null) {
			if (other.analyteCode != null)
				return false;
		} else if (!analyteCode.equals(other.analyteCode))
			return false;
		if (isAliquot != other.isAliquot)
			return false;
		if (isShippedPortion != other.isShippedPortion)
			return false;
		if (participantCode == null) {
			if (other.participantCode != null)
				return false;
		} else if (!participantCode.equals(other.participantCode))
			return false;
		if (plateId == null) {
			if (other.plateId != null)
				return false;
		} else if (!plateId.equals(other.plateId))
			return false;
		if (portionCode == null) {
			if (other.portionCode != null)
				return false;
		} else if (!portionCode.equals(other.portionCode))
			return false;
		if (projectCode == null) {
			if (other.projectCode != null)
				return false;
		} else if (!projectCode.equals(other.projectCode))
			return false;
		if (receivingCenterId == null) {
			if (other.receivingCenterId != null)
				return false;
		} else if (!receivingCenterId.equals(other.receivingCenterId))
			return false;
		if (sampleCode == null) {
			if (other.sampleCode != null)
				return false;
		} else if (!sampleCode.equals(other.sampleCode))
			return false;
		if (tssCode == null) {
			if (other.tssCode != null)
				return false;
		} else if (!tssCode.equals(other.tssCode))
			return false;
		if (vial == null) {
			if (other.vial != null)
				return false;
		} else if (!vial.equals(other.vial))
			return false;
		return true;
	}            
}

