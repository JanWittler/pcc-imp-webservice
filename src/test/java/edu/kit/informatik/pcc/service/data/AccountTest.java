package edu.kit.informatik.pcc.service.data;

import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/20/17.
 */
public class AccountTest {

    Account ac;

    @Before
    public void beforeTest() {
        String MAIL = "\"testEMAIL@123schonVorbei.com\"";
        String PASSWORD = "\"testPasswordForUni\"";
        String DATE = "\"123\"";
        String TRIGGER_TYPE = "\"23\"";
        String G_FORCE_X = "30.33333";
        String G_FORCE_Y = "40";
        String G_FORCE_Z = "50";

        ac = new Account("{\n" +
                "  \"account\": {\n" +
                "    \"mail\": " + MAIL + ",\n" +
                "    \"password\": " + PASSWORD + "\n" +
                "  },\n" +
                "  \"metaInfo\": {\n" +
                "    \"date\": " + DATE + ",\n" +
                "    \"triggerType\": " + TRIGGER_TYPE + ",\n" +
                "    \"gForceX\": " + G_FORCE_X + ",\n" +
                "    \"gForceY\": " + G_FORCE_Y + ",\n" +
                "    \"gForceZ\": " + G_FORCE_Z + "\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void hashPasswordTest() throws Exception {
        String hash = ac.hashPassword("test");
        System.out.println(hash);
    }
}