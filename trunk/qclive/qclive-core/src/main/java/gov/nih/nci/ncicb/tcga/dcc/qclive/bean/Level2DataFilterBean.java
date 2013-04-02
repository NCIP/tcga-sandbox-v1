package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Class which holds filter details for Level2 Data
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level2DataFilterBean implements Serializable {

    private static final long serialVersionUID = 23553803841290227L;
    private String platformName;
    private String centerDomainName;
    private String diseaseAbbreviation;
    private Set<Long> experimentIdList;

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getCenterDomainName() {
        return centerDomainName;
    }

    public void setCenterDomainName(String centerDomainName) {
        this.centerDomainName = centerDomainName;
    }

    public String getDiseaseAbbreviation() {
        return diseaseAbbreviation;
    }

    public void setDiseaseAbbreviation(String diseaseAbbreviation) {
        this.diseaseAbbreviation = diseaseAbbreviation;
    }

    public Set<Long> getExperimentIdList() {
        return experimentIdList;
    }

    public void setExperimentIdList(Set<Long> experimentIdList) {
        this.experimentIdList = experimentIdList;
    }

    public void addExperimentId(final long experimentId) {
        if (experimentIdList == null) {
            experimentIdList = new HashSet<Long>();
        }
        experimentIdList.add(experimentId);
    }

    public void addExperimentIds(final Set<Long> experimentIds) {
        if (experimentIdList == null) {
            experimentIdList = new HashSet<Long>();
        }
        experimentIdList.addAll(experimentIds);
    }

    public String getExperimentIdsAsString() {
        StringBuilder sb = new StringBuilder()
                .append("[");
        for (Long id : experimentIdList) {
            sb.append(id)
                    .append(",");

        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();

    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof Level2DataFilterBean))
            return false;

        Level2DataFilterBean compareObject = (Level2DataFilterBean) object;

        return this.getCenterDomainName().equals(compareObject.getCenterDomainName()) &&
                this.getDiseaseAbbreviation().equals(compareObject.getDiseaseAbbreviation()) &&
                this.getPlatformName().equals(compareObject.getPlatformName()) &&
                ((this.getExperimentIdList() == null && compareObject.getExperimentIdList() == null) ||
                        ((this.getExperimentIdList() != null && compareObject.getExperimentIdList() != null &&
                                this.getExperimentIdList().equals(compareObject.getExperimentIdList())))
                );
    }

}
