package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.VideoInfo;

import java.util.ArrayList;

/**
 * @author David Laubenstein, Fabian Wenzel
 *         Created by David Laubenstein on 01/18/2017
 */
public class AccountManager {
    //TODO: JAVADOC
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
    public String changeAccount(String newAccountData) {
        Account newAccount = new Account(newAccountData);
        String status = "NOTHING CHANGED";
        if (!newAccount.getMail().equals(account.getMail())) {
            status = setMail(newAccount.getMail());
            if (!(status.equals(SUCCESS))) {
                return status;
            }
        }
        if (!newAccount.getPasswordHash().equals(account.getPasswordHash())) {
            return setPassword(newAccount.getPasswordHash());
        }
        return status;
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
            for (VideoInfo videoInfo : videoInfoList) {
                String status = videoManager.videoDelete(videoInfo.getVideoId());
                if (status.equals(FAILURE)) {
                    //TODO: handle failure of videoDelete?
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

    //helper methods
    private String setMail(String newMail) {
        return databaseManager.setMail(newMail) ? SUCCESS : FAILURE;
    }

    private String setPassword(String passwordHash) {
        return databaseManager.setPassword(passwordHash) ? SUCCESS : FAILURE;
    }
}
