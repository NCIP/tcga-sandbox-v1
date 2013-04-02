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
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSorterAndGapFillerI;

import java.util.*;

/**
 * Author: David Nassau
 * <p/>
 * Contains all the "static" matrix information which is shared across all users.
 * One instance is created for each disease type.
 * This model does not track "selected" state, since that is a per-user property,
 * nor does it keep track of color schemes or filters.
 */
public class DAMStaticModel implements DAMModel {

    //separates column from row in the cell id
    private static final String CELLID_SEPARATOR = ".";
    //one instance per disease type
    // note: uses a Hashtable because that is synchronized and multiple threads will access this map
    private static final Map<String, DAMStaticModel> instances = new Hashtable<String, DAMStaticModel>();

    /**
     * Returns the instance for a disease type.
     *
     * @param diseaseType the disease type
     * @return a static model for the disease type
     */
    public static DAMStaticModel getInstance(final String diseaseType) {
        return instances.get(diseaseType);
    }

    //for testing
    public DAMModel getWrappedModel() {
        return null;
    }

    /**
     * Creates an instance for a disease type.
     *
     * @param diseaseType the disease type
     * @param dataSets    the data sets for the disease type
     * @param dataSetSorterAndGapFiller to use for sorting the data sets
     * @return the static model for the disease type and data sets
     */
    public static DAMStaticModel createInstance(final String diseaseType,
                                                final List<DataSet> dataSets,
                                                final DataSorterAndGapFillerI dataSetSorterAndGapFiller) {
        final DAMStaticModel instance = new DAMStaticModel(diseaseType);
        instance.setDataSetSorterAndGapFiller(dataSetSorterAndGapFiller);
        instance.addDataSets(dataSets);
        instances.put(diseaseType, instance);
        return instance;
    }

    //Cells are stored as a 2d array, and the Ids assigned to them are <col>.<row> so they are easy to look up
    private Cell[][] cellArray;
    //In addition to being stored in sorted lists, the headers also have parent-child relationships
    private final List<Header> batchHeaders = new ArrayList<Header>();
    private final List<Header> sampleHeaders = new ArrayList<Header>();
    private final List<Header> platformTypeHeaders = new ArrayList<Header>();
    private final List<Header> centerHeaders = new ArrayList<Header>();
    private final List<Header> levelHeaders = new ArrayList<Header>();
    //used to assign header Ids
    private int lastAssignedId;
    private final String diseaseType;

    private DataSorterAndGapFillerI dataSetSorterAndGapFiller;

