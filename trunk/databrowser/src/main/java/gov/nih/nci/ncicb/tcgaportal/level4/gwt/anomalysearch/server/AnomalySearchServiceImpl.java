/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallbackImpl;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesGetter;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.export.ExportData;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchService;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.util.Locations;
import gov.nih.nci.ncicb.tcgaportal.level4.util.ResultsPagingProcessor;
import gov.nih.nci.ncicb.tcgaportal.level4.util.SpringBeanGetter;
import gov.nih.nci.ncicb.tcgaportal.level4.util.TooltipContentHelper;
import gov.nih.nci.ncicb.tcgaportal.pathway.util.PathwayDiagramHandler;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import org.springframework.beans.BeansException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server-side implementation of GWT interface. This runs as a servlet in the application server.
 * Methods are resolved by means of reflection in the GWT RemoteServiceServlet implementation.
 * This servlet is activated by means of web.xml code such as:
 * <servlet>
 * <servlet-name>AnomalySearchService</servlet-name>
 * <servlet-class>anomalysearch.server.AnomalySearchServiceImpl</servlet-class>
 * <init-param>
 * <param-name>appcontext</param-name>
 * <param-value>/WEB-INF/applicationContext-jdbc.xml</param-value>
 * </init-param>
 * </servlet>
 * <servlet-mapping>
 * <servlet-name>AnomalySearchService</servlet-name>
 * <url-pattern>/anomalysearch.AnomalySearch/anomalysearch.AnomalySearch/AnomalySearchService</url-pattern>
 * </servlet-mapping>
 * <p/>
 * The appcontext parameter points to the location of a Spring XML file, used to instantiate
 * beans that are called from this class. This mechanism is used especially for database-activated classes.
 * <p/>
 * The url-pattern is a means for the GWT client to communicate specifically with this servlet instance. The client
 * must use this same pattern when making HTTP connections to the server.
 *
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

//todo  many protected methods here that should be private. This class has no subclasses
public class AnomalySearchServiceImpl extends RemoteServiceServlet implements AnomalySearchService {
    static final String SESSIONATTRIBUTE_CALLBACK_NAME = "l4callback";
    static final String SESSIONATTRIBUTE_RESULTS_PAGING_PROCESSOR = "rpp";

    //todo  would be so much nicer to have a singleton logger rather than have to pass this into callback
    private ProcessLogger logger = new ProcessLogger();

    protected Level4QueriesGetter getLevel4QueriesGetter() {
        return (Level4QueriesGetter) SpringBeanGetter.getInstance(getAppContextPath()).getBean("level4QueriesGetter");
    }

    protected ResultsPagingProcessor getResultsPagingProcessor() {
        HttpSession session = getThreadLocalRequest().getSession();
        ResultsPagingProcessor rpp = (ResultsPagingProcessor) session.getAttribute(SESSIONATTRIBUTE_RESULTS_PAGING_PROCESSOR);
        if (rpp == null) {
            //gets the one copy configured by Spring
            rpp = (ResultsPagingProcessor) SpringBeanGetter.getInstance(getAppContextPath()).getBean("resultsPagingProcessor");
            //return a copy that's per-user
            rpp = new ResultsPagingProcessor(rpp);
            session.setAttribute(SESSIONATTRIBUTE_RESULTS_PAGING_PROCESSOR, rpp);
        }
        return rpp;
    }

    protected Level4QueriesCallback makeNewCallback(final FilterSpecifier.ListBy listBy) {
        ResultsPagingProcessor pageproc = getResultsPagingProcessor();
        Level4QueriesCallback callback = new Level4QueriesCallbackImpl(pageproc, logger);
        storeCallback(callback, listBy);
        return callback;
    }

