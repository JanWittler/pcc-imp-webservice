package edu.kit.informatik.pcc.service.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;

import edu.kit.informatik.pcc.core.data.IFileManager;

@Path("webservice/videos")
public class VideoServerProxy {
	private static IFileManager temporaryFileManager;
	
	public static void setTemporaryFileManager(IFileManager pTemporaryFileManager) {
		assert temporaryFileManager == null;
		temporaryFileManager = pTemporaryFileManager;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getVideoIds(@HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
		assertCompletelySetup();
		Logger.getGlobal().info("Get Video Ids Request");
		int[] videoIds = WebService.getGlobal().getVideoIds(authenticationToken);
		if (videoIds == null) {
			return ServerConstants.NOT_AUTHENTICATED;
		}
		JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < videoIds.length; i++) {
            jsonArray.put(videoIds[i]);
        }
        return jsonArray.toString();
	}
	
	@GET
	@Path("{id}")
	public Response getVideo(@PathParam("id") int videoId, @HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
		assertCompletelySetup();
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
		assertCompletelySetup();
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
		assertCompletelySetup();
		Logger.getGlobal().info("Delete video by Id Request");
		WebService.getGlobal().deleteVideo(videoId, authenticationToken);
		return ServerConstants.SUCCESS;
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadVideo(@FormDataParam("video") InputStream encryptedVideoStream, @FormDataParam("metadata") InputStream encryptedMetadataStream, @FormDataParam("key") InputStream encryptedKeyStream, @HeaderParam(ServerConstants.TOKEN) String authenticationToken) {
    	assertCompletelySetup();
		Logger.getGlobal().info("Video upload");
		
		String uuid = UUID.randomUUID().toString();
		File encryptedVideo = temporaryFileManager.file(uuid + ".video");
		File encryptedMetadata = temporaryFileManager.file(uuid + ".metadata");
		File encryptedKeyFile = temporaryFileManager.file(uuid + ".key");
		saveStreamToFile(encryptedVideoStream, encryptedVideo);
		saveStreamToFile(encryptedMetadataStream, encryptedMetadata);
		saveStreamToFile(encryptedKeyStream, encryptedKeyFile);
		byte[] encryptedKey;
		String result = ServerConstants.SUCCESS;
		try {
			encryptedKey = Files.readAllBytes(encryptedKeyFile.toPath());
			WebService.getGlobal().postVideo(encryptedVideo, encryptedMetadata, encryptedKey, authenticationToken);
		} catch (IOException e) {
			Logger.getGlobal().warning("Failed to load encrypted key file: " + e.getLocalizedMessage());
			e.printStackTrace();
			result = ServerConstants.FAILURE;
		}
		temporaryFileManager.deleteFile(encryptedKeyFile);
    	temporaryFileManager.deleteFile(encryptedVideo);
    	temporaryFileManager.deleteFile(encryptedMetadata);
		return result;
    }
	
	private void assertCompletelySetup() {
		assert temporaryFileManager != null;
	}
	
	private void saveStreamToFile(InputStream inputStream, File file) {
		int read;
        byte[] bytes = new byte[1024];
		try (FileOutputStream fos = new FileOutputStream(file)) {
			while ((read = inputStream.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
			fos.flush();
		} catch (IOException e) {
			Logger.getGlobal().warning("Failed to store file during upload");
			e.printStackTrace();
		}
	}
}
