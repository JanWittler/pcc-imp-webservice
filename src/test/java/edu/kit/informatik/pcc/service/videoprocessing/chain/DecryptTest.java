package edu.kit.informatik.pcc.service.videoprocessing.chain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class DecryptTest {

    private File encKey;
    private File encVid;
    private File decVid;
    private File encMeta;
    private File decMeta;
    private Decryptor decryptor;

    @Before
    public void setUp() {
        encKey = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\encKey.txt");
        encVid = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\encVid.mp4");
        decVid = new File(System.getProperty("user.dir") + "\\target\\decVid.mp4");
        encMeta = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\encMeta.txt");
        decMeta = new File(System.getProperty("user.dir") + "\\target\\decMeta.txt");
        decryptor = new Decryptor();
    }

    @Test
    public void nullTest() {
        Assert.assertFalse(decryptor.decrypt(null, encKey, encMeta, decVid, decMeta));
    }

    @Test
    public void validTest() {
        Assert.assertTrue(decryptor.decrypt(encVid, encKey, encMeta, decVid, decMeta));
    }
}
