package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import edu.kit.informatik.pcc.service.manager.AccountManager;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Fabian Wenzel
 */
public class ServerProxyTest {
    //string for client/request/response
    private final String SUCCESS = "SUCCESS";
    private final String PATH = "http://localhost:2222/webservice/";
    private final String ACCOUNT = "account";

    private String accountJson;
    private String tempAccountJson;
    private String tempUUID = "3456qwe-qw234-2342f";
    private String anonym_dir = LocationConfig.ANONYM_VID_DIR;
    private String meta_dir = LocationConfig.META_DIR;
    private Form form;

    private DatabaseManager databaseManager;
    private Client client;
    private AccountManager accountManager;

    //mockup function for LocationConfig fields
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
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //create two json objects for testing
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mail", "fabiistkrass@imperium.baba");
        jsonObject.put("password", "yochilldeinlife");
        accountJson = jsonObject.toString();

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("mail", "fabiistababa@baba.de");
        jsonObject2.put("password", "ichbindershitfuckyooo");
        tempAccountJson = jsonObject2.toString();

        //setup account and databaseManager for various tests
        Account account = new Account(accountJson);
        databaseManager = new DatabaseManager(account);
        accountManager = new AccountManager(account);


        //register/verify account and put some test videos/metadata into database

        String uuid = "456-sgdfgd3t5g-345fs";
        accountManager.registerAccount(uuid);
        account.setId(databaseManager.getAccountId());
        databaseManager.verifyAccount(uuid);
        databaseManager.saveProcessedVideoAndMeta("pod", "testMeta");
        databaseManager.saveProcessedVideoAndMeta("input2", "testMeta2");
        databaseManager.saveProcessedVideoAndMeta("input3", "metaTest");

        //setup for requests
        form = new Form();
        client = ClientBuilder.newClient();

        //set directories to TEST_RESOURCES_DIR
        try {
            setFinalStatic(LocationConfig.class.getDeclaredField("ANONYM_VID_DIR"), LocationConfig.TEST_RESOURCES_DIR);
            setFinalStatic(LocationConfig.class.getDeclaredField("META_DIR"), LocationConfig.TEST_RESOURCES_DIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void authenticateTest() {
        form.param(ACCOUNT, accountJson);
        WebTarget webTarget = client.target(PATH).path("authenticate");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
    }

    @Test
    public void verifyTest() {
        //setup for test
        Account tempAccount = new Account(tempAccountJson);
        DatabaseManager tempDatabaseManager = new DatabaseManager(tempAccount);
        AccountManager tempAccountManager = new AccountManager(tempAccount);
        tempAccountManager.registerAccount(tempUUID);
        tempAccount.setId(tempDatabaseManager.getAccountId());

        //client request
        WebTarget webTarget = client.target(PATH).path("verifyAccount");
        System.out.println(webTarget.getUri());
        Response response = webTarget.queryParam("uuid", tempUUID).request().get();
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));

        //cleanup
        tempDatabaseManager.deleteAccount();
    }

