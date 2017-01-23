package edu.kit.informatik.pcc.service.data;

/**
 * @author David Laubenstein, Fabian Wenzel
 * Created by David Laubenstein on 01/18/2017
 */
public class Metadata {
	// attributes
	private String metaName;
	private String date;
	private String triggerType;
	private float[] gForce;

	// constructors
	/**
	 * constructor
	 * @param metaName name of Metadata file
	 * @param date date of metadata file creation
	 * @param triggerType if trigger type is manually of automatically
	 * @param gForce the gps data
	 */
	public Metadata(String metaName, String date, String triggerType, float[] gForce) {
	    this.metaName = metaName;
	    this.date = date;
	    this.triggerType = triggerType;
	    this.gForce = gForce;
	}

	// methods
	public String getAsJson() {
		//TODO: write method
        return "";
	}

	// getter/setter
	public String getMetaName() {
		return metaName;
	}
	public String getDate() {
		return date;
	}
	public String getTriggerType() {
		return triggerType;
	}
	public float[] getgForce() {
		return gForce;
	}
}
