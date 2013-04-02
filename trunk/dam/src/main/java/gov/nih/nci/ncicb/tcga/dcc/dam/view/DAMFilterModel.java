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
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.AVAILABILITY_AVAILABLE;
import static gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries.SHOW_COMPLETE_ROWS_ONLY;

/**
 * Wraps a DAMStaticModel.  Filters rows, columns and cells according to criteria
 * submitted by the user.
 */
public class DAMFilterModel implements DAMModel {

    private DAMModel wrappedModel;
    protected FilterRequestI filterRequest;
    private Map<String, Cell> filteredCells;
    private Map<String, Header> filteredHeaders;
    private Map<String, Cell> completeRowsCells;
    private Map<String, Header> completeRowsHeaders;
    private Map<String, Integer> rowOrColSpans;

    public DAMFilterModel(DAMModel wrappedModel) {
        this.wrappedModel = wrappedModel;
    }

    //for testing
    public DAMModel getWrappedModel() {
        return wrappedModel;
    }

    public void setFilterRequest(FilterRequestI request) {
        this.filterRequest = request;
        if (filterRequest == null) { //shouldn't happen but just in case
            filteredCells = null;
            filteredHeaders = null;
            return;
        }
        if (filterRequest.getMode() == FilterRequestI.Mode.Clear) {
            filteredCells = null;
            filteredHeaders = null;
            //replace with blank request object that has no criteria
            filterRequest = new FilterRequest();
            filterRequest.setMode(FilterRequestI.Mode.Clear);
            return;
        }
        if (filterRequest.getMode() == FilterRequestI.Mode.NoOp) {
            //keep everything the same
            return;
        }
        filterRequest.eliminateNullSampleCriteria();
        filteredCells = new HashMap<String, Cell>();
        filteredHeaders = new HashMap<String, Header>();
        rowOrColSpans = new HashMap<String, Integer>();
        final List<Header> levelHeaders = wrappedModel.getHeadersForCategory(Header.HeaderCategory.Level);
        for (int i = 0; i < wrappedModel.getTotalBatches(); i++) {
            final Header batchHeader = wrappedModel.getBatchHeader(i);
            final List<Header> sampleHeaders = batchHeader.getChildHeaders();
            for (final Header sampleHeader : sampleHeaders) {
                //look at the individual cells - if ANY matches, we add the row and column headers to the filter
                final List<Cell> sampleCells = wrappedModel.getCellsForHeader(sampleHeader);
                for (int icol = 0; icol < levelHeaders.size(); icol++) {
                    final Cell cell = sampleCells.get(icol);
                    final Header levelHeader = levelHeaders.get(icol);
                    if (filterRequest.cellMatchesFilter(cell, sampleHeader, levelHeader)) {
                        addToFilter(cell, sampleHeader, levelHeader, filteredCells, filteredHeaders);
                    }
                }
            }
        }
        if (SHOW_COMPLETE_ROWS_ONLY.equals(filterRequest.getShowCompleteRowsOnly())) {
            completeRowsCells = new HashMap<String, Cell>();
            completeRowsHeaders = new HashMap<String, Header>();
            processShowCompleteRowsOnly(completeRowsCells, completeRowsHeaders);
            filteredCells = completeRowsCells;
            filteredHeaders = completeRowsHeaders;
        }

        //recalc the row and colspan numbers to only included filtered children
        recalcRowColspans();
    }

    public FilterRequestI getFilterRequest() {
        return filterRequest;
    }

    protected void processShowCompleteRowsOnly(final Map<String, Cell> cellMap,
                                               final Map<String, Header> headerMap) {
        final List<Header> levelHeaders = getHeadersForCategory(Header.HeaderCategory.Level);
        for (int i = 0; i < getTotalBatches(); i++) {
            final Header batchHeader = getBatchHeader(i);
            final List<Header> sampleHeaders = batchHeader.getChildHeaders();
            for (final Header sampleHeader : sampleHeaders) {
                final List<Cell> sampleCells = getCellsForHeader(sampleHeader);
                for (final Cell cell : sampleCells) {
                    if (!AVAILABILITY_AVAILABLE.equals(cell.getAvailability())) {
                        sampleCells.clear();
                        break;
                    }
                }
                if (sampleCells.size() > 0) {
                    for (int icol = 0; icol < levelHeaders.size(); icol++) {
                        final Cell cell = sampleCells.get(icol);
                        final Header levelHeader = levelHeaders.get(icol);
                        if (filterRequest.cellMatchesFilter(cell, sampleHeader, levelHeader)) {
                            addToFilter(cell, sampleHeader, levelHeader, cellMap, headerMap);
                        }
                    }
                }
            }
        }
    }

