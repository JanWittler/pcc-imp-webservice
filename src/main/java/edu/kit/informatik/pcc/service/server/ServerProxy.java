package edu.kit.informatik.pcc.service.server;

import com.sun.org.apache.regexp.internal.RE;
import edu.kit.informatik.pcc.service.data.Account;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
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
	public String videoUpload (@FormDataParam("video") InputStream video, @FormDataParam("metadata") InputStream metadata, @FormDataParam("key") String encryptedSymmetricKey, @FormDataParam("data") String accountData, @Suspended AsyncResponse response) {
		return null;
	}

	@POST
    @Path("videoDownload")
	public Response videoDownload (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		return null;
	}

    @POST
    @Path("videoInfo")
	public String videoInfo (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		return null;
	}

    @POST
    @Path("videoDelete")
	public String videoDelete (@FormParam("id") int videoId, @FormParam("data") String accountData) {
		return null;
	}

    @POST
    @Path("getVideosByAccount")
	public String getVideosByAccount (@FormParam("data") String accountData) {
		return null;
	}

    @POST
    @Path("authenticate")
	public String authenticateAccount (@FormParam("data") String accountData) {
		return null;
	}

    @POST
    @Path("createAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String createAccount (@FormParam("data") String accountData, @FormParam("uuid") int uuid) {
		return null;
	}

    @POST
    @Path("changeAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String changeAccount (@FormParam("newData") String accountDataNew, @FormParam("data") String accountData) {
		return null;
	}

    @POST
    @Path("deleteAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String deleteAccount (@FormParam("data") String accountData) {
		return null;
	}

    @POST
    @Path("verfiyAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String verifyAccount (@FormParam("data") String accountData, @FormParam("uuid") int uuid) {
		return null;
	}

	private String setUpForRequest(String accountData) {
		return null;
	}


}
