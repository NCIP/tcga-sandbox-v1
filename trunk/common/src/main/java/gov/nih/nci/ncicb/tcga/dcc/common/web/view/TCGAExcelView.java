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
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.web.servlet.view.document.AbstractJExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_COLUMN_HEADERS;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_DATA;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_DATE_FORMAT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_FILE_NAME;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.ATTRIBUTE_TITLE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils.getExportString;

/**
 * This view defines an excel view for exporting data to excel
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class TCGAExcelView extends AbstractJExcelView {

    /**
     * Spring method to define: build the Excel view, given the specified model.
     * This takes few given variables from the model:
     * fileName: the name of the file to export
     * title: title of the excel sheet
     * cols: a map that contains pairs of bean attributes key and column display values.
     * The key of the pair is the bean attribute associated with the column
     * The value of the pair is the display name of the columns.
     * Example: Class MyBean has an attribute foo. The display text columns in the export report
     * associated with foo is "This is foo". A correct columns map should have a pair like this:
     * map.put("foo","This is foo");
     * data: a list of bean representing the data to export
     */
    @Override
    protected void buildExcelDocument(
            final Map model, final WritableWorkbook wb, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        //Set up
        String fileName = (String) model.get(ATTRIBUTE_FILE_NAME);
        String title = (String) model.get(ATTRIBUTE_TITLE);
        Map<String, String> columns = (Map<String, String>) model.get(ATTRIBUTE_COLUMN_HEADERS);
        List<Object> data = (List<Object>) model.get(ATTRIBUTE_DATA);
        DateFormat dateFormat = (DateFormat) model.get(ATTRIBUTE_DATE_FORMAT);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        WritableSheet sheet = wb.createSheet(title, 0);

        //Writing the columns headers
        int i = 0;
        for (Map.Entry<String, String> e : columns.entrySet()) {
            sheet.addCell(new Label(i, 0, e.getValue()));
            i++;
        }

        //Writing the data
        if (data != null) {
            for (int j = 0; j < data.size(); j++) {
                int k = 0;
                for (Map.Entry<String, String> e : columns.entrySet()) {
                    final Object obj = data.get((j));
                    final Object o = BeanToTextExporter.getAndInvokeGetter(obj, e.getKey());
                    String value = getExportString(o, dateFormat);
                    sheet.addCell(new Label(k, (j + 1), value));
                    k++;
                }
            }
        }
    }
}//End of Class
