package gov.nih.nci.ncicb.tcgaportal.pathway.dao.jdbc;

import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.GeneAgent;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Pathway;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author D. Nassau
 */

public class PathwayQueriesJDBCImpl extends JdbcDaoSupport implements IPathwayQueries {

    //values as they appear in the database - different from those in Gene class which are used in the application
    public static final String DBANOMALY_MUTATION = "mutation";
    public static final String DBANOMALY_AMPLIFICATION = "amplified";
    public static final String DBANOMALY_DELETION = "deleted";

    protected Map<String, Double> patientThresholds;
    protected Map<String, Integer> casesProbedPerAnomalyType;
    protected List<Pathway> pathwayList;
    private PreparedStatement pstmt = null;   //todo  no reason to make this an instance variable, use local variables instead
    private PreparedStatement geneAgentStmt = null;
    private PreparedStatement getPathwayHasAgentStmt = null;
    private Logger log = Logger.getLogger("JDBC");

    public PathwayQueriesJDBCImpl() {
    }

    //using pretty raw jdbc now - should use some of spring's classes for cleaner code

    public String lookupPathwayFileName(final String pathwayName) throws PathwayQueriesException {
        String ret = null;
        ResultSet resultSet = null;
        Connection conn = getConnection();
        try {
            final String query = "select svg_file_name from pathway where svg_identifier = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pathwayName);
            resultSet = pstmt.executeQuery();
            if (!resultSet.next()) throw new PathwayQueriesException("No pathway row found for " + pathwayName);
            ret = resultSet.getString("svg_file_name");
        } catch (SQLException e) {
            log.debug("SQLException in lookupPathwayFileName", e);
            throw new PathwayQueriesException(e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    log.debug("Resultset closed.");
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                    log.debug("Prepared statement closed.");
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.debug("SQLException while closing resultSet or prepared statement", e);
            }
        }
        return ret;
    }

    public String lookupPathwayDisplayName(final String pathwayName) throws PathwayQueriesException {
        String ret = null;
        ResultSet resultSet = null;
        Connection conn = getConnection();
        try {
            final String query = "select display_name from pathway where svg_identifier = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pathwayName);
            resultSet = pstmt.executeQuery();

            if (!resultSet.next()) throw new PathwayQueriesException("No pathway row found for " + pathwayName);

            ret = resultSet.getString("display_name");
        } catch (SQLException e) {
            log.error("SQLException in lookupPathwayDisplayName",e);
            throw new PathwayQueriesException(e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    log.debug("Resultset closed.");
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                    log.debug("Prepared statement closed.");
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.debug("SQLException while closing resultSet or prepared statement", e);
            }
        }
        return ret;
    }

    public List<GeneAgent> getGeneAgents(final int geneID) throws SQLException, PathwayQueriesException {
        final List<GeneAgent> agents = new ArrayList<GeneAgent>();
        final HashSet<String> drugSeen = new HashSet<String>();
        ResultSet rs = null;
        Connection conn = getConnection();
        try {
            if (geneAgentStmt == null || geneAgentStmt.getConnection().isClosed()) {
                final String sql = "SELECT d.name, d.comments, dcc.concept_code, dcc.comments " +
                        "FROM gene_drug gd, drug d, drug_concept_code dcc " +
                        "WHERE gd.drug_id = d.drug_id " +
                        "AND d.drug_id = dcc.drug_id " +
                        "AND gd.gene_id = ? " +
                        "ORDER BY d.name, dcc.concept_code";
                geneAgentStmt = conn.prepareStatement(sql);
            }

            geneAgentStmt.setInt(1, geneID);
            rs = geneAgentStmt.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    final String drugName = rs.getString(1);
                    final GeneAgent ga;
                    // only want the first concept code per drug.
                    if (!drugSeen.contains(drugName)) {
                        ga = new GeneAgent();
                        ga.setAgentName(drugName);
                        ga.setAgentComments(rs.getString(2));
                        ga.setSingleConceptCode(rs.getString(3));
                        ga.setSingleConceptCodeComments(rs.getString(4));
                        drugSeen.add(drugName);
                        agents.add(ga);
                    }
                }
            }
        } finally {
            try {
                if (geneAgentStmt != null) {
                    geneAgentStmt.close();
                    geneAgentStmt = null;
                }
                if (rs != null) {
                    rs.close();
                    log.debug("Resultset closed.");
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.debug("SQLException while closing resultSet or prepared statement", e);
            }
        }

//        testWriteGeneAgents(agents, geneID);

        return agents;
    }

