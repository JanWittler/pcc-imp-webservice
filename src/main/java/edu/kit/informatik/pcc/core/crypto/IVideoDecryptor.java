package edu.kit.informatik.pcc.core.crypto;

import java.io.File;

public interface IVideoDecryptor {
	public void decrypt(File inputVideo, File inputMetadata, byte[] keyData, File outputVideo, File outputMetadata);
}
