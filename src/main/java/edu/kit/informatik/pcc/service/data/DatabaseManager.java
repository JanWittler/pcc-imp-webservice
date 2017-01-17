package edu.kit.informatik.pcc.service.data;

import javax.sound.sampled.Port;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DatabaseManager {
	// attributes
	private Account account;
	private static String PORT = "5432";
	private static String HOST = "localhost";
	private static String DB_NAME = "PrivacyCrashCam";
	Connection c = null;
	// constructors
	public void DatabaseManager(Account account) {
	
	}
	// methods
	public void connectDatabase() {
		c = null;
		try {
			Class.forName("org.postgresql.Driver");
         	this.c = DriverManager
            	.getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME + "");
      	} catch (Exception e) {
			e.printStackTrace();
        	System.err.println(e.getClass().getName()+": "+e.getMessage());

	        System.exit(0);
      	}
	}
	public boolean saveProcessecVideoAndMeta(String videoName, String metaName) {
		return true;
	}
	public VideoInfo getVideoInfo(int videoId) {
		return null;
	}
	public ArrayList<VideoInfo> getVideoInfoList() {
		// create ArrayList
		ArrayList<VideoInfo> videoInfoList= new ArrayList<VideoInfo>();
	    // connect to database
	    connectDatabase();
	    // execute sql command and insert result in ArrayList
	    try {
			Statement stmt = null;
			stmt = this.c.createStatement();
			ResultSet rs = stmt.executeQuery( "select \"video_name\",vids.\"id\" from \"Video\" as vids  join \"User\" as usr ON vids.user_id=usr.id where usr.id='" + account.getId() + "'" );
			// insert result in ArrayList
			while ( rs.next() ) {
				String video_name = rs.getString("video_name");
				int id = Integer.parseInt(rs.getString("id"));
				VideoInfo vI = new VideoInfo(id, video_name);
				videoInfoList.add(vI);
			}
			rs.close();
			stmt.close();
			this.c.close();
		} catch (NullPointerException nPE) {
			System.out.println(nPE);
		} catch (SQLException sqlException) {
			System.out.println(sqlException);
		}
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
	// getter/setter
}
