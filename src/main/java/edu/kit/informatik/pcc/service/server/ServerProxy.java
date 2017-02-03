package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.manager.AccountManager;
import edu.kit.informatik.pcc.service.manager.VideoManager;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * @author Fabian Wenzel
 */
@Path("webservice")
public class ServerProxy {
    //TODO: EXPLAIN METHODS IN JAVADOC
    // attributes
    private final String WRONG_ACCOUNT    = "WRONG ACCOUNT";
    private final String SUCCESS          = "SUCCESS";
    private final String FAILURE          = "FAILURE";
    private final String NOT_EXISTING     = "NOT EXISTING";
    private final String NOT_VERIFIED     = "NOT VERIFIED";
    private final String WRONG_PASSWORD   = "WRONG PASSWORD";
    private final String ALREADY_VERIFIED = "ALREADY VERIFIED";
    private final String ACCOUNT_EXISTS   = "ACCOUNT EXISTS";

    //param strings
    private final String VIDEOID = "videoId";
    private final String ACCOUNT = "account";
    private final String NEWACCOUNT = "newAccount";
    private final String VIDEO = "video";
    private final String METADATA = "metadata";
    private final String KEY = "key";
    private final String UUID = "uuid";


    private AccountManager accountManager;
    private VideoManager videoManager;

    // methods
    /**
     * @param video                 uploaded video file recorded by android app
     * @param metadata              metadata of uploaded video as file
     * @param encryptedSymmetricKey key to decode symmetric encoded parts in service (metadata/video)
     * @param accountData           string as json with account specifications (mail and password)
     * @param fileDetail            extracting the file details to get name of file
     * @param response              mark response as async response
     * @return                      message in form of string is send back to the app whether uploading task has
     *                              started successfully or corresponding failure message
     */
    @POST
    @Path("videoUpload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String videoUpload(@FormDataParam(VIDEO) InputStream video, @FormDataParam(METADATA) InputStream metadata, @FormDataParam(KEY) InputStream encryptedSymmetricKey, @FormDataParam(ACCOUNT) String accountData, @FormDataParam(VIDEO) FormDataContentDisposition fileDetail, @Suspended AsyncResponse response) {
        if (video == null || metadata == null || encryptedSymmetricKey == null || accountData == null || fileDetail == null) {
            return FAILURE;
        }
        String videoName = fileDetail.getName();
        Logger.getGlobal().info("Upload Request");
        String accountStatus = setUpForRequest(accountData);
        return (accountStatus.equals(SUCCESS)) ?
                videoManager.upload(video, metadata, encryptedSymmetricKey, videoName, response) : WRONG_ACCOUNT;
    }

