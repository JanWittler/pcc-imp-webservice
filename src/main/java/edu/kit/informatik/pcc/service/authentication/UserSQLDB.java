package edu.kit.informatik.pcc.service.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.logging.Logger;

import edu.kit.informatik.pcc.service.server.Main;

public class UserSQLDB implements IUserDB, IUserSessionDB {
	// database constants
    private static final String PORT = "5432";
    private static final String HOST = "localhost";
    private static final String DB_NAME = "privacycrashcam";
    private static final String USER = "postgres";
    private static final String PASSWORD = "pccdata";

	@Override
	public void createUser(String email, String password) {
		int userId = unusedUserId();
		if (userId == IUserIdProvider.invalidId) {
			return;
		}
		byte[] salt = createSalt();
		String hashedPassword = hashPassword(password, salt);
		String saltString = Base64.getEncoder().encodeToString(salt);
		
		Connection connection = connectToDatabase();
		if (connection == null) {
			return;
		}
		try {
			Statement stmt = connection.createStatement();
			String sql = "insert into \"user\" (mail,password,uuid,verified,password_salt) values ('" + 
			email + "','" + hashedPassword + "','" + userId + "', false, '" + saltString + "' );";
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		}
		catch (SQLException e) {
			Logger.getGlobal().warning("Creating account in database failed");
		}
	}

	@Override
	public int getUserIdByMail(String email) {
		int userId = IUserIdProvider.invalidId;
		Connection connection = connectToDatabase();
		if (connection == null) {
			return userId;
		}
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select \"id\" from \"user\" where \"user\".\"mail\"='" +
                    email + "'");
            if (rs.next()) {
            	userId = rs.getInt("id");
            }
            stmt.close();
            connection.close();
        } 
        catch (SQLException | NullPointerException e) {
            Logger.getGlobal().warning("Retrieving account id from database failed");
            return IUserIdProvider.invalidId;
        }
        return userId;
	}

	@Override
	public Boolean validatePassword(int userId, String password) {
		Connection connection = connectToDatabase();
		if (connection == null) {
			return false;
		}
        String saltString = null;
        String passwordHash = null;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select \"password_salt\",\"password\" from \"user\" where \"user\".\"id\"=" +
                    userId + ";");
            if (rs.next()) {
                saltString = rs.getString("password_salt");
                passwordHash = rs.getString("password");
            }
            rs.close();
            stmt.close();
            connection.close();
        } 
        catch (NullPointerException | SQLException e) {
            Logger.getGlobal().severe("Validating password with database failed");
            return false;
        }
        
        if (saltString == null || passwordHash == null) {
        	return false;
        }
        byte[] salt = Base64.getDecoder().decode(saltString);
        return hashPassword(password, salt).equals(passwordHash);
	}

	@Override
	public void deleteAccount(int userId) {
		Connection connection = connectToDatabase();
		if (connection == null) {
			return;
		}
        try {
            Statement stmt = connection.createStatement();
            String sql = "delete from \"user\" where \"user\".\"id\"='" + userId + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();
        } 
        catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("Error while deleting account in database");
        }
	}
	
	@Override
	public int getUserId(String authenticationToken) {
		int userId = IUserIdProvider.invalidId;
		Connection connection = connectToDatabase();
		if (connection == null) {
			return userId;
		}
		try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select \"id\" from \"sessions\" where \"sessions\".\"token\"='" +
                    authenticationToken + "'");
            if (rs.next()) {
                userId = rs.getInt("id");
            }
            stmt.close();
            connection.close();
        } 
        catch (SQLException | NullPointerException e) {
            Logger.getGlobal().warning("Retrieving account id from database failed");
            return IUserIdProvider.invalidId;
        }
        return userId;
	}

	@Override
	public void storeAuthenticationToken(String authenticationToken, int userId) {
		Connection connection = connectToDatabase();
		if (connection == null) {
			return;
		}
		try {
			Statement stmt = connection.createStatement();
			String sql = "insert into \"sessions\" (id,token) values ('" + 
			userId + "','" + authenticationToken + "' );";
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		}
		catch (SQLException e) {
			Logger.getGlobal().warning("Storing token to database failed");
		}
	}

	@Override
	public void deleteTokensForUserId(int userId) {
		Connection connection = connectToDatabase();
		if (connection == null) {
			return;
		}
        try {
            Statement stmt = connection.createStatement();
            String sql = "delete from \"sessions\" where \"sessions\".\"id\"='" + userId + "'";
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();
        } 
        catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("Error while deleting tokens in database");
        }
	}
	
	/**
     * This method is used to open a connection to the database with the class attributes.
     * If the connection fails, the server is shut down.
     * <p>
     * IMPORTANT: A Connection will be opened, but not closed. After calling this method,
     * you have to close the returned connection.
     * </p>
     */
    private Connection connectToDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME, USER, PASSWORD);
        } 
        catch (Exception e) {
            e.printStackTrace();
            Logger.getGlobal().severe("No connection to database!");
            Main.stopServer();
            return null;
        }
    }
    
    private byte[] createSalt () {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } 
        catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Logger.getGlobal().warning("An error occurred getting a secure random instance!");
            return null;
        }
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        return salt;
    }
    
    private String hashPassword(String password, byte[] salt) {
    	if (salt == null || password == null || password.length() == 0) {
            return null;
        }
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes
            byte[] bytes = md.digest(password.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    private int unusedUserId() {
    	int userId = IUserIdProvider.invalidId;
    	Connection connection = connectToDatabase();
		if (connection == null) {
			return userId;
		}
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select max(\"id\") from \"user\"'");
            if (rs.next()) {
                userId = rs.getInt("id") + 1;
            }
            else {
            	userId = 1;
            }
            rs.close();
            stmt.close();
            connection.close();
        } 
        catch (NullPointerException | SQLException e) {
            Logger.getGlobal().warning("Error while deleting account in database");
            return IUserIdProvider.invalidId;
        }
        return userId;
    }
}
