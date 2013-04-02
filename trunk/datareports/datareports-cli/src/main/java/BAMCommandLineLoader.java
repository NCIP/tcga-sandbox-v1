/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

import gov.nih.nci.ncicb.tcga.dcc.bamloader.LoaderBAM;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Bam file command line loader
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BAMCommandLineLoader {

    public static final String APP_CONTEXT = "appContext-BAMLoader.xml";

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new FileSystemXmlApplicationContext(APP_CONTEXT);
        ctx.registerShutdownHook();
        LoaderBAM loader = (LoaderBAM) ctx.getBean("loaderBAM");
        loader.start();
    }

}//End of Class
