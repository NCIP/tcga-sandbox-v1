package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.Loader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

/**
 * Interface which provide APIs for LoaderRunner
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface LoaderRunnerI {
    public Loader getLoader();

    public void setLoader(final Loader loader);

    public void runJob() throws LoaderException;
}
