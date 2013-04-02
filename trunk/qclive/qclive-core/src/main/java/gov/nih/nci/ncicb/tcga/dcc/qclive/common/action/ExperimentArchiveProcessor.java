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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.List;

/**
 * This is a generic List Processor that executes on all archives in an experiment.  If archiveStatus is set, will
 * only execute on archives with the given status.  Configure with desired
 * Process class(es) as listProcessor(s).
 * <p/>
 * For example, configure an instance of this class with ArchiveSaver as the only listProcessor, and this class
 * will save all Archives in the Experiment.
 * <p/>
 * ExperimentArchiveProcessor eap = new ExperimentArchiveProcessor();
 * eap.addListStep(new ArchiveSaver());
 * ...
 * eap.execute(experiment); // will call ArchiveSaver.execute(...) for each archive in experiment
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ExperimentArchiveProcessor extends AbstractListProcessor<Experiment, Experiment, Archive, Archive> {

    private String name;
    private String archiveStatus;

    protected List<Archive> getWorkList( final Experiment input,
                                         final QcContext context ) throws ProcessorException {
        context.setExperiment( input );
        if(archiveStatus != null) {
            return input.getArchivesForStatus( archiveStatus );
        } else {
            return input.getArchives();
        }
    }

    protected Experiment afterWork( final Experiment input, final List<Archive> itemOutputs,
                                    final QcContext context ) throws ProcessorException {
        return input;
    }

    public String getName() {
        return name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public void setArchiveStatus( final String archiveStatus ) {
        this.archiveStatus = archiveStatus;
    }
}
