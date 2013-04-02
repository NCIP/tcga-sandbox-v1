/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view.request;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtils;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DAMResourceBundle;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Cell;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 *          <p/>
 *          This is more than a simple request object.  It actually implements the filter as well through
 *          the callback method cellMatchesFilter.  Also handles multiple types of sample ID inputs
 *          (dropdown, text area and file upload) in a way that works both with form input and external URL-based filter.
 */
public class FilterRequest implements Serializable, FilterRequestI {
    private static final long serialVersionUID = -678631727144224392L;

    private class SampleCriterion implements Serializable {
        private static final long serialVersionUID = -4406372797865322188L;
        public String collectionCenter, patientId, sampleType; //sample pieces
        public boolean hit;
        public boolean fromTextList;

        public boolean equals(Object o) {
            boolean ret = false;
            if (o instanceof SampleCriterion) {
                SampleCriterion other = (SampleCriterion) o;
                //this works because the strings were intern'd
                if (collectionCenter == other.collectionCenter && patientId == other.patientId && sampleType == other.sampleType) {
                    ret = true;
                }
            }
            return ret;
        }

        public String toString() {
            StringBuilder ret = new StringBuilder();
            ret.append("TCGA-");
            ret.append(collectionCenter != null ? collectionCenter : "*");
            ret.append("-");
            ret.append(patientId != null ? patientId : "*");
            ret.append("-");
            ret.append(sampleType != null ? sampleType : "*");
            return ret.toString();
        }
    }

    static protected final Log logger = LogFactory.getLog(FilterRequest.class);
    private Mode mode;
    private long millis; //for ensuring proper back-button behavior
    private String diseaseType; //used for external filtering
    // filter criteria start here: these are comma-separated lists of values
    private String availability; //ex: A,P
    private String batch; //ex: Batch1,Batch 2
    private String platformType; //numerical code from Header.getName()
    private String center; //numerical code includes both center and platform as in 3:4, or can just be first part
    protected String platform;  //platform name.
    private String level; //1, 2, or C
    private String protectedStatus; //P,N
    private String tumorNormal;
    private String samplesFromDropdowns;       //list of samples from the drop-downs on the UI
    private SampleCriterion[] sampleCriteria;   //internal criteria used to filter samples
    private Date startDate;
    private Date endDate;
    private String showCompleteRowsOnly;
    private SimpleDateFormat dateFormat;

    public FilterRequest() {
        this(Mode.Clear);
    }

