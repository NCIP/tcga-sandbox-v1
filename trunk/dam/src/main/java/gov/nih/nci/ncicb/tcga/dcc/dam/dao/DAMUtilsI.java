package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for DAMUtil APIs
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface DAMUtilsI {
    public List<String> getLevel3AllowedDataTypes(String dataTypeGroup);

    public List<Disease> getDiseases();

    public Disease getDisease(final String abbrev);

    public List<Disease> getActiveDiseases();

    public Collection<Map<String, Object>> getAllTumors();

    public Collection<Map<String, Object>> getAllCenters();

    public Collection<Map<String, Object>> getAllPlatforms();

    public Platform getPlatformWithAlias(final String platformAlias);

    public Platform getPlatformById(final Integer platformId);

    public Collection<Map<String, Object>> getAllDataTypes();

    /**
     * Method to group the {@link DataSet} passed in by disease. This is for the case
     * of controls where control datasets can span multiple disease schemas
     * @param dataSets
     * @return Map of disease to datasets for disease
     */
    public Map<String, List<DataSet>> groupDataSetsByDisease(List<DataSet> dataSets);
}
