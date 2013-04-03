/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.common.util.AlphanumComparator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.ExtJsFilter;
import net.sf.json.JSONObject;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ASC;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CSV;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DESC;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.EMPTY_FORM_VALUES;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.EXCEL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SEPARATOR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TAB;

/**
 * class that implements the common uuid service interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class UUIDCommonServiceImpl<X> implements UUIDCommonService<X> {

    protected final Log logger = LogFactory.getLog(getClass());

    public List<X> getPaginatedList(final List<X> list, final int start, final int limit) {
        int page = (start + limit);
        if (page > getTotalCount(list)) {
            page = getTotalCount(list);
        }
        return (page != 0) ? list.subList(start, page) : list;
    }

    public int getTotalCount(final List list) {
        return (list == null) ? 0 : list.size();
    }

    public List<X> getSortedListValueFirst(final List<X> list, final Class clazz, final String getterMethod,
                                           final String value) {
        Collections.sort(list, new Comparator<X>() {
            public int compare(X o1, X o2) {
                try {
                    final Method m = GetterMethod.getGetter(clazz, getterMethod);
                    final String str1 = m.invoke(clazz.cast(o1)).toString();
                    final String str2 = m.invoke(clazz.cast(o2)).toString();
                    if (value.equalsIgnoreCase(str1)) {
                        return (value.equalsIgnoreCase(str2)) ? 0 : -1;
                    }
                    if (value.equalsIgnoreCase(str2)) {
                        return 1;
                    }
                    return str1.compareTo(str2);
                } catch (Exception e) {
                    logger.debug(FancyExceptionLogger.printException(e));
                    return 0;
                }
            }
        });
        return list;
    }

    public List<X> getSortedList(
            final List<X> list, final Map<String, Comparator> comparatorMap,
            final String sort, final String dir) {
        if (ASC.equals(dir)) {
            Collections.sort(list, comparatorMap.get(sort));
        } else if (DESC.equals(dir)) {
            Collections.sort(list, Collections.reverseOrder(comparatorMap.get(sort)));
        }
        return list;
    }

    public List<String> processJsonMultipleFilter(final String filter, final String jsonFilter) {
        final List<String> tmpList = adaptJsonFilter(filter, jsonFilter);
        return (tmpList.size() > 0) ? tmpList : null;
    }

    public String processJsonSingleFilter(final String filter, final String jsonFilter) {
        final List<String> tmpList = adaptJsonFilter(filter, jsonFilter);
        return (tmpList.size() > 0) ? tmpList.get(0) : null;
    }

    @Cached
    public List<String> adaptJsonFilter(final String filter, final String jsonFilter) {
        final List<String> res = new LinkedList<String>();
        if (EMPTY_FORM_VALUES.contains(jsonFilter)) {
            return res;
        }
        final JSONObject form = JSONObject.fromObject(jsonFilter);
        String val = null;
        if (form.containsKey(filter)) {
            val = form.getString(filter);
        }
        if (!StringUtils.isBlank(val)) {
            if (val.contains(SEPARATOR)) {
                String[] tab = val.split(SEPARATOR);
                for (int i = 0; i < tab.length; i++) {
                    res.add(tab[i].trim());
                }
            } else {
                res.add(val);
            }
        }
        return res;
    }

    public String breakoutJsonFilter(final String filter, final String jsonFilter) {
        final JSONObject form = JSONObject.fromObject(jsonFilter);
        String val = null;
        if (form.containsKey(filter)) {
            val = form.getString(filter);
        }
        return val;
    }

    public List<String> makeListFromString(String val) {
        LinkedList res = null;
        if (!StringUtils.isBlank(val)) {
            res = new LinkedList<String>();
            String[] tab = val.split(SEPARATOR);
            for (int i = 0; i < tab.length; i++) {
                res.add(tab[i].trim());
            }
        }
        return res;
    }

    public Map<String, String> buildReportColumns(final Map<String, String> colMap, final String columns) {
        final Map<String, String> resMap = new LinkedHashMap<String, String>();
        final String[] colArray = columns.split(SEPARATOR);
        for (int i = 0; i < colArray.length; i++) {
            String colId = colArray[i];
            if (colId != null) {
                resMap.put(colId, colMap.get(colId));
            }
        }
        return resMap;
    }

    public void genORPredicateList(
            final Class clazz, final List<Predicate> pList,
            final List<String> valueList, final String getter, final boolean multiple) {
        final List l1 = genListPredicates(clazz, valueList, getter, multiple);
        if (l1 != null) {
            pList.add(PredicateUtils.anyPredicate(l1));
        }
    }

    public List<Predicate> genListPredicates(final Class clazz, final List<String> valueList, final String getter,
                                             final boolean multiple) {
        List<Predicate> predList = null;
        if (valueList != null) {
            predList = new LinkedList<Predicate>();
            for (final String strValue : valueList) {
                predList.add(new Predicate() {
                    public boolean evaluate(Object o) {
                        try {
                            final Method m = GetterMethod.getGetter(clazz, getter);
                            if (multiple) {
                                final String tmp = m.invoke(clazz.cast(o)).toString();
                                String[] values = tmp.split(SEPARATOR);
                                for (int i = 0; i < values.length; i++) {
                                    if (strValue.equalsIgnoreCase(values[i])) {
                                        return true;
                                    }
                                }
                                return false;
                            } else {
                                return strValue.equalsIgnoreCase(m.invoke(clazz.cast(o)).toString());
                            }
                        } catch (NullPointerException npe) {
                            return false;
                        } catch (Exception e) {
                            logger.debug(FancyExceptionLogger.printException(e));
                            return false;
                        }
                    }
                });
            }
        }
        return predList;
    }

    public Predicate genDatePredicate(final Class clazz, final String getter,
                                      final boolean before, final String dateStr, final DateFormat dateFormat) {
        return new Predicate() {
            public boolean evaluate(Object o) {
                if (dateStr == null || "".equals(dateStr)) {
                    return true;
                }
                Date date;
                try {
                    date = dateFormat.parse(dateStr);
                } catch (ParseException e) {
                    return false;
                }
                if (date == null) {
                    return false;
                }
                try {
                    final Method m = GetterMethod.getGetter(clazz, getter);
                    if (before) {
                        return ((Date) m.invoke(clazz.cast(o))).before(date);
                    } else {
                        return ((Date) m.invoke(clazz.cast(o))).after(date);
                    }
                } catch (Exception e) {
                    logger.debug(FancyExceptionLogger.printException(e));
                    return false;
                }
            }
        };
    }

    @Cached
    public Map<String, Comparator> getComparatorMap(final Class clazz, final Map<String, String> colMap) {
        final Map<String, Comparator> compMap = new HashMap<String, Comparator>();
        final Comparator alphaNum = new AlphanumComparator();
        for (final Map.Entry<String, String> e : colMap.entrySet()) {
            compMap.put(e.getKey(), new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    try {
                        final Method getter = GetterMethod.getGetter(clazz, e.getKey());
                        final String str1 = getter.invoke(clazz.cast(o1)).toString();
                        final String str2 = getter.invoke(clazz.cast(o2)).toString();
                        if (str1 == null || str2 == null) {
                            return 0;
                        }
                        return alphaNum.compare(str1.toUpperCase(), str2.toUpperCase());
                    } catch (Exception e) {
                        logger.debug(FancyExceptionLogger.printException(e));
                        return 0;
                    }
                }
            });
        }
        return compMap;
    }

    public Comparator<ExtJsFilter> comparatorExtJsFilter() {
        final Comparator alphaNum = new AlphanumComparator();
        return new Comparator<ExtJsFilter>() {
            public int compare(final ExtJsFilter o1, final ExtJsFilter o2) {
                try {
                    return alphaNum.compare(o1.getText(), o2.getText());
                } catch (Exception e) {
                    logger.debug(FancyExceptionLogger.printException(e));
                    return 0;
                }
            }
        };
    }

    public ViewAndExtensionForExport getViewAndExtForExport(final String exportType) {
        final ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        if (EXCEL.equals(exportType)) {
            vae.setView(EXCEL);
            vae.setExtension(".xlsx");
        } else if (CSV.equals(exportType)) {
            vae.setView("txt");
            vae.setExtension(".csv");
        } else if (TAB.equals(exportType)) {
            vae.setView("txt");
            vae.setExtension(".txt");
        }
        return vae;
    }

    public String buildServerURL(final HttpServletRequest request) {
        final String scheme = request.getScheme();
        final String serverName = request.getServerName();
        final int serverPort = request.getServerPort();
        final StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if (serverPort != 80) {
            url.append(":").append(serverPort);
        }
        return url.toString();
    }

} //End of Class