//    void testWriteGeneAgents(List<GeneAgent> agents, int geneID) {
//        if (agents.size() > 0) {
//            Writer writer = null;
//            try {
//                writer = new FileWriter("C:\\geneagents_" + geneID + ".txt");
//                for (GeneAgent a : agents) {
//                    writer.write(a.getAgentName());             writer.write("\t");
//                    writer.write(a.getSingleConceptCode());     writer.write("\n");
//                }
//                writer.flush();
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public boolean getPathwayHasAgent(final int pathwayID) throws SQLException {
        boolean hasAgent = false;
        ResultSet rs = null;
        Connection conn = getConnection();
        try {
            if (getPathwayHasAgentStmt == null) {
                final String sql = "SELECT count(gd.gene_id) " +
                        "FROM gene_drug gd, biocarta_gene bg, biocarta_gene_pathway bgp " +
                        "WHERE gd.gene_id = bg.gene_id " +
                        "AND bg.biocarta_gene_id = bgp.biocarta_gene_id " +
                        "AND bgp.pathway_id = ?";
                getPathwayHasAgentStmt = conn.prepareStatement(sql);
            }
            getPathwayHasAgentStmt.setInt(1, pathwayID);
            rs = getPathwayHasAgentStmt.executeQuery();
            if (rs != null) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    hasAgent = true;
                }
            }
        } finally {
            try {
                if (getPathwayHasAgentStmt != null) {
                    getPathwayHasAgentStmt.close();
                    getPathwayHasAgentStmt = null;
                }
                if (rs != null) {
                    rs.close();
                    log.debug("Resultset closed.");
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.debug("SQLException while closing resultSet or prepared statement", e);
            }
        }
        return hasAgent;
    }

    //TEMPORARY - since we know the data, we know that there's just one number for cases probed per anomaly
    //type for Deletion, Amplification and Mutation (and we're not using Expression currently).  Should this
    //change, or should we start using Expression, then we will have to start storing rows in
    //summary_by_gene table for genes with 0 cases detected.  Currently, there are no rows when
    //cases detected = 0, so we have to look up cases probed this way
    private void getCasesProbedPerAnomalyType() throws PathwayQueriesException {
        if (casesProbedPerAnomalyType == null) {
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
            String query = "select distinct a.anomaly, sg.cases_probed " +
                    "from summary_by_gene sg, anomaly_type a " +
                    "where sg.anomaly_type_id = a.anomaly_type_id " +
                    "and a.anomaly in ('MUTATION', 'AMPLIFIED', 'DELETED')";
            List<Map<String,Object>> rows = template.queryForList(query);
            if (rows.size() != 3) {
                //protects us against any unexpected change in the database: fail utterly
                throw new PathwayQueriesException("Could not get cases-probed: wrong number of rows");
            }
            casesProbedPerAnomalyType = new HashMap<String, Integer>();
            for (Map<String,Object> row : rows) {
                String anomaly = (String)row.get("anomaly");
                anomaly = translateAnomaly(anomaly);
                BigDecimal bdCasesProbed = (BigDecimal)row.get("cases_probed");
                casesProbedPerAnomalyType.put(anomaly, bdCasesProbed.intValue());
            }
        }
    }

    public List<Gene> getGeneAnomaliesForPathway(final String pathwayName) throws PathwayQueriesException {
        final List<Gene> ret = new ArrayList<Gene>();
        ResultSet resultSet = null;
        Connection conn = getConnection();
        try {
            getCasesProbedPerAnomalyType();

            //anomaly query gets number probed and number samples for each gene and anomaly type for this pathway
            final StringBuffer query = new StringBuffer();
            query.append("select c.gene_id, c.BIOCARTA_SYMBOL, g.ENTREZ_SYMBOL, f.ANOMALY, e.CASES_PROBED, e.CASES_DETECTED ");
            query.append("from pathway a, biocarta_gene_pathway b, biocarta_gene c, summary_by_gene e, anomaly_type f, gene g ");
            query.append("where a.SVG_IDENTIFIER = ? ");
            query.append("and a.PATHWAY_ID = b.PATHWAY_ID ");
            query.append("and b.BIOCARTA_GENE_ID = c.BIOCARTA_GENE_ID ");
            query.append("and c.GENE_ID = e.GENE_ID(+) ");
            query.append("and c.GENE_ID = g.GENE_ID ");
            query.append("and e.ANOMALY_TYPE_ID = f.ANOMALY_TYPE_ID(+) ");
            query.append("order by c.BIOCARTA_SYMBOL, f.Anomaly");
            log.debug(">>getGeneAnomaliesForPathway: query==" + query.toString());
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, pathwayName);
            resultSet = pstmt.executeQuery();
            Gene gene = null;
            String lastSymbol = null;
            while (resultSet.next()) {
                final int geneID = resultSet.getInt("GENE_ID");
                final String bcSymbol = resultSet.getString("BIOCARTA_SYMBOL");
                final String entrezSymbol = resultSet.getString("ENTREZ_SYMBOL");

                String anomaly = null;
                if (!bcSymbol.equals(lastSymbol)) {
                    gene = new Gene();
                    gene.setGeneID(geneID);
                    gene.setBioCartaId(bcSymbol);
                    gene.setEntrezSymbol(entrezSymbol);

                    //TEMP - we'll get the cases_probed number from a map, that way we can also produce this
                    //number where there are 0 cases detected. Currently these are missing rows.
                    //Ultimately we should have a row in the summary_by_gene even when there are no cases detected
                    gene.setMutationCasesProbed(casesProbedPerAnomalyType.get(Gene.ANOMALYTYPE_MUTATION));
                    gene.setAmplificationCasesProbed(casesProbedPerAnomalyType.get(Gene.ANOMALYTYPE_AMPLIFICATION));
                    gene.setDeletionCasesProbed(casesProbedPerAnomalyType.get(Gene.ANOMALYTYPE_DELETION));

                    ret.add(gene);
                }
                if (resultSet.getString("ANOMALY") != null) {
                    int casesDetected = resultSet.getInt("CASES_DETECTED");

                    anomaly = resultSet.getString("ANOMALY");
                    anomaly = translateAnomaly(anomaly);

                    if (anomaly != null) {
                        if (anomaly.equals(Gene.ANOMALYTYPE_MUTATION)) {
                            gene.setMutationCasesDetected(casesDetected);
                        } else if (anomaly.equals(Gene.ANOMALYTYPE_AMPLIFICATION)) {
                            gene.setAmplificationCasesDetected(casesDetected);
                        } else if (anomaly.equals(Gene.ANOMALYTYPE_DELETION)) {
                            gene.setDeletionCasesDetected(casesDetected);
                        }
                    }
                }
                lastSymbol = bcSymbol;
            }
        } catch (SQLException e) {
            log.debug("SQLException in getGeneAnomaliesForPathway", e);
            throw new PathwayQueriesException(e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    log.debug("Resultset closed.");
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                    log.debug("Prepared statement closed.");
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.debug("SQLException while closing resultSet or prepared statement", e);
            }
        }

