package edu.kit.informatik.pcc.service.videoprocessing;

import edu.kit.informatik.pcc.service.data.Account;

import javax.ws.rs.container.AsyncResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Controller for editing videos. Takes editing requests and puts them into it's queue.
 * When resources are free it takes the taks and executes the processing.
 *
 * @author Josh Romanowski
 */
public class VideoProcessingManager {

    // attributes

    /**
     * Size of the threapool used.
     */
    private final static int POOL_SIZE = 4;
    /**
     * Maximum amount of accepted tasks.
     */
    private final static int QUEUE_SIZE = 10;
    /**
     * Instance of the VideoProcessingManager used for Singleton behaviour.
     */
    private static VideoProcessingManager instance;

    /**
     * Queue that holds the tasks that are not yet executed.
     */
    private BlockingQueue<Runnable> queue;
    /**
     * Executor that controls the execution of the tasks.
     */
    private ExecutorService executor;

    // constructors

    /**
     * Sets up the queue and the executor. Defines what should happen if the queue is full
     * and a task is being inserted.
     */
    private VideoProcessingManager() {
        queue = new LinkedBlockingDeque<Runnable>(QUEUE_SIZE);
        executor = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 30,
                TimeUnit.SECONDS, queue, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                VideoProcessingChain chain = (VideoProcessingChain) r;
                chain.cleanUp();
                chain.getResponse().resume("Processing queue is full. Processing aborted");
            }
        });
    }

    /**
     * Gets the singleton instance of the VideoProcessingManager.
     *
     * @return Returns the singleton instance.
     */
    public static VideoProcessingManager getInstance() {
        return (instance == null) ? new VideoProcessingManager() : instance;
    }

    // methods

    /**
     * Adds a new task to the queue, which gets executed as soon as resources get free.
     * Gives response via the response object.
     *
     * @param video     Uploaded video.
     * @param metadata  Uploaded metadata.
     * @param key       Uploaded key.
     * @param account   User account who uploaded the video.
     * @param videoName Video name of the uploaded video.
     * @param response  Object use for giving responses.
     */
    public void addTask(InputStream video, InputStream metadata, InputStream key,
                        Account account, String videoName, AsyncResponse response) {
        VideoProcessingChain chain = null;

        try {
            chain = new VideoProcessingChain(video, metadata, key, account, videoName, response);
        } catch (IOException e) {
            Logger.getGlobal().warning("Setting up for editing video "
                    + videoName + " of user " + account.getId() + " failed. Processing aborted");
            response.resume("Setting up for editing video " + videoName + " failed. Processing aborted");
        }

        executor.execute(chain);
    }

    /**
     * Shuts down the queue. Waits 10 Seconds for termination of due tasks.
     */
    public void shutdown() {
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Logger.getGlobal().info("Got interruped while waiting for shutdown");
        }
        executor.shutdown();
    }
}
