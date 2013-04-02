/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TODO: INFO ABOUT CLASS
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DomainNameValidatorFastTest {

    private Mockery context = new JUnit4Mockery();
    private DomainNameValidator val = new DomainNameValidator();
    private final CenterQueries centerQueries = context.mock( CenterQueries.class );
    private final PlatformQueries platformQueries = context.mock( PlatformQueries.class );    

    @Before
    public void setup() {
        val.setCenterQueries( centerQueries );
        val.setPlatformQueries( platformQueries );
    }

    @Test
    public void testGood() throws Processor.ProcessorException {
        final Archive a = new Archive();
        QcContext qcContext = new QcContext();
        qcContext.setArchive( a );
        a.setDomainName( "broad.mit.edu" );
        a.setPlatform( "Genome_Wide_SNP_6" );
        final Platform platForm = new Platform();
        platForm.setCenterType("CGCC");
        context.checking( new Expectations() {{
            one( platformQueries ).getPlatformForName("Genome_Wide_SNP_6");
            will( returnValue(platForm));
            one( centerQueries ).findCenterId( "broad.mit.edu", "CGCC" );
            will( returnValue( 3 ) );
        }} );
        boolean isValid = val.execute( a, qcContext );
        assertTrue( isValid );
    }

    @Test
    public void testBad() throws Processor.ProcessorException {
        final Archive a = new Archive();
        QcContext qcContext = new QcContext();
        qcContext.setArchive( a );
        a.setDomainName( "pancakes" );
        a.setPlatform( "Genome_Wide_SNP_6" );        
        final Platform platForm = new Platform();
        platForm.setCenterType("CGCC");
        
        context.checking( new Expectations() {{
            one( platformQueries ).getPlatformForName("Genome_Wide_SNP_6");
            will( returnValue(platForm));            
            one( centerQueries ).findCenterId( "pancakes", "CGCC" );
            will( returnValue( null ) );
        }} );
        boolean isValid = val.execute( a, qcContext );
        assertFalse( isValid );
    }
}
