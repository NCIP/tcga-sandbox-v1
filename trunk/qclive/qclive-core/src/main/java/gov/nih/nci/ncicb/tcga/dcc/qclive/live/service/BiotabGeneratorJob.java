/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGenerator;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGeneratorException;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Biotab generator job is used to generate biotab files for a disease
 *
 * @author Stanley Girshik Last updated by: $Author$
 * @version $Rev$
 */
public class BiotabGeneratorJob implements Job {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void execute(final JobExecutionContext context)
			throws JobExecutionException {
		// look up dependencies in Spring config	
		FileGenerator biotabGenerator = (FileGenerator)SpringApplicationContext.getObject(ConstantValues.BIOTAB_GENERATOR_SPRING_BEAN_NAME);
		String tumorName = context.getJobDetail().getJobDataMap().getString("tumorName");
		try {
			logger.debug("Started executing biotab generator for " + tumorName);
			// kick off biotab generator to generate biotab filess
			biotabGenerator.generate(tumorName);
			logger.debug("Stopped executing biotab generator for " + tumorName);
		} catch (FileGeneratorException e) {
			String errorMessage = "Failed to generate biotab archives for " + tumorName;
			logger.error(errorMessage,e);
            sendErrorEmail(e, errorMessage);			
		}					
	}
	
	protected MailErrorHelper getErrorMailSender() {
        return (MailErrorHelper) SpringApplicationContext.getObject(ConstantValues.MAIL_ERROR_HELPER_SPRING_BEAN_NAME);
    }

    private void sendErrorEmail(final Exception exception,
                                final String message) {
        getErrorMailSender().send(message, StringUtil.stackTraceAsString(exception));
    }
}
