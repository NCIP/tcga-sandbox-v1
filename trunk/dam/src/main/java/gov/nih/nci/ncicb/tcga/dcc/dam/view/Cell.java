/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * represents a cell (dataset) in the matrix
 */
public class Cell {

    private String id;
    private List<DataSet> datasets = new ArrayList<DataSet>();
    private int row, col;

    //only called from DAMStaticModel, made public for unit test
    public void setId( final String id ) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow( final int row ) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol( final int col ) {
        this.col = col;
    }

    public List<DataSet> getDatasets() {
        return datasets;
    }

    public void addDataset( final DataSet dataset ) {
        datasets.add( dataset );
    }

    /**
     * This is just an internal ID assigned to the cell - doesn't correspond to any external data-driven ID
     * It's used by the JSP and JavaScript to communicate selection actions to the model.
     *
     * @return the internal cell ID
     */
    public String getId() {
        return id;
    }

    /**
     * Code indicating dataset availability, e.g. "A".  If at least one data set is Available then cell will
     * be available.
     *
     * @return the availability of the cell
     */
    public String getAvailability() {
        // a cell's availability is the "highest" of all contained datasets
        int availCount = 0;
        int pendCount = 0;
        int notCount = 0;
        for(DataSet dataset : datasets) {
            if(dataset.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
                availCount++;
            } else if(dataset.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_PENDING )) {
                pendCount++;
            } else if(dataset.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE )) {
                notCount++;
            }
        }
        if(availCount > 0) {
            // if any are available, cell is available
            return DataAccessMatrixQueries.AVAILABILITY_AVAILABLE;
        } else if(pendCount > 0) {
            // if any are pending (but none are available), cell is pending
            return DataAccessMatrixQueries.AVAILABILITY_PENDING;
        } else if(notCount > 0) {
            // if any are not available (but none are available or pending), cell is not available
            return DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE;
        } else {
            // otherwise, not applicable
            return DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE;
        }
    }

    /**
     * Looks at all Available data sets in the cell to determine the tumor/normal status.  If at least one
     * cell is tumor with matched normal, that is what the cell will be.  Order of priority after that is:
     * tumor without match, normal with match, normal without match.  If no data sets are available or have
     * a tumor normal status, will return null.
     *
     * @return the tumor/normal status of the cell
     */
    public String getTumorNormal() {
        int tnCount = 0;
        int tCount = 0;
        int ntCount = 0;
        int nCount = 0;
        int controlCount = 0;
        for(DataSet dataset : getAvailableDatasets()) {
            if(dataset.getTumorNormal() != null) {
                if(dataset.getTumorNormal().equals( DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL )) {
                    tnCount++;
                } else if(dataset.getTumorNormal().equals( DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL )) {
                    tCount++;
                } else if(dataset.getTumorNormal().equals( DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR )) {
                    ntCount++;
                } else if(dataset.getTumorNormal().equals( DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL)) {
                    nCount++;
                } else if (dataset.getTumorNormal().equals(DataAccessMatrixQueries.TUMORNORMAL_CELL_LINE_CONTROL)) {
                    controlCount++;
                }
            }
        }
        if(tnCount > 0) {
            // if any of the contained data sets have a matched normal, then the cell has at least one set of matched data
            return DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL;
        } else if(tCount > 0) {
            // otherwise, if the data sets are for tumor samples and none have matched normals...
            return DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL;
        } else if(ntCount > 0) {
            return DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR;
        } else if(nCount > 0) {
            return DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL;
        } else if (controlCount > 0) {
            return DataAccessMatrixQueries.TUMORNORMAL_CELL_LINE_CONTROL;
        } else {
            return null;
        }
    }

    /**
     * True if any of the cell's datasets are protected.
     *
     * @return if the cell contains protected data or not
     */
    public boolean isProtected() {
        // is protected if any of the contained data sets are protected
        for(DataSet ds : datasets) {
            if(ds.isProtected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the latest date added of all the datasets in the cell.
     *
     * @return the latest DateAdded for the data sets in the cell
     */
    public Date getLatestDateAdded() {
        Date latestDate = datasets.get( 0 ).getDateAdded();
        for(DataSet dataset : datasets) {
            // if this dataset has a date, and this date is after the current latest date (or latest date is null)
            if(dataset.getDateAdded() != null && ( latestDate == null || dataset.getDateAdded().after( latestDate ) )) {
                // then make this dataset's date the latest date
                latestDate = dataset.getDateAdded();
            }
        }
        return latestDate;
    }

    /**
     * Get the earliest added date for all datasets in the cell.
     *
     * @return the earliest DateAdded
     */
    public Date getEarliestDateAdded() {
        Date earliestDate = datasets.get( 0 ).getDateAdded();
        for(DataSet dataset : datasets) {
            // if this dataset has a date, and this date is before the current latest date (or latest date is null)
            if(dataset.getDateAdded() != null && ( earliestDate == null || dataset.getDateAdded().before( earliestDate ) )) {
                // then make this dataset's date the earliest date
                earliestDate = dataset.getDateAdded();
            }
        }
        return earliestDate;
    }

    /**
     * Gets a list of DataSets in this Cell that are available.
     *
     * @return a List of available DataSets in the Cell
     */
    public List<DataSet> getAvailableDatasets() {
        List<DataSet> availableDatasets = new ArrayList<DataSet>();
        for(DataSet ds : datasets) {
            if(ds.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
                availableDatasets.add( ds );
            }
        }
        return availableDatasets;
    }
}
