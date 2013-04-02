/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.aop;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.AbstractUsageLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.UsageLoggerException;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.UsageLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.FilterRequestValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.SelectionRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.web.*;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles usage logging for the portal application.  It is an Advice class which
 * advises various beans by getting parameters and/or return values and getting usage information
 * from them.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 * @see gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.AbstractUsageLogger
 */
public class UsageAdvice implements AfterReturningAdvice, MethodBeforeAdvice {

    private UsageLoggerI usageLoggerFile;
    private UsageLoggerI usageLoggerDb;
    private boolean writeToDb;
    private ProcessLogger processLogger = new ProcessLogger();
    private static final String METHOD_runJob = "runJob";
    private static final String METHOD_setSelectedFiles = "setSelectedFiles";
    private static final String METHOD_handleRequest = "handleRequest";
    private FilterRequestValidator filterRequestValidator;

    /**
     * @return the UsageLogger this uses to log actions
     */
    public UsageLoggerI getUsageLogger() {
        if (writeToDb) {
            return usageLoggerDb;
        } else {
            return usageLoggerFile;
        }
    }

    /**
     * Called before one of our advised methods is called.  Logs usage as appropriate.
     *
     * @param method the method that is about to be called
     * @param args   the method's arguments
     * @param target the object on which the method will be called
     */
    public void before(Method method, Object[] args, Object target) {
        try {
            // runJob from FilePackager; ignore anything else
            if (method.getName().equals(METHOD_runJob) && target instanceof FilePackagerI
                    && args[0] instanceof FilePackagerBean) {
                archiveCreationStartedAction((FilePackagerBean) args[0]);
            }
        }
        catch (Throwable e) {
            // catch everything else!  UsageLogging cannot be allowed to interrupt normal application flow.
            processLogger.logError(e);
        }
    }

    /**
     * Called after one of our advised methods returns.  Logs usage as appropriate.
     *
     * @param returnVal the return value that the method returned
     * @param method    the method that just returned
     * @param args      the method's arguments
     * @param target    the object on which the method was called
     */
    public void afterReturning(Object returnVal, Method method, Object[] args, Object target) {
        try {
            // look for methods called on targets we want to advise 
            if (method.getName().equals(METHOD_setSelectedFiles) && target instanceof FilePackagerI) {
                // make sure first argument is a list
                if (args != null && args.length > 0 && args[0] instanceof List) {
                    setSelectedFilesAction((List) args[0], (FilePackagerI) target);
                } else {
                    throw new UsageLoggerException("setSelectedFiles first argument was not a List");
                }
            } else if (method.getName().equals(METHOD_runJob) && target instanceof FilePackagerI) {
                archiveCreationFinishedAction((FilePackagerI) target);
            } else if (method.getName().equals(METHOD_handleRequest) && target instanceof WebController) {
                afterReturningFromWebController(returnVal, (HttpServletRequest) args[0], (WebController) target);
            }
        }
        catch (Throwable e) {
            // we want to catch everything so we don't interrupt the flow of the application
            processLogger.logError(e);
        }
    }

    // called by afterReturning if target is a WebController returning from handleRequest

    private void afterReturningFromWebController(Object returnVal, HttpServletRequest request, WebController target)
            throws UsageLoggerException {
        // get the session key out to use for logging
        String sessionKey = String.valueOf(target.getSessionKey(request));
        if (sessionKey == null) {
            throw new UsageLoggerException("Session object did not contain sessionKey; usage cannot be logged.");
        }
        if (target instanceof DataAccessDownloadController) {
            dadPageRequestedAction(sessionKey);
        } else if (target instanceof DataAccessFileProcessingController) {
            archiveCreationQueuedAction(sessionKey);
        } else {
            // all of the rest should have a facade
            DAMFacadeI facadeI = (DAMFacadeI) ((ModelAndView) returnVal).getModel().get(DAMFacade.FACADE_KEY_NAME);
            if (facadeI != null) {
                if (target instanceof DataAccessMatrixController) {
                    // this controller handles filters immediately, so just log that and skip the DAM requested logging
                    //dataAccessMatrixRequestedAction( sessionKey, facadeI.getDiseaseType() );
                    filterAppliedAction(sessionKey, facadeI.getPreviousFilterRequest());
                } else if (target instanceof DataAccessMatrixSelectorController) {
                    // get the SelectionRequest from the facade and pass the appropriate parameters to the handler
                    SelectionRequest selReq = facadeI.getPreviousSelectionRequest();
                    if (selReq.getMode().equals(SelectionRequest.MODE_HEADER)) {
                        headerSelectedAction(sessionKey, facadeI.getHeader(selReq.getHeaderId()),
                                selReq.isIntersect(), facadeI.isHeaderSelected(selReq.getHeaderId()));
                    } else if (selReq.getMode().equals(SelectionRequest.MODE_UNSELECTALL)) {
                        unselectAllAction(sessionKey);
                    }
                } else if (target instanceof DataAccessMatrixColorSchemeController) {
                    // get the color scheme selected in the facade
                    colorSchemeChangedAction(sessionKey, facadeI.getColorScheme().getName());
                }
            }
        }
    }

