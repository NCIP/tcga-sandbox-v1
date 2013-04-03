/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * TODO: Commented out the JUnit import and the assertions below since this is not a unit test, and also because
 *  Maven does not provide JUnit as a runtime dependency for production code resulting in compilation errors.
 *
 * import static junit.framework.Assert.assertEquals;
 * import static org.junit.Assert.assertFalse;
 */

/**
 * @author David Nassau
 *         Last updated by: $Author: nichollsmc $
 * @version $Rev: 9488 $
 */
public class FileComparer {
    public static void compareFiles(final String fname1, final String fname2) throws IOException {

        File f1 = new File(fname1);
        File f2 = new File(fname2);

        char b1, b2;
        FileInputStream fins1 = new FileInputStream(f1);
        FileInputStream fins2 = new FileInputStream(f2);

        DataInputStream dins1 = new DataInputStream(fins1);
        DataInputStream dins2 = new DataInputStream(fins2);

        b1 = dins1.readChar();
        b2 = dins2.readChar();
        try {
            // assertFalse("file1 has no content", b1 < 0);
            while (b1 > 0) {
                // assertEquals("files are not equal", b1, b2);
                b1 = dins1.readChar();
                b2 = dins2.readChar();
            }

        }
        finally {
            dins1.close();
            fins1.close();
            dins2.close();
            fins2.close();
        }
    }
}
