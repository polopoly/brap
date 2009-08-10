package no.tornado.brap.exception;

import java.io.IOException;

/**
 * Wrapper for returning IOException as unchecked exception,
 * for use by the MethodInvocationHandler when a communication
 * error occurs.
 */
public class CommunicationException extends RuntimeException {
    public CommunicationException(IOException e) {
        super(e);
    }
}
