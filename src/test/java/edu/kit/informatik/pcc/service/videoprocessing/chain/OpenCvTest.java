package edu.kit.informatik.pcc.service.videoprocessing.chain;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
@Ignore
public class OpenCvTest
{
    @BeforeClass
    public static void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void OpenCvTets() {
        Mat mat = new Mat();
        assertNotNull(mat);
    }
}
