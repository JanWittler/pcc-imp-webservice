package edu.kit.informatik.pcc.service.videoprocessing;

import edu.kit.informatik.pcc.core.crypto.IVideoDecryptor;
import edu.kit.informatik.pcc.service.data.IFileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Controller for editing videos. Takes editing requests and puts them into it's queue.
 * When resources are free it takes the taks and executes the processing.
 *
 * @author Josh Romanowski
 */
public class VideoProcessingManager implements IAsyncVideoProcessor {
	private IFileManager temporaryFileManager;
	private IVideoDecryptor videoDecryptor;
	private IVideoProcessor videoProcessor;
	
	public void setTemporaryFileManager(IFileManager temporaryFileManager) {
		assert this.temporaryFileManager == null;
		this.temporaryFileManager = temporaryFileManager;
	}
	
	public void setVideoDecryptor(IVideoDecryptor videoDecryptor) {
		assert this.videoDecryptor == null;
		this.videoDecryptor = videoDecryptor;
	}
	
	public void setVideoProcessor(IVideoProcessor videoProcessor) {
		assert this.videoProcessor == null;
		this.videoProcessor = videoProcessor;
	}

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Size of the thread pool used.
     */
    private final static int POOL_SIZE = 4;
    /**
     * Maximum amount of accepted tasks.
     */
    private final static int QUEUE_SIZE = 10;

    /**
     * Executor that controls the execution of the tasks.
     */
    private ExecutorService executor;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Sets up the queue and the executor. Defines what should happen if the queue is full
     * and a task is being inserted.
     */
    public VideoProcessingManager() {
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(QUEUE_SIZE);
        executor = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 30,
                TimeUnit.SECONDS, queue, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            	((VideoTask)r).cleanupAfterFailure();
                String errorMessage = "Inserting video in queue failed.";
                if (executor.isShutdown()) {
                    errorMessage += "Processing module is shut down.";
                } else {
                    errorMessage += "Queue is full.";
                }

                Logger.getGlobal().warning(errorMessage);
            }
        });
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
	public void processVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKeyData, File outputVideo,
			File outputMetadata) {
    	assertCompletelySetup();
    	//copy input files to temp directory so that processing is independent of outside file handling
		File newEncryptedVideo = temporaryFileManager.file(UUID.randomUUID().toString());
		File newEncryptedMetadata = temporaryFileManager.file(UUID.randomUUID().toString());
    	try {
    		Files.copy(encryptedVideo.toPath(), newEncryptedVideo.toPath(), StandardCopyOption.REPLACE_EXISTING);
    		Files.copy(encryptedMetadata.toPath(), newEncryptedMetadata.toPath(), StandardCopyOption.REPLACE_EXISTING);
    		VideoTask videoTask = new VideoTask(temporaryFileManager, videoDecryptor, videoProcessor, newEncryptedVideo, newEncryptedMetadata, encryptedKeyData, outputVideo, outputMetadata);
    		executor.execute(videoTask);
    	}
    	catch (IOException e) {
    		Logger.getGlobal().warning("Failed to process video " + encryptedVideo.getAbsolutePath());
    		return;
    	}
    	Logger.getGlobal().info("Inserted video " + encryptedVideo.getAbsolutePath() + " into queue.");
	}

    /**
     * Shuts down the queue. Waits 10 Seconds for termination of due tasks.
     */
    public void shutdown() {
        try {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Logger.getGlobal().info("Got interrupted while waiting for shutdown");
        }
        Logger.getGlobal().info("Video processing manager stopped");
    }
    
    private void assertCompletelySetup() {
		assert temporaryFileManager != null;
		assert videoDecryptor != null;
		assert videoProcessor != null;
	}
    
    private class VideoTask implements Runnable {
		private IFileManager temporaryFileManager;
		private IVideoDecryptor videoDecryptor;
		private IVideoProcessor videoProcessor;
		private File encryptedVideo;
		private File encryptedMetadata;
		private byte[] encryptedKeyData;
		private File outputVideo;
		private File outputMetadata;
		
		private VideoTask(IFileManager temporaryFileManager, IVideoDecryptor videoDecryptor, IVideoProcessor videoProcessor, File encryptedVideo, File encryptedMetadata, byte[] encryptedKeyData, File outputVideo,
				File outputMetadata) throws IOException {
			this.temporaryFileManager = temporaryFileManager;
			this.videoDecryptor = videoDecryptor;
			this.videoProcessor = videoProcessor;
			this.encryptedVideo = encryptedVideo;
			this.encryptedMetadata = encryptedMetadata;
			this.encryptedKeyData = encryptedKeyData;
			this.outputVideo = outputVideo;
			this.outputMetadata = outputMetadata;
		}
		
		@Override
	    public void run() {
	        Logger.getGlobal().info("Start editing video " + encryptedVideo.getAbsolutePath());

	        long startTime = System.currentTimeMillis();

	        File tempVideoFile = temporaryFileManager.file(UUID.randomUUID().toString());
			videoDecryptor.decrypt(encryptedVideo, encryptedMetadata, encryptedKeyData, tempVideoFile, outputMetadata);
			
			videoProcessor.processVideo(tempVideoFile, outputMetadata, outputVideo);
			temporaryFileManager.deleteFile(tempVideoFile);

			long duration = System.currentTimeMillis() - startTime;
	        Logger.getGlobal().info("Finished editing video "
	                + encryptedVideo.getAbsolutePath()
	                + ". It took " + (duration / 1000) + " seconds.");
	    }
		
		public void cleanupAfterFailure() {
			temporaryFileManager.deleteFile(encryptedVideo);
			temporaryFileManager.deleteFile(encryptedMetadata);
			temporaryFileManager.deleteFile(outputVideo);
			temporaryFileManager.deleteFile(outputMetadata);
		}
	}
}
