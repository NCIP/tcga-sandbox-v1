/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.RowCounter;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.StringTokenizerThatCanReturnNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 11, 2009
 * Time: 1:33:08 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Loads hybridization_value
 */
public class ValuesLoader {

    DataFile datafile;
    Map<String, Long> hybGroupIds;
    Map<String, Long> hybrefIdsByName;
    long datasetId;
    int platformId;
    long experimentId;
    LoaderQueries loaderQueries;
    RowCounter rowCounter;
    int batchSize = 1000;

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void insertValues() throws LoaderException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new FileReader( datafile.getFile() ) );
            //first line - parse out hybref
            String line = reader.readLine();
            StringTokenizerThatCanReturnNull st = new StringTokenizerThatCanReturnNull( line, "\t" );
            st.nextToken(); //skip first token
            //skip the null tokens for constants
            int constantCount = datafile.getConstantNames().size();
            for(int i = 0; i < constantCount; i++) {
                st.nextToken();
            }
            //gather up all the hybrefs into a list (for many centers, there will just be one per file)
            List<String> hybrefNames = new ArrayList<String>();
            List<Long> hybrefIds = gatherHybRefs( st, hybrefNames );
            int hybrefCount = hybrefIds.size();
            // now, insert dataset-hybref link
            for (int i=0; i<hybrefIds.size(); i++) {
                Long hybrefId = hybrefIds.get(i);
                if (hybrefId != null && !loaderQueries.hybRefDatasetExists(hybrefId, datasetId)) {
                    loaderQueries.insertHybRefDataset(hybrefId, datasetId, hybrefNames.get(i));
                    rowCounter.addToRowCounts("hybrid_ref_data_set");
                }
            }

            //second line: gather the column titles
            List<String> columnTitles = gatherColumnTitles( reader );
            int probeCount = 0;
            line = reader.readLine();

            List<Object[]> batchArguments = makeBatchArgumentsList();
            //now the actual probe values
            while(line != null && line.length() > 0) {
                probeCount++;
                st = new StringTokenizerThatCanReturnNull( line, "\t" );
                //first token is probe name
                String probeName = st.nextToken();
                //look up the probe id from the local map we have already populated
                Integer probeId = lookupProbeId(platformId, probeName);
                if (probeId == null) {
                    throw new LoaderException(String.format("Probe name %s not found in database for platform %d", probeName, platformId));
                }
                
                // need to skip constants, if any, in line
                for(int i = 0; i < constantCount; i++) {
                    st.nextToken();
                }

                //finally the experiment values
                for(int i = 0; i < hybrefCount; i++) {
                    if (hybrefIds.get(i) != null) {
                        //get hyb group corresponding to this column title
                        Long hybGroupId = hybGroupIds.get( columnTitles.get( i + constantCount + 1 ) );
                        if(hybGroupId == null) {
                            throw new LoaderException( "No hybridization group found for column title " + columnTitles.get( i ) );
                        }
                        String value = st.nextToken();
                        if (value == null || value.trim().length() == 0) {
                            value = "null";
                        }
                        // add this value to the batch list
                        Object[] values = new Object[] {platformId, hybrefIds.get(i), hybGroupId, probeId, value};
                        batchArguments.add(values);
                        // do the batch insert if we have reached the limit
                        if (batchArguments.size() == batchSize) {
                            loaderQueries.insertHybridizationValues(batchArguments);
                            rowCounter.addToRowCounts("hybridization_value", batchArguments.size());
                            batchArguments.clear();
                        }
                    } // else, skip this column
                }
                line = reader.readLine();
            }
            // insert last batch if didn't round exactly to batchSize
            if (batchArguments.size() > 0) {
                loaderQueries.insertHybridizationValues(batchArguments);
                rowCounter.addToRowCounts("hybridization_value", batchArguments.size());
            }
        }
        catch(IOException e) {
            throw new LoaderException( e );
        }
        catch(LoaderQueriesException e) {
            throw new LoaderException( e );
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch(IOException e) {
            }
        }
    }

    protected List<Object[]> makeBatchArgumentsList() {
        return new ArrayList<Object[]>(batchSize);
    }

    // extracted method for ease of testing
    protected Integer lookupProbeId(final int platformId, final String probeName) {
        return Loader.probeLookup.lookupProbeId(platformId, probeName);
    }

    private List<String> gatherColumnTitles( final BufferedReader reader ) throws IOException {
        final String line;
        final StringTokenizerThatCanReturnNull st;
        List<String> columnTitles = new ArrayList<String>();
        line = reader.readLine();
        st = new StringTokenizerThatCanReturnNull( line, "\t" );
        while(st.hasMoreTokens()) {
            columnTitles.add( st.nextToken() );
        }
        return columnTitles;
    }

    private List<Long> gatherHybRefs( final StringTokenizerThatCanReturnNull st, final List<String> hybrefNames ) throws LoaderException {
        List<Long> hybrefIds = new ArrayList<Long>();
        while(st.hasMoreTokens()) {
            String hybrefName = st.nextToken();
            Long hybrefId = hybrefIdsByName.get(hybrefName);
            if (hybrefId == null &&  !hybrefIdsByName.containsKey(hybrefName)) {
                throw new LoaderException(String.format("Hybref %s not present in SRDF", hybrefName));
            }
            // note could be adding null if the key is there with a null ID (that means hybref is not a tcga sample)
            hybrefIds.add( hybrefId );
            hybrefNames.add(hybrefName);
        }
        return hybrefIds;
    }
}
