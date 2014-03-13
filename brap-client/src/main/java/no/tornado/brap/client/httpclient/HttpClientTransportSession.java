package no.tornado.brap.client.httpclient;

import no.tornado.brap.client.MethodInvocationHandler;
import no.tornado.brap.client.TransportSession;
import no.tornado.brap.common.InvocationRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();
        byte[] serializedRequest = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedRequest);

        InputStream streamToSend = null;
        if(streamArgument != null) {
            streamToSend = new SequenceInputStream(bais, streamArgument);
        } else if (serializedRequest.length > 1024) {
            streamToSend = bais;
        }

        HttpUriRequest httpRequest;
        if (streamToSend != null) {
            HttpPost post = new HttpPost(new URI(invocationHandler.getServiceURI()));
            InputStreamEntity entity = new InputStreamEntity(streamToSend, -1);
            entity.setChunked(true);
            post.setEntity(entity);
            httpRequest = post;
        }
        else {
            httpRequest = new HttpGet(new URI(invocationHandler.getServiceURI()) + "/" + new String(Base64.encodeBase64(serializedRequest), "ASCII"));
        }

        httpResponse = httpClient.execute(httpRequest);
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
