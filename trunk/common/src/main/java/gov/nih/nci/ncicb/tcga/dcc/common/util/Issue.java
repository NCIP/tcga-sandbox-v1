/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * @author Robert S. Sfeir
 * @version 1.0
 *          <p/>
 *          This class is used in conjunction with IssueScanner.  IssueScanner shoves data in here and we create
 *          multiple Issue objects.  We can then get the value of the data as a toString().
 */
public class Issue {
    private String status;
    private String priority;
    private String submittedBy;
    private String assignedTo;
    private String summary;
    private String comment;
    private String product;
    private String component;
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(final String priority) {
        this.priority = priority;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(final String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getAssignedTo() {
        if (assignedTo == null) {
            return "";
        } else {
            return assignedTo;
        }
    }

    public void setAssignedTo(final String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getComment() {
        return comment;
    }

    /**
     * Special set method which sets the value of the comment.  Since the description and comment have a lot of text we
     * handle this in a StringBuilder.  This method also checks to make sure that any quotes (") that exist in the
     * comments or description, are changed to single quotes ' so that we don't trip up the comma delimited format
     * reader.  If there are more characters we want to escape differently, then we need to modify this method so we can
     * properly strip or escape those out.
     *
     * @param comment the description or comment we want to add.
     */
    public void setComment(final String comment) {
        //We need to scan the comment to make sure we escape Characters properly or the comma delimiter will fail
        final StringBuilder convertedComment = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(comment);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            //Find any " char and change to ' so we don't trip the comma quote delimited text.
            if (character == '"') {
                convertedComment.append("'");
            } else {
                convertedComment.append(character);
            }
            character = iterator.next();
        }
        this.comment = convertedComment.toString();
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(final String product) {
        this.product = product;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(final String component) {
        this.component = component;
    }

    /**
     * Overriden toString method used to return the comma delimited value of this issue entry.  Each value is comma
     * delimited encapsulated in quotes.
     *
     * @return the issue's details as comma delimited text.
     */
    @Override
    public String toString() {
        //return the string with comma delimited separators.
        final StringBuilder builtIssueString = new StringBuilder();
        return builtIssueString
                .append(QUOTE).append(getSummary()).append(QUOTE).append(COMMA)
                .append(QUOTE).append(getAssignedTo()).append(QUOTE).append(COMMA)
                .append(QUOTE).append(getComponent()).append(QUOTE).append(COMMA)
                .append(QUOTE).append(getProduct()).append(QUOTE).append(COMMA)
                .append(QUOTE).append(getPriority()).append(QUOTE).append(COMMA)
                .append(QUOTE).append(getStatus()).append(QUOTE).append(COMMA)
                .append(QUOTE).append(getSubmittedBy()).append(QUOTE).append(COMMA)
                .append(QUOTE).append(getComment()).append(QUOTE).append("\n").toString();
    }
}
