package edu.kit.informatik.pcc.service.data;
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
		//TODO: write method
		return passwordHash;
	}
	public String getEmail() {
		//TODO: write method
		return email;
	}
}
