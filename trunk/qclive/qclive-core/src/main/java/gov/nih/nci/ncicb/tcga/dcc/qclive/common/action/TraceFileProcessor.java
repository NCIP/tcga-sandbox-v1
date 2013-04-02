/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.TraceRelationship;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.TraceFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BiospecimenHelper;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TraceRelationshipQueries;
import org.apache.commons.io.IOUtils;
import org.springframework.dao.DataAccessException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes trace-sample relationship files (extension .tr), and puts the information into the database.
 * This does not do validation.  See TraceFileValidator for that.
 *
 * NOTE: data saved only to dccCommon, not disease-specific schemas
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class TraceFileProcessor extends AbstractArchiveFileProcessor<Archive> {

    private BCRIDQueries bcrIdQueries;
    private BCRDataService bcrDataService;

    private TraceRelationshipQueries commonTraceRelationshipQueries;
    private FileInfoQueries fileInfoQueries;  // dccCommon

    protected Archive getReturnValue( final Map<File, Archive> results, final QcContext context ) {
        return context.getArchive();
    }

    protected Archive processFile( final File file, final QcContext context ) throws ProcessorException {
        try {
            processTraceFile( file, context );
        }
        catch(IOException e) {
            context.getArchive().setDeployStatus( Archive.STATUS_IN_REVIEW );
            context.addError(MessageFormat.format(
            		MessagePropertyType.TRACE_FILE_PROCESSING_ERROR,
            		file.getName(),
            		e.getMessage()));
        } catch (DataAccessException e) {
            context.getArchive().setDeployStatus( Archive.STATUS_IN_REVIEW );
            context.addError(MessageFormat.format(
            		MessagePropertyType.TRACE_FILE_PROCESSING_ERROR,
            		file.getName(),
            		e.getMessage()));
        }
        return context.getArchive();
    }

    protected Archive getDefaultReturnValue( final Archive archive ) {
        return archive;
    }

    protected String getFileExtension() {
        return TraceFileValidator.TR_EXTENSION;
    }

    protected boolean isCorrectArchiveType( final Archive archive ) {
        return Experiment.TYPE_GSC.equals( archive.getExperimentType() ) && archive.getArchiveType().equals( Archive.TYPE_LEVEL_1 );
    }

    private void processTraceFile( final File traceFile, final QcContext context ) throws IOException {

        BufferedReader reader = null;
        try {
            // find the file id for this file
            final Long fileInfoId = fileInfoQueries.getFileId( traceFile.getName(), context.getArchive().getId() );
            if(fileInfoId == null) {
                context.getArchive().setDeployStatus( Archive.STATUS_IN_REVIEW );
                context.addError(MessageFormat.format(
                        MessagePropertyType.TRACE_FILE_PROCESSING_ERROR,
                        traceFile.getName(),
                        "File was not found in the database"));
                return;
            }
            // cache barcode ids since they repeat in the files
            final Map<String, Integer> barcodeIds = new HashMap<String, Integer>();
            final Date fileDate = new Date( traceFile.lastModified() );
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader = new BufferedReader( new FileReader( traceFile ) );
            String line = reader.readLine();
            int lineNum = 1;
            while(line != null) {
                processLine( line, lineNum, barcodeIds, traceFile, fileDate, fileInfoId, context );
                line = reader.readLine();
                lineNum++;
            }
            //now add biospecimen to file relationship
            final List<Integer>  biospecimenIds = new ArrayList<Integer>(barcodeIds.values());
            BiospecimenHelper.insertBiospecimenFileRelationship(biospecimenIds,
                    fileInfoId,
                    bcrDataService,
                    context.getArchive().getTheTumor());
            // This is ugly. Created new ticket to update the biosspecimen id to long
            final List<Long> shippedBiospecimenIds = new ArrayList<Long>();
            for(Integer id: biospecimenIds){
                shippedBiospecimenIds.add(id.longValue());
            }
            bcrDataService.addShippedBiospecimensFileRelationship(shippedBiospecimenIds, fileInfoId);
        } finally {
            IOUtils.closeQuietly(reader);
        }

    }

    private void processLine( final String line, final int lineNum, final Map<String, Integer> barcodeIds,
                              final File traceFile, final Date fileDate, final Long fileInfoId,
                              final QcContext context ) {
        // validator checks that first element is an integer and second is barcode.  first line may be header.
        final String[] fields = line.split( "\\t" );
        final Long traceId;
        try {
            traceId = Long.parseLong( fields[0] );
        }
        catch(NumberFormatException e) {
            if(lineNum > 1) {
                // if not the first line, a non-integer is an error
                context.getArchive().setDeployStatus( Archive.STATUS_IN_REVIEW );
                context.addError(MessageFormat.format(
                		MessagePropertyType.TRACE_FILE_PROCESSING_ERROR,
                		traceFile.getName(),
                		"Contains an invalid trace ID: " + fields[0]));
            }
            return;
        }
        final String barcode = fields[1].trim();
        Integer barcodeId = barcodeIds.get( barcode );
        // have not seen this barcode yet, so get the id from the db
        if(barcodeId == null) {
            barcodeId = bcrIdQueries.exists( barcode );
            if(barcodeId == -1) {
                // not in db, so orphan... need to insert it
                try {
                    BCRID commonBcrId = bcrDataService.parseAliquotBarcode(barcode);
                    commonBcrId.setFullID(barcode);
                    bcrDataService.storeBarcode( commonBcrId,  -1, context.getArchive().getTheTumor() );
                    barcodeIds.put( barcode, barcodeId );
                }
                catch(ParseException e) {
                    context.getArchive().setDeployStatus( Archive.STATUS_IN_REVIEW );
                    context.addError(MessageFormat.format(
                    		MessagePropertyType.TRACE_FILE_PROCESSING_ERROR,
                    		traceFile.getName(),
                    		new StringBuilder().append("Barcode '").append(barcode).append("', on line '").append(lineNum).append("' is in an invalid format").toString()));
                    return;
                } catch (UUIDException e) {
                    context.getArchive().setDeployStatus( Archive.STATUS_IN_REVIEW );
                    context.addError(MessageFormat.format(
                    		MessagePropertyType.TRACE_FILE_PROCESSING_ERROR,
                    		traceFile.getName(),
                    		e.getMessage()));
                    return;
                }
            } else {
                barcodeIds.put( barcode, barcodeId );
            }
        }
        // now add trace relationship
        insertTraceRelationship( barcodeId, traceId, fileInfoId, fileDate, context );

    }

    private void insertTraceRelationship( final Integer barcodeId, final Long traceId, final Long fileInfoId,
                                          final Date fileDate, final QcContext context ) {
        if(barcodeId != null && barcodeId != -1) {
            final TraceRelationship tr = new TraceRelationship();
            tr.setTraceID( traceId );
            tr.setBiospecimenID( barcodeId );
            tr.setDccReceived( fileDate );
            tr.setFileID( fileInfoId );
            final Date dateInDB = commonTraceRelationshipQueries.getDccDate( barcodeId, traceId );
            if(dateInDB != null) {
                // use earlier date, choosing between file mod date and date in db
                if(fileDate.before( dateInDB )) {
                    // need to update the date
                    commonTraceRelationshipQueries.updateDccDate( tr );
                }
                // since date not null, is already in db, so check the file info id
                final long fileIdInDb = commonTraceRelationshipQueries.getFileId( barcodeId, traceId );
                if(fileIdInDb != fileInfoId) {
                    // need to update the file info id to this one
                    commonTraceRelationshipQueries.updateFileID( tr );
                }
            } else {
                // relationship not in db yet, add it
                final int traceRelationshipId = commonTraceRelationshipQueries.addTraceRelationship( tr );
                if(traceRelationshipId == -1) {
                    // -1 means it failed to add
                    context.getArchive().setDeployStatus( Archive.STATUS_IN_REVIEW );
                    context.addError(MessageFormat.format(
                    		MessagePropertyType.GENERAL_VALIDATION_MESSAGE,
                    		new StringBuilder().append("Trace relationship between barcode with id ").append(barcodeId).append(" and trace id ").
                    		append(traceId).append(" in file with id ").append(fileInfoId).append(" failed to be added to the database").toString()));
                }
            }
        }
    }
      
    public String getName() {
        return "trace file processor";
    }

    public void setBcrIdQueries( final BCRIDQueries bcrIdQueries ) {
        this.bcrIdQueries = bcrIdQueries;
    }

    public void setCommonTraceRelationshipQueries( final TraceRelationshipQueries commonTraceRelationshipQueries) {
        this.commonTraceRelationshipQueries = commonTraceRelationshipQueries;
    }

    public void setFileInfoQueries( final FileInfoQueries fileInfoQueries ) {
        this.fileInfoQueries = fileInfoQueries;
    }

    public BCRDataService getBcrDataService() {
        return bcrDataService;
    }

    public void setBcrDataService(BCRDataService bcrDataService) {
        this.bcrDataService = bcrDataService;
    }
}
