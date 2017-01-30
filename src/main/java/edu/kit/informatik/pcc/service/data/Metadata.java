package edu.kit.informatik.pcc.service.data;

/**
 * @author David Laubenstein, Fabian Wenzel
 * Created by David Laubenstein on 01/18/2017
 */
public class Metadata {
	// attributes
	private String date;
	private String triggerType;
	private float[] gForce;

	/**
	 * constructor
	 * @param date date of metadata file creation
	 * @param triggerType if trigger type is manually of automatically
	 * @param gForce the gps data
	 */
	public Metadata(String date, String triggerType, float[] gForce) {
	    this.date = date;
	    this.triggerType = triggerType;
	    this.gForce = gForce;
	}

	// methods
	public String getAsJson() {
        return "{\"metadata\": {\n" +
				"  \"date\": \"" + date + "\",\n" +
				"  \"triggertype\": \"" + triggerType + " \",\n" +
				"  \"gForceX\": \""+ gForce[0] +"\",\n" +
				"  \"gForceY\": \""+ gForce[1] +"\",\n" +
				"  \"gForceZ\": \""+ gForce[2] +"\"\n" +
				"}}";
	}

	// getter/setter
	public String getDate() {
		return date;
	}
	public String getTriggerType() {
		return triggerType;
	}
	public float[] getGForce() {
		return gForce;
	}
}
