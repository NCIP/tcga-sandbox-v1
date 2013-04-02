package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.List;

/**
 * TODO: Class description
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface DataSetReducerI {
    List<DataSet> reduceLevelTwoThree( List<DataSet> dsList,
                                              int level ) throws DataAccessMatrixQueries.DAMQueriesException;

    List<DataSet> reduceLevelOne(
                                                      List<DataSet> dsList ) throws DataAccessMatrixQueries.DAMQueriesException;
}
