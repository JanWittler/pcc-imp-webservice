package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.data.LocationConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Test for the decryptor classes.
 *
 * @author Josh Romanowski
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
        encKey = new File(LocationConfig.TEST_RESOURCES_DIR + "\\encKey.txt");
        encVid = new File(LocationConfig.TEST_RESOURCES_DIR + "\\encVid.mp4");
        decVid = new File(LocationConfig.OUTPUT_DIR + "\\decVid.mp4");
        encMeta = new File(LocationConfig.TEST_RESOURCES_DIR + "\\encMeta.json");
        decMeta = new File(LocationConfig.OUTPUT_DIR + "\\target\\decMeta.txt");
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
