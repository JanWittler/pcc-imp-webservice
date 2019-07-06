package edu.kit.informatik.pcc.service.videoprocessing;

import java.io.File;

public interface IAsyncVideoProcessor {
	public void processVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKeyData, File outputVideo, File outputMetadata);
}
