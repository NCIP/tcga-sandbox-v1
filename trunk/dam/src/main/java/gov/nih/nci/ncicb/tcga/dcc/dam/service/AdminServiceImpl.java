package gov.nih.nci.ncicb.tcga.dcc.dam.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DccPropertyQueries;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Service class for admin related requests.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AdminServiceImpl implements AdminService {

    @Autowired
    DccPropertyQueries dccPropertyQueries;


    @Override
    public List<DccProperty> getAllPropertiesForAnApplication(final String appName){
        return dccPropertyQueries.getDccPropertiesForAnApplication(appName);
    }

    @Override
    public void addOrUpdateProperty(final DccProperty dccProperty){
        dccPropertyQueries.addOrUpdateProperty(dccProperty);
    }

    @Override
    public void deleteProperty(final DccProperty dccProperty){
        dccPropertyQueries.deleteProperty(dccProperty);
    }


    public void setDccPropertyCacheUtil(DccPropertyQueries dccPropertyQueries) {
        this.dccPropertyQueries = dccPropertyQueries;
    }

}
