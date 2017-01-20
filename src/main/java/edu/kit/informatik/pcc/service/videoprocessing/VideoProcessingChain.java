package edu.kit.informatik.pcc.service.videoprocessing;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.videoprocessing.chain.Decryptor;
import edu.kit.informatik.pcc.service.videoprocessing.chain.FileForwarder;
import edu.kit.informatik.pcc.service.videoprocessing.chain.OpenCVAnonymizer;
import edu.kit.informatik.pcc.service.videoprocessing.chain.Persistor;

import javax.ws.rs.container.AsyncResponse;
import java.io.*;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 *
 *
 * @author Josh Romanowski
 */
public class VideoProcessingChain implements Runnable {

    // attributes

    private AsyncResponse response;
    private LinkedList<IStage> stages;
    private EditingContext context;
    private String videoName;

    // constructors

    protected VideoProcessingChain(InputStream video, InputStream metadata,
                                   InputStream key, Account account, String videoName, AsyncResponse response, Chain chain)
            throws IOException {

        // save response
        this.response = response;
        this.videoName = videoName;

        // create context
        context = new EditingContext(account, videoName);

        // create stages
        stages = new LinkedList<>();
        initChain(chain);

        // save temp files
        saveTempFiles(video, metadata, key);
    }

    // methods

    @Override
    public void run() {
        Logger.getGlobal().info("Start editing video "
                + context.getVideoName() + " of user " + context.getAccount().getId());

        //execute all stages
        for (IStage stage : stages) {
            if (!stage.execute(context)) {
                Logger.getGlobal().warning("Stage " + stage.getName() + " failed");
                cleanUp();
                return;
            }
        }

        deleteTempFiles(context);

        Logger.getGlobal().info("Finished editing video "
                + context.getVideoName() + " of user " + context.getAccount().getId());

        response.resume("Finished editing video " + context.getVideoName());
    }

    public void cleanUp() {
        deleteTempFiles(context);
    }

    // getters/setters

    public AsyncResponse getResponse() {
        return response;
    }

    public String getVideoName() {
        return videoName;
    }

    // helper methods

    private void initChain(Chain chain) {
        switch (chain) {
            case EMPTY:
                break;
            case SIMPLE:
                stages.add(new Decryptor());
                stages.add(new FileForwarder());
                stages.add(new Persistor());
                break;
            case NORMAL:
                stages.add(new Decryptor());
                stages.add(new OpenCVAnonymizer());
                stages.add(new Persistor());
                break;
        }
    }

    protected enum Chain {
        EMPTY, SIMPLE, NORMAL
    }

    private void saveTempFiles(InputStream video, InputStream metadata, InputStream key)
            throws IOException {

        //create output files
        FileOutputStream videoOut = new FileOutputStream(context.getEncVid());
        FileOutputStream metaOut = new FileOutputStream(context.getEncMetadata());
        FileOutputStream keyOut = new FileOutputStream(context.getEncKey());

        //save files
        saveFile(video, videoOut);
        saveFile(metadata, metaOut);
        saveFile(key, keyOut);
    }

    private void saveFile(InputStream input, OutputStream output) throws IOException {
        int read;
        byte[] bytes = new byte[1024];

        try {
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
        } finally {
            input.close();
            output.flush();
            output.close();
        }
    }

    private void deleteTempFiles(EditingContext context) {
        for (File file : context.getAllTempFiles()) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
