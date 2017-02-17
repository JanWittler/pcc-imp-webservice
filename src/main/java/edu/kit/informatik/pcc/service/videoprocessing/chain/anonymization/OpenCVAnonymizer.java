package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.server.Main;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.xml.stream.Location;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Implements the AAnonymizer interface.
 * Takes a video file and divides it into frames.
 * Then it analyzes each frames for faces and finally
 * makes it unrecognizable.
 *
 * @author Josh Romanowski
 */
public class OpenCVAnonymizer extends AAnonymizer {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Analyzer that takes a frame and analyzes it for faces.
     */
    private IAnalyzer analyzer;
    /**
     * Filter that makes a face recognition unrecognizable.
     */
    private IFilter filter;

    static {
        try {
            addDir(LocationConfig.PROJECT_DIR + File.separator + "lib");
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.loadLibrary("opencv_ffmpeg310_64");
        } catch (IOException | UnsatisfiedLinkError e) {
            Logger.getGlobal().severe("Loading OpenCV failed. Check project setup");
            Main.stopServer();
        }
    }

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Loads the OpenCV library and creates the filters
     */
    public OpenCVAnonymizer() {
        analyzer = new OpenCVAnalyzer();
        filter = new OpenCVBoxfilter();
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
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

        Logger.getGlobal().info("Start anonymization video " + input.getName());
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
        capture.release();
        videoWriter.release();
        Logger.getGlobal().info("Finished anonymization video " + input.getName());
        return true;
    }

    public static void addDir(String s) throws IOException {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[])field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length+1];
            System.arraycopy(paths,0,tmp,0,paths.length);
            tmp[paths.length] = s;
            field.set(null,tmp);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }
}
