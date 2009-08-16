package no.tornado.brap.client;

import no.tornado.brap.common.InvocationRequest;
import no.tornado.brap.common.InvocationResponse;
import no.tornado.brap.common.ModificationList;
import no.tornado.brap.exception.RemotingException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * The MethodInvocationHandler is used by the <code>ServiceProxyFactory</code> to provide an implementation
 * of the supplied interface. It intercepts all method-calls and sends them
 * to the server and returns the invocation result.
 *
 * The recommended way to retrieve a service proxy is to call one of the static methods
 * in the <code>ServiceProxyFactory</code>.
 *
 */
public class MethodInvocationHandler implements InvocationHandler {
    private String serviceURI;
    private Serializable credentials;
    private static final String PROPERTY_DELIMITER = ".";

    /**
     * Default constructor to use if you override <code>getServiceURI</code>
     * and <code>getCredentials</code> to provide "dynamic" service-uri and credentials.
     */
    public MethodInvocationHandler() {
    }

    /**
     * Creates the service proxy on the given URI with the given credentials.
     *
     * Credentials can be changed using the ServiceProxyFactory#setCredentials method.
     * ServiceURI can be changed using the ServiceProxyFactory#setServiceURI method.
     *
     * @param serviceURI The URI to the remote service
     * @param credentials An object used to authenticate/authorize the request
     */
    public MethodInvocationHandler(String serviceURI, Serializable credentials) {
        this.serviceURI = serviceURI;
        this.credentials = credentials;
    }

    /**
     * Creates the service proxy on the given URI.
     *
     * ServiceURI can be changed using the ServiceProxyFactory#setServiceURI method.
     *
     * @param serviceURI The URI to the remote service
     */
    public MethodInvocationHandler(String serviceURI) {
        this(serviceURI, null);
    }

    /**
     * Intercepts the method call towards the proxy and sends the call over HTTP.
     *
     * If an exception is thrown on the server-side, it will be re-thrown to the caller.
     *
     * The return value of the method invocation is returned.
     *
     * @return Object the result of the method invocation
     *
     */
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        InvocationResponse response;
        
        try {
            InvocationRequest request = new InvocationRequest(method, args, getCredentials());

            URLConnection conn = new URL(getServiceURI()).openConnection();
            conn.setDoOutput(true);

            ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
            out.writeObject(request);

            ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
            response = (InvocationResponse) in.readObject();

            applyModifications(args, response.getModifications());
        } catch (IOException e) {
            throw new RemotingException(e);
        }

        if (response.getException() != null)
            throw response.getException();

        return response.getResult();
    }

    private void applyModifications(Object[] args, ModificationList[] modifications) {
        if (modifications != null) {
            for (int i = 0; i < modifications.length; i++) {
                ModificationList mods = modifications[i];
                if (mods != null) {
                    Iterator<Map.Entry<String, Object>> it = mods.getModifiedProperties().entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Object> entry = it.next();
                        try {
                            setModifiedValue(entry.getKey(), entry.getValue(), args[i]);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

    private void setModifiedValue(String key, Object value, Object object) throws NoSuchFieldException, IllegalAccessException {
        List<String> propertyGraph = new ArrayList<String>();
        propertyGraph.addAll(Arrays.asList(key.split("\\" + PROPERTY_DELIMITER)));

        Object nestedObject = object;
        Field nestedField = object.getClass().getDeclaredField(propertyGraph.get(0));
        propertyGraph.remove(0);

        for (String property : propertyGraph) {
            boolean accessible = nestedField.isAccessible();
            nestedField.setAccessible(true);
            Object o = nestedField.get(nestedObject);
            if (!accessible) nestedField.setAccessible(false);
            Field f = nestedObject.getClass().getDeclaredField(property);
            accessible = f.isAccessible();
            f.setAccessible(true);
            if (!accessible) f.setAccessible(false);
            if (o != null) {
                nestedObject = o;
                nestedField = f;
            } else {
                break;
            }
        }
        boolean accessible = nestedField.isAccessible();
        nestedField.setAccessible(true);
        nestedField.set(nestedObject, value);
        if (!accessible) nestedField.setAccessible(false);
    }

    /**
     * Getter for the ServiceURI. Override if you need a more dynamic serviceURI
     * than just setting the value.
     *
     * @return The serviceURI for subsequent method invocations.
     * @see no.tornado.brap.client.ServiceProxyFactory#setServiceURI(Object, String) 
     */
    public String getServiceURI() {
        return serviceURI;
    }

    public void setServiceURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }

    /**
     * Getter for the credentials. Override if you need more dynamic credentials
     * than just setting the values.
     *
     * @return The credentials to use for subsequent method invocations.
     * @see no.tornado.brap.client.ServiceProxyFactory#setCredentials(Object, java.io.Serializable)
     */
    public Serializable getCredentials() {
        return credentials;
    }

    public void setCredentials(Serializable credentials) {
        this.credentials = credentials;
    }

}