    public FilterRequest(Mode mode) {
        this.mode = mode;
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        setMillis(System.currentTimeMillis()); //should then be overwritten from client
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.Clear) {
            clearCriteria();
        }
    }

    private void clearCriteria() {
        batch = null;
        platformType = null;
        center = null;
        level = null;
        protectedStatus = null;
        tumorNormal = null;
        samplesFromDropdowns = null;
        sampleCriteria = null;
        showCompleteRowsOnly = null;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public String getAvailability() {
        return getstr(availability);
    }

    /**
     * Get the availability selections as an array
     *
     * @return an array of availabilities -- may be empty
     */
    public String[] getAvailabilities() {
        return parseValue(getAvailability());
    }

    //returns value for use by JSP - return "" instead of null

    private String getstr(String val) {
        if (val == null) {
            val = "";
        }
        return val;
    }

    public void setAvailability(String availability) {
        this.availability = setstr(availability);
    }

    @Override
    public String getShowCompleteRowsOnly() {
        return showCompleteRowsOnly == null ? "" : showCompleteRowsOnly;
    }

    @Override
    public void setShowCompleteRowsOnly(String showCompleteRowsOnly) {
        this.showCompleteRowsOnly = showCompleteRowsOnly;
    }

    public String getBatch() {
        return getstr(batch);
    }


    public String[] getBatches() {
        return parseValue(getBatch());
    }

    public void setBatch(String batch) {
        this.batch = setstr(batch);
    }

    private String setstr(String val) {
        if (val != null) {
            if (val.length() == 0 || val.equals("All")) {
                val = null;
            } else {
                val = "," + val + ",";
            }
        }
        return val;
    }

    public String getStartDate() {
        String ret = "";
        if (startDate != null) {
            ret = dateFormat.format(startDate);
        }
        return ret;
    }

    public void setStartDate(String startDateStr) throws ParseException {
        if (startDateStr != null && startDateStr.length() > 0) {
            startDate = dateFormat.parse(startDateStr);
        }
    }

    public String getEndDate() {
        String ret = "";
        if (endDate != null) {
            ret = dateFormat.format(endDate);
        }
        return ret;
    }

    public void setEndDate(String endDateStr) throws ParseException {
        if (endDateStr != null && endDateStr.length() > 0) {
            endDate = dateFormat.parse(endDateStr);
        }
    }

    public String getProtectedStatus() {
        return getstr(protectedStatus);
    }

    public String[] getProtectedStatuses() {
        return parseValue(getProtectedStatus());
    }

    public void setProtectedStatus(String protectedStatus) {
        this.protectedStatus = setstr(protectedStatus);
    }

    public String getTumorNormal() {
        return getstr(tumorNormal);
    }

    public String[] getTissueTypes() {   //todo: rename
        return parseValue(getTumorNormal());
    }

    public void setTumorNormal(String tumorNormal) {
        this.tumorNormal = setstr(tumorNormal);
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public String getSamplesFromDropdowns() {
        return samplesFromDropdowns;
    }

    public SampleCriterion[] getSampleCriteria() {
        return sampleCriteria;
    }


    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    //the following three methods are used by the JSP to build up the previous filter UI - dropdowns

    public String[] getSampleCollectionArray() {
        if (samplesFromDropdowns == null || samplesFromDropdowns.length() == 0) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(samplesFromDropdowns, ",");
        String[] ret = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            int pos1 = s.indexOf('-');
            int pos2 = s.indexOf('-', pos1 + 1);
            s = s.substring(pos1 + 1, pos2);
            if (s.equals("*")) {
                s = "";
            }
            ret[i++] = s;
        }
        return ret;
    }

    public String[] getSamplePatientIdArray() {
        if (samplesFromDropdowns == null || samplesFromDropdowns.length() == 0) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(samplesFromDropdowns, ",");
        String[] ret = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            int pos1 = s.indexOf('-');
            int pos2 = s.indexOf('-', pos1 + 1);
            int pos3 = s.indexOf('-', pos2 + 1);
            s = s.substring(pos2 + 1, pos3);
            if (s.equals("*")) {
                s = "";
            }
            ret[i++] = s;
        }
        return ret;
    }

    public String[] getSampleSampleTypeArray() {
        if (samplesFromDropdowns == null || samplesFromDropdowns.length() == 0) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(samplesFromDropdowns, ",");
        String[] ret = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            int pos1 = s.indexOf('-');
            int pos2 = s.indexOf('-', pos1 + 1);
            int pos3 = s.indexOf('-', pos2 + 1);
            s = s.substring(pos3 + 1);
            if (s.equals("*")) {
                s = "";
            }
            ret[i++] = s;
        }
        return ret;
    }

    //if sampleCriteria is just a bunch of empty array values, null it out for speed

    public void eliminateNullSampleCriteria() {
        if (sampleCriteria == null) {
            return;
        }
        for (int i = 0; i < sampleCriteria.length; i++) {
            if (sampleCriteria[i].collectionCenter != null && sampleCriteria[i].collectionCenter.length() > 0) {
                return;
            }
            if (sampleCriteria[i].patientId != null && sampleCriteria[i].patientId.length() > 0) {
                return;
            }
            if (sampleCriteria[i].sampleType != null && sampleCriteria[i].sampleType.length() > 0) {
                return;
            }
        }
        //got here, so there are no criteria
        sampleCriteria = null;
    }

    //called from usage logging system

    public String getSampleString() {
        return getSampleString(',');
    }

    //produces a displayable sample string, with wildcards, from criteria array

    private String getSampleString(char delimiter) {
        if (sampleCriteria == null || sampleCriteria.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sampleCriteria.length; i++) {
            if (sampleCriteria[i].fromTextList) {
                if (i > 0) {
                    sb.append(delimiter);
                }
                sb.append(sampleCriteria[i].toString());
            }
        }
        return sb.toString();
    }

    //parses a list of sample IDs into arrays of the separate pieces, which will be used in the actual filter
    //Remember, this may be called several times: once for drop-downs, one for text list, one for upload document

    private void parseSampleList(String value, boolean fromTextList) {
        if (value != null && value.length() > 0) {
            StringTokenizer tokens = new StringTokenizer(value, ",;|\n ");
            List<SampleCriterion> tempList = new ArrayList<SampleCriterion>();
            int i = 0;
            while (tokens.hasMoreTokens()) {
                String sample = tokens.nextToken().trim();
                if (!sample.toUpperCase().startsWith("TCGA-")) {
                    continue;
                }
                int dash1 = sample.indexOf('-');
                if (dash1 < 0) {
                    continue;
                }
                int dash2 = sample.indexOf('-', dash1 + 1);
                if (dash2 < 0) {
                    continue;
                }
                int dash3 = sample.indexOf('-', dash2 + 1);
                if (dash3 < 0) {
//                    continue;
                    //user entered patient ID instead of full sample. That's OK, we'll just assume * for the remainder
                    sample = sample + "-*";
                    dash3 = sample.indexOf('-', dash2 + 1);
                }

                String collectionCenter = sample.substring(dash1 + 1, dash2);
                String patientId = sample.substring(dash2 + 1, dash3);
                String sampleType;
                if (sample.charAt(dash3 + 1) == '*') {
                    sampleType = "*";
                } else {
                    sampleType = sample.substring(dash3 + 1, dash3 + 3);
                }

                //"blank" conditions that will throw this criteria away, including all wildcards
                if (collectionCenter.length() == 0 && patientId.length() == 0 && sampleType.length() == 0) {
                    continue;
                }
                if (collectionCenter.equals("*") && patientId.equals("*") && sampleType.equals("*")) {
                    continue;
                }
                SampleCriterion newCrit = new SampleCriterion();
                //leave null for both blanks and wildcards
                newCrit.collectionCenter = (collectionCenter.length() > 0 && !collectionCenter.equals("*") ? collectionCenter : null);
                newCrit.patientId = (patientId.length() > 0 && !patientId.equals("*") ? patientId : null);
                newCrit.sampleType = (sampleType.length() > 0 && !sampleType.equals("*") ? sampleType : null);

                //intern for fast comparison
                if (newCrit.collectionCenter != null) {
                    newCrit.collectionCenter = newCrit.collectionCenter.intern();
                }
                if (newCrit.patientId != null) {
                    newCrit.patientId = newCrit.patientId.intern();
                }
                if (newCrit.sampleType != null) {
                    newCrit.sampleType = newCrit.sampleType.intern();
                }
                //see if we already added it, e.g. from the drop-downs
                SampleCriterion existingCrit = null;
                if (sampleCriteria != null) {
                    for (SampleCriterion sc : sampleCriteria) {
                        if (sc.equals(newCrit)) {
                            existingCrit = sc;
                            break;
                        }
                    }
                }
                if (existingCrit == null) {
                    newCrit.fromTextList = fromTextList;
                    tempList.add(newCrit);
                } else {
                    //only set it in the case of fromTextList=true. Allows it to be in both dropdowns and text list
                    if (fromTextList) {
                        existingCrit.fromTextList = true;
                    }
                }
            }
            //eliminate any dupes in this set (e.g. in the same document upload)
            int tempListSize = tempList.size();
            boolean[] dupe = new boolean[tempListSize];
            for (int i1 = 0; i1 < tempListSize - 1; i1++) {
                for (int i2 = i1 + 1; i2 < tempListSize; i2++) {
                    SampleCriterion sc1 = tempList.get(i1);
                    SampleCriterion sc2 = tempList.get(i2);
                    if (sc1.equals(sc2)) {
                        dupe[i2] = true;
                    }
                }
            }
            int idupe = tempList.size() - 1;
            while (idupe >= 0) {
                if (dupe[idupe]) {
                    tempList.remove(idupe);
                }
                idupe--;
            }
            //convert to Java array for speed
            if (tempList.size() > 0) {
                if (sampleCriteria == null) {
                    sampleCriteria = new SampleCriterion[tempList.size()];
                    tempList.toArray(sampleCriteria);
                } else {
                    SampleCriterion[] oldArray = sampleCriteria;
                    sampleCriteria = new SampleCriterion[tempList.size() + oldArray.length];
                    System.arraycopy(oldArray, 0, sampleCriteria, 0, oldArray.length);
                    int ii = oldArray.length;
                    for (SampleCriterion crit : tempList) {
                        sampleCriteria[ii++] = crit;
                    }
                }
            }
        }
    }

    //free-form list of samples/barcodes, from the text area on the form
    //We keep track of it separately from the sampleCriteria form, so we can repopulate the form

    public String getSampleList() {
        return getSampleString('\n');
    }

    //called from form's text area - also called from setSampleListFile() for file upload.
    //Either way the extracted sample IDs get displayed in the text area

    public void setSampleList(String sampleList) {
        if (sampleList == null) sampleList = "";
        parseSampleList(sampleList.trim(), true);
    }

    //file upload

    public void setSampleListFile(String sampleListFile) {
        //we're not going to store the contents separate from the sampleCriteria array.
        //The file could be large and that would just waste memory.
        //So just parse directly into sampleCriteria and display the extracted
        //sample IDs in the text area
        setSampleList(sampleListFile);
    }

    //samples from dropdowns on the form - we keep track of them separately from
    //the sampleCriteria array, so we can repopulate the form

    public void setSamplesFromDropdowns(String samplesFromDropdowns) {
        this.samplesFromDropdowns = samplesFromDropdowns;
        parseSampleList(samplesFromDropdowns, false);
    }

    public String[] getUnmatchedSamples() {
        String[] ret = new String[0];
        if (sampleCriteria != null) {
            int size = 0;
            for (int i = 0; i < sampleCriteria.length; i++) {
                if (!sampleCriteria[i].hit) {
                    size++;
                }
            }
            ret = new String[size];
            int ihit = 0;
            for (int i = 0; i < sampleCriteria.length; i++) {
                if (!sampleCriteria[i].hit) {
                    ret[ihit++] = sampleCriteria[i].toString();
                }
            }
        }
        return ret;
    }

    public String getPlatformType() {
        return getstr(platformType);
    }

    public String[] getPlatformTypes() {
        return parseValue(getPlatformType());
    }

    public void setPlatformType(final String platformType) {
        this.platformType = setstr(platformType);
    }

    public String getCenter() {
        return getstr(center);
    }

    public String[] getCenters() {
        return parseValue(getCenter());
    }

    public void setCenter(String center) {
        this.center = setstr(center);
    }

    public String getPlatform() {
        return getstr(platform);
    }

    public String[] getPlatforms() {
        return parseValue(getPlatform());
    }

    public void setPlatform(String platform) {
        this.platform = setstr(platform);
    }

    public String getLevel() {
        return getstr(level);
    }

    public String[] getLevels() {
        return parseValue(getLevel());
    }

    public void setLevel(String level) {
        this.level = setstr(level);
    }

    //callback from the DAMFilterModel
    //Applies filter to each cell

    public boolean cellMatchesFilter(Cell cell, Header sampleHeader, Header levelHeader) {
        //have to put sample filter first, even though it's the slowest
        if (sampleCriteria != null && !matchesSampleFilter(sampleHeader)) {
            return false;
        }
        if (cell.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE)) {
            //always filter out NA cells
            return false;
        }

        if (platformType != null
                && !platformType.contains("," + levelHeader.getParentHeader().getParentHeader().getName() + ",")) {
            return false;
        }
        String damHeaderFilter = levelHeader.getParentHeader().getName();
        if (center != null && !matchesCenterFilter(damHeaderFilter)) {
            return false;
        }
        if (platform != null && !matchesPlatformFilter(damHeaderFilter)) {
            return false;
        }
        if (level != null && level.length() > 0
                && !level.contains("," + levelHeader.getName() + ",")) {
            return false;
        }
        if (availability != null
                && !availability.contains("," + cell.getAvailability() + ",")) {
            return false;
        }
        if (protectedStatus != null) { //handled a bit differently since the actual Cell property is boolean
            //always exclude NA and N cells, they have no protected status
            String avail = cell.getAvailability();
            if (avail.equals(DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE) || avail.equals(DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE)) {
                return false;
            }
            String cellProt = (cell.isProtected() ? "P" : "N");
            if (!protectedStatus.contains("," + cellProt + ",")) {
                return false;
            }
        }
        //for tissue type, have to exclude those with null values
        if (tumorNormal != null) {
            String tt = cell.getTumorNormal();
            if (tt == null || !tumorNormal.contains("," + tt + ",")) {
                return false;
            }
        }
        if (batch != null && batch.length() > 0
                && !batch.contains("," + sampleHeader.getParentHeader().getName() + ",")) {
            return false;
        }
        if (startDate != null || endDate != null) {
            if (cell.getLatestDateAdded() == null || cell.getEarliestDateAdded() == null) {
                return false;
            } else {
                // if the latest date is before the start, doesn't pass
                if (startDate != null && cell.getLatestDateAdded().before(startDate)) {
                    return false;
                }
                // if the earliest date is after the end date, doesn't pass
                if (endDate != null && cell.getEarliestDateAdded().after(endDate)) {
                    return false;
                }
            }
        }
        return true;
    }

    //can match either the whole center.platform string, e.g. "3.5" or just the first part: "3"

    private boolean matchesCenterFilter(String cellCenterPlatform) {
        cellCenterPlatform = "," + cellCenterPlatform + ",";
        boolean matched = center.contains(cellCenterPlatform);
        if (!matched) {
            int comma = cellCenterPlatform.indexOf('.');
            if (comma >= 0) {
                cellCenterPlatform = cellCenterPlatform.substring(0, comma) + ",";
            }
            matched = center.contains(cellCenterPlatform);
        }
        return matched;
    }

    //matches just the last part of the center.platform string

    private boolean matchesPlatformFilter(String cellCenterPlatform) {
        boolean matched = false;
        int comma = cellCenterPlatform.indexOf('.');
        if (comma >= 0) {
            cellCenterPlatform = "," + cellCenterPlatform.substring(comma + 1) + ",";
            matched = platform.contains(cellCenterPlatform);
        }
        return matched;
    }

    //have to match ALL the sample parts for each row

    private boolean matchesSampleFilter(Header sampleHeader) {   //(String sampleId) {
        if (sampleCriteria == null || sampleCriteria.length == 0) {
            return true;
        }
        for (int i = 0; i < sampleCriteria.length; i++) {
            SampleCriterion scrit = sampleCriteria[i];
            String sample = sampleHeader.getName();
            if (!textmatch(scrit.collectionCenter, sample.substring(5, 7))) {
                continue;
            }
            if (!textmatch(scrit.patientId, sample.substring(8, 12))) {
                continue;
            }
            if (!textmatch(scrit.sampleType, sample.substring(13, 15))) {
                continue;
            }
            //made it this far, must be a match
            scrit.hit = true;
            return true;
        }
        return false;
    }

    //special match: if s1 is null, return true

    private boolean textmatch(String s1, String s2) {
        if (s1 == null || s1.length() == 0) {
            return true;
        }
        return s1.equals(s2);
    }

    // parses the value string into an array

    protected String[] parseValue(String val) {
        if (val.startsWith(",")) {
            val = val.substring(1);
        }
        if (val.endsWith(",")) {
            val = val.substring(0, val.length() - 1);
        }
        return val.split(",");
    }

    //an "easy" way to validate the arguments. Might replace with formal Validator class at some point..

    public void validate() throws IllegalArgumentException {
        if (diseaseType == null) {
            throw new IllegalArgumentException("Disease cannot be null");
        }
        Disease disease = DAMUtils.getInstance().getDisease(diseaseType);
        if (disease == null) {
            throw new IllegalArgumentException("Disease type '" + diseaseType + "' is unknown");
        } else if (!disease.isActive()) {
            throw new IllegalArgumentException("Disease type '" + diseaseType + "' is not yet available");
        }
    }

    /**
     * Creates a descriptive string which lists all the settings for this filter request.  Used by the DAM to
     * include filter details in emails to users.
     *
     * @return string representation of the filter
     */
    public String toString() {
        final StringBuilder emailText = new StringBuilder();

        final String disease = getDiseaseType();
        if (disease != null) {
            emailText.append("Disease: ").append(disease).append("\n\n");
        }
        String criteriaVal = getCommaSeparatedValue(getPlatformTypes(), ConstantValues.PLATFORM_TYPE);
        if (criteriaVal != null) {
            emailText.append("Platform Type: ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getCommaSeparatedValue(getCenters(), ConstantValues.CENTER);
        if (criteriaVal != null) {
            emailText.append("Center (Platform): ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getCommaSeparatedValue(getBatches(), null);
        if (criteriaVal != null) {
            emailText.append("Batch: ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getCommaSeparatedValue(getLevels(), null);
        if (criteriaVal != null) {
            emailText.append("Level: ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getCommaSeparatedValue(getAvailabilities(), ConstantValues.AVAILABILITY);
        if (criteriaVal != null) {
            emailText.append("Availability: ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getSampleString();
        if (isNonEmpty(criteriaVal)) {
            emailText.append("Sample List: ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getCommaSeparatedValue(getProtectedStatuses(), ConstantValues.PROTECTEDSTATUS);
        if (criteriaVal != null) {
            emailText.append("Protection Status: ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getStartDate();
        if (isNonEmpty(criteriaVal)) {
            emailText.append("Submitted Since: ").append(criteriaVal).append("\n\n");
        }
        criteriaVal = getEndDate();
        if (isNonEmpty(criteriaVal)) {
            emailText.append("Submitted Upto: ").append(criteriaVal).append("\n\n");
        }
        return emailText.toString();
    }

    private boolean isNonEmpty(final String stringValue) {
        return (stringValue != null) && (stringValue.length() != 0);
    }

    private String getCommaSeparatedValue(final String[] valueList, final String valueType) {
        StringBuilder commaSeparatedList = null;
        if (valueList != null && valueList.length > 0) {
            String displayVal;
            for (final String valStr : valueList) {
                if ((valStr != null) && (valStr.length() != 0)) {
                    displayVal = getDisplayValue(valStr, valueType);
                    if (commaSeparatedList == null) {
                        commaSeparatedList = new StringBuilder(displayVal);
                    } else {
                        commaSeparatedList.append(", ").append(displayVal);
                    }
                }
            }
        }
        return (commaSeparatedList != null) ? commaSeparatedList.toString() : null;
    }

    private String getDisplayValue(final String valStr, final String valueType) {
        String displayVal = valStr;
        if (valueType != null) {
            if (valueType.equals(ConstantValues.PROTECTEDSTATUS)) {
                displayVal = getProtectionStatusDisplayValue(valStr);
            } else if (valueType.equals(ConstantValues.AVAILABILITY)) {
                displayVal = getAvailabilityDisplayValue(valStr);
            } else if (valueType.equals(ConstantValues.CENTER)) {
                displayVal = DataAccessMatrixJSPUtil.lookupHeaderText(Header.HeaderCategory.Center, valStr);
            } else if (valueType.equals(ConstantValues.PLATFORM_TYPE)) {
                displayVal = DataAccessMatrixJSPUtil.lookupHeaderText(Header.HeaderCategory.PlatformType, valStr);
            }
        }
        return displayVal;
    }

    private String getAvailabilityDisplayValue(final String value) {
        String returnVal = value;
        if (value != null) {
            if (value.equalsIgnoreCase(ConstantValues.AVAILABILITY_AVAILABLE)) {
                returnVal = DAMResourceBundle.getMessage("legend.Available");
            } else if (value.equalsIgnoreCase(ConstantValues.AVAILABILITY_PENDING)) {
                returnVal = DAMResourceBundle.getMessage("label.pending");
            } else if (value.equalsIgnoreCase(ConstantValues.AVAILABILITY_NOTAVAILABLE)) {
                returnVal = DAMResourceBundle.getMessage("label.notAvailable");
            }
        }
        return returnVal;
    }

    private String getProtectionStatusDisplayValue(final String value) {
        String returnVal = value;
        if (value != null) {
            if (value.equalsIgnoreCase(ConstantValues.PROTECTEDSTATUS_PROTECTED_VALUE)) {
                returnVal = ConstantValues.PROTECTEDSTATUS_PROTECTED;
            } else if (value.equalsIgnoreCase(ConstantValues.PROTECTEDSTATUS_PUBLIC_VALUE)) {
                returnVal = ConstantValues.PROTECTEDSTATUS_PUBLIC;
            }
        }
        return returnVal;
    }
}
