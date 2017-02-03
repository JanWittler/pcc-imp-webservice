package edu.kit.informatik.pcc.service.data;

import org.json.JSONObject;

/**
 * Datacontainer for information concerning user accounts.
 *
 * @author Fabian Wenzel, David Laubenstein, Josh Romanowski
 */
public class Account {

    // JSON keys
    private static final String JSON_KEY_MAIL = "mail";
    private static final String JSON_KEY_PASSWORD = "password";

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
        this.passwordHash = hashPassword(account.getString(JSON_KEY_PASSWORD));
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * hashes a password with a function
     *
     * @param password password in plain text
     * @return hashed password
     */
    private String hashPassword(String password) {
        //TODO: hashPassword
        return password;
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

    public String getMail() {
        return mail;
    }
}
