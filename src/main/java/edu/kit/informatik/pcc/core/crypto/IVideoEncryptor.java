package edu.kit.informatik.pcc.core.crypto;

import java.io.File;

public interface IVideoEncryptor {
	public byte[] encrypt(File inputVideo, File inputMetadata, File outputVideo, File outputMetadata);
}
