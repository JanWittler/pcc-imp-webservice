package edu.kit.informatik.pcc.service.videoprocessing.chain;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import edu.kit.informatik.pcc.service.server.Main;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class RSADecryptor implements IKeyDecryptor {

    private static final String PRIVATE_KEY_FILE = System.getProperty("user.dir") + "\\src\\main\\resources\\private.key";
    private PrivateKey privateKey;

    public RSADecryptor() {
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            privateKey = (PrivateKey) inputStream.readObject();
        } catch (FileNotFoundException e) {
            Logger.getGlobal().severe("Private key file was missing");
            Main.stopServer();
            return;
        } catch (ClassNotFoundException | IOException e) {
            Logger.getGlobal().severe("Reading the private key failed");
            Main.stopServer();
            return;
        }
        Logger.getGlobal().info("Successfully read private key");
    }

    public SecretKey decrypt (File input) {
        if (input == null) {
            Logger.getGlobal().warning("Empty key " + input.getName());
            return null;
        }

        byte[] cipherText = openCrypt(input);
        final String plainText = decryptData(cipherText);
        byte[] decodedKey = Base64.getDecoder().decode(plainText);
        Logger.getGlobal().info("Successfully decrypted key " + input.getName());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    private String decryptData (byte[] text) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            dectyptedText = cipher.doFinal(text);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            Logger.getGlobal().warning("Creating cipher failed");
            return null;
        }

        return new String(dectyptedText);
    }

    private byte[] openCrypt(File file) {
        try {
            return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            Logger.getGlobal().warning("Reading crypt failed");
            return null;
        }
    }
}
