/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view.request;

import gov.nih.nci.ncicb.tcga.dcc.dam.view.Cell;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: nanans
 * Date: Sep 11, 2008
 * Time: 5:04:53 PM
 * This interface was created for testing purposes.
 */
public interface FilterRequestI {

    String DATE_FORMAT = "MM/dd/yyyy";
    String GOTO_PARAM = "goto";
    String GOTO_MATRIX = "matrix";
    String GOTO_FILETREE = "filetree";

    FilterRequest.Mode getMode();

    void setMode(FilterRequest.Mode mode);

    String getDiseaseType();

    void setDiseaseType(String diseaseType);

    String getAvailability();

    String[] getAvailabilities();

    void setAvailability(String availability);

    String getShowCompleteRowsOnly();

    void setShowCompleteRowsOnly(String showCompleteRowsOnly);

    String getBatch();

    String[] getBatches();

    void setBatch(String batch);

    String getStartDate();

    void setStartDate(String startDateStr) throws ParseException;

    String getEndDate();

    void setEndDate(String endDateStr) throws ParseException;

    String getProtectedStatus();

    String[] getProtectedStatuses();

    void setProtectedStatus(String protectedStatus);

    String getTumorNormal();

    String[] getTissueTypes();

    void setTumorNormal(String tumorNormal);

    long getMillis();

    void setMillis(long millis);

    String[] getSampleCollectionArray();

    String[] getSamplePatientIdArray();

    String[] getSampleSampleTypeArray();//if sampleCriteria is just a bunch of empty array values, null it out for speed

    void eliminateNullSampleCriteria();//called from usage logging system

    String getSampleString();//free-form list of samples/barcodes, from the text area on the form

    //We keep track of it separately from the sampleCriteria form, so we can repopulate the form
    String getSampleList();//called from form's text area - also called from setSampleListFile() for file upload.

    //Either way the extracted sample IDs get displayed in the text area
    void setSampleList(String sampleList);//file upload

    void setSampleListFile(
            String sampleListFile);//samples from dropdowns on the form - we keep track of them separately from

    //the sampleCriteria array, so we can repopulate the form
    void setSamplesFromDropdowns(String samplesFromDropdowns);

    String[] getUnmatchedSamples();

    String getPlatformType();

    String[] getPlatformTypes();

    void setPlatformType(String platformType);

    String getCenter();

    String[] getCenters();

    void setCenter(String center);

    String getPlatform();

    String[] getPlatforms();

    void setPlatform(String platform);

    String getLevel();

    String[] getLevels();

    void setLevel(String level);//callback from the DAMFilterModel

    //Applies filter to each cell
    boolean cellMatchesFilter(Cell cell, Header sampleHeader,
                              Header levelHeader);//an "easy" way to validate the arguments. Might replace with formal Validator class at some point..

    void validate() throws IllegalArgumentException;

    enum Mode {

        ApplyFilter, Clear, NoOp
    }
}
