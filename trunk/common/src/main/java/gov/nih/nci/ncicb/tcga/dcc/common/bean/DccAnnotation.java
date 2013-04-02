/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bean representing an Annotation.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name = "dccAnnotation")
@XmlAccessorType(XmlAccessType.FIELD)
public class DccAnnotation {

    public final static String STATUS_APPROVED = "Approved";
    public final static String STATUS_PENDING = "Pending";
    public final static String STATUS_RESCINDED = "Rescinded";

    @XmlElement(name = "id")
    private Long id;

    @XmlElement(name = "dateCreated")
    private Date dateCreated;

    @XmlElement(name = "createdBy")
    private String createdBy;

    @XmlElement(name= "status")
    private String status = STATUS_APPROVED;

    @XmlElement(name = "annotationCategory")
    private DccAnnotationCategory annotationCategory;
    
    @XmlElement(name = "items")
    private List<DccAnnotationItem> items;

    @XmlElement(name = "notes")
    private List<DccAnnotationNote> notes;

    private Boolean approved = false; // initialize as false always since that is the default

    private Boolean rescinded = false; // initialize as false always since that is the default

    private String updatedBy;

    private Date dateUpdated;
    
    public DccAnnotation() {
    	approved = false;
        rescinded = false;
    	items = new ArrayList<DccAnnotationItem>();
    	notes = new ArrayList<DccAnnotationNote>();
    }

    /**
     * Adds a note to this annotation.
     *
     * @param noteText the content of the note
     * @param username the user who added the note
     * @param dateAdded the date the note was added (uses current date if null)
     */
    public void addNote(final String noteText, final String username, Date dateAdded) {
        if((noteText != null )&& noteText.length() > 0){
            final DccAnnotationNote note = new DccAnnotationNote();

            if (dateAdded == null) {
                dateAdded = new Date();
            }

            note.setNoteText(noteText);
            note.setAddedBy(username);
            note.setDateAdded(dateAdded);

            if (notes == null) {
                notes = new ArrayList<DccAnnotationNote>();
            }

            notes.add(note);
        }
    }

    /**
     * Adds a note to this annotation, using the current date for dateAdded. 
     *
     * @param noteText the content of the note
     * @param username the user who added the note
     */
    public void addNote(final String noteText, final String username) {
        addNote(noteText, username, null);
    }

    /**
     * Adds a note to this anotation
     * @param note The note to be added
     */
    public void addNote(final DccAnnotationNote note) {
        if (notes == null) {
            notes = new ArrayList<DccAnnotationNote>();
        }
        notes.add(note);
    }

    /**
     * Add an item to this annotation
     * 
     * @param dccAnnotationItem the <code>DccAnnotationItem</code> to add
     */
    public void addItem(final DccAnnotationItem dccAnnotationItem) {
    	
    	if(items == null) {
    		items = new ArrayList<DccAnnotationItem>();
    	}
    	
        items.add(dccAnnotationItem);
    }
    
    /*
     * Getter / Setter
     */

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
       return status;
    }

	public void setStatus(final String status) {
		this.status = status;
	}

    public DccAnnotationCategory getAnnotationCategory() {
        return annotationCategory;
    }

    public void setAnnotationCategory(final DccAnnotationCategory annotationCategory) {
        this.annotationCategory = annotationCategory;
    }

	public List<DccAnnotationItem> getItems() {
		return items;
	}

	public void setItems(final List<DccAnnotationItem> items) {
		this.items = items;
	}

    public List<DccAnnotationNote> getNotes() {
        return notes;
    }

    public Boolean isNotesEmpty(){
        return (notes == null || notes.size() == 0);
    }

    public void setNotes(final List<DccAnnotationNote> notes) {
        this.notes = notes;
    }

    public Boolean getRescinded() {
        return rescinded;
    }

    public void setRescinded(final Boolean rescinded) throws BeanException {
        isValidStatusUpdate(getApproved(), rescinded);

        this.rescinded = rescinded;

        //Update status
        if(getApproved() != null && rescinded != null) {
            setStatus(deriveStatus(getApproved(), rescinded));
        }
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(final Boolean approved) throws BeanException {
        isValidStatusUpdate(approved, getRescinded());

        this.approved = approved;
        
        //Update status
        if(approved != null && getRescinded() != null) {
            setStatus(deriveStatus(approved, getRescinded()));
        }
    }

    public Date getDateUpdated () {
       return dateUpdated;
    }

    public void setDateUpdated(final Date dateUpdated)  {
        this.dateUpdated = dateUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }


    /**
     * This returns a list of Tumor objects representing the diseases of each item.  The disease will be in the same
     * order as the items.  Note: this is probably temporary, until we come up with a better solution.
     * @return  list of Tumor objects representing item diseases
     */
    public List<Tumor> getDiseases() {
        List<Tumor> diseases = new ArrayList<Tumor>();
        for (final DccAnnotationItem item : items) {
            diseases.add(item.getDisease());
        }
        return diseases;
    }

    /**
     * This returns a list of DccAnnotationItemType objects representing the item types of each item.  The item types
     * will be in the same order as the items.  Note: this is probably temporary.
     * @return list of DccAnnotationItemType objects representing item types
     */
    public List<DccAnnotationItemType> getItemTypes() {
        List<DccAnnotationItemType> itemTypes = new ArrayList<DccAnnotationItemType>();
        for (final DccAnnotationItem item : items) {
            itemTypes.add(item.getItemType());
        }
        return itemTypes;
    }

    /**
     * This method generates the {@link DccAnnotation#status} based on the values of the approved and
     * rescinded flags. The generated status string is a human readable display of the status of the annotation.
     * NOTE : The status where a annotation is PENDING and is RESCINDED is not an allowed state
     * @param approved A boolean value representing the approval of this annotation
     * @param rescinded A boolean value representing the recission of this annotation
     * @return A String representing the status.
     */
    public String deriveStatus(final Boolean approved, final Boolean rescinded) {
        String ret = STATUS_PENDING;
        if(approved && rescinded) {
            ret = STATUS_RESCINDED;
        } else if(approved && !rescinded) {
            ret = STATUS_APPROVED;
        } else if(!approved && !rescinded) {
            ret = STATUS_PENDING;
        }
        return ret;
    }

    public void isValidStatusUpdate(final Boolean approved, final Boolean rescinded) throws BeanException {
        if(!approved && rescinded) {
            throw new BeanException("An annotation must be approved in order to be rescinded.");
        }
    }
}
