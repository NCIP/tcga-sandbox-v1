/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Step that runs a list of objects as its main work.  List is made by getWorkList method, which is abstract.
 * The generics are I,O is this class's input/output, L and M are the List steps' input/output
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public abstract class AbstractListProcessor<I, O, L, M> extends AbstractProcessor<I, O> {

    private List<Processor<L, M>> listProcessors;

    /**
     * @param input   the input object
     * @param context the qc context
     * @return the List of items to do work on (do not return null!  use empty list if no items)
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an error getting the list
     */
    protected abstract List<L> getWorkList( I input, QcContext context ) throws ProcessorException;

    protected O doWork( final I input, final QcContext context ) throws ProcessorException {
        final List<L> items = getWorkList( input, context );
        // run all list action on each item in turn
        final List<M> itemOutputs = new ArrayList<M>();
        for(final L item : items) {
            context.setItemInProgress(item);
            for(final Processor<L, M> step : listProcessors) {
                // save each output from list action
                itemOutputs.add( step.execute( item, context ) );
            }
            updateItemWorkStatus(item, context);
        }

        return afterWork( input, itemOutputs, context );
    }

    protected void updateItemWorkStatus(final L item, final QcContext context){
        // do nothing
        // should be overwritten by the required derived classes
    }

    // required to generate output object
    protected abstract O afterWork( I input, List<M> itemOutputs, QcContext context ) throws ProcessorException;

    public void setListProcessors( final List<Processor<L, M>> listSteps ) {
        this.listProcessors = listSteps;
    }

    public void addListProcessor( final Processor<L, M> listStep ) {
        if(listProcessors == null) {
            listProcessors = new ArrayList<Processor<L, M>>();
        }
        listProcessors.add( listStep );
    }
}
