/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 2, 2009
 * Time: 12:45:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoaderException extends Exception {

    public LoaderException( final String message ) {
        super( message );
    }

    public LoaderException( final String message, final Throwable cause ) {
        super( message, cause );
    }

    public LoaderException( final Throwable cause ) {
        super( cause );
    }
}
