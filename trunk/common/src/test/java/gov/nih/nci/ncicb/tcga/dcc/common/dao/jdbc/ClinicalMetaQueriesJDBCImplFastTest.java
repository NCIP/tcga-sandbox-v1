/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Fast tests for ClinicalMetaQueriesJDBCImpl
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalMetaQueriesJDBCImplFastTest {

    private ClinicalMetaQueries.ClinicalFile fakeFile;
    private ClinicalMetaQueriesJDBCImpl clinicalMetaQueries;

    @Before
    public void setup() {
        fakeFile = new ClinicalMetaQueries.ClinicalFile();
        clinicalMetaQueries = new ClinicalMetaQueriesJDBCImpl();
    }

    @Test
    public void testParseJoinClauses() {
        // final Set<String> tablesToQuery, final Set<String> tableJoins, final Set<String> aliasedTables
        final Set<String> tablesToQuery = new HashSet<String>();
        tablesToQuery.add("APPLE");
        tablesToQuery.add("BANANA");
        tablesToQuery.add("eggplant as obergine");
        final Set<String> tableJoins = new HashSet<String>();
        tableJoins.add("apple.id=banana.id AND apple.id=fruit_bowl.fruit_id");
        tableJoins.add("obergine.id=fruit_bowl.fruit_id and apple.id=banana.id");
        final Set<String> aliasedTables = new HashSet<String>();
        aliasedTables.add("obergine");

        Set<String> joinClauses = clinicalMetaQueries.parseJoinClauses(tablesToQuery, tableJoins, aliasedTables);
        // fruit_bowl should have been added, but not obergine because that's an alias
        assertTrue("fruit_bowl was not added to the tables to query set!", tablesToQuery.contains("FRUIT_BOWL"));
        assertEquals(4, tablesToQuery.size());
        assertEquals(3, joinClauses.size());
        assertTrue(joinClauses.contains("apple.id=banana.id"));
        assertTrue(joinClauses.contains("apple.id=fruit_bowl.fruit_id"));
        assertTrue(joinClauses.contains("obergine.id=fruit_bowl.fruit_id"));
    }


    private ClinicalMetaQueries.ClinicalFileColumn makeColumn(final String table, final Integer columnNumber, final String join) {
        ClinicalMetaQueries.ClinicalFileColumn column = new ClinicalMetaQueries.ClinicalFileColumn();
        column.columnName = table + "COLUMN" + columnNumber;
        column.tableName = table;
        column.tableColumnName = "COL" + columnNumber;
        column.joinClause = join;
        column.xsdElementId = columnNumber;
        column.tableIdColumn = table + "_ID";
        return column;
    }
}
