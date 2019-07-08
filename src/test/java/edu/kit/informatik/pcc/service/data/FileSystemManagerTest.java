package edu.kit.informatik.pcc.service.data;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.informatik.pcc.core.testing.TestingHelper;

public class FileSystemManagerTest {
	private FileSystemManager fileSystemManager;
	
	@Before
	public void setupBefore() {
		fileSystemManager = new FileSystemManager(TestingHelper.testsDirectory + File.separator + "file_system_manager");
	}
	
	@After
	public void cleanUpAfter() {
		File dir = new File(fileSystemManager.getContainerName());
		TestingHelper.deleteDirectoryAndItsContent(dir);
	}
	
	@Test
	public void fileCreationTest() {
		String fileName = "a";
		assertNull(fileSystemManager.existingFile(fileName));
		assertNotNull(fileSystemManager.file(fileName));
		File file = fileSystemManager.existingFile(fileName);
		assertNotNull(file);
		fileSystemManager.deleteFile(file);
		assertNull(fileSystemManager.existingFile(fileName));
	}
	
	@Test
	public void fileInDirectoryCreationTest() {
		String fileName = "a";
		String directoryName = "dir";
		assertNull(fileSystemManager.existingFile(fileName, directoryName));
		fileSystemManager.createDirectory(directoryName);
		assertNotNull(fileSystemManager.file(fileName, directoryName));
		File file = fileSystemManager.existingFile(fileName, directoryName);
		assertNotNull(file);
		File[] files = fileSystemManager.filesInDirectory(directoryName);
		assertEquals(files.length, 1);
		assertEquals(files[0], file);
		fileSystemManager.deleteFile(file);
		assertNull(fileSystemManager.existingFile(fileName, directoryName));
		assertEquals(fileSystemManager.filesInDirectory(directoryName).length, 0);
	}
}
