/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.generation;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility class to process biotab data
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BioTabDataProcessorUtil {
    public static final String DAY_OF = "day_of_";
    public static final String MONTH_OF = "month_of_";
    public static final String YEAR_OF = "year_of_";
    public static final String DATE_OF = "date_of_";

    public final static String[] dateComponents = {
            DAY_OF,
            MONTH_OF,
            YEAR_OF
    };

    /**
     *  Converts day_of, month_of and year_of data into date_of data
     * @param bioTabData
     * @return Updated Map which contains date_of elements instead of day_of,month_of and year_of elements
     */

    public Map<String,String> transformDayMonthYearToDate(final Map<String,String> bioTabData){
        final Map<String,String> dateMap = new HashMap<String,String>();
        final List<String> deleteKeyList = new ArrayList<String>();

        for(final String key: bioTabData.keySet()){
            if(!deleteKeyList.contains(key)){
                final String dateObject = getDateObject(key);
                if(dateObject != null){
                    // Process for concatenation only those dates that have all three elements
                    if(bioTabData.containsKey(DAY_OF+dateObject) &&
                            bioTabData.containsKey(MONTH_OF+dateObject) &&
                            bioTabData.containsKey(YEAR_OF+dateObject)) {

                        deleteKeyList.add(DAY_OF+dateObject);
                        deleteKeyList.add(MONTH_OF+dateObject);
                        deleteKeyList.add(YEAR_OF+dateObject);

                        dateMap.put(DATE_OF+dateObject,getDate(bioTabData,dateObject));

                    }
                }
            }
        }
        // remove all the day_of, month_of and year_of data
        for(final String keyToBeDeleted: deleteKeyList){
            bioTabData.remove(keyToBeDeleted);

        }
        // add date
        bioTabData.putAll(dateMap);

        return bioTabData;
    }

    /**
     * Converts day_of, month_of and year_of elements into date_of elements
     * @param bioTabDataColumnHeader
     * @return  Updated list with date_of elements
     */
    public List<String> transformDayMonthYearToDateColumnHeader(final List<String> bioTabDataColumnHeader){
        final Map<String,String> emptyDataMap = new HashMap<String, String>();
        for(final String key:bioTabDataColumnHeader){
            emptyDataMap.put(key,"");
        }
        final Map<String,String> transformedData = transformDayMonthYearToDate(emptyDataMap);
        return new ArrayList<String>(transformedData.keySet());
    }


    /**
     * returns date in YYYY-MM-DD format.
     * For partial date:
     * - returns "NA" if year is not available
     * - returns "NA" if month is not available
     * - returns YYYY-00-00 if month and day are not available
     * - returns YYYY-MM-00 if day is not available
     *
     * @param bioTabData
     * @param dateObject
     * @return
     */
    private String getDate(final Map<String,String> bioTabData, final String dateObject){
        final StringBuffer date = new StringBuffer();
        String  year = bioTabData.get(YEAR_OF + dateObject);
        String  month = bioTabData.get(MONTH_OF + dateObject);
        String day = bioTabData.get(DAY_OF + dateObject);

        if(!StringUtils.isNumeric(year) || (StringUtils.isNumeric(day)) && !(StringUtils.isNumeric(month))){
            date.append("NA");
        }else{
            date.append(year)
                    .append("-")
                    .append((StringUtils.isNumeric(month))?month:"00")
                    .append("-")
                    .append((StringUtils.isNumeric(day))?day:"00");
        }
        return date.toString();
    }



    private String getDateObject(final String key){

        for(final String dateComponent: dateComponents){
            if(key.startsWith(dateComponent) ){
                return key.substring(dateComponent.length());
            }
        }
        return null;
    }




}
