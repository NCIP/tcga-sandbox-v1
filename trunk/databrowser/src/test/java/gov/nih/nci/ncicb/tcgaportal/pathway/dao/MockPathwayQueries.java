package gov.nih.nci.ncicb.tcgaportal.pathway.dao;

import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.GeneAgent;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Pathway;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Nov 10, 2008
 * Time: 11:52:45 AM
 * To change this template use File | Settings | File Templates.
 * Last updated by: $Author$
 *
 * @version $Rev$
 */
public class MockPathwayQueries implements IPathwayQueries {

    public static final String PORTAL_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    public List<Gene> getGeneAnomaliesForPathway(String pathwayName) throws PathwayQueriesException {
        List<Gene> ret = new ArrayList<Gene>();
        try {
            //rdr = new FileReader(thisFolder + "genelist.txt");
            Reader r = new FileReader(PORTAL_FOLDER + "genelist.txt");
            StreamTokenizer st = new StreamTokenizer(r);
            st.whitespaceChars((int) '\t', (int) '\t');
            st.whitespaceChars((int) '\n', (int) '\n');
            st.wordChars((int) '/', (int) '/');
            st.wordChars((int) '_', (int) '_');
            int i = st.nextToken();
            while (i != StreamTokenizer.TT_EOF) {
                Gene g = new Gene();
                g.setEntrezSymbol(st.sval);
                st.nextToken();
                g.setGeneID((int) st.nval);
                st.nextToken();
                g.setBioCartaId(st.sval);
                st.nextToken();
                g.setMutationCasesDetected((int) st.nval);
                st.nextToken();
                g.setMutationCasesProbed((int) st.nval);
                st.nextToken();
                g.setAmplificationCasesDetected((int) st.nval);
                st.nextToken();
                g.setAmplificationCasesProbed((int) st.nval);
                st.nextToken();
                g.setDeletionCasesDetected((int) st.nval);
                st.nextToken();
                g.setDeletionCasesProbed((int) st.nval);

                ret.add(g);
                i = st.nextToken();
            }
        } catch (IOException e) {
            throw new PathwayQueriesException(e);
        }
        return ret;
    }

    public List<Pathway> getAllPathwaysWithTheirAnomalies() throws PathwayQueriesException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String lookupPathwayFileName(String pathwayName) throws PathwayQueriesException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String lookupPathwayDisplayName(String pathwayName) throws PathwayQueriesException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<GeneAgent> getGeneAgents(int geneID) throws SQLException, PathwayQueriesException {
        List<GeneAgent> ret = new ArrayList<GeneAgent>();
        try {
            File f = new File(PORTAL_FOLDER + "geneagents_" + geneID + ".txt");
            if (f.exists()) {
                Reader r = new FileReader(f);
                StreamTokenizer st = new StreamTokenizer(r);
                st.whitespaceChars((int) '\t', (int) '\t');
                st.whitespaceChars((int) '\n', (int) '\n');
                st.wordChars((int) '_', (int) '_');
                int i = st.nextToken();
                while (i != StreamTokenizer.TT_EOF) {
                    GeneAgent ga = new GeneAgent();
                    ga.setAgentName(st.sval);
                    st.nextToken();
                    ga.setSingleConceptCode(st.sval);

                    ret.add(ga);
                    i = st.nextToken();
                }
            }
        } catch (IOException e) {
            throw new PathwayQueriesException(e);
        }
        return ret;
    }

    public boolean getPathwayHasAgent(int pathwayID) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map<String, Double> lookupAnomalyThresholds() throws PathwayQueriesException {
        Map<String, Double> ret = new HashMap<String, Double>();
        ret.put(Gene.ANOMALYTYPE_DELETION, 0.1);
        ret.put(Gene.ANOMALYTYPE_MUTATION, 0.05);
        ret.put(Gene.ANOMALYTYPE_AMPLIFICATION, 0.1);
        return ret;
    }
}
