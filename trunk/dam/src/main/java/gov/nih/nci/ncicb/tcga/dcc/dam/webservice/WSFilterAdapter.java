/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.HttpStatusCode;
import gov.nih.nci.ncicb.tcga.dcc.common.webservice.WebServiceUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtils;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.WebApplicationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Adapter class that defines the regular DAM FilterRequest with added methods and override to better suit the DAM web service.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class WSFilterAdapter extends FilterRequest {

    private static final long serialVersionUID = -6694801611437120151L;

    private DAMWSUtil damWSUtil = new DAMWSUtil();

    /**
     * These constants are defined here so that if the dao changes we don't have to go deep in the code to fix.
     */
    private static final String DATA_TYPE_ID = "data_type_id";

    private static final String PLATFORM_ALIAS = "platform_alias";
    private static final String PLATFORM_NAME = "platform_name";

    private static final String CENTER_ID = "center_id";
    private static final String CENTER_SHORT_NAME = "short_name";
    private static final String CENTER_DISPLAY_NAME = "display_name";
    private static final String CENTER_TYPE_CODE = "center_type_code";

    private static final String HTML_NEW_LINE = "<br/>";
    private static final String HTML_TAB = "&nbsp;&nbsp;&nbsp;";

    /**
     * Allow for direct numeric value of batch
     * instead of imposing writing 'Batch 5' for example
     */
    @Override
    public void setBatch(String batchAsString) {
        if (!StringUtils.isBlank(batchAsString)) {
            String res = "";
            for (final String batch : batchAsString.split(",", -1)) {
                res += processBatch(batch) + ",";
            }
            super.setBatch(res.substring(0, res.length() - 1));
        }
    }

    /**
     * process one batch to be added to the filter
     *
     * @param batch
     * @return valid batch string
     */
    protected String processBatch(final String batch) {
        if (batch != null && !batch.startsWith("Batch")) {
            try {
                return "Batch " + Integer.parseInt(batch);
            } catch (NumberFormatException e) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                        HttpStatusCode.PRECONDITION_FAILED, "Batch must be a number"));
            }
        } else {
            return batch;
        }
    }

    /**
     * Allow for usage of platform type name
     *
     * @param platformType the platform type
     */
    @Override
    public void setPlatformType(final String platformType) {
        //Not changing this yet as it is expected in the web service filter to only have 1 platform type
        //I strongly suggest we modify this to allow for multiple datatype as this restriction is only
        //enforced in the DAM UI when generating the web service url, not from direct browser url entry
        if (!StringUtils.isBlank(platformType)) {

            try {
                Integer.parseInt(platformType);
                super.setPlatformType(platformType);

            } catch (final NumberFormatException e) {

                boolean matchFound = false;

                for (final Map<String, Object> dataTypeMap : DAMUtils.getInstance().getAllDataTypes()) {
                    if (damWSUtil.hasValueIgnoreCase(dataTypeMap, platformType)) {
                        super.setPlatformType(dataTypeMap.get(DATA_TYPE_ID).toString());
                        matchFound = true;
                        break;
                    }
                }

                if (!matchFound) {
                    throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED, "Platform type '" +
                            platformType + "' is unknown"));
                }
            }
        } else {
            super.setPlatformType(platformType);
        }
    }

    /**
     * Allow for usage of center name
     * <p/>
     * If the center is other than an Id, then a match will be searched among all the centers with the condition that the match
     * supports the platform (the platform *must* be set prior to the center)
     *
     * @param centerAsString the name of the center to be set
     */
    @Override
    public void setCenter(final String centerAsString) {
        final String platFormTypeC = trimFirstAndLastComma(getPlatformType());
        if (!StringUtils.isBlank(centerAsString)) {
            String res = "";
            for (final String center : centerAsString.split(",", -1)) {
                res += processCenter(center, platFormTypeC) + ",";
            }
            super.setCenter(res.substring(0, res.length() - 1));
        } else {
            // added check to allow blank values for clinical type data
            if (!StringUtils.equalsIgnoreCase(platFormTypeC,
                    NonplatformType.NONPLATFORMTYPE_CLINICAL.getAssociatedPseudoPlatformType())) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED,
                        "Center cannot be null." + HTML_NEW_LINE + HTML_NEW_LINE + getAllCenterListForHtml()));
            }
        }
    }

    /**
     * process one center to be added to the filter
     *
     * @param center
     * @param platFormTypeC
     * @return valid center id string
     */
    protected String processCenter(final String center, final String platFormTypeC) {
        //added this check to disallow values for clinical type
        if (StringUtils.equalsIgnoreCase(platFormTypeC,
                NonplatformType.NONPLATFORMTYPE_CLINICAL.getAssociatedPseudoPlatformType())) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.PRECONDITION_FAILED, "Center cannot be specified with clinical data type"));
        }
        try {
            return "" + Integer.parseInt(center);
        } catch (NumberFormatException e) {
            //The platform must be set to be able to select the correct center
            if (StringUtils.isBlank(platform)) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED,
                        "The platform must be set."));
            }
            final List<Map<String, Object>> centers = (List<Map<String, Object>>) DAMUtils.getInstance().getAllCenters();
            boolean centerMatchFound = false;
            boolean platformMatchFound = false;
            for (final String platformAlias : parseValue(platform)) {
                final Platform platform = DAMUtils.getInstance().getPlatformWithAlias(platformAlias);
                final String platformCenterType = platform.getCenterType();
                final Iterator<Map<String, Object>> centerMapIterator = centers.iterator();
                while (centerMapIterator.hasNext()) {
                    final Map<String, Object> centerMap = centerMapIterator.next();
                    if (damWSUtil.hasValueIgnoreCase(centerMap, center)) {
                        centerMatchFound = true;
                        if (damWSUtil.hasValueIgnoreCase(centerMap, platformCenterType)) {
                            return centerMap.get(CENTER_ID).toString();
                        }
                    }
                }
            }
            if (!centerMatchFound) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED,
                        "Center '" + center + "' is unknown."));
            }
            if (!platformMatchFound) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED,
                        "Center '" + center + "' does not support the platform '" +
                                trimFirstAndLastComma(platform) + "'."));
            }

        }
        return "";
    }


    /**
     * trim the first and last comma of a string
     *
     * @param val
     * @return trimmed string
     */
    protected String trimFirstAndLastComma(final String val) {
        String res = val;
        if (res != null) {
            if (res.startsWith(",")) {
                res = res.substring(1);
            }
            if (res.endsWith(",")) {
                res = res.substring(0, res.length() - 1);
            }
        }
        return res;
    }

    /**
     * Allow for usage of platform id since the filter actually uses the name of the platform
     * and not its id which is, to say the least very confusing and annoying.
     *
     * @param platformAsString the platform
     */
    @Override
    public void setPlatform(String platformAsString) {
        final String platFormTypeC = trimFirstAndLastComma(getPlatformType());
        if (!StringUtils.isBlank(platformAsString)) {
            String res = "";
            for (final String platform : platformAsString.split(",", -1)) {
                res += processPlatform(platform, platFormTypeC) + ",";
            }
            super.setPlatform(res.substring(0, res.length() - 1));
        } else {
            // added check to allow blank values for clinical type data
            if (!StringUtils.equalsIgnoreCase(platFormTypeC,
                    NonplatformType.NONPLATFORMTYPE_CLINICAL.getAssociatedPseudoPlatformType())) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                        HttpStatusCode.PRECONDITION_FAILED, "Platform cannot be null." +
                        HTML_NEW_LINE + HTML_NEW_LINE + getAllPlatformListForHtml()));
            }
        }
    }

    /**
     * process one platform to be added to the filter
     *
     * @param platformAsString
     * @param platFormType
     * @return a valid platform
     */
    protected String processPlatform(final String platformAsString, final String platFormType) {
        //added this check to disallow values for clinical type
        if (StringUtils.equalsIgnoreCase(platFormType,
                NonplatformType.NONPLATFORMTYPE_CLINICAL.getAssociatedPseudoPlatformType())) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.PRECONDITION_FAILED,
                    "Platform cannot be specified with clinical data type"));
        }
        try {
            final Platform platform = DAMUtils.getInstance().getPlatformById(
                    Integer.parseInt(platformAsString));
            return platform.getPlatformAlias();
        } catch (final NumberFormatException e) {
            for (final Map<String, Object> platformMap : DAMUtils.getInstance().getAllPlatforms()) {
                if (damWSUtil.hasValueIgnoreCase(platformMap, platformAsString)) {
                    return platformMap.get(PLATFORM_ALIAS).toString();
                }
            }
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(
                    HttpStatusCode.PRECONDITION_FAILED, "Platform '" +
                    platformAsString + "' is unknown."));
        }
    }

    /**
     * disallow nullity of level
     */
    @Override
    public void setLevel(String level) {

        final String platFormTypeC = trimFirstAndLastComma(getPlatformType());

        if (!StringUtils.isBlank(level)) {
            //added this check to disallow values for clinical type
            if (StringUtils.equalsIgnoreCase(platFormTypeC, NonplatformType.NONPLATFORMTYPE_CLINICAL.getAssociatedPseudoPlatformType())) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED, "Level cannot be specified with clinical data type"));
            }
            super.setLevel(level);

        } else {

            //added check to allow blank values for clinical type data
            if (!StringUtils.equalsIgnoreCase(platFormTypeC, NonplatformType.NONPLATFORMTYPE_CLINICAL.getAssociatedPseudoPlatformType())) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED, "Level cannot be null."));
            }
        }
    }

    /**
     * disallow nullity of disease or invalid ones
     */
    @Override
    public void setDiseaseType(final String disease) {

        if (StringUtils.isBlank(disease)) {

            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED,
                    "Disease cannot be null." + HTML_NEW_LINE + HTML_NEW_LINE + getAllDiseaseListForHtml()));
        }

        try {
            Disease maladie = DAMUtils.getInstance().getDisease(disease);
            if (maladie == null) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED, "Disease type '" +
                        disease + "' is unknown"));
            } else if (!maladie.isActive()) {
                throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED, "Disease type '" +
                        disease + "' is not yet available"));
            }
        } catch (IllegalStateException e) {
            throw new WebApplicationException(WebServiceUtil.getStatusResponse(HttpStatusCode.PRECONDITION_FAILED, "Disease type '" +
                    disease + "' is unknown"));
        }
        super.setDiseaseType(disease);
    }

    /**
     * Return a list of all diseases, formatted for HTML output
     *
     * @return a list of all diseases, formatted for HTML output
     */
    private String getAllDiseaseListForHtml() {

        final StringBuilder diseaseListStringBuilder = new StringBuilder("Available diseases are:").append(HTML_NEW_LINE);

        for (final Disease disease : DAMUtils.getInstance().getActiveDiseases()) {
            diseaseListStringBuilder
                    .append(HTML_NEW_LINE)
                    .append(HTML_TAB)
                    .append(disease);
        }

        return diseaseListStringBuilder.toString();
    }

    /**
     * Return a list of all platforms, formatted for HTML output
     *
     * @return a list of all platforms, formatted for HTML output
     */
    private String getAllPlatformListForHtml() {

        final StringBuilder platformListStringBuilder = new StringBuilder("Available platforms are (alias - name):").append(HTML_NEW_LINE);

        for (final Map<String, Object> platform : DAMUtils.getInstance().getAllPlatforms()) {

            platformListStringBuilder
                    .append(HTML_NEW_LINE)
                    .append(HTML_TAB)
                    .append(platform.get(PLATFORM_ALIAS))
                    .append(" - ")
                    .append(platform.get(PLATFORM_NAME));
        }

        return platformListStringBuilder.toString();
    }

    /**
     * Return a list of all centers, formatted for HTML output
     *
     * @return a list of all centers, formatted for HTML output
     */
    private String getAllCenterListForHtml() {

        final StringBuilder centerListStringBuilder = new StringBuilder("Available centers are:").append(HTML_NEW_LINE);

        for (final Map<String, Object> center : DAMUtils.getInstance().getAllCenters()) {

            centerListStringBuilder
                    .append(HTML_NEW_LINE)
                    .append(HTML_TAB)
                    .append(center.get(CENTER_SHORT_NAME))
                    .append(" - ")
                    .append(center.get(CENTER_DISPLAY_NAME))
                    .append(" (")
                    .append(center.get(CENTER_TYPE_CODE))
                    .append(")");
        }

        return centerListStringBuilder.toString();
    }

    /**
     * For unit tests: calls the setPlatform() from super.
     *
     * @param platformAlias the platform alias to set
     */
    public void setPlatformAlias(final String platformAlias) {
        super.setPlatform(platformAlias);
    }

}//End of Class
