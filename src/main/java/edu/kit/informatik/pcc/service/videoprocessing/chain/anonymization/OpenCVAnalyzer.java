package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.server.Main;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

import java.util.logging.Logger;

/**
 * Class that analyzes a frame with the OpenCV
 * framework and identifies all frontal faces.
 *
 * @author Josh Romanowski
 */
public class OpenCVAnalyzer implements IAnalyzer {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Classifier used to detect faces.
     */
    private CascadeClassifier classifier;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Loads the classifier.
     */
    public OpenCVAnalyzer() {
        classifier = new CascadeClassifier(LocationConfig.RESOURCES_DIR + "\\haarcascade_frontalface_alt.xml");
        if (classifier.empty()) {
            Logger.getGlobal().severe("Classifier couldn't be loaded");
            Main.stopServer();
        }
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    public RectVector analyze(Mat frame) {
        RectVector detections = new RectVector();
        classifier.detectMultiScale(frame, detections);
        return detections;
    }
}