//        testWriteGeneList(ret);

        return ret;
    }

//    void testWriteGeneList(List<Gene> genes) {
//        Writer writer=null;
//        try {
//            writer = new FileWriter("c:\\genelist.txt");
//            for (Gene gene : genes) {
//                writer.write(gene.getBioCartaId());                 writer.write("\t");
//                writer.write(Integer.toString(gene.getGeneID()));   writer.write("\t");
//                writer.write(gene.getEntrezSymbol());               writer.write("\t");
//                String s = gene.getMutationRatioString();
//                writer.write(s.substring(0, s.indexOf('/')));       writer.write("\t");
//                writer.write(s.substring(s.indexOf('/')+ 1));       writer.write("\t");
//                s = gene.getAmplificationRatioString();
//                writer.write(s.substring(0, s.indexOf('/')));       writer.write("\t");
//                writer.write(s.substring(s.indexOf('/')+ 1));       writer.write("\t");
//                s = gene.getDeletionRatioString();                  writer.write("\t");
//                writer.write(s.substring(0, s.indexOf('/')));       writer.write("\t");
//                writer.write(s.substring(s.indexOf('/')+ 1));       writer.write("\n");
//            }
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //translate from the db coding to the one we use in the app
    private String translateAnomaly(String dbanomaly) {
        String ret = null;
        if (dbanomaly == null) return null;
        dbanomaly = dbanomaly.toLowerCase();
        if (dbanomaly.equals(DBANOMALY_MUTATION)) {
            ret = Gene.ANOMALYTYPE_MUTATION;
        } else if (dbanomaly.equals(DBANOMALY_AMPLIFICATION)) {
            ret = Gene.ANOMALYTYPE_AMPLIFICATION;
        } else if (dbanomaly.equals(DBANOMALY_DELETION)) {
            ret = Gene.ANOMALYTYPE_DELETION;
        }
        return ret;
    }

    //just do once per session
    public Map<String,Double> lookupAnomalyThresholds() throws PathwayQueriesException {
        if (patientThresholds != null) {
            return patientThresholds;
        }

        patientThresholds = new HashMap<String,Double>();
        ResultSet resultSet = null;
        Connection conn = getConnection();
        try {
            final String query = "select anomaly, patient_threshold from anomaly_type";
            pstmt = conn.prepareStatement(query);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String anomalyType = resultSet.getString("ANOMALY");
                anomalyType = translateAnomaly(anomalyType);
                if (anomalyType != null) {
                    double patientThreshold = resultSet.getDouble("PATIENT_THRESHOLD");
                    patientThresholds.put(anomalyType, patientThreshold);
                }
            }
        } catch (SQLException e) {
            log.debug("SQLException in lookupAnomalyThreashold", e);
            throw new PathwayQueriesException(e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    log.debug("Resultset closed.");
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                    log.debug("Prepared statement closed.");
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.debug("SQLException while closing resultSet or prepared statement", e);
            }
        }
        return patientThresholds;
    }

    public List<Pathway> getAllPathwaysWithTheirAnomalies
            () throws PathwayQueriesException {
        if (pathwayList != null) return pathwayList; //already created the list this session, just reuse it

        pathwayList = new ArrayList<Pathway>();
        ResultSet resultSet = null;
        Connection conn = getConnection();
        try {
            lookupAnomalyThresholds();

            final StringBuffer query = new StringBuffer();
            //query gets max of ratio of cases detected/probed per pathway
            query.append("select p.pathway_id, p.SVG_IDENTIFIER, p.DISPLAY_NAME, a.ANOMALY, max(sg.CASES_DETECTED/sg.CASES_PROBED) as maxratio ");
            query.append("from pathway p, biocarta_gene_pathway bp, biocarta_gene bg, summary_by_gene sg, anomaly_type a ");
            query.append("where p.pathway_id = bp.pathway_id ");
            query.append("and bp.biocarta_gene_id = bg.biocarta_gene_id ");
            query.append("and bg.gene_id = sg.gene_id(+) ");
            query.append("and sg.anomaly_type_id = a.anomaly_type_id(+) ");
            query.append("group by p.pathway_id, p.SVG_IDENTIFIER, p.DISPLAY_NAME, a.ANOMALY ");
            query.append("order by p.DISPLAY_NAME, a.ANOMALY");
            log.debug(">>getAllPathwaysWithTheirAnomalies: query==" + query);
            pstmt = conn.prepareStatement(query.toString());
            resultSet = pstmt.executeQuery(query.toString());

            Pathway pathway = null;
            String lastPathwayName = null;
            while (resultSet.next()) {
                final int pathwayID = resultSet.getInt("PATHWAY_ID");
                final String pathwayName = resultSet.getString("SVG_IDENTIFIER").intern();
                final String displayName = resultSet.getString("DISPLAY_NAME");
                String anomaly = resultSet.getString("ANOMALY");
                final Double ratio = resultSet.getDouble("MAXRATIO");

                anomaly = translateAnomaly(anomaly);
                final Double threshold = (anomaly != null ? patientThresholds.get(anomaly) : 0);

                if (!pathwayName.equals(lastPathwayName)) {  //ok to use != because strings are intern'd
                    pathway = new Pathway();
                    pathway.setPathwayID(pathwayID);
                    pathway.setPathwayName(pathwayName);
                    pathway.setDisplayName(displayName);
                    pathwayList.add(pathway);
                }

                if (anomaly != null && ratio >= threshold) {
                    if (anomaly.equals(Gene.ANOMALYTYPE_MUTATION)) {
                        pathway.setMutated(true);
                    } else if (anomaly.equals(Gene.ANOMALYTYPE_AMPLIFICATION)) {
                        pathway.setAmplified(true);
                    } else if (anomaly.equals(Gene.ANOMALYTYPE_DELETION)) {
                        pathway.setDeleted(true);
                    }
                }
                lastPathwayName = pathwayName;
            }
            for (Pathway p : pathwayList) {
                p.setAffectedByAgents(getPathwayHasAgent(p.getPathwayID()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            pathwayList = null; //so we don't cache it
            throw new PathwayQueriesException(e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.debug("Could not close resultset or pstmt", e);
            }
        }
        return pathwayList;
    }
}
