package edu.kit.informatik.pcc.service.data;

import org.junit.*;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/18/17.
 */
public class DatabaseManagerTest {
    private Account account;
    private DatabaseManager dm;
    private String json;

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @Before
    public void setUpBefore() {
        json = "{\n" +
                "  \"accountData\": {\n" +
                "    \"mail\": \"testasdfasdf@example.com\",\n" +
                "    \"password\": \"123123\"\n" +
                "  }\n" +
                "}";
        account = new Account(json);
        dm = new DatabaseManager(account);
        dm.register("123345522367");
        account.setId(dm.getAccountId());
    }

    @Test
    public void register() throws Exception {
        // already registered in beforeTest
        System.out.println("account id: " + account.getId());
        dm.verifyAccount("123345522367");
        Assert.assertTrue(dm.isVerified());
    }

    @Test
    public void getAccountId() {
        Assert.assertEquals("account.getId() is not equals dm.getAccountId()", account.getId(), dm.getAccountId());
    }

    @Test
    public void isVerified() {
       Assert.assertTrue(!dm.isVerified());
       //TODO: if isVerified and verifyAccount works, uncomment this downside
       //dm.verifyAccount("123345522367");
       //Assert.assertTrue(dm.isVerified());
    }

    @Test
    public void saveProcessedVideoAndMeta() {
        //TODO: write test
    }

    @Test
    public void getVideoInfo() {
        //TODO: write test
    }

    @Test
    public void getVideoInfoList() {
        //TODO: write test
    }

    @Test
    public void deleteVideoAndMeta() {
        //TODO: write test
    }

    @Test
    public void getMetaData() {
        //TODO: write test
    }

    @Test
    public void setMail() {
        //TODO: write test
    }

    @Test
    public void setPassword() {
        //TODO: write test
    }

    @Test
    public void deleteAccount() {
        Assert.assertTrue(dm.deleteAccount());
    }

    @Test
    public void authenticate() {
        //TODO: write test
    }
    @After
    public void cleanUpAfter() {
        dm.deleteAccount();
        account = null;
    }

    @AfterClass
    public static void cleanUpAfterClass() {

    }
}
