/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;
import org.apache.log4j.Level;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * TODO add class javadoc description
 *
 * @author David Nassau
 * @version $Rev$
 */
public class ProbeLookup {

    private LoaderQueries loaderQueries;
    private Map<Integer, Map<String,Integer>> probes = new TreeMap<Integer, Map<String,Integer>>();
    private Set<Integer> loadingPlatforms = new TreeSet<Integer>();
    private ProcessLoggerI logger;

    public void setLoaderQueries(final LoaderQueries loaderQueries) {
        this.loaderQueries = loaderQueries;
    }

    public void setLogger(final ProcessLoggerI logger) {
        this.logger = logger;
    }

    public boolean hasProbesForPlatform(int platformId) {
        return probes.containsKey(platformId);
    }

    public Integer lookupProbeId(int platformId, String probeName) {
        Integer id = null;
        if (hasProbesForPlatform(platformId)) {
            id = probes.get(platformId).get(probeName);
        }
        return id;
    }

    public int probeCount(int platformId) {
        int count = 0;
        if (hasProbesForPlatform(platformId)) {
            count = probes.get(platformId).size();
        }
        return count;
    }

    //todo  this is only allowing one platform's probes to be downloaded at a time.
    //todo  Instead can do synchronized((new String(platformId)).intern()) which would synch
    //todo  each platform individually and speed up the download
    public synchronized void load(int platformId) throws LoaderQueriesException {
        if (hasProbesForPlatform(platformId)) {
            //already loaded this platform
            return;
        }

        if (loadingPlatforms.contains(platformId)) {
            do {
                //currently loading this platform, wait till it finishes. By waiting,
                //we make sure this thread doesn't jump the gun and process data before the probe
                //map is loaded.
                try {
                    wait();
                } catch (InterruptedException e) {}
            } while (loadingPlatforms.contains(platformId));
            //already loaded this platform, return false
            return;
        }

        //we're the first one in
        loadingPlatforms.add(platformId);
        if (logger != null) logger.logToLogger(Level.INFO, String.format("Downloading probes for platform %d", platformId));
        Map<String,Integer> platformProbes = loaderQueries.downloadProbesForPlatform(platformId);
        if (logger != null) logger.logToLogger(Level.INFO, String.format("Finished downloading probes for platform %d", platformId));
        probes.put(platformId, platformProbes);
        loadingPlatforms.remove(platformId);
        notifyAll(); //wake up threads that are in the above wait
        return;
    }

}
