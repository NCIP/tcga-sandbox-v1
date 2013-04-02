package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataMatrixFileBean;

import java.util.List;
import java.util.Map;

/**
 * Interface for DataMatrix Files database queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface DataMatrixQueries {

    public Map<String,List<DataMatrixFileBean>> getMultipleAliquotDataMatrixFiles();
}
