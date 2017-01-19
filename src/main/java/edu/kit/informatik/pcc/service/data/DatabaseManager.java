package edu.kit.informatik.pcc.service.data;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager { //TODO: LOGGER
	// attributes
	private Account account;
	private static String PORT = "5432";
	private static String HOST = "localhost";
	private static String DB_NAME = "PrivacyCrashCam";
	private Connection c = null; //TODO: LOGGER
	// constructors
	public void DatabaseManager(Account account) {
	    // create access to account
	    this.account = account;
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
        connectDatabase();
		// send sql command and catch possible exeptions
        try {
            Statement stmt = null;
            stmt = this.c.createStatement();
            // sql command
			//TODO: change id in sql command
			String sql = "insert into \"video\" (id,user_id,video_name,meta_name) values (3," + account.getId() + ",'" + videoName + "','" + metaName + "');";
            stmt.executeUpdate(sql);
            this.c.commit();
            stmt.close();
            this.c.close();
        } catch (NullPointerException nPE) {
            System.out.println(nPE);
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
		//TODO: check, if insert was successful??? or do I have to test this in TestCases?
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

			ResultSet rs = stmt.executeQuery( "select \"video_name\",vid.\"id\" from \"video\" as vid  join \"user\" as usr ON vid.user_id=usr.id where usr.id='" + account.getId() + "'" );
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
	    connectDatabase();
	    try {
            Statement stmt = null;
            stmt = this.c.createStatement();
            stmt = c.createStatement();
            // sql command
            String sql = "DELETE from \"video\" where id=" + videoId + ";";
            stmt.executeUpdate(sql);
            this.c.commit();
            stmt.close();
            this.c.close();
        } catch (NullPointerException nPE) {
            System.out.println(nPE);
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        //TODO: check, if video is deleted
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
            String sql = "UPDATE \"user\" set mail='" + newMail + "' where id=" + account.getId() + ";";
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
        // connect to database
        connectDatabase();
        // send sql command and catch possible exeptions
        try {
            Statement stmt = null;
            stmt = this.c.createStatement();
            stmt = c.createStatement();
            // sql command
            String sql = "UPDATE \"user\" set password='" + newPasswordHash + "' where id=" + account.getId() + ";";
            stmt.executeUpdate(sql);
            this.c.commit();
            stmt.close();
            this.c.close();
        } catch (NullPointerException nPE) {
            System.out.println(nPE);
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        // TODO: check, if password is updated correctly
		return false;
	}
	public boolean authenticate() {
	    String mail = "";
	    String passwordHash = "";
	    // connect to database
	    connectDatabase();
	    // execute sql command and insert result in ArrayList
	    try {
			Statement stmt = null;
			stmt = this.c.createStatement();

			ResultSet rs = stmt.executeQuery( "select \"mail\",\"password\" from \"user\" where id='" + account.getId() + "'" );
			// insert result in ArrayList
			while ( rs.next() ) {
				mail = rs.getString("mail");
				passwordHash = rs.getString("password");
			}
			rs.close();
			stmt.close();
			this.c.close();
		} catch (NullPointerException nPE) {
			System.out.println(nPE);
		} catch (SQLException sqlException) {
			System.out.println(sqlException);
		}
		//return boolean, if password and mail are equal to database data
		return mail.equals(account.getEmail()) && passwordHash.equals(account.getPasswordHash());

	}
	public boolean deleteAccount() {
        //TODO: do I have to delete the videos first? or does the AccountManager handles this?
		return false;
	}
	public int getAccountId() {
        //TODO: write method how do I get the account id? from the database? compared with what???
		return 1;
	}
	public boolean register(String uuid) {
		connectDatabase();
		//TODO: check, if mail is already existing. If not, create Account
		// send sql command and catch possible exeptions
        try {
            Statement stmt = null;
            stmt = this.c.createStatement();
            stmt = c.createStatement();
            // sql command
            //TODO: change id
			String sql = "insert into \"user\" (id,mail,password,uuid,verified) values (1,'" + account.getEmail() + "','" + account.getPasswordHash() + "'," + uuid + ",false);";
            stmt.executeUpdate(sql);
            this.c.commit();
            stmt.close();
            this.c.close();
        } catch (NullPointerException nPE) {
            System.out.println(nPE);
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
		//TODO: check, if insert was successful??? or do I have to test this in TestCases?
		return false;
	}
	public String verifyAccount(String uuid) {
	    String ret = "";
        //connect to Database
		connectDatabase();
		// get uuid from account
        String uuidDatabase = "";
		try {
			Statement stmt = null;
			stmt = this.c.createStatement();

			ResultSet rs = stmt.executeQuery( "select \"uuid\" from \"user\" as usr  where usr.id='" + account.getId() + "'" );
			// insert result in ArrayList
			uuidDatabase= rs.getString("uuid");
			rs.close();
			stmt.close();
		} catch (NullPointerException nPE) {
			System.out.println(nPE);
		} catch (SQLException sqlException) {
			System.out.println(sqlException);
		}
		if (uuidDatabase.equals(uuid)) {
			//TODO: set verified --> true
            try {
                Statement stmt = null;
                stmt = this.c.createStatement();

                stmt.executeQuery("update \"user\" set verified=TRUE where id=" + account.getId() + ";");
                stmt.close();
                this.c.close();
            } catch (NullPointerException nPE) {
                System.out.println(nPE);
            } catch (SQLException sqlException) {
                System.out.println(sqlException);
            }
            return ret;
		} else {
            //TODO: return error message, because uuid not correct
            // close c, because when if=true, c is needed
            try {
                this.c.close();
            } catch (SQLException sqlE) {
                System.out.println(sqlE);
            }
            return ret;
        }
	}
	public boolean isVerified() {
	    // connect to Database
	    connectDatabase();
	    int verified = 2;
        try {
            Statement stmt = null;
            stmt = this.c.createStatement();

            ResultSet rs = stmt.executeQuery("select \"verified\" from \"user\" where id=" + account.getId() + ";");
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

			ResultSet rs = stmt.executeQuery("select \"meta_name \" from \"video \" as vid where vid.id=" + videoId + ";");
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
