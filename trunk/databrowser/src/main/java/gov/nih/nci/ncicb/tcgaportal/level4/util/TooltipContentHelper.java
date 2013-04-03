/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.util;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.TooltipTextMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

/**
 * Populates a TooltipTextMap from a tab delimited file.
 */
public class TooltipContentHelper {

    public static TooltipTextMap createTooltipMap(URL tooltipURL) {
        try {
            TooltipTextMap ttmap = new TooltipTextMap();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            tooltipURL.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.trim().startsWith("#")) { //skip comments
                    String[] line = inputLine.split("\\t");
                    if (line.length > 1) {
                        ttmap.put(line[0], line[1]);
                    }
                }
            }
            in.close();
            return ttmap;
        } catch (IOException e) {
            e.printStackTrace();  //todo  replace with logger
            //return an empty map - the error isn't serious enough to terminate the application's,
            //user session and it will be recorded in the log
            return new TooltipTextMap();
        }
    }

}
