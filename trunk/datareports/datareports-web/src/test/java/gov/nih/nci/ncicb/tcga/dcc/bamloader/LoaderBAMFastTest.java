/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.bamloader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for the BAM Loader
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class LoaderBAMFastTest {

    private Mockery context;
    private LoaderBAM service;
    private LookupForBAM dao;
    private BAMLoaderConstants props;
    private static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String EXCEL_PATH = SAMPLE_DIR + "miniBAMtelemetryTest.txt";

    @Before
    public void before() throws Exception {
        props = new BAMLoaderConstants();
        context = new JUnit4Mockery();
        dao = context.mock(LookupForBAM.class);
        service = new LoaderBAM();
        Field daoServiceField = service.getClass().getDeclaredField("lookupForBAM");
        daoServiceField.setAccessible(true);
        daoServiceField.set(service, dao);
        props.setBAMFilePath(EXCEL_PATH);
    }

    @Test
    public void testReadBAMData() throws Exception {
        context.checking(new Expectations() {{
            allowing(dao).getAliquots();
            will(returnValue(mockAliquots()));
            allowing(dao).getCenters();
            will(returnValue(mockCenters()));
            allowing(dao).getDatatypeBAMs();
            will(returnValue(mockDatatypeBAMs()));
            allowing(dao).getDiseases();
            will(returnValue(mockDiseases()));
        }});
        service.readTextFile();
        List<BAMFile> bamList = service.fileBAMs;
        assertNotNull(bamList);
        assertEquals(6, bamList.size());
        assertEquals(new Integer(12), bamList.get(0).getCenterId());
        assertEquals(new Long(70), bamList.get(0).getBiospecimenId());
        assertEquals(new Long(81435540), bamList.get(1).getFileSizeBAM());
        assertEquals("TCGA-AA-3858-01A-01W-0900-09_IlluminaGA-DNASeq_exome.bam", bamList.get(2).getFileNameBAM());
        assertEquals("TCGA-AA-3664-01A-01W-0900-09_IlluminaGA-DNASeq_exome.bam", bamList.get(4).getFileNameBAM());
        assertEquals(new Integer(2), bamList.get(4).getDiseaseId());
        assertEquals(new Integer(10), bamList.get(3).getDatatypeBAMId());
        assertEquals(new Integer(12), bamList.get(3).getCenterId());
    }


    private List<Tumor> mockDiseases() {
        return new LinkedList<Tumor>(){{
            add(new Tumor(1,"GBM"));
            add(new Tumor(4, "LUAD"));
            add(new Tumor(3, "OV"));
        }};
    }

    private List<CenterShort> mockCenters() {
        return new LinkedList<CenterShort>() {{
            add(new CenterShort(3,"CGCC","BI"));
            add(new CenterShort(10,"GSC","WUSM"));
        }};
    }

    private List<BAMDatatype> mockDatatypeBAMs() {
        return new LinkedList<BAMDatatype>() {{
            add(new BAMDatatype(1, "454","DNA","Exome"));
            add(new BAMDatatype(2, "mirna","RNA","miRNA"));
        }};
    }

    private List<AliquotShort> mockAliquots() {
        return new LinkedList<AliquotShort>() {{
            add(new AliquotShort(1L, "TCGA-01-0621-01A-01T-0364-07"));
            add(new AliquotShort(2L, "TCGA-59-2362-10A-01D-0704-01"));
            add(new AliquotShort(3L, "TCGA-01-0623-01A-01T-0364-07"));
            add(new AliquotShort(55L, "TCGA-01-0655-01A-01T-0364-07"));
            add(new AliquotShort(66L, "TCGA-01-0666-01A-01T-0364-07"));
            add(new AliquotShort(70L, "TCGA-01-0138-01A-01R-0231-06"));
        }};
    }


}//End of Class
