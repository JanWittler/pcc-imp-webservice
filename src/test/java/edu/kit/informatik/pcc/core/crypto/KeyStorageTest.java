package edu.kit.informatik.pcc.core.crypto;

import static org.junit.Assert.*;

import java.io.File;
import java.security.Key;
import java.security.KeyPair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.informatik.pcc.core.data.FileSystemManager;
import edu.kit.informatik.pcc.core.testing.TestingHelper;

public class KeyStorageTest {
	private KeyStorage keyStorage;
	private FileSystemManager temporaryFileManager;
	private Key[] keys;
	
	@Before
	public void setupBefore() {
		keyStorage = new KeyStorage();
		temporaryFileManager = new FileSystemManager(TestingHelper.testsDirectory + File.separator + "test_keyStorage");
		keyStorage.setFileManager(temporaryFileManager);
		KeyPair keyPair = new JavaCryptoRSA().generateAsymmetricKeyPair();
		keys = new Key[] {keyPair.getPublic(), keyPair.getPrivate() };
	}
	
	@After
	public void cleanUpAfter() {
		File dir = new File(temporaryFileManager.getContainerName());
		TestingHelper.deleteDirectoryAndItsContent(dir);
	}
	
	@Test
	public void testStoringLoading() {
		for (int i = 0; i < keys.length; i++) {
			keyStorage.storeKey("key_" + i, keys[i]);
		}
		for (int i = 0; i < keys.length; i++) {
			Key loadedKey = keyStorage.loadKey("key_" + i);
			assertNotNull(loadedKey);
			assertEquals(keys[i], loadedKey);
		}
	}
}
