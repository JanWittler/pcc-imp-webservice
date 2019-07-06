package edu.kit.informatik.pcc.service.server;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("webservice/account")
public class UserServerProxy {
	//status strings
    private final static String SUCCESS          	= "SUCCESS";
    private final static String FAILURE          	= "FAILURE";
    private final static String WRONG_PASSWORD   	= "WRONG PASSWORD";
    private final static String NOT_AUTHENTICATED 	= "NOT AUTHENTICATED";

    //param strings
    private final static String TOKEN 	 	= "token";
    private final static String EMAIL    	= "mail";
    private final static String PASSWORD 	= "password";
    
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Path("create")
    public String createAccount(@FormParam(EMAIL) String email, @FormParam(PASSWORD) String password) {
        Logger.getGlobal().info("Account Creation Request");
        Boolean success = WebService.getGlobal().createAccount(email, password);
        return success ? SUCCESS : FAILURE;
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Path("login")
    public Response login(@FormParam(EMAIL) String email, @FormParam(PASSWORD) String password) {
    	Logger.getGlobal().info("Account Login Request");
    	String authenticationToken = WebService.getGlobal().login(email, password);
    	if (authenticationToken == null) {
    		return Response.status(401).entity(WRONG_PASSWORD).build();
    	}
    	else {
    		ResponseBuilder response = Response.ok();
    		response.cookie(new NewCookie("token", authenticationToken));
    		return response.build();
    	}
    }
    
    @POST
    @Path("delete")
    public String deleteAccount(@HeaderParam(TOKEN) String authenticationToken) {
        Logger.getGlobal().info("Account Deletion Request");
        Boolean success = WebService.getGlobal().deleteAccount(authenticationToken);
        return success ? SUCCESS : NOT_AUTHENTICATED;
    }
}
