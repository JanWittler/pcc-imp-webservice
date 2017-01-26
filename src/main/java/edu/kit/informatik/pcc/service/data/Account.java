package edu.kit.informatik.pcc.service.data;

import org.json.JSONObject;

/**
 * @author Fabian Wenzel, David Laubenstein
 * Created by David Laubenstein at 17.01.2017
 */
public class Account {
	// attributes
	private String mail;
	private String passwordHash;
	private int id;

	/**
	 * constructor, which relates the json string to the class variables
	 * @param json which includes account information
	 */
	public Account(String json) {
	    // convert json String to class attributes
        // create JSON Object
		JSONObject obj = new JSONObject(json);
		// go into account object
		JSONObject account = obj.getJSONObject("account");
		// save Strings in account object to class attributes
		this.mail = account.getString("mail");
		this.passwordHash = hashPassword(account.getString("password"));
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
	public String getMail() {
		return mail;
	}
	public void setId(int id) {
		this.id = id;
	}
}
