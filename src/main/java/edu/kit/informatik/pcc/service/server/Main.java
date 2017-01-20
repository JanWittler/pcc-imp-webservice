package edu.kit.informatik.pcc.service.server;

import edu.kit.informatik.pcc.service.data.LocationConfig;
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
public class Main{
    private static Server server;
    private static final int PORT = 2222;
    private static Logger LOGGER;

    public static void main(String[] args ) {
        startServer();
    }

    private static boolean startServer() {
        if (!setupLogger() || !setupDirectories()) {
            System.out.println("Setup failed");
            return false;
        }

        Logger.getGlobal().info("starting Server");
        ResourceConfig config = new ResourceConfig();
        config.packages("edu.kit.informatik.pcc.service.server"); //where to search for rest requests
        config.register(MultiPartFeature.class); //register feature for file upload (multipartfeature)
        ServletHolder servlet = new ServletHolder(new ServletContainer(config)); // add the config to the servletholder

        server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            Logger.getGlobal().warning("Server was interrupted during execution");
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            server.destroy();
            return true;
        }
    }

    public static void stopServer() {
        Logger.getGlobal().info("Stopping Server");
        try {
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
        LOGGER = Logger.getGlobal();
        try {
            Handler fileHandler = new FileHandler("log.txt");
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.WARNING);

            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);

            LOGGER.addHandler(fileHandler);
        } catch (SecurityException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static boolean setupDirectories() {
        boolean ret = true;

        File vidDir = new File(LocationConfig.ANONYM_VID_DIR);
        File metaDir = new File(LocationConfig.META_DIR);
        File tempDir = new File(LocationConfig.TEMP_DIR);

        if (!vidDir.exists()) {
            ret &= vidDir.mkdir();
        }
        if (!metaDir.exists()) {
            ret &= metaDir.mkdir();
        }
        if (!tempDir.exists()) {
            ret &= tempDir.mkdir();
        } else {
            //delete all temp files
            File[] tempFiles = tempDir.listFiles();
            for (File file : tempFiles) {
                ret &= file.delete();
            }
        }
        Logger.getGlobal().info("Setup directories");
        return ret;
    }
}
