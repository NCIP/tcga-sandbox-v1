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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BatchNumberAssignment;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BatchNumberQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.List;

/**
 * Validates the disease type in the archive name
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class BCRBatchNumberValidator extends AbstractProcessor<Archive, Boolean> {
   private BatchNumberQueries batchNumberQueries;
   private PlatformQueries platformQueries;

   protected Boolean doWork(final Archive archive, final QcContext context) throws ProcessorException {
        context.setArchive(archive);
     final String archiveName = archive.getArchiveFile().getName();
     final String platform = archive.getPlatform();
     if ( platformQueries != null && batchNumberQueries != null ) {
         final String centerType = getArchiveCenterType(platform);
         // Only if this is a BCR center validate the serial index
         if ( centerType != null && centerType.equals(Experiment.TYPE_BCR) ) {
            return validateBCRArchiveSerialIndex(archive, context);
         }
      }
      return true;
   }
    /**
     * @return the descriptive name for what this processor does
    */
    public String getName() {
        return "Batch Number validation";
    }

    public void setBatchNumberQueries(final BatchNumberQueries batchNumberQueries) {
         this.batchNumberQueries = batchNumberQueries;
     }

    public void setPlatformQueries(final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }

     private String getArchiveCenterType(final String platformName) {
         final Platform platform = platformQueries.getPlatformForName(platformName);
         return ( platform == null ? null : platform.getCenterType() );
     }

    private boolean validateBCRArchiveSerialIndex(final Archive archive, final QcContext context) {

        if (batchNumberQueries.isValidBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()), archive.getTumorType(), archive.getDomainName())) {
            return true;
        } else {
             // not valid, so find out if the batch is unknown or if the batch is associated with a different disease or center
            List<BatchNumberAssignment> batchNumberAssignments = batchNumberQueries.getBatchNumberAssignment(Integer.valueOf(archive.getSerialIndex()));
            if (batchNumberAssignments == null || batchNumberAssignments.size() == 0) {
                context.addError("Batch " + archive.getSerialIndex() + " has not yet been registered with the DCC");
            } else {
                context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_NAME_INVALID_SERIAL_INDEX_ERROR,
                    archive.getSerialIndex(),
                    batchNumberAssignments.get(0).getCenterDomainName(),
                    batchNumberAssignments.get(0).getDisease()));
            }
            return false;
        }
    }
}
