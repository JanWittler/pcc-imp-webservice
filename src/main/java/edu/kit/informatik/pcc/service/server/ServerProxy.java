package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import edu.kit.informatik.pcc.service.manager.AccountManager;
import edu.kit.informatik.pcc.service.manager.VideoManager;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Fabian Wenzel
 */
@Path("webservice")
public class ServerProxy {
	//TODO: JAVADOC
	// attributes
	private VideoManager videoManager;
	private AccountManager accountManager;
	private final String WRONGACCOUNT = "WRONG ACCOUNT";
	private final String SUCCESS = "SUCCESS";
	private final String FAILURE = "FAILURE";

	// methods
	/**
	 * @param video inputstream of videofile to upload
	 * @param metadata inputstream of metadatafile to upload
	 * @param encryptedSymmetricKey inputstream of keyfile to upload
	 * @param accountData json string of accountdata
	 * @param videoName string of videoname
	 * @param response create async response
	 * @return string if task started successfully
	 */
	@POST
	@Path("videoUpload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String videoUpload (@FormDataParam("video") InputStream video, @FormDataParam("metadata") InputStream metadata, @FormDataParam("key") InputStream encryptedSymmetricKey, @FormDataParam("data") String accountData, @FormDataParam("videoName") String videoName, @Suspended AsyncResponse response) {
		if(video == null || metadata == null || encryptedSymmetricKey == null || accountData == null || videoName == null) {
			return FAILURE;
		}
		Logger.getGlobal().info("Upload Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return videoManager.upload(video, metadata, encryptedSymmetricKey, videoName, response);
		}
		return WRONGACCOUNT;
	}

	/**
	 * @param videoId integer of videoid to download
	 * @param accountData json string of accountdata
	 * @return response with video or failure message
	 */
	@POST
    @Path("videoDownload")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response videoDownload (@FormParam("videoId") int videoId, @FormParam("data") String accountData) {
		Response.ResponseBuilder response = null;
		if (accountData == null || videoId == 0) {
			return response.status(400).build();
		}
		Logger.getGlobal().info("Download Request");
		setUpForRequest(accountData);
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus == null) {
			return null;
		}
		if (accountStatus.equals(SUCCESS)) {
			File video = videoManager.download(videoId);
			if (video == null) {
				try {
					return response.status(404).build();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				InputStream inputStream = null;
				try {
					inputStream = new FileInputStream(video.getPath());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				response = Response.ok();
				return response.status(200).entity(inputStream).build();
			}
		}
		return response.status(401).build();
	}


	/**
	 * @param videoId integer of videoid of associated metadata
	 * @param accountData json string of accountdata
	 * @return json string with metadata
	 */
	@POST
    @Path("videoInfo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String videoInfo (@FormParam("videoId") int videoId, @FormParam("data") String accountData) {
		if (accountData == null || videoId == 0) {
			return FAILURE;
		}
		Logger.getGlobal().info("Get Metadata Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return videoManager.getMetaData(videoId);
		}
    	return WRONGACCOUNT;
	}

	/**
	 * @param videoId integer of videoid
	 * @param accountData json string of accountdata
	 * @return string if deletion successfully accomplished
	 */
	@POST
    @Path("videoDelete")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String videoDelete (@FormParam("videoId") int videoId, @FormParam("data") String accountData) {
		if (accountData == null || videoId == 0) {
			return FAILURE;
		}
		Logger.getGlobal().info("Video Deletion Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return videoManager.videoDelete(videoId);
		}
		return WRONGACCOUNT;
	}

	/**
	 * @param accountData json string of accountdata
	 * @return json array with all videoinfos of account
	 */
	@POST
    @Path("getVideosByAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getVideosByAccount (@FormParam("data") String accountData) {
		if(accountData == null) {
			return "FAILRUE";
		}
		Logger.getGlobal().info("GetVideosByAccount Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			//convert VideoInfos to JSONArray
			//TODO:FAILURE MESSAGE, CHECK IF videoInfoList == null ?
			//TODO:PRODUCE JSON IN VIDEOMANAGER
			ArrayList<VideoInfo> videoInfoList = videoManager.getVideoInfoList();
			JSONArray videoInfoArray = new JSONArray();
			for (int i = 0; i < videoInfoList.size(); i++) {
				String json = videoInfoList.get(i).getAsJson();
				JSONObject jsonObject = new JSONObject(json);
				videoInfoArray.put(i, jsonObject);
			}
			return videoInfoArray.toString();
		}
		return WRONGACCOUNT;
	}

	/**
	 * @param accountData json string of accountdata
	 * @return string if authentication successfully accomplished
	 */
	@POST
    @Path("authenticate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String authenticateAccount (@FormParam("data") String accountData) {
		if (accountData == null) {
			return FAILURE;
		}
		Logger.getGlobal().info("Authenticate Request");
		return setUpForRequest(accountData);
	}

	/**
	 * @param accountData json string of accountdata
	 * @param uuid strinf of uuid to set in database
	 * @return string if account creation successfully accomplished
	 */
	@POST
    @Path("createAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String createAccount (@FormParam("data") String accountData, @FormParam("uuid") String uuid) {
		if (accountData == null || uuid == null) {
			return FAILURE;
		}
		Logger.getGlobal().info("Account Creation Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("NOT EXISTING")) {
			return accountManager.registerAccount(uuid);
		}
    	return "ACCOUNT EXISTS";
	}

	/**
	 * @param newAccountData json string of new accountdata
	 * @param accountData json string of accountdata
	 * @return string if accountdata change successfully accomplished
	 */
	@POST
    @Path("changeAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String changeAccount (@FormParam("newData") String newAccountData, @FormParam("data") String accountData) {
		Logger.getGlobal().info("AccountData Changing Request");
		if (accountData == null || newAccountData == null) {
			return FAILURE;
		}
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return accountManager.changeAccount(newAccountData);
		}
		return WRONGACCOUNT;
	}

