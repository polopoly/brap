package no.tornado.brap.exception;

/**
 * Exception thrown by the AuthenticationProvider if authentication
 * is required and the supplied credentials are either missing
 * or insufficient.
 */
public class AuthenticationFailedException extends Exception {

    private static final long serialVersionUID = -310768736923016356L;

    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }
}
