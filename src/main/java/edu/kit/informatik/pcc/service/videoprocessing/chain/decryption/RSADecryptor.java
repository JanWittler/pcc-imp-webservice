package edu.kit.informatik.pcc.service.videoprocessing.chain.decryption;

import edu.kit.informatik.pcc.service.data.LocationConfig;
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
    private static final String PRIVATE_KEY_FILE =
            LocationConfig.RESOURCES_DIR + File.separator + "private.key";
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
        ObjectInputStream inputStream;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            privateKey = (PrivateKey) inputStream.readObject();
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
