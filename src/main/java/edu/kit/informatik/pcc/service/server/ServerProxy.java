package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.manager.AccountManager;
import edu.kit.informatik.pcc.service.manager.VideoManager;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * The ServerProxy is responsible to take requests from the clients and
 * forward the requests to the manager module which process the request further on.
 * The requests results are returned from the manager classes through the
 * ServerProxy back to the creator of the requests.
 *
 * @author Fabian Wenzel
 */
@Path("webservice")
public class ServerProxy {
    //TODO: ASK IF YOU NEED TO CHECK IF VIDEO_ID AND ACCOUNT HAVE A CONNECTION TO EACH OTHER, else send a FAILURE MESSAGE BACK

    /* #############################################################################################
    *                                  attributes
    * ###########################################################################################*/

    //status strings
    private final String WRONG_ACCOUNT    = "WRONG ACCOUNT";
    private final String SUCCESS          = "SUCCESS";
    private final String FAILURE          = "FAILURE";
    private final String NOT_EXISTING     = "NOT EXISTING";
    private final String NOT_VERIFIED     = "NOT VERIFIED";
    private final String WRONG_PASSWORD   = "WRONG PASSWORD";
    private final String ACCOUNT_EXISTS   = "ACCOUNT EXISTS";

    //param strings
    private final String VIDEO_ID    = "videoId";
    private final String ACCOUNT     = "account";
    private final String NEW_ACCOUNT = "newAccount";
    private final String VIDEO       = "video";
    private final String METADATA    = "metadata";
    private final String KEY         = "key";
    private final String UUID        = "uuid";

    //manager instances
    private AccountManager accountManager;
    private VideoManager videoManager;

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * This is the main function of the ServerProxy, taking a upload request
     * and forward it to to the VideoManager. All needed files and data are send with the MultiPartFeature
     * (org.glassfish.jersey.media.multipart.MultiPart and co.),
     * which give us the opportunity to send files via inputstream from the client to the service.
     * This method sends back two different responses, a synchronous(from the ServerProxy/VideoManager)
     * and a asynchronous response (from VideoProcessing module).
     *
     * @param video                 uploaded video file recorded by client
     * @param metadata              metadata of uploaded video as file
     * @param encryptedSymmetricKey key to decode symmetric encoded parts in service (metadata/video)
     * @param accountData           string as json with account specifications (mail and password)
     * @param fileDetail            extracting the file details to get name of file
     * @param response              mark response as async response
     * @return                      message in form of string is send back to the client whether uploading task has
     *                              started successfully or corresponding failure message
     */
    @POST
    @Path("videoUpload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String videoUpload(@FormDataParam(VIDEO) InputStream video, @FormDataParam(METADATA) InputStream metadata,
                              @FormDataParam(KEY) InputStream encryptedSymmetricKey, @FormDataParam(ACCOUNT) String accountData,
                              @FormDataParam(VIDEO) FormDataContentDisposition fileDetail, @Suspended AsyncResponse response) {
        Logger.getGlobal().info("Upload Request");
        if (video == null || metadata == null || encryptedSymmetricKey == null || accountData == null || fileDetail == null) {
            return FAILURE;
        }
        String videoName = FilenameUtils.getBaseName(fileDetail.getFileName());
        String accountStatus = setUpForRequest(accountData);
        return (accountStatus.equals(SUCCESS)) ?
                videoManager.upload(video, metadata, encryptedSymmetricKey, videoName, response) : WRONG_ACCOUNT;
    }

    //!All other requests use the normal javax.ws.rs.core.Form for sending data to the service!

