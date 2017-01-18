package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.videoprocessing.EditingContext;
import edu.kit.informatik.pcc.service.videoprocessing.IStage;

import java.io.File;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public abstract class AAnonymizer implements IStage {

    public boolean execute(EditingContext context) {
        return anonymize(context.getDecVid(), context.getAnonymizedVid());
    }

    public abstract boolean anonymize(File input, File output);
}
