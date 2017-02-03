package edu.kit.informatik.pcc.service.videoprocessing.chain.decryption;

import javax.crypto.SecretKey;
import java.io.File;

/**
 * Interface for classes that decrpyt SecretKeys
 * with a asymmetric private key.
 * Should be synced with the file encryptor in the app so both use the same algorithm.
 *
 * @author Josh Romanowski
 */
public interface IKeyDecryptor {

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * Decrypts the input SecretKey with the own
     * private asymmetric key.
     * The symmetric keys data should be encoded with Base64.
     *
     * @param encKey Encrypted input key.
     * @return Returns the decrypted symmetric SecretKey.
     */
    public SecretKey decrypt(File encKey);
}
