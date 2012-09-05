package no.tornado.brap.exception;

/**
 * Exception thrown by the AuthorizationProvider if the
 * principal in the <code>AuthenticationContext</code> should
 * not be allowed to invoke the requested method.
 */
public class AuthorizationFailedException extends Exception {

    private static final long serialVersionUID = 8574077863293026935L;

    public AuthorizationFailedException(String message) {
        super(message);
    }
}