    /**
     * Logs usage when a file packager has had its selected files set
     *
     * @param dataFiles list of DataFile objects
     * @param fp        the file packager
     * @throws UsageLoggerException if there is an error
     */
    public void setSelectedFilesAction(List dataFiles, FilePackagerI fp) throws UsageLoggerException {
        // set up counts
        int numLevel1 = 0;
        int numLevel2 = 0;
        int numLevel3 = 0;
        int numClinical = 0;
        int numMeta = 0;
        int numPublic = 0;
        int numProtected = 0;
        for (Object o : dataFiles) {
            try {
                // catch class cast exception below in case setSelectedFiles signature has changed
                DataFile df = (DataFile) o;
                if (df.isProtected()) {
                    numProtected++;
                } else {
                    numPublic++;
                }
                if (df.getLevel().equals("1")) {
                    numLevel1++;
                } else if (df.getLevel().equals("2")) {
                    numLevel2++;
                } else if (df.getLevel().equals("3")) {
                    numLevel3++;
                } else if (df.getLevel().equals(DataAccessMatrixQueries.LEVEL_CLINICAL)) {
                    numClinical++;
                } else if (df.getLevel().equals(DataAccessMatrixQueries.LEVEL_METADATA)) {
                    numMeta++;
                } else {
                    throw new UsageLoggerException("DataFile has unknown level: " + df.getLevel());
                }
            }
            catch (ClassCastException ccex) {
                throw new UsageLoggerException("setSelectedFilesAction was called with a list containing a " + o.getClass().getName());
            }
        }
        Map<String, Object> actions = new HashMap<String, Object>();
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.FILES_SELECTED), dataFiles.size());
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.LEVEL_1_FILES_SELECTED), numLevel1);
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.LEVEL_2_FILES_SELECTED), numLevel2);
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.LEVEL_3_FILES_SELECTED), numLevel3);
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.CLINICAL_FILES_SELECTED), numClinical);
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.METADATA_FILES_SELECTED), numMeta);
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.PROTECTED_FILES_SELECTED), numProtected);
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.PUBLIC_FILES_SELECTED), numPublic);
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.CALCULATED_SIZE), fp.getFilePackagerBean().getEstimatedUncompressedSize());
        getUsageLogger().logActionGroup(String.valueOf(fp.getFilePackagerBean().getKey()), actions);
    }

    public void archiveCreationStartedAction(FilePackagerBean filePackagerBean) throws UsageLoggerException {
        // just log the current time as the "time archive creation started"
        getUsageLogger().logAction(String.valueOf(filePackagerBean.getKey()), AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.ARCHIVE_STARTED), null);
    }

    /**
     * Called after the FP's runJob method returns.
     *
     * @param fp the FilePackager whose runJob method has just returned
     * @throws UsageLoggerException if there is an error logging the usage
     */
    public void archiveCreationFinishedAction(FilePackagerI fp) throws UsageLoggerException {
        Map<String, Object> actions = new HashMap<String, Object>();
        // log times whether job succeeded or failed
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.ARCHIVE_GENERATION_TIME), fp.getArchiveGenerationTime());
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.FILE_PROCESSING_TIME), fp.getFileProcessingTime());
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.TOTAL_ARCHIVE_CREATION_TIME), fp.getTotalTime());
        actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.WAITING_IN_QUEUE_TIME), fp.getWaitingInQueueTime());
        // if failed, log that
        if (fp.getFilePackagerBean().isFailed()) {
            actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.ARCHIVE_FAILED), null);
        } else {
            // succeeded, so get sizes
            actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.COMPRESSED_SIZE), fp.getCompressedArchive().length());
            actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.UNCOMPRESSED_SIZE), fp.getActualUncompressedSize());
            actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.ARCHIVE_FINISHED), null);
        }
        getUsageLogger().logActionGroup(String.valueOf(fp.getFilePackagerBean().getKey()), actions);
    }

    /**
     * This is called after the file processing controller's handle method returns.
     *
     * @param sessionKey the key to the session
     * @throws UsageLoggerException if there is an error
     */
    public void archiveCreationQueuedAction(String sessionKey) throws UsageLoggerException {
        getUsageLogger().logAction(sessionKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.ARCHIVE_QUEUED), null);
    }

    /**
     * Called after the handleRequest method of the DataAccessMatrixController returns.
     *
     * @param sessionKey the session key retrieved from the session properties
     * @param disease    the disease name; will use default if null
     * @throws UsageLoggerException if there is an error
     */
    public void dataAccessMatrixRequestedAction(String sessionKey, String disease) throws UsageLoggerException {
        if (disease == null) {
            disease = DataAccessMatrixQueries.DEFAULT_DISEASETYPE;
        }
        getUsageLogger().logAction(sessionKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAM_REQUESTED), disease);
    }

    /**
     * Called after the handleRequest method of the DataAccessMatrixSelectorController returns.
     *
     * @param sessionKey  the session key retrieved from the session
     * @param header      the header that was selected
     * @param isIntersect is the selection an intersection
     * @param isSelected  is the header selected or not
     * @throws UsageLoggerException if there is an error logging the usage
     */
    public void headerSelectedAction(String sessionKey, Header header, boolean isIntersect,
                                     boolean isSelected) throws UsageLoggerException {
        // figure out action types based on isIntersect and isSelected
        String action = isSelected ? (isIntersect ? AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_INTERSECTED) : AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_SELECTED)) : AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_DESELECTED);
        String catAction = isSelected ? (isIntersect ? AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_CATEGORY_INTERSECTED) : AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_CATEGORY_SELECTED)) : AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_CATEGORY_DESELECTED);
        String typeAction = isSelected ? (isIntersect ? AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_TYPE_INTERSECTED) : AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_TYPE_SELECTED)) : AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.HEADER_TYPE_DESELECTED);
        // get full name, recursing through parents;
        String name = getHeaderName(header, null).toString();
        Map<String, Object> actions = new HashMap<String, Object>();
        actions.put(action, name);
        actions.put(catAction, header.getCategory());
        actions.put(typeAction, header.getHeaderType());
        getUsageLogger().logActionGroup(sessionKey, actions);
    }

    public void unselectAllAction(String sessionKey) throws UsageLoggerException {
        getUsageLogger().logAction(sessionKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAM_RESET), null);
    }

    // gets the header name recursively.  this should probably be in the Header class or something.

    private StringBuilder getHeaderName(Header header, StringBuilder headerName) {
        // first, so don't append anything
        if (headerName == null) {
            headerName = new StringBuilder(DataAccessMatrixJSPUtil.lookupHeaderText(header.getCategory(), header.getName()));
        } else {
            // insert a separatator and then the result of the recursive call
            headerName.insert(0, " / ");
            headerName.insert(0, DataAccessMatrixJSPUtil.lookupHeaderText(header.getCategory(), header.getName()));
        }
        // if no parent, we have the whole name.  if has parent, get the parent header's name(s).
        if (header.getParentHeader() == null) {
            return headerName;
        } else {
            return getHeaderName(header.getParentHeader(), headerName);
        }
    }

    /**
     * Method to handle logging filter actions after a filter request has been processed.
     *
     * @param sessionKey    the session id to log the action for
     * @param filterRequest the filter request that was just handled
     * @throws UsageLoggerException if there is an error logging the usage
     */
    public void filterAppliedAction(String sessionKey, FilterRequestI filterRequest) throws UsageLoggerException {
        if (filterRequest.getMode().equals(FilterRequestI.Mode.ApplyFilter)) {
            Map<String, Object> actions = new HashMap<String, Object>();
            actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.FILTER_APPLIED), null);
            final String[] availabilities = filterRequestValidator.getValidAvailabilitySelections(filterRequest.getAvailabilities());
            if (availabilities.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.AVAILABILITY_FILTER_APPLIED), availabilities);
            }

            final String[] batches = filterRequestValidator.getValidBatchSelections(filterRequest.getBatches());
            if (batches.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.BATCH_FILTER_APPLIED), batches);
            }

            // first split centers into platforms and centers, because "centers" property of filter may be in the form center.platform
            final List<String> platformList = new ArrayList<String>();
            if (filterRequest.getCenter().length() > 0) {
                final String[] centerPlatforms = filterRequest.getCenters();
                final List<String> centers = new ArrayList<String>();

                for (final String centerPlatform : centerPlatforms) {
                    if (centerPlatform.contains(".")) {
                        final String[] centerAndPlatform = centerPlatform.split("\\.");
                        centers.add(centerAndPlatform[0]);
                        platformList.add(centerAndPlatform[1]);
                    } else {
                        // if no "." is just center
                        centers.add(centerPlatform);
                    }
                }
                String[] centerArray = new String[centers.size()];
                centerArray = centers.toArray(centerArray);
                centerArray = filterRequestValidator.getValidCenterSelections(centerArray);
                if(centerArray.length > 0) {
                    actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.CENTER_FILTER_APPLIED), centerArray);
                }
            }

            if (filterRequest.getPlatform().length() > 0) {
                Collections.addAll(platformList, filterRequest.getPlatforms());
            }
            String[] platformArray = new String[platformList.size()];
            platformArray = platformList.toArray(platformArray);
            final String[] platforms = filterRequestValidator.getValidPlatformSelections(platformArray);
            if (platforms.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.PLATFORM_FILTER_APPLIED), platforms);
            }
            final String[] levels = filterRequestValidator.getValidLevelSelections(filterRequest.getLevels());
            if (levels.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.LEVEL_FILTER_APPLIED), levels);
            }
            final String[] platformTypes = filterRequestValidator.getValidPlatformTypeSelections(filterRequest.getPlatformTypes());
            if (platformTypes.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.PLATFORM_TYPE_FILTER_APPLIED), platformTypes);
            }
            final String[] protectedStatus = filterRequestValidator.getValidProtectedStatusSelections(filterRequest.getProtectedStatuses());
            if (protectedStatus.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.PROTECTED_STATUS_FILTER_APPLIED), protectedStatus);
            }
            final String[] samples = filterRequestValidator.getValidSampleSelections(filterRequest.getSampleString());
            if (samples.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.SAMPLE_FILTER_APPLIED), samples);
            }
            final String[] tissueTypes = filterRequestValidator.getValidTumorNormalSelections(filterRequest.getTissueTypes());
            if (tissueTypes.length > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.TUMOR_NORMAL_FILTER_APPLIED), tissueTypes);
            }
            if (filterRequest.getEndDate().length() > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.END_DATE_FILTER_APPLIED), filterRequest.getEndDate());
            }
            if (filterRequest.getStartDate().length() > 0) {
                actions.put(AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.START_DATE_FILTER_APPLIED), filterRequest.getStartDate());
            }
            getUsageLogger().logActionGroup(sessionKey, actions);
            // log that filters were cleared
        } else if (filterRequest.getMode().equals(FilterRequestI.Mode.Clear)) {
            getUsageLogger().logAction(sessionKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.FILTER_CLEARED), null);
        }
        // else: NoOp mode does not get logged
    }

    /**
     * Called after the DAD has been requested.
     *
     * @param sessionKey the session in which the request happened
     * @throws UsageLoggerException if there is an error logging the session
     */
    public void dadPageRequestedAction(String sessionKey) throws UsageLoggerException {
        getUsageLogger().logAction(sessionKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAD_REQUESTED), null);
    }

    /**
     * Called after the color scheme has been changed
     *
     * @param sessionKey      the session in which the color scheme was changed
     * @param colorSchemeName the name of the new color scheme
     * @throws UsageLoggerException if there is an error logging
     */
    public void colorSchemeChangedAction(String sessionKey, String colorSchemeName) throws UsageLoggerException {
        getUsageLogger().logAction(sessionKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.COLOR_SCHEME_CHANGED), colorSchemeName);
    }

    public void setWriteToDb(final boolean writeToDb) {
        this.writeToDb = writeToDb;
    }

    public void setUsageLoggerDb(final UsageLoggerI usageLoggerDb) {
        this.usageLoggerDb = usageLoggerDb;
    }

    public void setUsageLoggerFile(final UsageLoggerI usageLoggerFile) {
        this.usageLoggerFile = usageLoggerFile;
    }

    public void setFilterRequestValidator(FilterRequestValidator filterRequestValidator) {
        this.filterRequestValidator = filterRequestValidator;
    }
}
