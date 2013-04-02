/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.common.util.AlphanumComparator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import net.sf.json.JSONObject;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

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

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.ASC;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CSV;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DESC;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.EMPTY_FORM_VALUES;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PAGE_SIZE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.SEPARATOR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TAB;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TOTAL_COUNT;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.XL;

/**
 * class that implements the datareportService interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class DatareportsServiceImpl<X> implements DatareportsService<X> {

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
        if (val != null && !"".equals(val)) {
            if (val.contains(SEPARATOR)) {
                String[] tab = val.split(SEPARATOR);
                for (int i = 0; i < tab.length; i++) {
                    res.add(tab[i]);
                }
            } else {
                res.add(val);
            }
        }
        return res;
    }

    public Map<String, String> buildReportColumns(final Map<String, String> colMap, final String columns) {
        final Map<String, String> resMap = new LinkedHashMap<String, String>();
        if (StringUtils.isBlank(columns)) {
            return colMap;
        }
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
            final List<String> valueList, final String getter) {
        final List l1 = genListPredicates(clazz, valueList, getter);
        if (l1 != null) {
            pList.add(PredicateUtils.anyPredicate(l1));
        }
    }

    public List<Predicate> genListPredicates(final Class clazz, final List<String> valueList, final String getter) {
        List<Predicate> predList = null;
        if (valueList != null) {
            predList = new LinkedList<Predicate>();
            for (final String strValue : valueList) {
                predList.add(new Predicate() {
                    public boolean evaluate(Object o) {
                        try {
                            final Method m = GetterMethod.getGetter(clazz, getter);
                            final Object obj = m.invoke(clazz.cast(o));
                            final String str = obj == null ? "" : obj.toString();
                            return strValue.equalsIgnoreCase(str);
                        } catch (Exception e) {
                            logger.debug(FancyExceptionLogger.printException(e));
                            return true;
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
                    return true;
                }
                if (date == null) {
                    return true;
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
                    return true;
                }
            }
        };
    }

    @Cached
    public Map<String, Comparator> getComparatorMap(final Class clazz, final Map<String, String> colMap) {
        final Map<String, Comparator> compMap = new HashMap<String, Comparator>();
        final Comparator alphaNum = new AlphanumComparator();
        for (final Map.Entry<String, String> entry : colMap.entrySet()) {
            compMap.put(entry.getKey(), new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    try {
                        final Method getter = GetterMethod.getGetter(clazz, entry.getKey());
                        final Object obj1 = getter.invoke(clazz.cast(o1));
                        final Object obj2 = getter.invoke(clazz.cast(o2));
                        final String str1 = obj1 == null ? "" : obj1.toString();
                        final String str2 = obj2 == null ? "" : obj2.toString();
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

    public ViewAndExtensionForExport getViewAndExtForExport(final String exportType) {
        final ViewAndExtensionForExport vae = new ViewAndExtensionForExport();
        if (XL.equals(exportType)) {
            vae.setView(XL);
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

    @Override
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

    @Override
    public void processDisplayTag(final String id, final List<X> list, final ModelMap model,
                                  final HttpServletRequest request) {
        final String page = request.getParameter(new ParamEncoder(id)
                .encodeParameterName(TableTagParameters.PARAMETER_PAGE));
        final int pageSize = 50;
        int pageNumber;
        try {
            pageNumber = Integer.parseInt(page);
        } catch (Exception e) {
            pageNumber = 0;
        }
        model.addAttribute(id + "List", getPaginatedList(list, (pageNumber - 1) * pageSize, pageSize));
        model.addAttribute(id + StringUtils.capitalize(TOTAL_COUNT), getTotalCount(list));
        model.addAttribute(id + StringUtils.capitalize(PAGE_SIZE), pageSize);
    }

} //End of Class
