package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of Filter Request Validator.  Used by UsageAdvice.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilterRequestValidatorImpl implements FilterRequestValidator {
    private static final Pattern BATCH_PATTERN = Pattern.compile("Batch \\d+");

    private CenterQueries centerQueries;
    private DataTypeQueries dataTypeQueries;
    private CommonBarcodeAndUUIDValidator barcodeValidator;
    private PlatformQueries platformQueries;

    // caches
    private static List<String> dataTypeIdsCache = null;
    private static List<String> platformNameCache = null;
    private static List<String> centerIdCache = null;

    /**
     * Clears the caches of data type ids, platform ids, and center ids
     */
    public static void clearCaches() {
        dataTypeIdsCache = null;
        platformNameCache = null;
        centerIdCache = null;
    }

    /**
     * Filters out availabilities that aren't one of
     * DataAccessMatrixQueries.AVAILABILITY_AVAILABLE
     * DataAccessMatrixQueries.AVAILABILITY_PENDING
     * DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE
     * DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE
     *
     * @param selectedAvailabilities the availabilities selected in the filter
     * @return array of availabilities that are valid
     */
    @Override
    public String[] getValidAvailabilitySelections(final String[] selectedAvailabilities) {
        final List<String> validAvailabilities = new ArrayList<String>();
        for (final String availability : selectedAvailabilities) {
            if (availability.equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE) ||
                    availability.equals(DataAccessMatrixQueries.AVAILABILITY_PENDING) ||
                    availability.equals(DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE) ||
                    availability.equals(DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE)) {

                validAvailabilities.add(availability);
            }
        }
        return validAvailabilities.toArray(new String[validAvailabilities.size()]);
    }

    /**
     * Filters out batches that aren't either "Unclassified" or "Batch X" where X is an integer.
     *
     * @param selectedBatches batches selected in the filter
     * @return array of valid batches
     */
    @Override
    public String[] getValidBatchSelections(final String[] selectedBatches) {
        final List<String> validBatches = new ArrayList<String>();
        for (final String batch : selectedBatches) {
            if (batch.equals(ConstantValues.UNCLASSIFIED_BATCH)) {
                validBatches.add(batch);
            } else {
                final Matcher batchMatcher = BATCH_PATTERN.matcher(batch);
                if (batchMatcher.matches()) {
                    validBatches.add(batch);
                }
            }
        }
        return validBatches.toArray(new String[validBatches.size()]);
    }

    /**
     * Filters out center selections that aren't valid center IDs.
     *
     * @param selectedCenters centers selected in the filter
     * @return array of valid center IDs
     */
    @Override
    public String[] getValidCenterSelections(final String[] selectedCenters) {
        if (centerIdCache == null) {
            centerIdCache = new ArrayList<String>();
            final List<Center> existingCenters = centerQueries.getCenterList();
            for (final Center existingCenter : existingCenters) {
                centerIdCache.add(String.valueOf(existingCenter.getCenterId()));
            }
        }
        final List<String> validCenters = new ArrayList<String>();
        for (final String center : selectedCenters) {
            if (centerIdCache.contains(center)) {
                validCenters.add(center);
            }
        }
        return validCenters.toArray(new String[validCenters.size()]);
    }

    /**
     * Filters out levels that aren't 1, 2, 3, or C.
     *
     * @param selectedLevels the levels selected in the filter
     * @return array of valid levels
     */
    @Override
    public String[] getValidLevelSelections(final String[] selectedLevels) {
        final List<String> validLevels = new ArrayList<String>();
        for (final String level : selectedLevels) {
            if (level.equals("1") || level.equals("2") || level.equals("3") || level.equals("C")) {
                validLevels.add(level);
            }
        }
        return validLevels.toArray(new String[validLevels.size()]);
    }

    /**
     * Filters out platforms that aren't valid platform IDs.
     *
     * @param selectedPlatformTypes platform types selected in the filter
     * @return array of valid platform ids
     */
    @Override
    public String[] getValidPlatformTypeSelections(final String[] selectedPlatformTypes) {
        if (dataTypeIdsCache == null) {
            dataTypeIdsCache = new ArrayList<String>();
            final Collection<Map<String, Object>> dataTypes = dataTypeQueries.getAllDataTypes();
            for (final Map<String, Object> dataTypeMap : dataTypes) {
                dataTypeIdsCache.add(String.valueOf(dataTypeMap.get("data_type_id")));
            }
        }

        final List<String> validPlatformTypes = new ArrayList<String>();
        for (final String platformType : selectedPlatformTypes) {
            if (dataTypeIdsCache.contains(platformType)) {
                validPlatformTypes.add(platformType);
            }
        }
        return validPlatformTypes.toArray(new String[validPlatformTypes.size()]);
    }

    /**
     * Filters out protected statuses that aren't N or P (which stand for Not Protected and Protected)
     * @param selectedProtectedStatuses protected statuses selected in the filter
     * @return array of valid protected statuses
     */
    @Override
    public String[] getValidProtectedStatusSelections(final String[] selectedProtectedStatuses) {
        final List<String> validProtectedStatuses = new ArrayList<String>();
        for (final String status : selectedProtectedStatuses) {
            if (status.equals("N") || status.equals("P")) {
                validProtectedStatuses.add(status);
            }
        }
        return validProtectedStatuses.toArray(new String[validProtectedStatuses.size()]);
    }

    /**
     * Filters out sample selections that aren't valid sample barcodes or wildcard searches.
     *
     * @param selectedSampleString comma-delimited string of sample barcodes
     * @return array of valid sample barcodes or wildcard search terms
     */
    @Override
    public String[] getValidSampleSelections(final String selectedSampleString) {
        final List<String> validSamples = new ArrayList<String>();
        // split by commas with optional space around them
        final String[] selectedSamples = selectedSampleString.split("\\s*,\\s*");
        for (final String selectedSample : selectedSamples) {
            // valid samples or things ending with "*" ... because if there is a wildcard we could even have "T*"
            if (barcodeValidator.validateSampleBarcodeFormat(selectedSample) || selectedSample.endsWith("*")) {
                validSamples.add(selectedSample);
            }
        }
        return validSamples.toArray(new String[validSamples.size()]);
    }

    /**
     * Filters out tumor/normal selections that aren't one of:
     * DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL
     * DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL
     * DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR
     * DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITHOUT_MATCHED_TUMOR
     *
     * @param selectedTumorNormal tumor/normal selections from the filter
     * @return array of valid tumor/normal selections
     */
    @Override
    public String[] getValidTumorNormalSelections(final String[] selectedTumorNormal) {
        final List<String> validTumorNormals = new ArrayList<String>();
        for (final String tumorNormal : selectedTumorNormal) {
            if (tumorNormal.equals(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL) ||
                    tumorNormal.equals(DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL) ||
                    tumorNormal.equals(DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR) ||
                    tumorNormal.equals(DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL)) {

                validTumorNormals.add(tumorNormal);
            }
        }
        return validTumorNormals.toArray(new String[validTumorNormals.size()]);
    }

    /**
     * Filters out platorms that aren't valid platform names.
     * Note: the DAM filter requires platforms to be specified by name while center and platformType are specified by
     * ID.  It is inconsistent and I don't know why.
     *
     * @param selectedPlatforms platforms selected in the filter
     * @return array of valid platform names
     */
    @Override
    public String[] getValidPlatformSelections(final String[] selectedPlatforms) {
        if (platformNameCache == null) {
            platformNameCache = new ArrayList<String>();
            final List<Platform> allPlatforms = platformQueries.getPlatformList();
            for (final Platform platform : allPlatforms) {
                  platformNameCache.add(String.valueOf(platform.getPlatformName()));
            }
        }
        final List<String> validPlatforms = new ArrayList<String>();
        for (final String platform : selectedPlatforms) {
            if (platformNameCache.contains(platform)) {
                validPlatforms.add(platform);
            }
        }
        return validPlatforms.toArray(new String[validPlatforms.size()]);
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setDataTypeQueries(final DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }

    public void setBarcodeValidator(final CommonBarcodeAndUUIDValidator barcodeValidator) {
        this.barcodeValidator = barcodeValidator;
    }

    public void setPlatformQueries(final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }
}
