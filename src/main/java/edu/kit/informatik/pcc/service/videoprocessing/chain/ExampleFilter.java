package edu.kit.informatik.pcc.service.videoprocessing.chain;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class ExampleFilter implements IFilter {


    public Mat applyFilter(Mat frame, MatOfRect detections) {
        for (Rect rect : detections.toArray()) {
            Imgproc.blur(frame.submat(rect), frame.submat(rect), new Size(55, 55));
        }
        return frame;
    }
}
