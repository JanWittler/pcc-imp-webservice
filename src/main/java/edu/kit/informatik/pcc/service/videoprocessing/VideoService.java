package edu.kit.informatik.pcc.service.videoprocessing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import edu.kit.informatik.pcc.service.data.IFileHierachyManager;
import edu.kit.informatik.pcc.service.data.IFileManager;

public class VideoService implements IVideoService {
	private static final String videosDirectory = "videos";
	private static final String metadataDirectory = "metadata";
	
	private IFileHierachyManager fileHierachyManager;
	private IFileManager temporaryFileManager;
	private IAsyncVideoProcessor asyncVideoProcessor;
	
	public void setFileHierachyManager(IFileHierachyManager fileHierachyManager) {
		assert this.fileHierachyManager == null;
		this.fileHierachyManager = fileHierachyManager;
	}
	
	public void setTemporaryFileManager(IFileManager temporaryFileManager) {
		assert this.temporaryFileManager == null;
		this.temporaryFileManager = temporaryFileManager;
	}
	
	public void setAsyncVideoProcessor(IAsyncVideoProcessor asyncVideoProcessor) {
		assert this.asyncVideoProcessor == null;
		this.asyncVideoProcessor = asyncVideoProcessor;
	}
	
	@Override
	public int[] getVideoIds(int userId) {
		assertCompletelySetup();
		File[] videos = fileHierachyManager.filesInDirectory(videoDirectory(userId));
		List<Integer> videoIdList = new ArrayList<Integer>();
		for (File video: videos) {
			try {
				int videoId = Integer.parseInt(FilenameUtils.removeExtension(video.getName()));
				videoIdList.add(new Integer(videoId));
			}
			catch (NumberFormatException e) {
				Logger.getGlobal().warning("got video with invalid name " + video + " for user " + userId);
			}
		}
		int[] videoIds = new int[videoIdList.size()];
		for (int i = 0; i < videoIdList.size(); i++) {
			videoIds[i] = videoIdList.get(i).intValue();
		}
		return videoIds;
	}

	@Override
	public void postVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKeyData, int userId) {
		assertCompletelySetup();
		if (encryptedVideo == null || encryptedMetadata == null || 
				encryptedKeyData == null || encryptedKeyData.length == 0) {
			return;
		}
		int newVideoId = unusedVideoId(userId);
		File outputVideo = fileHierachyManager.file(videoFileName(newVideoId), videoDirectory(userId));
		File outputMetadata = fileHierachyManager.file(metadataFileName(newVideoId), metadataDirectory(userId));
		asyncVideoProcessor.processVideo(encryptedVideo, encryptedMetadata, encryptedKeyData, outputVideo, outputMetadata);
	}

	@Override
	public File getVideo(int videoId, int userId) {
		assertCompletelySetup();
		return fileHierachyManager.existingFile(videoFileName(videoId), videoDirectory(userId));
	}

	@Override
	public File getMetadata(int videoId, int userId) {
		assertCompletelySetup();
		return fileHierachyManager.existingFile(metadataFileName(videoId), metadataDirectory(userId));
	}

	@Override
	public void deleteVideo(int videoId, int userId) {
		assertCompletelySetup();
		fileHierachyManager.deleteFile(getVideo(videoId, userId));
		fileHierachyManager.deleteFile(getMetadata(videoId, userId));
	}
	
	private String videoFileName(int videoId) {
		return videoId + ".mp4";
	}
	
	private String metadataFileName(int videoId) {
		return videoId + ".json";
	}
	
	private String videoDirectory(int userId) {
		return userId + File.separator + videosDirectory;
	}
	
	private String metadataDirectory(int userId) {
		return userId + File.separator + metadataDirectory;
	}
	
	private int unusedVideoId(int userId) {
		int[] videoIds = getVideoIds(userId);
		if (videoIds.length == 0) {
			return 1;
		}
		return Arrays.stream(videoIds).max().getAsInt() + 1;
	}

	private void assertCompletelySetup() {
		assert fileHierachyManager != null;
		assert temporaryFileManager != null;
		assert asyncVideoProcessor != null;
	}
}
