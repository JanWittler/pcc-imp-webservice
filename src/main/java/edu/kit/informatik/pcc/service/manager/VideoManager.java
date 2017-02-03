package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.*;
import edu.kit.informatik.pcc.service.videoprocessing.VideoProcessingManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.container.AsyncResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The VideoManager processes requests concerning video up-/download and progression.
 *
 * @author Fabian Wenzel, David Laubenstein
 */
public class VideoManager {

    // processing status constants
    private final String SUCCESS = "SUCCESS";
    private final String FAILURE = "FAILURE";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Active user account.
     */
    private Account account;
    /**
     * Database manager used to process database queries.
     */
    private DatabaseManager databaseManager;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Creates a new video manager for the given account.
     *
     * @param account Active user account.
     */
    public VideoManager(Account account) {
        this.account = account;
        databaseManager = new DatabaseManager(account);
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * Gets the video information for all videos of a user and creates a JSON string out of it.
     *
     * @return Returns JSON string with a JSON array containing all VideoInfo JSONs.
     */
    public String getVideoInfoList() {
        //convert VideoInfos to JSONArray
        ArrayList<VideoInfo> videoInfoList = databaseManager.getVideoInfoList();
        if (videoInfoList == null) {
            Logger.getGlobal().warning("An error occurred fetching the videoInfoList!");
            return FAILURE;
        }
        JSONArray videoInfoArray = new JSONArray();
        for (int i = 0; i < videoInfoList.size(); i++) {
            String json = videoInfoList.get(i).getAsJson();
            JSONObject jsonObject = new JSONObject(json);
            videoInfoArray.put(i, jsonObject);
        }
        return videoInfoArray.toString();
    }

    /**
     * Uploads a video to the server. Files get uploaded as input streams.
     * As the processing of the video is asynchronous to the uploading, there will not be
     * a useful immediate response.
     *
     * @param video                 inputstream of video file to upload
     * @param metadata              inputstream of metadata file to upload
     * @param encryptedSymmetricKey inputstream of key file to upload
     * @param videoName             name of the uploaded video without extention
     * @param response              asynchronous response used to give response to the client
     * @return Returns process status message.
     */
    public String upload(InputStream video, InputStream metadata, InputStream encryptedSymmetricKey,
                         String videoName, AsyncResponse response) {
        VideoProcessingManager videoProcessingManager = VideoProcessingManager.getInstance();

        if (videoProcessingManager == null) {
            return FAILURE;
        }

        videoProcessingManager.addTask(video, metadata, encryptedSymmetricKey, account, videoName, response);
        return SUCCESS;
    }

    /**
     * Fetches a video from the database and provides an InputStream to download the video.
     *
     * @param videoId Unique video identifier of the video to download.
     * @return Returns an InputStream for the download.
     */
    public InputStream download(int videoId) {
        VideoInfo videoInfo = databaseManager.getVideoInfo(videoId);

        if (videoInfo == null) {
            return null;
        }

        String videoName = videoInfo.getName();
        File video = new File(LocationConfig.ANONYM_VID_DIR + File.separator + videoName + ".avi");

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(video.getPath());
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("An error has occurred finding file to download!");
            return null;
        }
        return inputStream;
    }

    /**
     * Deletes a video and the respective metadata file from the server.
     *
     * @param videoId of video to delete
     * @return string if file deletion successfully accomplished
     */
    public String videoDelete(int videoId) {
        VideoInfo videoInfo = databaseManager.getVideoInfo(videoId);
        if (videoInfo == null) {
            return FAILURE;
        }

        // delete video file
        File videoFile = new File(
                LocationConfig.ANONYM_VID_DIR + File.separator + videoInfo.getName() + ".mp4");
        if (videoFile.exists())
            videoFile.delete();

        //delete metadata file.
        String metaName = databaseManager.getMetaName(videoId);
        File metaFile = new File(LocationConfig.META_DIR + File.separator + metaName + ".json");
        if (metaFile.exists())
            metaFile.delete();

        //delete both in database
        return databaseManager.deleteVideoAndMeta(videoId) ? SUCCESS : FAILURE;
    }

    /**
     * Gets the metadata of a video as JSON string.
     *
     * @param videoId Unique identifier of the video to fetch metadata for.
     * @return JSON string with metadata information
     */
    public String getMetaData(int videoId) {
        Metadata metadata = databaseManager.getMetaData(videoId);
        if (metadata == null) {
            return FAILURE;
        }
        return metadata.getAsJSON();
    }
}
