package edu.kit.informatik.pcc.service.videoprocessing;

/**
 * Interface for classes used by the VideoProcessingChain.
 * Only provides a very general interface so very diverse functionality
 * can be provided by the stages.
 *
 * @author Josh Romanowski
 */
public interface IStage {

    // methods

    /**
     * Executes the work of the stage.
     *
     * @param context Context in which the stage gets executed.
     * @return Returns weather editing was successful or not.
     */
    public boolean execute(EditingContext context);

    /**
     * Gets the name of a stage.
     *
     * @return Return the stages name.
     */
    public String getName();
}
