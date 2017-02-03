package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Fabian Wenzel
 *         Created by Fabi on 20.01.2017.
 */
public class ServerProxyTest {
    private final String SUCCESS = "SUCCESS";
    private DatabaseManager databaseManager;
    private String tempUUID = "3456qwe-qw234-2342f";
    private String accountJson;
    private String tempAccountJson;

    //mockup LocationConfig fields
    //public because of DatabaseManagerTest
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @Before
    public void setUp() {
        //start server in different thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Main.main(new String[0]);
            }
        });
        t.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mail", "fabiistkrass@imperium.baba");
        jsonObject.put("password", "yochilldeinlife");
        accountJson = jsonObject.toString();

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("mail", "fabiistababa@baba.de");
        jsonObject2.put("password", "ichbindershitfuckyooo");
        tempAccountJson = jsonObject2.toString();

        //setup for various tests
        Account account = new Account(accountJson);
        String uuid = "456-sgdfgd3t5g-345fs";
        databaseManager = new DatabaseManager(account);
        databaseManager.register(uuid);
        account.setId(databaseManager.getAccountId());
        databaseManager.saveProcessedVideoAndMeta("input", "testMeta");
        databaseManager.saveProcessedVideoAndMeta("input2", "testMeta2");
        databaseManager.saveProcessedVideoAndMeta("input3", "metaTest");
        databaseManager.verifyAccount(uuid);

        //set directories to TEST_RESOURCES_DIR
        try {
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
        f.param("account", accountJson);
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("authenticate");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
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
        f.param("account", tempAccountJson);
        f.param("uuid", tempUUID);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("verifyAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));

        tempDatabaseManager.deleteAccount();
    }

    @org.junit.Test
    public void downloadTest() {
        String videoId = Integer.toString(databaseManager.getVideoIdByName("input"));
        Form f = new Form();
        f.param("account", accountJson);
        f.param("videoId", videoId);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoDownload");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        InputStream inputStream = response.readEntity(InputStream.class);
        if (response.getStatus() == 200) {
            File downloadFile = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "fileDownloadTestFail.mp4");
            try {
                Files.copy(inputStream, downloadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                downloadFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue(response.getStatus() == 200);
    }

    @org.junit.Test
    public void videosByAccountTest() {
        Form f = new Form();
        f.param("account", accountJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("getVideosByAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        JSONArray jsonArray = new JSONArray(response.readEntity(String.class));
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String jsonName = jsonObject.getString("name");
        Assert.assertTrue(jsonName.equals("input"));
    }

    @org.junit.Test
    public void createAccountTest() {
        Account account2 = new Account(tempAccountJson);
        Form f = new Form();
        f.param("account", tempAccountJson);
        f.param("uuid", tempUUID);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("createAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        DatabaseManager tempDM = new DatabaseManager(account2);
        account2.setId(tempDM.getAccountId());
        tempDM.deleteAccount();
    }

    @org.junit.Test
    public void changeAccountTest() {
        Form f = new Form();
        f.param("account", accountJson);
        f.param("newAccount", tempAccountJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("changeAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
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
        File file1 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteVideo1" + ".mp4");
        File file2 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteVideo2" + ".mp4");
        File file3 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteMeta1" + ".json");
        File file4 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteMeta2" + ".json");
        try {
            file1.createNewFile();
            file2.createNewFile();
            file3.createNewFile();
            file4.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Form f = new Form();
        f.param("account", tempAccountJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("deleteAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        Assert.assertFalse(file1.exists());
        Assert.assertFalse(file2.exists());
        Assert.assertFalse(file3.exists());
        Assert.assertFalse(file4.exists());
        tempDatabaseManager.deleteAccount();
    }


    @org.junit.Test
    public void videoDeleteTest() {
        String videoId = "-1";
        databaseManager.saveProcessedVideoAndMeta("input4", "blaa");
        for (VideoInfo videoInfo : databaseManager.getVideoInfoList()) {
            if (videoInfo.getName().equals("input4")) {
                videoId = Integer.toString(videoInfo.getVideoId());
            }
        }
        Assert.assertFalse(videoId.equals("-1"));

        Form f = new Form();
        f.param("account", accountJson);
        f.param("videoId", videoId);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoDelete");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input4"));
    }

    @org.junit.Test
    public void videoInfoTest() {
        String videoId = "-1";
        for (VideoInfo videoInfo : databaseManager.getVideoInfoList()) {
            if (videoInfo.getName().equals("input3")) {
                videoId = Integer.toString(videoInfo.getVideoId());
            }
        }
        Assert.assertFalse(videoId.equals("-1"));

        Form f = new Form();
        f.param("account", accountJson);
        f.param("videoId", videoId);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoInfo");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        String entity = response.readEntity(String.class);
        if (!entity.equals("FAILURE")) {
            JSONObject jsonObject = new JSONObject(entity);
            float gForceY = (float) jsonObject.getDouble("triggerForceY");
            Assert.assertTrue(gForceY == 40.0f);
        } else {
            Assert.fail();
        }
    }

    @org.junit.Test
    public void uploadTest() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoUpload").register(MultiPartFeature.class);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        FileDataBodyPart video = new FileDataBodyPart("video", new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "encVid.mp4"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FileDataBodyPart metadata = new FileDataBodyPart("metadata", new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "encMeta.json"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FileDataBodyPart key = new FileDataBodyPart("key", new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "encKey.txt"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FormDataBodyPart data = new FormDataBodyPart("account", accountJson);
        multiPart.bodyPart(video);
        multiPart.bodyPart(metadata);
        multiPart.bodyPart(key);
        multiPart.bodyPart(data);
        Future<Response> futureResponse = webTarget.request().async().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        try {
            Response response = futureResponse.get();
            Assert.assertTrue(response.readEntity(String.class).equals("Finished editing video encVid"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("encVid"));
    }

    @After
    public void afterElse() {
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input"));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input2"));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input3"));
        databaseManager.deleteAccount();
        Main.stopServer();
    }

}
