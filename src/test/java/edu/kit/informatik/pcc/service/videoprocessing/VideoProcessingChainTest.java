package edu.kit.informatik.pcc.service.videoprocessing;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.data.DatabaseManager;
import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.data.VideoInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Josh Romanowski
 */
public class VideoProcessingChainTest {

    private VideoProcessingChain chain;
    private DatabaseManager databaseManager;

    private String responseString;

    private File vidFile;
    private File metaFile;
    private File keyFile;
    private String videoName;

    private FileInputStream vidInput;
    private FileInputStream metaInput;
    private FileInputStream keyInput;

    private CountDownLatch lock;

    @Mock
    private Account account;
    private AsyncResponse response;

    @Before
    public void setUp() {
        response = setupResponse();

        vidFile = new File(LocationConfig.TEST_RESOURCES_DIR + "\\encVid.mp4");
        metaFile = new File(LocationConfig.TEST_RESOURCES_DIR + "\\encMeta.txt");
        keyFile = new File(LocationConfig.TEST_RESOURCES_DIR + "\\encKey.txt");

        videoName = "testVideo";
        account = Mockito.mock(Account.class);

        Mockito.when(account.getId()).thenReturn(-1);

        databaseManager = new DatabaseManager(account);

        setupStreams();
    }

    @After
    public void cleanUp() {
        List<VideoInfo> videos = databaseManager.getVideoInfoList();
        for (VideoInfo video : videos) {
            databaseManager.deleteVideoAndMeta(video.getVideoId());
        }

        File testedVid = new File(LocationConfig.ANONYM_VID_DIR + "\\-1_testVideo.avi");
        File testedMeta = new File(LocationConfig.META_DIR + "\\-1_testVideo_meta.txt");

        if (testedVid.exists())
            testedVid.delete();
        if (testedMeta.exists())
            testedMeta.delete();
    }

    @Test
    public void emptyChainTest() {
        testChainType(VideoProcessingChain.Chain.EMPTY, 1);
    }

    @Test
    public void simpleChainTest() {
        testChainType(VideoProcessingChain.Chain.SIMPLE, 5);
    }

    @Test
    public void normalChainTest() {
        testChainType(VideoProcessingChain.Chain.NORMAL, 120);
    }

    private void testChainType(VideoProcessingChain.Chain chainType, long timeout) {
        // test setup
        lock = new CountDownLatch(1);
        try {
            chain = new VideoProcessingChain(vidInput, metaInput, keyInput,
                    account, videoName, response, chainType);
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }

        // test

        chain.run();

        // check result
        try {
            lock.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Logger.getGlobal().warning("Interrupted while waiting for response");
        }

        Assert.assertEquals(responseString, "Finished editing video " + videoName);
    }

    // helper functions

    private AsyncResponse setupResponse() {
        return new AsyncResponse() {
            @Override
            public boolean resume(Object response) {
                responseString = (String) response;
                lock.countDown();
                return true;
            }

            @Override
            public boolean resume(Throwable response) {
                responseString = response.getMessage();
                lock.countDown();
                return true;
            }

            @Override
            public boolean cancel() {
                return false;
            }

            @Override
            public boolean cancel(int retryAfter) {
                return false;
            }

            @Override
            public boolean cancel(Date retryAfter) {
                return false;
            }

            @Override
            public boolean isSuspended() {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public boolean setTimeout(long time, TimeUnit unit) {
                return false;
            }

            @Override
            public void setTimeoutHandler(TimeoutHandler handler) {

            }

            @Override
            public Collection<Class<?>> register(Class<?> callback) {
                return null;
            }

            @Override
            public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback, Class<?>[] callbacks) {
                return null;
            }

            @Override
            public Collection<Class<?>> register(Object callback) {
                return null;
            }

            @Override
            public Map<Class<?>, Collection<Class<?>>> register(Object callback, Object... callbacks) {
                return null;
            }
        };
    }

    private void setupStreams() {
        try {
            vidInput = new FileInputStream(vidFile);
            metaInput = new FileInputStream(metaFile);
            keyInput = new FileInputStream(keyFile);
        } catch (FileNotFoundException e) {
            Assert.fail();
        }
    }
}