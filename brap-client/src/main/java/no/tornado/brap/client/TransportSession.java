package no.tornado.brap.client;

import no.tornado.brap.common.InvocationRequest;

import java.io.InputStream;
import java.lang.reflect.Method;

public interface TransportSession {
    InputStream sendInvocationRequest(Method method, InvocationRequest request, InputStream streamArgument) throws Exception;
}
