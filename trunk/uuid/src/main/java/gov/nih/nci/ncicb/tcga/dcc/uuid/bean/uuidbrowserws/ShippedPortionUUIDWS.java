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
 * shipped portion bean class to be used for the uuid browser view web service
 *
 * @author bertondl Last updated by: $Author$
 * @version $Rev$
 */


@XmlRootElement(name = "shipped-portion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShippedPortionUUIDWS {

    @XmlAttribute(name = "href")
    private String shippedPortion;
    private CenterUUIDWS receivingCenter;
    private String platform;
    private String plateID;
    private String lastSubmission;
    private Boolean shipped;
    private String shippedDate;

    public ShippedPortionUUIDWS() {
    }

    public ShippedPortionUUIDWS(final String shippedPortion) {
        this.shippedPortion = shippedPortion;
    }

    public ShippedPortionUUIDWS(final CenterUUIDWS receivingCenter, final String platform,
                                final String plateID, final String lastSubmission, final Boolean shipped,
                                final String shippedDate) {
        this.receivingCenter = receivingCenter;
        this.platform = platform;
        this.plateID = plateID;
        this.lastSubmission = lastSubmission;
        this.shipped = shipped;
        this.shippedDate = shippedDate;
    }

    public String getLastSubmission() {
        return lastSubmission;
    }

    public void setLastSubmission(final String lastSubmission) {
        this.lastSubmission = lastSubmission;
    }

    public String getShippedPortion() {
        return shippedPortion;
    }

    public void setShippedPortion(final String shippedPortion) {
        this.shippedPortion = shippedPortion;
    }

    public CenterUUIDWS getReceivingCenter() {
        return receivingCenter;
    }

    public void setReceivingCenter(final CenterUUIDWS receivingCenter) {
        this.receivingCenter = receivingCenter;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getPlateID() {
        return plateID;
    }

    public void setPlateID(final String plateID) {
        this.plateID = plateID;
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
}
