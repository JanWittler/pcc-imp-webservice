package edu.kit.informatik.pcc.service.videoprocessing.chain;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Class that takes an input file and decrypts it with
 * an AES Secret key.
 *
 * @author Josh Romanowski
 */
public class AESDecryptor implements IFileDecryptor {

    // methods

    @Override
    public boolean decrypt(File input, SecretKey key, File output) {
        if (input == null || key == null || output == null) {
            Logger.getGlobal().warning("Empty input/key/output");
            return false;
        }

        // open files
        FileInputStream encfis = null;
        FileOutputStream decfos = null;

        try {
            encfis = new FileInputStream(input);
            decfos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("Could not open input/output " + input.getName() + ", " + output.getName());
            return false;
        }

        // create cipher
        Cipher decipher = null;
        try {
            decipher = Cipher.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            Logger.getGlobal().warning("Creating cipher failed");
            return false;
        }

        CipherOutputStream cos = new CipherOutputStream(decfos, decipher);

        // decrypt
        int read;
        byte[] buffer = new byte[1024];

        try {
            while ((read = encfis.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
                cos.flush();
            }
            cos.close();
            encfis.close();
        } catch (IOException e) {
            Logger.getGlobal().warning("Error while decrypting file " + input.getName());
            return false;
        }
        return true;
    }
}
