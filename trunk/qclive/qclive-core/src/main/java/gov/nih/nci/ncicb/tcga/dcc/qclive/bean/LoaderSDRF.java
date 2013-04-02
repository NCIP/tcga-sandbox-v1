/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;


import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.CutColumns;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Represents an SDRF for the autoloader.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LoaderSDRF implements Serializable {

    private List<String[]> columnData;
    private File sdrfFile;

    public LoaderSDRF(final File file, String[] selectedHeaders) throws LoaderException {
        setSDRFFile(file);
        cutColumns(selectedHeaders);
    }

    void setSDRFFile(File file) {
        sdrfFile = file;
    }

    void cutColumns(String[] selectedHeaders) throws LoaderException {
        try {
            columnData = CutColumns.cut(sdrfFile, selectedHeaders);
        }
        catch (IOException e) {
            throw new LoaderException(e);
        }
    }

    public String getColumnHeader(int col) {
        if (columnData == null) {
            return null;
        }
        if (col < 0) {
            return null;
        }
        if (col >= columnData.get(0).length) {
            return null;
        }
        return columnData.get(0)[col];
    }

    public String getColumnData(String header, int row) {
        if (columnData == null) {
            return null;
        }
        if (header == null) {
            return null;
        }
        if (row < 0 || row >= columnData.size()) {
            return null;
        }
        int pickedCol = -1;
        String[] headerRow = columnData.get(0);
        for (int icol = 0; icol < headerRow.length; icol++) {
            if (header.equals(headerRow[icol])) {
                pickedCol = icol;
                break;
            }
        }
        if (pickedCol == -1) {
            return null;
        }
        return columnData.get(row)[pickedCol];
    }

    public int getRowCount() {
        return columnData.size();
    }
}
