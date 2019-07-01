package edu.kit.informatik.pcc.service.authentication;

public interface IUserManagement {
	public Boolean createAccount(String email, String password);
	public String login(String email, String password);
	public void deleteAccount(String authenticationToken);
}
