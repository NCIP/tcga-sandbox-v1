/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.web.view;

import gov.nih.nci.ncicb.tcga.dcc.common.util.BeanToTextExporter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_COLUMN_HEADERS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_DATE_FORMAT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_EXPORT_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_FILE_NAME;

/**
 * This view defines a text view for exporting data to csv, tab-delimited ...
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class TCGATextView extends AbstractView {


    /**
     * The content type for an text response
     */
    private static final String CONTENT_TYPE = "text/plain";

    /**
     * Default Constructor.
     * Sets the content type of the view to "text/plain".
     */
    public TCGATextView() {
        setContentType(CONTENT_TYPE);
    }

    /**
     * Spring method to define: Renders the Txt view, given the specified model.
     * This takes few given variables from the model:
     * exportType: string of the type of export: csv, tab(tab-delimited)
     * filName: the name of the file to export
     * cols: a map that contains pairs of bean attributes key and column display values.
     * The key of the pair is the bean attribute associated with the column
     * The value of the pair is the display name of the columns.
     * Example: Class MyBean has an attribute foo. The display text columns in the export report
     * associated with foo is "This is foo". A correct columns map should have a pair like this:
     * map.put("foo","This is foo");
     * data: a list of bean representing the data to export
     */
    protected final void renderMergedOutputModel(
            final Map model, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        try {
            String exportType = (String) model.get(ATTRIBUTE_EXPORT_TYPE);
            String fileName = (String) model.get(ATTRIBUTE_FILE_NAME);
            Map<String, String> columns = (Map<String, String>) model.get(ATTRIBUTE_COLUMN_HEADERS);
            List<Object> data = (List<Object>) model.get(ATTRIBUTE_DATA);
            DateFormat dateFormat = (DateFormat) model.get(ATTRIBUTE_DATE_FORMAT);
            response.setContentType(getContentType());
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            PrintWriter out = response.getWriter();
            BeanToTextExporter.beanListToText(exportType, out, columns, data, dateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

} //End of Class
