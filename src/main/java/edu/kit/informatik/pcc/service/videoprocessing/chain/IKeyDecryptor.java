package edu.kit.informatik.pcc.service.videoprocessing.chain;

import javax.crypto.SecretKey;
import java.io.File;

/**
 * Interface for classes that decrpyt SecretKeys
 * with a asymmetric private key.
 *
 * @author Josh Romanowski
 */
public interface IKeyDecryptor {

    // methods

    /**
     * Decrypts the input SecretKey with the own
     * private asymmetric key.
     *
     * @param encKey Encrypted input key.
     * @return Returns the decrypted symmetric SecretKey.
     */
    public SecretKey decrypt(File encKey);
}
