package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Oct 31, 2008
 * Time: 11:41:57 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IPathwaySVGFetcher {

    String fetchPathwaySVG(String pathwayName, String bcgene, String anomaly) throws IOException, IPathwayQueries.PathwayQueriesException;

}
