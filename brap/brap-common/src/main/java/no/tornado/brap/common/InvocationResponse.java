package no.tornado.brap.common;

import java.io.Serializable;

/**
 * The response to an <code>InvocationRequest</code> is created by the
 * <code>ProxyServlet</code> after invocation.
 *
 */
public class InvocationResponse implements Serializable {

    /**
     * Will hold any exception thrown by either the remote service method itself,
     * or by the <code>ProxyServlet</code> if it encounters an error while invoking
     * or preparing to invoke the method.
     */
    private Throwable exception;

    /**
     * The result of the method invocation if no exceptions are thrown.
     */
    private Serializable result;

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Serializable getResult() {
        return result;
    }

    public void setResult(Serializable result) {
        this.result = result;
    }
}
