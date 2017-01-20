package edu.kit.informatik.pcc.service.data;
public class VideoInfo {
	// attributes
	private int videoId;
	private String videoName;
	// constructors
	public VideoInfo(int videoId, String videoName) {
		this.videoId = videoId;
		this.videoName = videoName;
	}
	// methods
	public String getAsJson() {
		//TODO: write method
		return "";
	}
	// getter/setter
	public String getName() {
		//TODO: write method
		return videoName;
	}

	public int getVideoId() {
		return videoId;
	}
}
