/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.ExtJsFilter;
import org.apache.commons.collections.Predicate;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Interface thats defines common method used by uuid components.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface UUIDCommonService<X> {

    /**
     * creates a sublist of list according to start and limit
     *
     * @param list  the list to be paginated
     * @param start begining of page
     * @param limit number of elements per page
     * @return a paginated list
     */
    public List<X> getPaginatedList(List<X> list, int start, int limit);

    /**
     * get the total of elements in a list
     *
     * @param list
     * @return a total of rows for the list
     */
    public int getTotalCount(List<X> list);

    /**
     * creates a sorted list of object according to direction and sort
     *
     * @param list          list to sort
     * @param comparatorMap map containing instances of comparators for the object
     * @param sort          value of the column to sort
     * @param dir           direction of the sort ASC or DESC
     * @return a sorted list of object
     */
    public List<X> getSortedList(List<X> list, Map<String, Comparator> comparatorMap, String sort, String dir);

    /**
     * creates a sorted list with input value first.
     *
     * @param list
     * @param clazz
     * @param getterMethod
     * @param value
     * @return a sorted list of object
     */
    public List<X> getSortedListValueFirst(final List<X> list, final Class clazz, final String getterMethod,
                                           final String value);

    /**
     * process the value from the filters saved in jsonlist according to the filters type
     * from the grid store in extjs
     *
     * @param filter     type of the filter: disease, center or platform, etc ..
     * @param jsonFilter json string coming from Extjs containing the filter information
     * @return the java list of filter values
     */
    public List<String> processJsonMultipleFilter(String filter, String jsonFilter);

    /**
     * process the value from the filter saved in jsonlist according to the filter type
     * from the grid store in extjs
     *
     * @param filter     type of the filter: disease, center or platform, etc ..
     * @param jsonFilter json string coming from Extjs containing the filter information
     * @return the value of filter
     */
    public String processJsonSingleFilter(String filter, String jsonFilter);

    /**
     * translate a json string into a list of string of filters
     *
     * @param filter     type of the filter: disease, center or platform, etc ..
     * @param jsonFilter json string coming from Extjs containing the filter information
     * @return the list of values of filter
     */
    public List<String> adaptJsonFilter(String filter, String jsonFilter);

    /**
     * breakout a json filter into value strings
     *
     * @param filter
     * @param jsonFilter
     * @return value of filter
     */
    public String breakoutJsonFilter(String filter, String jsonFilter);

    /**
     * build a map containing the columns name and getters according to the columns parameter
     * from the grid store in extjs
     *
     * @param colMap  map containing all the columns Id and columns header Text
     * @param columns comma separated string of selected columns
     * @return the map of columns name and getter
     */
    public Map<String, String> buildReportColumns(Map<String, String> colMap, String columns);

    /**
     * generate a list of predicates with the OR predicate assigned to them
     *
     * @param clazz     class of object to use predicates on
     * @param pList     list of predicates
     * @param valueList list of values
     * @param getter    name of the getter method of the object
     * @param multiple  boolean for multiple bean values
     * @return nothing
     */
    public void genORPredicateList(final Class clazz, final List<Predicate> pList, final List<String> valueList,
                                   final String getter, final boolean multiple);

    /**
     * generate a list of predicates for the given class and getter of the instance of the class.
     *
     * @param clazz     class of object to use predicates on
     * @param valueList list of values
     * @param getter    name of the getter method of the object
     * @param multiple  boolean for multiple bean values
     * @return list of predicates
     */
    public List<Predicate> genListPredicates(final Class clazz, final List<String> valueList, final String getter,
                                             final boolean multiple);

    /**
     * generate a date predicate for the given class and getter of the instance of the class
     *
     * @param clazz      class of object to use predicates on
     * @param getter     name of the getter method of the object
     * @param before     boolean to set before or after date
     * @param dateStr    actual string representation of date
     * @param dateFormat simple date format
     * @return
     */
    public Predicate genDatePredicate(final Class clazz, final String getter, final boolean before, final String dateStr,
                                      final DateFormat dateFormat);

    /**
     * generate a map of comparators for the given class to allow collection sorting by user input.
     *
     * @param clazz  class of object to use comparator on
     * @param colMap map of columns Id and display values
     * @return map of comparator with keys
     */
    public Map<String, Comparator> getComparatorMap(Class clazz, Map<String, String> colMap);

    /**
     * generate a exportViewandExt object from the exportType.
     *
     * @param exportType type of export
     * @return a filled ViewAndExtForExport
     */
    public ViewAndExtensionForExport getViewAndExtForExport(final String exportType);

    /**
     * util to make a list of string out of a comma separated values string
     *
     * @param val
     * @return list of string
     */
    public List<String> makeListFromString(String val);

    /**
     * Ext js fileter bean comparator
     *
     * @return comparator
     */
    public Comparator<ExtJsFilter> comparatorExtJsFilter();

    /**
     * build server url from a httpServlet request
     *
     * @param request
     * @return String of url
     */
    public String buildServerURL(HttpServletRequest request);

}//End of Interface
