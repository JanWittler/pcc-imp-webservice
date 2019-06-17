package edu.kit.informatik.pcc.core.crypto;

import java.io.File;
import java.security.Key;

public class JavaRSA_AESFileEncryptor implements IVideoEncryptor {
	private VideoEncryptor videoEncryptor;
	
	public JavaRSA_AESFileEncryptor() {
		JavaCryptoRSA javaCryptoRSA = new JavaCryptoRSA();
		JavaCryptoAES javaCryptoAES = new JavaCryptoAES();
		DoubleSecuredFileEncryptor doubleSecuredFileEncryptor = new DoubleSecuredFileEncryptor();
		VideoEncryptor videoEncryptor = new VideoEncryptor();
		
		doubleSecuredFileEncryptor.setAsymmetricEncryptor(javaCryptoRSA);
		doubleSecuredFileEncryptor.setSymmetricEncryptor(javaCryptoAES);
		videoEncryptor.setFileEncryptor(doubleSecuredFileEncryptor);
		
		this.videoEncryptor = videoEncryptor;
	}
	
	public void setPublicKeyProvider(IPublicKeyProvider publicKeyProvider) {
		videoEncryptor.setPublicKeyProvider(publicKeyProvider);
	}

	@Override
	public byte[] encrypt(File inputVideo, File inputMetadata, Key key, File outputVideo, File outputMetadata) {
		return videoEncryptor.encrypt(inputVideo, inputMetadata, key, outputVideo, outputMetadata);
	}
}
