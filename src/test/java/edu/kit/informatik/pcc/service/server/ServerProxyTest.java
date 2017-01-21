package edu.kit.informatik.pcc.service.server;

import org.junit.*;

/**
 *
 * @author Fabian Wenzel
 * Created by Fabi on 20.01.2017.
 */
public class ServerProxyTest {
    private ServerProxy serverProxy;
    private String validJson = "{\n" +
            "  \"accountData\": {\n" +
            "    \"mail\": \"testasdfasdf@example.com\",\n" +
            "    \"password\": \"123123\"\n" +
            "  }\n" +
            "}";
    private String noAccountIdJson = "{\n" +
            "  \"accountData\": {\n" +
            "    \"mail\": \"blaaaaf@example.com\",\n" +
            "    \"password\": \"123123\"\n" +
            "  }\n" +
            "}";
    private String wrongPasswordJson = "{\n" +
            "  \"accountData\": {\n" +
            "    \"mail\": \"testasdfasdf@example.com\",\n" +
            "    \"password\": \"123124\"\n" +
            "  }\n" +
            "}";

    @Before
    public void setUp() {
        serverProxy = new ServerProxy();
    }

    @org.junit.Test
    public void validTest() {
        String status = serverProxy.authenticateAccount(validJson);
        Assert.assertTrue(status.equals("SUCCESS"));
    }

    @org.junit.Test
    public void NoAccountIdTest() {
        String status = serverProxy.authenticateAccount(noAccountIdJson);
        Assert.assertTrue(status.equals("NO ACCOUNTID"));
    }

    @org.junit.Test
    public void wrongPasswordTest() {
        String status = serverProxy.authenticateAccount(wrongPasswordJson);
        Assert.assertTrue(status.equals("WRONG PASSWORD"));
    }

}
