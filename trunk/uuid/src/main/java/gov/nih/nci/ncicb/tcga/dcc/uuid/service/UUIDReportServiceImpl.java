/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.util.AlphanumComparator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.ASC;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN_CENTER_NAME;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN_CREATED_BY;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN_CREATION_DATE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN_DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN_GENERATION_METHOD;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN_LATEST_BARCODE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.COLUMN_UUID;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DESC;

/**
 * Implementation for the Interface for methods used by reports in UUID Manager
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Service
public class UUIDReportServiceImpl implements UUIDReportService {

    protected final Log logger = LogFactory.getLog(getClass());
    private final Comparator alphaNum = new AlphanumComparator();

    /**
     * Get a page from the specified list for the given start and limit values
     *
     * @param list  list to be paginated
     * @param start start index of the page
     * @param limit number of entries from the list
     * @return Paginated list
     */
    public List getPaginatedList(final List list, final int start, final int limit) {
        int page = (start + limit);
        if ((page) > getTotalCount(list)) {
            page = getTotalCount(list);
        }
        return list.subList(start, page);
    }

    public int getTotalCount(final List list) {
        return (list == null) ? 0 : list.size();
    }

    private int processComparator(final String str1, final String str2) {
        try {
            if (str1 == null || str2 == null) {
                return 0;
            }
            return alphaNum.compare(str1.toUpperCase(), str2.toUpperCase());
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
            return 0;
        }
    }

    /**
     * Returns the comparators used for sorting on different UUID Report columns
     *
     * @return map of Column Vs Comparator
     */
    @Cached
    public Map<String, Comparator> getUUIDDetailComparatorMap() {
        final Map<String, Comparator> comparatorMap = new HashMap<String, Comparator>();
        comparatorMap.put(COLUMN_UUID, new Comparator<UUIDDetail>() {
            public int compare(final UUIDDetail uuid1, final UUIDDetail uuid2) {
                return processComparator(uuid1.getUuid(), uuid2.getUuid());
            }
        });
        comparatorMap.put(COLUMN_CENTER_NAME, new Comparator<UUIDDetail>() {
            public int compare(final UUIDDetail uuid1, final UUIDDetail uuid2) {
                return processComparator(uuid1.getCenter().getCenterName(),
                        uuid2.getCenter().getCenterName());
            }
        });
        comparatorMap.put(COLUMN_CREATED_BY, new Comparator<UUIDDetail>() {
            public int compare(final UUIDDetail uuid1, final UUIDDetail uuid2) {
                return processComparator(uuid1.getCreatedBy(), uuid2.getCreatedBy());
            }
        });
        comparatorMap.put(COLUMN_GENERATION_METHOD, new Comparator<UUIDDetail>() {
            public int compare(final UUIDDetail uuid1, final UUIDDetail uuid2) {
                return processComparator(uuid1.getGenerationMethod().getDisplayValue(),
                        uuid2.getGenerationMethod().getDisplayValue());
            }
        });
        comparatorMap.put(COLUMN_CREATION_DATE, new Comparator<UUIDDetail>() {
            public int compare(final UUIDDetail uuid1, final UUIDDetail uuid2) {
                return processComparator(uuid1.getCreationDate().toString(),
                        uuid2.getCreationDate().toString());
            }
        });
        comparatorMap.put(COLUMN_LATEST_BARCODE, new Comparator<UUIDDetail>() {
            public int compare(final UUIDDetail uuid1, final UUIDDetail uuid2) {
                return processComparator(uuid1.getLatestBarcode(), uuid2.getLatestBarcode());
            }
        });
        comparatorMap.put(COLUMN_DISEASE, new Comparator<UUIDDetail>() {
            public int compare(final UUIDDetail uuid1, final UUIDDetail uuid2) {
                return processComparator(uuid1.getDiseaseAbbrev(), uuid2.getDiseaseAbbrev());
            }
        });
        return comparatorMap;
    }

    /**
     * Sorts the data in the specified list on the column specified by sortColumn
     * and in the direction specified by direction
     *
     * @param list       list to be sorted
     * @param sortColumn column to be sorted
     * @param direction  direction of sorting ASC/DESC
     */
    public void sortList(final List<UUIDDetail> list, final String sortColumn, final String direction) {
        if (list != null) {
            final Map<String, Comparator> comparatorMap = getUUIDDetailComparatorMap();
            if (ASC.equals(direction)) {
                Collections.sort(list, comparatorMap.get(sortColumn));
            } else if (DESC.equals(direction)) {
                Collections.sort(list, Collections.reverseOrder(comparatorMap.get(sortColumn)));
            }
        }
    }

}
