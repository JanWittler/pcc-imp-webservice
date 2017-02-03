package edu.kit.informatik.pcc.service.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * @author David Laubenstein, Fabian Wenzel, Josh Romanowski
 */
public class Metadata {
    // JSON keys
    private final static String JSON_KEY_DATE = "date";
    private final static String JSON_KEY_TRIGGER_TYPE = "triggerType";
    private final static String JSON_KEY_TRIGGER_FORCE_X = "triggerForceX";
    private final static String JSON_KEY_TRIGGER_FORCE_Y = "triggerForceY";
    private final static String JSON_KEY_TRIGGER_FORCE_Z = "triggerForceZ";

    // attributes
    private long date;
    private String triggerType;
    private float[] gForce;

    // constructors

    public Metadata(String json) {
        JSONObject metadata = new JSONObject(json);

        // retrieve json data
        this.date = metadata.getLong(JSON_KEY_DATE);
        this.triggerType = metadata.getString(JSON_KEY_TRIGGER_TYPE);
        this.gForce = new float[3];
        this.gForce[0] = (float) metadata.getDouble(JSON_KEY_TRIGGER_FORCE_X);
        this.gForce[1] = (float) metadata.getDouble(JSON_KEY_TRIGGER_FORCE_Y);
        this.gForce[2] = (float) metadata.getDouble(JSON_KEY_TRIGGER_FORCE_Z);
    }

    /**
     * constructor
     *
     * @param date        date of metadata file creation
     * @param triggerType if trigger type is manually of automatically
     * @param gForce      the gps data
     */
    public Metadata(long date, String triggerType, float[] gForce) {
        this.date = date;
        this.triggerType = triggerType;
        this.gForce = gForce;
    }

    // methods
    public String getAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_KEY_DATE, this.date);
            json.put(JSON_KEY_TRIGGER_TYPE, this.triggerType);
            json.put(JSON_KEY_TRIGGER_FORCE_X, this.gForce[0]);
            json.put(JSON_KEY_TRIGGER_FORCE_Y, this.gForce[1]);
            json.put(JSON_KEY_TRIGGER_FORCE_Z, this.gForce[2]);
        } catch (JSONException e) {
            Logger.getGlobal().warning("Error creating metadata json");
        }
        return json.toString();
    }

    // getter/setter
    public long getDate() {
        return date;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public float[] getGForce() {
        return gForce;
    }
}
