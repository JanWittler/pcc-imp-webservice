package edu.kit.informatik.pcc.core.crypto;

import java.io.File;
import java.security.Key;

public interface IFileDecryptor {
	Key decryptKey(byte[] keyData, Key privateKey);
	void decryptFile(File inputFile, Key key, File outputFile);
}
