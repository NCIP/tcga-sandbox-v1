/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * Keeps the DataSet from the archive with the greatest archive number which is Available.
 *
 * @author HickeyE
 * @version $id$
 */
public class DataSetReducer implements DataSetReducerI {

    public List<DataSet> reduceLevelTwoThree( final List<DataSet> dsList,
                                              final int level ) throws DataAccessMatrixQueries.DAMQueriesException {
        // initial ret is copy of complete list
        final List<DataSet> ret = new ArrayList<DataSet>();
        ret.addAll( dsList );
        ListIterator i = dsList.listIterator();
        HashMap<String, DataSetLevelTwoThree> createdDataSets = new HashMap<String, DataSetLevelTwoThree>();
        HashMap<String, DataSetLevelTwoThree> representativeDataSet = new HashMap<String, DataSetLevelTwoThree>();
        while(i.hasNext()) {
            DataSetLevelTwoThree dsLoop = (DataSetLevelTwoThree) i.next();
            final String platformType = dsLoop.getPlatformTypeId();
            final String center = dsLoop.getCenterName();
            final String platform = dsLoop.getPlatformId();
            final String sample = dsLoop.getSample();
            final int si = dsLoop.getDepositBatch();
            final Integer rev = dsLoop.getDataRevision();
            final String key = platformType + "|" + center + "|" + platform + "|" + level + "|" + sample + "|" + si + "|" + rev + "|";
            final String key2 = platformType + "|" + center + "|" + platform + "|" + level + "|" + sample + "|";
            DataSetLevelTwoThree ds = createdDataSets.get( key );
            DataSetLevelTwoThree dsRep = representativeDataSet.get( key2 );
            if(dsRep == null && ds == null) {
                // neither the specific deposit archive nor the deposit archive "type" have been seen yet.
                createdDataSets.put( key, dsLoop );
                representativeDataSet.put( key2, dsLoop );
            } else if(dsRep != null && ds == null) {
                // the specific archive has not been seen, but the archive type has.
                createdDataSets.put( key, dsLoop );
                if(shouldReplace( dsLoop, dsRep )) {
                    representativeDataSet.remove( key2 );
                    representativeDataSet.put( key2, dsLoop );
                    // remove dsRep from the return list
                    ret.remove( dsRep );
                }
            } else if(dsRep != null) {
                // the specific archive has been seen and the archive type has
                if(shouldReplace( ds, dsRep )) {
                    representativeDataSet.remove( key2 );
                    representativeDataSet.put( key2, ds );
                    ret.remove( dsRep );
                }
            } else {
                //  logger.logToLogger( Level.ERROR, "Should not be able to have a new dataset when the representative dataset is null" );
                throw new DataAccessMatrixQueries.DAMQueriesException( "Should not be able to have a new dataset when the representative dataset is null" );
            }
        }
        return ret;
    }

    public List<DataSet> reduceLevelOne(
            final List<DataSet> dsList ) throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> ret = new ArrayList<DataSet>();
        ret.addAll( dsList );
        int level = 1;
        ListIterator i = dsList.listIterator();
        final HashMap<String, DataSet> createdDataSets = new HashMap<String, DataSet>();
        final HashMap<String, DataSet> representativeDataSet = new HashMap<String, DataSet>();
        while(i.hasNext()) {
            DataSet dsLoop = (DataSet) i.next();
            final String platformType = dsLoop.getPlatformTypeId();
            final String center = dsLoop.getCenterId();
            final String platform = dsLoop.getPlatformId();
            final String sample = dsLoop.getSample();
            // data deposit revision is not stored for level one.  hopefully there will not be more than one revision per archive number, which would be a DCC error anyway
            // final Integer rev = dsLoop.getRevision();
            final String key = platformType + "|" + center + "|" + platform + "|" + level + "|" + sample + "|" + dsLoop.getBatch(); //  + "|" + rev;
            final String key2 = platformType + "|" + center + "|" + platform + "|" + sample;
            DataSet ds = createdDataSets.get( key );
            DataSet dsRep = representativeDataSet.get( key2 );
            // neither the specific deposit archive nor the deposit archive "type" have been seen yet.
            // therefore it is a whole new dataset.
            if(dsRep == null && ds == null) {
                createdDataSets.put( key, dsLoop );
                representativeDataSet.put( key2, dsLoop );
                // the specific archive has not been seen, but the archive type has.
            } else if(dsRep != null && ds == null) {
                createdDataSets.put( key, dsLoop );
                if(shouldReplace( dsLoop, dsRep )) {
                    representativeDataSet.remove( key2 );
                    representativeDataSet.put( key2, dsLoop );
                    ret.remove( dsRep );
                }
                // the specific archive has been seen and the archive type has
                // want to keep the latest specific archive as the representative
            } else if(dsRep != null) {
                if(shouldReplace( ds, dsRep )) {
                    representativeDataSet.remove( key2 );
                    representativeDataSet.put( key2, ds );
                    ret.remove( dsRep );
                }
            } else {
                //  logger.logToLogger( Level.ERROR, "Should not be able to have a new dataset when the representative dataset is null" );
                throw new DataAccessMatrixQueries.DAMQueriesException( "Should not be able to have a new dataset when the representative dataset is null" );
            }
        }
        return ret;
    }

    private boolean shouldReplace( DataSet ds, DataSet rep ) {
        boolean shouldReplace = false;
        if(ds.getAvailability().equals( rep.getAvailability() )) {
            // if availabilites are the same, replace if batch of ds is more recent than rep
            shouldReplace = ds.getDateAdded().after( rep.getDateAdded() );
        } else {
            // we know the availabilities are different...
            if(ds.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
                // if ds is available and rep isn't, then should replace
                shouldReplace = true;
            } else if(rep.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE )) {
                // if rep is not available, replace with ds
                shouldReplace = true;
            }
        }
        if(shouldReplace) {
            shouldReplace = false;
            // only really replace if one or more barcodes in the datasets are exactly the same
            // otherwise it is ok to keep both of them
            for(String barcode : ds.getBarcodes()) {
                if(rep.getBarcodes().contains( barcode )) {
                    shouldReplace = true;
                }
            }
        }
        return shouldReplace;
    }
}