    @Test
    public void downloadTest() {
        //setup for test
        String videoId = Integer.toString(databaseManager.getVideoIdByName("pod"));
        form.param(ACCOUNT, accountJson);
        form.param("videoId", videoId);
        WebTarget webTarget = client.target(PATH).path("videoDownload");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        InputStream inputStream = response.readEntity(InputStream.class);
        if (response.getStatus() == 200) {
            File downloadFile = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "fileDownloadTest" + VideoInfo.FILE_EXTENTION);
            try {
                Files.copy(inputStream, downloadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                boolean status = downloadFile.delete();
                Assert.assertTrue(status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue(response.getStatus() == 200);
    }

    @Test
    public void videosTest() {
        //setup for test
        form.param(ACCOUNT, accountJson);
        WebTarget webTarget = client.target(PATH).path("getVideos");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        JSONArray jsonArray = new JSONArray(response.readEntity(String.class));
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String jsonName = jsonObject.getString("name");
        Assert.assertTrue(jsonName.equals("pod"));
    }

    @Test
    public void createAccountTest() {
        //setup for test
        Account account2 = new Account(tempAccountJson);
        form.param(ACCOUNT, tempAccountJson);
        form.param("uuid", tempUUID);
        WebTarget webTarget = client.target(PATH).path("createAccount");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        DatabaseManager tempDM = new DatabaseManager(account2);
        account2.setId(tempDM.getAccountId());
        tempDM.deleteAccount();
    }

    @Test
    public void changeAccountTest() {
        //setup for test
        form.param(ACCOUNT, accountJson);
        form.param("newAccount", tempAccountJson);
        WebTarget webTarget = client.target(PATH).path("changeAccount");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
    }

    @Test
    public void deleteAccountTest() {
        //setup for test
        Account tempAccount = new Account(tempAccountJson);
        DatabaseManager tempDatabaseManager = new DatabaseManager(tempAccount);
        AccountManager tempAccountManager = new AccountManager(tempAccount);
        tempAccountManager.registerAccount(tempUUID);
        tempAccount.setId(tempDatabaseManager.getAccountId());
        tempDatabaseManager.verifyAccount(tempUUID);
        tempDatabaseManager.saveProcessedVideoAndMeta("deleteVideo1", "deleteMeta1");
        tempDatabaseManager.saveProcessedVideoAndMeta("deleteVideo2", "deleteMeta2");

        //create files for testing
        File file1 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteVideo1" + VideoInfo.FILE_EXTENTION);
        File file2 = new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "deleteVideo2" + VideoInfo.FILE_EXTENTION);
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

        form.param(ACCOUNT, tempAccountJson);
        WebTarget webTarget = client.target(PATH).path("deleteAccount");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);

        //various assertions
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        Assert.assertFalse(file1.exists());
        Assert.assertFalse(file2.exists());
        Assert.assertFalse(file3.exists());
        Assert.assertFalse(file4.exists());

        //cleanup
        tempDatabaseManager.deleteAccount();
    }


    @Test
    public void videoDeleteTest() {
        String videoId = "-1";
        databaseManager.saveProcessedVideoAndMeta("input4", "blaa");
        for (VideoInfo videoInfo : databaseManager.getVideoInfoList()) {
            if (videoInfo.getName().equals("input4")) {
                videoId = Integer.toString(videoInfo.getVideoId());
            }
        }
        Assert.assertFalse(videoId.equals("-1"));

        form.param(ACCOUNT, accountJson);
        form.param("videoId", videoId);
        WebTarget webTarget = client.target(PATH).path("videoDelete");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals(SUCCESS));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input4"));
    }

    @Test
    public void videoInfoTest() {
        String videoId = "-1";
        for (VideoInfo videoInfo : databaseManager.getVideoInfoList()) {
            if (videoInfo.getName().equals("input3")) {
                videoId = Integer.toString(videoInfo.getVideoId());
            }
        }
        Assert.assertFalse(videoId.equals("-1"));

        form.param(ACCOUNT, accountJson);
        form.param("videoId", videoId);
        WebTarget webTarget = client.target(PATH).path("videoInfo");
        Response response = webTarget.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        String entity = response.readEntity(String.class);
        if (!entity.equals("FAILURE")) {
            JSONObject jsonObject = new JSONObject(entity);
            float gForceY = (float) jsonObject.getDouble("triggerForceY");
            Assert.assertTrue(gForceY == 40.0f);
        } else {
            Assert.fail();
        }
    }

    @Test
    public void uploadTest() {
        //set directories to standard
        try {
            setFinalStatic(LocationConfig.class.getDeclaredField("ANONYM_VID_DIR"), anonym_dir);
            setFinalStatic(LocationConfig.class.getDeclaredField("META_DIR"), meta_dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebTarget webTarget = client.target(PATH).path("videoUpload").register(MultiPartFeature.class);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        FileDataBodyPart video = new FileDataBodyPart("video", new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "encVid.mp4"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FileDataBodyPart metadata = new FileDataBodyPart("metadata", new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "encMeta.json"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FileDataBodyPart key = new FileDataBodyPart("key", new File(LocationConfig.TEST_RESOURCES_DIR + File.separator + "encKey.txt"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FormDataBodyPart data = new FormDataBodyPart(ACCOUNT, accountJson);
        multiPart.bodyPart(video);
        multiPart.bodyPart(metadata);
        multiPart.bodyPart(key);
        multiPart.bodyPart(data);
        Future<Response> futureResponse = webTarget.request().async().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        try {
            Response response = futureResponse.get();
            Assert.assertTrue(response.readEntity(String.class).equals("Finished editing video"));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("encVid"));
    }

    @After
    public void after() {
        //set directories back to original paths
        try {
            setFinalStatic(LocationConfig.class.getDeclaredField("ANONYM_VID_DIR"), anonym_dir);
            setFinalStatic(LocationConfig.class.getDeclaredField("META_DIR"), meta_dir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //delete videos/metadata/account from database
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("pod"));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input2"));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input3"));
        databaseManager.deleteAccount();

        //stop server
        Main.stopServer();
    }
}
