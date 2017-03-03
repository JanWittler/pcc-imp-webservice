package edu.kit.informatik.pcc.service.videoprocessing.chain.decryption;

import edu.kit.informatik.pcc.service.server.Main;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Decrypts a SecretKey with it's private asymmetric Key.
 * Therefore uses the RSA algorithm for decryption.
 *
 * @author Josh Romanowski
 */
public class RSADecryptor implements IKeyDecryptor {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Location of the private asymmetric key.
     */
    private static final String PRIVATE_KEY_FILE = "/private.key";
    /**
     * Private asymmetric key.
     */
    private PrivateKey privateKey;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Loads the private asymmetric key.
     */
    public RSADecryptor() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(getClass().getResourceAsStream(PRIVATE_KEY_FILE));
            privateKey = (PrivateKey) inputStream.readObject();
            inputStream.close();
        } catch (FileNotFoundException e) {
            Logger.getGlobal().severe("Private key file was missing");
            Main.stopServer();
        } catch (ClassNotFoundException | IOException e) {
            Logger.getGlobal().severe("Reading the private key failed");
            Main.stopServer();
        }
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    public SecretKey decrypt(File input) {
        if (input == null) {
            Logger.getGlobal().warning("Asymmetric key missing");
            return null;
        }

        // read cipher
        byte[] cipherText;
        try {
            cipherText = Files.readAllBytes(Paths.get(input.getAbsolutePath()));
        } catch (IOException e) {
            Logger.getGlobal().warning("Reading crypt failed");
            return null;
        }

        // decrypt the text using the private key
        String plainText;
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            plainText = new String(cipher.doFinal(cipherText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            Logger.getGlobal().warning("Creating cipher failed");
            return null;
        }

        // decode secret key
        byte[] decodedKey = Base64.getDecoder().decode(plainText);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
