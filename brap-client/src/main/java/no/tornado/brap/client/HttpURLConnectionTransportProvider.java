package no.tornado.brap.client;

import java.io.IOException;

public class HttpURLConnectionTransportProvider implements TransportProvider<HttpURLTransportSession> {
    public HttpURLTransportSession createSession(MethodInvocationHandler invocationHandler) {
        return new HttpURLTransportSession(invocationHandler);
    }

    public void endSession(HttpURLTransportSession session, MethodInvocationHandler invocationHandler) {
        try {
            session.getConn().getInputStream().close();
        } catch (IOException ignored) {
        }
    }
}
