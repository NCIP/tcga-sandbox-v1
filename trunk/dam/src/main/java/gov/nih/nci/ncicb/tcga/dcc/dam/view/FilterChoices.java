/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;

import java.util.*;

/**
 * Author: David Nassau
 */
//singleton - returns lists of information used for building filter UI.
//only one instance needed for all users
public class FilterChoices {

    //one instance per disease type
    private static final Map<String, FilterChoices> INSTANCES = new HashMap<String, FilterChoices>();

    /**
     * Removes all cached instances of FilterChoices
     */
    public static void clearInstances() {
        INSTANCES.clear();
    }

    /**
     * This is only here for ease of testing...
     * @param disease the disease name
     * @return true if we have an instance of FilterChoices for this disease, false if not
     */
    public static boolean hasInstanceFor(final String disease) {
        return INSTANCES.containsKey(disease);
    }

    /**
     * Gets the FilterChoices object for this model
     *
     * @param damModel the model
     * @return a FilterChoices object
     */
    public static FilterChoices getInstance(final DAMModel damModel) {
        String diseaseType = damModel.getDiseaseType();
        if (INSTANCES.get(diseaseType) == null) {
            FilterChoices instance = new FilterChoices(damModel);
            INSTANCES.put(diseaseType, instance);
        }
        return INSTANCES.get(diseaseType);
    }

    private DAMModel damModel;
    private List<String> batchList, platformTypeList, centerList, levelList,
            sampleCollectionCenterList, sampleTypeList;

    private FilterChoices(DAMModel damModel) {
        this.damModel = damModel;
        buildLists();
    }

    //special comparator that sorts lists based on their lookup names
    class FilterListComparator implements Comparator<String> {

        Header.HeaderCategory cat;

        public FilterListComparator(Header.HeaderCategory cat) {
            this.cat = cat;
        }

        public int compare(String s1, String s2) {
            s1 = DataAccessMatrixJSPUtil.lookupHeaderText(cat, s1);
            s2 = DataAccessMatrixJSPUtil.lookupHeaderText(cat, s2);
            return s1.compareTo(s2);
        }
    }

    private void buildLists() {
        platformTypeList = new ArrayList<String>();
        for (Header ptHeader : damModel.getHeadersForCategory(Header.HeaderCategory.PlatformType)) {
            platformTypeList.add(ptHeader.getName());
        }
        centerList = new ArrayList<String>();
        for (Header centerHeader : damModel.getHeadersForCategory(Header.HeaderCategory.Center)) {
            String center = centerHeader.getName();
            //don't add clinical "centers" to list. reason: it looks weird
            if (!center.equals(DataAccessMatrixQueries.CLINICAL_XML_CENTER)
                    && !center.equals(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER) &&
                    !centerList.contains(centerHeader.getName())) {
                centerList.add(centerHeader.getName());
            }
        }
        levelList = new ArrayList<String>();
        for (Header levelHeader : damModel.getHeadersForCategory(Header.HeaderCategory.Level)) {
            if (!levelList.contains(levelHeader.getName())) {
                levelList.add(levelHeader.getName());
            }
        }
        batchList = new ArrayList<String>();
        for (int i = 0, max = damModel.getTotalBatches(); i < max; i++) {
            batchList.add(damModel.getBatchHeader(i).getName());
        }
        scanSampleIds();
        //sort them by their lookup values, not the actual id value
        Collections.sort(platformTypeList, new FilterListComparator(Header.HeaderCategory.PlatformType));
        Collections.sort(centerList, new FilterListComparator(Header.HeaderCategory.Center));
        Collections.sort(levelList, new FilterListComparator(Header.HeaderCategory.Level));
    }

    private void scanSampleIds() {
        sampleCollectionCenterList = new ArrayList<String>();
        sampleTypeList = new ArrayList<String>();
        for (Header sampleHeader : damModel.getHeadersForCategory(Header.HeaderCategory.Sample)) {
            String sampleId = sampleHeader.getName();
            String collectionCenter = sampleId.substring(5, 7);
            String sampleType = sampleId.substring(13, 15);
            if (!sampleCollectionCenterList.contains(collectionCenter)) {
                sampleCollectionCenterList.add(collectionCenter);
            }
            if (!sampleTypeList.contains(sampleType)) {
                sampleTypeList.add(sampleType);
            }
        }
        Collections.sort(sampleCollectionCenterList);
        Collections.sort(sampleTypeList);
    }

    public List<String> getAllBatches() {
        return batchList;
    }

    public List<String> getAllPlatformTypes() {
        return platformTypeList;
    }

    public List<String> getAllCenters() {
        return centerList;
    }

    public List<String> getAllLevels() {
        return levelList;
    }

    public List<String> getSampleCollectionCenterOptions() {
        return sampleCollectionCenterList;
    }

    public List<String> getSampleTypeOptions() {
        return sampleTypeList;
    }
}
