package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.VideoInfo;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The AccountManager processes requests concerning accounts and account information.
 * It takes requests from the ServerProxy handles them and forwards them to the
 * Model devices
 *
 * @author David Laubenstein, Fabian Wenzel
 */
public class AccountManager {

    // status message constants
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
     * Manager for database access.
     */
    private DatabaseManager databaseManager;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Creates a new account manager for the given account.
     *
     * @param account Active user account.
     */
    public AccountManager(Account account) {
        this.account = account;
        databaseManager = new DatabaseManager(account);
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * Changes the active account to a new one.
     *
     * @param newAccountData JSON String of the account to change to.
     * @return Returns status string of the request.
     */
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
     * Gets the unique account id of an account.
     *
     * @return Returns the unique account id.
     */
    public int getAccountId() {
        return databaseManager.getAccountId();
    }

    /**
     * Tries to register an account. The UUID gets stored as a value used to
     * enable account verification later.
     *
     * @param uuid Unique id used to verify registration.
     * @return Returns status of the account creation.
     */
    public String registerAccount(String uuid) {
        return databaseManager.register(uuid) ? SUCCESS : FAILURE;
    }

    /**
     * Deletes an account from the server. Therefore all videos and metadata registered to the account
     * get deleted aswell.
     *
     * @param videoManager VideoManager used to delete videos and metadata of an account
     * @return Returns status of the account deletion.
     */
    public String deleteAccount(VideoManager videoManager) {
        ArrayList<VideoInfo> videoInfoList = databaseManager.getVideoInfoList();
        if (videoInfoList != null) {
            for (VideoInfo videoInfo : videoInfoList) {
                String status = videoManager.videoDelete(videoInfo.getVideoId());
                if (status.equals(FAILURE)) {
                    Logger.getGlobal().warning("An error occurred deleting videos!");
                    //TODO: handle failure of videoDelete?
                }
            }
        }
        return databaseManager.deleteAccount() ? SUCCESS : FAILURE;
    }

    /**
     * Tries to authenticate an account via its main and password.
     *
     * @return Returns if authentication worked successfully
     */
    public boolean authenticate() {
        return databaseManager.authenticate();
    }

    /**
     * Verifies an account by comparing the uuid to the stored one.
     *
     * @param uuid string uuid to compare with uuid in database
     * @return string if deletion successfully accomplished
     */
    public String verifyAccount(String uuid) {
        return databaseManager.verifyAccount(uuid) ? SUCCESS : FAILURE;
    }

    /**
     * Checks if the active account is verified or not.
     *
     * @return boolean if account is verified
     */
    public boolean isVerified() {
        return databaseManager.isVerified();
    }

    /* #############################################################################################
     *                                  helper methods
     * ###########################################################################################*/

    /**
     * Sets the mail of an account.
     *
     * @param newMail New mail to be set.
     * @return Returns string with a status message.
     */
    private String setMail(String newMail) {
        return databaseManager.setMail(newMail) ? SUCCESS : FAILURE;
    }

    /**
     * Sets the password of an account.
     *
     * @param passwordHash New password to be set.
     * @return Returns status message of the request.
     */
    private String setPassword(String passwordHash) {
        return databaseManager.setPassword(passwordHash) ? SUCCESS : FAILURE;
    }
}
