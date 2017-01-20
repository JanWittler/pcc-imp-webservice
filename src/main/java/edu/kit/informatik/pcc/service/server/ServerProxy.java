package edu.kit.informatik.pcc.service.server;

import com.sun.org.apache.regexp.internal.RE;
import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import edu.kit.informatik.pcc.service.manager.AccountManager;
import edu.kit.informatik.pcc.service.manager.VideoManager;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Fabian Wenzel
 */
@Path("webservice")
public class ServerProxy {
	// attributes
	private Account account;
	private VideoManager videoManager;
	private AccountManager accountManager;

	// methods
    @POST
    @Path("videoUpload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String videoUpload (@FormDataParam("video") InputStream video, @FormDataParam("metadata") InputStream metadata, @FormDataParam("key") String encryptedSymmetricKey, @FormDataParam("data") String accountData, @FormDataParam("videoName") String videoName, @Suspended AsyncResponse response) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("SUCCESS")) {
			String uploadResult = videoManager.upload(video, metadata, encryptedSymmetricKey, videoName, response);
			return uploadResult;
		}
    	return "WRONG ACCOUNT";
	}

	@POST
    @Path("videoDownload")
	public Response videoDownload (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		setUpForRequest(accountData);
		String accountStatus = setUpForRequest(accountData);
		Response.ResponseBuilder response = null;
		if (accountStatus.equals("SUCCESS")) {
			File video = videoManager.download(videoId);
			if (video == null) {
				return response.status(200).entity("VideoNotFound").build();
			}
			response = Response.ok((Object) video);
			response.header("Content-Disposition", "attachment; filename=\""+video.getName()+".mp4\"");
			return response.build();
		}
		return response.status(200).entity("WRONG ACCOUNT").build();
	}

    @POST
    @Path("videoInfo")
	public String videoInfo (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("SUCCESS")) {
			String videoInfo = videoManager.getMetaData(videoId);
			return videoInfo;
		}
    	return "WRONG ACCOUNT";
	}

    @POST
    @Path("videoDelete")
	public String videoDelete (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("SUCCESS")) {
			String status = videoManager.videoDelete(videoId);
			return status;
		}
		return "WRONG ACCOUNT";
	}

    @POST
    @Path("getVideosByAccount")
	public String getVideosByAccount (@FormParam("data") String accountData) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("SUCCESS")) {
			ArrayList<VideoInfo> videoInfoList = videoManager.getVideoInfoList();
			//make json
			return "";
		}
		return "WRONG ACCOUNT";
	}

    @POST
    @Path("authenticate")
	public String authenticateAccount (@FormParam("data") String accountData) {
		String accountStatus = setUpForRequest(accountData);
		return accountStatus;
	}

    @POST
    @Path("createAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String createAccount (@FormParam("data") String accountData, @FormParam("uuid") String uuid) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("NO ACCOUNTID")) {
			String status = accountManager.registerAccount(uuid);
			return status;
		}
    	return "ACCOUNT EXISTS";
	}

    @POST
    @Path("changeAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String changeAccount (@FormParam("newData") String accountDataNew, @FormParam("data") String accountData) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("SUCCESS")) {
			Account newAccount = new Account(accountDataNew);
			String status = accountManager.setMail(newAccount.getEmail());
			if (!(status.equals("SUCCESS"))){
				return status;
			}
			status = accountManager.setPassword(newAccount.getPasswordHash());
			return status;
		}
		return "WRONG ACCOUNT";
	}

    @POST
    @Path("deleteAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String deleteAccount (@FormParam("data") String accountData) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("SUCCESS")) {
			ArrayList<VideoInfo> videoInfoList = videoManager.getVideoInfoList();
			for (VideoInfo videoInfo: videoInfoList){
				String status = videoManager.videoDelete(videoInfo.getVideoId());
				if (status.equals("FAILURE")){
					//exception handling
				}
			}
			String result = accountManager.deleteAccount();
			return result;
		}
		return "WRONG ACCOUNT";
	}

    @POST
    @Path("verfiyAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String verifyAccount (@FormParam("data") String accountData, @FormParam("uuid") String uuid) {
		String accountStatus = setUpForRequest(accountData);
		if (accountStatus.equals("SUCCESS")) {
			String status = accountManager.verifyAccount(uuid);
			return status;
		}
		return "WRONG ACCOUNT";
	}

	private String setUpForRequest(String accountData) {
    	//setup account and manager
		Account account = new Account(accountData);
		videoManager = new VideoManager(account);
		accountManager = new AccountManager(account);

		//authentication process
		int accountId = account.getId();
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
    	return "SUCCESS";
	}


}
