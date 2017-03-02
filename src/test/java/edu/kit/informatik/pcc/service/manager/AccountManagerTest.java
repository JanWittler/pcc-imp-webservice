package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;

/**
 * @author Fabian Wenzel
 *         Created by Fabian Wenzel on 12.02.2017.
 */
public class AccountManagerTest {
    //TEST STUFF

    private String accountJson;
    private Account account;
    private DatabaseManager databaseManager;
    private AccountManager accountManager;

    private String uuid = "456-sgdfgd3t5g-345fs";
    private String registerSalt = "zgbnjiu7ztgfvbnjiu7667uijhgt";

    // test password hashing
    // test e-mail validation
    @Before
    public void setUp() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mail", "fabiistkrass@gmail.de");
        jsonObject.put("password", "yochilldeinlife");
        accountJson = jsonObject.toString();

        account = new Account(accountJson);
        accountManager = new AccountManager(account);
        databaseManager = new DatabaseManager(account);
        databaseManager.register(uuid, registerSalt);
        account.setId(databaseManager.getAccountId());
    }

    @Test
    public void createSaltTest() {
        byte[] salt = accountManager.getSalt();
        String saltString = Base64.getEncoder().encodeToString(salt);
        Assert.assertTrue(saltString.equals(registerSalt));
    }

    @Test
    public void changeTest() {

    }

    @Test
    public void registerTest() {

    }

    @Test
    public void deleteTest() {

    }
    @Test
    public void accountIdTest() {

    }

    @Test
    public void authenticateTest() {

    }
    @Test
    public void verifyTest() {

    }

    @Test
    public void isVerifiedTest() {

    }


    @After
    public void cleanUp() {
        databaseManager.deleteAccount();
    }
}
