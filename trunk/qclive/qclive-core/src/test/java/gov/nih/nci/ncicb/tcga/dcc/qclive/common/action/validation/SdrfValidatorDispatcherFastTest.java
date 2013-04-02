/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
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
 * Test for SdrfValidatorDispatcher.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class SdrfValidatorDispatcherFastTest {

    private Mockery context;
    private SdrfValidatorDispatcher sdrfValidatorDispatcher;
    private Archive archive;
    private QcContext qcContext;
    private Processor<Archive, Boolean> mockRnaSeqSdrfValidator, mockMiRnaSeqSdrfValidator, mockArraySdrfValidator;


    @Before
    public void setUp() {
        sdrfValidatorDispatcher = new SdrfValidatorDispatcher();
        context = new JUnit4Mockery();
        // can't mock using generics but it still seems to work...
        mockRnaSeqSdrfValidator = context.mock(Processor.class, "RNASeqSdrfValidator");
        mockMiRnaSeqSdrfValidator = context.mock(Processor.class, "miRNASeqSdrfValidator");
        mockArraySdrfValidator = context.mock(Processor.class, "ArraySdrfValidator");
        sdrfValidatorDispatcher.setRnaSeqSdrfValidator(mockRnaSeqSdrfValidator);
        sdrfValidatorDispatcher.setArraySdrfValidator(mockArraySdrfValidator);
        sdrfValidatorDispatcher.setMiRnaSeqSdrfValidator(mockMiRnaSeqSdrfValidator);
        archive = new Archive();
        qcContext = new QcContext();
    }

    @Test
    public void testNoPlatform() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockArraySdrfValidator).execute(archive, qcContext);
            will(returnValue(true));
        }});
        assertTrue(sdrfValidatorDispatcher.execute(archive, qcContext));
    }

    @Test
    public void testRNASeqPlatform() throws Processor.ProcessorException {
        archive.setPlatform("thisIsAnRNASeqPlatform");
        context.checking(new Expectations() {{
            one(mockRnaSeqSdrfValidator).execute(archive, qcContext);
            will(returnValue(true));
        }});
        assertTrue(sdrfValidatorDispatcher.execute(archive, qcContext));
    }

    @Test
    public void testMiRNASeqPlatform() throws Processor.ProcessorException {

        archive.setPlatform("thisIsAmiRNASeqPlatform");

        context.checking(new Expectations() {{
            one(mockMiRnaSeqSdrfValidator).execute(archive, qcContext);
            will(returnValue(true));
        }});

        assertTrue(sdrfValidatorDispatcher.execute(archive, qcContext));
    }

    @Test
    public void testArrayPlatform() throws Processor.ProcessorException {
        archive.setPlatform("Genome_Wide_SNP_6");
        context.checking(new Expectations() {{
            one(mockArraySdrfValidator).execute(archive, qcContext);
            will(returnValue(true));
        }});
        assertTrue(sdrfValidatorDispatcher.execute(archive, qcContext));
    }

    @Test
    public void testValidationFailed() throws Processor.ProcessorException {
        archive.setPlatform("hi");
        context.checking(new Expectations() {{
            one(mockArraySdrfValidator).execute(archive, qcContext);
            will(returnValue(false));
        }});
        assertFalse(sdrfValidatorDispatcher.execute(archive, qcContext));
    }
}