    // only called from createInstance and from tests.
    protected DAMStaticModel(final String diseaseType) {
        lastAssignedId = 0;
        this.diseaseType = diseaseType;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    protected void addDataSets(final List<DataSet> dataSets) {
        if (dataSetSorterAndGapFiller != null) {
            dataSetSorterAndGapFiller.sort(dataSets);
        }

        //create the header objects
        createHeaders(dataSets);
        //sort the headers
        sortHeaders();
        //create all the A and P cells
        createCells(dataSets);
        //calculate the rowspan and colspan for the HTML layout
        calcHeaderRowColSpans();
        //calculate which headers are protected
        calcHeaderProtected();
    }

    /*
    helper method called by createCells and createHeaders.  centerPlatform string uses platform alias if not null.
     */
    private String getCenterPlatform(DataSet dataSet) {
        String center = dataSet.getCenterId();
        final String platform = dataSet.getPlatformId();
        final String platformAlias = dataSet.getPlatformAlias();
        if (platformAlias != null && platformAlias.length() > 0) {
            center += ("." + platformAlias);
        } else if (platform != null && platform.length() > 0) {
            center += ("." + platform);
        }
        return center;
    }

    private void createCells(final List<DataSet> dataSets) {
        cellArray = new Cell[levelHeaders.size()][sampleHeaders.size()];
        for (final DataSet dataSet : dataSets) {
            final Header batchHeader = findHeader(Header.HeaderCategory.Batch, dataSet.getBatch(), null);
            final Header sampleHeader = findHeader(Header.HeaderCategory.Sample, dataSet.getSample(), batchHeader);
            final Header platformTypeHeader = findHeader(Header.HeaderCategory.PlatformType, dataSet.getPlatformTypeId(), null);
            String centerPlatform = getCenterPlatform(dataSet);
            final Header centerHeader = findHeader(Header.HeaderCategory.Center, centerPlatform, platformTypeHeader);
            final Header levelHeader = findHeader(Header.HeaderCategory.Level, dataSet.getLevel(), centerHeader);
            final int levelIndex = levelHeader.getCategoryIndex();
            final int sampleIndex = sampleHeader.getCategoryIndex();
            // if the cell isn't already there, create it
            Cell cell = cellArray[levelIndex][sampleIndex];
            if (cell == null) {
                cell = new Cell();
                cell.setRow(sampleIndex);
                cell.setCol(levelIndex);
                cell.setId(levelIndex + CELLID_SEPARATOR + sampleIndex);
                cellArray[levelIndex][sampleIndex] = cell;
            }
            // add the dataset to the cell
            cell.addDataset(dataSet);
        }
    }

    private void createHeaders(final List<DataSet> dataSets) {
        for (final DataSet dataSet : dataSets) {
            //row headers
            Header batchHeader = findHeader(Header.HeaderCategory.Batch, dataSet.getBatch(), null);
            if (batchHeader == null) {
                batchHeader = makeHeader(Header.HeaderCategory.Batch, dataSet.getBatch(), null, Header.HeaderType.ROW_HEADER);
                batchHeaders.add(batchHeader);
            }
            Header sampleHeader = findHeader(Header.HeaderCategory.Sample, dataSet.getSample(), batchHeader);
            if (sampleHeader == null) {
                sampleHeader = makeHeader(Header.HeaderCategory.Sample, dataSet.getSample(), batchHeader, Header.HeaderType.ROW_HEADER);
                sampleHeaders.add(sampleHeader);
            }
            //column headers
            Header platformTypeHeader = findHeader(Header.HeaderCategory.PlatformType, dataSet.getPlatformTypeId(), null);
            if (platformTypeHeader == null) {
                platformTypeHeader = makeHeader(Header.HeaderCategory.PlatformType, dataSet.getPlatformTypeId(), null, Header.HeaderType.COL_HEADER);
                platformTypeHeaders.add(platformTypeHeader);
            }
            String centerPlatform = getCenterPlatform(dataSet);
            Header centerHeader = findHeader(Header.HeaderCategory.Center, centerPlatform, platformTypeHeader);
            if (centerHeader == null) {
                centerHeader = makeHeader(Header.HeaderCategory.Center, centerPlatform, platformTypeHeader, Header.HeaderType.COL_HEADER);
                //insert the platform Id separately so we can look it up and use in a tooltip
                centerHeader.setTag(centerPlatform);
                centerHeaders.add(centerHeader);
            }
            Header levelHeader = findHeader(Header.HeaderCategory.Level, dataSet.getLevel(), centerHeader);
            if (levelHeader == null) {
                levelHeader = makeHeader(Header.HeaderCategory.Level, dataSet.getLevel(), centerHeader, Header.HeaderType.COL_HEADER);
                levelHeaders.add(levelHeader);
            }
        }
    }

    private void sortHeaders() {
        int iPlatType = 0, iCenter = 0, iLevel = 0, iSample = 0, iBatch = 0;
        //number the column headers
        for (final Header header : getHeadersForCategory(Header.HeaderCategory.PlatformType)) {
            header.setCategoryIndex(iPlatType++);
            for (final Header centerHeader : header.getChildHeaders()) {
                centerHeader.setCategoryIndex(iCenter++);
                for (final Header levelHeader : centerHeader.getChildHeaders()) {
                    levelHeader.setCategoryIndex(iLevel++);
                }
            }
        }
        //number the batch and sample headers
        for (final Header header : getHeadersForCategory(Header.HeaderCategory.Batch)) {
            header.setCategoryIndex(iBatch++);
            for (final Header sampleHeader : header.getChildHeaders()) {
                sampleHeader.setCategoryIndex(iSample++);
            }
        }

        // make sure the child header arrays are in the correct order based on the order of their parent
        Collections.sort(centerHeaders);
        Collections.sort(levelHeaders);
        Collections.sort(sampleHeaders);
    }

    private void calcHeaderProtected() {
        for (final Header levelHeader : levelHeaders) {
            //do any cells under this header have protection?
            boolean isProtected = false;
            for (int irow = 0; irow < sampleHeaders.size(); irow++) {
                final Cell cell = cellArray[levelHeader.getCategoryIndex()][irow];
                if (cell != null && cell.isProtected()) {
                    isProtected = true;
                    break;
                }
            }
            if (isProtected) {
                levelHeader.setProtected(true);
                levelHeader.getParentHeader().setProtected(true);
                levelHeader.getParentHeader().getParentHeader().setProtected(true);
            }
        }
    }

    //called after adding all the cells to calculate how big each header should be
    //The unit is one cell
    private void calcHeaderRowColSpans() {
        for (final Header ptHeader : platformTypeHeaders) {
            _calcHeaderRowColSpans(ptHeader);
        }
        for (final Header batchHeader : batchHeaders) {
            _calcHeaderRowColSpans(batchHeader);
        }
    }

    private int _calcHeaderRowColSpans(final Header header) {
        int totalSpan = 0;
        if (header.getChildHeaders().isEmpty()) {
            //no children, so our span is 1 by default (sample or level header)
            totalSpan = 1;
        } else {
            for (final Header childHeader : header.getChildHeaders()) {
                if (childHeader.getRowOrColSpan() == 0) {
                    //recurse
                    totalSpan += _calcHeaderRowColSpans(childHeader);
                }
            }
        }
        header.setRowOrColSpan(totalSpan);
        return totalSpan;
    }

    private int getNextSequentialId() {
        return ++lastAssignedId;
    }

    private Header makeHeader(final Header.HeaderCategory category, final String name, final Header parent,
                              final Header.HeaderType headerType) {
        final Header header = new Header();
        header.setId(getNextSequentialId());
        header.setHeaderType(headerType);
        header.setCategory(category);
        header.setName(name);
        header.setParentHeader(parent);
        if (parent != null) {
            parent.getChildHeaders().add(header);
        }
        return header;
    }

    public List<Header> getHeadersForCategory(final Header.HeaderCategory category) {
        List<Header> ret = null;
        switch (category) {
            case Batch:
                ret = batchHeaders;
                break;
            case Sample:
                ret = sampleHeaders;
                break;
            case PlatformType:
                ret = platformTypeHeaders;
                break;
            case Center:
                ret = centerHeaders;
                break;
            case Level:
                ret = levelHeaders;
                break;
        }
        return ret;
    }

    //private Header findHeader(String category, String name, Header parent) {
    private Header findHeader(final Header.HeaderCategory category, final String name, final Header parent) {
        Header ret = null;
        for (final Header header : getHeadersForCategory(category)) {
            if (header.getParentHeader() == parent &&
                    header.getCategory() == category &&
                    stringsAreEqual(name, header.getName())) {
                ret = header;
                break;
            }
        }
        return ret;
    }

    private boolean stringsAreEqual(final String s1, final String s2) {
        boolean ret;
        if (s1 == null && s2 == null) {
            ret = true;
        } else if (s1 == null || s2 == null) {
            ret = false;
        } else {
            ret = s1.equals(s2);
        }
        return ret;
    }

    /**
     * Returns a cell object given a cell Id
     *
     * @param id The cell Id assigned by the model, not a sample Id or barcode.
     * @return the Cell for the given Id
     */
    public Cell getCell(final String id) {
        Cell ret = null;
        if (id != null && id.contains(CELLID_SEPARATOR)) {
            //ret = (Cell) cells.get(id.intern());
            final int dot = id.indexOf('.');
            final int col = Integer.parseInt(id.substring(0, dot));
            final int row = Integer.parseInt(id.substring(dot + 1));
            ret = cellArray[col][row];
        }
        return ret;
    }

    /**
     * Returns a header object given a category and name.
     *
     * @param category the header category
     * @param name     the name for the header
     * @return the Header object
     */
    public Header getHeader(final Header.HeaderCategory category, final String name) {
        Header ret = null;
        for (final Header header : getHeadersForCategory(category)) {
            if (header.getName().equals(name)) {
                ret = header;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns a list of cells for a given header.
     *
     * @param header the header
     * @return cells under the given header
     */
    public List<Cell> getCellsForHeader(final Header header) {
        final List<Cell> ret = new ArrayList<Cell>();
        collectCellsForHeader(header, ret);
        return ret;
    }

    //recurse to the lowest level header and then collect the cells
    private void collectCellsForHeader(final Header header, final List<Cell> cells) {
        boolean isLowestLevelHeader = true;
        for (final Header childHeader : header.getChildHeaders()) {
            collectCellsForHeader(childHeader, cells);
            isLowestLevelHeader = false;
        }
        if (isLowestLevelHeader) {
            if (header.getCategory() == Header.HeaderCategory.Sample) {
                final int row = header.getCategoryIndex();
                cells.addAll(getCellsForRow(row));
            } else {
                final int col = header.getCategoryIndex();
                cells.addAll(getCellsForCol(col));
            }
        }
    }

    private List<Cell> getCellsForRow(final int row) {
        final ArrayList<Cell> ret = new ArrayList<Cell>();
        for (Cell[] aCellArray : cellArray) {
            ret.add(aCellArray[row]);
        }
        return ret;
    }

    private List<Cell> getCellsForCol(final int col) {
        final ArrayList<Cell> ret = new ArrayList<Cell>();
        ret.addAll(Arrays.asList(cellArray[col]));
        return ret;
    }

    /**
     * Returns all subordinate headers under a header.
     *
     * @param header the header
     * @return headers underneath the given header
     */
    public List<Header> getHeadersForHeader(Header header) {
        return header.getChildHeaders();
    }

    /**
     * Returns the total number of batches in the matrix.
     *
     * @return number of batches total in model
     */
    public int getTotalBatches() {
        return batchHeaders.size();
    }

    /**
     * Returns a batch header by sequential index.
     *
     * @param index the index of the desired batch
     * @return the Header for the batch at the given index
     */
    public Header getBatchHeader(final int index) {
        return batchHeaders.get(index);
    }

    /**
     * Returns the header object for a given header Id.
     *
     * @param id header id
     * @return the header with the given id
     */
    public Header getHeaderById(final String id) {
        Header ret = null;
        final List<Header> allHeaders = new ArrayList<Header>();
        allHeaders.addAll(batchHeaders);
        allHeaders.addAll(sampleHeaders);
        allHeaders.addAll(platformTypeHeaders);
        allHeaders.addAll(centerHeaders);
        allHeaders.addAll(levelHeaders);
        for (final Header header : allHeaders) {
            if (id.equals(header.getId())) {
                ret = header;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns all cells in the matrix.
     *
     * @return all the cells in the model
     */
    public Collection<Cell> getAllCells() {
        //return cells.values();
        final ArrayList<Cell> ret = new ArrayList<Cell>();
        for (Cell[] aCellArray : cellArray) {
            ret.addAll(Arrays.asList(aCellArray));
        }
        return ret;
    }

    /**
     * Returns total number of columns (number of "level" headers)
     *
     * @return the number of cell columns in the model
     */
    public int getTotalColumns() {
        return levelHeaders.size();
    }

    public int getHeaderColSpan(String headerId) {
        int ret = 0;
        Header hdr = getHeaderById(headerId);
        if (hdr != null && hdr.getHeaderType() == Header.HeaderType.COL_HEADER) {
            ret = hdr.getRowOrColSpan();
        }
        return ret;
    }

    public int getHeaderRowSpan(String headerId) {
        int ret = 0;
        Header hdr = getHeaderById(headerId);
        if (hdr != null && hdr.getHeaderType() == Header.HeaderType.ROW_HEADER) {
            ret = hdr.getRowOrColSpan();
        }
        return ret;
    }

    public void setDataSetSorterAndGapFiller(final DataSorterAndGapFillerI dataSetSorterAndGapFiller) {
        this.dataSetSorterAndGapFiller = dataSetSorterAndGapFiller;
    }
}
