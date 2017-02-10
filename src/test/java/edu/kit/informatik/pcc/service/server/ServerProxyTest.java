package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import edu.kit.informatik.pcc.service.manager.AccountManager;
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

}
