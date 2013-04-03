package gov.nih.nci.ncicb.tcgaportal.level4.util;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.TooltipTextMap;
import junit.framework.TestCase;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Sep 11, 2009
 * Time: 1:19:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class TooltipContentFastTest extends TestCase {

	private static final String filepath = 
			Thread.currentThread().getContextClassLoader().getResource("level4_tooltips.txt").getPath();

    //populates map, makes sure that all consts have a tooltip associated
    public void testTooltipConstants() throws MalformedURLException {
        String path = filepath;
        if (path.startsWith("C:")) {
            path = path.replace("C:", "");
        }
        TooltipTextMap ttmap = TooltipContentHelper.createTooltipMap(new URL("file://" + path));
        assertNotNull(ttmap);
        assertTrue(ttmap.size() > 0);

        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_GENESYMBOL));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_BIOCARTA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_MIRNASYMBOL));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_METHYLATIONSYMBOL));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNV));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_LOCATION));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNGENE));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPGENE));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_MUTATION));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CORRELATION));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_METHYLATION));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECN));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECNMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXP));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXPMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMETH));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMUT));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECORRELATIONS));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCN));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCNMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXP));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXPMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMETH));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMUT));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCN));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_PATHWAYCNMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXP));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXPMIRNA));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMETH));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMUT));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCORRELATIONS));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PIVOTFROMGENE));
        assertNotNull(ttmap.get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PIVOTFROMPATIENT));
    }

    //make sure that everything in the tooltip file is mapped to a constant
    public void testTooltipTopics() throws MalformedURLException {
        String path = filepath;
        if (path.startsWith("C:")) {
            path = path.replace("C:", "");
        }
        TooltipTextMap ttmap = TooltipContentHelper.createTooltipMap(new URL("file://" + path));
        assertNotNull(ttmap);
        assertTrue(ttmap.size() > 0);

        Map<String,String> consts = new HashMap<String,String>();
        String x = "x";
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_GENESYMBOL, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_BIOCARTA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_MIRNASYMBOL, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_METHYLATIONSYMBOL, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNV, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_LOCATION, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNGENE, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPGENE, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_MUTATION, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CORRELATION, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_METHYLATION, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATIENTID, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATHWAYSIGNIF, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_DOWNLOADDATAFILES, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPORTDATA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_GENECOPYCHECKEDTOSEARCH, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATIENTCOPYCHECKEDTOSEARCH, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATHWAYNAME, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECN, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECNMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXP, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEEXPMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMETH, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENEMUT, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENECORRELATIONS, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCN, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTCNMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXP, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTEXPMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMETH, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTMUT, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCN, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_PATHWAYCNMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXP, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYEXPMIRNA, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMETH, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYMUT, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATHWAYCORRELATIONS, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_DISEASE, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_GENES, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_PATIENTS, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_FILTER_ADDBUTTON, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PIVOTFROMGENE, x);
        consts.put(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PIVOTFROMPATIENT, x);
        
        for (String key : ttmap.keySet()) {
            if (!key.endsWith("_widget")) {
                assertNotNull(key + " not found", consts.get(key));
            }
        }
    }

}
