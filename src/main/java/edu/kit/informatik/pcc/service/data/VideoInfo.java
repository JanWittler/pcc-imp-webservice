package edu.kit.informatik.pcc.service.data;

/**
 * @author David Laubenstein, Fabian Wenzel
 * Created by David Laubenstein on 01/18/2017
 */
public class VideoInfo {
	// attributes
	private int videoId;
	private String videoName;

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
		//language=JSON
		return	"{\"videoInfo\": {\n" +
				"  \"name\": \"" + videoName + "\",\n" +
				"  \"id\": \""+ videoId +"\"\n" +
				"}}";
	}

	// getter/setter
	public String getName() {
		return videoName;
	}
	public int getVideoId() {
		return videoId;
	}
}
