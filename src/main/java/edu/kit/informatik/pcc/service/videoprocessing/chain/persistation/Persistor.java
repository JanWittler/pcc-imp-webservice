package edu.kit.informatik.pcc.service.videoprocessing.chain.persistation;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.videoprocessing.EditingContext;
import edu.kit.informatik.pcc.service.videoprocessing.IStage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

/**
 * Persists the fully edited videos on the server.
 * Also saves the metadata to the video's metadata.
 *
 * @author Josh Romanowski
 */
public class Persistor implements IStage {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Database manager used to add the persisted video to the database.
     */
    private DatabaseManager databaseManager;

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    public boolean execute(EditingContext context) {
        databaseManager = new DatabaseManager(context.getAccount());
        return persist(context.getAnonymizedVid(), context.getDecMetadata()
                , context.getAccount(), context.getVideoName());
    }

    @Override
    public String getName() {
        return "Persistor";
    }

    /* #############################################################################################
     *                                  helper methods
     * ###########################################################################################*/

    /**
     * Moves copies the files to their final destination after
     * adding the metadata to it.
     *
     * @param video     Final processed video.
     * @param metadata  Final processed metadata.
     * @param account   The user's account.
     * @param videoName The name of the video.
     * @return Returns whether persisting was successfull or not.
     */
    private boolean persist(File video, File metadata, Account account, String videoName) {

        String metaName = videoName + "_" + "meta";

        try {

            //Save video to final destination.
            Files.copy(
                    video.toPath(),
                    new File(LocationConfig.ANONYM_VID_DIR + File.separator +
                            account.getId() + "_" + videoName + ".avi").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            //Save metadata to final destination.
            Files.copy(
                    metadata.toPath(),
                    new File(LocationConfig.META_DIR + File.separator +
                            account.getId() + "_" + metaName + ".json").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);


            //Add database entry.
            databaseManager.saveProcessedVideoAndMeta(videoName, videoName + "_" + "meta");
        } catch (IOException e) {
            Logger.getGlobal().warning("Persisting video " + videoName + " failed");
            return false;
        }
        return true;
    }
}
