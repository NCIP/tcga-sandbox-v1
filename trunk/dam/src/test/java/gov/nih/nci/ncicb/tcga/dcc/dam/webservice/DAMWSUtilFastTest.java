/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

public class DAMWSUtilFastTest {

    List<DataFile> dataFiles;
    List<DataFile> dataFiles2;
    DAMWSUtil damWsUtil;

    @Before
    public void before() throws Exception {

        dataFiles = new LinkedList<DataFile>();
        dataFiles2 = new LinkedList<DataFile>();
        DataFile df = new DataFileLevelThree();
        df.setCenterId("1");
        df.setPlatformId("2");
        df.setPlatformTypeId("3");
        df.setSamples(Arrays.asList("TCGA-01-0001-01"));
        df.setSize(69L);
        dataFiles.add(df);
        DataFileMetadata meta = new DataFileMetadata();
        meta.setCenterId("4");
        meta.setPlatformId("5");
        meta.setPlatformTypeId("6");
        meta.setSamples(Arrays.asList("TCGA-01-0001-01"));
        meta.setSize(999L);
        meta.setProtected(false);
        dataFiles.add(meta);
        DataFile l1 = new DataFileLevelOne();
        l1.setCenterId("7");
        l1.setPlatformId("8");
        l1.setPlatformTypeId("9");
        l1.setSamples(Arrays.asList("TCGA-01-0001-01"));
        l1.setSize(2988435457L);
        l1.setProtected(true);
        dataFiles2.add(l1);
        damWsUtil = new DAMWSUtil();
    }

    @Test
    public void testRemoveMetadata() {
           List<DataFile> noMetadata = damWsUtil.removeMetadata(dataFiles);
          assertNotNull(noMetadata);
          assertEquals(1,noMetadata.size());
          assertTrue(noMetadata.get(0) instanceof DataFileLevelThree);
    }

    @Test
    public void testIsDownloadingProtectedTrue() throws Exception {
            assertTrue(damWsUtil.isDownloadingProtected(dataFiles2));
    }

    @Test
    public void testIsDownloadingProtectedFalse() throws Exception {
           assertFalse(damWsUtil.isDownloadingProtected(dataFiles));
    }

    @Test(expected= WebApplicationException.class)
    public void testCheckTotalSizeTooBig() throws Exception {
           damWsUtil.checkTotalSize(dataFiles2,2);
    }

    @Test
    public void testCheckTotalSizeOK() throws Exception {
           assertEquals(1068L,damWsUtil.checkTotalSize(dataFiles,2));
    }

    @Test
    public void testHasValueIgnoreCase() throws Exception {
        Map map = new HashMap(){{
            put(1,"jedi");
            put(2,"knight");
        }};
       assertTrue(damWsUtil.hasValueIgnoreCase(map,"jedi"));
       assertTrue(damWsUtil.hasValueIgnoreCase(map,"JedI"));
       assertTrue(damWsUtil.hasValueIgnoreCase(map,"kNiGht"));
       assertFalse(damWsUtil.hasValueIgnoreCase(map,"SIth"));
    }
}//End of Class
