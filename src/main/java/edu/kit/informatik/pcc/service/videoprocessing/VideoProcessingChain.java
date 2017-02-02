package edu.kit.informatik.pcc.service.videoprocessing;

import edu.kit.informatik.pcc.service.data.Account;
import edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization.OpenCVAnonymizer;
import edu.kit.informatik.pcc.service.videoprocessing.chain.decryption.Decryptor;
import edu.kit.informatik.pcc.service.videoprocessing.chain.persistation.FileForwarder;
import edu.kit.informatik.pcc.service.videoprocessing.chain.persistation.Persistor;

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

    // TODO: jdoc

    // attributes

    private AsyncResponse response;
    private LinkedList<IStage> stages;
    private EditingContext context;
    private String videoName;

    // constructors

    protected VideoProcessingChain(InputStream video, InputStream metadata,
                                   InputStream key, Account account, String videoName, AsyncResponse response, Chain chain)
            throws IllegalArgumentException {

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
                + context.getVideoName() + " of user " + context.getAccount().getId() + ".");

        long startTime = System.currentTimeMillis();

        //execute all stages
        for (IStage stage : stages) {
            if (!stage.execute(context)) {
                Logger.getGlobal().warning("Stage " + stage.getName() + " failed");
                cleanUp();
                return;
            }
        }

        deleteTempFiles(context);

        long endTime = System.currentTimeMillis() - startTime;

        Logger.getGlobal().info("Finished editing video "
                + context.getVideoName() + " of user " + context.getAccount().getId()
                + ". It took " + (endTime / 1000) + " seconds.");

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

    private void saveTempFiles(InputStream video, InputStream metadata, InputStream key)
            throws IllegalArgumentException {

        try {

            //create output files
            FileOutputStream videoOut = new FileOutputStream(context.getEncVid());
            FileOutputStream metaOut = new FileOutputStream(context.getEncMetadata());
            FileOutputStream keyOut = new FileOutputStream(context.getEncKey());

            //save files
            saveFile(video, videoOut);
            saveFile(metadata, metaOut);
            saveFile(key, keyOut);
        } catch (IOException e) {
            cleanUp();
            throw new IllegalArgumentException();
        }
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

    protected enum Chain {
        EMPTY, SIMPLE, NORMAL
    }
}
