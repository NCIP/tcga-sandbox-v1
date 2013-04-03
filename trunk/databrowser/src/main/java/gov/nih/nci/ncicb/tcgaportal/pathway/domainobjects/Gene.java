package gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Nov 12, 2007
 * Time: 10:32:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gene {
    //these match the parameters passed to the site to retrieve biocarta diagrams
    public static final String ANOMALYTYPE_MUTATION = "mutation";
    public static final String ANOMALYTYPE_AMPLIFICATION = "amplification";
    public static final String ANOMALYTYPE_DELETION = "deletion";
    public static final String ANOMALYTYPE_AGENT = "agent"; //it's not really an anomaly but we treat it like one for highlighting purposes
    public static final String ANOMALYTYPE_ANY = "all";

    public static final String SORTBYGENE = "gene";

    private class AnomalyNumbers {
        public int casesProbed;
        public int casesDetected;
    }

    private String bcId;
    private int geneID;
    private String entrezSymbol;
    private List<GeneAgent> agents;
    private String agentList = null;
    private boolean showAgents;

    private AnomalyNumbers mutationNumbers = new AnomalyNumbers();
    private AnomalyNumbers amplificationNumbers = new AnomalyNumbers();
    private AnomalyNumbers deletionNumbers = new AnomalyNumbers();

    public void setGeneID(int geneID) {
        this.geneID = geneID;
    }

    public int getGeneID() {
        return geneID;
    }

    public void setBioCartaId(String value) {
        bcId = value;
    }

    public String getBioCartaId() {
        return bcId;
    }

    public String getEntrezSymbol() {
        return entrezSymbol;
    }

    public void setEntrezSymbol(String entrezSymbol) {
        this.entrezSymbol = entrezSymbol;
    }

    public void setMutationCasesProbed(int casesProbed) {
        mutationNumbers.casesProbed = casesProbed;
    }
    public void setAmplificationCasesProbed(int casesProbed) {
        amplificationNumbers.casesProbed = casesProbed;
    }
    public void setDeletionCasesProbed(int casesProbed) {
        deletionNumbers.casesProbed = casesProbed;
    }

    public void setMutationCasesDetected(int casesProbed) {
        mutationNumbers.casesDetected = casesProbed;
    }
    public void setAmplificationCasesDetected(int casesProbed) {
        amplificationNumbers.casesDetected = casesProbed;
    }
    public void setDeletionCasesDetected(int casesProbed) {
        deletionNumbers.casesDetected = casesProbed;
    }

    public double getMutationRatio() {
        return ratio(mutationNumbers);
    }
    public double getAmplificationRatio() {
        return ratio(amplificationNumbers);
    }
    public double getDeletionRatio() {
        return ratio(deletionNumbers);
    }

    //used to sort table - first desc then asc
    public double getMutationInverseRatio() {
        return 1 - getMutationRatio();
    }
    public double getAmplificationInverseRatio() {
        return 1 - getAmplificationRatio();
    }
    public double getDeletionInverseRatio() {
        return 1 - getDeletionRatio();
    }

    public String getMutationPercentageString() {
        return strPercent(mutationNumbers);
    }
    public String getAmplificationPercentageString() {
        return strPercent(amplificationNumbers);
    }
    public String getDeletionPercentageString() {
        return strPercent(deletionNumbers);
    }

    public String getMutationRatioString() {
        return strRatio(mutationNumbers);
    }
    public String getAmplificationRatioString() {
        return strRatio(amplificationNumbers);
    }
    public String getDeletionRatioString() {
        return strRatio(deletionNumbers);
    }

    private String strRatio(AnomalyNumbers an) {
        return Integer.toString(an.casesDetected) + "/" + Integer.toString(an.casesProbed);
    }

    private double ratio(AnomalyNumbers an) {
        double ret = 0.;
        if (an.casesProbed != 0) {
            ret = (double)an.casesDetected/(double)an.casesProbed;
        }
        return ret;
    }

    private String strPercent(AnomalyNumbers an) {
        double ratio = ratio(an);
        int pct = (int)Math.round(ratio * 100.);
        return Integer.toString(pct) + "%";
    }

    public String getAgentList() {
        StringBuffer temp = new StringBuffer();
        if (agentList == null) {
            for (GeneAgent agent : agents) {
                temp.append("<a href=\"");
                temp.append(GeneAgent.conceptCodeURLBase);
                temp.append(agent.getSingleConceptCode());
                temp.append("\"><u><font color=blue>");
                temp.append(agent.getAgentName());
                temp.append("</u></font></a>, ");
            }
            // kinda lazy, but workable way to remove the last comma
            if (temp.toString().endsWith(", ")) {
                temp.append("endend");
            }
            agentList = temp.toString();
            agentList = agentList.replaceFirst(", endend", "");
        }
        return agentList;
    }

    public List getAgents() {
        return agents;
    }

    public void setAgents(List<GeneAgent> agents) {
        this.agents = agents;
    }

    public boolean isAffectedByAgent() {
        return (agents != null && !agents.isEmpty());
    }
    //used for table sorting
    public boolean isNotAffectedByAgent() {
        return !isAffectedByAgent();
    }

    public boolean isShowAgents() {
        return showAgents;
    }

    public void setShowAgents(boolean showAgents) {
        this.showAgents = showAgents;
    }
}
