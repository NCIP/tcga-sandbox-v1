package gov.nih.nci.ncicb.tcgaportal.pathway.util;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import gov.nih.nci.ncicb.tcgaportal.level4.util.Locations;
import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.transcoder.TranscoderException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test class for PathwayDiagramHander
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Ignore
public class PathwayDiagramHandlerFastTest {

    public static final String PORTAL_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath();

    private static final String referenceJpgFile = "h_atmPathway.jpg";

    private static final String pathwayName = "h_atmPathway";

    private PathwayDiagramHandlerImpl pdh;

    @Before
    public void setUp() {
        pdh = new PathwayDiagramHandlerImpl();
        pdh.setSvgFetcher(new MockPathwaySVGFetcher());
        pdh.setTempFileLocation(PORTAL_FOLDER);

        Locations locUtil = new Locations();
        locUtil.setTempFileLocation(PORTAL_FOLDER);
        pdh.setLocationsUtility(locUtil);
    }

    @Test
    public void testJPEGImageWriterFound() {
        assertNotNull(ImageWriterRegistry.getInstance().getWriterFor("image/jpeg"));
    }
    
    @Test
    public void testFetchAndTranslateSVG() throws IOException, TranscoderException, IPathwayQueries.PathwayQueriesException {
        //fetch SVG and translate into test1.jpg
        pdh.fetchAndTranslateSVG(pathwayName, null, "test1.jpg");

        //compare to reference jpg
        FileComparer.compareFiles(PORTAL_FOLDER + referenceJpgFile, PORTAL_FOLDER + "test1.jpg");

        //schedule for deletion
        pdh.planDeletionOfImage("test1.jpg");

        //wait till it's deleted
        try {
            Thread.sleep(PathwayDiagramHandler.WAITTODELETE_MILLIS + 1000);
        } catch (InterruptedException e) {
        }

        //make sure it's gone
        assertFalse((new File(PORTAL_FOLDER + "test1.jpg")).exists());
    }

}
