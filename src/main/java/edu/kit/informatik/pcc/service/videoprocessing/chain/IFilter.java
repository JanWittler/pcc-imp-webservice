package edu.kit.informatik.pcc.service.videoprocessing.chain;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * Interface for classes that make detections
 * unrecognizable with the OpenCV framework.
 *
 * @author Josh Romanowski
 */
public interface IFilter {

    //methods

    /**
     * Takes the input frame and makes all dections
     * on it unrecognizable.
     *
     * @param frame      Input frame.
     * @param detections Detections to anonymize.
     * @return Returns the edited input frame.
     */
    public Mat applyFilter(Mat frame, MatOfRect detections);
}
