package edu.kit.informatik.pcc.service.manager;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import static edu.kit.informatik.pcc.service.server.ServerProxyTest.setFinalStatic;

/**
 * @author Fabian Wenzel
 *         Created by Fabian Wenzel on 12.02.2017.
 */
public class VideoManagerTest {
    // status message constants
    private final String SUCCESS = "SUCCESS";
    private final String FAILURE = "FAILURE";

    private String accountJson;
    private Account account;
    private DatabaseManager databaseManager;
    private VideoManager videoManager;

    private String uuid = "456-sgdfgd3t5g-345fs";
    private byte[] registerSalt = new byte[16];

    @Before
    public void setUp() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mail", "fabiistkrass@gmail.de");
        jsonObject.put("password", "yochilldeinlife");
        accountJson = jsonObject.toString();

        //setup account (registered)
        account = new Account(accountJson);
        databaseManager = new DatabaseManager(account);
        videoManager = new VideoManager(account);
        String saltString = Base64.getEncoder().encodeToString(registerSalt);
        databaseManager.register(uuid, saltString);
        account.setId(databaseManager.getAccountId());
        databaseManager.verifyAccount(uuid);
        databaseManager.saveProcessedVideoAndMeta("input3", "metaTest");

        //set directories to TEST_RESOURCES_DIR
        try {
            setFinalStatic(LocationConfig.class.getDeclaredField("ANONYM_VID_DIR"), LocationConfig.TEST_RESOURCES_DIR);
            setFinalStatic(LocationConfig.class.getDeclaredField("META_DIR"), LocationConfig.TEST_RESOURCES_DIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void videoInfoListTest() {
        String list = videoManager.getVideoInfoList();
        JSONArray jsonArray = new JSONArray(list);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String jsonName = jsonObject.getString("name");
        Assert.assertTrue(jsonName.equals("input3"));
    }

    @Test
    public void downloadTest() {
        databaseManager.saveProcessedVideoAndMeta("pod", "testMeta");
        int videoId = databaseManager.getVideoIdByName("pod");
        File podAccount = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + account.getId() + "_pod"+ VideoInfo.FILE_EXTENTION);
        File podStandard = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "pod" + VideoInfo.FILE_EXTENTION);
        Assert.assertTrue(podStandard.renameTo(podAccount));
        InputStream inputStream = videoManager.download(videoId);
        databaseManager.deleteVideoAndMeta(videoId);
        File downloadFile = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "fileDownloadTest" + VideoInfo.FILE_EXTENTION);
        try {
            Files.copy(inputStream, downloadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            Assert.assertTrue(podAccount.renameTo(podStandard));
            Assert.assertTrue(downloadFile.delete());
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void deleteTest() {

    }

    @Test
    public void metadataTest() {

    }

    @After
    public void cleanUp() {
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input3"));
        databaseManager.deleteAccount();
    }
}
