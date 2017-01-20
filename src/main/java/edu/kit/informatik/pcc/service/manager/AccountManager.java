package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;

/**
 * @author Fabian Wenzel
 */
public class AccountManager {
	// attributes
	private Account account;
	private DatabaseManager dbms;

	// constructor
	public AccountManager(Account account) {
		dbms = new DatabaseManager();
		this.account = account;
	}

	// methods
	public String setMail(String newMail) {
		//TODO: write method
		return "";
	}
	public String setPassword(String passwordHash) {
		//TODO: write method
		return "";
	}
	public int getAccount() {
		//TODO: write method
	    return 1;
	}
	public String registerAccount(String uuid) {
		//TODO: write method
		return "";
	}
	public String deleteAccount() {
	    //TODO: write method
		return "";
	}
	public boolean authenticate() {
		//TODO: write method
		return false;
	}
	public String verifyAccount(String uuid) {
		//TODO: write method
		return "";
	}
	public boolean isVerified() {
		//TODO: write method
		return false;
	}
	// getter/setter
}
