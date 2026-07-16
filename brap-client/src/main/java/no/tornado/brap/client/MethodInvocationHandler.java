package no.tornado.brap.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.SequenceInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;

import no.tornado.brap.common.InputStreamArgumentPlaceholder;
import no.tornado.brap.common.InvocationRequest;
import no.tornado.brap.common.InvocationResponse;
import no.tornado.brap.common.ModificationList;
import no.tornado.brap.exception.RemotingException;

/**
 * The MethodInvocationHandler is used by the <code>ServiceProxyFactory</code> to provide an implementation
 * of the supplied interface. It intercepts all method-calls and sends them
 * to the server and returns the invocation result.
 * <p>
 * The recommended way to retrieve a service proxy is to call one of the static methods
 * in the <code>ServiceProxyFactory</code>.
 * </p>
 */
public class MethodInvocationHandler implements InvocationHandler, Serializable {

    private static final Logger LOG = Logger.getLogger(MethodInvocationHandler.class.getName());

    private static final long serialVersionUID = -4707857501935404577L;
    private String serviceURI;
    private Serializable credentials;
    private static final String REGEXP_PROPERTY_DELIMITER = "\\.";

    private final HttpClient httpClient;

    /**
     * Default constructor to use if you override <code>getServiceURI</code>
     * and <code>getCredentials</code> to provide "dynamic" service-uri and credentials.
     *
     * @param client The HttpClient that this BRAP client will use
     *
     * @throws IllegalArgumentException if the client is null
     */
    public MethodInvocationHandler(HttpClient client) {
        if (client == null) {
            throw new IllegalArgumentException("HttpClient argument is null!");
        }
        httpClient = client;
    }

    /**
     * Creates the service proxy on the given URI with the given credentials.
     * <p>Credentials can be changed using the ServiceProxyFactory#setCredentials method.</p>
     * <p>ServiceURI can be changed using the ServiceProxyFactory#setServiceURI method.</p>
     *
     * @param client The HttpClient that this BRAP client will use
     * @param serviceURI  The URI to the remote service
     * @param credentials An object used to authenticate/authorize the request
     *
     * @throws IllegalArgumentException if the client is null
     */
    public MethodInvocationHandler(HttpClient client, String serviceURI, Serializable credentials) {
        this(client);
        this.serviceURI = serviceURI;
        this.credentials = credentials;
    }

    /**
     * Creates the service proxy on the given URI.
     * <p>ServiceURI can be changed using the ServiceProxyFactory#setServiceURI method.</p>
     *
     * @param client The HttpClient that this BRAP client will use
     * @param serviceURI The URI to the remote service
     *
     * @throws IllegalArgumentException if the client is null
     */
    public MethodInvocationHandler(HttpClient client, String serviceURI) {
        this(client, serviceURI, null);
    }

