package edu.kit.informatik.pcc.service.videoprocessing.chain.decryption;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Class that takes an input file and decrypts it with an AES Secret key.
 *
 * <p>Specifically uses AES/ECB/PKCS5Padding version of the AES algorithm as standard AES algorithms
 * vary between Android and desktop JRE's. Therefore be careful when changing the algorithms.
 * Also exclusively uses 128 bit keys as some Android devices use other formats as default
 * but 128 bit is the only size supported by all Java JRE's.</p>
 *
 * @author Josh Romanowski
 */
public class AESDecryptor implements IFileDecryptor {

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    public boolean decrypt(File input, SecretKey key, File output) {
        if (input == null || key == null || output == null) {
            Logger.getGlobal().warning("Empty input/key/output");
            return false;
        }

        // open files
        FileInputStream encfis;
        FileOutputStream decfos;
        try {
            encfis = new FileInputStream(input);
            decfos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("Could not open input/output " + input.getName() + ", " + output.getName());
            return false;
        }

        // create cipher
        Cipher decipher;
        try {
            decipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            Logger.getGlobal().warning("Creating cipher failed");
            return false;
        }

        // decrypt
        try {
            int read;
            byte[] buffer = new byte[1024];
            CipherOutputStream cos = new CipherOutputStream(decfos, decipher);

            while ((read = encfis.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
                cos.flush();
            }
            cos.close();
            decfos.flush();
            decfos.close();
            encfis.close();
        } catch (IOException e) {
            Logger.getGlobal().warning("Error while decrypting file " + input.getName());
            return false;
        }
        return true;
    }
}
