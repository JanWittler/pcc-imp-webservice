package edu.kit.informatik.pcc.core.crypto;

import java.io.File;
import java.security.Key;

public interface IVideoEncryptor {
	public byte[] encrypt(File inputVideo, File inputMetadata, Key key, File outputVideo, File outputMetadata);
}
