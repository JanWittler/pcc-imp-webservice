package edu.kit.informatik.pcc.service.videoprocessing;

import java.io.File;

public interface IVideoProcessor {
	public void processVideo(File inputVideo, File metadata, File outputVideo);
}
