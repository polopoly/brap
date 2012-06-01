package no.tornado.brap.client;

import no.tornado.brap.common.InvocationRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLTransportSession implements TransportSession {
    private MethodInvocationHandler invocationHandler;
    private HttpURLConnection conn;

    public HttpURLTransportSession(MethodInvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    public InputStream sendInvocationRequest(Method method, InvocationRequest request, InputStream streamArgument) throws IOException {
        conn = (HttpURLConnection) new URL(invocationHandler.getServiceURI()).openConnection();
        conn.setDoOutput(true);

        if (streamArgument != null)
            conn.setChunkedStreamingMode(ServiceProxyFactory.streamBufferSize);

        ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
        out.writeObject(request);
        out.flush();

        if (streamArgument != null)
            sendStreamArgumentToHttpOutputStream(streamArgument, conn.getOutputStream());

        return conn.getInputStream();
    }

    private void sendStreamArgumentToHttpOutputStream(InputStream streamArgument, OutputStream outputStream) throws IOException {
        byte[] buf = new byte[ServiceProxyFactory.streamBufferSize];
        int len;
        while ((len = streamArgument.read(buf)) > -1)
            outputStream.write(buf, 0, len);
    }

    public HttpURLConnection getConn() {
        return conn;
    }
}
