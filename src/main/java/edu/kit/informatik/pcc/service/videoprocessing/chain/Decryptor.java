package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.videoprocessing.EditingContext;
import edu.kit.informatik.pcc.service.videoprocessing.IStage;

import javax.crypto.SecretKey;
import java.io.File;

/**
 * Class that decrypts uploaded the files.
 * Takes a key decryptor and decrypts the uploaded key.
 * After that it takes the key and decrypts the metadata and
 * the video with it.
 *
 * @author Josh Romanowski
 */
public class Decryptor implements IStage {

    // attributes

    /**
     * Decryptor used for decrypting video and metadata.
     */
    private IFileDecryptor fileDecryptor;

    /**
     * Decryptor used for decrypting keys.
     */
    private IKeyDecryptor keyDecryptor;

    //constructors

    /**
     * Creates the used decryptors.
     */
    public Decryptor() {
        fileDecryptor = new AESDecryptor();
        keyDecryptor = new RSADecryptor();
    }

    // methods

    public boolean execute(EditingContext context) {
        return decrypt(context.getEncVid(), context.getEncKey(),
                context.getEncMetadata(), context.getDecVid(), context.getDecMetadata());
    }

    /**
     * Takes the encrypted key and decrypts it via the keyDecryptor.
     * After that the decrypted key is used to decrypt the video and the
     * metadata.
     *
     * @param encVid  Encrypted video file.
     * @param encKey  Encrypted key file.
     * @param encMeta Encrypted metadata file.
     * @param decVid  Decrypted video file.
     * @param decMeta Decrypted metadata file.
     * @return Returns whether decrypting was successfull or not.
     */
    protected boolean decrypt(File encVid, File encKey, File encMeta, File decVid, File decMeta) {
        SecretKey key = keyDecryptor.decrypt(encKey);
        return fileDecryptor.decrypt(encVid, key, decVid) && fileDecryptor.decrypt(encMeta, key, decMeta);
    }
}
