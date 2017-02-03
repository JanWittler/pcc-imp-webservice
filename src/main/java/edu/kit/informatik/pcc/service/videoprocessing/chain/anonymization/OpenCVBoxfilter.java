package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Uses an OpenCV blur filter to anonymize face detections.
 *
 * @author Josh Romanowski
 */
public class OpenCVBoxfilter implements IFilter {

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    public Mat applyFilter(Mat frame, MatOfRect detections) {
        for (Rect rect : detections.toArray()) {
            Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 0, 0), -1);
        }
        return frame;
    }
}
