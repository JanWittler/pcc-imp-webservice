package edu.kit.informatik.pcc.service.authentication;

public interface IUserDB {
	public void createUser(String email, String password);
	public int getUserIdByMail(String email);
	public Boolean validatePassword(int userId, String password);
	public void deleteAccount(int userId);
}
