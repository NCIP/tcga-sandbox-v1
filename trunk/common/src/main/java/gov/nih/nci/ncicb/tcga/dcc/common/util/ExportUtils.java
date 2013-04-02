/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Marker class for TCGA export views.  Has static constants.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExportUtils {
    public static final String ATTRIBUTE_EXPORT_TYPE = "exportType";
    public static final String ATTRIBUTE_FILE_NAME = "fileName";
    public static final String ATTRIBUTE_COLUMN_HEADERS = "cols";
    public static final String ATTRIBUTE_DATA = "data";
    public static final String ATTRIBUTE_DATE_FORMAT = "dateFormat";
    public static final String ATTRIBUTE_TITLE = "title";

    public static String getExportString(final Object obj, final DateFormat dateFormat) {
        String value = "";
        if (obj != null) {
            if (obj instanceof Date && dateFormat != null) {
                value = dateFormat.format(obj);
            } else if (obj instanceof Collection) {
                StringBuilder str = new StringBuilder();
                Iterator it = ((Collection)obj).iterator();
                while(it.hasNext()) {
                    str.append(it.next().toString());
                    if (it.hasNext()) {
                        str.append("; ");
                    }
                }
                value = str.toString();
            } else {
                value = obj.toString();
            }

        }
        value = value.replace("\n", " ");
        return value;
    }
}
