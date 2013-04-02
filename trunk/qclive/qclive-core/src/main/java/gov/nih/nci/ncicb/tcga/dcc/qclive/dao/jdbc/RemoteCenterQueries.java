package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Partial implementation of CenterQueries for Soundcheck.  Note: only findCenterId is implemented for now.
 *
 * @author Jessica Walton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RemoteCenterQueries implements CenterQueries {
    // cache to avoid querying for the same data more than once
    private final Map<String, Integer> centerIdCache = new HashMap<String, Integer>();

    private RemoteValidationHelper remoteValidationHelper;

    /**
     * Finds the center ID for the center with the given name and center type.  Will return null if no such center
     * is found.
     *
     * @param centerName the center domain name
     * @param centerType the center type
     * @return center ID or null
     */
    @Override
    public Integer findCenterId(final String centerName, final String centerType) {
        final String cacheKey = centerName + "." + centerType;
        try {
            if (centerIdCache.get(cacheKey) != null) {
                return centerIdCache.get(cacheKey);
            } else {
                final Integer centerId = remoteValidationHelper.getCenterId(centerName, centerType);
                centerIdCache.put(cacheKey, centerId);
                return centerId;
            }

        } catch (ApplicationException e) {
            return null;
        }
    }

    /**
     * @deprecated
     */
    @Override
    public Collection<Map<String, Object>> getAllCenters() {
        return null;
    }

    @Override
    public List<Center> getCenterList() {
        return null;
    }

    @Override
    public List<Center> getRealCenterList() {
        return null;
    }

    @Override
    public Center getCenterById(final Integer centerId) {
        return null;
    }

    @Override
    public Center getCenterByName(final String centerName, final String centerType) {
        return null;
    }

    @Override
    public Integer getCenterIdForBCRCenter(final String bcrCenter) {
        return null;
    }

    public void setRemoteValidationHelper(final RemoteValidationHelper remoteValidationHelper) {
        this.remoteValidationHelper = remoteValidationHelper;
    }

	@Override
	public List<Center> getConvertedToUUIDCenters() {		
		return null;
	}

	@Override
	public boolean isCenterCenvertedToUUID(Center center) {
		return false;
	}

    @Override
    public boolean isCenterConvertedToUUID(String domainName, String centerTypeCode){
        return false;
    }

    @Override
    public boolean doesCenterRequireMageTab(final String centerName, final String centerTypeCode) {
        return false;
    }
}
