package edu.kit.informatik.pcc.service.authentication;

public interface IUserIdProvider {
	final static int invalidId = -1;
	
	public int getUserId(String authenticationToken);
}
