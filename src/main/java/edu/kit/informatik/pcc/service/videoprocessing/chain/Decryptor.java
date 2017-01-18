package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.videoprocessing.EditingContext;
import edu.kit.informatik.pcc.service.videoprocessing.IStage;

import javax.crypto.SecretKey;
import java.io.File;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class Decryptor implements IStage{
    IFileDecryptor fileDecryptor;
    IKeyDecryptor keyDecryptor;

    public Decryptor() {
        fileDecryptor = new AESDecryptor();
        keyDecryptor = new RSADecryptor();
    }

    public boolean execute(EditingContext context) {
       return decrypt(context.getEncVid(), context.getEncKey(),
               context.getEncMetadata(), context.getDecVid(), context.getDecMetadata());
    }

    protected boolean decrypt(File encVid, File encKey, File encMeta, File decVid, File decMeta) {
        SecretKey key = keyDecryptor.decrypt(encKey);
        return fileDecryptor.decrypt(encVid, key, decVid) && fileDecryptor.decrypt(encMeta, key, decMeta);
    }
}
