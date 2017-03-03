package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * @author Josh Romanowski
 */
public class OpenCVPythonAnonymizer extends AAnonymizer {
    private static final String PYTHON_DIR = LocationConfig.PROJECT_DIR + File.separator + "Python";
    private static final String ANONYM_SUFFIX = "_out_inverse_result_inverted";

    private VideoPictureConverter converter;

    public OpenCVPythonAnonymizer() {
        this.converter = new VideoPictureConverter();
    }

    @Override
    public boolean anonymize(File input, File output) {
        IContainer container = IContainer.make();
        container.open(input.getAbsolutePath(), IContainer.Type.READ, null);
        IStream stream = container.getStream(0);

        if (stream == null) {
            Logger.getGlobal().warning("Uploaded file contained no video stream");
            return false;
        }

        // read settings
        IStreamCoder coder = stream.getStreamCoder();
        int width = coder.getWidth();
        int heigth = coder.getHeight();
        double fps = coder.getFrameRate().getValue();
        long length = container.getDuration() / 1000000;
        coder.close();
        container.close();


        Logger.getGlobal().info(String.format("Start splitting %s", input.getName()));

        // make temporary editing dir
        File editingDir = new File(LocationConfig.TEMP_DIR + File.separator + "editingDir");
        if (!editingDir.mkdir())
            return false;

        // make temporary dir for pictures
        File picDir = new File(editingDir + File.separator + "input");
        if (!picDir.mkdir())
            return false;

        // split up video
        converter.splitUp(input, picDir, fps);

        Logger.getGlobal().info(String.format(
                "Start anonymizing %s. Fps:%d, Size:%d x %d, Dur:%ds",
                input.getName(), (int) fps, width, heigth, length));

        //anonymize
        try {
            Process p = Runtime.getRuntime().exec(
                    "python processing_chain.py -i " + picDir.getAbsolutePath() + " -f 10", null,
                    new File(PYTHON_DIR));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((reader.readLine()) != null) {
            }
            p.waitFor();
            reader.close();
        } catch (IOException | InterruptedException e) {
            Logger.getGlobal().warning("executing python script failed");
            return false;
        }

        Logger.getGlobal().info(String.format("Start merging %s", input.getName()));

        // merge video again
        File anonymPicDir = new File(picDir + ANONYM_SUFFIX);
        converter.merge(anonymPicDir, fps, width, heigth, output);

        // delete all files
        try {
            FileUtils.cleanDirectory(editingDir);
            editingDir.delete();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

}
