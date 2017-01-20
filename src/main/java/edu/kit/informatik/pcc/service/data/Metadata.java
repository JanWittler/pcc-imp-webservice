package edu.kit.informatik.pcc.service.data;

/**
 * @author David Laubenstein
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
		//TODO: write constructor
        return;
	}
	// methods
	public String getAsJson() {
		//TODO: write method
        return "";
	}
	// getter/setter
}
