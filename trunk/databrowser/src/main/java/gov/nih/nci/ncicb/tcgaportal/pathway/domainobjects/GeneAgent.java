package gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects;

/**
 *
 *  An agent, or drug, and a concept code to link out from.
 *
 * @user: HickeyE
 * @version: $id$
 */
public class GeneAgent {
    private String agentName;
    private String singleConceptCode; // there may be multiple concept codes.  cannot link to two at once, so just grab thr first one.
    private String agentComments;
    private String singleConceptCodeComments;
    public static final String conceptCodeURLBase = "http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code=";


    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getSingleConceptCode() {
        return singleConceptCode;
    }

    public void setSingleConceptCode(String singleConceptCode) {
        this.singleConceptCode = singleConceptCode;
    }

    public String getAgentComments() {
        return agentComments;
    }

    public void setAgentComments(String agentComments) {
        this.agentComments = agentComments;
    }

    public String getSingleConceptCodeComments() {
        return singleConceptCodeComments;
    }

    public void setSingleConceptCodeComments(String singleConceptCodeComments) {
        this.singleConceptCodeComments = singleConceptCodeComments;
    }
}
