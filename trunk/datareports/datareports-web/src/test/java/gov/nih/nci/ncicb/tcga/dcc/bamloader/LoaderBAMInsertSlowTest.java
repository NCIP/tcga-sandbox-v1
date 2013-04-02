/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.DatareportDBUnitConfig;

import java.io.File;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

/**
 * Slow test for the insert of the BAM loader
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LoaderBAMInsertSlowTest extends DatareportDBUnitConfig {

//    LookupForBAMImpl lookup = new LookupForBAMImpl();
//    LoaderBAM impl = new LoaderBAM();
//    DataFieldMaxValueIncrementer fileBAMSequence = new OracleSequenceMaxValueIncrementer(getDataSource(),"BAM_FILE_SEQ");
//    private static final String SAMPLE_DIR =
//    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
//    private static final String BAM_FILE_PATH = SAMPLE_DIR + "miniBAMtelemetryTest.txt";
//    BAMLoaderConstants props = new BAMLoaderConstants();
//
//    public void testLoadData() throws Exception {
//        props.setBAMFilePath(BAM_FILE_PATH);
//        impl.setDataSource(getDataSource());
//        impl.setFileBAMSequence(fileBAMSequence);
//        lookup.setDataSource(getDataSource());
//        lookup.setupQueries();
//        impl.setLookupForBAM(lookup);
//        impl.start();
//        assertEquals(6,impl.getJdbcTemplate().queryForInt("SELECT count(*) FROM BAM_FILE"));
//    }
//
}//End of Class
