package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.VideoInfo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import javax.ws.rs.container.AsyncResponse;

public class VideoManager {
	// attributes
	private Account account;
	private DatabaseManager dbms;
	// constructors
	public void VideoManager(Account account) {
	    dbms = new DatabaseManager();
	    return;
	}
	// methods
	public ArrayList<VideoInfo> getVideoInfoList() {
		dbms.getVideoInfoList();
		return null;
	}
	public String upload(InputStream video, InputStream metadata, String encryptedSymmetricKey, AsyncResponse response) {
		//TODO: write method
		return "test";
	}
	public File download(int videoId) {
		//TODO: write method
		return null;
	}
	public String videoDelete(int videoId) {
		//TODO: write method
		return "";
	}
	public String getMetaData(int videoId) {
		//TODO: write method
		return "";
	}
	// getter/setter
}
