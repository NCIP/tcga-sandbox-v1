package gov.nih.nci.ncicb.tcga.dcc.dam.view;

/**
 * Helper APIs for DAM web layer
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface DAMHelperI {
    public void cacheTumorCenterPlatformInfo();

    public void refreshTumorCenterPlatformInfoCache();
}
