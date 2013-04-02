package gov.nih.nci.ncicb.tcga.dcc.dam.util;

/**
 * Interface for validation of filter request parameters.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface FilterRequestValidator {

    /**
     * From the selected availabilities, get only those that are valid.
     *
     * @param selectedAvailabilities the availabilities selected in the filter
     * @return valid availabilities
     */
    public String[] getValidAvailabilitySelections(String[] selectedAvailabilities);

    /**
     * From the selected batches, get only those that have the expected format for the filter.
     *
     * @param selectedBatches batches selected in the filter
     * @return valid batch selections
     */
    public String[] getValidBatchSelections(String[] selectedBatches);

    /**
     * From selected centers, get those that are valid.
     *
     * @param selectedCenters centers selected in the filter
     * @return valid center ids
     */
    public String[] getValidCenterSelections(String[] selectedCenters);

    /**
     * From selected levels, get those that are valid.
     *
     * @param selectedLevels the levels selected in the filter
     * @return valid levels
     */
    public String[] getValidLevelSelections(String[] selectedLevels);

    /**
     * From selected platform types, get those that are valid (data type ids).
     *
     * @param selectedPlatformTypes platform types selected in the filter
     * @return valid platform types
     */
    public String[] getValidPlatformTypeSelections(String[] selectedPlatformTypes);

    /**
     * From selected protected statuses, get those that are valid.
     *
     * @param selectedProtectedStatuses protected statuses selected in the filter
     * @return valid protected statuses
     */
    public String[] getValidProtectedStatusSelections(String[] selectedProtectedStatuses);

    /**
     * From selected samples, get those that are valid.
     *
     * @param selectedSamples comma-separated list of selected samples in the filter
     * @return valid sample barcodes or wildcard searches
     */
    public String[] getValidSampleSelections(String selectedSamples);

    /**
     * From selected tumor/normal selections, get a list of valid tumor-normal selections.
     *
     * @param selectedTumorNormal tumor/normal selections from the filter
     * @return valid tumor/normal selections
     */
    public String[] getValidTumorNormalSelections(String[] selectedTumorNormal);

    /**
     * From selected platforms, get a list of valid platforms.
     *
     * @param selectedPlatforms platforms selected in the filter
     * @return valid platforms
     */
    public String[] getValidPlatformSelections(String[] selectedPlatforms);
}
