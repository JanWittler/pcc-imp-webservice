package edu.kit.informatik.pcc.service.server;

import java.io.File;

public interface IWebVideoService {
	public int[] getVideoIds(String authenticationToken);
	public void postVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKeyData, String authenticationToken);
	public File getVideo(int videoId, String authenticationToken);
	public File getMetadata(int videoId, String authenticationToken);
	public void deleteVideo(int videoId, String authenticationToken);
}
