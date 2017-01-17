package edu.kit.informatik.pcc.service.server;

import com.sun.org.apache.regexp.internal.RE;
import edu.kit.informatik.pcc.service.data.Account;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
	public String videoUpload (@FormDataParam("") InputStream video, @FormDataParam("") InputStream metadata, @FormDataParam("") String encryptedSymmetricKey, @FormDataParam("") String accountData, @FormDataParam("") AsyncResponse response) {
		return null;
	}

	@POST
    @Path("videoDownload")
	public Response videoDownload (@FormParam("") int videoId, @FormParam("") String accountData) {
		return null;
	}

    @POST
    @Path("videoIndo")
	public String videoInfo (@FormParam("") int videoId, @FormParam("") String accountData) {
		return null;
	}

    @POST
    @Path("videoDelete")
	public String videoDelete (@FormParam("") int videoId, @FormParam("") String accountData) {
		return null;
	}

    @POST
    @Path("getVideosByAccount")
	public String getVideosByAccount (@FormParam("") String accountData) {
		return null;
	}

    @POST
    @Path("authenticate")
	public String authenticateAccount (@FormParam("") String accountData) {
		return null;
	}

    @POST
    @Path("createAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String createAccount (@FormParam("") String accountData, @FormParam("") int uuid) {
		return null;
	}

    @POST
    @Path("changeAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String changeAccount (@FormParam("") String accountDataNew, @FormParam("") String accountData) {
		return null;
	}

    @POST
    @Path("deleteAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String deleteAccount (@FormParam("") String accountData) {
		return null;
	}

    @POST
    @Path("verfiyAccount")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public String verifyAccount (@FormParam("") String accountData, @FormParam("") int uuid) {
		return null;
	}

	private String setUpForRequest(String accountData) {
		return null;
	}


}
