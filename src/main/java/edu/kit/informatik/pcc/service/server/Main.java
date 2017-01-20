package edu.kit.informatik.pcc.service.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

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
        if (!setupLogger() || !setupDirectories()) {
            System.out.println("Setup failed");
            return;
        }
        startServer();
    }

    private static boolean startServer() {
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
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public static void restartServer() {
        stopServer();
        //wait
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

        return false;
    }
}
