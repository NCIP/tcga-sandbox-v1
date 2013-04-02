/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents an abstract processing step in the archive processing process.  Subclasses should define their
 * input and output types (I and O) as well as the doWork method.  The execute method is what performs the main work.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public abstract class AbstractProcessor<I, O> implements Processor<I, O>, ConstantValues {

    protected List<Processor<I, I>> preProcessors = new ArrayList<Processor<I, I>>();
    protected List<Processor<O, O>> postProcessors = new ArrayList<Processor<O, O>>();
    protected List<Processor<I, Boolean>> inputValidators;
    protected List<Processor<O, Boolean>> outputValidators;
    protected boolean doEmail = false;
    protected MailSender mailSender;
    protected String toEmails;
    protected AtomicBoolean inputValidationDoEmail = new AtomicBoolean(false);

    /**
     * This does the main work of the processor.  It runs after input validators and preSteps, and before postSteps.
     *
     * @param input   the input to the processor
     * @param context the context for this QC call
     * @return the output object
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an unrecoverable error
     */
    protected abstract O doWork( I input, QcContext context ) throws ProcessorException;

    /**
     * Executes the processor by doing the following things, in this order:
     * 1. Run pre-loader
     * 2. Run input validators
     * 3. doWork (abstract method)
     * 4. Run output validators
     * 5. Run post-loader
     * 6. Return output
     *
     * If doWork returns null, an exception will be thrown. Also, if any output validators fail, an exception
     * will be thrown.
     *
     * @param input   the input object
     * @param context the qc context
     * @return output the output object
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an unrecoverable error during execution
     */
    public O execute( final I input, final QcContext context ) throws ProcessorException {
        try {
            if(context.getLogger() != null) {
                // if context has a logger, that means we want to explicitly log processes as they run
                context.getLogger().log( Level.INFO, "Running " + this.getDescription( input ) );
            }
            runPreProcessors( input, context );
            runInputValidators( input, context );
            sendInputValidationEmail(input, context);
            // now do whatever this step is supposed to do, if anything
            final O output = doWork( input, context );
            if(output == null) {
                throw new ProcessorException( new StringBuilder().append( "Execution of " ).append( this.getName() ).append( " on " ).append( input.toString() ).append( " failed" ).toString() );
            }
            runOutputValidators( output, context );
            runPostProcessors( output, context );
            sendPostExecuteEmail( input, context );
            return output;
        }
        catch(ProcessorException e) {
            if(e.getMessage() != null && !e.getMessage().equals( "" )) {
                context.addError( e.getMessage() );
            }
            sendPostExecuteEmail( input, context ) ;
            throw e;
        }
    }

    /**
     * Sends email, if properties are set properly to allow it, after execute method has finished.
     *
     * @param input the input which has just been through execution
     * @param context the context
     */
    protected void sendPostExecuteEmail( final I input, final QcContext context ) {
        // only send if context has Archive, and Archive Center is set, which has Center email.
        if(mailSender != null && getDoEmail() && context.getCenterEmail() != null) {
            mailSender.send( context.getCenterEmail(), null,
                    buildEmailSubject( input, context ), buildEmailBody( input, context ), false );
        }
    }

    /**
     * Sends email, if properties are set properly to allow it, after input validation has finished
     * Only sends email if errors are present in Context and properties allow it
     *
     * @param input the input which has just been through execution
     * @param context the context
     */
    protected void sendInputValidationEmail( final I input, final QcContext context ) {
        // only send if context has Archive, and Archive Center is set, which has Center email.
        if(mailSender != null && getInputValidationDoEmail() && context.getCenterEmail() != null && context.getErrors().size() > 0) {
            mailSender.send( context.getCenterEmail(), null,
                    buildEmailSubject( input, context ), buildEmailBody( input, context ), false );
        }
    }

    /**
     * Builds the subject line for post-execute email. Will indicate success or failure of processor.
     *
     * @param input the input given to execute
     * @param context the context
     * @return email subject
     */
    protected String buildEmailSubject( final I input, final QcContext context ) {
        final StringBuilder subject = new StringBuilder().append( getDescription( input ) );
        if(context.getErrorCount() > 0) {
            subject.append( " failed" );
        } else {
            subject.append( " completed" );
        }
        return subject.toString();
    }

    /**
     * Builds the email body for post-execute email.  Will list errors and warnings, if any,
     *
     * @param input the input passed to execute
     * @param context the context
     * @return the email body
     */
    protected String buildEmailBody( final I input, final QcContext context ) {
        final StringBuilder sb = new StringBuilder();
        if(context.getErrorCount() > 0) {
            if(context.getErrorCount() == 1) {
                sb.append( "There was 1 error " );
            } else {
                sb.append( "There were " ).append( context.getErrorCount() ).append( " errors " );
            }
            sb.append( "recorded during execution of " ).append( getDescription( input ) );
            for(final String error : context.getErrors()) {
                sb.append( "\n" ).append( "- " ).append( error );
            }
            sb.append( "\n\n" );
        } else {
            if(context.getWarningCount() > 0) {
                if(context.getWarningCount() == 1) {
                    sb.append( "There was 1 warning " );
                } else {
                    sb.append( "There were " ).append( context.getWarningCount() ).append( " warnings " );
                }
                sb.append( "recorded during execution of " ).append( getDescription( input ) );
                for(final String warning : context.getWarnings()) {
                    sb.append( "\n" ).append( "- " ).append( warning );
                }
                sb.append( "\n\n" );
            } else {
                sb.append( getSuccessEmailBody(input) );
            }
        }
        return sb.toString();
    }

    /**
     * Gets the email body for a successful execute call.
     *
     * @param input the input passed to execute
     * @return email body for successful call
     */
    protected String getSuccessEmailBody(final I input) {
        return "Execution of " + getDescription( input ) + " completed with no errors or warnings.";
    }

    /**
     * Runs the pre-loader one at a time.
     *
     * @param input the input object to pass to pre-loader
     * @param context the context
     * @throws ProcessorException if any of the pre-loader has an error
     */
    protected void runPreProcessors( final I input, final QcContext context ) throws ProcessorException {
        // if there are pre-processing action for the input, run them
        if(preProcessors != null) {
            for(final Processor<I, I> preStep : preProcessors) {
                preStep.execute( input, context );
            }
        }
    }

    /**
     * Runs the input validators one at a time.
     *
     * @param input the input to validate
     * @param context the context
     * @return true if all input validators passed, false if not
     * @throws ProcessorException if any of the validators encounters an error
     */
    protected boolean runInputValidators( final I input, final QcContext context ) throws ProcessorException {
        boolean passed = true;
        if(inputValidators != null) {
            for(final Processor<I, Boolean> val : inputValidators) {
                passed = passed && val.execute( input, context );
            }
        }
        return passed;
    }

    /**
     * Runs the output validators one at a time.  If the method returns normally, that means all validators
     * passed (i.e. returned true).  This will throw an exception if any fail, but not until all have been run.
     *
     * @param output the output to validate
     * @param context the context
     * @throws ProcessorException if any of the output validators encounters an error, or if any of the output
     * validators fails.
     */
    protected void runOutputValidators( final O output, final QcContext context ) throws ProcessorException {
        boolean passed = true;
        if(outputValidators != null) {
            for(final Processor<O, Boolean> val : outputValidators) {
                boolean thisPassed = val.execute( output, context );
                passed = passed && thisPassed;
            }
        }
        if(!passed) {
            throw new ProcessorException( new StringBuilder().append( getName() ).append( " failed" ).toString() );
        }
    }

    /**
     * Runs the post-loader one at a time.
     *
     * @param output the output to process
     * @param context the context
     * @throws ProcessorException if one of the post-loader encounters an error
     */
    protected void runPostProcessors( final O output, final QcContext context ) throws ProcessorException {
        // now run post-processing action on the output
        if(postProcessors != null) {
            for(final Processor<O, O> postStep : postProcessors) {
                postStep.execute( output, context );
            }
        }
    }

    /**
     * Sets the list of input validators to run on the input passed to execute.  Will
     * run them in the order they are given in the List.  Input validators must have the
     * same input type as this processor, and have output type Boolean.
     *
     * @param inputValidators list of Processors that accept input type I and have output Boolean
     */
    public void setInputValidators( final List<Processor<I, Boolean>> inputValidators ) {
        this.inputValidators = inputValidators;
    }

    /**
     * Adds an input validator to the end of the list of input validators.
     * 
     * @param inputValidator the input validator to add
     */
    public void addInputValidator( final Processor<I, Boolean> inputValidator ) {
        if(inputValidators == null) {
            inputValidators = new ArrayList<Processor<I, Boolean>>();
        }
        inputValidators.add( inputValidator );
    }

    /**
     * Sets the list of output validators to run on the output produced by execute.  Will
     * run them in the order they are given in the List.  Output validators must have the
     * same input type (S) as the output type of this processor, and have output type
     * Boolean.
     * 
     * @param outputValidators list of Processors that accept input type O and have output Boolean
     */
    public void setOutputValidators( final List<Processor<O, Boolean>> outputValidators ) {
        this.outputValidators = outputValidators;
    }

    /**
     * Adds an output validator to the end of the list of output validators.
     * 
     * @param outputValidator the output validator to add
     */
    public void addOutputValidator( final Processor<O, Boolean> outputValidator ) {
        if(outputValidators == null) {
            outputValidators = new ArrayList<Processor<O, Boolean>>();
        }
        outputValidators.add( outputValidator );
    }

    /**
     * Sets the list of pre-loader to run on the input before the main work of this
     * processor is done.  Pre-loader will be run in the order they appear in the
     * List.  A pre-processor's input and output types must be the same as this processor's
     * input type.
     * 
     * @param processors list of pre-loader to run before the main work of this processor
     */
    public void setPreProcessors( final List<Processor<I, I>> processors ) {
        this.preProcessors = processors;
    }

    /**
     * Sets the list of post-loader to run on the output before it is returned.  Post-
     * loader will be run in the order they appear in the List.  A post-processor's
     * input and output types must match the output type of this processor.
     *
     * @param processors list of post-loader to run before returning output from this processor
     */
    public void setPostProcessors( final List<Processor<O, O>> processors ) {
        this.postProcessors = processors;
    }

    /**
     * Adds a post-processor to the end of the list of post-loader.
     * 
     * @param processor the pre-processor to add
     */
    public void addPreProcessor( final Processor<I, I> processor ) {
        if(preProcessors == null) {
            preProcessors = new ArrayList<Processor<I, I>>();
        }
        preProcessors.add( processor );
    }

    /**
     * Adds a post-processor to the end of the list of post-loader.
     * 
     * @param processor the processor to add
     */
    public void addPostProcessor( final Processor<O, O> processor ) {
        if(postProcessors == null) {
            postProcessors = new ArrayList<Processor<O, O>>();
        }
        postProcessors.add( processor );
    }

    /**
     * Gets whether or not this processor should sent email when it is done excuting.
     *
     * @return true if email should be sent, false if not
     */
    public boolean getDoEmail() {
        return doEmail;
    }

    /**
     * Sets whether or not this processor should send email when it is done executing. Default is false.
     *
     * @param doEmail true if email should be sent, false if not.
     */
    public void setDoEmail( final boolean doEmail ) {
        this.doEmail = doEmail;
    }

    /**
     * Sets the MailSender object this processor should use.
     *
     * @param mailSender the MailSender instance
     */
    public void setMailSender( final MailSender mailSender ) {
        this.mailSender = mailSender;
    }

    // to be removed
    public void setToEmails( final String toEmails ) {
        this.toEmails = toEmails;
    }

    /**
     * Gets the description for the processor as it applies to the given input.
     *
     * @param input the input object passed to the processor
     * @return the description of this processor as it applies to the input
     */
    public String getDescription( final I input ) {
        return getName() + ( input != null ? ( " on " + input.toString() ) : "" );
    }

	/**
     * Gets whether or not this processor should sent email when it is done executing input validations.
	 * @return the inputValidationDoEmail true if email should be sent, false if not
	 */
	public boolean getInputValidationDoEmail() {
		return inputValidationDoEmail.get();
	}

	/**
     * Sets whether or not this processor should send email when it is done executing input validations. Default is false.
	 * @param inputValidationDoEmail the inputValidationDoEmail to set true if email should be sent, false if not
	 */
	public void setInputValidationDoEmail(boolean inputValidationDoEmail) {
		this.inputValidationDoEmail.getAndSet(inputValidationDoEmail);
	}
}
