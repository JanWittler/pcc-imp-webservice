package edu.kit.informatik.pcc.service.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;

@Path("webservice/videos")
public class VideoServerProxy {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getVideoIds(@HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
		Logger.getGlobal().info("Get Video Ids Request");
		int[] videoIds = WebService.getGlobal().getVideoIds(authenticationToken);
		JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < videoIds.length; i++) {
            jsonArray.put(videoIds[i]);
        }
        return jsonArray.toString();
	}
	
	@GET
	@Path("{id}")
	public Response getVideo(@PathParam("id") int videoId, @HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
		Logger.getGlobal().info("Get Video by Id Request");
		File video = WebService.getGlobal().getVideo(videoId, authenticationToken);
		if (video == null) {
			return Response.status(404).build();
		}
		
		InputStream inputStream;
        try {
            inputStream = new FileInputStream(video.getPath());
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("An error has occurred finding file " + video.getPath());
            return Response.status(404).build();
        }
        return Response.ok().entity(inputStream).build();
	}
	
	@GET
	@Path("metadata/{id}")
	public Response getMetadata(@PathParam("id") int videoId, @HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
		Logger.getGlobal().info("Get Metadata by Id Request");
		File metadata = WebService.getGlobal().getMetadata(videoId, authenticationToken);
		if (metadata == null) {
			return Response.status(404).build();
		}
		
		InputStream inputStream;
        try {
            inputStream = new FileInputStream(metadata.getPath());
        } catch (FileNotFoundException e) {
            Logger.getGlobal().warning("An error has occurred finding file " + metadata.getPath());
            return Response.status(404).build();
        }
        return Response.ok().entity(inputStream).build();
	}
	
	@DELETE
	@Path("{id}")
	public String deleteVideo(@PathParam("id") int videoId, @HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
		Logger.getGlobal().info("Delete video by Id Request");
		WebService.getGlobal().deleteVideo(videoId, authenticationToken);
		return ServerConstants.SUCCESS;
	}
	
	/*@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadVideo(@FormDataParam("video") InputStream encryptedVideo, @FormDataParam("metadata") InputStream encryptedMetadata, @FormDataParam("key") InputStream encryptedKey, @FormDataParam("video") FormDataContentDisposition fileDetail, @HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
    	Logger.getGlobal().info("Upload video");
    	WebService.getGlobal().postVideo(encryptedVideo, encryptedMetadata, encryptedKey, authenticationToken);
    }*/
}
