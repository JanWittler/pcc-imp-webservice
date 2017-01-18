package edu.kit.informatik.pcc.service.videoprocessing;

import edu.kit.informatik.pcc.service.server.Main;
import edu.kit.informatik.pcc.service.videoprocessing.chain.Anonymizer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class AnonymizeTest {

    private Anonymizer anonymizer;
    private File input;
    private File output;

    @Before
    public void setUp() {
        anonymizer = new Anonymizer();
        Main.LOGGER = Logger.getGlobal();
        input = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\input.mp4");
        output = new File(System.getProperty("user.dir") + "\\target\\output.avi");
    }

    @Test
    public void nullTest() {
        Assert.assertFalse(anonymizer.anonymize(null, output));
        Assert.assertFalse(anonymizer.anonymize(input, null));
        Assert.assertFalse(anonymizer.anonymize(null, null));
    }

    @Test
    public void validTest() {
        Assert.assertTrue(anonymizer.anonymize(input, output));
    }
}
