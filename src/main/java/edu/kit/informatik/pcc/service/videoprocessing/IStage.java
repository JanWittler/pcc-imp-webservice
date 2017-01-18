package edu.kit.informatik.pcc.service.videoprocessing;

/**
 * Created by Josh Romanowski on 17.01.2017.
 */
public interface IStage {

    /**
     * Executes the work of the stage.
     *
     * @param context Context in which the stage gets executed.
     * @return Returns weather editing was successful or not.
     */
    public boolean execute(EditingContext context);
}
