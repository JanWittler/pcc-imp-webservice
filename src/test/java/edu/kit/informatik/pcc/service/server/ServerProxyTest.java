package edu.kit.informatik.pcc.service.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

import java.util.ArrayList;

/**
 *
 * @author Fabian Wenzel
 * Created by Fabi on 20.01.2017.
 */
public class ServerProxyTest {
    private ServerProxy serverProxy;
    private String validJson = "{\n" +
            "  \"account\": {\n" +
            "    \"mail\": \"testasdfasdf@example.com\",\n" +
            "    \"password\": \"123123\"\n" +
            "  }\n" +
            "}";
    private String noAccountIdJson = "{\n" +
            "  \"account\": {\n" +
            "    \"mail\": \"blaaaaf@example.com\",\n" +
            "    \"password\": \"123123\"\n" +
            "  }\n" +
            "}";
    private String wrongPasswordJson = "{\n" +
            "  \"account\": {\n" +
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

    @org.junit.Test
    public void gsonTest() {
        VideoInfo vI1 = new VideoInfo(10, "v1");
        VideoInfo vI2 = new VideoInfo(11, "v2");
        VideoInfo vI3 = new VideoInfo(12, "v3");
        ArrayList<VideoInfo> videoInfoList = new ArrayList<>();
       videoInfoList.add(vI1);
       videoInfoList.add(vI2);
       videoInfoList.add(vI3);
       JSONArray videoInfoInArray = new JSONArray();
       int i = 0;
        for (VideoInfo videoInfo : videoInfoList) {
            String json = videoInfo.getAsJson();
            JSONObject jO = new JSONObject(json);
            videoInfoInArray.put(i,jO);
            i++;
        }

        JSONObject objects = videoInfoInArray.getJSONObject(1);
        System.out.println(objects);
        System.out.println(videoInfoInArray.toString());






        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONObject object = new JSONObject();

        object.put("",videoInfoInArray);
        String json = gson.toJson(object);
        System.out.println(object.toString(2));

    }

}
