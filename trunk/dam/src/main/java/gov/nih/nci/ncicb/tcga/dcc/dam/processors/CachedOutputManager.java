/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import gov.nih.nci.ncicb.tcga.dcc.common.service.Level2DataService;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Cell;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * @author David Nassau
 *         <p/>
 *         Assists in use of cached output files representing whole columns
 */
public class CachedOutputManager {

    private static String cachefileDirectory;
    private static Map<String, Integer> columnSizes = new HashMap<String, Integer>();

    //not static so we can call it from spring fwork

    public void setCachefileDirectory(final String s) {
        cachefileDirectory = s;
    }

    /**
     * count the available cells for each level 2 or 3 column and store the sizes in a map
     *
     * @param model the model
     */
    public static void registerColumnSizes(final DAMModel model) {
        List<Header> ptHeaders = model.getHeadersForCategory(Header.HeaderCategory.PlatformType);
        for (final Header ptHeader : ptHeaders) {
            for (final Header centerHeader : ptHeader.getChildHeaders()) {
                for (final Header levelHeader : centerHeader.getChildHeaders()) {
                    if ( !DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE.equals(ptHeader.getName())) {
                        String level = levelHeader.getName();
                        if (DataFile.LEVEL_2.equals(level) || DataFile.LEVEL_3.equals(level)) {
                            countLevel23Datasets(model, levelHeader);
                        }
                    }
                }
            }
        }
    }

    //count available datasets and store the counts indexed by disease, platformtype, center, platform

    private static void countLevel23Datasets(final DAMModel model, final Header levelHeader) {
        List<Cell> cells = model.getCellsForHeader(levelHeader);
        for (final Cell cell : cells) {
            if (DataAccessMatrixQueries.AVAILABILITY_AVAILABLE.equals(cell.getAvailability())) {
                for (final DataSet ds : cell.getDatasets()) {
                    if (DataAccessMatrixQueries.AVAILABILITY_AVAILABLE.equals(ds.getAvailability())) {
                        String key = model.getDiseaseType() + "." + "level" + levelHeader.getName() + "." + ds.getPlatformTypeId() + "." + ds.getCenterId() + "." + ds.getPlatformId();
                        Integer count = columnSizes.get(key);
                        if (count == null) {
                            count = 0;
                        }
                        columnSizes.put(key, ++count);
                    }
                }
            }
        }
    }

    /*
     * Where a cache file should exist, substitute the literal path to that file and set the flag indicating
     * that it is a permanent file (therefore not to be deleted after adding to the archive).
     * But it does not check for the actual existence of the cache file at this point: it will do so
     * in the FilePackager.
     */

    public static void addCachedFileNames(final String diseaseType, final List<DataFile> files) {
        for (final DataFile df : files) {
            if (df instanceof DataFileLevelTwoThree) {
                processLevel2Or3File((DataFileLevelTwoThree) df, diseaseType);
            }
        }
    }


    static String makeDateString(final Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    private static void processLevel2Or3File(final DataFileLevelTwoThree df23, final String diseaseType) {
        //does it have a whole column of available cells?
        String levelToken = "level" + df23.getLevel();  // (df23 instanceof DataFileLevelTwo ? "level2" : "level3");
        String key = diseaseType + "." + levelToken + "." + df23.getPlatformTypeId() + "." + df23.getCenterId() + "." + df23.getPlatformId();
        Integer columnSize = columnSizes.get(key);
        if (columnSize != null && df23.getSamples().size() == columnSize) {
            //include disease and dataset ids (all of them!) in the cache file name
            StringBuilder datasetIdString = new StringBuilder();
            for (final int datasetId : df23.getDataSetsDP()) {
                datasetIdString.append(datasetId).append(".");
            }
            if (df23.getLevel().equals(DataFile.LEVEL_2)) {
                df23.setPath(cachefileDirectory + Level2DataService.getFileName(diseaseType,
                        df23.getPlatformName(),
                        df23.getCenterName(),
                        df23.getSourceFileType()));
            } else {
                df23.setPath(cachefileDirectory + diseaseType + "." + levelToken + "." + datasetIdString.toString() + df23.getFileName());
            }

            df23.setPermanentFile(true);
        }
    }

    //for unit test

    static void clear() {
        columnSizes = new HashMap<String, Integer>();
        cachefileDirectory = null;
    }
}
