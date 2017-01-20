package edu.kit.informatik.pcc.service.videoprocessing.chain;

import edu.kit.informatik.pcc.service.videoprocessing.EditingContext;
import edu.kit.informatik.pcc.service.videoprocessing.IStage;

/**
 * Stage used to forward files when the simple chain gets used and anonymizing gets skipped.
 *
 * @author Josh Romanowski
 */
public class FileForwarder implements IStage {

    @Override
    public boolean execute(EditingContext context) {
        context.getDecVid().renameTo(context.getAnonymizedVid());
        return true;
    }

    @Override
    public String getName() {
        return "File forwarder";
    }
}
