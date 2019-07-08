package edu.kit.informatik.pcc.service.videoprocessing.opencv;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.informatik.pcc.core.testing.TestingHelper;
import edu.kit.informatik.pcc.service.data.FileSystemManager;

public class OpenCVAnonymizerTest {
	private OpenCVAnonymizer openCVAnonymizer;
	private FileSystemManager temporaryFileManager;
	private File input;
    private File output;
	
	@Before
	public void setupBefore() {
		openCVAnonymizer = new OpenCVAnonymizer();
		temporaryFileManager = new FileSystemManager(TestingHelper.testsDirectory + File.separator + "test_opencv");
		input = new File(TestingHelper.TEST_RESOURCES_DIR + File.separator + "decVid.mp4");
        output = temporaryFileManager.file("output.mp4");
	}
	
	@After
	public void cleanUpAfter() {
		File dir = new File(temporaryFileManager.getContainerName());
		TestingHelper.deleteDirectoryAndItsContent(dir);
	}
	
	@Ignore //ignore test in regular test suite as it just takes too long
	@Test
	public void validRunTest() {
		assertNotNull(input);
		assertNotNull(output);
		assertTrue(openCVAnonymizer.processVideo(input, null, output));
	}
}
