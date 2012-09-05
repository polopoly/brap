package no.tornado.brap.exception;

/**
 * Wrapper for returning Exception as unchecked exception,
 * for use by the MethodInvocationHandler when a remoting- or
 * communication error occurs.
 */
public class RemotingException extends RuntimeException {

    private static final long serialVersionUID = 2499737830501102760L;

    public RemotingException(Exception e) {
        super(e);
    }
}