	/**
	 * @param accountData json string of accountdata
	 * @return string if account deletion successfully accomplished
	 */
	@POST
    @Path("deleteAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String deleteAccount (@FormParam("data") String accountData) {
		if (accountData == null) {
			return FAILURE;
		}
		//TODO: STILL DELETE IF NOT VERIFIED
		Logger.getGlobal().info("Account Deletion Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return accountManager.deleteAccount(videoManager);
		}
		return WRONGACCOUNT;
	}

	/**
	 * @param accountData json string of accountdata
	 * @param uuid string of uuid to compare with database uuid
	 * @return string if verification successfully accomplished
	 */
	@POST
    @Path("verifyAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String verifyAccount (@FormParam("data") String accountData, @FormParam("uuid") String uuid) {
		if (accountData == null || uuid == null) {
			return FAILURE;
		}
		Logger.getGlobal().info("Account Verification Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("NOT VERIFIED")) {
			return accountManager.verifyAccount(uuid);
		}
		return WRONGACCOUNT;
	}

	/**
	 * @param accountData json string of accountdata
	 * @return string if authentication accomplished or error message
	 */
	private String setUpForRequest(String accountData) {
    	//setup account and manager
		Account account = new Account(accountData);
		videoManager = new VideoManager(account);
		accountManager = new AccountManager(account);

		//TODO: do things with "?"
		//authentication process
		int accountId = accountManager.getAccountId();
		if (accountId < 1) {
			return "NOT EXISTING";
		}
		account.setId(accountId);
		boolean authenticate = accountManager.authenticate();
		if (!authenticate) {
			return "WRONG PASSWORD";
		}
		boolean verified = accountManager.isVerified();
		if (!verified) {
			return "NOT VERIFIED";
		}
    	return SUCCESS;
	}


}
