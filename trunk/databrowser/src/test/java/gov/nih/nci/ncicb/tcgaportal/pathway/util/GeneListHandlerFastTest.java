package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.dao.MockPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Nov 10, 2008
 * Time: 1:11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneListHandlerFastTest extends TestCase {

    GeneListHandler glh;

    public void setUp() {
        glh = new GeneListHandler();
        glh.setPathwayQueries(new MockPathwayQueries());
    }

    public void testSortGenes() throws IPathwayQueries.PathwayQueriesException, SQLException {
        List<Gene> genes = glh.gatherGenes("foo");
        assertTrue("gene list has no elements", genes.size() != 0);

        glh.sortGenes(genes, Gene.ANOMALYTYPE_MUTATION);
        assertTrue("sort by mutation not correct", isSortedByMutation(genes));

        glh.sortGenes(genes, Gene.ANOMALYTYPE_AMPLIFICATION);
        assertTrue("sort by amplification not correct", isSortedByAmplification(genes));

        glh.sortGenes(genes, Gene.ANOMALYTYPE_DELETION);
        assertTrue("sort by deletion not correct", isSortedByDeletion(genes));

        glh.sortGenes(genes, Gene.SORTBYGENE);
        assertTrue("sort by gene name not correct", isSortedByGeneName(genes));

        glh.sortGenes(genes, Gene.ANOMALYTYPE_AGENT);
        assertTrue("sort by agent not correct", isSortedByAgent(genes));
    }

    public void testTagToShowAgents() throws IPathwayQueries.PathwayQueriesException, SQLException {
        List<Gene> genes = glh.gatherGenes("foo");
        assertTrue("gene list has no elements", genes.size() != 0);

        StringBuilder sb = new StringBuilder();
        for (Gene gene : genes) {
            if (gene.isAffectedByAgent()) {
                sb.append(gene.getGeneID()).append(",");
            }
        }

        glh.tagGenesForWhichToShowAgents(genes, sb.toString());

        for (Gene gene : genes) {
            if (gene.isShowAgents()) {
                assertTrue("tagged wrong gene", gene.isAffectedByAgent());
            }
        }
    }

    boolean isSortedByAgent(List<Gene> genes) {
        boolean prev = true;
        for (Gene gene : genes) {
            if (gene.isAffectedByAgent() && !prev) {
                return false;
            }
            prev = gene.isAffectedByAgent();
        }
        return true;
    }

    boolean isSortedByGeneName(List<Gene> genes) {
        String prev = "";
        for (Gene gene : genes) {
            if (gene.getEntrezSymbol().compareTo(prev) < 0) {
                return false;
            }
            prev = gene.getEntrezSymbol();
        }
        return true;
    }

    boolean isSortedByMutation(List<Gene> genes) {
        double prev = 2;
        for (Gene gene : genes) {
            if (gene.getMutationRatio() > prev) {
                return false;
            }
            prev = gene.getMutationRatio();
        }
        return true;
    }

    boolean isSortedByAmplification(List<Gene> genes) {
        double prev = 2;
        for (Gene gene : genes) {
            if (gene.getAmplificationRatio() > prev) {
                return false;
            }
            prev = gene.getAmplificationRatio();
        }
        return true;
    }

    boolean isSortedByDeletion(List<Gene> genes) {
        double prev = 2;
        for (Gene gene : genes) {
            if (gene.getDeletionRatio() > prev) {
                return false;
            }
            prev = gene.getDeletionRatio();
        }
        return true;
    }
    
}
