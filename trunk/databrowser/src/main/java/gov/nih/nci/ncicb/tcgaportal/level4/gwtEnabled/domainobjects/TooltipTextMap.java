package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Jun 8, 2009
 * Time: 5:22:10 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Defines tooltip map as a singleton to simplify access
 */
public class TooltipTextMap extends HashMap<String, String> {

    private static TooltipTextMap instance;

    public static void setInstance(TooltipTextMap ttm) {   //kosher?
        instance = ttm;
    }

    public static TooltipTextMap getInstance() {
        if (instance == null) {
            instance = new TooltipTextMap(); //for testing
        }
        return instance;
    }

}
