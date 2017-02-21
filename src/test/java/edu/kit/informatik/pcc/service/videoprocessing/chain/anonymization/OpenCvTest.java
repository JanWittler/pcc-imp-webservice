package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import org.bytedeco.javacpp.opencv_core;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class OpenCvTest {

    @Test
    public void OpenCvTets() {
        opencv_core.Mat mat = new opencv_core.Mat();
        assertNotNull(mat);
    }
}
