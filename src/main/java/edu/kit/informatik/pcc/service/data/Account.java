package edu.kit.informatik.pcc.service.data;

import org.json.JSONObject;

import java.sql.ResultSet;

/**
 * @author Fabian Wenzel, David Laubenstein
 * Created by David Laubenstein at 17.01.2017
 */
public class Account {
	// attributes
	private String email;
	private String passwordHash;
	private int id;
	// constructors

	/**
	 * constructor, which relates the json string to the class variables
	 * @param json which includes account information
	 */
	public Account(String json) {
	    // convert json String to class attributes
        // create JSON Object
		JSONObject obj = new JSONObject(json);
		// go into accountData object
		JSONObject accountData = obj.getJSONObject("accountData");
		// save Strings in accountData object to class attributes
		this.email = accountData.getString("mail");
		this.passwordHash = hashPassword(accountData.getString("password"));
	}

	// methods
	/**
	 * hashes a password with a function
	 * @param password password in plain text
	 * @return: hashed password
	 */
	public String hashPassword(String password) {
	    //TODO: hashPassword
        return password;
	}
	// getter/setter
	public int getId() {
		return id;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public String getEmail() {
		return email;
	}
	public void setId(int id) {
		this.id = id;
	}
}
