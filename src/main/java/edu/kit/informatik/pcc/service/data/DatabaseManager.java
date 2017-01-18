package edu.kit.informatik.pcc.service.data;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {
	// attributes
	private Account account;
	private static String PORT = "5432";
	private static String HOST = "localhost";
	private static String DB_NAME = "PrivacyCrashCam";
	Connection c = null;
	// constructors
	public void DatabaseManager(Account account) {
        //TODO: write method
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
        //TODO: write method
		return true;
	}
	public VideoInfo getVideoInfo(int videoId) {
        //TODO: write method
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

			ResultSet rs = stmt.executeQuery( "select \"video_name\",vid.\"id\" from \"Video\" as vid  join \"User\" as usr ON vid.user_id=usr.id where usr.id='" + account.getId() + "'" );
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
		return videoInfoList;
	}
	public boolean deleteVideoAndMeta(int videoId) {
	    //TODO: write method
		return false;
	}
	public Metadata getMetaData(int videoId){
	    // create String, where meta file is stored
	    String filePath = LocationConfig.metaDataDirectory + "/" + getMetaNameByVideoId(videoId);
	    // read Meta file to get infos
        // TODO read Meta file to get infos
	    // Metadata meta = new Metadata();
        // return meta;
        return null;
	}
	public boolean setMail(String newMail) {
	    // connect to database
        connectDatabase();
        // send sql command and catch possible exeptions
        try {
            Statement stmt = null;
            stmt = this.c.createStatement();
            stmt = c.createStatement();
            // sql command
            String sql = "UPDATE \"User\" set mail='" + newMail + "' where id=" + account.getId() + ";";
            stmt.executeUpdate(sql);
            this.c.commit();
            stmt.close();
            this.c.close();
        } catch (NullPointerException nPE) {
            System.out.println(nPE);
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        // TODO: check, if mail is updated correctly
        return true;
	}
	public boolean setPassword(String newPasswordHash) {
        //TODO: write method
		return false;
	}
	public boolean authenticate() {
        //TODO: write method
		return false;
	}
	public boolean deleteAccount() {
        //TODO: write method
		return false;
	}
	public int getAccountId() {
        //TODO: write method
		return 1;
	}
	public boolean register(String uuid) {
        //TODO: write method
		return false;
	}
	public String verifyAccount(String uuid) {
        //TODO: write method
		return "";
	}
	public boolean isVerified() {
	    // connect to Database
	    connectDatabase();
	    int verified = 2;
        try {
            Statement stmt = null;
            stmt = this.c.createStatement();

            ResultSet rs = stmt.executeQuery("select \"verified\" from \"User\" where id=" + account.getId() + ";");
            // insert result in ArrayList
            if (rs.getFetchSize() <= 1) {
                verified = Integer.parseInt(rs.getString("meta_name"));
            }
            rs.close();
            stmt.close();
            this.c.close();
        } catch (NullPointerException nPE) {
            System.out.println(nPE);
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        if(verified == 0) {
            return false;
        } else if (verified == 1) {
            return true;
        } else {
            //TODO: create Error Message
            return false;
        }
	}
	// getter/setter
    private String getMetaNameByVideoId(int videoId) {
	    //connect to database
	    connectDatabase();
        String meta = "";
	    try {
			Statement stmt = null;
			stmt = this.c.createStatement();

			ResultSet rs = stmt.executeQuery("select \"meta_name \" from \"Video \" as vid where vid.id=" + videoId + ";");
			// insert result in ArrayList
            if (rs.getFetchSize() <= 1) {
                meta = rs.getString("meta_name");
            }
			rs.close();
			stmt.close();
			this.c.close();
		} catch (NullPointerException nPE) {
			System.out.println(nPE);
		} catch (SQLException sqlException) {
			System.out.println(sqlException);
		}
		return meta;
    }
}
