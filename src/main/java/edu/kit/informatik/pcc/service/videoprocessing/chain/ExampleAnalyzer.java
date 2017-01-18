package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.server.Main;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class ExampleAnalyzer implements IAnalyzer {

    private CascadeClassifier classifier;

    public ExampleAnalyzer() {
        classifier = new CascadeClassifier(System.getProperty("user.dir") + "\\src\\main\\resources\\haarcascade_frontalface_alt.xml");
        if (classifier.empty()) {
            Logger.getGlobal().severe("Classifier couldn't be loaded");
            Main.stopServer();
        } else {
            Logger.getGlobal().info("Successfully loaded classifier");
        }
    }

    public MatOfRect analyze(Mat frame) {
        MatOfRect detections = new MatOfRect();
        classifier.detectMultiScale(frame, detections);
        return detections;
    }
}
