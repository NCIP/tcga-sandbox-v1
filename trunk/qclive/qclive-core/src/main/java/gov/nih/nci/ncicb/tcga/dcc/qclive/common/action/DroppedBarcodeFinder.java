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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class finds out if there are any dropped barcodes in this archive as against the previous revision.
 * If there any missing barcodes a warning will be added to QcContext  
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DroppedBarcodeFinder extends AbstractProcessor<Archive, Archive> {

    private BCRIDQueries bcrIdQueries;
    
    @Override
    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        final List<BCRID> currentBarcodeList = bcrIdQueries.getArchiveBarcodes(archive.getId());
        final Archive previousArchive = context.getExperiment().getPreviousArchiveFor(archive);
        if(previousArchive != null) {
            final List<BCRID> previousBarcodeList = bcrIdQueries.getArchiveBarcodes(previousArchive.getId());
            final Collection<BCRID> droppedBarcodeList = filterBarcodes(currentBarcodeList, previousBarcodeList);
            if(droppedBarcodeList.size() > 0) {
                final StringBuilder warning = new StringBuilder("The following barcodes have been dropped in the archive '");
                warning.append(archive.getArchiveName());
                warning.append("': ");

                for(final BCRID bcrId : droppedBarcodeList) {
                    warning.append(bcrId.getFullID()).append(", ");                    
                }
                final int length = warning.length();
                warning.delete(length-2, length-1); // remove the extra , at the end
                context.addWarning(warning.toString());
            }                
        }
        return archive;
    }

    private Collection<BCRID> filterBarcodes(final Collection<BCRID> currentBarcodeList, final Collection<BCRID> previousBarcodeList) {
        // put them into a Set to make sure there are no duplicates
        final Set<BCRID> currentBarcodeSet = new HashSet<BCRID>();
        currentBarcodeSet.addAll(currentBarcodeList);
        final Set<BCRID> previousBarcodeSet = new HashSet<BCRID>();
        previousBarcodeSet.addAll(previousBarcodeList);
        final Collection<BCRID> droppedBarcodes = CollectionUtils.subtract(previousBarcodeSet, currentBarcodeSet);
        if(droppedBarcodes.size() != 0 ) {
            for (Iterator<BCRID> it = droppedBarcodes.iterator(); it.hasNext();) {
                if(it.next().getViewable() != 1) {
                    it.remove();
                }                                
            }
        }
        return droppedBarcodes;
    }

    public String getName() {
        return "dropped barcode finder";
    }

    public void setBcrIdQueries(final BCRIDQueries bcrIdQueries) {
        this.bcrIdQueries = bcrIdQueries;
    }
}
