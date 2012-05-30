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
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemTest {

    private Server server;
    private volatile Exception threadException;

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
        for (int i = 0; i < 1000; i++)
            runServices(service);
    }


    @Test
    public void runMultiServiceOnSameClient() throws Exception {
//        HttpClient client = new DefaultHttpClient();
        HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager());
        final TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:8080/TestService");
        final TestService service2 = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:8080/TestService");

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 500; i++)
                        runServices(service);
                } catch (Exception e) {
                    threadException = e;
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 500; i++)
                        runServices(service2);
                } catch (Exception e) {
                    threadException = e;
                }
            }
        });
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        
        assertNull("got exception from a thread", threadException);

    }

    private void runServices(TestService service) {
        try {
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
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();

        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }
        return sb.toString();
    }

}
