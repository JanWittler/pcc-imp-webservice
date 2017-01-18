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
 * Created by Josh Romanowski on 18.01.2017.
 */
public class AESDecryptor implements IFileDecryptor{

    public boolean decrypt(File input, SecretKey key, File output) {
        if (input == null || key == null || output == null) {
            Logger.getGlobal().warning("Empty input/key/output");
            return false;
        }

        FileInputStream encfis = null;
        FileOutputStream decfos = null;

        try {
            encfis = new FileInputStream(input);
            decfos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("Could not open input/output " + input.getName() + ", " + output.getName());
            return false;
        }


        Cipher decipher = null;
        try {
            decipher = Cipher.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            Logger.getGlobal().warning("Creating cipher failed");
            return false;
        }

        CipherOutputStream cos = new CipherOutputStream(decfos, decipher);

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
        Logger.getGlobal().info("Successfully decrypted " + input.getName());
        return true;
    }
}
