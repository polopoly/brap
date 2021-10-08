package no.tornado.brap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import no.tornado.brap.client.ServiceProxyFactory;
import no.tornado.brap.servlet.ProxyServlet;
import no.tornado.brap.test.TestService;

public class SystemTest {

    private Server server;
    private volatile Exception threadException;

    @BeforeClass
    public static void beforeClass() throws IOException {
        SocketHelpers.init();
    }

    @AfterClass
    public static void afterClass() {
        Assert.assertEquals(0, SocketHelpers.checkOpenSockets("END"));;
    }

    @Before
    public void setUp() throws Exception {
        server = new Server(15291);
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
        TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:15291/TestService");
        runServices(service);
    }

    @Test
    public void runManyTimes() throws Exception {
        HttpClient client = new DefaultHttpClient();
        TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:15291/TestService");
        for (int i = 0; i < 1000; i++)
            runServices(service);
    }

//    /**
//     * temp check for leaks
//     * @throws Exception
//     */
//    @Test
//    public void runyTimes() throws Exception {
//        ThreadSafeClientConnManager conman = new ThreadSafeClientConnManager();
//        HttpClient client = new DefaultHttpClient(conman);
//        TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:15291/TestService");
//        while(true)
//            runServices(service);
//    }

    

    @Test
    public void runMultiServiceOnSameClient() throws Exception {
        PoolingClientConnectionManager conman = new PoolingClientConnectionManager();
        HttpClient client = new DefaultHttpClient(conman);
        final TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:15291/TestService");
        final TestService service2 = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:15291/TestService");

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 500; i++) {
                    runServices(service);
                }
            } catch (Exception e) {
                threadException = e;
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < 500; i++) {
                    runServices(service2);
                }
            } catch (Exception e) {
                threadException = e;
            }
        });
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertNull("got exception from a thread", threadException);
        conman.shutdown();
    }

    private void runServices(TestService service) {
        try {
            assertEquals("HEJHejhej", service.echo("Hej"));
            service.doVoid();
            try (InputStream is = service.getStream()) {
                String result = readInputStream(is);
                assertEquals("getStream calling", result);
            }
            service.setStream(new ByteArrayInputStream("hej".getBytes(StandardCharsets.UTF_8)));
            service.setStreamAndString(new ByteArrayInputStream("hej".getBytes(StandardCharsets.UTF_8)), "hej");
            service.setStringAndStream("hej", new ByteArrayInputStream("hej".getBytes(StandardCharsets.UTF_8)));
            service.setStringAndStreamAndInt("hej", new ByteArrayInputStream("hej".getBytes(StandardCharsets.UTF_8)), 1);
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
