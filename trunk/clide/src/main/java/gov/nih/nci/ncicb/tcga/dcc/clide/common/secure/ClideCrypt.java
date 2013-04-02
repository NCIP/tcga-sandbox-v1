/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common.secure;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Encrypt and decrypt files with AES using public and private keys
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClideCrypt {

    public long encrypt(final File targetFile, final File encryptedFile, final File keyFile)
            throws
            GeneralSecurityException,
            IOException,
            ClassNotFoundException {

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        SecretKey key = keygen.generateKey();

        // wrap with RSA public key
        ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(keyFile));
        Key publicKey = (Key) keyIn.readObject();
        keyIn.close();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.WRAP_MODE, publicKey);
        byte[] wrappedKey = cipher.wrap(key);
        DataOutputStream out = new DataOutputStream(new FileOutputStream(encryptedFile));
        out.writeInt(wrappedKey.length);
        out.write(wrappedKey);

        InputStream in = new FileInputStream(targetFile);
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        long timeSpent;
        try {
            timeSpent = crypt(in, out, cipher);
        } finally {
            in.close();
            out.close();
        }
        return timeSpent;
    }

    public long decrypt(final File encryptedFile, final File decryptedFile, final File keyFile)
            throws
            IOException,
            ClassNotFoundException,
            GeneralSecurityException {

        DataInputStream in = new DataInputStream(new FileInputStream(encryptedFile));
        int length = in.readInt();
        byte[] wrappedKey = new byte[length];
        in.read(wrappedKey, 0, length);

        // unwrap with RSA private key
        ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(keyFile));
        Key privateKey = (Key) keyIn.readObject();
        keyIn.close();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        Key key = cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

        OutputStream out = new FileOutputStream(decryptedFile);
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        long timeSpent;
        try {
            timeSpent = crypt(in, out, cipher);
        } finally {
            in.close();
            out.close();
        }
        return timeSpent;
    }

    /**
     * Uses a cipher to transform the bytes in an input stream and sends the transformed bytes to an output stream.
     *
     * @param in     the input stream
     * @param out    the output stream
     * @param cipher the cipher that transforms the bytes
     * @return milliseconds spent in this method
     * @throws java.io.IOException couldn't open a file
     * @throws java.security.GeneralSecurityException
     *                             no permission to use crypto
     */
    public long crypt(
            final InputStream in, final OutputStream out,
            final Cipher cipher) throws IOException, GeneralSecurityException {
        long start = System.currentTimeMillis();
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] inBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];

        int inLength = 0;

        boolean more = true;
        while (more) {
            inLength = in.read(inBytes);
            if (inLength == blockSize) {
                int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
                out.write(outBytes, 0, outLength);
            } else {
                more = false;
            }
        }
        if (inLength > 0) {
            outBytes = cipher.doFinal(inBytes, 0, inLength);
        } else {
            outBytes = cipher.doFinal();
        }
        out.write(outBytes);
        return System.currentTimeMillis() - start;
    }
}