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
 * <p>To use this provider as default, you can:</p>
 * <pre>MethodInvocationFactory.setDefaultTransportProvider(new HttpClientTransportProvider()).</pre>
 *
 * <p>Alternatively you can supply this or another implementation to <code>ServiceProxyFactory.createProxy()</code>
 * by instantiating a <code>MethodInvocationHandler</code> with your <code>TransportProvider</code>.</p>
 */
public class HttpClientTransportProvider implements TransportProvider<HttpClientTransportSession> {
    private HttpClient httpClient;

    public HttpClientTransportProvider() {
        httpClient = new DefaultHttpClient();
    }

    public HttpClientTransportProvider(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClientTransportSession createSession(MethodInvocationHandler invocationHandler) {
        return new HttpClientTransportSession(httpClient, invocationHandler);
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void endSession(HttpClientTransportSession session, MethodInvocationHandler invocationHandler) {
        try {
            if (session.getHttpResponse() != null) {
                session.getHttpResponse().getEntity().getContent().close();
            }
        } catch (IOException ignored) {
        }
    }
}
