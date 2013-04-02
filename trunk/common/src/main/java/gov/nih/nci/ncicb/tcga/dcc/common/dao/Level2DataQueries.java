package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.exception.DataException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.JDBCCallback;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for Level2 data queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface Level2DataQueries {
    public List<String> getHybridizationDataGroupNames(final long dataSetId);

    public List<Long> getHybridizationRefIds(final List<Long> dataSetIds);

    public Map<String, Long> getBarcodesForHybrefIds(final List<Long> hybRefIds);

    public Integer getProbeCountForValidChromosome(final int platformId);

    public Integer getProbeCount(final int platformId);

    public Integer getDataGroupsCount(final long dataSetId);

    public void getHybridizationValue(final int platformId,
                                      final boolean useHint,
                                      final List<Long> hybRefIds,
                                      final List<Long> dataSetIds,
                                      final List<String> hybDataGroupNames,
                                      final Map<String, Long> barcodesHybRefIdMap,
                                      final boolean willHaveProbeConstants,
                                      final JDBCCallback callback) throws DataException;

    public List<String> getExperimentSourceFileTypes(final Collection<Long> experimentId);

    public List<Long> getLevel2DataSetIds(final int platformId,
                                          final int centerId,
                                          final String sourceFileType
    );

    public int updateDataSetUseInDAMStatus(final Collection<Long> experimentIdList);
}
