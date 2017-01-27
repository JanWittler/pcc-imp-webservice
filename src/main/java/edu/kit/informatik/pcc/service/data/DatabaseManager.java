package edu.kit.informatik.pcc.service.data;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author David Laubenstein
 * Created by David Laubenstein at 1/18/17
 */
public class DatabaseManager {

	// attributes
	private Account account;
	private static final String PORT = "5432";
	private static final String HOST = "localhost";
	private static final String DB_NAME = "PrivacyCrashCam";
    private static final String USER = "postgres";
    private static final String PASSWORD = "";
    private Connection c = null;
    // constructors

    /**
     * constructor, which includes the account object.
     * @param account to have access to the actual account.
     */
    public DatabaseManager(Account account) {
        // create access to account
        this.account = account;
    }
    // methods

    /**
     * creates the connection to the database.
     */
    private void connectDatabase() {
        c = null;
        try {
            Class.forName("org.postgresql.Driver");
             this.c = DriverManager
                .getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME ,USER, PASSWORD);
          } catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().severe("No connection to database!"
                    + e.getClass().getName()+": " + e.getMessage());
            System.exit(0);
          }
    }

    /**
     * save anonymized video to database with metadata, related to an account
     * @param videoName name of the video file, which should be saved
     * @param metaName name of the meta data file, which should be saved
     * @return boolean to indicate success or failure
     */
    public boolean saveProcessedVideoAndMeta(String videoName, String metaName) {
        connectDatabase();
        // send sql command and catch possible exeptions
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "insert into \"video\" (user_id,video_name,meta_name) values (" + account.getId() + ",'" +
                    videoName + "','" + metaName + "');";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("Insert SQL command has not been executed successfully: " +
                    "Video was not saved in database: " + e);
        }
        return true;
    }

    /**
     * get the saved information for a video
     * @param videoId the id of the video
     * @return a VideoInfo object, where all information is in.
     */
    public VideoInfo getVideoInfo(int videoId) {
        VideoInfo vI = null;
        connectDatabase();
        // execute sql command and insert result in ArrayList
        try {
            Statement stmt = this.c.createStatement();
            ResultSet rs = stmt.executeQuery( "select \"video_name\",vid.\"id\" from \"video\" as vid  " +
                    "join \"user\" as usr ON vid.user_id=usr.id where usr.id='" + account.getId() + "' " +
                    "AND vid.\"id\"=" + videoId);
            // insert result in ArrayList
            while ( rs.next() ) {
                String video_name = rs.getString("video_name");
                int id = Integer.parseInt(rs.getString("id"));
                vI = new VideoInfo(id, video_name);
            }
            rs.close();
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("Select SQL command has not been executed successfully: " + e);
        }
        return vI;
    }

    /**
     * return an ArrayList, which includes all Videos of a user
     * @return ArrayList ov VideoInfo-Objects
     */
    public ArrayList<VideoInfo> getVideoInfoList() {
        // create ArrayList
        ArrayList<VideoInfo> videoInfoList= new ArrayList<>();
        // connect to database
        connectDatabase();
        // execute sql command and insert result in ArrayList
        try {
            Statement stmt = this.c.createStatement();
            ResultSet rs = stmt.executeQuery( "select \"video_name\",vid.\"id\" from \"video\" as vid  " +
                    "join \"user\" as usr ON vid.user_id=usr.id where usr.id='" + account.getId() + "'" );
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
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("SELECT SQL command has not been executed successfully: " + e);
        }
        return videoInfoList;
    }

    /**
     * delete the row with all infos for the video, but just in Database, the files are already existing...
     * @param videoId: the unique id of the video
     * @return a boolean, to review the success of the sql statement
     */
    public boolean deleteVideoAndMeta(int videoId) {
        connectDatabase();
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "DELETE from \"video\" where id=" + videoId + ";";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
            return true;
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("DELETE SQL command has not been executed successfully: There is a problem with the Video, maybe the id of the video: " + e);
        }
        return false;
    }

    /**
     * return metadata of a videoId
     * @param videoId: unique id of a video
     * @return a metadata-object
     */
    public Metadata getMetaData(int videoId){
        // create String, where meta file is stored
        //String filePath = LocationConfig.META_DIR + File.separator + getMetaNameByVideoId(videoId);
        String filePath = LocationConfig.TEST_RESOURCES_DIR + File.separator + "testData" + File.separator +
                getMetaNameByVideoId(videoId) + ".json";
        //read the json File into a String
        String metaJSON = "";
        // read Meta file to get infos
        try {
            metaJSON = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            Logger.getGlobal().warning("An IOException occurred in getMetaData. " +
                    "The filepath seems to be wrong: " + e);
        }
        // put String into JSON and save them into java variables
        JSONObject obj = new JSONObject(metaJSON);
        JSONObject meta = obj.getJSONObject("metaInfo");
        String date = meta.getString("date");
        String triggerType = meta.getString("triggerType");
        float gForceX = (float) meta.getDouble("gForceX");
        float gForceY = (float) meta.getDouble("gForceY");
        float gForceZ = (float) meta.getDouble("gForceZ");
        float[] gForce = {gForceX, gForceY, gForceZ};

        // return Metadata object
        return new Metadata(date, triggerType, gForce);
    }

    /**
     * changes the mail address of an account
     * @param newMail: new Mail address which will be set as new mail address
     * @return boolean, which symbolizes the success of the sql statement
     */
    public boolean setMail(String newMail) {
        // connect to database
        connectDatabase();
        // send sql command and catch possible exeptions
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "UPDATE \"user\" set mail='" + newMail + "' where id=" + account.getId() + ";";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("UPDATE SQL command has not been executed successfully: " + e);
        }
        return true;
    }

    /**
     * set new password for user
     * @param newPasswordHash: set this passwordHash as new password for this user
     * @return boolean if sql statement was successful
     */
    public boolean setPassword(String newPasswordHash) {
        // connect to database
        connectDatabase();
        // send sql command and catch possible exeptions
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "UPDATE \"user\" set password='" + newPasswordHash + "' where id=" + account.getId() + ";";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
            return true;
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("UPDATE SQL command has not been executed successfully: " + e);
        }
        return false;
    }

    /**
     * authenticate account (check, if password and mail are correct)
     * @return boolean true=successful auth, false=wrong passwd or mail
     */
    public boolean authenticate() {
        String mail = "";
        String passwordHash = "";
        // connect to database
        connectDatabase();
        // execute sql command and insert result in ArrayList
        try {
            Statement stmt = this.c.createStatement();

            ResultSet rs = stmt.executeQuery( "select \"mail\",\"password\" from \"user\" where id='" +
                    account.getId() + "'" );
            // insert result in ArrayList
            while ( rs.next() ) {
                mail = rs.getString("mail");
                passwordHash = rs.getString("password");
            }
            rs.close();
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("SELECT SQL command or get JSON variable seems to have a problem, " +
                    "check mail and password! " + e);
        }
        //return boolean, if password and mail are equal to database data
        return mail.equals(account.getMail()) && passwordHash.equals(account.getPasswordHash());

    }

    /**
     * delete user row
     * @return boolean that symbolizes success of deletion
     */
    public boolean deleteAccount() {
        connectDatabase();
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "delete from \"user\" where \"user\".\"id\"='" + account.getId() + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("DELTE SQL command has not been executed successfully. Check, if the account is existing! " + e);
        }
        return true;
    }

    /**
     * get the account id, which is saved in the database
     * @return int of account id, which is the database
     */
    public int getAccountId() {
        int accountId = -2;
        connectDatabase();
        try {
            Statement stmt = this.c.createStatement();
            ResultSet rs = stmt.executeQuery( "select \"id\" from \"user\" where \"user\".\"mail\"='" +
                    account.getMail() + "'");
            // insert result in ArrayList
            while ( rs.next() ) {
               accountId = Integer.parseInt(rs.getString("id"));
            }
            stmt.close();
            this.c.close();
        } catch (SQLException | NullPointerException e) {
            Logger.getGlobal().warning("getAccountId: SELECT SQL command or getting the JSON variable seems " +
                    "to have a problem. Check Account Mail Address! " + e);
            return -1;
        }
        return accountId;
    }

    /**
     * register an account. necessary data is the account object and the uuid
     * @param uuid is unique and is for verification process
     * @return boolean, which symbolizes success of sql statement
     */
    public boolean register(String uuid) {
        connectDatabase();
        // send sql command and catch possible exeptions
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "insert into \"user\" (mail,password,uuid,verified) values ('" + account.getMail() + "','" +
                    account.getPasswordHash() + "'," + uuid + ", false);";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("Account registration has occurred a problem. check uuid, " +
                    "mail and passwordHash! " + e);
            return false;
        }
        return true;
    }

    /**
     * verifies an account, compare uuid of database and uuid of url
     * @param uuid is the uuid, which is in the link of the verification mail
     * @return String of success
     */
    public boolean verifyAccount(String uuid) {
        //connect to Database
        connectDatabase();
        // get uuid from account
        String uuidDatabase = "";
        try {
            Statement stmt = this.c.createStatement();

            ResultSet rs = stmt.executeQuery( "select \"uuid\" from \"user\" as usr  where usr.id='" +
                    account.getId() + "'" );
            // insert result in ArrayList
            if (rs != null && rs.next()) {
                uuidDatabase= rs.getString("uuid");
            }
            rs.close();
            stmt.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("verifyAccount occurred a problem in storing uuid temporarily: " + e);
        }
        if (uuidDatabase.equals(uuid)) {
            try {
                Statement stmt = this.c.createStatement();
                stmt.executeUpdate("update \"user\" set verified=TRUE where id=" + account.getId() + ";");
                stmt.close();
                this.c.close();
                return true;
            } catch (NullPointerException | SQLException e) {
                Logger.getGlobal().severe("verifyAccount occurred a problem in updating account to isVerified! "
                        + e);
            }
            return false;
        } else {
            Logger.getGlobal().warning("verifyAccount: uuid not like uuid in database!");
            // close c, because when if=true, c is needed
            try {
                this.c.close();
            } catch (SQLException sqlE) {
                Logger.getGlobal().warning("verifyAccount: Connection c cannot be closed! " + sqlE);
            }
            return false;
        }
    }

    /**
     * check, if the value "verified" in table "user" is true or false
     * @return value "verified" in table "user"
     */
    public boolean isVerified() {
        // connect to Database
        connectDatabase();
        boolean verified = false;
        try {
            Statement stmt = this.c.createStatement();

            ResultSet rs = stmt.executeQuery("select \"verified\" from \"user\" where id=" +
                    account.getId() + ";");
            while(rs.next()) {
                verified = rs.getBoolean("verified");
            }
            rs.close();
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("isVerified occurred a problem. SELECT command has thrown an exception! "
                    + e);
        }
        return verified;
    }

    // getter/setter

    public int getVideoIdByName(String video_name) {
        connectDatabase();
        int id = -1;
        try {
            Statement stmt = this.c.createStatement();

            ResultSet rs = stmt.executeQuery("select id from \"video\" where \"video_name\"='" +
                    video_name + "'");
            // insert result in ArrayList
            while (rs.next()) {
                id = Integer.parseInt(rs.getString("id"));
            }
            rs.close();
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("getVideoIdByName: SELECT SQL command or " +
                    "storing column variable of database occurred a problem. " + e);
        }
        return id;
    }

    /**
     * get the name of the metadata file
     * @param videoId: to get the related video to the metadata
     * @return String of metadata name
     */
    //TODO: can be private, but for testing it is public
    public String getMetaNameByVideoId(int videoId) {
        //connect to database
        connectDatabase();
        String meta = "";
        try {
            Statement stmt = this.c.createStatement();

			ResultSet rs = stmt.executeQuery("select \"meta_name\" from \"video\" as vid where vid.id=" +
                    videoId + ";");
			// insert result in ArrayList
            if (rs.next()) {
                meta = rs.getString("meta_name");
            }
			rs.close();
			stmt.close();
			this.c.close();
		} catch (NullPointerException | SQLException e) {
	        Logger.getGlobal().severe("getMetaNameByVideoId: SELECT SQL command or " +
                    "storing column variable of database occurred a problem. " + e);
		}
		return meta;
    }
}