    /**
     * @param videoId     integer of specific video to download from webinterface
     * @param accountData string as json with account specifications (mail and password)
     * @return returning video as inputstream by success or corresponding failure messagech
     */
    @POST
    @Path("videoDownload")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response videoDownload(@FormParam(VIDEOID) int videoId, @FormParam(ACCOUNT) String accountData) {
        Response.ResponseBuilder response = null;
        if (accountData == null || videoId == 0) {
            return response.status(400).build();
        }
        Logger.getGlobal().info("Download Request");
        setUpForRequest(accountData);
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            return videoManager.download(videoId);
        }
        return response.status(401).build();
    }


    /**
     * <p>
     *
     * </p>
     * @param videoId     integer of videoId of associated metadata
     * @param accountData string as json with account specifications (mail and password)
     * @return            string as json with video specific information (name and id) by success or corresponding failure message
     */
    @POST
    @Path("videoInfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String videoInfo(@FormParam(VIDEOID) int videoId, @FormParam(ACCOUNT) String accountData) {
        if (accountData == null || videoId == 0) {
            return FAILURE;
        }
        Logger.getGlobal().info("Get Metadata Request");
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            return videoManager.getMetaData(videoId);
        }
        return WRONG_ACCOUNT;
    }

    /**
     * @param videoId     integer of videoId to delete from service because user deleted file in webinterface
     * @param accountData string as json with account specifications (mail and password)
     * @return            message as string whether deletion was successfully or or corresponding failure message
     */
    @POST
    @Path("videoDelete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String videoDelete(@FormParam(VIDEOID) int videoId, @FormParam(ACCOUNT) String accountData) {
        if (accountData == null || videoId == 0) {
            return FAILURE;
        }
        Logger.getGlobal().info("Video Deletion Request");
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            return videoManager.videoDelete(videoId);
        }
        return WRONG_ACCOUNT;
    }

    /**
     * @param accountData string as json with account specifications (mail and password)
     * @return            all videoInfo of user (by accountData) by success or variant failure message
     */
    @POST
    @Path("getVideosByAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String getVideosByAccount(@FormParam(ACCOUNT) String accountData) {
        if (accountData == null) {
            return FAILURE;
        }
        Logger.getGlobal().info("GetVideosByAccount Request");
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
           return videoManager.getVideoInfoList();
        }
        return WRONG_ACCOUNT;
    }

    /**
     * @param accountData string as json with account specifications (mail and password)
     * @return            account status in service (specified by setUpForRequest)
     */
    @POST
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String authenticateAccount(@FormParam(ACCOUNT) String accountData) {
        if (accountData == null) {
            return FAILURE;
        }
        Logger.getGlobal().info("Authenticate Request");
        return setUpForRequest(accountData);
    }

    /**
     * @param accountData string as json with account specifications (mail and password)
     * @param uuid        uuid of account to set in database to fulfill verification later
     * @return            message as string whether creation was successfully or not
     */
    @POST
    @Path("createAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createAccount(@FormParam(ACCOUNT) String accountData, @FormParam(UUID) String uuid) {
        if (accountData == null || uuid == null) {
            return FAILURE;
        }
        Logger.getGlobal().info("Account Creation Request");
        String accountStatus = setUpForRequest(accountData);
        return (accountStatus.equals(NOT_EXISTING)) ?
                accountManager.registerAccount(uuid) : ACCOUNT_EXISTS;
    }

    /**
     * @param newAccountData string as json with  new set account specifications (mail and password)
     * @param accountData    string as json with account specifications (mail and password)
     * @return               message as string whether changing was successfully or not
     */
    @POST
    @Path("changeAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String changeAccount(@FormParam(NEWACCOUNT) String newAccountData, @FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("AccountData Changing Request");
        if (accountData == null || newAccountData == null) {
            return FAILURE;
        }
        return (setUpForRequest(accountData).equals(SUCCESS)) ?
                accountManager.changeAccount(newAccountData) : WRONG_ACCOUNT;
    }

    /**
     * @param accountData string as json with account specifications (mail and password)
     * @return            message as string whether deletion was successfully or not
     */
    @POST
    @Path("deleteAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String deleteAccount(@FormParam(ACCOUNT) String accountData) {
        if (accountData == null) {
            return FAILURE;
        }
        Logger.getGlobal().info("Account Deletion Request");
        String accountStatus = setUpForRequest(accountData);
        return (accountStatus.equals(SUCCESS) || accountStatus.equals(NOT_VERIFIED)) ?
                accountManager.deleteAccount(videoManager) : WRONG_ACCOUNT;
    }

    /**
     * @param accountData string as json with account specifications (mail and password)
     * @param uuid        uuid from user to compare with corresponding uuid in database
     * @return            message as string whether verification was successfully or not (or already verified)
     */
    @POST
    @Path("verifyAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String verifyAccount(@FormParam(ACCOUNT) String accountData, @FormParam(UUID) String uuid) {
        if (accountData == null || uuid == null) {
            return FAILURE;
        }
        Logger.getGlobal().info("Account Verification Request");
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            return ALREADY_VERIFIED;
        }
        return (accountStatus.equals(NOT_VERIFIED)) ?
                accountManager.verifyAccount(uuid) : WRONG_ACCOUNT;
    }

    /**
     * @param accountData string as json with account specifications (mail and password)
     * @return            account specific status (3 different variants) as string
     */
    private String setUpForRequest(String accountData) {
        //setup account and manager
        Account account = new Account(accountData);
        videoManager    = new VideoManager(account);
        accountManager  = new AccountManager(account);

        //TODO: do things with "?"
        //authentication process
        int accountId = accountManager.getAccountId();
        if (accountId < 1) {
            return NOT_EXISTING;
        }

        account.setId(accountId);

        if (!accountManager.authenticate()) {
            return WRONG_PASSWORD;
        }

        return (!accountManager.isVerified()) ? NOT_VERIFIED : SUCCESS;
    }
}
