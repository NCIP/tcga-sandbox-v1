package gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Nov 19, 2007
 * Time: 12:12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pathway {

    private int pathwayID;
    private String pathwayName, displayName;
    private boolean mutated;
    private boolean amplified;
    private boolean deleted;
    private boolean overexpressed_affy;
    private boolean underexpressed_affy;
    private boolean affectedByAgents;

    public int getPathwayID() {
        return pathwayID;
    }

    public void setPathwayID(int pathwayID) {
        this.pathwayID = pathwayID;
    }

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

    public void setMutated(boolean value) {
        mutated = value;
    }

    public boolean isMutated() {
        return mutated;
    }

    public void setAmplified(boolean value) {
        amplified = value;
    }

    public boolean isAmplified() {
        return amplified;
    }

    public void setDeleted(boolean value) {
        deleted = value;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setOverexpressed_Affy(boolean value) {
        overexpressed_affy = value;
    }

    public boolean isOverexpressed_Affy() {
        return overexpressed_affy;
    }

    public void setUnderexpressed_Affy(boolean value) {
        underexpressed_affy = value;
    }

    public boolean isUnderexpressed_Affy() {
        return underexpressed_affy;
    }


    public boolean isAffectedByAgents() {
        return affectedByAgents;
    }

    public void setAffectedByAgents(boolean affectedByAgents) {
        this.affectedByAgents = affectedByAgents;
    }



    //true if it has ANY anomaly or agent
    public boolean isAnomalous() { //todo: rename method since it also include agents
        return (this.isMutated() || this.isAmplified() || this.isDeleted()
                || this.isOverexpressed_Affy() || this.isUnderexpressed_Affy()
                || this.isAffectedByAgents());
    }

    //we need a set of inverse "is not" functions so table sorting will work correctly
    public boolean isNotMutated() {
        return !mutated;
    }

    public boolean isNotAmplified() {
        return !amplified;
    }

    public boolean isNotDeleted() {
        return !deleted;
    }

    public boolean isNotOverexpressed_Affy() {
        return !overexpressed_affy;
    }

    public boolean isNotUnderexpressed_Affy() {
        return !underexpressed_affy;
    }

    public boolean isNotAffectedByAgents() {
        return !affectedByAgents;
    }

    public boolean isNotAnomalous() {
        return !isAnomalous();
    }
}