    /**
     * Intercepts the method call towards the proxy and sends the call over HTTP.
     * <p>If an exception is thrown on the server-side, it will be re-thrown to the caller.</p>
     * <p>The return value of the method invocation is returned.</p>
     *
     * @return Object the result of the method invocation
     */
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format(
                    "Invoke %s.%s returning %s",
                    obj.getClass().getName(),
                    method.getName(),
                    method.getReturnType().getName()
            ));
        }
        boolean handleIOException = true;
        InvocationResponse response;
        try {
            InvocationRequest request = new InvocationRequest(method, args, getCredentials());

            // Look for the first argument that is an input stream, remove the argument data from
            // the argument array and prepare to transfer the data via the connection outputstream
            // after serializing the invocation request.
            InputStream streamArgument = null;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && InputStream.class.isAssignableFrom(args[i].getClass())) {
                        streamArgument = (InputStream) args[i];
                        args[i] = new InputStreamArgumentPlaceholder();
                        break;
                    }
                }
            }

            HttpPost post = new HttpPost(new URI(getServiceURI()));

            // serialize invocation object
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(request);
                    oos.flush();
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                InputStream streamToSend = null;
                if (streamArgument != null) {
                    streamToSend = new SequenceInputStream(bais, streamArgument);
                } else {
                    streamToSend = bais;
                }

                final InputStreamEntity entity = new InputStreamEntity(streamToSend, -1);
                entity.setChunked(true);
                post.setEntity(entity);

                HttpResponse httpresponse = null;
                boolean returningStream = false;
                try {
                    httpresponse = httpClient.execute(post);

                    // if the request failed we will throw an IOException explaining why the request
                    // failed, previously you simply got a strange "java.io.StreamCorruptedException: invalid stream header"
                    // error.

                    final int statusCode = httpresponse.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                        switch (statusCode) {
                            case 301:
                            case 302: {
                                final Header location = httpresponse.getFirstHeader("Location");
                                if (location != null) {
                                    final String newUri = location.getValue();
                                    throw new IOException(String.format(
                                        "%s has been moved to %s (http status %d)",
                                        getServiceURI(),
                                        newUri,
                                        statusCode
                                    ));
                                }
                            }
                            case 255: {
                                // special case, the method invocation threw an exception on the server, and the
                                // exception is serialized in the response body but the method returns an InputStream
                                // since we do not want to read all the inputStream responses we signal an exception with
                                // the 255 status code, in such case we will read the response which will contain a
                                // serialized InvocationResponse with the exception set and re-throw the exception to the caller.
                                response = readStream(httpresponse, args);
                                if (response.getException() != null) {
                                    final Throwable e = response.getException();
                                    for (Class<?> exType : method.getExceptionTypes()) {
                                        if (exType.isAssignableFrom(e.getClass())) {
                                            if (e instanceof IOException) {
                                                handleIOException = false;
                                            }
                                            throw e;
                                        }
                                    }

                                    throw appendLocalStack(e);
                                } else {
                                    throw new IOException(String.format(
                                        "%s failed with status %d, but no exception was found in the response",
                                        getServiceURI(),
                                        statusCode
                                    ));
                                }
                            }
                            default: {
                                if (LOG.isLoggable(Level.FINE)) {
                                    final String serverOutput = EntityUtils.toString(httpresponse.getEntity(), StandardCharsets.UTF_8);
                                    LOG.severe(String.format(
                                        "%s failed with status %d, server response is:\n%s",
                                        getServiceURI(),
                                        statusCode,
                                        serverOutput
                                    ));
                                }
                                throw new IOException(String.format(
                                    "%s failed with status %d",
                                    getServiceURI(),
                                    statusCode
                                ));
                            }
                        }
                    }

                    if (!method.getReturnType().equals(Object.class)
                            && method.getReturnType().isAssignableFrom(InputStream.class)) {
                        returningStream = true;
                        return httpresponse.getEntity().getContent();
                    }
                    response = readStream(httpresponse, args);
                } finally {
                    if (httpresponse != null && !returningStream) {
                        EntityUtils.consumeQuietly(httpresponse.getEntity());
                    }
                    EntityUtils.consumeQuietly(entity);
                    closeStream(streamToSend);
                }
            }
        } catch (IOException e) {
            if (handleIOException) {
                throw new RemotingException(e);
            } else {
                throw appendLocalStack(e);
            }
        }

        if (response.getException() != null) {
            throw appendLocalStack(response.getException());
        }

        return response.getResult();
    }

    protected InvocationResponse readStream(final HttpResponse httpresponse,
                                            final Object[] args) throws IOException,
                                                                        ClassNotFoundException {
        InvocationResponse response;
        try (InputStream contentInputStream = httpresponse.getEntity().getContent()) {
            try {
                try (ObjectInputStream in = new ObjectInputStream(contentInputStream)) {
                    response = (InvocationResponse) in.readObject();
                    applyModifications(args, response.getModifications());
                }
            } finally {
                closeStream(contentInputStream);
            }
        } finally {
            EntityUtils.consumeQuietly(httpresponse.getEntity());
        }
        return response;
    }

    protected void closeStream(final InputStream s) {
        if (null == s) {
            return;
        }

        try {
            s.close();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Got exception when closing InputStream.", e);
        }
    }

    private Throwable appendLocalStack(Throwable exception) {
        Throwable stack = new Throwable();
        StackTraceElement[] thisStack = stack.getStackTrace();
        StackTraceElement[] thatStack = exception.getStackTrace();
        StackTraceElement[] st = new StackTraceElement[thisStack.length + 1 + thatStack.length];
        System.arraycopy(thatStack, 0, st, 0, thatStack.length);
        st[thatStack.length] = new StackTraceElement("REMOTE", "DELIMITER", "brap_http", 1);
        System.arraycopy(thisStack, 0, st, thatStack.length + 1, thisStack.length);
        exception.setStackTrace(st);
        return exception;
    }

    private void applyModifications(Object[] args, ModificationList[] modifications) {
        if (modifications != null) {
            for (int i = 0; i < modifications.length; i++) {
                ModificationList mods = modifications[i];
                if (mods != null) {
                    for (Map.Entry<String, Object> entry : mods.getModifiedProperties().entrySet()) {
                        try {
                            setModifiedValue(entry.getKey(), entry.getValue(), args[i]);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
    }

    private void setModifiedValue(String key, Object value, Object object) throws NoSuchFieldException, IllegalAccessException {
        String[] propertyGraph = key.split(REGEXP_PROPERTY_DELIMITER);
        int i = 0;

        for (; i < propertyGraph.length - 1; i++)
            object = getValue(object, object.getClass().getDeclaredField(propertyGraph[i]));

        setValue(object, object.getClass().getDeclaredField(propertyGraph[i]), value);
    }

    private void setValue(Object object, Field field, Object value) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        if (!accessible) field.setAccessible(true);
        field.set(object, value);
        if (!accessible) field.setAccessible(false);
    }

    private Object getValue(Object object, Field field) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        if (!accessible) field.setAccessible(true);
        Object value = field.get(object);
        if (!accessible) field.setAccessible(false);
        return value;
    }


    /**
     * Getter for the ServiceURI. Override if you need a more dynamic serviceURI
     * than just setting the value.
     *
     * @return The serviceURI for subsequent method invocations.
     * @see ServiceProxyFactory#setServiceURI(Object, String)
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
     * @see ServiceProxyFactory#setCredentials(Object, Serializable)
     */
    public Serializable getCredentials() {
        return credentials;
    }

    public void setCredentials(Serializable credentials) {
        this.credentials = credentials;
    }

}
