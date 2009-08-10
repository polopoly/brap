package no.tornado.brap.auth;

import java.io.Serializable;

/**
 * The <code>AuthenticationContext</code> holds the authenticated principal
 * that can be used by the <code>AuthorizationProvider</code> and also
 * by the service classes directly to retrieve information about the
 * authenticated user.
 *
 * The ThreadLocal variable that holds the principal is reset before
 * and after every incoming HTTP request using a try/finally block.
 *
 * @see no.tornado.brap.auth.AuthorizationProvider
 */
public class AuthenticationContext {
    private static ThreadLocal<Serializable> principalHolder = new ThreadLocal<Serializable>();

    /**
     * Return the current principal. If none is set, the default
     * <code>AnonymousPrincipal</code> is returned.
     *
     * @return The current principal set for this invocation
     */
    public static final Serializable getPrincipal() {
        return principalHolder.get();
    }

    /**
     * Set a new principal for this invocation. Normally this method is
     * called from the <code>AuthenticationProvider</code> after a successful
     * authentication, alternatively after an unsuccsessfull authentication attempt
     * if you want to set for example an alternative principal to tell a failed
     * authentication apart from a non-authenticated invocation.
     *
     * It is good practise to simply set the credentials object from the
     * <code>InvocationRequest</code> as the principal after a successful authentication.
     *
     * @param principal The principal to set for this invocation
     */
    public static final void setPrincipal(Serializable principal) {
        principalHolder.set(principal);
    }

    public static void exit() {
        principalHolder.set(null);
    }

    public static void enter() {
        principalHolder.set(null);
    }
}
