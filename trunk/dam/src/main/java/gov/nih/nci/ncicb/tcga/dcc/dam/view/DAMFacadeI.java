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
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nanans
 * Date: Sep 9, 2008
 * Time: 1:15:13 PM
 * This interface was created for testing purposes.
 */
public interface DAMFacadeI {

    void setFilter( FilterRequestI filterRequest );

    void setColorSchemeName( String colorSchemeName );

    String getColorSchemeName();

    FilterChoices getFilterChoices();

    FilterRequestI getPreviousFilterRequest();

    String getDiseaseType();

    int getTotalColumns();

    int getTotalBatches();

    String getBatchHeaderId( int idx );

    int getBatchHeaderRowSpan( String headerId );

    int getColumnCount( Header.HeaderCategory cat );

    String getColumnHeaderId( Header.HeaderCategory cat, int idx );

    int getColumnHeaderColSpan( String headerId );

    int getChildHeaderCount( String headerId );

    String getChildHeaderId( String headerId, int idx );

    String getHeaderName( String headerId );

    Header getHeader( String headerId );

    String getChildCellId( String headerId, int idx );

    String getCellAvailability( String cellId );

    boolean isHeaderProtected( String headerId );

    void setSelection( SelectionRequest selectionRequest );

    SelectionRequest getPreviousSelectionRequest();

    boolean isHeaderSelected( String headerId );

    boolean isCellSelected( String cellId );

    void unselectAll();

    List<DataSet> getSelectedDataSets();

    List getSelectedCellIds();

    List<String> getColorSchemeNames();

    String[] getCellColorAndLetter( String cellId );

    String[][] getLegend();//for testing only - not to be used by the application

    DAMStaticModel getStaticModel();

    DAMFilterModel getFilterModel();

    DAMSelectionModel getSelectionModel();

    DAMColorScheme getColorScheme();
}
