package edu.kit.informatik.pcc.service.data;

import java.io.File;

public interface IFileHierachyManager {
	public File file(String name, String directoryPath);
	public File existingFile(String name, String directoryPath);
	public void createDirectory(String directoryPath);
	public File[] filesInDirectory(String directoryPath);
	public void deleteFile(File file);
}
