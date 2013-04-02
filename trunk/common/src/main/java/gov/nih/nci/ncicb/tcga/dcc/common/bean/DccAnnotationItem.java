/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * AnnotationItem bean
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name = "dccAnnotationItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DccAnnotationItem {

    @XmlElement(name = "item")
    private String item;

	@XmlElement(name = "itemType")
    private DccAnnotationItemType itemType;
    
    @XmlElement(name = "disease")
    private Tumor disease;

    private Long id;
    
    /*
     * Getter / Setter
     */

    public String getItem() {
		return item;
	}

	public void setItem(final String item) {
		this.item = item;
	}

	public DccAnnotationItemType getItemType() {
		return itemType;
	}

	public void setItemType(final DccAnnotationItemType itemType) {
		this.itemType = itemType;
	}

	public Tumor getDisease() {
		return disease;
	}

	public void setDisease(final Tumor disease) {
		this.disease = disease;
	}

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getId () {
        return id;
    }

    /**
     * Uses the item property as the string representation
     *
     * @return the string version of this object
     */
    public String toString() {
        return item;
    }
}
