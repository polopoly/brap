package no.tornado.brap.client.httpclient;

import no.tornado.brap.client.MethodInvocationHandler;
import no.tornado.brap.client.TransportProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * <p>Minimalistic provider for Apache HttpClient based transport. You will probably
 * want to either extend this class or implement TransportProvider directly
 * to provide a configured HttpClient. It is recommended to extend and override getHttpClient(),
 * and possibly include a pooling mechanism.</p>
 *
 * To use this provider as default, you can:
 * <pre>MethodInvocationFactory.setDefaultTransportProvider(new HttpClientConnectionTransportProvider()).</pre>
 *
 * Alternatively you can supply this or another implementation to <code>ServiceProxyFactory.createProxy()</code>
 */
public class HttpClientConnectionTransportProvider implements TransportProvider<HttpClientTransportSession> {
    public HttpClientTransportSession createSession(MethodInvocationHandler invocationHandler) {
        HttpClient httpClient = getHttpClient();
        return new HttpClientTransportSession(httpClient, invocationHandler);
    }

    protected HttpClient getHttpClient() {
        return new DefaultHttpClient();
    }

    public void endSession(HttpClientTransportSession session, MethodInvocationHandler invocationHandler) {
        try {
            session.getHttpResponse().getEntity().getContent().close();
        } catch (IOException ignored) {
        }
    }
}
