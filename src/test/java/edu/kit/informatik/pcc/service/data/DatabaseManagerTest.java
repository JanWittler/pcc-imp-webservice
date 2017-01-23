package edu.kit.informatik.pcc.service.data;

import org.junit.*;

import java.util.ArrayList;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/18/17.
 */
public class DatabaseManagerTest {
    private Account account;
    private DatabaseManager dm;
    private final String OWN_UUID = "102394871234";

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @Before
    public void setUpBefore() {
        String MAIL = "\"testEMAIL@123schonVorbei.com\"";
        String PASSWORD = "\"testPasswordForUni\"";
        String json = "{\n" +
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
        String videoName1 = "videoGetVideoInfoListTest";
        String videoName2 = "video2GetVideoInfoListTest";
        String metaName1 = "meta1GetVideoInfoListTest";
        String metaName2 = "meta2GetVideoInfoListTest";
        boolean check1 = false;
        boolean check2= false;
        // create 2 testVideos
        dm.saveProcessedVideoAndMeta(videoName1, metaName1);
        dm.saveProcessedVideoAndMeta(videoName2, metaName2);

        // save List in ArrayList<VideoInfo>
        ArrayList<VideoInfo> arrayList= dm.getVideoInfoList();
        // check, if both videos are in list
        for (VideoInfo listElement : arrayList) {
            if (listElement.getName().equals(videoName1) && listElement.getVideoId() == dm.getVideoIdByName(videoName1)) {
                check1 = true;
            }
            if (listElement.getName().equals(videoName2) && listElement.getVideoId() == dm.getVideoIdByName(videoName2)) {
                check2 = true;
            }
        }
        // if length of ArrayList = 2 and both videos are found, test success
        Assert.assertTrue(check1 && check2 && arrayList.size() == 2);
        // delete both videos to delete Account
        dm.deleteVideoAndMeta(dm.getVideoIdByName(videoName1));
        dm.deleteVideoAndMeta(dm.getVideoIdByName(videoName2));
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
        String videoName = "videoTestGETMETADATA";
        String metaName = "metaTestGETMETADATA";
        //TODO: write metadataFile to analyze
        dm.saveProcessedVideoAndMeta(videoName, metaName);
        Metadata md = dm.getMetaData(dm.getVideoIdByName(videoName));
        //System.out.println(md.getMetaName() + md.getDate());
        //boolean check = (md.getMetaName().equals("") & md.getDate().equals("") & md.getgForce().equals(""));
        //Assert.assertTrue(check);
        dm.deleteVideoAndMeta(dm.getVideoIdByName(videoName));
    }

    @Test
    public void getMetaNameByVideoId() {
        String videoName = "videoGetMetaNameByVideoId";
        String metaName = "metaGetMetaNameByVideoId";
        // save video in database
        dm.saveProcessedVideoAndMeta(videoName, metaName);
        // if metaName equals the info in database, test passes
        Assert.assertTrue(dm.getMetaNameByVideoId(dm.getVideoIdByName(videoName)).equals(metaName));
        // delete Video, so that the account can be deleted
        dm.deleteVideoAndMeta(dm.getVideoIdByName(videoName));
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
