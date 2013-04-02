package gov.nih.nci.ncicb.tcga.dcc.dam.bean;


import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;

import java.util.List;

/**
 * Bean to hold admin requests result data
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AdminDataModel {
    String layout;
    String appName;
    List<DccProperty> propertyData;

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<DccProperty> getPropertyData() {
        return propertyData;
    }

    public void setPropertyData(List<DccProperty> propertyData) {
        this.propertyData = propertyData;
    }
}
