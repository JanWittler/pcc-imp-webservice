package edu.kit.informatik.pcc.core.testing;

import java.io.File;

public class TestingHelper {
	public static final String testsDirectory = System.getProperty("user.dir") + File.separator + "target" + File.separator + "test_files";
	
	/**
     * Directory for test resources.
     */
    public static final String TEST_RESOURCES_DIR = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    
	public static void deleteDirectoryAndItsContent(File dir) {
		if (dir == null || !dir.exists()) {
            return;
        }
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				deleteDirectoryAndItsContent(file);
			}
			else {
				file.delete();
			}
		}
		dir.delete();
	}
}
