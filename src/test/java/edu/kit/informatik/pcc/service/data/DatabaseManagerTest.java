package edu.kit.informatik.pcc.service.data;

import org.junit.*;

/**
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
    }

    @Test
    public void register() throws Exception {
        dm.register("123345522367");
        //Assert.assertTrue(dm.authenticate());
    }

    @After
    public void cleanUpAfter() {
        dm.deleteAccount();
    }

    @AfterClass
    public static void cleanUpAfterClass() {

    }
}
