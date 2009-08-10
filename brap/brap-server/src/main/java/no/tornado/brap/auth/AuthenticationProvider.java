package no.tornado.brap.auth;

import no.tornado.brap.common.InvocationRequest;
import no.tornado.brap.exception.AuthenticationFailedException;

/**
 * Authenticate the user by the supplied invocationRequest. The supplied
 * credentials (if any) are available in the <code>credentials</code> property.
 *
 * Set the principal by calling <code>AuthenticationContext#setPrincipal</code>.
 *
 * The configured AuthorizationProvider can then access the ThreadLocal
 * containing the principal from <code>AuthenticationContext#getPrincipal</code>.
 *
 * @see AuthenticationNotRequiredAuthenticator
 * @see no.tornado.brap.auth.SingleUsernamePasswordAuthenticator
 * @see no.tornado.brap.auth.DatabaseUsernamePasswordAuthenticator
 * @see no.tornado.brap.auth.AnonymousPrincipal
 * @see no.tornado.brap.auth.UsernamePasswordPrincipal
 */
public interface AuthenticationProvider {
    /**
     * The authentication call. Is made from the <code>ProxyServlet</code>
     * after an incoming invocation request is made.
     *
     * If a successful authentication is made, the <code>AuthenticationContext#setPrincipal()</code>
     * method is called to update the principal with a spesific object,
     * preferably the Credentials object found in the <code>InvocationRequest</code>.
     *
     * @param invocationRequest The invocationRequest, possibly containing a <code>credentials</code> object
     *
     * @see no.tornado.brap.servlet.ProxyServlet
     * @see no.tornado.brap.auth.AuthorizationProvider
     * @see AuthenticationContext
     * @see no.tornado.brap.auth.UsernamePasswordPrincipal
     * @see no.tornado.brap.auth.DatabaseUsernamePasswordAuthenticator
     */
    void authenticate(InvocationRequest invocationRequest) throws AuthenticationFailedException;

}
