package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.common.exception.DataException;

/**
 * Callback API to return part of retrieved data from the database into caller object.
 * This should be used in Query APIs where large resultset is expected.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */

public interface JDBCCallback {
    public void processData(Object... data) throws DataException;
}
