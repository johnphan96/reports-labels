package labels;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;

import spark.embeddedserver.jetty.JettyServerFactory;

public class customJettyServerFactory implements JettyServerFactory {

	@Override
	public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
		// TODO Auto-generated method stub
		Server server = new Server();
		System.out.println("Using customized embedded Jetty server....");
		
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 1024 * 1024 * 512); // max 0.5 GB for form requests should be sent to the server
		
		return server;
	}

	@Override
	public Server create(ThreadPool threadPool) {
		// TODO Auto-generated method stub
		return null;
	}

}
