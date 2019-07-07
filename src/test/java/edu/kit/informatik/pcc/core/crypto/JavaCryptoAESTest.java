package edu.kit.informatik.pcc.core.crypto;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.Key;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.informatik.pcc.service.data.FileSystemManager;

public class JavaCryptoAESTest {
	private JavaCryptoAES crypto;
	private FileSystemManager temporaryFileManager;
	
	@Before
	public void setupBefore() {
		crypto = new JavaCryptoAES();
		temporaryFileManager = new FileSystemManager(System.getProperty("user.dir") + File.separator + "test_java_aes");
	}
	
	@After
	public void cleanUpAfter() {
		File dir = new File(temporaryFileManager.getContainerName());
		dir.delete();
	}
	
	@Test
	public void encryptUnencryptTest() {
		Key key = crypto.generateSymmetricKey();
		File input = temporaryFileManager.file("test_input");
		File output = temporaryFileManager.file("test_output");
		File result = temporaryFileManager.file("test_result");
		String inputString = "Test123@!?";
		try {
			FileUtils.writeStringToFile(input, inputString, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
			return;
		}
		crypto.encryptFile(input, key, output);
		crypto.decryptFile(output, key, result);
		byte[] inputData = null;
		byte[] outputData = null;
		String writtenInputString = null;
		String resultString = null;
		try {
			inputData = Files.readAllBytes(input.toPath());
			outputData = Files.readAllBytes(output.toPath());
			writtenInputString = String.join("\n", Files.readAllLines(input.toPath(), Charset.defaultCharset()));
			resultString = String.join("\n", Files.readAllLines(result.toPath(), Charset.defaultCharset()));
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
			return;
		}
		assertNotEquals(inputData, outputData);
		assertEquals(inputString, writtenInputString);
		assertEquals(inputString, resultString);
		
		temporaryFileManager.deleteFile(input);
		temporaryFileManager.deleteFile(output);
		temporaryFileManager.deleteFile(result);
	}
}
