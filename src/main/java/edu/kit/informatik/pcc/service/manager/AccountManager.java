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
	/**
	 * @param account account for accountmanager
	 */
	public AccountManager(Account account) {
		this.account = account;
		databaseManager = new DatabaseManager(account);
	}

	// methods
	/**
	 * @param newMail string of new mail to set
	 * @return string if mail set successfully
	 */
	public String setMail(String newMail) {
		return databaseManager.setMail(newMail) ? SUCCESS : FAILURE;
	}

	/**
	 * @param passwordHash string of hashed password to set
	 * @return string if password set successfully
	 */
	public String setPassword(String passwordHash) {
		return databaseManager.setMail(passwordHash) ? SUCCESS : FAILURE;
	}

	/**
	 * @return integer of accountid
	 */
	public int getAccountId() {
	    return databaseManager.getAccountId();
	}

	/**
	 * @param uuid string uuid to set to account
	 * @return string if account creation successfully
	 */
	public String registerAccount(String uuid) {
		return databaseManager.register(uuid) ? SUCCESS : FAILURE;
	}

	/**
	 * @param videoManager videomanager instance to delete videos of account
	 * @return string if deletion worked successfully
	 */
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

	/**
	 * @return boolean if authentication worked successfully
	 */
	public boolean authenticate() {
		return databaseManager.authenticate();
	}

	/**
	 * @param uuid string uuid to compare with uuid in database
	 * @return string if deletion successfully accomplished
	 */
	public String verifyAccount(String uuid) {
		return databaseManager.verifyAccount(uuid) ? SUCCESS : FAILURE;
	}

	/**
	 * @return boolean if account is verified
	 */
	public boolean isVerified() {
		return databaseManager.isVerified();
	}
}
