package no.tornado.brap.servlet;

import no.tornado.brap.auth.AuthenticationProvider;
import no.tornado.brap.auth.AuthorizationProvider;

/**
 * The ServiceWrapper holds the service object to expose via remoting
 * in it's <code>service</code> member.
 *
 * This object can be constructed directly in web.xml, or from Spring
 * using the <code>SpringProxyServlet</code> from the brap-spring project.
 *
 * The <code>ServiceWrapper</code> can also be constructed manually.
 *
 * @see ProxyServlet
 */
public class ServiceWrapper {
    /**
     * Holder of the wrapped service, being the actual service implementation.
     */
    private Object service;

    /**
     * The <code>authenticationProvider</code> is consulted before every method invocation.
     *
     * A default will be provided by the <code>ProxyServlet</code> if none is supplied.
     *
     */
    private AuthenticationProvider authenticationProvider;

    /**
     * The <code>authorizationProvider</code> is consulted before every method invocation
     * after the <code>authenticationProvider</code> has run.
     *
     * A default will be provided by the <code>ProxyServlet</code> if none is supplied.
     */
    private AuthorizationProvider authorizationProvider;

    /* Getters and setters */
    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public AuthorizationProvider getAuthorizationProvider() {
        return authorizationProvider;
    }

    public void setAuthorizationProvider(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

}
