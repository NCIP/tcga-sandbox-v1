/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.web.view;

import gov.nih.nci.ncicb.tcga.dcc.common.util.BeanToTextExporter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
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
 *
 * This view defines an excel view for exporting data to excel using POI
 *
 * @author bertondl Last updated by: $Author$
 * @version $Rev$
 */
public class TCGAExcelSXSSView extends AbstractView {

	private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    /**
	 * Default Constructor.
	 * Sets the content type of the view to "application/vnd.ms-excel".
	 */
	public TCGAExcelSXSSView() {
		setContentType(CONTENT_TYPE);
	}

	protected boolean generatesDownloadContent() {
		return true;
	}

    protected void buildExcelDocument(
            final Map model, final SXSSFWorkbook workbook,
            final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        //Set up
        final String fileName = (String)model.get(ATTRIBUTE_FILE_NAME);
        final String title = (String)model.get(ATTRIBUTE_TITLE);
        final Map<String,String> columns = (Map<String,String>)model.get(ATTRIBUTE_COLUMN_HEADERS);
        final List<Object> data  = (List<Object>)model.get(ATTRIBUTE_DATA);
        final DateFormat dateFormat = (DateFormat) model.get(ATTRIBUTE_DATE_FORMAT);
        response.setHeader("Content-Disposition", "attachment; filename="+fileName);
        final Sheet sheet = workbook.createSheet(title);

        //Writing the columns headers
        int i=0;
        final Row columnNameRow = sheet.createRow(0);
        for (Map.Entry<String, String> e : columns.entrySet()) {
            final Cell cell = columnNameRow.createCell(i);
            cell.setCellValue(e.getValue());
            i++;
        }

        //Writing the data
        if (data!=null) {
            for (int j = 0; j < data.size(); j++) {
                final Row row = sheet.createRow(j+1);
                int k = 0;
                for (Map.Entry<String, String> e : columns.entrySet()) {
                    final Object obj = data.get((j));
                    final Object o = BeanToTextExporter.getAndInvokeGetter(obj, e.getKey());
                    final String value = getExportString(o, dateFormat);
                    final Cell cell = row.createCell(k);
                    cell.setCellValue(value);
                    k++;
                }
            }
        }
    }

    /**
	 * Renders the Excel view, given the specified model.
	 */
    @Override
    protected void renderMergedOutputModel(
            final Map model, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
		logger.debug("Created Excel Workbook from scratch");
		buildExcelDocument(model, workbook, request, response);
		response.setContentType(getContentType());
        final ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
    }

}//End of Class
