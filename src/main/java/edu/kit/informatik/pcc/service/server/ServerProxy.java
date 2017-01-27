package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import edu.kit.informatik.pcc.service.manager.AccountManager;
import edu.kit.informatik.pcc.service.manager.VideoManager;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Fabian Wenzel
 */
@Path("webservice")
public class ServerProxy {

	// attributes
	private VideoManager videoManager;
	private AccountManager accountManager;
	private final String WRONGACCOUNT = "WRONG ACCOUNT";
	private final String SUCCESS = "SUCCESS";

	// methods
    @POST
    @Path("videoUpload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String videoUpload (@FormDataParam("video") InputStream video, @FormDataParam("metadata") InputStream metadata, @FormDataParam("key") InputStream encryptedSymmetricKey, @FormDataParam("data") String accountData, @FormDataParam("videoName") String videoName, @Suspended AsyncResponse response) {
		Logger.getGlobal().info("Upload Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return videoManager.upload(video, metadata, encryptedSymmetricKey, videoName, response);
		}
    	return WRONGACCOUNT;
	}

	@POST
    @Path("videoDownload")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response videoDownload (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		Logger.getGlobal().info("Download Request");
		setUpForRequest(accountData);
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus == null) {
			//TODO: Problem on accountStatus needs to be checked? Not sure i think not :)
			return null;
		}
		Response.ResponseBuilder response = null;
		if (accountStatus.equals(SUCCESS)) {
			File video = videoManager.download(videoId);
			if (video == null) {
				try {
					return response.status(200).entity("VideoNotFound").build();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				response = Response.ok(video);
				response.header("Content-Disposition", "attachment; filename=\""+video.getName()+".mp4\"");
				return response.build();
			}
		}
		return response.status(200).entity(WRONGACCOUNT).build();
	}

    @POST
    @Path("videoInfo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String videoInfo (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		Logger.getGlobal().info("Get Metadata Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return videoManager.getMetaData(videoId);
		}
    	return WRONGACCOUNT;
	}

    @POST
    @Path("videoDelete")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String videoDelete (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		Logger.getGlobal().info("Video Deletion Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return videoManager.videoDelete(videoId);
		}
		return WRONGACCOUNT;
	}

    @POST
    @Path("getVideosByAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getVideosByAccount (@FormParam("data") String accountData) {
		Logger.getGlobal().info("GetVideosByAccount Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			//convert VideoInfos to JSONArray
			ArrayList<VideoInfo> videoInfoList = videoManager.getVideoInfoList();
			JSONArray videoInfoArray = new JSONArray();
			for (int i = 0; i < videoInfoList.size(); i++) {
				videoInfoArray.put(i, videoInfoList.get(i).getAsJson());
			}
			return videoInfoArray.toString();
		}
		return WRONGACCOUNT;
	}

    @POST
    @Path("authenticate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String authenticateAccount (@FormParam("data") String accountData) {
		Logger.getGlobal().info("Authenticate Request");
		return setUpForRequest(accountData);
	}

    @POST
    @Path("createAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String createAccount (@FormParam("data") String accountData, @FormParam("uuid") String uuid) {
		Logger.getGlobal().info("Account Creation Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("NO ACCOUNTID")) {
			return accountManager.registerAccount(uuid);
		}
    	return "ACCOUNT EXISTS";
	}

    @POST
    @Path("changeAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String changeAccount (@FormParam("newData") String accountDataNew, @FormParam("data") String accountData) {
		Logger.getGlobal().info("AccountData Changing Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			Account newAccount = new Account(accountDataNew);
			String status = accountManager.setMail(newAccount.getMail());
			if (!(status.equals(SUCCESS))){
				return status;
			}
			status = accountManager.setPassword(newAccount.getPasswordHash());
			return status;
		}
		return WRONGACCOUNT;
	}

    @POST
    @Path("deleteAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String deleteAccount (@FormParam("data") String accountData) {
		Logger.getGlobal().info("Account Deletion Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return accountManager.deleteAccount(videoManager);
		}
		return WRONGACCOUNT;
	}

    @POST
    @Path("verfiyAccount")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String verifyAccount (@FormParam("data") String accountData, @FormParam("uuid") String uuid) {
		Logger.getGlobal().info("Account Verification Request");
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals(SUCCESS)) {
			return accountManager.verifyAccount(uuid);
		}
		return WRONGACCOUNT;
	}

	private String setUpForRequest(String accountData) {
    	//setup account and manager
		Account account = new Account(accountData);
		videoManager = new VideoManager(account);
		accountManager = new AccountManager(account);

		//authentication process
		int accountId = accountManager.getAccountId();
		if (accountId == -1) {
			return "NO ACCOUNTID";
		}
		account.setId(accountId);
		boolean authenticate = accountManager.authenticate();
		if (!authenticate) {
			return "WRONG PASSWORD";
		}
		boolean verfied = accountManager.isVerified();
		if (!verfied) {
			return "NOT VERIFIED";
		}
    	return SUCCESS;
	}


}
