/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ClinicalLoaderCaller;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.LoggerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.StdoutLoggerDestination;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class is used to bootstrap ClinicalLoaderCaller a qclive embedded
 * implimentation of ClinicalLoader This class: 1. Parses command line arguments
 * 2. Initializes ClinicalLoaderDependencies 3. Kicks off ClinicalLoaderCaller
 * load job
 * 
 * @author girshiks
 * 
 */
public class ClinicalLoaderStandalone {

	private static ApplicationContext ctx = null;
	private static ClinicalLoaderCaller loader = null;
	private static Logger logger = new LoggerImpl();
	private static String applicationContextFile;
	private static final String defaultApplicationContext = "standalone.clinical.applicationContext.xml";

	static {
		logger.addDestination(new StdoutLoggerDestination());
	}

	/**
	 * This method is used to initialize the standalone loader.
	 * 
	 * @param applicationContextFile
	 *            path to applicationContext configuration files
	 * @throws LoaderException
	 *             if initialization failed
	 */
	public static void init() throws LoaderException {

		logger.log(Level.INFO, " initializing standalone clinical loader");
		ctx = new ClassPathXmlApplicationContext(getApplicationContextFile());

		Object obj = ctx.getBean("clinicalLoaderCaller");

		if (!(obj instanceof ClinicalLoaderCaller)) {
			throw new LoaderException(
					"Unable to lookup clinical loader in Spring configuration ");
		} else {
			loader = (ClinicalLoaderCaller) obj;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		List<String> archivesToLoad = new ArrayList<String>();

		// process command line arguments
		if (args == null || args.length <= 0) {
			logger.log(Level.FATAL,
					" Invalid number of command line arguments. Clinical loader"
							+ " must have at least one archive name to load. ");
			System.exit(-1);
		} else {
			// init loader
			try {
				init();

			} catch (LoaderException e) {
				logger.log(Level.FATAL,
						" Error when initializing standalone clinical loader. exiting.. ");
				System.exit(-1);
			}
			// load archives
			archivesToLoad = Arrays.asList(args);
			for (final String archiveName : archivesToLoad) {
				try {
					// parse the diseaseName and set it on ThreadLocal
					// the format should have been validated by qclive
					String diseasePart = (archiveName.split("_"))[1];
					String disease = (diseasePart.split("\\."))[0];
					DiseaseContextHolder.setDisease(disease);
					loader.loadArchiveByName(archiveName,null);
					logger.log(Level.INFO, " Archive " + archiveName
							+ " has loaded successfuly ");
				} catch (ClinicalLoaderException e) {
					logger.log(Level.ERROR,
							" Error while attempting to load archive "
									+ archiveName);
					logger.log(e);
				}
			}
		}

	}

	public static String getApplicationContextFile() {
		if (applicationContextFile == null
				|| defaultApplicationContext.length() <= 0) {
			applicationContextFile = defaultApplicationContext;
		}
		return applicationContextFile;
	}

	public static void setApplicationContextFile(String applicationContextFile) {
		ClinicalLoaderStandalone.applicationContextFile = applicationContextFile;
	}

}
