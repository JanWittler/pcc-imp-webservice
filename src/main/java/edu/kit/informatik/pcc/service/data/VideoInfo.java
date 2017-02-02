package edu.kit.informatik.pcc.service.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * @author David Laubenstein, Fabian Wenzel
 *         Created by David Laubenstein on 01/18/2017
 */
public class VideoInfo {
    // JSON keys
    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_ID = "id";

    // attributes
    private int videoId;
    private String videoName;

    /**
     * constructor
     *
     * @param videoId   the id of the video
     * @param videoName the name of the video
     */
    public VideoInfo(int videoId, String videoName) {
        this.videoId = videoId;
        this.videoName = videoName;
    }

    // methods

    /**
     * return VideoInfo-Object as json String
     *
     * @return json String of videoInfo object
     */
    public String getAsJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_KEY_NAME, this.videoName);
            json.put(JSON_KEY_ID, this.videoId);
        } catch (JSONException e) {
            Logger.getGlobal().warning("Error while creating video info json");
        }
        return json.toString();
    }

    // getter/setter
    public String getName() {
        return videoName;
    }

    public int getVideoId() {
        return videoId;
    }
}
