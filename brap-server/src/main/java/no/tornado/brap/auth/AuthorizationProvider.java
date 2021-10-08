package no.tornado.brap.auth;

import no.tornado.brap.common.InvocationRequest;
import no.tornado.brap.exception.AuthorizationFailedException;

/**
 * Authorize the InvocationRequest by checking the principal in
 * the current AuthenticationContext. Return true to allow the invocation.
 *
 * Check the principal with the static method <code>AuthenticationContext#getPrincipal()</code>,
 * which returns the principal from a ThreadLocal.
 *
 * You configure which AuthorizationProvider to use by setting it on the
 * <code>ServiceWrapper</code>.
 *
 * @see AuthenticationNotRequiredAuthorizer
 * @see AuthenticationRequiredAuthorizer
 * @see AuthenticationContext
 * @see no.tornado.brap.auth.AnonymousPrincipal
 * @see no.tornado.brap.common.UsernamePasswordPrincipal
 * @see no.tornado.brap.servlet.ServiceWrapper
 */
public interface AuthorizationProvider {
    /**
     * The authorization call. Is made from the <code>ProxyServlet</code>
     * after an incoming invocation request is authenticated, and before the
     * method is invoked on the exposed service.
     *
     * If a successful authorization is made, true is returned.
     *
     * Normally the <code>AuthenticationContext#getPrincipal()</code> method is consulted
     * to retrieve the principal, so that the principal and the invocationRequest
     * can be matched.
     *
     * @param invocationRequest The deserialized InvocationRequest
     * @throws AuthorizationFailedException to signal insufficient credentials
     *
     * @see no.tornado.brap.auth.AuthenticationProvider
     * @see no.tornado.brap.common.InvocationRequest
     * @see AuthenticationNotRequiredAuthorizer
     */
    void authorize(InvocationRequest invocationRequest) throws AuthorizationFailedException;
}
