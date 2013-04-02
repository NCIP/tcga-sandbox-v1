/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

import gov.nih.nci.ncicb.tcga.dcc.clide.common.secure.ClideKeyGen;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * A simple class outside of all packages to simplify the CLIDE command line
 *
 * @author Jon Whitmore
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class KeyGen {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ClideKeyGen.main(args);

    }
}
