package edu.kit.informatik.pcc.service.videoprocessing.chain;

import javax.crypto.SecretKey;
import java.io.File;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public interface IKeyDecryptor {

    public SecretKey decrypt(File encKey);
}
