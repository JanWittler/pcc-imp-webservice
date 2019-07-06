package edu.kit.informatik.pcc.service.data;

import java.io.File;

public interface IFileManager {
	public File file(String name);
	public File existingFile(String name);
	public void deleteFile(File file);
}