    public Results processFilter(final FilterSpecifier filter) throws SearchServiceException {
        try {
            stopRunningSearch(filter.getListBy());
            // create callback instance which is specific to this search
            // note this method will also store the callback!
            Level4QueriesCallback newCallback = makeNewCallback(filter.getListBy());

            //start the query going
            Level4Queries l4q = getLevel4QueriesGetter().getLevel4Queries(filter.getDisease());
            if (filter.getListBy() == FilterSpecifier.ListBy.Pathways) {
                //todo  unify these into one method of the DAO interface?
                l4q.getPathwayResults(filter, newCallback);
            } else {
                l4q.getAnomalyResults(filter, newCallback);
            }
            // return the first page (method will wait until it gets a full page)
            return newCallback.getPage(1);

        } catch (BeansException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        } catch (QueriesException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
    }

    protected void stopRunningSearch(final FilterSpecifier.ListBy listBy) {
        Level4QueriesCallback onlyTheGood = getCallback(listBy);
        if (onlyTheGood != null) {
            onlyTheGood.dieYoung();
            removeCallback(listBy);
        }
    }

    protected void storeCallback(final Level4QueriesCallback newCallback, final FilterSpecifier.ListBy listBy) {
        HttpSession session = getThreadLocalRequest().getSession();
        Map<FilterSpecifier.ListBy, Level4QueriesCallback> callbacks = (Map<FilterSpecifier.ListBy, Level4QueriesCallback>) session.getAttribute(SESSIONATTRIBUTE_CALLBACK_NAME);
        if (callbacks == null) {
            callbacks = new HashMap<FilterSpecifier.ListBy, Level4QueriesCallback>();
            session.setAttribute(SESSIONATTRIBUTE_CALLBACK_NAME, callbacks);
        }
        callbacks.put(listBy, newCallback);
    }

    protected void removeCallback(final FilterSpecifier.ListBy listBy) {
        HttpSession session = getThreadLocalRequest().getSession();
        Map<FilterSpecifier.ListBy, Level4QueriesCallback> callbacks = (Map<FilterSpecifier.ListBy, Level4QueriesCallback>) session.getAttribute(SESSIONATTRIBUTE_CALLBACK_NAME);
        if (callbacks != null) {
            callbacks.remove(listBy);
        }
    }

    protected Level4QueriesCallback getCallback(final FilterSpecifier.ListBy listBy) {
        Level4QueriesCallback ret = null;
        HttpSession session = getThreadLocalRequest().getSession();
        Map<FilterSpecifier.ListBy, Level4QueriesCallback> callbacks = (Map<FilterSpecifier.ListBy, Level4QueriesCallback>) session.getAttribute(SESSIONATTRIBUTE_CALLBACK_NAME);
        if (callbacks != null) {
            ret = callbacks.get(listBy);
        }
        return ret;
    }

    public List<Disease> getDiseases() throws SearchServiceException {
        List<Disease> diseases = new ArrayList<Disease>();
        Collection<String> diseaseNames = getLevel4QueriesGetter().getDiseaseNames();
        if (diseaseNames == null || diseaseNames.size() < 1) {
            throw new SearchServiceException("No diseases are defined for the application");
        }
        try {
            for (final String diseaseName : diseaseNames) {
                Level4Queries level4Queries = getLevel4QueriesGetter().getLevel4Queries(diseaseName);
                diseases.addAll(level4Queries.getDiseases());
            }

        } catch (QueriesException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
        return diseases;
    }

    public List<ColumnType> getColumnTypes(final String disease) throws SearchServiceException {
        Level4Queries level4Queries = getLevel4QueriesGetter().getLevel4Queries(disease);
        List<ColumnType> coltypes;
        List<ColumnType> ret = new ArrayList<ColumnType>();
        try {
            coltypes = level4Queries.getColumnTypes(disease);
            for (final ColumnType ctype : coltypes) {
                ret.add((ColumnType) ctype.cloneColumn());
            }
        } catch (QueriesException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
        return ret;
    }

    /**
     * Returns the real path to the Spring application context file, as specified in the appcontext
     * parameter in the web.xml.
     *
     * @return the real path to the Spring application context file
     */
    protected String getAppContextPath() {
        //get the appcontext file location and inject into SpringBeanGetter
        String appcontext = getServletConfig().getInitParameter("appcontext");
        if (appcontext == null) {
            throw new RuntimeException("No appcontext property specified in web.xml");
        }
        appcontext = getServletContext().getRealPath(appcontext);
        return appcontext;
    }

    public Results getResultsPage(final FilterSpecifier.ListBy listBy, final int page) throws SearchServiceException {
        try {
            Level4QueriesCallback callback = getCallback(listBy);
            if (callback == null) {
                throw new SearchServiceException("No callback method stored for session - possible timeout");
            }
            //get the page - may have to wait if page is not yet gathered from DAO
            return callback.getPage(page);
        } catch (QueriesException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
    }

    public Results setRowsPerPage(final FilterSpecifier.ListBy listBy, final int rowsPerPage) throws SearchServiceException {
        try {
            Level4QueriesCallback callback = getCallback(listBy);
            if (callback == null) {
                throw new SearchServiceException("No callback method stored for session - possible timeout");
            }
            if (rowsPerPage <= 0) {
                throw new SearchServiceException("RowsPerPage must be positive and nonzero");
            }
            callback.setRowsPerPage(rowsPerPage);
            return callback.getPage(1);
        } catch (QueriesException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
    }

    public SinglePathwayResults getSinglePathway(final SinglePathwaySpecifier sps) throws SearchServiceException {
        try {
            DiseaseContextHolder.setDisease(sps.getFilterSpecifier().getDisease());
            Level4Queries level4Queries = getLevel4QueriesGetter().getLevel4Queries(sps.getFilterSpecifier().getDisease());
            PathwayDiagramHandler diagramHandler = getPathwayDiagramHandler();
            SinglePathwayResults results = level4Queries.getSinglePathway(sps);
            results.setFinalRowCount(true); //needed for sorting; we don't have to set the other fields for paging

            //fetch highlighted pathway diagram
            List<String> bcgenes = new ArrayList<String>();
            for (int i = 0; i < results.getActualRowCount(); i++) {
                ResultRow row = results.getRow(i);
                Boolean matches = (Boolean) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH);
                if (matches != null && matches) {
                    bcgenes.add((String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE));
                }
            }
            String imgPath = diagramHandler.fetchPathwayImage(results.getName(), bcgenes);
            if (isRunningLocal()) {
                //need it to run local for development - on server will have apache alias mapping
                imgPath = getThreadLocalRequest().getContextPath() + imgPath;
            }
            results.setImagePath(imgPath);

            //so it deletes the image file after 30 seconds (after browser had a chance to retrieve it)
            diagramHandler.planDeletionOfImage(imgPath);

            return results;
        } catch (Exception e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
    }

    public Results getPivotPage(final FilterSpecifier.ListBy sourceListby, final String rowName, final FilterSpecifier filter) throws SearchServiceException {
        try {
            Level4Queries level4Queries = getLevel4QueriesGetter().getLevel4Queries(filter.getDisease());
            return level4Queries.getPivotResults(sourceListby, rowName, filter);
        } catch (QueriesException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
    }

    protected PathwayDiagramHandler getPathwayDiagramHandler() {
        return (PathwayDiagramHandler) SpringBeanGetter.getInstance(getAppContextPath()).getBean("pathwayDiagramHandler");
    }

    boolean isRunningLocal() {
        boolean ret = false;
        String local = getServletConfig().getInitParameter("local");
        if (local != null && local.equalsIgnoreCase("true")) {
            ret = true;
        }
        return ret;
    }

    public Results sortResults(final FilterSpecifier.ListBy listBy, final SortSpecifier sortspec) throws SearchServiceException {
        try {
            Level4QueriesCallback callback = getCallback(listBy);
            if (callback == null) {
                throw new SearchServiceException("No callback method stored for session - possible timeout");
            }
            //sort the results. If not all rows gathered, will wait for all rows to be gathered.
            callback.sortResults(sortspec);
            return callback.getPage(1);
        } catch (QueriesException e) {
            logger.logError(e);
            throw new SearchServiceException(e.getMessage());
        }
    }

    public String getUserGuideLocation() throws SearchServiceException {
        Locations locations = (Locations) SpringBeanGetter.getInstance(getAppContextPath()).getBean("locations");
        return locations.getUserGuideLocation();
    }

    private String getTooltipFileLocation() {
        Locations locations = (Locations) SpringBeanGetter.getInstance(getAppContextPath()).getBean("locations");
        String location = locations.getTooltipFileLocation();
        if (!location.startsWith("http:")) {
            if (!location.startsWith("/")) {
                location = "/" + location;
            }
            HttpServletRequest req = getThreadLocalRequest();
            location = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + location;
        }
        return location;
    }

    public TooltipTextMap getTooltipText() throws SearchServiceException {
        try {
            URL tooltipURL = new URL(getTooltipFileLocation());
            return TooltipContentHelper.createTooltipMap(tooltipURL);
        } catch (MalformedURLException e) {
            logger.logError(e);
            //return an empty map - the error isn't serious enough to terminate the application's,
            //user session and it will be recorded in the log
            return new TooltipTextMap();
        }
    }

    public String getOnlineHelpLocation() {
        Locations locations = (Locations) SpringBeanGetter.getInstance(getAppContextPath()).getBean("locations");
        return locations.getOnlineHelpLocation();
    }

    public void keepAlive() {
        //do nothing. Serves to prevent the session from timing out.
        //This lets us set the timeout to a small number like 5 minutes without losing active users.
    }

    public String exportResultsData(final FilterSpecifier.ListBy listBy, final String filename) throws SearchServiceException {

        //Get the results and write them to a file
        Level4QueriesCallback callback = getCallback(listBy);
        if (callback == null) {
            throw new SearchServiceException("No callback method stored for session - possible timeout");
        }
        Results results = callback.getResultSet();

        writeExportFile(listBy, results, filename, false);
        return filename;
    }

    public String exportPivotResultsData(final FilterSpecifier.ListBy listBy, final String filename, final Results results) throws SearchServiceException {
        writeExportFile(listBy, results, filename, true);
        return filename;
    }

    private void writeExportFile(final FilterSpecifier.ListBy listBy, final Results results, final String serverFileName, final boolean isPivot)
            throws SearchServiceException {
        if (!results.isFinalRowCount()) {
            throw new SearchServiceException("The results are not ready yet.");
        }

        Locations locations = (Locations) SpringBeanGetter.getInstance(getAppContextPath()).getBean("locations");
        String exportFileDir = locations.getTempFileLocation();
        if (exportFileDir == null) {
            throw new SearchServiceException("Error: " + "Export File location not found");
        }
        try {
            locations.makeSureLocationExists(exportFileDir);
        } catch (IOException e) {
            throw new SearchServiceException(e);
        }

        File outputFile = new File(exportFileDir, serverFileName);

        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter(outputFile));
            ExportData exportData = ExportData.getInstance(listBy);
            exportData.setPivot(isPivot);
            exportData.export(results, output);

        } catch (IOException ioe) {
            logger.logError(ioe);
            throw new SearchServiceException("Error: " + ioe.getMessage());
        }
        finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException e) {
                logger.logError(e);
            }
        }
    }

}