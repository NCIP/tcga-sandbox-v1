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
import gov.nih.nci.ncicb.tcga.dcc.dam.util.StaticLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import org.apache.log4j.Level;

import java.util.*;

/**
 * Author: David Nassau
 * <p/>
 * Model instantiated one per user, keeps track of that which cells/headers are selected.
 */
public class DAMSelectionModel {

    private DAMModel wrappedModel; //the singleton instance of the static model shared by all users
    private Map<String, Cell> selectedCells = new HashMap<String, Cell>();
    private Map<String, Header> selectedHeaders = new HashMap<String, Header>();

    public DAMSelectionModel(DAMModel damModel) {
        this.wrappedModel = damModel;
    }

    public void wrapModel(DAMModel damModel) {
        this.wrappedModel = damModel;
    }

    public void setSelection(SelectionRequest selectionRequest) {
        if (selectionRequest != null) {
            String mode = selectionRequest.getMode();
            if (SelectionRequest.MODE_HEADER.equals(mode)) {
                updateSelectedCells(selectionRequest.getSelectedCells());
                toggleHeader(selectionRequest.getHeaderId(), selectionRequest.isIntersect());
            } else if (SelectionRequest.MODE_CELLS.equals(mode)) {
                updateSelectedCells(selectionRequest.getSelectedCells());
            } else if (SelectionRequest.MODE_SELECTALL.equals(mode)) {
                selectAll();
            } else if (SelectionRequest.MODE_UNSELECTALL.equals(mode)) {
                unselectAll();
            } else if (SelectionRequest.MODE_NOOP.equals(mode)) {
                //do nothing: used to cancel from DAD back to current state of DAM
            } else {
                StaticLogger.getInstance().logToLogger(Level.ERROR, "Mode not recognized: " + mode);
            }
        }
    }

    public boolean isCellSelected(String cellId) {
        return (selectedCells.get(cellId) != null);
    }

    /**
     * Returns true if a particular header is selected
     */
    public boolean isHeaderSelected(String headerId) {
        return (selectedHeaders.get(headerId) != null);
    }

    /**
     * used to preload Javascript array with already-selected cells
     *
     * @return
     */
    public List getSelectedCellIds() {
        List ret = new ArrayList();
        for (Cell cell : selectedCells.values()) {
            ret.add(cell.getId());
        }
        return ret;
    }

    /**
     * updates model with new set of selected cells
     *
     * @param cellIds
     */
    public void updateSelectedCells(String cellIds) {
        selectedCells.clear();
        StringTokenizer tokens = new StringTokenizer(cellIds, ",");
        while (tokens.hasMoreTokens()) {
            String cellId = tokens.nextToken();
            Cell cell = wrappedModel.getCell(cellId);
            if (cell != null) {
                selectedCells.put(cellId, cell);
            } else {
            }
        }
    }

    /**
     * selects/unselects a header
     *
     * @param headerId
     * @param intersect
     */
    public void toggleHeader(String headerId, boolean intersect) {
        Header header = wrappedModel.getHeaderById(headerId);
        List<Cell> intersectCells = (intersect ? new ArrayList<Cell>() : null);
        if (header != null) {
            toggleHeader(header, intersect, !isHeaderSelected(header.getId()), intersectCells);
        }
        if (intersect) {
            applyIntersect(intersectCells);
        }
    }

    private void toggleHeader(Header header, boolean intersect, boolean doSelect, List<Cell> intersectCells) {
        if (doSelect) {
            if (selectedHeaders.get(header.getId()) == null) {
                selectedHeaders.put(header.getId(), header);
            }
        } else {
            if (selectedHeaders.get(header.getId()) != null) {
                selectedHeaders.remove(header.getId());
            }
        }
        if (header.getChildHeaders().size() > 0) { //recurse to child headers
            for (Header childHeader : header.getChildHeaders()) {
                toggleHeader(childHeader, intersect, doSelect, intersectCells);
            }
        } else {  //lowest level header, select/unselect the cells
            if (!intersect || !doSelect) { //either union or unselect, do normal behavior
                toggleCellsForHeader(header, doSelect);
            } else {
                intersectCellsForHeader(header, intersectCells);
            }
        }
    }

    private void toggleCellsForHeader(Header header, boolean doSelect) {
        for (Cell cell : wrappedModel.getCellsForHeader(header)) {
            if (doSelect) {
                if (DataAccessMatrixQueries.AVAILABILITY_AVAILABLE.equals(cell.getAvailability()) && !isCellSelected(cell.getId())) {
                    selectedCells.put(cell.getId(), cell);
                }
            } else {
                if (isCellSelected(cell.getId())) {
                    selectedCells.remove(cell.getId());
                }
            }
        }
    }

    private void intersectCellsForHeader(Header header, List<Cell> intersectCells) {
        //special behavior: we select only those cells that are under this header AND already selected.
        //All others are unselected
        for (Cell cell : wrappedModel.getCellsForHeader(header)) { //header.getChildCells()) {
            if (cell.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE) && isCellSelected(cell.getId())) {
                intersectCells.add(cell);
            }
        }
    }

    private void applyIntersect(List<Cell> intersectCells) {
        selectedCells.clear();
        for (Cell cell : intersectCells) {
            selectedCells.put(cell.getId(), cell);
        }
    }

    /**
     * Selects all cells
     */
    public void selectAll() {
        selectedCells.clear();
        for (Cell cell : wrappedModel.getAllCells()) {
            if (cell.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                selectedCells.put(cell.getId(), cell);
            }
        }
    }

    /**
     * Unselects all cells
     */
    public void unselectAll() {
        selectedCells.clear();
        selectedHeaders.clear();
    }

    //*** used to get list of selected cells for segue to DAD page ***
    private List<Cell> getSelectedCells() {
        List<Cell> ret = new ArrayList<Cell>();
        //has to be retrieved in order of column headers
        List<Header> platformHeaders = wrappedModel.getHeadersForCategory(Header.HeaderCategory.PlatformType);
        for (Header platformHeader : platformHeaders) {
            for (Header centerHeader : platformHeader.getChildHeaders()) {
                for (Header levelHeader : centerHeader.getChildHeaders()) {
                    for (Cell cell : wrappedModel.getCellsForHeader(levelHeader)) {
                        if (selectedCells.containsKey(cell.getId())) {
                            ret.add(cell);
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Fetches a list of all the selected datasets, for use as input to the DAO interface.
     *
     * @return
     */
    public List<DataSet> getSelectedDataSets() {
        List<DataSet> ret = new ArrayList<DataSet>();
        for (Cell cell : getSelectedCells()) {
            ret.addAll(cell.getAvailableDatasets());
        }
        return ret;
    }
}
