package edu.kit.informatik.pcc.service.data;

import org.json.JSONObject;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Fabian Wenzel, David Laubenstein
 *         Created by David Laubenstein at 17.01.2017
 */
public class Account {
    // attributes
    private String mail;
    private String passwordHash;
    private int id;
    // constructors

    /**
     * constructor, which relates the json string to the class variables
     *
     * @param json which includes account information
     */
    public Account(String json) {
    	//TODO: PARSE JSON CORRECTLY ?
        // convert json String to class attributes
        // create JSON Object
        JSONObject obj = new JSONObject(json);
        // go into account object
        JSONObject account = obj.getJSONObject("account");
        // save Strings in account object to class attributes

        this.mail = account.getString("mail");
		this.passwordHash = hashPassword(account.getString("password"));
	}

    // methods

    /**
     * hashes a password with a function
     *
     * @param password password in plain text
     * @return: hashed password
     */
    public String hashPassword(String password) {
        //TODO: hashPassword
        return password;
    }


    // getter/setter
    public int getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getMail() {
        return mail;
    }

    public void setId(int id) {
        this.id = id;
    }
}
