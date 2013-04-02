package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;

import java.util.List;

/**
 * TODO: Class description
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface TumorNormalClassifierI {
    /**
     * Classify the DataSets given as TN, NT, T, or N.  Only available data sets will be classified and
     * used to classify other available data sets.
     *
     * @param dataSets the data sets to classify
     */
    void classifyTumorNormal(List<DataSet> dataSets);
}
