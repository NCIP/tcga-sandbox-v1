package gov.nih.nci.ncicb.tcgaportal.level4;

import com.google.gwt.junit.tools.GWTTestSuite;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.PagingWidgetFastTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for GWT client code.  Add new test classes here to run them all at once and save
 * time on setting up GWT test environment.  Note: this is in this package because GWT complains if it is
 * within the client class.  Something about JRE Emulation not working for GWTTestSuite.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class AnomalySearchTestSuite extends GWTTestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PagingWidgetFastTest.class);
        suite.addTestSuite(RegionWidgetFastTest.class);
        suite.addTestSuite(NonMutationAnomalyWidgetFastTest.class);
        suite.addTestSuite(MutationWidgetFastTest.class);
        suite.addTestSuite(MutationDisplayFastTest.class);
        suite.addTestSuite(CorrelationWidgetFastTest.class);
        suite.addTestSuite(CorrelationDisplayFastTest.class);
        suite.addTestSuite(CopyNumberWidgetFastTest.class);
        suite.addTestSuite(CopyNumberDisplayFastTest.class);
        return suite;
    }
}
