package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.VideoInfo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import javax.ws.rs.container.AsyncResponse;

public class VideoManager {
	// attributes
	private Account account;
	// constructors
	public void VideoManager(Account account) {
	    return;
	}
	// methods
	public ArrayList<VideoInfo> getVideoInfoList() {
		return null;
	}
	public String upload(InputStream video, InputStream metadata, String encryptedSymmetricKey, AsyncResponse response) {
		return "test";
	}
	public File download(int videoId) {
		return null;
	}
	public String videoDelete(int videoId) {
		return "";
	}
	public String getMetaData(int videoId) {
		return "";
	}
	// getter/setter
}
