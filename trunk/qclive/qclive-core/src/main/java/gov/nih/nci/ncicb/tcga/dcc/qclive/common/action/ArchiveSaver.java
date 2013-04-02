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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.*;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * This class saves an archive to the database.  It will first check if the archive is already in the database
 * (by querying by the name), and if so, update deploy status and date added only.  Otherwise, the entire archive
 * is added and the ID of the Archive object is set to the new value.
 * <p/>
 * Note: this does not save anything but the archive_info.  No file_info rows are added.  Use ArchiveFileSaver for that.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ArchiveSaver extends AbstractProcessor<Archive, Archive> {
    // DAO for dccCommon
    private ArchiveQueries commonArchiveQueries;
    // DAO for disease specific schema    
    private ArchiveQueries diseaseArchiveQueries;    

    // these are used for querying only, not for writing, so are dccCommon
    private CenterQueries centerQueries;
    private PlatformQueries platformQueries;
    private TumorQueries tumorQueries;
    private DataTypeQueries dataTypeQueries;
    private ArchiveTypeQueries archiveTypeQueries;

    /**
     * This does the main work of the step.  It saves the archive by either doing an insert (if it's new) or
     * by doing an update on select fields (if it's pre-existing).
     *
     * @param archive the input to the step
     * @return the same archive
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an unrecoverable error
     */
    protected Archive doWork( final Archive archive, final QcContext context ) throws ProcessorException {
        try {
            // make sure we point to the right disease DB
            DiseaseContextHolder.setDisease(archive.getTumorType());

            populateFields( archive );

            // first, see if the archive is in the database already
            try {
                final long archiveId = commonArchiveQueries.getArchiveIdByName( archive.getArchiveName() );
                archive.setId( archiveId );
            }
            catch(EmptyResultDataAccessException e) {
                // ok, that just means it's not in the database yet
            }
            // if ID is < 1, do initial save and set ID
            if(archive.getId() < 1) {                
                final Long id = commonArchiveQueries.addArchive( archive );
                if(id == null || id < 1) {
                    throw new ProcessorException( "Archive failed to save to database" );
                } else {
                    archive.setId( id );
                    diseaseArchiveQueries.addArchive(archive, true);                    
                }                    
            } else {
                // update deploy status and date, which are the two things that can change
                // note that archive queries will update is_latest when status is updated
                updateArchiveStatus(archive);
                updateAdditionDate(archive);
                updateDeployLocation(archive);
            }
        }
        catch(DataAccessException e) {
            throw new ProcessorException( new StringBuilder().append( "Error saving archive to database: " ).append( e.getMessage() ).toString(), e );
        }
        return archive;
    }

    private void updateDeployLocation(final Archive archive) {
        commonArchiveQueries.updateDeployLocation( archive );
        diseaseArchiveQueries.updateDeployLocation( archive );        
    }

    private void updateAdditionDate(final Archive archive) {
        commonArchiveQueries.updateAddedDate(archive.getId(), archive.getDateAdded());
        diseaseArchiveQueries.updateAddedDate(archive.getId(), archive.getDateAdded());        
    }

    private void updateArchiveStatus(final Archive archive) {
        commonArchiveQueries.updateArchiveStatus( archive );
        diseaseArchiveQueries.updateArchiveStatus( archive );        
    }

    private void populateFields( final Archive archive ) throws ProcessorException {

        archive.setThePlatform( platformQueries.getPlatformForName( archive.getPlatform() ) );
        if(archive.getThePlatform() == null) {
            throw new ProcessorException( new StringBuilder().append( "Archive's platform (" ).append( archive.getPlatform() ).append( ") was not found in the database" ).toString() );
        }
        archive.setTheCenter( centerQueries.getCenterByName( archive.getDomainName(), archive.getThePlatform().getCenterType()));
        if(archive.getTheCenter() == null) {
            throw new ProcessorException( new StringBuilder().append( "Center with name (" ).append( archive.getDomainName() ).append( ") and type (").
                    append(archive.getThePlatform().getCenterType()).append( ") was not found in the database" ).toString() );
        }
        archive.setTheTumor( tumorQueries.getTumorForName( archive.getTumorType() ) );
        if(archive.getTheTumor() == null) {
            throw new ProcessorException( new StringBuilder().append( "Archive's tumor type (" ).append( archive.getTumorType() ).append( ") is not in the database" ).toString() );
        }
        archive.setDataType( dataTypeQueries.getBaseDataTypeDisplayNameForPlatform( archive.getThePlatform().getPlatformId() ) );
        archive.setArchiveTypeId( archiveTypeQueries.getArchiveTypeId( archive.getArchiveType() ) );        
        archive.setExperimentType(dataTypeQueries.getCenterTypeIdForPlatformId(archive.getThePlatform().getPlatformId()));
    }

    /**
     *
     * @return the name for ArchiveSaver
     */
    public String getName() {
        return "archive saver";
    }

    /**
     * Sets the archive queries instance.
     *  
     * @param commonArchiveQueries the archive queries instance
     */
    public void setCommonArchiveQueries( final ArchiveQueries commonArchiveQueries) {
        this.commonArchiveQueries = commonArchiveQueries;
    }

    /**
     * Sets the archive queries instance for disease specific schema.
     *
     * @param diseaseArchiveQueries the archive queries instance
     */

    public void setDiseaseArchiveQueries(final ArchiveQueries diseaseArchiveQueries) {
        this.diseaseArchiveQueries = diseaseArchiveQueries;
    }

    /**
     * Sets the center queries instance.
     *
     * @param centerQueries the center queries instance
     */
    public void setCenterQueries( final CenterQueries centerQueries ) {
        this.centerQueries = centerQueries;
    }

    /**
     * Sets the platform queries instance.
     *
     * @param platformQueries the platform queries instance
     */
    public void setPlatformQueries( final PlatformQueries platformQueries ) {
        this.platformQueries = platformQueries;
    }

    /**
     * Sets the tumor queries instance.
     *
     * @param tumorQueries the tumor queries instance
     */
    public void setTumorQueries( final TumorQueries tumorQueries ) {
        this.tumorQueries = tumorQueries;
    }

    /**
     * Sets the datatype queries instance.
     *
     * @param dataTypeQueries the datatype queries instance
     */
    public void setDataTypeQueries( final DataTypeQueries dataTypeQueries ) {
        this.dataTypeQueries = dataTypeQueries;
    }

    /**
     * Sets the archive type queries instance.
     *
     * @param archiveTypeQueries the archive type queries instance
     */
    public void setArchiveTypeQueries( final ArchiveTypeQueries archiveTypeQueries ) {
        this.archiveTypeQueries = archiveTypeQueries;
    }
}
