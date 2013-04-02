/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
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
import java.util.ArrayList;
import java.util.List;

/**
 * Bean representing an annotation category.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DccAnnotationCategory implements Comparable<DccAnnotationCategory> {

    @XmlElement(name = "categoryId")
    private Long categoryId;

    @XmlElement(name = "categoryName")
    private String categoryName;

    @XmlElement(name = "categoryDescription")
    private String categoryDescription;

    @XmlElement(name = "itemTypes")
    private List<DccAnnotationItemType> itemTypes = new ArrayList<DccAnnotationItemType>();

    @XmlElement(name = "annotationClassification")
    private  DccAnnotationClassification annotationClassification;

    /**
     * The ID of the category for "flagged Do Not Use" in the database
     */
    public static final Long DCC_ANNOTATION_DNU_ID = 29L;

    public String toString() {
        return categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    public DccAnnotationClassification getAnnotationClassification() {
        return annotationClassification;
    }

    public void setAnnotationClassification(final DccAnnotationClassification annotationClassification) {
        this.annotationClassification = annotationClassification;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(final String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public List<DccAnnotationItemType> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(final List<DccAnnotationItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public void addItemType(final DccAnnotationItemType itemType) {
        if (itemTypes == null) {
            itemTypes = new ArrayList<DccAnnotationItemType>();
        }
        itemTypes.add(itemType);
    }

    public int compareTo(final DccAnnotationCategory o) {
        return this.getCategoryName().compareTo(o.getCategoryName());
    }
}
