/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3214 $
 */
public class IDFFastTest {

	private static final String SAMPLE_DIR = 
	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private final String fileLocation = SAMPLE_DIR + "qclive";
    private final String fileName = "idfTestFile.idf.txt";
    IDF idf;
    
    @Before
    public void setUp(){
    	idf = new IDF();    	
    }
    
    
    @Test
    public void testGetIDF() throws IOException {
        idf.initIDF(fileLocation, fileName);
        assertNotNull(idf.getIDF());
    }
    @Test
    public void testGetIDFColHeaders() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        assertNotNull(idf.getIDFColHeaders());
    }
    @Test
    public void testGetIDFByColNumberList() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        assertNotNull(idf.getIDFColByNumber(1));
    }
    @Test
    public void testGetIDFColValue() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        assertEquals("Laird", idf.getIDFColValueByColNumber(1, 5));
    }
    @Test
    public void testGetIDFColValueBadRow() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        assertNull(idf.getIDFColValueByColNumber(1, -1));
    }
    @Test
    public void testGetIDFColValueBadCol() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        assertNull(idf.getIDFColValueByColNumber(-1, 1));
    }
    @Test
    public void testGetIDFColNumberByName() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        assertEquals(4, idf.getColNumberByName("Experimental Factor Type Term Source REF"));
        //Example of how to check the values of 2 column values
        assertEquals(
                idf.getIDFColValueByColNumber(1, idf.getColNumberByName("Experimental Design Term Source REF")),
                idf.getIDFColValueByColNumber(1, idf.getColNumberByName("Experimental Factor Type Term Source REF")));
    }
    @Test
    public void testCompareTermSourceNames() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        //Get and build the term source name values array.
        List tsnList = idf.getAllIDFColValuesByColName("Term Source Name");
        assertEquals(3, tsnList.size());
        //Find all columns with Term Source REF in the name
        List colsToWorkWith = idf.getAllColsContainingString("Term Source REF");
        assertEquals(5, colsToWorkWith.size());
        int foundItemCount = 0;
        for (final Object aColsToWorkWith : colsToWorkWith) {
            final List values = (List) Arrays.asList(aColsToWorkWith).get(0);
            for (final Object value : values.subList(1, values.size())) {
                if (value.toString().trim().length() > 0 && tsnList.contains(value)) {
                    foundItemCount++;
                }
            }
            assertTrue(foundItemCount > 0);
        }
    }
    
    @Test
    public void testColNameFound() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        //Get and build the term source name values array.
        List tsnList = idf.getAllIDFColValuesByColName("Term Source Name");
        assertEquals(3, tsnList.size());
    }
    @Test
    public void testColNameFailed() throws IOException {
    	idf.initIDF(fileLocation, fileName);
        List tsnBadList = idf.getAllIDFColValuesByColName("Foo");
        assertEquals(0, tsnBadList.size());
    }    
}
