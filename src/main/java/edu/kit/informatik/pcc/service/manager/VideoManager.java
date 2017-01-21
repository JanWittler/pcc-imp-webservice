package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.*;
import edu.kit.informatik.pcc.service.videoprocessing.VideoProcessingManager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.ws.rs.container.AsyncResponse;

/**
 * @author Fabian Wenzel, David Laubenstein
 * Created by David Laubenstein on 01/18/2017
 */
public class VideoManager {
	// attributes
	private Account account;
	private DatabaseManager databaseManager;

	// constructor
	public VideoManager(Account account) {
		this.account = account;
		databaseManager = new DatabaseManager(account);
	}
	// methods
	public ArrayList<VideoInfo> getVideoInfoList() {
		//TODO: more stuff?
		return databaseManager.getVideoInfoList();
	}
	public String upload(InputStream video, InputStream metadata, InputStream encryptedSymmetricKey, String videoName, AsyncResponse response) {
		VideoProcessingManager videoProcessingManager = VideoProcessingManager.getInstance();
		videoProcessingManager.addTask(video, metadata, encryptedSymmetricKey, account, videoName, response);
		//TODO: return more strings? why addTask returns void?
		return "SUCCESS";
	}
	public File download(int videoId) {
		VideoInfo videoInfo = databaseManager.getVideoInfo(videoId);
		if (videoInfo == null) {
			return null;
		}
		String videoName = videoInfo.getName();
		return new File(LocationConfig.ANONYM_VID_DIR + "/" + videoName);
	}
	public String videoDelete(int videoId) {
		VideoInfo videoInfo = databaseManager.getVideoInfo(videoId);
		if (videoInfo == null) {
			return "FAILURE";
		}
		Metadata metadata = databaseManager.getMetaData(videoId);
		if (metadata == null) {
			return "FAILURE";
		}
		String videoName = videoInfo.getName();
		String metaName = metadata.getMetaName();
		File videoFile = null;
		try {
			videoFile = new File(LocationConfig.ANONYM_VID_DIR + "/" + videoName);
		} catch (NullPointerException e) {
			//TODO: Logger!
			e.printStackTrace();
		}
		videoFile.delete();
		File metaFile = null;
		try {
			metaFile = new File(LocationConfig.META_DIR + "/" + metaName);
		} catch (NullPointerException e) {
			//TODO: Logger!
			e.printStackTrace();
		}
		metaFile.delete();
		boolean status = databaseManager.deleteVideoAndMeta(videoId);
		if (status == false) {
			//TODO: what to restore here?
			return "FAILURE";
		}
		return "SUCCESS";
	}
	public String getMetaData(int videoId) {
		Metadata metadata = databaseManager.getMetaData(videoId);
		if (metadata == null) {
			return "FAILURE";
		}
		return metadata.getAsJson();
	}
}
