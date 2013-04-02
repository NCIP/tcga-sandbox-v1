package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;

import java.util.List;
import java.util.Set;

/**
 * Interface for data set sorter and gap filler.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface DataSorterAndGapFillerI {
    /**
     * Convenience method to sort and fill gaps.
     *
     * @param datasets datasets to sort and fill in
     * @param submittedSamples list of all submitted samples we expect to be represented in the data sets
     */
    void sortAndFillGaps(List<DataSet> datasets, Set<String> submittedSamples);

    /**
     * Sorts the datasets by: platformType, center, platform, level, batch, then sample
     *
     * @param datasets datasets to sort
     */
    void sort(List<DataSet> datasets);

    /**
     * Fills in gaps in the datasets using the submittedSamples list.  That is, for every sample in the submitted sample list,
     * it puts in a "not available" dataset for every center/platform/level combination in the current data sets.
     *
     * @param datasets the dataset list
     * @param submittedSamples list of samples submitted
     */
    void fillGaps(List<DataSet> datasets, Set<String> submittedSamples);
}
