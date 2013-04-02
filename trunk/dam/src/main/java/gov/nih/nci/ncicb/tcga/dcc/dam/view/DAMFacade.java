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
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DAMResourceBundle;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;

import java.util.List;
/**
 * Author: David Nassau
 */

/**
 * The only class used by the JSP to build up the Data Access Matrix display. Hides an assortment of
 * "model" classes which keep track of the Matrix's state.
 */
public class DAMFacade implements DAMFacadeI {

    public static final String AVAILABILITY_NOTAPPLICABLE = DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE;
    public static final String AVAILABILITY_AVAILABILE = DataAccessMatrixQueries.AVAILABILITY_AVAILABLE;
    /**
     * the string used to get the facade from places
     */
    public static final String FACADE_KEY_NAME = "damFacade";
    private DAMModel damModel; //the basic DAM model, which may be a DAMStaticModel or a wrapping DAMFilterModel
    private DAMSelectionModel selectionModel; //contains information about user-made selections
    private DAMColorScheme colorScheme; //knows how to paint the cells
    private FilterChoices filterChoices; //hands out information used to create filter UI
    private FilterRequestI previousFilterRequest; //user's last filter, so we can re-display it
    private SelectionRequest previousSelectionRequest; // user's last selection request

    public DAMFacade(DAMModel staticModel) {
        filterChoices = FilterChoices.getInstance(staticModel);
        damModel = new DAMFilterModel(staticModel);
        selectionModel = new DAMSelectionModel(damModel);
        colorScheme = DAMColorScheme.getColorScheme(DAMColorScheme.DEFAULT_COLOR_SCHEME); //depends on servlet.xml having created DAMDefaultColorScheme as bean
    }

    /**
     * Specifies a filter, or clears the current filter.
     *
     * @param filterRequest
     */
    public void setFilter(FilterRequestI filterRequest) {
        DAMFilterModel filterModel = (DAMFilterModel) damModel;
        filterModel.setFilterRequest(filterRequest);
        previousFilterRequest = filterModel.getFilterRequest();
    }

    /**
     * Sets the current color scheme. The name must correspond to a name returned by
     * getName() for a particular color scheme class (child of DAMColorScheme). These instances
     * are created by Spring as beans.
     *
     * @param colorSchemeName
     */
    public void setColorSchemeName(String colorSchemeName) {
        DAMColorScheme cs = DAMColorScheme.getColorScheme(colorSchemeName);
        if (cs == null) {
            cs = DAMColorScheme.getColorScheme(DAMColorScheme.DEFAULT_COLOR_SCHEME); //todo: throw exception?
        }
        colorScheme = cs;
    }

    /**
     * Gets the current color scheme name.
     */
    public String getColorSchemeName() {
        String ret = "";
        if (colorScheme != null) {
            ret = colorScheme.getName();
        }
        return ret;
    }

    /**
     * Returns a class that can provide lists of information needed for building a filter UI
     *
     * @return
     */
    public FilterChoices getFilterChoices() {
        return filterChoices;
    }

    public FilterRequestI getPreviousFilterRequest() {
        FilterRequestI ret = previousFilterRequest;
        if (ret == null) {
            //never return null - make an empty one. Null makes the code in JSP too unwieldy
            ret = new FilterRequest(FilterRequestI.Mode.NoOp);
        }
        return ret;
    }

    /**
     * Returns the disease type, e.g. GBM
     *
     * @return
     */
    public String getDiseaseType() {
        return damModel.getDiseaseType();
    }

    /**
     * Returns the total number of columns.
     *
     * @return
     */
    public int getTotalColumns() {
        return damModel.getTotalColumns();
    }

    /**
     * Returns the total number of batches.
     *
     * @return
     */
    public int getTotalBatches() {
        return damModel.getTotalBatches();
    }

    /**
     * Returns the id for a batch header, by index
     *
     * @param idx
     * @return
     */
    public String getBatchHeaderId(int idx) {
        return damModel.getBatchHeader(idx).getId();
    }

    /**
     * Returns the rowspan for a specified batch header.
     *
     * @param headerId
     * @return
     */
    public int getBatchHeaderRowSpan(String headerId) {
        return damModel.getHeaderRowSpan(headerId);
    }

    /**
     * Returns the total number of columns for a specified category.
     *
     * @param cat
     * @return
     */
    public int getColumnCount(Header.HeaderCategory cat) {
        return damModel.getHeadersForCategory(cat).size();
    }

    /**
     * Returns a column header by index for a specified category.
     *
     * @param cat
     * @param idx
     * @return
     */
    public String getColumnHeaderId(Header.HeaderCategory cat, int idx) {
        return damModel.getHeadersForCategory(cat).get(idx).getId();
    }

    /**
     * Returns the colspan of a column header, for html layout purposes.
     *
     * @param headerId
     * @return
     */
    public int getColumnHeaderColSpan(String headerId) {
        return damModel.getHeaderColSpan(headerId);
    }

