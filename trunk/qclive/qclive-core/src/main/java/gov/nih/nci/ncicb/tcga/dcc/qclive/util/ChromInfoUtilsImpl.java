/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.ChromInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ChromInfoQueries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ChromInfoUtils interface that checks chromosome name, length and build against actual values in the DB.
 *
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ChromInfoUtilsImpl implements ChromInfoUtils {

    /**
     * Map of chromosome name -> Map <genome build, chromosome length>
     */
    final private Map<String, Map<String, Integer>> chromosomeNameMap = new HashMap<String, Map<String, Integer>>();

    private ChromInfoQueries chromInfoQueries;

    /**
     * Initialize the chromosomeNameMap with values from the database.
     * Chromosome name and genome build are converted to lower case to be able to do case-insensitive search.
     */
    public void init() {

        final List<ChromInfo> chromInfoList = getChromInfoQueries().getAllChromInfo();

        Map<String, Integer> buildMap;
        String lowerCaseChromosomeName;

        for (final ChromInfo chromInfo : chromInfoList) {

            lowerCaseChromosomeName = chromInfo.getChromName().toLowerCase();
            buildMap = getChromosomeNameMap().get(lowerCaseChromosomeName);

            if(buildMap == null) {
                buildMap = new HashMap<String, Integer>();
            }

            buildMap.put(chromInfo.getBuild().toLowerCase(), chromInfo.getChromSize());

            getChromosomeNameMap().put(lowerCaseChromosomeName, buildMap);
        }
    }

    @Override
    public int getChromSizeForGenomeBuild(final String chromosomeName, final String genomeBuildName) throws UnknownChromException {

        Integer result = null;

        final Map<String, Integer> buildMap = getChromosomeNameMap().get(chromosomeName.toLowerCase());

        if(buildMap != null) {

            final String lowerCaseGenomeBuildName = genomeBuildName.toLowerCase();

            if(buildMap.containsKey(lowerCaseGenomeBuildName)) {
                result = buildMap.get(lowerCaseGenomeBuildName);

            } else {
                throw new UnknownChromException("Invalid genome build: " + genomeBuildName);
            }

        } else {
            throw new UnknownChromException("Invalid chromosome: " + chromosomeName);
        }

        return result;
    }

    @Override
    public boolean isValidChromCoord(final String chromosomeName, final int coordinate, final String genomeBuildName) {

        boolean result = false;

        try {
            result = 1 <= coordinate && coordinate <= getChromSizeForGenomeBuild(chromosomeName, genomeBuildName);

        } catch (final UnknownChromException e) {
            result = true;
        }

        return result;
    }

    @Override
    public boolean isValidChromValue(final String chromosomeName) {
        return getChromosomeNameMap().get(chromosomeName.toLowerCase()) != null;
    }

    public ChromInfoQueries getChromInfoQueries() {
        return chromInfoQueries;
    }

    public void setChromInfoQueries(final ChromInfoQueries chromInfoQueries) {
        this.chromInfoQueries = chromInfoQueries;
    }

    private Map<String, Map<String, Integer>> getChromosomeNameMap() {
        return chromosomeNameMap;
    }
}
