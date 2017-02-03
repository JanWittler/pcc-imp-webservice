package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.*;
import edu.kit.informatik.pcc.service.videoprocessing.VideoProcessingManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Fabian Wenzel, David Laubenstein
 *         Created by David Laubenstein on 01/18/2017
 */
public class VideoManager {
    //TODO: JAVADOC
    // attributes
    private final String SUCCESS = "SUCCESS";
    private final String FAILURE = "FAILURE";
    private Account account;
    private DatabaseManager databaseManager;

    // constructor
    /**
     * @param account account for videomanager
     */
    public VideoManager(Account account) {
        this.account = account;
        databaseManager = new DatabaseManager(account);
    }

    // methods
    /**
     * @return arraylist of videoinfos of account
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
     * @param video                 inputstream of videofile to upload
     * @param metadata              inputstream of metadatafile to upload
     * @param encryptedSymmetricKey inputstream of keyfile to upload
     * @param videoName             string of videoname
     * @param response              create async response
     * @return string if task started successfully
     */
    public String upload(InputStream video, InputStream metadata, InputStream encryptedSymmetricKey, String videoName, AsyncResponse response) {
        VideoProcessingManager videoProcessingManager = VideoProcessingManager.getInstance();
        videoProcessingManager.addTask(video, metadata, encryptedSymmetricKey, account, videoName, response);
        return SUCCESS;
    }

    /**
     * @param videoId of video to download
     * @return file to download
     */
    public Response download(int videoId) {
        VideoInfo videoInfo = databaseManager.getVideoInfo(videoId);
        Response.ResponseBuilder response = null;
        if (videoInfo == null) {
            return response.status(404).build();
        }
        String videoName = videoInfo.getName();
        File video = new File(LocationConfig.ANONYM_VID_DIR + File.separator + videoName + ".mp4");
        if (video == null) {
            //you cannot catch the status exception correctly
            try {
                return response.status(404).build();
            } catch (NullPointerException e) {
                Logger.getGlobal().warning("An error has occurred while building the response!");
            }
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(video.getPath());
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("An error has occurred finding file to download!");
        }
        response = Response.ok();
        return response.status(200).entity(inputStream).build();
    }

    /**
     * @param videoId of video to delete
     * @return string if file deletion successfully accomplished
     */
    public String videoDelete(int videoId) {
        VideoInfo videoInfo = databaseManager.getVideoInfo(videoId);
        if (videoInfo == null) {
            return FAILURE;
        }
        //TODO: CHECK IF NEEDED
//		Metadata metadata = databaseManager.getMetaData(videoId);
//		if (metadata == null) {
//			return FAILURE;
//		}
        String videoName = videoInfo.getName();
        String metaName = databaseManager.getMetaNameByVideoId(databaseManager.getVideoIdByName(videoName));
        File videoFile = null;
        try {
            videoFile = new File(LocationConfig.ANONYM_VID_DIR + File.separator + videoName + ".mp4");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        videoFile.delete();
        File metaFile = null;
        try {
            metaFile = new File(LocationConfig.META_DIR + File.separator + metaName + ".json");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        metaFile.delete();
        return databaseManager.deleteVideoAndMeta(videoId) ? SUCCESS : FAILURE;

    }

    /**
     * @param videoId of metadata to get
     * @return json string with metadata information
     */
    public String getMetaData(int videoId) {
        Metadata metadata = databaseManager.getMetaData(videoId);
        if (metadata == null) {
            return FAILURE;
        }
        return metadata.getAsJson();
    }
}
