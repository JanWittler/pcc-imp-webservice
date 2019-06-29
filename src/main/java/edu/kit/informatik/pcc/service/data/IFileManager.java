package edu.kit.informatik.pcc.service.data;

import java.io.File;

public interface IFileManager {
	public File fileWithName(String name);
	public void deleteFile(File file);
}
