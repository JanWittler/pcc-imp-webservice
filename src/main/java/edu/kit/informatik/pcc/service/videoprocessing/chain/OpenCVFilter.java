package edu.kit.informatik.pcc.service.videoprocessing.chain;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Uses an OpenCV blur filter to anonymize face detections.
 *
 * @author Josh Romanowski
 */
public class OpenCVFilter implements IFilter {

    //methods

    @Override
    public Mat applyFilter(Mat frame, MatOfRect detections) {
        for (Rect rect : detections.toArray()) {
            Imgproc.blur(frame.submat(rect), frame.submat(rect), new Size(55, 55));
        }
        return frame;
    }
}
