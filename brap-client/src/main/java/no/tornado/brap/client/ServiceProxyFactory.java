package no.tornado.brap.client;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.apache.http.client.HttpClient;

/**
 * Factory that creates service proxies for the client.
 *
 * Simply supply the interface class you want to access, and the URL to the
 * exposed service and cast the result to your interface class.
 *
 * <p>Accessing a remote service is as easy as:</p>
 *
 * <pre>
 * MyService myService = (MyService) ServiceProxyFactory.
 *      createProxy(MyService.class, "http://example.com/MyService");
 * </pre>
 *
 * <p>You can also supply an optional credentials argument.</p>
 *
 * <pre>
 * MyService myService = (MyService) ServiceProxyFactory.
 *      createProxy(MyService.class, "http://example.com/MyService", user);
 * </pre>
 *
 */
public class ServiceProxyFactory {
    public static int streamBufferSize = 16384;
    
    /**
     * Creates a proxy for the supplied serviceInterface class on the
     * supplied URI.
     *
     *
     * @param serviceInterface The service inteface to implement
     * @param serviceURI A fully qualified URI to the remote service
     * @return A service proxy that implements the given serviceInterface
     *
     */
    public static <T> T createProxy(Class<? extends T> serviceInterface, HttpClient client, String serviceURI) {
        return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] { serviceInterface }, new MethodInvocationHandler(client, serviceURI)));
    }

    /**
     * Creates a proxy for the supplied serviceInterface class on the
     * supplied URI with the supplied credentials
     *
     *
     * @param serviceInterface The service inteface to implement
     * @param serviceURI A fully qualified URI to the remote service
     * @param credentials The credentials to use for authentication
     * @return A service proxy that implements the given serviceInterface
     *
     */
    public static <T> T createProxy(Class<? extends T> serviceInterface, HttpClient client, String serviceURI, Serializable credentials) {
        return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] { serviceInterface }, new MethodInvocationHandler(client, serviceURI, credentials)));
    }

    /**
     * Creates a service proxy for the supplied serviceInterface taking an instantiated
     * MethodInvocationHandler or a subclass.
     *
     * Clients are intended to subclass MethodInvocationHandler if you need to control the method
     * invocation process or the credentials/url in a more spesific way.
     *
     * @param serviceInterface The service inteface to implement
     * @param methodInvocationHandler The already instantiated MethodInvocationHandler
     * @return A service proxy that implements the given serviceInterface
     */
    public static <T> T createProxy(Class<? extends T> serviceInterface, MethodInvocationHandler methodInvocationHandler) {
        return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] { serviceInterface }, methodInvocationHandler));
    }

    /**
     * Set the credentials of an already created serviceproxy.
     *
     * @param proxy The serviceproxy created earlier with createProxy()
     * @param credentials The serializable credentials object you want the proxy to
     * include with every remote invocation.
     * @see MethodInvocationHandler#getCredentials()
     */
    public static void setCredentials(Object proxy, Serializable credentials) {
        MethodInvocationHandler methodInvocationHandler = (MethodInvocationHandler) Proxy.getInvocationHandler(proxy);
        methodInvocationHandler.setCredentials(credentials);
    }

    /**
     * Set the serviceURI for an already created serviceproxy.

     * @param proxy The serviceproxy created earlier with createProxy()
     * @param serviceURI The new serviceURI to use for subsequent method invocations
     * @see MethodInvocationHandler#getServiceURI()
     */
    public static void setServiceURI(Object proxy, String serviceURI) {
        MethodInvocationHandler methodInvocationHandler = (MethodInvocationHandler) Proxy.getInvocationHandler(proxy);
        methodInvocationHandler.setServiceURI(serviceURI);
    }

}