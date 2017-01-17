package edu.kit.informatik.pcc.service.server;

import com.sun.org.apache.regexp.internal.RE;
import edu.kit.informatik.pcc.service.data.Account;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("webservice")
public class ServerProxy {
	// attributes
	private Account account;
	// constructor
	// methods

    @POST
    @Path("videoUpload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
	public String videoUpload (InputStream video, InputStream metadata, String encryptedSymmetricKey, String accountData, AsyncResponse response) {
		return null;
	}

	@POST
    @Path("videoDownload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response videoDownload (int videoId, String accountData) {
		return null;
	}
	public String videoInfo (int videoId, String accountData) {
		return null;
	}
	public String videoDelete (int videoId, String accountData) {
		return null;
	}
	public String getVideosByAccount (String accountData) {
		return null;
	}
	public String authenticateAccount (String accountData) {
		return null;
	}
	public String createAccount (String accountData, int uuid) {
		return null;
	}
	public String changeAccount (String accountDataNew, String accountData) {
		return null;
	}
	public String deleteAccount (String accountData) {
		return null;
	}
	public String verifyAccount (String accountData, int uuid) {
		return null;
	}
	private String setUpForRequest(String accountData) {
		return null;
	}


}
