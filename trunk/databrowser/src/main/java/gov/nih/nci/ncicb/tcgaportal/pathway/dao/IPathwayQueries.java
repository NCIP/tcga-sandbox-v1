package gov.nih.nci.ncicb.tcgaportal.pathway.dao;

import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.GeneAgent;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Pathway;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author D. Nassau
 */
public interface IPathwayQueries {

    List<Gene> getGeneAnomaliesForPathway(String pathwayName) throws PathwayQueriesException;

    List<Pathway> getAllPathwaysWithTheirAnomalies() throws PathwayQueriesException;

    String lookupPathwayFileName(String pathwayName) throws PathwayQueriesException;

    String lookupPathwayDisplayName(String pathwayName) throws PathwayQueriesException;

    List<GeneAgent> getGeneAgents (int geneID) throws SQLException, PathwayQueriesException;

    boolean getPathwayHasAgent (int pathwayID) throws SQLException;

    Map<String,Double> lookupAnomalyThresholds() throws PathwayQueriesException;

    /**
 * Created by IntelliJ IDEA.
     * User: nassaud
     * Date: Nov 26, 2007
     * Time: 11:54:30 AM
     * To change this template use File | Settings | File Templates.
     */
    public static class PathwayQueriesException extends Exception {
        public PathwayQueriesException(String message) {
            super(message);
        }

        public PathwayQueriesException(Throwable cause) {
            super(cause);
        }

        public PathwayQueriesException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}
