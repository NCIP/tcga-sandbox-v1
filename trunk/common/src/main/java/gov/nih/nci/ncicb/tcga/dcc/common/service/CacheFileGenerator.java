package gov.nih.nci.ncicb.tcga.dcc.common.service;

import java.io.IOException;
import java.util.List;

/**
 * Interface which provides APIs to generate cache files
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface CacheFileGenerator {
    public List<String> generateAllCacheFiles(final String diseaseAbbreviation) throws IOException;
}
