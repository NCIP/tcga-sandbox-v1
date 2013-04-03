package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import gov.nih.nci.ncicb.tcgaportal.level4.util.Locations;
import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.domainobjects.Gene;
import org.apache.batik.transcoder.TranscoderException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface for PathwayDiagramHandler functionality.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public interface PathwayDiagramHandler {
    int WAITTODELETE_MILLIS = 30000;
    String ANOMALY_COLOR = Gene.ANOMALYTYPE_MUTATION; //not the actual anomaly, but the color we use to highlight all anomalies

    void setTempFileLocation(String tempFileLocation);
    void setLocationsUtility(Locations locationsUtil);

    void setSvgFetcher(IPathwaySVGFetcher svgFetcher);

    String fetchPathwayImage(String pathwayName, List<Gene> genes, String anomaliesSelectedByUser, Map<String,Double> thresholds) throws TranscoderException, IOException, IPathwayQueries.PathwayQueriesException;

    String fetchPathwayImage(String pathwayName, List<String> bcgenes) throws TranscoderException, IOException, IPathwayQueries.PathwayQueriesException;

    void planDeletionOfImage(String filename);
}
