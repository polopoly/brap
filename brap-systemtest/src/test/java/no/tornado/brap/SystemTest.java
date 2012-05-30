package no.tornado.brap;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import no.tornado.brap.client.ServiceProxyFactory;
import no.tornado.brap.servlet.ProxyServlet;
import no.tornado.brap.test.TestService;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemTest {

    private Server server;


    @Before
    public void setUp() throws Exception {
        server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder servletHolder = new ServletHolder(new ProxyServlet());
        servletHolder.setInitParameter("service", "no.tornado.brap.test.TestServiceImpl");
        servletHolder.setInitParameter("authorizationProvider", "no.tornado.brap.auth.AuthenticationNotRequiredAuthorizer");
        context.addServlet(servletHolder, "/TestService");
        server.start();
    }
    
    
    
    @After
    public void tearDown() throws Exception {
        server.stop();
    };
    
    @Test
    public void runOnce() throws Exception {
        HttpClient client = new DefaultHttpClient();
        TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:8080/TestService");
        runServices(service);
    }

    
    @Test
    public void runManyTimes() throws Exception {
        HttpClient client = new DefaultHttpClient();
        TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:8080/TestService");
        for(int i = 0; i < 1000; i++) 
            runServices(service);
    }



    private void runServices(TestService service) throws IOException, UnsupportedEncodingException {
        assertEquals("HEJHejhej", service.echo("Hej"));
        service.doVoid();
        assertEquals("getStream calling", readInputStream(service.getStream()));
        service.setStream(new ByteArrayInputStream("hej".getBytes("UTF-8")));
        service.setStreamAndString(new ByteArrayInputStream("hej".getBytes("UTF-8")), "hej");
        service.setStringAndStream("hej", new ByteArrayInputStream("hej".getBytes("UTF-8")));
        service.setStringAndStreamAndInt("hej", new ByteArrayInputStream("hej".getBytes("UTF-8")), 1);
        try {
            service.throwException();
            fail("expected exception");
        } catch (Exception e) {
            // expected
        }
    }
    
    private String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String read = br.readLine();
    
        while(read != null) {
            sb.append(read);
            read = br.readLine();
        }
        return sb.toString();
    }
    
}
