package edu.kit.informatik.pcc.service.data;

import org.json.JSONObject;

/**
 * @author Fabian Wenzel, David Laubenstein, Josh Romanowski
 */
public class Account {

    // JSON keys
    private static final String JSON_KEY_MAIL = "mail";
    private static final String JSON_KEY_PASSWORD = "password";

    // attributes

    private String mail;
    private String passwordHash;
    private int id;

    // constructors

    /**
     * Takes the json string and parses the attributes from it.
     *
     * @param json which includes account information
     */
    public Account(String json) {
        JSONObject account = new JSONObject(json);
        this.mail = account.getString(JSON_KEY_MAIL);
        this.passwordHash = hashPassword(account.getString(JSON_KEY_PASSWORD));
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

    public void setId(int id) {
        this.id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getMail() {
        return mail;
    }
}
