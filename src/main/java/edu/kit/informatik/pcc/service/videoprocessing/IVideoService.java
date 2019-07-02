package edu.kit.informatik.pcc.service.videoprocessing;

import java.io.File;

public interface IVideoService {
	public int[] getVideoIds(int userId);
	public void postVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKeyData, int userId);
	public File getVideo(int videoId, int userId);
	public File getMetadata(int videoId, int userId);
	public void deleteVideo(int videoId, int userId);
}
