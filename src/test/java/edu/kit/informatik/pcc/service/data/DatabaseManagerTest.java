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
    private final String OWN_UUID = "102394871234";
    private final String MAIL = "\"testEMAIL@123schonVorbei.com\"";
    private final String PASSWORD = "\"testPasswordForUni\"";

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @Before
    public void setUpBefore() {
        json = "{\n" +
                "  \"accountData\": {\n" +
                "    \"mail\": " + MAIL + ",\n" +
                "    \"password\": " + PASSWORD + "\n" +
                "  }\n" +
                "}";
        account = new Account(json);
        dm = new DatabaseManager(account);
        dm.register(OWN_UUID);
        account.setId(dm.getAccountId());
    }

    @Test
    public void registerTest() {
        // already registered in beforeTest
        dm.verifyAccount(OWN_UUID);
        Assert.assertTrue(dm.isVerified());
    }

    @Test
    public void getAccountIdTest() {
        Assert.assertEquals("account.getId() is not equals dm.getAccountId()", account.getId(), dm.getAccountId());
    }

    @Test
    public void isVerifiedTest() {
       Assert.assertTrue(!dm.isVerified());
       //TODO: if isVerified and verifyAccount works, uncomment this downside
       dm.verifyAccount(OWN_UUID);
       Assert.assertTrue(dm.isVerified());
    }

    @Test
    public void saveProcessedVideoAndMetaTest() {
        //TODO: write test
        String videoName = "videoTest123";
        String metaName = "metaTest123";
        // save video
        dm.saveProcessedVideoAndMeta(videoName, metaName);
        // getVideoInfo
        VideoInfo vI = dm.getVideoInfo(dm.getVideoIdByName(videoName));
        Assert.assertTrue(vI.getName().equals(videoName));
        // getMetaInfo
        Metadata mD = dm.getMetaData(dm.getVideoIdByName(videoName));
        Assert.assertTrue(mD.getMetaName().equals(metaName));
        // delete Video
        dm.deleteVideoAndMeta(dm.getVideoIdByName(videoName));
    }

    @Test
    public void getVideoInfoTest() {
        //TODO: write test
        dm.saveProcessedVideoAndMeta("videoTest123321","metaTest123");
        VideoInfo vI = dm.getVideoInfo(dm.getVideoIdByName("videoTest123321"));
        Assert.assertTrue(vI.getName().equals("videoTest123321"));
        Assert.assertTrue(vI.getVideoId() == dm.getVideoIdByName("videoTest123321"));
    }

    @Test
    public void getVideoIdByNameTest() {
        dm.saveProcessedVideoAndMeta("getVideoIdByNameTestVIDEO", "getVideoIdByNameTestMETA");
        Assert.assertTrue(dm.deleteVideoAndMeta(dm.getVideoIdByName("getVideoIdByNameTestVIDEO")));
    }

    @Test
    public void getVideoInfoListTest() {
        //TODO: write test
    }

    @Test
    public void deleteVideoAndMetaTest() {
        //TODO: write test
        boolean create  = dm.saveProcessedVideoAndMeta("test","test");
        boolean delete = dm.deleteVideoAndMeta(dm.getVideoIdByName("test"));
        Assert.assertTrue("insert video or delete Video not working!", create && delete);
    }

    @Test
    public void getMetaDataTest() {
        //TODO: write metadataFile to analyze
        dm.saveProcessedVideoAndMeta("videoTestGETMETADATA", "metaTestGETMETADATA");
        //TODO: change videoId
        Metadata md = dm.getMetaData(dm.getVideoIdByName("videoTestGETMETADATA"));
        System.out.println(md.getMetaName() + md.getDate());
        //boolean check = (md.getMetaName().equals("") & md.getDate().equals("") & md.getgForce().equals(""));
        //Assert.assertTrue(check);
    }

    @Test
    public void getMetaNameByVideoId() {
        //TODO: write method
    }

    @Test
    public void setMailTest() {
        Assert.assertTrue(dm.setMail("newTESTMAIL@hlminop.de"));
    }

    @Test
    public void setPasswordTest() {
       Assert.assertTrue(dm.setPassword("passwordTestabc"));
    }

    @Test
    public void deleteAccountTest() {
        Assert.assertTrue(dm.deleteAccount());
    }

    @Test
    public void verifyAccountTest() {
        //TODO: write test
        Assert.assertTrue(!dm.isVerified());
        dm.verifyAccount(OWN_UUID);
        Assert.assertTrue(dm.isVerified());
    }

    @Test
    public void authenticateTest() {
        Assert.assertTrue(dm.authenticate());
    }
    @After
    public void cleanUpAfter() {
        dm.deleteAccount();
        dm = null;
    }

    @AfterClass
    public static void cleanUpAfterClass() {

    }
}
