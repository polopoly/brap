package no.tornado.brap.client.httpclient;

import no.tornado.brap.client.MethodInvocationHandler;
import no.tornado.brap.client.TransportSession;
import no.tornado.brap.common.InvocationRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.SequenceInputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpClientTransportSession implements TransportSession {
    private final HttpClient httpClient;
    private final MethodInvocationHandler invocationHandler;
    private HttpResponse httpResponse;

    public HttpClientTransportSession(HttpClient httpClient, MethodInvocationHandler invocationHandler) {
        this.httpClient = httpClient;
        this.invocationHandler = invocationHandler;
    }

    public InputStream sendInvocationRequest(Method method, InvocationRequest request, InputStream streamArgument) throws URISyntaxException, IOException {
        HttpPost post = new HttpPost(new URI(invocationHandler.getServiceURI()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        InputStream streamToSend;
        if(streamArgument != null) {
            streamToSend = new SequenceInputStream(bais, streamArgument);
        } else {
            streamToSend = bais;
        }

        InputStreamEntity entity = new InputStreamEntity(streamToSend, -1);
        entity.setChunked(true);
        post.setEntity(entity);

        httpResponse = httpClient.execute(post);
        return httpResponse.getEntity().getContent();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public MethodInvocationHandler getInvocationHandler() {
        return invocationHandler;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

}
