package edu.kit.informatik.pcc.service.authentication;

import java.util.UUID;

import org.apache.commons.validator.routines.EmailValidator;

public class UserService implements IUserManagement, IUserIdProvider {
	private IUserDB userDB;
	private IUserSessionDB userSessionDB;
	
	public void setUserDB(IUserDB userDB) {
		assert this.userDB == null;
		this.userDB = userDB;
	}
	
	public void setUserSessionDB(IUserSessionDB userSessionDB) {
		assert this.userSessionDB == null;
		this.userSessionDB = userSessionDB;
	}

	@Override
	public Boolean createAccount(String email, String password) {
		assertCompletelySetup();
		int userId = userDB.getUserIdByMail(email);
		//if account with email already exists
		if (userId != IUserIdProvider.invalidId) {
			return false;
		}
		if (!isMailValid(email)) {
			return false;
		}
		if (password == null || password.length() == 0) {
			return false;
		}
		userDB.createUser(email, password);
		return true;

	}

	@Override
	public String login(String email, String password) {
		assertCompletelySetup();
		int userId = userDB.getUserIdByMail(email);
		if (userId == IUserIdProvider.invalidId) {
			return null;
		}
		if (!userDB.validatePassword(userId, password)) {
			return null;
		}
		String token = UUID.randomUUID().toString();
		userSessionDB.storeAuthenticationToken(token, userId);
		return token;
	}

	@Override
	public Boolean deleteAccount(String authenticationToken) {
		assertCompletelySetup();
		int userId = userSessionDB.getUserId(authenticationToken);
		if (userId != IUserIdProvider.invalidId) {
			userDB.deleteAccount(userId);
			userSessionDB.deleteTokensForUserId(userId);
			return true;
		}
		return false;
	}
	
	@Override
	public int getUserId(String authenticationToken) {
		assertCompletelySetup();
		return userSessionDB.getUserId(authenticationToken);
	}
	
	private boolean isMailValid(String email) {
        if (email == null || email.length() == 0) {
        	return false;
        }
        EmailValidator ev = EmailValidator.getInstance();
        return ev.isValid(email.trim());
    }
	
	private void assertCompletelySetup() {
		assert userDB != null;
		assert userSessionDB != null;
	}
}
