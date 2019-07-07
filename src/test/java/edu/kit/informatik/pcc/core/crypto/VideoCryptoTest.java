package edu.kit.informatik.pcc.core.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.informatik.pcc.core.testing.TestingHelper;
import edu.kit.informatik.pcc.service.data.FileSystemManager;

public class VideoCryptoTest {
	private VideoDecryptor decryptor;
	private VideoEncryptor encryptor;
	private FileSystemManager temporaryFileManager;
	
	@Before
	public void setupBefore() {
		temporaryFileManager = new FileSystemManager(TestingHelper.testsDirectory + File.separator + "test_file_crypto");
		
		decryptor = new VideoDecryptor();
		encryptor = new VideoEncryptor();
		
		DoubleSecuredFileDecryptor fileDecryptor = new DoubleSecuredFileDecryptor();
		DoubleSecuredFileEncryptor fileEncryptor = new DoubleSecuredFileEncryptor();
		JavaCryptoAES symmetricCrypto = new JavaCryptoAES();
		JavaCryptoRSA asymmetricCrypto = new JavaCryptoRSA();
		KeyStorage keyStorage = new KeyStorage();
		
		decryptor.setFileDecryptor(fileDecryptor);
		decryptor.setAsymmetricDecryptor(asymmetricCrypto);
		decryptor.setKeyStorage(keyStorage);
		encryptor.setFileEncryptor(fileEncryptor);
		encryptor.setPublicKeyProvider(decryptor);
		encryptor.setSymmetricEncryptor(symmetricCrypto);
		
		fileDecryptor.setSymmetricDecryptor(symmetricCrypto);
		fileDecryptor.setAsymmetricDecryptor(asymmetricCrypto);
		fileEncryptor.setSymmetricEncryptor(symmetricCrypto);
		fileEncryptor.setAsymmetricEncryptor(asymmetricCrypto);	
		keyStorage.setFileManager(temporaryFileManager);
	}
	
	@After
	public void cleanUpAfter() {
		File dir = new File(temporaryFileManager.getContainerName());
		TestingHelper.deleteDirectoryAndItsContent(dir);
	}
	
	@Test
	public void encryptUnencryptTest() {
		File i1 = temporaryFileManager.file("test_video_input");
		File i2 = temporaryFileManager.file("test_metadata_input");
		File o1 = temporaryFileManager.file("test_video_output");
		File o2 = temporaryFileManager.file("test_metadata_output");
		File r1 = temporaryFileManager.file("test_video_result");
		File r2 = temporaryFileManager.file("test_metadata_result");
		String inputString1 = "Test123@!?";
		String inputString2 = "?!@321tseT";
		try {
			FileUtils.writeStringToFile(i1, inputString1, Charset.defaultCharset());
			FileUtils.writeStringToFile(i2, inputString2, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
			return;
		}
		byte[] encryptedKey = encryptor.encrypt(i1, i2, o1, o2);
		assertNotNull(encryptedKey);
		decryptor.decrypt(o1, o2, encryptedKey, r1, r2);
		byte[] i1Data = null;
		byte[] i2Data = null;
		byte[] o1Data = null;
		byte[] o2Data = null;
		String i1Written = null;
		String i2Written = null;
		String r1String = null;
		String r2String = null;
		try {
			i1Data = Files.readAllBytes(i1.toPath());
			i2Data = Files.readAllBytes(i2.toPath());
			o1Data = Files.readAllBytes(o1.toPath());
			o2Data = Files.readAllBytes(o2.toPath());
			i1Written = String.join("\n", Files.readAllLines(i1.toPath(), Charset.defaultCharset()));
			i2Written = String.join("\n", Files.readAllLines(i2.toPath(), Charset.defaultCharset()));
			r1String = String.join("\n", Files.readAllLines(r1.toPath(), Charset.defaultCharset()));
			r2String = String.join("\n", Files.readAllLines(r2.toPath(), Charset.defaultCharset()));
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
			return;
		}
		assertNotEquals(i1Data, o1Data);
		assertNotEquals(i2Data, o2Data);
		assertEquals(i1Written, inputString1);
		assertEquals(i2Written, inputString2);
		assertEquals(inputString1, r1String);
		assertEquals(inputString2, r2String);
		
		temporaryFileManager.deleteFile(i1);
		temporaryFileManager.deleteFile(i2);
		temporaryFileManager.deleteFile(o1);
		temporaryFileManager.deleteFile(o2);
		temporaryFileManager.deleteFile(r1);
		temporaryFileManager.deleteFile(r2);
	}
}
