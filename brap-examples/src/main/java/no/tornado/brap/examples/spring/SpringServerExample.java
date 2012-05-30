package no.tornado.brap.examples.spring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class SpringServerExample {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        WebAppContext bb = new WebAppContext();
        bb.setWar("src/main/spring-webapp");
        server.setHandler(bb);
        server.start();
        server.join();
    }
}
