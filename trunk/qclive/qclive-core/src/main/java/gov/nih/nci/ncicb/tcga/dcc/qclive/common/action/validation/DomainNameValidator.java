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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * Validates an archive's center (domain) name against the database.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class DomainNameValidator extends AbstractProcessor<Archive, Boolean> {

    private CenterQueries centerQueries;
    private PlatformQueries platformQueries;

    protected Boolean doWork( final Archive archive, final QcContext context ) throws ProcessorException {
        context.setArchive( archive );

        String centerType = getCenterType(archive.getPlatform());
        if(centerType == null) {
            archive.setDeployStatus( Archive.STATUS_INVALID );
            context.addError(MessageFormat.format(
            		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
            		archive, 
            		"Center type for the platform '" + archive.getPlatform() + "' not found"));
            return false;
        }else {
            final Integer id = getCenterId( archive.getDomainName(), centerType);
            if(id == null || id == 0) {
                archive.setDeployStatus( Archive.STATUS_INVALID );
                context.addError(MessageFormat.format(
                		MessagePropertyType.ARCHIVE_PROCESSING_ERROR, 
                		archive, 
                		"Center name '" + archive.getDomainName() + "' is invalid for center type '" + centerType + "'"));
                return false;
            }
        }            

        return true;
    }

    public String getName() {
        return "domain name validation";
    }

    public void setCenterQueries( final CenterQueries centerQueries ) {
        this.centerQueries = centerQueries;
    }

    public void setPlatformQueries( final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }    

    protected Integer getCenterId(final String domainName, final String centerType) {
        try {
            return centerQueries.findCenterId( domainName, centerType);
        } catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    protected String getCenterType(final String platformName) {
        try {
            Platform platform = platformQueries.getPlatformForName(platformName);
            return platform!=null? platform.getCenterType():null;
        } catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
    
}
