/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * Parent class for DbGap test.  Has shared methods for setting up tests.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DbGapTestParent {

        protected void addFileColumn(final ClinicalMetaQueries.ClinicalFile clinicalFile, final String name,
                                     final String descr, final boolean hasNonNulls, final String... values) {
            ClinicalMetaQueries.ClinicalFileColumn column = new ClinicalMetaQueries.ClinicalFileColumn();
            column.columnName = name;
            column.description = descr;
            column.tableName = "table";
            column.tableColumnName = name;
            column.type = "string";
            column.hasNonNullData = hasNonNulls;
            if (values.length > 0) {
                column.values = Arrays.asList(values);
            }
            if (clinicalFile.columns == null) {
                clinicalFile.columns = new ArrayList<ClinicalMetaQueries.ClinicalFileColumn>();
            }
            clinicalFile.columns.add(column);
        }

    protected void addFileColumn(final ClinicalMetaQueries.ClinicalFile clinicalFile, final String name, final String descr, final String... values) {
        addFileColumn(clinicalFile, name, descr, true, values);
    }
}
