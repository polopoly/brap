package no.tornado.brap;

import no.tornado.brap.client.ServiceProxyFactory;
import no.tornado.brap.servlet.ProxyServlet;
import no.tornado.brap.test.TestService;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
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
    public void testrunOnce() throws Exception {
        HttpClient client = new DefaultHttpClient();
//        HttpGet get = new HttpGet("http://localhost:8080");
//        ResponseHandler<String> handler = new BasicResponseHandler();
//        
//        String result = client.execute(get, handler);
//        
//        System.out.println(result);
        
        TestService service = ServiceProxyFactory.createProxy(TestService.class, client, "http://localhost:8080/TestService");
        
        System.out.println(service.echo("hej"));
        
        
    }
    
    
//    class BasicTest extends AbstractHandler {
//
//        public void handle(String target, Request baseRequest,
//                HttpServletRequest request, HttpServletResponse response)
//                throws IOException, ServletException {
//            baseRequest.setHandled(true);
//            response.setContentType("text/html;charset=utf-8");
//            response.setStatus(HttpServletResponse.SC_OK);
//            PrintWriter writer = response.getWriter();
//            writer.write("lol no");
//            
//        }
//    }
}
