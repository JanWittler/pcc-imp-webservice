package edu.kit.informatik.pcc.service.data;

import edu.kit.informatik.pcc.service.server.Main;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class handles all low-level database queries
 *
 * @author David Laubenstein
 * Created by David Laubenstein at 1/18/17
 */
public class DatabaseManager {

	private static final String PORT = "5432";
	private static final String HOST = "localhost";
	private static final String DB_NAME = "PrivacyCrashCam";
    private static final String USER = "postgres";
    private static final String PASSWORD = "";
    // attributes
    private Account account;
    private Connection c = null;
    // constructors

    /**
     * Constructor, which includes the {@link Account} object in the {@link DatabaseManager}.
     * The {@link Account} object saves important information about the user.
     *
     * @param account to have access to the actual account.
     */
    public DatabaseManager(Account account) {
        // create access to account
        this.account = account;
    }
    // methods

    /**
     * This method is used to open a connection to the database with the class attributes.
     * If the connection fails, the
     *
     * <p>
     *     IMPORTANT: A Connection c will be opened, but not closed. After calling this method,
     *     you have to close the connection with <b>this.c.close()</b>
     * </p>
     */
    private boolean connectDatabase() {
        c = null;
        try {
            Class.forName("org.postgresql.Driver");
             this.c = DriverManager
                .getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME ,USER, PASSWORD);
          } catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().severe("No connection to database!");
            Main.stopServer();
            return false;
          }
          return true;
    }

    /**
     * Save Video and Metadata to database, related to an account
     * This is not the real file or the path to the file. This is only the name of the
     * video file and metadata file as a String
     *
     * @param videoName name of the video file, which should be saved
     * @param metaName name of the meta data file, which should be saved
     * @return boolean to indicate success or failure
     */
    public boolean saveProcessedVideoAndMeta(String videoName, String metaName) {
        if(!connectDatabase()) return false;
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
                    "Video was not saved in database!");
        }
        return true;
    }

    /**
     * Get the saved information for a video as a {@link VideoInfo} object.
     * The database command selects the video_name and the id of the video from the
     * table <b>video</b> and creates a {@link VideoInfo} object with this information
     *
     * @param videoId is the id of the video the information should be collected
     * @return a VideoInfo object, where all information are saved in the database
     */
    public VideoInfo getVideoInfo(int videoId) {
        VideoInfo vI = null;
        if(!connectDatabase()) return null;
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
            Logger.getGlobal().warning("Select SQL command has not been executed successfully: ");
        }
        return vI;
    }

    /**
     * Get all videos of a user packed in {@link VideoInfo} objects
     * The SQL command collects all video_name and id's of the videos and store them into an ArrayList of
     * {@link VideoInfo} objects.
     *
     * @return all information for the videos of a user
     */
    public ArrayList<VideoInfo> getVideoInfoList() {
        // create ArrayList
        ArrayList<VideoInfo> videoInfoList= new ArrayList<>();
        // connect to database
        if(!connectDatabase()) return null;
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
            Logger.getGlobal().warning("SELECT SQL command has not been executed successfully: ");
        }
        return videoInfoList;
    }

    /**
     * Delete the row with all information for the video in the table <b>video</b>
     * But the deletion is just in the database, the files are already existing.
     *
     * <p><b>
     *     You have to delete the videos before you call this method,
     *     so you are not able to find the video anymore
     * </b></p>
     *
     * @param videoId: the <b>unique</b> id of the video
     * @return a boolean, to review the success of the sql statement
     */
    public boolean deleteVideoAndMeta(int videoId) {
        if(!connectDatabase()) return false;
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "DELETE from \"video\" where id=" + videoId + ";";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
            return true;
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("DELETE SQL command has not been executed successfully: There is a problem with the Video, maybe the id of the video: ");
        }
        return false;
    }

    /**
     * Get the metadata of a video as a {@link Metadata} object.
     * <p>
     *      The SQL command search for the id of the video and get the name of the metadata File.
     *      Then the path will be merged by the path to the folder, where all metadata are saved, the name of the
     *      metadata file and the extension of the file.
     *      With this path the metadata File will be read out and stored in a {@link Metadata} object.
     * </p>
     *
     * @param videoId: unique id of a video
     * @return a metadata-object
     */
    public Metadata getMetaData(int videoId){
        // create String, where meta file is stored
        String filePath = LocationConfig.META_DIR + File.separator +
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
        JSONObject meta = obj.getJSONObject("metadata");
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
     * Changes the mail address of an account
     *
     * <p>
     *     Check, if the mail is not given to another user, because the column "mail" is unique.
     *     If the mail is not assigned to another account, the new mail will be set
     * </p>
     *
     * @param newMail: mail address, which should be updated in the account, if the mail is not assigned to another
     *               account
     * @return if the mail address was changed
     */
    public boolean setMail(String newMail) {
        // connect to database
        if(!connectDatabase()) return false;
        // send sql command and catch possible exeptions
        try {
            Statement stmt = this.c.createStatement();
            // if mail is not given to another account, execute sql command
            if (!isMailExisting(newMail)) {
                String sql = "UPDATE \"user\" set mail='" + newMail + "' where id=" + account.getId() + ";";
                stmt.executeUpdate(sql);
                stmt.close();
                this.c.close();
                return true;
            } else {
                Logger.getGlobal().warning("The mail address you want to update is already stored" +
                        "in another user account. Please choose another mail address");
            }
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("UPDATE SQL command has not been executed successfully: ");
        }
        return false;
    }

    /**
     * Set new password for user in the database
     *
     * @param newPasswordHash: set this passwordHash as new password for this user
     * @return if setting password was successful
     */
    public boolean setPassword(String newPasswordHash) {
        // connect to database
        if(!connectDatabase()) return false;
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
            Logger.getGlobal().severe("Failed to set new password");
        }
        return false;
    }

    /**
     * Authenticate account (check, if password and mail are correct)
     *
     * @return if the account information (mail, password) are the same as in the database
     */
    public boolean authenticate() {
        String mail = "";
        String passwordHash = "";
        // connect to database
        if(!connectDatabase()) return false;
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
     * Deletes account of the user
     *
     * @return success of user deletion
     */
    public boolean deleteAccount() {
        if(!connectDatabase()) return false;
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
     * Get the account id, which is saved in the database
     *
     * @return account id as integer from the database
     */
    public int getAccountId() {
        int accountId = -1;
        if(!connectDatabase()) return -1;
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
                    "to have a problem. Check Account Mail Address! ");
            return -1;
        }
        return accountId;
    }

    /**
     * Registers an account.
     * Necessary data is fetched in the account object and the uuid
     *
     * @param uuid is unique and is for verification process
     * @return success of registration
     */
    public boolean register(String uuid) {
        if(!connectDatabase()) return false;
        // send sql command and catch possible exeptions
        try {
            Statement stmt = this.c.createStatement();
            // sql command
            String sql = "insert into \"user\" (mail,password,uuid,verified) values ('" + account.getMail() + "','" +
                    account.getPasswordHash() + "','" + uuid + "', false);";
            stmt.executeUpdate(sql);
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("Account registration has occurred a problem. check uuid, " +
                    "mail and passwordHash! ");
            return false;
        }
        return true;
    }

    /**
     * Verifies an account, compares uuid of database and uuid of url
     *
     * @param uuid is the uuid, which is in the link of the verification mail
     * @return String of success
     */
    public boolean verifyAccount(String uuid) {
        //connect to Database
        if(!connectDatabase()) return false;
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
            Logger.getGlobal().warning("verifyAccount occurred a problem in storing uuid temporarily: ");
        }
        if (uuidDatabase.equals(uuid)) {
            try {
                Statement stmt = this.c.createStatement();
                stmt.executeUpdate("update \"user\" set verified=TRUE where id=" + account.getId() + ";");
                stmt.close();
                this.c.close();
                return true;
            } catch (NullPointerException | SQLException e) {
                Logger.getGlobal().severe("verifyAccount occurred a problem in updating account to isVerified! ");
            }
            return false;
        } else {
            Logger.getGlobal().warning("verifyAccount: uuid not like uuid in database!");
            // close c, because when if=true, c is needed
            try {
                this.c.close();
            } catch (SQLException sqlE) {
                Logger.getGlobal().warning("verifyAccount: Connection c cannot be closed! ");
            }
            return false;
        }
    }

    /**
     * check, if the value "verified" in table "user" is true or false
     *
     * @return value "verified" in table "user"
     */
    public boolean isVerified() {
        // connect to Database
        if(!connectDatabase()) return false;
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
        if(!connectDatabase()) return -1;
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
     * Get the name of the metadata file, which will be searched by the id of the video
     *
     * @param videoId: to get the related video to the metadata
     * @return String of metadata name
     */
    public String getMetaNameByVideoId(int videoId) {
        //connect to database
        if(!connectDatabase()) return null;
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

    /**
     * check, if a mail address is already saved in database table <b>user</b>
     *
     * @param mail is the mail, which will be checked
     * @return if mail exists in database
     */
    protected boolean isMailExisting(String mail) {
        //connect to database
        if(!connectDatabase()) return false;
        int count_mail = 0;
        try {
            Statement stmt = this.c.createStatement();

            ResultSet rs = stmt.executeQuery("select count(mail) from \"user\" where mail='" + mail + "';");
            // insert result in ArrayList
            if (rs.next()) {
                count_mail = rs.getInt("count");
            }
            rs.close();
            stmt.close();
            this.c.close();
        } catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("getMetaNameByVideoId: SELECT SQL command or " +
                    "storing column variable of database occurred a problem. " + e);
        }
        if (count_mail == 0) {
            return false;
        } else {
            return true;
        }
    }
}
