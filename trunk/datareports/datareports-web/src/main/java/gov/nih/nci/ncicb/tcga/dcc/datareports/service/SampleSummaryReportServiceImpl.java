package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.SampleSummaryReportDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATFORM;

/**
 * This class creates a model of the Sample Summary Report based on tumor type
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */


@Service
public class SampleSummaryReportServiceImpl implements SampleSummaryReportService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private SampleSummaryReportDAO daoImpl;

    @Autowired
    private DatareportsService commonService;

    @Autowired
    private CenterQueries centerQueries;

    /**
     * A query to return all of the basic center information so we know who we are emailing
     *
     * @return a List of centers
     */
    @Cached
    public List<Center> getCenters() {
        return centerQueries.getCenterList();
    }

    @Cached
    public List<SampleSummary> getSampleSummaryReport() {
        return daoImpl.getSampleSummaryRows();
    }

    public List<SampleSummary> getSampleSummaryReport(final String diseaseAbbr) {
        return daoImpl.getSampleSummaryRows(diseaseAbbr);
    }

    public List<SampleSummary> getFilteredSampleSummaryReport(final String centerName) {
        final List<SampleSummary> samples = getSampleSummaryReport();
        final List<SampleSummary> filtered = new ArrayList<SampleSummary>();
        for (final SampleSummary sample : samples) {
            if (sample.getCenterName().equals(centerName)) {
                filtered.add(sample);
            }
        }
        return filtered;
    }

    public List<Sample> getDrillDown(final SampleSummary sampleSummary, final String property) {
        if (sampleSummary != null) {
            final String tumor = sampleSummary.getDisease();
            final String centerName = sampleSummary.getCenterName();
            final String centerType = sampleSummary.getCenterType();
            final String portionAnalyte = sampleSummary.getPortionAnalyte();
            final String platform = sampleSummary.getPlatform();
            List<Sample> samples = null;
            if (SampleSummaryReportConstants.BCR_SENT.equals(property)) {
                samples = daoImpl.getSamplesForTotalSamplesBCRSent(tumor, centerName, centerType,
                        portionAnalyte);
            } else if (SampleSummaryReportConstants.CENTER_SENT.equals(property)) {
                samples = daoImpl.getSamplesForTotalSamplesCenterSent(tumor, centerName, centerType,
                        portionAnalyte, platform);
            } else if (SampleSummaryReportConstants.BCR_UNKNOWN.equals(property)) {
                samples = daoImpl.getSamplesForTotalSamplesUnaccountedForBCR(tumor, centerName, centerType,
                        portionAnalyte, platform);
            } else if (SampleSummaryReportConstants.CENTER_UNKNOWN.equals(property)) {
                samples = daoImpl.getSamplesForTotalSamplesUnaccountedForCenter(tumor, centerName, centerType,
                        portionAnalyte, platform);
            } else if (SampleSummaryReportConstants.LEVEL1_SS.equals(property)) {
                samples = daoImpl.getSamplesForLevelTotal(tumor, centerName, centerType, portionAnalyte,
                        platform, 1);
            } else if (SampleSummaryReportConstants.LEVEL2_SS.equals(property)) {
                samples = daoImpl.getSamplesForLevelTotal(tumor, centerName, centerType, portionAnalyte,
                        platform, 2);
            } else if (SampleSummaryReportConstants.LEVEL3_SS.equals(property)) {
                samples = daoImpl.getSamplesForLevelTotal(tumor, centerName, centerType, portionAnalyte,
                        platform, 3);
            }
            return samples;
        } else {
            return null;
        }
    }

    public SampleSummary findSampleSummary(
            final List<SampleSummary> summarySamples,
            final String tumor, final String center, final String portionAnalyte, final String platform) {
        for (final SampleSummary ss : summarySamples) {
            if (ss.getDisease().equals(tumor)
                    && ss.getCenter().equals(center)
                    && ss.getPortionAnalyte().equals(portionAnalyte)
                    && ss.getPlatform().equals(platform)) {
                return ss;
            }
        }
        return null;
    }

    public Map<String, Comparator> getSampleSummaryComparator() {
        return commonService.getComparatorMap(SampleSummary.class, SampleSummaryReportConstants.SAMPLE_SUMMARY_COLS);
    }

    public Map<String, Comparator> getSampleComparator(boolean bcr) {
        if (bcr) {
            return commonService.getComparatorMap(Sample.class, SampleSummaryReportConstants.SAMPLE_BCR_COLS);
        } else {
            return commonService.getComparatorMap(Sample.class, SampleSummaryReportConstants.SAMPLE_CENTER_COLS);
        }
    }

    public List<SampleSummary> getFilteredSampleSummaryList(
            final List<SampleSummary> list,
            final List<String> disease, final List<String> center,
            final List<String> portionAnalyte, final List<String> platform,
            final List<String> levelFour) {
        final StringBuilder strLog = new StringBuilder();
        strLog.append("Filter used: disease:").append(disease).append(" center:")
                .append(center).append(" analyte:").append(portionAnalyte)
                .append(" platform:").append(platform).append(" levelFour:").append(levelFour);
        logger.debug(strLog);
        if (disease == null && center == null && portionAnalyte == null &&
                platform == null && levelFour == null) {
            return list; //quick test so we don't have to evaluate the predicates
        }
        //Cool predicates to do my sql behavior WHERE .. AND ... in java collections
        final List<Predicate> ssPredicateList = new LinkedList<Predicate>();

        commonService.genORPredicateList(SampleSummary.class, ssPredicateList, disease, DISEASE);
        commonService.genORPredicateList(SampleSummary.class, ssPredicateList, center, CENTER);
        commonService.genORPredicateList(SampleSummary.class, ssPredicateList, portionAnalyte, SampleSummaryReportConstants.PORTION_ANALYTE);
        commonService.genORPredicateList(SampleSummary.class, ssPredicateList, platform, PLATFORM);
        commonService.genORPredicateList(SampleSummary.class, ssPredicateList, levelFour, SampleSummaryReportConstants.LEVEL4_SS);

        final Predicate sampleSummaryPredicates = PredicateUtils.allPredicate(ssPredicateList);
        final List<SampleSummary> fList = (List<SampleSummary>) CollectionUtils.select(list, sampleSummaryPredicates);
        return fList;
    }

    @Cached
    public List<ExtJsFilter> getSampleSummaryFilterDistinctValues(final String getterString) {
        final Set<String> tmpSet = new LinkedHashSet<String>();
        final List<ExtJsFilter> bfList = new LinkedList<ExtJsFilter>();
        try {
            final Method getter = GetterMethod.getGetter(SampleSummary.class, getterString);
            for (final SampleSummary ss : getSampleSummaryReport()) {
                tmpSet.add(getter.invoke(ss).toString());
            }
            for (final String str : tmpSet) {
                bfList.add(new ExtJsFilter(str, str));
            }
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
            return null;
        }
        Collections.sort(bfList, commonService.comparatorExtJsFilter());
        return bfList;
    }

    public List<SampleSummary> processSampleSummary(final String centerName) {
        logger.debug("Center to process: " + centerName);
        final List<SampleSummary> ss;
        if (centerName != null && !centerName.equals("")) {
            String center = centerName;
            if (centerName.contains(" (")) {
                center = centerName.substring(0, centerName.indexOf(" ("));
            }
            ss = getFilteredSampleSummaryReport(center);
        } else {
            ss = getSampleSummaryReport();
        }
        return ss;
    }

    public Date getLatest(final List<SampleSummary> list) {
        return (list != null && list.size() > 0) ? list.get(0).getLastRefresh() : null;
    }
}//End of Class
