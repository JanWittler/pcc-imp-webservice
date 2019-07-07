package edu.kit.informatik.pcc.core.testing;

import java.io.File;

public class TestingHelper {
	public static final String testsDirectory = System.getProperty("user.dir") + File.separator + "test_files";
	
	public static void deleteDirectoryAndItsContent(File dir) {
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
