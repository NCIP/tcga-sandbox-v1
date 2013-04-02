/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Writes a TabDelimitedContent's data to a file.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class TabDelimitedContentWriter {

    public void writeToFile(TabDelimitedContent content, File file) throws IOException {
        FileWriter fWriter = new FileWriter(file);
        BufferedWriter bWriter = new BufferedWriter(fWriter);
        PrintWriter out = new PrintWriter(bWriter);
        try {
            for (int row = 0; row < content.getTabDelimitedContents().size(); row++) {
                String[] rowContents = content.getTabDelimitedContents().get(row);
                for (int col = 0; col < rowContents.length; col++) {
                    if (col > 0) {
                        out.print("\t");
                    }
                    out.print(rowContents[col]);
                }
                out.println();
            }
        } finally {
            out.flush();
            out.close();
            out = null;
            bWriter.close();
            bWriter = null;
            fWriter.close();
            fWriter = null;
        }
    }
}
