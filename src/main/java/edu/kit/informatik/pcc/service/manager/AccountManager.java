package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;

/**
 * @author David Laubenstein, Fabian Wenzel
 * Created by David Laubenstein on 01/18/2017
 */
public class AccountManager {
	// attributes
	private Account account;
	private DatabaseManager databaseManager;

	// constructor
	public AccountManager(Account account) {
		this.account = account;
		databaseManager = new DatabaseManager(account);
	}

	// methods
	public String setMail(String newMail) {
		boolean status = databaseManager.setMail(newMail);
		if (status == false) {
			return "FAILURE";
		}
		return "SUCCESS";
	}
	public String setPassword(String passwordHash) {
		boolean status = databaseManager.setMail(passwordHash);
		if (status == false) {
			return "FAILURE";
		}
		return "SUCCESS";
	}
	public int getAccountId() {
	    return databaseManager.getAccountId();
	}
	public String registerAccount(String uuid) {
		boolean status = databaseManager.register(uuid);
		if (status == false) {
			return "FAILURE";
		}
		return "SUCCESS";
	}
	public String deleteAccount() {
		boolean status = databaseManager.deleteAccount();
		if (status == false) {
			return "FAILURE";
		}
		return "SUCCESS";
	}
	public boolean authenticate() {
		return databaseManager.authenticate();
	}
	public String verifyAccount(String uuid) {
		boolean status = databaseManager.verifyAccount(uuid);
		if (status == false) {
			return "FAILURE";
		}
		return "SUCCESS";
	}
	public boolean isVerified() {
		return databaseManager.isVerified();
	}
}
