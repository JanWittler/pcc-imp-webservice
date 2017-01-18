package edu.kit.informatik.pcc.service.server;

import java.io.IOException;
import java.util.logging.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main{
	private static Server server;
	private static final int PORT = 2222;
	public static Logger LOGGER;
	public static void main( String[] args ) {
		setupLogger();
		setupDirectories();
		startServer();
	}
	private static boolean startServer() {
		ResourceConfig config = new ResourceConfig();
		config.packages("edu.kit.informatik.pcc.service.server");
		ServletHolder servlet = new ServletHolder(new ServletContainer(config));

		server = new Server(2222);
		ServletContextHandler context = new ServletContextHandler(server, "/*");
		context.addServlet(servlet, "/*");

		try {
			server.start();
			server.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.destroy();
		}

		return false;
	}
	public static void stopServer() {
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
