package edu.kit.informatik.pcc.service.server;

import java.util.logging.Logger;
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
		config.packages("newGroup");
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

		return false;
	}
	private static boolean setupDirectories() {

		return false;
	}
}
