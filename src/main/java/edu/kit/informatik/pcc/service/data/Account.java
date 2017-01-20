package edu.kit.informatik.pcc.service.data;

/**
 * @author Fabian Wenzel
 */
public class Account {
	// attributes
	private String email;
	private String passwordHash;
	private int id;
	// constructors
	public Account(String json) {
	    // convert json String to class attributes
		//TODO: write constructor
	}
	// methods
	public String hashPassword(String password) {
	    // TODO create Method
        return "";
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
