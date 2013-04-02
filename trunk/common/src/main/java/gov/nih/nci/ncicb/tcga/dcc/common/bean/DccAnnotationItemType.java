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

/**
 * Bean representing an annotation item type.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DccAnnotationItemType {

    @XmlElement(name = "itemTypeId")
    private Long itemTypeId;

    @XmlElement(name = "itemTypeName")
    private String itemTypeName;

    @XmlElement(name = "itemTypeDescription")
    private String itemTypeDescription;

    /**
     * The ID of the aliquot item type in the database
     */
    public static final Long ALIQUOT_TYPE_ID = 1L;

    public String toString() {
        return itemTypeName;
    }

    public Long getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(final Long itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(final String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    public String getItemTypeDescription() {
        return itemTypeDescription;
    }

    public void setItemTypeDescription(final String itemTypeDescription) {
        this.itemTypeDescription = itemTypeDescription;
    }
}
