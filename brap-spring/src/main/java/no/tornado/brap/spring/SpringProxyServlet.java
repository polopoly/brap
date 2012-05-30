package no.tornado.brap.spring;

import no.tornado.brap.auth.AuthenticationNotRequiredAuthenticator;
import no.tornado.brap.auth.AuthenticationRequiredAuthorizer;
import no.tornado.brap.modification.ChangesIgnoredModificationManager;
import no.tornado.brap.servlet.ProxyServlet;
import no.tornado.brap.servlet.ServiceWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This SpringProxyServlet is configured from web.xml for each service you wish
 * to expose as a remoting service.
 * 
 * You must refer to a bean in your applicationContext that provides the complete
 * ServiceWrapper.
 *
 * <p>web.xml example of exposing a remoting service:</p>
 * <pre>
 *  &lt;servlet&gt;
 *      &lt;servlet-name&gt;hello&lt;/servlet-name&gt;
 *      &lt;servlet-class&gt;no.tornado.brap.servlet.ProxyServlet&lt;/servlet-class&gt;
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;beanName&lt;/param-name&gt;
 *          &lt;param-value&gt;helloRemoteService&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 *  &lt;/servlet&gt;
 *  &lt;servlet-mapping&gt;
 *      &lt;servlet-name&gt;hello&lt;/servlet-name&gt;
 *      &lt;url-pattern&gt;/remoting/hello&lt;/url-pattern&gt;
 *  &lt;/servlet-mapping&gt;
 * </pre>
 *
 * <p>applicationContext.xml example:</p>
 * <pre>
 *   &lt;bean id="helloRemoteService" class="no.tornado.brap.servlet.ServiceWrapper"&gt;
 *       &lt;property name="service" ref="helloService"/&gt;
 *       &lt;property name="authenticationProvider" ref="authenticationProvider"/&gt;
 *       &lt;property name="authorizationProvider" ref="authorizationProvider"/&gt;
 *   &lt;/bean&gt;
 *
 *   &lt;bean id="authenticationProvider" class="no.tornado.brap.auth.SingleUsernamePasswordAuthenticator"&gt;
 *       &lt;property name="username" value="john"/&gt;
 *       &lt;property name="password" value="secret"/&gt;
 *   &lt;/bean&gt;
 *
 *  &lt;bean id="authorizationProvider" class="no.tornado.brap.auth.AllowAllAuthorizer"/&gt;
 *
 *   &lt;bean id="helloService" class="no.tornado.brap.test.service.HelloService"/&gt;
 * </pre>
 *
 */
public class SpringProxyServlet extends ProxyServlet {
    protected ApplicationContext applicationContext;

    public void createServiceWrapper() {
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());

        if (applicationContext == null)
            throw new RuntimeException("No WebApplicationContext found. Did you forget to add the ContextLoaderListener to web.xml?");

        String beanName = servletConfig.getInitParameter("beanName");

        if (beanName == null)
            throw new RuntimeException("You must provide the \"beanName\" init-param to the SpringProxyServlet.");

        serviceWrapper = (ServiceWrapper) applicationContext.getBean(beanName);

        if (serviceWrapper.getAuthenticationProvider() == null)
            serviceWrapper.setAuthenticationProvider(new AuthenticationNotRequiredAuthenticator());

        if (serviceWrapper.getAuthorizationProvider() == null)
            serviceWrapper.setAuthorizationProvider(new AuthenticationRequiredAuthorizer());

        if (serviceWrapper.getModificationManager() == null)
            serviceWrapper.setModificationManager( new ChangesIgnoredModificationManager());
    }

}
