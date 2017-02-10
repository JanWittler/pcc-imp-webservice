package edu.kit.informatik.pcc.service.data;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/**
 * Datacontainer for information concerning user accounts.
 *
 * @author Fabian Wenzel, David Laubenstein, Josh Romanowski
 */
public class Account {

    // JSON keys
    private static final String JSON_KEY_MAIL = "mail";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * E-mail address of the account
     */
    private String mail;

    /**
     * Password of the account.
     */
    private String passwordHash;

    /**
     * Unique identifier for each account, maps to the one in the database.
     */
    private int id;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Takes the json string and parses the attributes from it.
     *
     * @param json JSON string which includes account information.
     */
    public Account(String json) {
        JSONObject account = new JSONObject(json);
        this.mail = account.getString(JSON_KEY_MAIL);
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * hashes a password with a function and given salt
     *
     * @param password password in plain text
     * @return hashed password
     */
    private String hashPassword(String password, byte[] salt) {

        String generatedPassword = password;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
           // md.update(salt);
            //Get the hash's bytes
            byte[] bytes = md.digest(generatedPassword.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


    /* #############################################################################################
     *                                  getter/setter
     * ###########################################################################################*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String json, byte[] salt) {
        JSONObject account = new JSONObject(json);
        String password = account.getString("password");
        this.passwordHash = hashPassword(password, salt);
    }

    public String getMail() {
        return mail;
    }
}
