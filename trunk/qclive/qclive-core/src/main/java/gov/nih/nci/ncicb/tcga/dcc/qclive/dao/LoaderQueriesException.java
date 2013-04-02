/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 2, 2009
 * Time: 12:15:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoaderQueriesException extends Exception {

    public LoaderQueriesException( Throwable cause ) {
        super( cause );
    }

    public LoaderQueriesException( String msg ) {
        super( msg );
    }

    public LoaderQueriesException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}
