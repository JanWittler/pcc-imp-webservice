package edu.kit.informatik.pcc.service.data;

import java.io.File;
import java.util.logging.Logger;

public class FileSystemManager extends AFileManager {
	private String containerName;
	
	public FileSystemManager(String containerName) {
		this.containerName = containerName;
	}
	
	@Override
	public File file(String name, String directoryPath) {
		File file = new File(fullPath(name, directoryPath));
		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (Exception e) {
				Logger.getGlobal().warning("Failed to create file " + file.getAbsolutePath());
			}
		}
		return file;
	}

	@Override
	public File existingFile(String name, String directoryPath) {
		File file = new File(fullPath(name, directoryPath));
		if (file.exists()) {
			return file;
		}
		return null;
	}

	@Override
	public void createDirectory(String directoryPath) {
		new File(combinedPath(directoryPath, containerName)).mkdirs();		
	}

	@Override
	public File[] filesInDirectory(String directoryPath) {
		return new File(combinedPath(directoryPath, containerName)).listFiles();
	}
	
	private String combinedPath(String fileName, String directoryPath) {
		if (directoryPath == null) {
			return fileName;
		}
		if (directoryPath.endsWith(File.separator) || fileName.startsWith(File.separator)) {
			return directoryPath + fileName;
		}
		return directoryPath + File.separator + fileName;
	}
	
	private String fullPath(String fileName, String directoryPath) {
		return combinedPath(combinedPath(fileName, directoryPath), containerName);
	}
}
