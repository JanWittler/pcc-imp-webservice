package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.server.Main;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class Anonymizer extends AAnonymizer {

    private IAnalyzer analyzer;
    private IFilter filter;

    public Anonymizer() {
        try
        {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.loadLibrary("opencv_ffmpeg310_64");
        }
        catch (UnsatisfiedLinkError e) {
            Logger.getGlobal().severe("Loading OpenCV failed. Check project setup");
            Main.stopServer();
        }

        analyzer = new ExampleAnalyzer();
        filter = new ExampleFilter();
    }

    public boolean anonymize(File input, File output) {
        if (input == null || output == null) {
            Logger.getGlobal().warning("Invalid input/oputput");
            return false;
        }

        //setup capturing
        VideoCapture capture = new VideoCapture(input.getAbsolutePath());
        if (!capture.isOpened()) {
            Logger.getGlobal().warning("Video " + input.getAbsolutePath() + " couldn't be opened.");
            return false;
        }

        Size frameSize = new Size((int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH),
                (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        VideoWriter videoWriter = new VideoWriter(output.getAbsolutePath(), VideoWriter.fourcc('X', 'V', 'I', 'D'),
                capture.get(Videoio.CAP_PROP_FPS), frameSize, true);

        //start capture
        Mat frame = new Mat();

        Logger.getGlobal().info("Start anonymizing video " + input.getName());
        while (capture.read(frame)) {
            if (!frame.empty()) {
                // detect faces
                MatOfRect detections = analyzer.analyze(frame);
                frame = filter.applyFilter(frame, detections);
            } else {
                Logger.getGlobal().info("Finished capturing video " + input.getName());
                break;
            }
            videoWriter.write(frame);
        }
        Logger.getGlobal().info("Finished anonymizing video " + input.getName());
        return true;
    }
}
