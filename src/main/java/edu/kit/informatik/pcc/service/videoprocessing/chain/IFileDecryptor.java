package edu.kit.informatik.pcc.service.videoprocessing.chain;

import javax.crypto.SecretKey;
import java.io.File;

/**
 * Interface for classes that decrypt single files with
 * a symmetric secret key.
 * Should be synced with the file encryptor in the app so both use the same algorithm.
 *
 * @author Josh Romanowski
 */
public interface IFileDecryptor {

    // methods

    /**
     * Decrypts a file with the given symmetric key and saves the
     * decrypted file to the desired location.
     *
     * @param input  Input file.
     * @param key    Symmetric key for decryption.
     * @param output Output file.
     * @return Returns whether decryption was successfull or not.
     */
    public boolean decrypt(File input, SecretKey key, File output);
}
