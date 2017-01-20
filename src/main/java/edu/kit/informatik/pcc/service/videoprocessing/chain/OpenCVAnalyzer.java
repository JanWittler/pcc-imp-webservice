package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.server.Main;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.logging.Logger;

/**
 * Class that analyzes a frame with the OpenCV
 * framework and identifies all frontal faces.
 *
 * @author Josh Romanowski
 */
public class OpenCVAnalyzer implements IAnalyzer {

    // attributes

    /**
     * Classifier used to detect faces.
     */
    private CascadeClassifier classifier;

    // constructors

    /**
     * Loads the classifier.
     */
    public OpenCVAnalyzer() {
        classifier = new CascadeClassifier(LocationConfig.RESOURCES_DIR + "\\haarcascade_frontalface_alt.xml");
        if (classifier.empty()) {
            Logger.getGlobal().severe("Classifier couldn't be loaded");
            Main.stopServer();
        } else {
            Logger.getGlobal().info("Successfully loaded classifier");
        }
    }

    // methods

    @Override
    public MatOfRect analyze(Mat frame) {
        MatOfRect detections = new MatOfRect();
        classifier.detectMultiScale(frame, detections);
        return detections;
    }
}
