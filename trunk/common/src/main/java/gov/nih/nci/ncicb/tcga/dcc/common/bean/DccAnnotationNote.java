/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Bean representing an annotation note.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name = "dccAnnotationNote")
@XmlAccessorType(XmlAccessType.FIELD)
public class DccAnnotationNote {

    @XmlElement(name = "noteId")
    private Long noteId;

    @XmlElement(name = "noteText")
    private String noteText;

    @XmlElement(name = "addedBy")
    private String addedBy;

    @XmlElement(name = "dateAdded")
    private Date dateAdded;

    @XmlElement(name = "dateEdited")
    private Date dateEdited;

    @XmlElement(name = "editedBy")
    private String editedBy;

    @XmlElement(name = "annotationId")
    private Long annotationId;

    public String toString() {
        return StringUtil.spaceAllWhitespace(noteText);
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(final Long noteId) {
        this.noteId = noteId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(final String noteText) {
        this.noteText = noteText;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(final String addedBy) {
        this.addedBy = addedBy;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(final Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getDateEdited() {
        return dateEdited;
    }

    public void setDateEdited(final Date dateEdited) {
        this.dateEdited = dateEdited;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(final String editedBy) {
        this.editedBy = editedBy;
    }

    public Long getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(final Long annotationId) {
        this.annotationId = annotationId;
    }
}
