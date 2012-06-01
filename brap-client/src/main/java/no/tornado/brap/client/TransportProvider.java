package no.tornado.brap.client;

/**
 * A TransportProvider is used to transport the method invocation call to the remote server. The default implementation
 * uses HttpURLConnection for the Transport, and there is an alternative for Apache HttpClient as well.
 *
 * You can change the default TransportProvider by calling the static method:
 * <pre>MethodInvocationFactory.setDefaultTransportProvider(yourImplementation).</pre>
 *
 * <p>Alternatively you can supply the TransportProvider to ServiceProxyFactory.createProxy()</p>
 *
 * @param <T>
 */
public interface TransportProvider<T extends TransportSession> {
    /**
     * Create a new session that can be used to transport the method invocation call to the remote server.
     *
     * @param invocationHandler The invocationHandler in charge of the method invocation.
     * @return A TransportSession primed and ready to handle one invocation request.
     */
    T createSession(MethodInvocationHandler invocationHandler);

    /**
     * Called by the InvocationHandler after the method invocation has been performed. If you
     * implement some kind of pooling and need to deliver and underlying object back to a pool,
     * or need to perform other kinds of cleanup after the method invocation, this is the place to do it.
     *
     * @param session The TransportSession that is now redundant
     * @param invocationHandler The invocationHandler that handled the method invocation.
     */
    void endSession(T session, MethodInvocationHandler invocationHandler);
}
