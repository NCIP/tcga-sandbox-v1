/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import java.util.Collection;
import java.util.List;

/**
 * Author: David Nassau
 * <p/>
 * Provides a common interface for DAMStaticModel and DAMFilterModel.
 */
public interface DAMModel {

    String getDiseaseType();

    Cell getCell( String id );

    Header getHeader( Header.HeaderCategory category, String name );

    List<Cell> getCellsForHeader( Header header );

    List<Header> getHeadersForHeader( Header header );

    List<Header> getHeadersForCategory( Header.HeaderCategory category );

    int getTotalBatches();

    Header getBatchHeader( int index );

    Header getHeaderById( String id );

    Collection<Cell> getAllCells();

    int getTotalColumns();

    int getHeaderColSpan( String headerId );

    int getHeaderRowSpan( String headerId );

    //for testing
    DAMModel getWrappedModel();
}
