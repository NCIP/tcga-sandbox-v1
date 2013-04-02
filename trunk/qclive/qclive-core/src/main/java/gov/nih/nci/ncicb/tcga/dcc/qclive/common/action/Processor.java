/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.List;

/**
 * Interface for steps in archive submission / checking / validation / deployment process.
 * Type "T" is the input type for the step and type "S" is the output type.
 * Sub-steps must
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface Processor<T, S> {

    /**
     * The main method for Processor objects.  Does the work.
     *
     * @param input   the input object
     * @param context the qc context
     * @return output the output object
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an unrecoverable error during execution
     */
    public S execute( T input, QcContext context ) throws ProcessorException;

    /**
     * Sets the list of input validators to run on the input passed to execute.  Will
     * run them in the order they are given in the List.  Input validators must have the
     * same input type as this processor, and have output type Boolean.
     *
     * @param inputValidators list of Processors that accept input type T and have output Boolean
     */
    public void setInputValidators( List<Processor<T, Boolean>> inputValidators );

    /**
     * Adds an input validator to the end of the list of input validators.
     *
     * @param inputValidator the input validator to add
     */
    public void addInputValidator( final Processor<T, Boolean> inputValidator );

    /**
     * Sets the list of output validators to run on the output produced by execute.  Will
     * run them in the order they are given in the List.  Output validators must have the
     * same input type (S) as the output type of this processor, and have output type
     * Boolean.
     *
     * @param outputValidators list of Processors that accept input type S and have output Boolean
     */
    public void setOutputValidators( List<Processor<S, Boolean>> outputValidators );

    /**
     * Adds an output validator to the end of the list of output validators.
     *
     * @param outputValidator the output validator to add
     */
    public void addOutputValidator( final Processor<S, Boolean> outputValidator );

    /**
     * Sets the list of pre-loader to run on the input before the main work of this
     * processor is done.  Pre-loader will be run in the order they appear in the
     * List.  A pre-processor's input and output types must be the same as this processor's
     * input type.   
     *
     * @param processors list of pre-loader to run before the main work of this processor
     */
    public void setPreProcessors( List<Processor<T, T>> processors );

    /**
     * Adds a pre-processor to the end of the list of pre-loader.
     *
     * @param processor the pre-processor to add
     */
    public void addPreProcessor( final Processor<T, T> processor );

    /**
     * Sets the list of post-loader to run on the output before it is returned.  Post-
     * loader will be run in the order they appear in the List.  A post-processor's
     * input and output types must match the output type of this processor.
     *
     * @param processors list of post-loader to run before returning output from this processor
     */
    public void setPostProcessors( List<Processor<S, S>> processors );

    /**
     * Adds a post-processor to the end of the list of post-loader.
     *
     * @param processor the processor to add
     */
    public void addPostProcessor( final Processor<S, S> processor );

    /**
     * Gets the name of the processor, in descriptive English.  Name should fit into a sentence
     * in the form "Execution of " + getName() + " completed", if possible.
     * @return the descriptive name of this processor
     */
    public String getName();

    /**
     * Gets the description for the processor as it applies to the given input.  For example,
     * if a processor has a File object as input, the description might be:
     * "Validation of the file " + file.getName()
     *
     * @param input the input object passed to the processor
     * @return the description of this processor as it applies to the input
     */
    public String getDescription( T input );

    /**
     * Sets whether or not this processor should send email when it is done executing. Default is false.
     *
     * @param doEmail true if email should be sent, false if not.
     */
    public void setDoEmail( boolean doEmail );

    /**
     * Gets whether or not this processor should sent email when it is done excuting.
     *
     * @return true if email should be sent, false if not
     */
    public boolean getDoEmail();

    /**
     * Sets the MailSender object this processor should use.
     *
     * @param mailSender the MailSender instance
     */
    public void setMailSender( final MailSender mailSender );

    // to be removed
    public void setToEmails( final String toEmails );

    /**
     * General exception for use by Processor classes.
     */
    public class ProcessorException extends Exception {

        /**
         * Constructs a ProcessorException based on another exception.
         *  
         * @param cause the underlying cause of the exception
         */
        public ProcessorException( final Throwable cause ) {
            super( cause );
        }

        /**
         * Constructs a ProcessorException with the given message.
         *
         * @param message the exception message
         */
        public ProcessorException( final String message ) {
            super( message );
        }

        /**
         * Constructs a ProcessorException based on another exception,
         * with the given message.
         *
         * @param message the exception message
         * @param cause the underlying cause of the exception
         */
        public ProcessorException( final String message, final Throwable cause ) {
            super( message, cause );
        }
    }
}
