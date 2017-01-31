package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.LocationConfig;
import edu.kit.informatik.pcc.service.videoprocessing.VideoProcessingManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * @author Josh Romanowski, Fabian Wenzel
 */
public class Main {
    private static final int PORT = 2222;
    private static final String REQUESTLOCATION = "edu.kit.informatik.pcc.service.server";
    private static Server server;

    /**
     * @param args no args
     */
    public static void main(String[] args) {
        startServer();
    }

    private static boolean startServer() {

        if (!setupLogger() || !setupDirectories()) {
            System.out.println("Setup failed");
            return false;
        }

        Logger.getGlobal().info("Starting Server");
        ResourceConfig config = new ResourceConfig();
        config.packages(REQUESTLOCATION); //where to search for rest requests
        config.register(MultiPartFeature.class); //register feature for file upload (multipartfeature)
        ServletHolder servlet = new ServletHolder(new ServletContainer(config)); // add the config to the servletholder

        server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(servlet, "/*");

        if (server.isStarted()) {
            Logger.getGlobal().info("Server already started");
            return true;
        }

        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            Logger.getGlobal().warning("Server was interrupted during execution");
            return false;
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            server.destroy();
            return true;
        }
    }

    public static void stopServer() {

        if (server.isStopped()) {
            Logger.getGlobal().info("Server is already stopped");
            return;
        }

        Logger.getGlobal().info("Stopping Server");

        // shutdown video processing
        VideoProcessingManager.getInstance().shutdown();

        try {
            // shutdown server
            server.stop();
        } catch (Exception e) {
            Logger.getGlobal().warning("Stopping the server failed.");
        }
    }

    public static void restartServer() {
        Logger.getGlobal().info("Restarting server");
        stopServer();

        startServer();
    }

    private static boolean setupLogger() {
        Logger logger = Logger.getGlobal();
        try {
            // output file = "error.log", max size = 1 MByte, 1 single logfile
            Handler fileErrorHandler = new FileHandler("log\\error.log", 1024000, 1);
            fileErrorHandler.setFormatter(new SimpleFormatter());
            fileErrorHandler.setLevel(Level.WARNING);

            Handler fileInfoHandler = new FileHandler("log\\server.log", 1024000, 1);
            fileInfoHandler.setFormatter(new SimpleFormatter());
            fileInfoHandler.setLevel(Level.WARNING);

            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);

            logger.addHandler(fileInfoHandler);
            logger.addHandler(fileErrorHandler);
            logger.addHandler(consoleHandler);
        } catch (SecurityException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static boolean setupDirectories() {
        boolean ret = true;

        File[] dirs = new File[]{
                new File(LocationConfig.ANONYM_VID_DIR),
                new File(LocationConfig.META_DIR),
                new File(LocationConfig.TEMP_DIR),
                new File(LocationConfig.LOG_DIR)
        };

        for (File dir : dirs) {
            if (!dir.exists()) {
                ret &= dir.mkdir();
            }
        }

        //delete all temp files
        File[] tempFiles = new File(LocationConfig.TEMP_DIR).listFiles();
        for (File file : tempFiles) {
            ret &= file.delete();
        }

        Logger.getGlobal().info("Setup directories");
        return ret;
    }
}
