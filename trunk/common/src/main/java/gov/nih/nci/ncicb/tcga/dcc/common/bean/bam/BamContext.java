/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.bean.bam;

import java.util.LinkedList;
import java.util.List;

/**
 * Context bean holding shared data for the Bam loader processes.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamContext {

    private List<String> errorList = new LinkedList<String>();
    private List<String> warningList = new LinkedList<String>();

    public void addError(final String process, final String error) {
        final StringBuilder builder = new StringBuilder();
        builder.append("BAM ").append(process).append(" Error: ");
        builder.append(error);
        errorList.add(builder.toString());
    }

    public void addWarning(final String process, final String warning) {
        final StringBuilder builder = new StringBuilder();
        builder.append("BAM ").append(process).append(" Warning: ");
        builder.append(warning);
        warningList.add(builder.toString());
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    public List<String> getWarningList() {
        return warningList;
    }

    public void setWarningList(List<String> warningList) {
        this.warningList = warningList;
    }

}//End of Class
