package edu.kit.informatik.pcc.core.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.security.Key;
import java.security.KeyPair;

import org.junit.Before;
import org.junit.Test;

public class JavaCryptoRSATest {
	private JavaCryptoRSA crypto;
	private JavaCryptoAES keyGen;
	
	@Before
	public void setupBefore() {
		crypto = new JavaCryptoRSA();
		keyGen = new JavaCryptoAES();
	}
	
	@Test
	public void encryptUnencryptTest() {
		KeyPair keyPair = crypto.generateAsymmetricKeyPair();
		Key key = keyGen.generateSymmetricKey();
		byte[] encryptedKey = crypto.encryptKey(key, keyPair.getPublic());
		Key decryptedKey = crypto.decryptKey(encryptedKey, keyGen.keyAlgorithm(), keyPair.getPrivate());
		
		assertNotEquals(key.getEncoded(), encryptedKey);
		assertEquals(key, decryptedKey);
	}
}
