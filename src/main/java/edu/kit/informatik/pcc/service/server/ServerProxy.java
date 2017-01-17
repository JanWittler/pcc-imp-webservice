package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;

import java.io.InputStream;

public class ServerProxy {
	// attributes
	private Account account;
	// constructor
	// methods
	public String videoUpload (InputStream video, InputStream metadata, String encryptedSymmetricKey, String accountData, AsyncResponse response) {
	
	}
	public Response videoDownload (int videoId, String accountData) {
	
	}
	public String videoInfo (int videoId, String accountData) {
	
	}
	public String videoDelete (int videoId, String accountData) {
	
	}
	public String getVideosByAccount (String accountData) {
	
	}
	public String authenticateAccount (String accountData) {
	
	}
	public String createAccount (String accountData, int uuid) {
	
	}
	public String changeAccount (String accountDataNew, String accountData) {
	
	}
	public String deleteAccount (String accountData) {
	
	}
	public String verifyAccount (String accountData, int uuid) {
	
	}
	
	private String setUpForRequest(String accountData) {
	
	}
	// getter/setter
}
