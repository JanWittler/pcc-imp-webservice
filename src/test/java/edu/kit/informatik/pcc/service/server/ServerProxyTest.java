package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import edu.kit.informatik.pcc.service.manager.VideoManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author Fabian Wenzel
 * Created by Fabi on 20.01.2017.
 */
@Ignore
public class ServerProxyTest {
    private DatabaseManager databaseManager;
    private Account account;
    private final String SUCCESS = "SUCCESS";
    private String tempUUID = "1235";
    private String accountJson = "{\n" +
            "  \"account\": {\n" +
            "    \"mail\": \"fabiistkrass@imperium.baba\",\n" +
            "    \"password\": \"yochilldeinlife\"\n" +
            "  }\n" +
            "}";
    private String tempAccountJson = "{\n" +
            "  \"account\": {\n" +
            "    \"mail\": \"fabiistbaba@squad.moneyflow\",\n" +
            "    \"password\": \"ichbindershitfuckyooo\"\n" +
            "  }\n" +
            "}";

    //mockup LocationConfig fields
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @Before
    public void setUp() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Main.main(new String[0]);
            }
        });
        t.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String uuid = "1234";
        account = new Account(accountJson);
        databaseManager = new DatabaseManager(account);
        databaseManager.register(uuid);
        account.setId(databaseManager.getAccountId());
        databaseManager.saveProcessedVideoAndMeta("input", "testMeta");
        databaseManager.saveProcessedVideoAndMeta("input2", "testMeta2");
        databaseManager.saveProcessedVideoAndMeta("input3", "metaTest");
        databaseManager.verifyAccount(uuid);

        //set directories to TEST_RESOURCES_DIR
        try {
            setFinalStatic(LocationConfig.class.getDeclaredField("RESOURCES_DIR"), LocationConfig.TEST_RESOURCES_DIR);
            setFinalStatic(LocationConfig.class.getDeclaredField("ANONYM_VID_DIR"), LocationConfig.TEST_RESOURCES_DIR);
            setFinalStatic(LocationConfig.class.getDeclaredField("META_DIR"), LocationConfig.TEST_RESOURCES_DIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void authenticateTest() {
        Client client = ClientBuilder.newClient();
        Form f = new Form();
        f.param("data", accountJson);
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("authenticate");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
    }

    @org.junit.Test
    public void verifyTest() {
        //setup for test
        Account tempAccount = new Account(tempAccountJson);
        DatabaseManager tempDatabaseManager = new DatabaseManager(tempAccount);
        tempDatabaseManager.register(tempUUID);
        tempAccount.setId(tempDatabaseManager.getAccountId());

        Form f = new Form();
        f.param("data", tempAccountJson);
        f.param("uuid", tempUUID);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("verifyAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));

        tempDatabaseManager.deleteAccount();
    }

    @org.junit.Test
    public void downloadTest() {
        String videoId = Integer.toString(databaseManager.getVideoIdByName("input"));
        Form f = new Form();
        f.param("data", accountJson);
        f.param("videoId", videoId);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoDownload");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        InputStream inputStream = response.readEntity(InputStream.class);
        if (response.getStatus() == 200) {
            File downloadfile = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "fileDownloadTestFail.mp4");
            try {
                Files.copy(inputStream, downloadfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue(response.getStatus() == 200);
    }

    @org.junit.Test
    public void videosByAccountTest() {
        Form f = new Form();
        f.param("data", accountJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("getVideosByAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        JSONArray jsonArray = new JSONArray(response.readEntity(String.class));
        JSONObject outerObjects = jsonArray.getJSONObject(0);
        JSONObject innerObject = outerObjects.getJSONObject("videoInfo");
        String jsonName = innerObject.getString("name");
        Assert.assertTrue(jsonName.equals("input"));
    }

    @org.junit.Test
    public void createAccountTest() {
        Account account2 = new Account(tempAccountJson);
        Form f = new Form();
        f.param("data", tempAccountJson);
        f.param("uuid", tempUUID);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("createAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        DatabaseManager tempDM = new DatabaseManager(account2);
        account2.setId(tempDM.getAccountId());
        tempDM.deleteAccount();
    }

    @org.junit.Test
    public void changeAccountTest() {
        Form f = new Form();
        f.param("data", accountJson);
        f.param("newData", tempAccountJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("changeAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
    }

    @org.junit.Test
    public void deleteAccountTest() {
        //setup for test
        Account tempAccount = new Account(tempAccountJson);
        DatabaseManager tempDatabaseManager = new DatabaseManager(tempAccount);
        tempDatabaseManager.register(tempUUID);
        tempAccount.setId(tempDatabaseManager.getAccountId());
        tempDatabaseManager.verifyAccount(tempUUID);
        tempDatabaseManager.saveProcessedVideoAndMeta("deleteVideo1", "deleteMeta1");
        tempDatabaseManager.saveProcessedVideoAndMeta("deleteVideo2", "deleteMeta2");

        //create files for testing
        boolean createFiles;
        File file1 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteVideo1" + ".mp4");
        File file2 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteVideo2" + ".mp4");
        File file3 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteMeta1" + ".json");
        File file4 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteMeta2" + ".json");
        try {
            createFiles =  file1.createNewFile();
            Assert.assertTrue(createFiles);
            createFiles =  file2.createNewFile();
            Assert.assertTrue(createFiles);
            createFiles =  file3.createNewFile();
            Assert.assertTrue(createFiles);
            createFiles =  file4.createNewFile();
            Assert.assertTrue(createFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Form f = new Form();
        f.param("data", tempAccountJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("deleteAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        tempDatabaseManager.deleteAccount();
    }


    @org.junit.Test
    public void videoDeleteTest() {
        String videoId = "-1";
        databaseManager.saveProcessedVideoAndMeta("input4", "blaa");
        VideoManager videoManager = new VideoManager(account);
        for (VideoInfo videoInfo: videoManager.getVideoInfoList()) {
            if (videoInfo.getName().equals("input4")) {
                videoId = Integer.toString(videoInfo.getVideoId());
            }
        }
        Assert.assertFalse(videoId.equals("-1"));

        Form f = new Form();
        f.param("data", accountJson);
        f.param("videoId", videoId);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoDelete");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input4"));
    }

    @org.junit.Test
    public void videoInfoTest() {
        String videoId = "-1";
        VideoManager videoManager = new VideoManager(account);
        for (VideoInfo videoInfo: videoManager.getVideoInfoList()) {
            if (videoInfo.getName().equals("input3")) {
                videoId = Integer.toString(videoInfo.getVideoId());
            }
        }
        Assert.assertFalse(videoId.equals("-1"));

        Form f = new Form();
        f.param("data", accountJson);
        f.param("videoId", videoId);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoInfo");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        JSONObject jsonObject = new JSONObject(response.readEntity(String.class));
        JSONObject metadata = jsonObject.getJSONObject("metadata");
        String gForceY = metadata.getString("gForceY");
        Assert.assertTrue(gForceY.equals("40.0"));
    }

    @After
    public void after() {
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input"));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input2"));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input3"));
        databaseManager.deleteAccount();
        Main.stopServer();
    }

}
