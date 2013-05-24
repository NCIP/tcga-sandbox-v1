/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamContext;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BAMFileQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;


/**
 * Test class for BAM loader.
 *
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BAMLoaderFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread().getContextClassLoader().getResource("samples")
            .getPath();
    private static final String BAM_LOADER_DIR = SAMPLES_DIR + File.separator + "bamloader" + File.separator;
    private static final String cgHubURL = "cgHubURL";
    private BAMParser mockBAMParser;
    private BAMValidator mockBamValidator;
    private BAMLoader bamLoader;
    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private BAMFileQueries mockBamFileQueries;

    @Before
    public void setUp() throws Exception {
        mockBAMParser = context.mock(BAMParser.class);
        mockBamValidator = context.mock(BAMValidator.class);
        bamLoader = new BAMLoader() {
            protected String downloadBamXmlFile(final String urlParam) throws IOException {
                return BAM_LOADER_DIR + "CGHubSample.xml";
            }
        };
        mockBamFileQueries = context.mock(BAMFileQueries.class);
        bamLoader.setBamFileQueries(mockBamFileQueries);
        bamLoader.setBamParser(mockBAMParser);
        bamLoader.setCgHubUrl(cgHubURL);
        bamLoader.setBamXmlFilePath(BAM_LOADER_DIR);
        bamLoader.setBamValidator(mockBamValidator);
    }

    @Test
    public void loadBAMData() throws Exception {
        BamContext bamContext = new BamContext();
        context.checking(new Expectations() {{
            allowing(mockBamFileQueries).getLatestUploadedDate();
            will(returnValue(new Date()));
            one(mockBAMParser).parse(with(any(String.class)));
            one(mockBamValidator).validate(with(any(BamXmlResultSet.class)), with(any(BamContext.class)));
            one(mockBamFileQueries).store(with(any(BamXmlResultSet.class)));
        }});
        bamLoader.loadBAMData(bamContext);
    }

}//End of Class
