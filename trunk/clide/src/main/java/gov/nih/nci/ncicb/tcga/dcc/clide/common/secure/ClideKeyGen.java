/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common.secure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A simple utility to generate new public/private keys
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClideKeyGen {

    public static final int KEYSIZE = 512;

    public static final String ALGORITHM = "RSA";

    public static void main(final String args[]) throws IOException, NoSuchAlgorithmException {
        if (args.length != 2) {
            usage();
            System.exit(-1);

        }
        try {
            new ClideKeyGen().generateNewPairOfKeys(args[0], args[1]);

        } catch (IOException iox) {
            System.err.println("There was an IO exception while trying to create the keys.");
            System.err.println(iox.getMessage());
            usage();
            // normally we would not want to rethrow, but for the sake of testing command line arguments
            // we are so the test can be sure what happened
            throw iox;

        } catch (NoSuchAlgorithmException nsax) {
            System.err.println("Algorithm " + ALGORITHM + " was not found.  Please check the version of the JDK you are using.");
            System.err.println(nsax.getMessage());
            usage();
            // normally we would not want to rethrow, but for the sake of testing command line arguments
            // we are so the test can be sure what happened
            throw nsax;

        }

    }

    private static void usage() {
        System.err.println("usage: <publicKeyPath> <privateKeyPath>");
    }

    public void generateNewPairOfKeys(final String publicKeyPath, final String privateKeyPath)
            throws NoSuchAlgorithmException, IOException {

        generateNewPairOfKeys(new File(publicKeyPath), new File(privateKeyPath));
    }


    public void generateNewPairOfKeys(final File publicKeyFile, final File privateKeyFile)
            throws NoSuchAlgorithmException, IOException {

        KeyPairGenerator pairgen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom random = new SecureRandom();
        pairgen.initialize(KEYSIZE, random);
        KeyPair keyPair = pairgen.generateKeyPair();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
        out.writeObject(keyPair.getPublic());
        out.close();
        out = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
        out.writeObject(keyPair.getPrivate());
        out.close();
    }
}
