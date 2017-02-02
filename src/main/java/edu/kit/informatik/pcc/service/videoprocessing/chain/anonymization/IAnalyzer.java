package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * Interfaces for classes that analyzes single frames
 * for personal information with the OpenCV functionality.
 *
 * @author Josh Romanowski
 */
public interface IAnalyzer {

    // methods

    /**
     * Takes a single frames and analyzes it for personal
     * data.
     *
     * @param frame Input frame.
     * @return Returns all face detections.
     */
    public MatOfRect analyze(Mat frame);
}
