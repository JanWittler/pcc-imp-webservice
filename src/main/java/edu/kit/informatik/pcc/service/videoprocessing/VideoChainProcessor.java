package edu.kit.informatik.pcc.service.videoprocessing;

import java.io.File;
import java.util.UUID;

import edu.kit.informatik.pcc.service.data.IFileManager;

public class VideoChainProcessor implements IVideoProcessor {
	private IFileManager temporaryFileManager;
	private IVideoProcessor[] videoProcessors;
	
	public void setTemporaryFileManager(IFileManager temporaryFileManager) {
		assert this.temporaryFileManager == null;
		this.temporaryFileManager = temporaryFileManager;
	}
	
	public void setVideoProcessors(IVideoProcessor[] videoProcessors) {
		assert this.videoProcessors == null;
		assert videoProcessors.length > 0;
		this.videoProcessors = videoProcessors;
	}

	@Override
	public void processVideo(File inputVideo, File metadata, File outputVideo) {
		assertCompletelySetup();
		File intermediateVideo = inputVideo;
		for (int i = 0; i < videoProcessors.length - 1; i++) {
			File temporaryFile = temporaryFileManager.file(UUID.randomUUID().toString());
			videoProcessors[i].processVideo(intermediateVideo, metadata, temporaryFile);
			intermediateVideo = temporaryFile;
		}
		videoProcessors[videoProcessors.length - 1].processVideo(intermediateVideo, metadata, outputVideo);
		if (intermediateVideo != inputVideo) {
			temporaryFileManager.deleteFile(intermediateVideo);
		}
	}
	
	private void assertCompletelySetup() {
		assert videoProcessors != null;
	}
}