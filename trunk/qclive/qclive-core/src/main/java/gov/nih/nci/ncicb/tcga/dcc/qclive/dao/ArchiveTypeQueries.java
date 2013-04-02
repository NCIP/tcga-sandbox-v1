/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import java.util.List;

/**
 * Query interface for archive type
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ArchiveTypeQueries {

    public List<String> getAllArchiveTypes();

    public Integer getArchiveTypeId( String type );

    public String getArchiveType( Integer id );

    public boolean isValidArchiveType( String type );

    public Integer getArchiveTypeDataLevel( String type );
}
