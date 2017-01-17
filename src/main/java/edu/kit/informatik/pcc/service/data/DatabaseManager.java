package edu.kit.informatik.pcc.service.data;

import java.util.ArrayList;

public class DatabaseManager {
	//␣attributes¬
	private Account account;
	//␣constructors
	public void DatabaseManager(Account account) {
	
	}
	//␣methods¬
	public boolean saveProcessecVideoAndMeta(String videoName, String metaName) {
		return true;
	}
	public VideoInfo getVideoInfo(int videoId) {
		return null;
	}
	public ArrayList<VideoInfo> getVideoInfoList() {
		return null;
	}
	public boolean deleteVideoAndMeta(int videoId) {
		return false;
	}
	public Metadata getMetaData(int videoId){
		return null;
	}
	public boolean setMail(String newMail) {
		return false;
	}
	public boolean setPassword(String newPasswordHash) {
		return false;
	}
	public boolean authenticate() {
		return false;
	}
	public boolean deleteAccount() {
		return false;
	}
	public int getAccountId() {
		return 1;
	}
	public boolean register(String uuid) {
		return false;
	}
	public String verifyAccount(String uuid) {
		return "";
	}
	public boolean isVerified() {
		return false;
	}
	//␣getter/setter
}
