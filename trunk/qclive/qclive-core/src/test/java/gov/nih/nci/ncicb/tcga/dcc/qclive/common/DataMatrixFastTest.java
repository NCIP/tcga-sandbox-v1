/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

/**
 * Test class for DataMatrix
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixFastTest {

    @Test
    public void testNames() {
        DataMatrix dmf = new DataMatrix();
        String[] names = new String[]{"barcode1", "barcode2", "barcode3"};
        dmf.setNames( names );
        String type = "Hybridization";
        dmf.setNameType( type );
        String[] theNames = dmf.getNames();
        assertEquals( names.length, theNames.length );
        assertEquals( type, dmf.getNameType() );
        for(int i = 0; i < names.length; i++) {
            assertEquals( names[i], theNames[i] );
            assertEquals( names[i], dmf.getName( i ) );
        }
    }

    @Test
    public void testQuantitationTypes() {
        DataMatrix dmf = new DataMatrix();
        String[] types = new String[]{"X", "Y", "X", "Y"};
        dmf.setQuantitationTypes( types );
        String[] theTypes = dmf.getQuantitationTypes();
        assertEquals( types.length, theTypes.length );
        for(int i = 0; i < types.length; i++) {
            assertEquals( types[i], theTypes[i] );
        }
    }

    @Test
    public void testDistinctQuantitationTypes() {
        DataMatrix dmf = new DataMatrix();
        String[] types = new String[]{"X", "Y", "X", "Y"};
        dmf.setQuantitationTypes( types );
        Set distinctTypes = dmf.getDistinctQuantitationTypes();
        assertEquals( 2, distinctTypes.size() );
        assertTrue( distinctTypes.contains( "X" ) );
        assertTrue( distinctTypes.contains( "Y" ) );
    }

    @Test
    public void testConstantTypes() {
        DataMatrix dmf = new DataMatrix();
        String[] constantTypes = new String[]{"Chr", "Pos"};
        dmf.setConstantTypes( constantTypes );
        String[] types = dmf.getConstantTypes();
        assertEquals( constantTypes.length, types.length );
        for(int i = 0; i < constantTypes.length; i++) {
            assertEquals( constantTypes[i], types[i] );
        }
    }
//    @Test
//    public void testConstants() {
//        DataMatrix dmf = new DataMatrix();
//        String[] consts = new String[]{"2", "5", "13"};
//        dmf.setConstants("Chr", consts);
//        String[] constants = dmf.getConstants("Chr");
//        assertEquals(consts.length, constants.length);
//        for (int i=0; i<consts.length; i++) {
//            assertEquals(consts[i], constants[i]);
//        }
//    }
}
