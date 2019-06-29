package edu.kit.informatik.pcc.service.data;

import java.io.File;

public class FileSystemManager implements IFileManager {
	private String containerName;
	
	public FileSystemManager(String containerName) {
		this.containerName = containerName;
	}

	@Override
	public File fileWithName(String name) {
		return new File(this.containerName + File.separator + name);
	}

	@Override
	public void deleteFile(File file) {
		file.delete();
	}
}
