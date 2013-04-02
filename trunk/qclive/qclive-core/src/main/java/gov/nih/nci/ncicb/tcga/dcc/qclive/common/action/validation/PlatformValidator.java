/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * Validates an archive's platform name against the database.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class PlatformValidator extends AbstractProcessor<Archive, Boolean> {

    private PlatformQueries platformQueries;

    /**
     * This does the main work of the step.  It runs after input validators and preSteps, and before postSteps.
     *
     * @param archive the input to the step
     * @return the output object
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an unrecoverable error
     */
    protected Boolean doWork( final Archive archive, final QcContext context ) throws ProcessorException {
        try {
            final Integer id = platformQueries.getPlatformIdByName( archive.getPlatform() );
            if(id == null || id == 0 || id == -1) {
                archive.setDeployStatus( Archive.STATUS_INVALID );
                throw new ProcessorException( new StringBuilder().append( "Platform '" ).append( archive.getPlatform() ).append( "' is invalid" ).toString() );
            }
        }
        catch(IncorrectResultSizeDataAccessException e) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            throw new ProcessorException( new StringBuilder().append( "Platform '" ).append( archive.getPlatform() ).append( "' is invalid" ).toString() );
        }
        return true;
    }

    public String getName() {
        return "platform type validation";
    }

    public String getDescription( final Archive input ) {
        return new StringBuilder().append( getName() ).append( " on " ).append( input.getPlatform() ).toString();
    }

    public void setPlatformQueries( final PlatformQueries platformQueries ) {
        this.platformQueries = platformQueries;
    }
}
