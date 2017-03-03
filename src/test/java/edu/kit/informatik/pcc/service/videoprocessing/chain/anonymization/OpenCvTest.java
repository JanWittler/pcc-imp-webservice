package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import org.bytedeco.javacpp.opencv_core;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created just to test if opencv java bindings work on that platform
 *
 * @author Josh Romanowski
 * Created by Josh Romanowski on 18.01.2017.
 */
public class OpenCvTest {

    @Test
    public void OpenCvTest() {
        opencv_core.Mat mat = new opencv_core.Mat();
        assertNotNull(mat);
    }
}