    private void addToFilter(Cell cell, Header sampleHeader, Header levelHeader,
                             Map<String, Cell> cellMap, Map<String, Header> headerMap) {
        if (cellMap.get(cell.getId()) == null) {
            cellMap.put(cell.getId(), cell);
        }
        if (headerMap.get(sampleHeader.getId()) == null) {
            headerMap.put(sampleHeader.getId(), sampleHeader);
            Header batchHeader = sampleHeader.getParentHeader();
            if (headerMap.get(batchHeader.getId()) == null) {
                headerMap.put(batchHeader.getId(), batchHeader);
            }
        }
        if (headerMap.get(levelHeader.getId()) == null) {
            headerMap.put(levelHeader.getId(), levelHeader);
            Header centerHeader = levelHeader.getParentHeader();
            if (headerMap.get(centerHeader.getId()) == null) {
                headerMap.put(centerHeader.getId(), centerHeader);
                Header ptHeader = centerHeader.getParentHeader();
                if (headerMap.get(ptHeader.getId()) == null) {
                    headerMap.put(ptHeader.getId(), ptHeader);
                }
            }
        }
    }

    private void recalcRowColspans() {
        for (Header hdr : filteredHeaders.values()) {
            if (hdr.getCategory() == Header.HeaderCategory.PlatformType
                    || hdr.getCategory() == Header.HeaderCategory.Batch) {
                _calcHeaderRowColSpans(hdr);
            }
        }
    }

    private int _calcHeaderRowColSpans(Header header) {
        int totalSpan = 0;
        if (header.getChildHeaders().isEmpty()) {
            //no children, so our span is 1 by default (sample or level header)
            totalSpan = 1;
        } else {
            for (Header childHeader : header.getChildHeaders()) {
                //include only FILTERED children
                if (filteredHeaders.get(childHeader.getId()) != null) {
                    totalSpan += _calcHeaderRowColSpans(childHeader);
                }
            }
        }
        rowOrColSpans.put(header.getId(), totalSpan);
        return totalSpan;
    }

    public String getDiseaseType() {
        return wrappedModel.getDiseaseType();
    }

    public int getTotalBatches() {
        int ret;
        if (filterIsActive()) {
            ret = getTotalFilteredBatches();
        } else {
            ret = wrappedModel.getTotalBatches();
        }
        return ret;
    }

    private int getTotalFilteredBatches() {
        int ret = 0;
        for (Header header : filteredHeaders.values()) {
            if (header.getCategory() == Header.HeaderCategory.Batch) {
                ret++;
            }
        }
        return ret;
    }

    public Header getBatchHeader(int index) {
        Header ret;
        if (filterIsActive()) {
            ret = getFilteredBatchHeader(index);
        } else {
            ret = wrappedModel.getBatchHeader(index);
        }
        return ret;
    }

    private Header getFilteredBatchHeader(int index) {
        Header ret = null;
        int iFilteredBatch = -1;
        int totalBatches = wrappedModel.getTotalBatches();
        for (int i = 0; i < totalBatches; i++) {
            Header batch = wrappedModel.getBatchHeader(i);
            if (filteredHeaders.containsKey(batch.getId())) {
                iFilteredBatch++;
                if (iFilteredBatch == index) {
                    ret = batch;
                    break;
                }
            }
        }
        return ret;
    }

    public Header getHeaderById(String id) {
        Header ret;
        Header hdr = wrappedModel.getHeaderById(id);
        if (filterIsActive() && hdr.getHeaderType() != Header.HeaderType.COL_HEADER) {
            ret = filteredHeaders.get(id);
        } else {
            ret = hdr;
        }
        return ret;
    }

    public Collection<Cell> getAllCells() {
        Collection<Cell> ret;
        if (filterIsActive()) {
            ret = filteredCells.values();
        } else {
            ret = wrappedModel.getAllCells();
        }
        return ret;
    }

    public Cell getCell(String id) {
        Cell ret;
        if (filterIsActive()) {
            ret = filteredCells.get(id);
            if (ret == null) {
                ret = makeNullCell(wrappedModel.getCell(id));
            }
        } else {
            ret = wrappedModel.getCell(id);
        }
        return ret;
    }

    private DataSet nullds;

    //make an empty cell to show in place of the actual cell, when cell has been filtered
    private Cell makeNullCell(Cell original) {
        Cell ret = new Cell();
        ret.setCol(original.getCol());
        ret.setRow(original.getRow());
        ret.setId(original.getId());
        if (nullds == null) {
            nullds = new DataSet();  //or should we make a special subclass just for null
            nullds.setAvailability(DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE);
        }
        ret.addDataset(nullds);
        return ret;
    }

    public Header getHeader(Header.HeaderCategory category, String name) {
        Header ret;
        if (filterIsActive()) {
            switch (category) {
                case Batch:
                case Sample:
                    ret = getFilteredHeader(category, name);
                    break;
                default:
                    ret = wrappedModel.getHeader(category, name);
            }
        } else {
            ret = wrappedModel.getHeader(category, name);
        }
        return ret;
    }

    private Header getFilteredHeader(Header.HeaderCategory category, String name) {
        Header ret = null;
        for (Header header : filteredHeaders.values()) {
            if (header.getCategory() == category && header.getName().equals(name)) {
                ret = header;
                break;
            }
        }
        return ret;
    }

    //used to cache result of last call to getCellsForHeader as optimization
    private class GetCellsForHeader_args {

        public Header header;
        public long filterMillis;
        public List<Cell> ret;
    }

