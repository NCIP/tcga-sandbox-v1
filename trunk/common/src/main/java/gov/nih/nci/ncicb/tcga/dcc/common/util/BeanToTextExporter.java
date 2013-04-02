/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.getExportString;

/**
 * Small class that define a BeanToTextExporter method.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class BeanToTextExporter {

    protected static final Log logger = LogFactory.getLog(BeanToTextExporter.class);

    public static String beanListToText(
            final String type, final Writer out,
            final Map<String, String> columns,
            final List data, final DateFormat dateFormat) {
        CSVWriter writer = new CSVWriter(out);
        if ("tab".equals(type)) {
            writer = new CSVWriter(out, '\t', CSVWriter.NO_QUOTE_CHARACTER);
        }
        List<String> cols = new LinkedList<String>();
        try {
            //Writing the column headers
            for (Map.Entry<String, String> e : columns.entrySet()) {
                cols.add(e.getValue());
            }
            writer.writeNext((String[]) cols.toArray(new String[columns.size()]));
            cols.clear();
            //Writing the data
            if (data != null) {
                for (Object o : data) {
                    for (Map.Entry<String, String> e : columns.entrySet()) {
                        final Object obj = getAndInvokeGetter(o, e.getKey());
                        String value = getExportString(obj, dateFormat);

                        if ("tab".equals(type)) {
                            value = value.replace("\t", "   ");
                        }
                        cols.add(value);
                    }
                    writer.writeNext((String[]) cols.toArray(new String[columns.size()]));
                    cols.clear();
                }
            }
            writer.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
            return "An error occurred.";
        }
        return out.toString();
    }

    /**
     * If the propertyName is in the format a.b.c then first o.getA() will be called, and the result
     * will have getB() called and the result of that will have getC() called.  So that if you want to get
     * a property of a property in the object, you can do it.
     *
     * @param o            the object on which to begin invocation chain
     * @param propertyName the name of the getter to call -- may be period-separated to indicate multiple levels
     * @return the object from the final get
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object getAndInvokeGetter(final Object o, final String propertyName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String[] properties = propertyName.split("\\.");
        Object result = o;
        for (final String property : properties) {
            result = GetterMethod.getGetter(result.getClass(), property).invoke(result);
        }
        return result;
    }

}//End of Class
