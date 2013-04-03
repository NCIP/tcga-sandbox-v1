package gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Nov 12, 2007
 * Time: 10:52:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class PathwayAndGenes {
    private String pathwayName, displayName;
    private String relativePathwayFileName;
    private List<Gene> genes;
    private Map<String, Double> patientThresholds;

    public void setPathwayName(String value) {
        pathwayName = value;
    }

    public String getPathwayName() {
        return pathwayName;
    }

    public void setDisplayName(String value) {
        displayName = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setRelativePathwayFileName(String value) {
        relativePathwayFileName = value;
    }

    public String getRelativePathwayFileName() {
        return relativePathwayFileName;
    }

    public void setGenes(List<Gene> value) {
        genes = value;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setPatientThresholds(Map<String, Double> patientThresholds) {
        this.patientThresholds = patientThresholds;
    }

    //break it down like this so we can easily retrieve them in the JSP
    public int getMutationThresholdPercent() {
        return percent(patientThresholds.get(Gene.ANOMALYTYPE_MUTATION));
    }
    public int getAmplificationThresholdPercent() {
        return percent(patientThresholds.get(Gene.ANOMALYTYPE_AMPLIFICATION));
    }
    public int getDeletionThresholdPercent() {
        return percent(patientThresholds.get(Gene.ANOMALYTYPE_DELETION));
    }
    
    private int percent(double n) {
        return (int)Math.round(n * 100.);
    }

}