    /**
     * This method takes video download requests from client and returns a
     * response with an entity including an inputstream of the wanted video back.
     * The correctness of the answer is given by the http status code 200.
     * Each other status code symbolizes a form of error.
     *
     * @param videoId     integer of specific video to download from client
     * @param accountData string as json with account specifications (mail and password)
     * @return            returning video as inputstream by success or corresponding failure message
     */
    //TODO: Check if http numbers are correctly set for each case!
    @POST
    @Path("videoDownload")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response videoDownload(@FormParam(VIDEO_ID) int videoId, @FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("Download Request");
        Response.ResponseBuilder response = Response.ok();
        if (accountData == null || videoId == 0) {
            return response.status(400).build();
        }
        setUpForRequest(accountData);
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            InputStream inputStream = videoManager.download(videoId);
            if (inputStream == null) {
                return response.status(400).build();
            }
            return response.status(200).entity(inputStream).build();
        }
        return response.status(401).build();
    }

    /**
     * This method takes videoInfo requests from client and returns a json string
     * of the specific video information. If an error during request occurred a
     * "FAILURE" message is send back.
     *
     * @param videoId     integer of videoId of associated metadata
     * @param accountData string as json with account specifications (mail and password)
     * @return            string as json with video specific information (name and id) by success
     *                    or corresponding failure message
     */
    @POST
    @Path("videoInfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String videoInfo(@FormParam(VIDEO_ID) int videoId, @FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("Get Metadata Request");
        if (accountData == null || videoId == 0) {
            return FAILURE;
        }
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            return videoManager.getMetaData(videoId);
        }
        return WRONG_ACCOUNT;
    }

    /**
     * This method takes videoDelete requests from client and returns
     * a string with a success or failure message back to the client.
     * Expects the account status to be SUCCESS or NOT_VERIFIED from setUpForRequest
     * to have the possibility to delete not verified accounts.
     *
     * @param videoId     integer of videoId to delete from service (files and corresponding database entry)
     * @param accountData string as json with account specifications (mail and password)
     * @return            message as string whether deletion was successfully or or corresponding failure message
     */
    @POST
    @Path("videoDelete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String videoDelete(@FormParam(VIDEO_ID) int videoId, @FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("Video Deletion Request");
        if (accountData == null || videoId == 0) {
            return FAILURE;
        }
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            return videoManager.videoDelete(videoId);
        }
        return WRONG_ACCOUNT;
    }

    /**
     * This method takes a getVideos request from client and returns
     * an JSONArray of all relevant video information for the given account.
     * If an error occurred an error string is send back to the client.
     *
     * @param accountData string as json with account specifications (mail and password)
     * @return            all videoInfo of user (by accountData) by success or variant failure message
     */
    @POST
    @Path("getVideos")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String getVideos(@FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("GetVideosByAccount Request");
        if (accountData == null) {
            return FAILURE;
        }
        String accountStatus = setUpForRequest(accountData);
        if (accountStatus.equals(SUCCESS)) {
            return videoManager.getVideoInfoList();
        }
        return WRONG_ACCOUNT;
    }

    /**
     * This method takes an authenticate request from the client and
     * returns the account status produced by setUpForRequest.
     *
     * @param accountData string as json with account specifications (mail and password)
     * @return            account status in service (specified by setUpForRequest)
     */
    @POST
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String authenticateAccount(@FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("Authenticate Request");
        if (accountData == null) {
            return FAILURE;
        }
        return setUpForRequest(accountData);
    }

    /**
     * This method takes a createAccount request from client and returns
     * a string with a success or failure message back to the client.
     * Expects the account status to be NOT_EXISTING from setUpForRequest
     * to ensure the uniqueness of each account.
     *
     * @param accountData string as json with account specifications (mail and password)
     * @param uuid        uuid of account to set in database to fulfill verification later
     * @return            message as string whether creation was successfully or not
     */
    @POST
    @Path("createAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createAccount(@FormParam(ACCOUNT) String accountData, @FormParam(UUID) String uuid) {
        Logger.getGlobal().info("Account Creation Request");
        if (accountData == null || uuid == null) {
            return FAILURE;
        }
        String accountStatus = setUpForRequest(accountData);
        return (accountStatus.equals(NOT_EXISTING)) ?
                accountManager.registerAccount(uuid) : ACCOUNT_EXISTS;
    }

    /**
     * This method takes a changeAccount request from client and returns
     * a string with a success or failure message back to the client.
     *
     * @param newAccountData string as json with  new set account specifications (mail and password)
     * @param accountData    string as json with account specifications (mail and password)
     * @return               message as string whether changing was successfully or not
     */
    @POST
    @Path("changeAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String changeAccount(@FormParam(NEW_ACCOUNT) String newAccountData, @FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("AccountData Changing Request");
        if (accountData == null || newAccountData == null) {
            return FAILURE;
        }
        return (setUpForRequest(accountData).equals(SUCCESS)) ?
                accountManager.changeAccount(newAccountData) : WRONG_ACCOUNT;
    }

    /**
     * This method takes a deleteAccount request from client and returns
     * a string with a success or failure message back to the client.
     *
     * @param accountData string as json with account specifications (mail and password)
     * @return            message as string whether deletion was successfully or not
     */
    @POST
    @Path("deleteAccount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String deleteAccount(@FormParam(ACCOUNT) String accountData) {
        Logger.getGlobal().info("Account Deletion Request");
        if (accountData == null) {
            return FAILURE;
        }
        String accountStatus = setUpForRequest(accountData);
        return (accountStatus.equals(SUCCESS) || accountStatus.equals(NOT_VERIFIED)) ?
                accountManager.deleteAccount(videoManager) : WRONG_ACCOUNT;
    }

    /**
     * This method takes a deleteAccount request from client and returns
     * a string with a success or failure message back to the client.
     * Expects the account status to be NOT_VERIFIED from setUpForRequest
     * because account only needs to verify once.
     *
     * @param uuid        uuid from user to compare with corresponding uuid in database
     * @return            message as string whether verification was successfully or not (or already verified)
     */
    @GET
    @Path("verifyAccount")
    @Produces(MediaType.TEXT_PLAIN)
    public String verifyAccount(@QueryParam(UUID) String uuid) {
        Logger.getGlobal().info("Account Verification Request");
        if (uuid == null) {
            return FAILURE;
        }
        AccountManager accountManager = new AccountManager(null);
        return accountManager.verifyAccount(uuid);
    }

    /**
     * Every public method in ServerProxy calls the setUpForRequest-method
     * to verify the correctness of each incoming request and setting up the needed manager classes.
     * The verification is fulfilled by comparing the send accountData
     * with the database content and giving a account status back to the calling methods.
     *
     * @param accountData string as json with account specifications (mail and password)
     * @return            account specific status (3 different variants) as string
     */
    private String setUpForRequest(String accountData) {
        //setup account and managers
        Account account = new Account(accountData);
        videoManager = new VideoManager(account);
        accountManager = new AccountManager(account);

        //authentication process
        int accountId = accountManager.getAccountId();
        if (accountId < 1)
            return NOT_EXISTING;

        account.setId(accountId);

        byte[] salt = accountManager.getSalt();
        if (salt == null || !account.hashPassword(salt))
            return FAILURE;

        if (!accountManager.authenticate())
            return WRONG_PASSWORD;

        return (!accountManager.isVerified()) ? NOT_VERIFIED : SUCCESS;
    }
}
