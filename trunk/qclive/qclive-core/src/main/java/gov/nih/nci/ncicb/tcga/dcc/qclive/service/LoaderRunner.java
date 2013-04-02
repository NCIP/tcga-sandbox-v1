/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.BareBonesLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.Loader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import java.io.Serializable;

/**
 * Runner that wraps a Loader instance and is called by Quartz to start things going.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LoaderRunner implements Serializable, LoaderRunnerI {

    private static final long serialVersionUID = 12345678910L;

    private Loader loader;

    public LoaderRunner() {
    }

    public Loader getLoader() {
        return loader;
    }

    public void setLoader(final Loader loader) {
        this.loader = loader;
    }

    public void runJob() throws LoaderException {
        //this is how we get around having to serialize the DAO (which can't be done)
        //We get it from the singleton LoaderEnqueuer
        loader.setLoaderQueries(LoaderEnqueuer.getLoaderEnqueuer().getLoaderQueries());
        loader.setCommonArchiveQueries(LoaderEnqueuer.getLoaderEnqueuer().getCommonArchiveQueries());
        loader.setDiseaseArchiveQueries(LoaderEnqueuer.getLoaderEnqueuer().getDiseaseArchiveQueries());
        loader.setUuidDAO(LoaderEnqueuer.getLoaderEnqueuer().getUuidDAO());
        loader.setMailSender(LoaderEnqueuer.getLoaderEnqueuer().getMailSender());
        loader.setLogger(new BareBonesLogger());
        loader.go();
    }
}
