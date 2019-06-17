package edu.kit.informatik.pcc.core.crypto;

import java.io.File;
import java.security.Key;

public class VideoEncryptor implements IVideoEncryptor {
	private IFileEncryptor fileEncryptor;
	private IPublicKeyProvider publicKeyProvider;
	
	public void setFileEncryptor(IFileEncryptor fileEncryptor) {
		assert this.fileEncryptor == null;
		this.fileEncryptor = fileEncryptor;
	}
	
	public void setPublicKeyProvider(IPublicKeyProvider publicKeyProvider) {
		assert this.publicKeyProvider == null;
		this.publicKeyProvider = publicKeyProvider;
	}

	@Override
	public byte[] encrypt(File inputVideo, File inputMetadata, Key key, File outputVideo, File outputMetadata) {
		assertCompletelySetup();
		fileEncryptor.encryptFile(inputVideo, key, outputVideo);
		fileEncryptor.encryptFile(inputMetadata, key, outputMetadata);
		Key publicKey = publicKeyProvider.getPublicKey();
		return fileEncryptor.encryptKey(key, publicKey);
	}
	
	private void assertCompletelySetup() {
		assert this.fileEncryptor != null;
		assert this.publicKeyProvider != null;
	}
}
