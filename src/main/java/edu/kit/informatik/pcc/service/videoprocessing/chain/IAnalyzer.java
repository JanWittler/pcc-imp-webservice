package edu.kit.informatik.pcc.service.videoprocessing.chain;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public interface IAnalyzer {

    public MatOfRect analyze(Mat frmae);
}
