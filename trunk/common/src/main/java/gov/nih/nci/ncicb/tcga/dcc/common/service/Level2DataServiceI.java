package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.common.exception.DataException;

import java.io.File;

/**
 * Interface which provides APIs for level2DataService layer
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface Level2DataServiceI {
    public File generateDataFile(final int platformId,
                                 final int centerId,
                                 final String sourceFileType,
                                 final String dataFilename
    ) throws DataException;
}