    private GetCellsForHeader_args getCellsForHeader_args;

    public List<Cell> getCellsForHeader(Header header) {
        List<Cell> ret;
        if (filterIsActive()) {
            //optimization: this call is made repeatedly, and we don't need to generate the list each time
            if (getCellsForHeader_args != null
                    && getCellsForHeader_args.header == header
                    && getCellsForHeader_args.filterMillis == filterRequest.getMillis()) {
                ret = getCellsForHeader_args.ret;
            } else {
                //break it into list of Level or Sample headers
                List<Header> rowOrColHdrs = new ArrayList<Header>();
                getRowOrColHeaders(header, rowOrColHdrs);
                ret = new ArrayList<Cell>();
                for (Header rowOrColHdr : rowOrColHdrs) {
                    getCellsForRowOrColHeader(rowOrColHdr, ret);
                }
                //cache the list so we can use it next time
                getCellsForHeader_args = new GetCellsForHeader_args();
                getCellsForHeader_args.header = header;
                getCellsForHeader_args.filterMillis = filterRequest.getMillis();
                getCellsForHeader_args.ret = ret;
            }
        } else {
            ret = wrappedModel.getCellsForHeader(header);
        }
        return ret;
    }

    private void getRowOrColHeaders(Header header, List<Header> rowOrColHdrs) {
        List<Header> childHeaders = header.getChildHeaders();
        if (childHeaders != null && childHeaders.size() > 0) {
            for (Header childHdr : childHeaders) {
                getRowOrColHeaders(childHdr, rowOrColHdrs);
            }
        } else {
            rowOrColHdrs.add(header);
        }
    }

    private void getCellsForRowOrColHeader(Header header, List<Cell> ret) {
        List<Header> oppositeAxisHeaders;
        if (header.getHeaderType() == Header.HeaderType.ROW_HEADER) {
            oppositeAxisHeaders = wrappedModel.getHeadersForCategory(Header.HeaderCategory.Level);
        } else {
            oppositeAxisHeaders = wrappedModel.getHeadersForCategory(Header.HeaderCategory.Sample);
        }
        int iCell = 0;
        for (Cell cell : wrappedModel.getCellsForHeader(header)) {
            //get the opposite axis header corresponding to this cell - is it in the filtered list?
            Header oppositeHeader = oppositeAxisHeaders.get(iCell);
            if (filteredHeaders.containsKey(oppositeHeader.getId())) {
                if (!filteredCells.containsKey(cell.getId())) {
                    cell = makeNullCell(cell);
                }
                ret.add(cell);
            }
            iCell++;
        }
    }

    public List<Header> getHeadersForHeader(Header header) {
        List<Header> ret = null;
        if (filterIsActive()) {
            ret = new ArrayList<Header>();
            if (filteredHeaders.get(header.getId()) != null) {
                for (Header childHeader : header.getChildHeaders()) {
                    if (filteredHeaders.get(childHeader.getId()) != null) {
                        ret.add(childHeader);
                    }
                }
            }
        } else {
            ret = wrappedModel.getHeadersForHeader(header);
        }
        return ret;
    }

    public List<Header> getHeadersForCategory(Header.HeaderCategory category) {
        List<Header> ret = null;
        if (filterIsActive()) {
            ret = new ArrayList<Header>();
            for (Header header : wrappedModel.getHeadersForCategory(category)) {
                if (filteredHeaders.get(header.getId()) != null) {
                    ret.add(header);
                }
            }
        } else {
            ret = wrappedModel.getHeadersForCategory(category);
        }
        return ret;
    }

    public int getTotalColumns() {
        int ret = 0;
        if (filterIsActive()) {
            ret = getTotalFilteredColumns();
        } else {
            ret = wrappedModel.getTotalColumns();
        }
        return ret;
    }

    private int getTotalFilteredColumns() {
        int ret = 0;
        for (Header levelHeader : wrappedModel.getHeadersForCategory(Header.HeaderCategory.Level)) {
            if (filteredHeaders.get(levelHeader.getId()) != null) {
                ret++;
            }
        }
        return ret;
    }

    //todo: combine these two methods into one?
    public int getHeaderColSpan(String headerId) {
        int ret = 0;
        if (filterIsActive()) {
            ret = rowOrColSpans.get(headerId);
        } else {
            ret = wrappedModel.getHeaderColSpan(headerId);
        }
        return ret;
    }

    public int getHeaderRowSpan(String headerId) {
        int ret = 0;
        if (filterIsActive()) {
            ret = rowOrColSpans.get(headerId);
        } else {
            ret = wrappedModel.getHeaderRowSpan(headerId);
        }
        return ret;
    }

    // false if filter request is null or mode is Clear
    private boolean filterIsActive() {
        return filterRequest != null && filterRequest.getMode() != FilterRequestI.Mode.Clear;
    }

    public void setFilteredCells(Map<String, Cell> filteredCells) {
        this.filteredCells = filteredCells;
    }

    public void setFilteredHeaders(Map<String, Header> filteredHeaders) {
        this.filteredHeaders = filteredHeaders;
    }
}
