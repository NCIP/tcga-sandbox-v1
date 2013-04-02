package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.ChromInfo;
import java.util.List;


/**
 * Interface for ChromInfoQueries
 *
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ChromInfoQueries {
    List<ChromInfo> getAllChromInfo();
}
