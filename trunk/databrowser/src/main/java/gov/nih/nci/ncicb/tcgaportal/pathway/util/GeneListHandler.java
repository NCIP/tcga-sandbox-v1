package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Nov 7, 2008
 * Time: 4:02:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneListHandler {

    private IPathwayQueries pathwayQueries;

    public void setPathwayQueries(IPathwayQueries pathwayQueries) {
        this.pathwayQueries = pathwayQueries;
    }

    public List<Gene> gatherGenes(String pathway) throws IPathwayQueries.PathwayQueriesException, SQLException {
        List<Gene> genes = pathwayQueries.getGeneAnomaliesForPathway(pathway);
        for (Gene gene : genes) {
            gene.setAgents(pathwayQueries.getGeneAgents(gene.getGeneID()));
        }
        return genes;
    }

    public void sortGenes(List<Gene> genes, String sortby) {
        if (sortby == null || sortby.length() == 0) {
            sortby = Gene.SORTBYGENE;
        }

        Comparator comp;
        if (sortby.equals(Gene.SORTBYGENE)) {
            comp = new Comparator<Gene>() {
                public int compare(Gene g1, Gene g2) {
                    return g1.getEntrezSymbol().compareTo(g2.getEntrezSymbol());
                }
            };
        } else if (sortby.equals(Gene.ANOMALYTYPE_MUTATION)) {
            comp = new Comparator<Gene>() {
                public int compare(Gene g1, Gene g2) {
                    if (g1.getMutationRatio() > g2.getMutationRatio()) {
                        return -1;
                    } else if (g1.getMutationRatio() < g2.getMutationRatio()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
        } else if (sortby.equals(Gene.ANOMALYTYPE_AMPLIFICATION)) {
            comp = new Comparator<Gene>() {
                public int compare(Gene g1, Gene g2) {
                    if (g1.getAmplificationRatio() > g2.getAmplificationRatio()) {
                        return -1;
                    } else if (g1.getAmplificationRatio() < g2.getAmplificationRatio()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
        } else if (sortby.equals(Gene.ANOMALYTYPE_DELETION)) {
            comp = new Comparator<Gene>() {
                public int compare(Gene g1, Gene g2) {
                    if (g1.getDeletionRatio() > g2.getDeletionRatio()) {
                        return -1;
                    } else if (g1.getDeletionRatio() < g2.getDeletionRatio()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
        } else if (sortby.equals(Gene.ANOMALYTYPE_AGENT)) {
            comp = new Comparator<Gene>() {
                public int compare(Gene g1, Gene g2) {
                    if (g1.isAffectedByAgent() && !g2.isAffectedByAgent()) {
                        return -1;
                    } else if (!g1.isAffectedByAgent() && g2.isAffectedByAgent()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
        } else {
            throw new IllegalArgumentException("sort order " + sortby + " is not allowed");
        }

        Collections.sort(genes, comp);
    }

    public void tagGenesForWhichToShowAgents(List<Gene> genes, String showAgentsForGene) {
        if (showAgentsForGene != null) {
            for (Gene gene : genes) {
                if (showAgentsForGene.contains(Integer.toString(gene.getGeneID()))) {
                    gene.setShowAgents(true);
                }
            }
        }
    }
}
