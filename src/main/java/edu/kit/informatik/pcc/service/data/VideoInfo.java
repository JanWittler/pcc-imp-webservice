package edu.kit.informatik.pcc.service.data;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 01/18/2017
 */
public class VideoInfo {
	// attributes
	private int videoId;
	private String videoName;
	// constructors

	/**
	 * constructor
	 * @param videoId the id of the video
	 * @param videoName the name of the video
	 */
	public VideoInfo(int videoId, String videoName) {
		this.videoId = videoId;
		this.videoName = videoName;
	}
	// methods

	/**
	 * return VideoInfo-Object as json String
	 * @return json String of videoInfo object
	 */
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
