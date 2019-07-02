package edu.kit.informatik.pcc.service.server;

import java.io.File;

import edu.kit.informatik.pcc.service.authentication.IUserIdProvider;
import edu.kit.informatik.pcc.service.authentication.IUserManagement;
import edu.kit.informatik.pcc.service.videoprocessing.IVideoService;

public class WebService implements IUserManagement, IWebVideoService {
	private IUserManagement userManagement;
	private IUserIdProvider userIdProvider;
	private IVideoService videoService;
	
	private static WebService globalInstance;
	
	public void setUserManagement(IUserManagement userManagement) {
		assert this.userManagement == null;
		this.userManagement = userManagement;
	}
	
	public void setUserIdProvider(IUserIdProvider userIdProvider) {
		assert this.userIdProvider == null;
		this.userIdProvider = userIdProvider;
	}
	
	public void setVideoService(IVideoService videoService) {
		assert this.videoService == null;
		this.videoService = videoService;
	}
	
	public static WebService getGlobal() {
		assert globalInstance != null;
		return globalInstance;
	}
	
	public static void setGlobal(WebService webService) {
		assert globalInstance == null;
		globalInstance = webService;
	}

	@Override
	public Boolean createAccount(String email, String password) {
		assertCompletelySetup();
		return userManagement.createAccount(email, password);
	}

	@Override
	public String login(String email, String password) {
		assertCompletelySetup();
		return userManagement.login(email, password);
	}

	@Override
	public Boolean deleteAccount(String authenticationToken) {
		assertCompletelySetup();
		int userId = userIdProvider.getUserId(authenticationToken);
		if (userId == IUserIdProvider.invalidId) {
			return false;
		}
		int[] videoIds = getVideoIds(authenticationToken);
		for (int videoId: videoIds) {
			videoService.deleteVideo(videoId, userId);
		}
		return userManagement.deleteAccount(authenticationToken);
	}

	@Override
	public int[] getVideoIds(String authenticationToken) {
		assertCompletelySetup();
		int userId = userIdProvider.getUserId(authenticationToken);
		if (userId == IUserIdProvider.invalidId) {
			return null;
		}
		return videoService.getVideoIds(userId);
	}

	@Override
	public void postVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKeyData,
			String authenticationToken) {
		assertCompletelySetup();
		int userId = userIdProvider.getUserId(authenticationToken);
		if (userId == IUserIdProvider.invalidId) {
			return;
		}
		videoService.postVideo(encryptedVideo, encryptedMetadata, encryptedKeyData, userId);

	}

	@Override
	public File getVideo(int videoId, String authenticationToken) {
		assertCompletelySetup();
		int userId = userIdProvider.getUserId(authenticationToken);
		if (userId == IUserIdProvider.invalidId) {
			return null;
		}
		return videoService.getVideo(videoId, userId);
	}

	@Override
	public File getMetadata(int videoId, String authenticationToken) {
		assertCompletelySetup();
		int userId = userIdProvider.getUserId(authenticationToken);
		if (userId == IUserIdProvider.invalidId) {
			return null;
		}
		return videoService.getMetadata(videoId, userId);
	}

	@Override
	public void deleteVideo(int videoId, String authenticationToken) {
		assertCompletelySetup();
		int userId = userIdProvider.getUserId(authenticationToken);
		if (userId == IUserIdProvider.invalidId) {
			return;
		}
		videoService.deleteVideo(videoId, userId);
	}
	
	private void assertCompletelySetup() {
		assert userManagement != null;
		assert userIdProvider != null;
		assert videoService != null;
	}
}
