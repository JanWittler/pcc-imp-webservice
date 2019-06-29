package edu.kit.informatik.pcc.core.crypto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import edu.kit.informatik.pcc.service.data.IFileManager;

public class KeyStorage implements IKeyStorage {
	private IFileManager fileManager;
	private static final String specsSuffix = "_spec";
	
	public void setFileManager(IFileManager fileManager) {
		assert this.fileManager == null;
		this.fileManager = fileManager;
	}

	@Override
	public void storeKey(String id, Key key) {
		assertCompletelySetup();
		File file = fileManager.fileWithName(id);
		File specsFile = fileManager.fileWithName(id + specsSuffix);
		try {
			Files.write(file.toPath(), key.getEncoded());
			Files.write(specsFile.toPath(), key.getAlgorithm().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Key loadKey(String id) {
		assertCompletelySetup();
		File specsFile = fileManager.fileWithName(id + specsSuffix);
		try {
			List<String> keySpecs = Files.readAllLines(specsFile.toPath());
			if (keySpecs.isEmpty()) {
				return null;
			}
			String keyAlgorithm = keySpecs.get(0);
			File file = fileManager.fileWithName(id);
			byte[] data = Files.readAllBytes(file.toPath());
			return new SecretKeySpec(data, keyAlgorithm);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	private void assertCompletelySetup() {
		assert fileManager != null;
	}
}
