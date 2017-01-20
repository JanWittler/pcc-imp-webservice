package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.videoprocessing.EditingContext;
import edu.kit.informatik.pcc.service.videoprocessing.IStage;

import java.io.File;

/**
 * Interface for single video anonymizing classes.
 * Takes a video file analyzes it for personal content and
 * makes it unrecognizable. Puts the anonymized video to
 * the desired output file.
 *
 * @author Josh Romanowski
 */
public abstract class AAnonymizer implements IStage {

    // methods

    @Override
    public boolean execute(EditingContext context) {
        return anonymize(context.getDecVid(), context.getAnonymizedVid());
    }

    /**
     * Takes an input video file, analyzes it for personal content
     * and makes the personal content unrecognizable.
     *
     * @param input  Input video file.
     * @param output Output video file.
     * @return Returns whether the anonymizing was successful or not.
     */
    public abstract boolean anonymize(File input, File output);
}
