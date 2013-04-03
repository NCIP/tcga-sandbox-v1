/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.bean.uuidbrowserws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * aliquot bean class to be used for the uuid browser view web service
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@XmlRootElement(name = "aliquot")
@XmlAccessorType(XmlAccessType.FIELD)
public class AliquotUUIDWS {

    @XmlAttribute(name = "href")
    private String aliquot;
    private CenterUUIDWS receivingCenter;
    private String platform;
    private String plateID;
    private String lastSubmission;
    private Boolean shipped;
    private String shippedDate;

    public AliquotUUIDWS() {
    }

    public AliquotUUIDWS(String aliquot) {
        this.aliquot = aliquot;
    }

    public AliquotUUIDWS(final CenterUUIDWS receivingCenter, final String platform, final String plateID,
                         final String lastSubmission, final Boolean shipped, final String shippedDate) {
        this.receivingCenter = receivingCenter;
        this.platform = platform;
        this.plateID = plateID;
        this.lastSubmission = lastSubmission;
        this.shipped = shipped;
        this.shippedDate = shippedDate;
    }

    public String getAliquot() {
        return aliquot;
    }

    public void setAliquot(String aliquot) {
        this.aliquot = aliquot;
    }

    public CenterUUIDWS getReceivingCenter() {
        return receivingCenter;
    }

    public void setReceivingCenter(CenterUUIDWS receivingCenter) {
        this.receivingCenter = receivingCenter;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlateID() {
        return plateID;
    }

    public void setPlateID(String plateID) {
        this.plateID = plateID;
    }

    public String getLastSubmission() {
        return lastSubmission;
    }

    public void setLastSubmission(String lastSubmission) {
        this.lastSubmission = lastSubmission;
    }

    public Boolean getShipped() {
        return shipped;
    }

    public void setShipped(Boolean shipped) {
        this.shipped = shipped;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }
}//End of Class