    /**
     * Returns the total number of child headers for a header.
     *
     * @param headerId
     * @return
     */
    public int getChildHeaderCount(String headerId) {
        return damModel.getHeadersForHeader(damModel.getHeaderById(headerId)).size();
    }

    /**
     * Returns a child header for a header, by index
     *
     * @param headerId
     * @param idx
     * @return
     */
    public String getChildHeaderId(String headerId, int idx) {
        return damModel.getHeadersForHeader(damModel.getHeaderById(headerId)).get(idx).getId();
    }

    /**
     * Returns a header's "name" - for a row header, it will be the batch or sample.
     * For a column header, it will be a numeric code which we can use to look up
     * the descriptive name.
     *
     * @param headerId
     * @return
     */
    public String getHeaderName(String headerId) {
        return damModel.getHeaderById(headerId).getName();
    }

    /**
     * Get the actual Header object for the id.
     *
     * @param headerId
     * @return
     */
    public Header getHeader(String headerId) {
        return damModel.getHeaderById(headerId);
    }

    /**
     * Returns a child cell for a header, by index
     *
     * @param headerId
     * @param idx
     * @return
     */
    public String getChildCellId(String headerId, int idx) {
        final Header header = damModel.getHeaderById(headerId);
        final List<Cell> cells = damModel.getCellsForHeader(header);
        if (cells != null) {
            final Cell cell = cells.get(idx);
            if (cell != null) {
                return cell.getId();
            }
        }
        return null;
    }

    /**
     * Returns the letter code for cell availability
     *
     * @param cellId
     * @return
     */
    public String getCellAvailability(String cellId) {
        return damModel.getCell(cellId).getAvailability();
    }

    /**
     * Returns true if the header contains protected information (column headers only)
     *
     * @param headerId
     * @return
     */
    public boolean isHeaderProtected(String headerId) {
        return damModel.getHeaderById(headerId).isProtected();
    }

    /**
     * Processes a page-level request for selecting, such as header-based selection
     * or clearing all selections.
     *
     * @param selectionRequest
     */
    public void setSelection(SelectionRequest selectionRequest) {
        selectionModel.setSelection(selectionRequest);
        // save the request
        previousSelectionRequest = selectionRequest;
    }

    /**
     * @return the user's last selection request.  may be null.
     */
    public SelectionRequest getPreviousSelectionRequest() {
        return this.previousSelectionRequest;
    }

    /**
     * Returns true if the specified header is in selected state
     *
     * @param headerId
     * @return
     */
    public boolean isHeaderSelected(String headerId) {
        return selectionModel.isHeaderSelected(headerId);
    }

    /**
     * Returns true if the user has selected the given cell
     *
     * @param cellId
     * @return
     */
    public boolean isCellSelected(String cellId) {
        return selectionModel.isCellSelected(cellId);
    }

    /**
     * Removes all user selection of cells.
     */
    public void unselectAll() {
        selectionModel.unselectAll();
    }

    /**
     * Returns all the selected datasets.
     *
     * @return
     */
    public List<DataSet> getSelectedDataSets() {
        return selectionModel.getSelectedDataSets();
    }

    /**
     * Returns the cell Ids of all user-selected cells
     * Must use java 1.4 syntax because JSP compiler we are using does not use java 1.5
     *
     * @return
     */
    public List getSelectedCellIds() {
        return selectionModel.getSelectedCellIds();
    }

    /**
     * Returns all the available color scheme names.
     *
     * @return
     */
    public List<String> getColorSchemeNames() {
        return DAMColorScheme.getColorSchemeNames();
    }

    /**
     * Returns the hex color and letter code for a cell
     * [0]: color
     * [1]: letter code
     *
     * @param cellId
     * @return
     */
    public String[] getCellColorAndLetter(String cellId) {
        String[] ret = new String[2];
        Cell cell = damModel.getCell(cellId);
        ret[0] = colorScheme.getHTMLColorForCell(cell);
        String letterCode = colorScheme.getLetterCodeForCell(cell);
        if (letterCode != "") {
            ret[1] = DAMResourceBundle.getMessage("legend.code." + letterCode, letterCode);
        } else {
            ret[1] = letterCode;
        }

        return ret;
    }

    /**
     * Returns legend information for the current color scheme.
     * [0]: letter code
     * [1]: hex color
     * [2]: description
     *
     * @return
     */
    public String[][] getLegend() {
        return colorScheme.getLegend();
    }

    //for testing only - not to be used by the application 
    public DAMStaticModel getStaticModel() {
        DAMStaticModel ret = null;
        if (damModel instanceof DAMStaticModel) {
            ret = (DAMStaticModel) damModel;
        } else {
            ret = (DAMStaticModel) damModel.getWrappedModel();
        }
        return ret;
    }

    public DAMFilterModel getFilterModel() {
        DAMFilterModel ret = null;
        if (damModel instanceof DAMFilterModel) {
            ret = (DAMFilterModel) damModel;
        }
        return ret;
    }

    public DAMSelectionModel getSelectionModel() {
        return this.selectionModel;
    }

    public DAMColorScheme getColorScheme() {
        return this.colorScheme;
    }
}
