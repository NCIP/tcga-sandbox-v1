/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TissueSourceSiteQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple classifier that uses the data set list to figure out the tumor/normal status of the data sets.
 * <p/>
 * All potentially matching DataSets must be passed it at once to classify.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TumorNormalClassifier implements TumorNormalClassifierI {
    private static final Map<String, Boolean> SAMPLE_TYPE_IS_TUMOR_MAP = new HashMap<String, Boolean>();
    private static final List<String> CONTROL_TSS_CODES = new ArrayList<String>();

    private SampleTypeQueries sampleTypeQueries;
    private TissueSourceSiteQueries tissueSourceSiteQueries;


    /**
     * Classify the DataSets given as TN, NT, T, or N.  Only available data sets will be classified and
     * used to classify other available data sets.
     *
     * @param dataSets the data sets to classify
     */
    public void classifyTumorNormal(final List<DataSet> dataSets) {
        //key is just patient barcode
        // value is a list of sample types (01, 10, etc) from the available barcodes for this key
        Map<String, List<String>> availableSamples = new HashMap<String, List<String>>();
        // now iterate through all data sets and add the available ones to the map
        for(final DataSet ds : dataSets) {
            // parse the sample barcode
            String sampleType = ds.getSample().substring( 13, 15 );
            // construct the key
            String key = getKey( ds );
            // add this sample type to the map's list, possibly creating a new list
            List<String> sampleTypes = availableSamples.get( key );
            if(sampleTypes == null) {
                sampleTypes = new ArrayList<String>();
                availableSamples.put( key, sampleTypes );
            }
            sampleTypes.add( sampleType );
        }
        // then, go through again and  classify each available set using the map
        for(final DataSet ds : dataSets) {
            String tssCode = ds.getSample().substring(5, 7);
            if (isControl(tssCode)) {
                ds.setTumorNormal(DataAccessMatrixQueries.TUMORNORMAL_CELL_LINE_CONTROL);
            } else {

                // get sample type by looking at last 2 digits of sample
                String sampleType = ds.getSample().substring( 13, 15 );
                // construct the key the same way as above
                String key = getKey( ds );
                // set initial value as if no match
                String tumorNormalClass = null;
                if (isTumorType(sampleType)) {
                    tumorNormalClass = DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL;
                } else {
                    tumorNormalClass = DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL;
                }
                // check if there is a matching tumor/normal data set available for this key
                List<String> types = availableSamples.get( key );
                for(final String type : types) {
                    if (isTumorType(sampleType) && !isTumorType(type)) {
                        // is a tumor and has a match
                        tumorNormalClass = DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL;
                    } else if (!isTumorType(sampleType) && isTumorType(type)) {
                        // is a normal with matched tumor
                        tumorNormalClass = DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR;
                    }
                }
                ds.setTumorNormal( tumorNormalClass );
            }
        }
    }

    /**
     * @param ds the dataset for which to get a key
     * @return the key for this dataset, used for getting and setting values in the available map
     */
    private static String getKey( final DataSet ds ) {
        return ds.getSample().substring( 0, 12 );
    }

    public void setSampleTypeQueries(final SampleTypeQueries sampleTypeQueries) {
        this.sampleTypeQueries = sampleTypeQueries;
    }

    private static void initLookups(final SampleTypeQueries sampleTypeQueries, final TissueSourceSiteQueries tissueSourceSiteQueries) {
        List<SampleType> sampleTypes = sampleTypeQueries.getAllSampleTypes();
        for (final SampleType sampleType : sampleTypes) {
            SAMPLE_TYPE_IS_TUMOR_MAP.put(sampleType.getSampleTypeCode(), sampleType.getIsTumor());
        }

        CONTROL_TSS_CODES.addAll(tissueSourceSiteQueries.getControlTssCodes());
    }


    private boolean isTumorType(final String sampleType) {
        if (SAMPLE_TYPE_IS_TUMOR_MAP == null || SAMPLE_TYPE_IS_TUMOR_MAP.size() == 0) {
            initLookups(sampleTypeQueries, tissueSourceSiteQueries);
        }
        if (SAMPLE_TYPE_IS_TUMOR_MAP.containsKey(sampleType)) {
            return SAMPLE_TYPE_IS_TUMOR_MAP.get(sampleType);
        } else {
            throw new IllegalArgumentException("Sample type " + sampleType + " is unknown");
        }
    }

    private boolean isControl(final String tssCode) {
        if (CONTROL_TSS_CODES.size() == 0) {
            initLookups(sampleTypeQueries, tissueSourceSiteQueries);
        }
        return CONTROL_TSS_CODES.contains(tssCode);
    }

    public static void clearSampleTypeMap() {
        SAMPLE_TYPE_IS_TUMOR_MAP.clear();
    }

    public void setTissueSourceSiteQueries(final TissueSourceSiteQueries tissueSourceSiteQueries) {
        this.tissueSourceSiteQueries = tissueSourceSiteQueries;
    }
}
