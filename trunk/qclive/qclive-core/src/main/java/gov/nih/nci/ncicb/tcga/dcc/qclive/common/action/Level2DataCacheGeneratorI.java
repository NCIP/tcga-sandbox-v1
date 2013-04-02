package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;


import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Level2DataFilterBean;

/**
 * Interface which provides APIs for generating level2 data cache files
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface Level2DataCacheGeneratorI {
    public void generateCacheFiles(Level2DataFilterBean level2DataFilterBean) throws Exception;
}
