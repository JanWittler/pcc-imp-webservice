package edu.kit.informatik.pcc.service.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Fabian Wenzel
 * Created by Fabi on 20.01.2017.
 */
public class ServerProxyTest {
    private ServerProxy serverProxy;
    private DatabaseManager databaseManager;
    private Account account;
    private VideoInfo videoInfo;
    private String uuid = "1234";
    private String newUuid = "1235";
    private String validJson = "{\n" +
            "  \"account\": {\n" +
            "    \"mail\": \"fabiistkrass@imperium.baba\",\n" +
            "    \"password\": \"yochilldeinlife\"\n" +
            "  }\n" +
            "}";
    private String newJson = "{\n" +
            "  \"account\": {\n" +
            "    \"mail\": \"fabiistbaba@squad.moneyflow\",\n" +
            "    \"password\": \"ichbindershitfuckyooo\"\n" +
            "  }\n" +
            "}";

    @Before
    public void setUp() {
        serverProxy = new ServerProxy();
        account = new Account(validJson);
        databaseManager = new DatabaseManager(account);
        databaseManager.register(uuid);
        account.setId(databaseManager.getAccountId());
        databaseManager.saveProcessedVideoAndMeta("input", "testMeta");
        databaseManager.saveProcessedVideoAndMeta("input2", "testMeta2");
    }

    @org.junit.Test
    public void authenticateTest() {
        databaseManager.verifyAccount(uuid);
        Form f = new Form();
        f.param("data", validJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("authenticate");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals("SUCCESS"));
    }

    @org.junit.Test
    public void verifyTest() {
        Form f = new Form();
        f.param("data", validJson);
        f.param("uuid", uuid);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("verifyAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals("SUCCESS"));
    }

    @org.junit.Test
    public void downloadTest() {
        databaseManager.verifyAccount(uuid);
        String videoId = Integer.toString(databaseManager.getVideoIdByName("input"));
        Form f = new Form();
        f.param("data", validJson);
        f.param("videoId", videoId);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("videoDownload");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        InputStream inputStream = response.readEntity(InputStream.class);
        if (response.getStatus() == 200) {
            File downloadfile = new File("c://test/input.mp4");
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
        databaseManager.verifyAccount(uuid);
        Form f = new Form();
        f.param("data", validJson);
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
        Account account2 = new Account(newJson);
        Form f = new Form();
        f.param("data", newJson);
        f.param("uuid", newUuid);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("createAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals("SUCCESS"));
        DatabaseManager tempDM = new DatabaseManager(account2);
        account2.setId(tempDM.getAccountId());
        tempDM.deleteAccount();
    }

    @org.junit.Test
    public void changeAccountTest() {
        databaseManager.verifyAccount(uuid);
        Form f = new Form();
        f.param("data", validJson);
        f.param("newData", newJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("changeAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals("SUCCESS"));
    }

    @org.junit.Test
    public void deleteAccountTest() {
        Account account2 = new Account(newJson);
        DatabaseManager tempDatabaseManager = new DatabaseManager(account2);
        tempDatabaseManager.register(newUuid);
        account2.setId(tempDatabaseManager.getAccountId());
        tempDatabaseManager.verifyAccount(newUuid);
        tempDatabaseManager.saveProcessedVideoAndMeta("deleteVideo1", "deleteMeta1");
        tempDatabaseManager.saveProcessedVideoAndMeta("deleteVideo2", "deleteMeta2");
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
        account2.setId(tempDatabaseManager.getAccountId());
        Form f = new Form();
        f.param("data", newJson);
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:2222/").path("webservice").path("deleteAccount");
        Response response = webTarget.request().post(Entity.entity(f, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
        Assert.assertTrue(response.readEntity(String.class).equals("SUCCESS"));
    }

//    @org.junit.Test
//    public void jsonTest() {
//        VideoInfo vI1 = new VideoInfo(10, "v1");
//        VideoInfo vI2 = new VideoInfo(11, "v2");
//        VideoInfo vI3 = new VideoInfo(12, "v3");
//        ArrayList<VideoInfo> videoInfoList = new ArrayList<>();
//       videoInfoList.add(vI1);
//       videoInfoList.add(vI2);
//       videoInfoList.add(vI3);
//       JSONArray videoInfoInArray = new JSONArray();
//       int i = 0;
//        for (VideoInfo videoInfo : videoInfoList) {
//            String json = videoInfo.getAsJson();
//            JSONObject jO = new JSONObject(json);
//            videoInfoInArray.put(i,jO);
//            i++;
//        }
//
//        JSONObject objects = videoInfoInArray.getJSONObject(1);
//        System.out.println(objects);
//        System.out.println(videoInfoInArray.toString());
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JSONObject object = new JSONObject();
//
//        object.put("",videoInfoInArray);
//        String json = gson.toJson(object);
//       // System.out.println(object.toString(2));
//    }

    @After
    public void after() {
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input"));
        databaseManager.deleteVideoAndMeta(databaseManager.getVideoIdByName("input2"));
        databaseManager.deleteAccount();
    }

}
