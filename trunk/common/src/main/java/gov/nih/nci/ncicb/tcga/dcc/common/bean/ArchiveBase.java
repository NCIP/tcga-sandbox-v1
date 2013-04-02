package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import java.io.Serializable;

/**
 * Archive Base class which can be serialized
 *
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveBase implements Serializable {
    private static final long serialVersionUID = 1234567890L;
    private String experimentType;
    private String domainName = null;
    private String depositLocation;

    public ArchiveBase() {
    }

    public ArchiveBase(ArchiveBase archiveBase) {
        this.experimentType = archiveBase.getExperimentType();
        this.domainName = archiveBase.getDomainName();
        this.depositLocation = archiveBase.getDepositLocation();
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }

    public void setExperimentType(final String experimentType) {
        this.experimentType = experimentType;
    }

    public String getExperimentType() {
        return experimentType;
    }

    public String getDepositLocation() {
        return depositLocation;
    }

    public void setDepositLocation(String depositLocation) {
        this.depositLocation = depositLocation;
    }
}
