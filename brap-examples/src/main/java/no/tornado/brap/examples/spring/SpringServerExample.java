package no.tornado.brap.examples.spring;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class SpringServerExample {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath("/");
        bb.setWar("src/main/spring-webapp");

        server.setHandler(bb);

        server.start();
        server.join();
    }
}
