package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.VideoInfo;

import java.util.ArrayList;

/**
 * @author David Laubenstein, Fabian Wenzel
 * Created by David Laubenstein on 01/18/2017
 */
public class AccountManager {
	// attributes
	private final String SUCCESS = "SUCCESS";
	private final String FAILURE = "FAILURE";
	private Account account;
	private DatabaseManager databaseManager;

	// constructor
	public AccountManager(Account account) {
		this.account = account;
		databaseManager = new DatabaseManager(account);
	}

	// methods
	public String setMail(String newMail) {
		return databaseManager.setMail(newMail) ? SUCCESS : FAILURE;
	}

	public String setPassword(String passwordHash) {
		return databaseManager.setMail(passwordHash) ? SUCCESS : FAILURE;
	}

	public int getAccountId() {
	    return databaseManager.getAccountId();
	}

	public String registerAccount(String uuid) {
		return databaseManager.register(uuid) ? SUCCESS : FAILURE;
	}

	public String deleteAccount(VideoManager videoManager) {
		ArrayList<VideoInfo> videoInfoList = videoManager.getVideoInfoList();
		if (videoInfoList != null) {
			for (VideoInfo videoInfo: videoInfoList){
				String status = videoManager.videoDelete(videoInfo.getVideoId());
				if (status.equals(FAILURE)){
					//TODO: handle failure of videoDelete? how?
				}
			}
		}
		return databaseManager.deleteAccount() ? SUCCESS : FAILURE;
	}

	public boolean authenticate() {
		return databaseManager.authenticate();
	}

	public String verifyAccount(String uuid) {
		return databaseManager.verifyAccount(uuid) ? SUCCESS : FAILURE;
	}
	public boolean isVerified() {
		return databaseManager.isVerified();
	}
}
