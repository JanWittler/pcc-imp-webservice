package edu.kit.informatik.pcc.core.crypto;

import static org.junit.Assert.*;

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
	
	@Test
	public void keygenTest() {
		KeyPair keyPair1 = crypto.generateAsymmetricKeyPair();
		KeyPair keyPair2 = crypto.generateAsymmetricKeyPair();
		assertNotNull(keyPair1);
		assertNotNull(keyPair2);
		assertNotEquals(keyPair1.getPublic(), keyPair2.getPublic());
		assertNotEquals(keyPair1.getPrivate(), keyPair2.getPrivate());
	}
}
